package kr.co.ideait.platform.gaiacairos.comp.progress;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.platform.gaiacairos.comp.api.service.ApiService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.ActivityService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.RevisionService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.WbsService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.C3RService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrRevision;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.revision.RevisionMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.revision.RevisionMybatisParam.DeleteRevisionInput;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RevisionComponent extends AbstractComponent {

    @Autowired
    RevisionService revisionService;

    @Autowired
    WbsService wbsService;

    @Autowired
    ActivityService activityService;

    @Autowired
    C3RService c3RService;

    @Autowired
    ApiService apiService;

    /**
     * 리비젼 관련 메서드
     */

    // 리비젼 목록 조회
    public List selectRevisionList(RevisionMybatisParam.RevisionListInput revisionListInput) {

        // FIXME - REVISION 장기계속계약_차수별 - 인 경우, 차수별 REVISION 이 쌓일 수 있다. - 조회추가
        
        return revisionService.selectRevisionList(revisionListInput);
    }

    // 리비젼 조회
    public PrRevision getRevision(String cntrctChgId, String revisionId) {
        return revisionService.getRevision(cntrctChgId, revisionId);
    }

    // 계약변경 조회
    public Map selectContractChange(String cntrctNo) {
        return revisionService.selectContractChange(cntrctNo);
    }

    // 리비젼 삭제 (삭제 여부 업데이트)
    @Transactional
    public void deleteRevision(List<DeleteRevisionInput> delRevisionList, Map<String, Object> param) {

        String usrId = param.get("usrId").toString();
        String isApiYn = param.get("isApiYn").toString();
        String pjtDiv = param.get("pjtDiv").toString();

        // REVISION 논리삭제
        revisionService.deleteRevision(delRevisionList, usrId);

        // WBS, ACTIVITY 논리삭제
        HashMap<String, Object> deleteVO = new HashMap<>();
        deleteVO.put("CNTRCT_CHG_ID", delRevisionList.getFirst().getCntrctChgId());
        deleteVO.put("REVISION_ID", delRevisionList.getFirst().getRevisionId());
        deleteVO.put("USR_ID", usrId);
        activityService.updateActivityDeleteState(deleteVO);
        wbsService.updateWbsDeleteState(deleteVO);

        /**
         * API 연동 판별변수 CAIROS -> PGAIA
         * 1. 사업이 공공(P) 경우
         * 2. MENU API 사용여부 (Y)
         * 3. 현재플랫폼이 카이로스
         */
        boolean isApiEnabled = "Y".equals(isApiYn)
                && "P".equals(pjtDiv)
                && PlatformType.CAIROS.getName().equals(platform);
        if (isApiEnabled) {
            Map result = invokeCairos2Pgaia("CAGA2003", Map.of(
                    "delRevisionList", delRevisionList,
                    "usrId", usrId
            ));

            String resultCode = result.get("resultCode").toString();
            if (!"00".equals(resultCode)) {
                throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA2001");
            }
        }
    }


    /**
     * Primavera -> GaiA/CaiROS
     * REVISION, WBS, ACTIVITY 정보 생성 수행
     * @param prRevision
     * @param param
     * @throws GaiaBizException
     */
    @Transactional
    public void insertRevisionWbsActivity(PrRevision prRevision, Map<String, Object> param) throws GaiaBizException {

        String cntrctNo = param.get("cntrctNo").toString();
        String usrId = param.get("usrId").toString();
        String isApiYn = param.get("isApiYn").toString();
        String pjtDiv = param.get("pjtDiv").toString();

        // 00. 초기화 - Update 하려는 revision이 최종버전일 경우 계약건에 대한 setLastRevisionYn("N") 처리
        // * 변경:
        //     20250714 장기계속계약_차수별 인경우 해당 차수만 초기화, 그 외는 전체 초기화
        if("Y".equals(prRevision.getLastRevisionYn())) {
            revisionService.updateLastYn(prRevision.getCntrctChgId());
        }

        // 01. insert to PR_REVISION
        prRevision.setDltYn("N");
//		prRevision.setRgstrId(UserAuth.get(true).getUsrId());
        revisionService.insertRevision(prRevision);

        // 02. primavera 로부터 데이터를 수신.
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("login_id", usrId);
        requestBody.put("platform", platform);
        requestBody.put("workType", "GAPR0030");
        requestBody.put("projId", prRevision.getP6ProjectObjId());

        Map<String, Object> respMap = apiService.primaveraApiGetPost(requestBody).getDetails();
        LinkedHashMap<String, Object> rst = (LinkedHashMap<String, Object>) respMap.get("data");

        // 03. 초기화 (삭제)
        HashMap<String, Object> deleteVO = new HashMap<>();
        deleteVO.put("CNTRCT_CHG_ID", prRevision.getCntrctChgId());
        deleteVO.put("REVISION_ID", prRevision.getRevisionId());

        c3RService.deleteQdbResourceWithRevision(deleteVO);     // Activity 참조하는 QDB_RESOURCE 삭제
        c3RService.deleteQdbWithRevision(deleteVO);             // Activity 참조하는 QDB 삭제
        activityService.deleteActivity(deleteVO);               // Activity  삭제
        wbsService.deleteWbs(deleteVO);                         // WBS 삭제

        // 04. insert to PR_WBS
        ArrayList<HashMap<String, Object>> wbsList = (ArrayList<HashMap<String, Object>>) rst.get("wbsList");
        wbsService.createWbs(prRevision, wbsList);

        // 05. insert to PR_ACTIVITY
        ArrayList<HashMap<String, Object>> activityList = (ArrayList<HashMap<String, Object>>) rst.get("activityList");
        activityService.createActivity(prRevision, activityList);

        // 06. insert 후 코드정보 가공.
        HashMap<String, Object> vo = new HashMap<>();
        vo.put("CNTRCT_CHG_ID", prRevision.getCntrctChgId());
        vo.put("REVISION_ID", prRevision.getRevisionId());
        wbsService.updateWbsCd(vo);


        // 07. 기존 최종 Revision 기준 - QDB, Activity_plan 생성
        param.put("revisionId", prRevision.getRevisionId());
        Map prevRevision = revisionService.getPrevLastRevision(param);      // 7-1. 직전 Revision 정보를 가져온다.
        boolean isQdbExist = c3RService.checkQdbExists(prevRevision);       // 7-2. 직전 Revision 기준 QDB 존재여부 확인

        Map<String, Object> qdbParam = new HashMap<>();
        if (prevRevision != null && isQdbExist) {
            log.info("QDB 복사 START");
            log.info("계약변경 ID: {}", prevRevision.get("cntrctChgId").toString());
            log.info("직전 REVISION ID: {}", prevRevision.get("revisionId").toString());
            log.info("계약차수: {}", prevRevision.get("cntrctPhase").toString());
            log.info("계약회차: {}", prevRevision.get("cntrctChgNo").toString());

            qdbParam.put("CNTRCT_NO", param.get("cntrctNo"));
            qdbParam.put("CNTRCT_CHG_ID", prRevision.getCntrctChgId());
            qdbParam.put("REVISION_ID", prRevision.getRevisionId());
            qdbParam.put("PREV_REVISION_ID", prevRevision.get("revisionId"));
            qdbParam.put("USR_ID", param.get("usrId"));

            c3RService.insertQdbActivityPlanDataWithRevision(qdbParam);
        }


        // API 송신 (CAIROS -> PGAIA)
        rst.put("cntrctNo", cntrctNo);
        rst.put("usrId", usrId);

        /**
         * API 연동 판별변수 CAIROS -> PGAIA
         * 1. 사업이 공공(P) 경우
         * 2. MENU API 사용여부 (Y)
         * 3. 현재플랫폼이 카이로스
         */
        boolean isApiEnabled = "Y".equals(isApiYn)
                        && "P".equals(pjtDiv)
                        && PlatformType.CAIROS.getName().equals(platform);
        if (isApiEnabled) {
            Map<String, Object> apiVO = new HashMap<>();
            apiVO.put("prRevision", prRevision);
            apiVO.put("reqBody", rst);
            apiVO.put("prevRevision", prevRevision);
            apiVO.put("isQdbExist", isQdbExist);
            Map result = invokeCairos2Pgaia("CAGA2001", apiVO);

            String resultCode = result.get("resultCode").toString();
            String resultMsg = result.get("resultMsg").toString();
            if (!"00".equals(resultCode)) {
                log.error("Cairos2Pgaia - CAGA2001 Error {}", resultMsg);

                // msg.api.001 - API 통신이 원활하지 않습니다. 관리자에게 문의하길 바랍니다.
                String message = messageSource.getMessage("msg.api.001", null, LocaleContextHolder.getLocale());
                throw new GaiaBizException(ErrorType.INTERFACE, "[Cairos2Pgaia - CAGA2001]" + message);
            }
        }
    }

    /**
     * Primavera -> GaiA/CaiROS
     * REVISION, WBS, ACTIVITY 정보 수정 수행
     * @param prRevision
     * @param param
     * @throws GaiaBizException
     */
    @Transactional
    public void updateRevisionWbsActivity(PrRevision prRevision, Map<String, Object> param) throws GaiaBizException {

        String cntrctNo = param.get("cntrctNo").toString();
        String usrId = param.get("usrId").toString();
        String isApiYn = param.get("isApiYn").toString();
        String pjtDiv = param.get("pjtDiv").toString();

        // 00. 초기화 - Update 하려는 revision이 최종버전일 경우 계약건에 대한 setLastRevisionYn("N") 처리
        if("Y".equals(prRevision.getLastRevisionYn())) {
            revisionService.updateLastYn(prRevision.getCntrctChgId());
        }

        // 01. Revision update
        prRevision.setChgId(usrId);
        prRevision.setChgDt(LocalDateTime.now());
        revisionService.updateRevision(prRevision);

        // 02. primavera 로부터 데이터를 수신.
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("login_id", usrId);
        requestBody.put("platform", platform);
        requestBody.put("workType", "GAPR0030");
        requestBody.put("projId", prRevision.getP6ProjectObjId());

        Map<String, Object> respMap = apiService.primaveraApiGetPost(requestBody).getDetails();
        LinkedHashMap<String, Object> rst = (LinkedHashMap<String, Object>) respMap.get("data");

        // 03. 초기화 (삭제)
        HashMap<String, Object> deleteVO = new HashMap<>();
        deleteVO.put("CNTRCT_CHG_ID", prRevision.getCntrctChgId());
        deleteVO.put("REVISION_ID", prRevision.getRevisionId());

        c3RService.deleteQdbResourceWithRevision(deleteVO);     // Activity 참조하는 QDB_RESOURCE 삭제
        c3RService.deleteQdbWithRevision(deleteVO);             // Activity 참조하는 QDB 삭제
        activityService.deleteActivity(deleteVO);               // Activity  삭제
        wbsService.deleteWbs(deleteVO);                         // WBS 삭제

        // 04. insert to PR_WBS
        ArrayList<HashMap<String, Object>> wbsList = (ArrayList<HashMap<String, Object>>) rst.get("wbsList");
        wbsService.createWbs(prRevision, wbsList);


        // 05. insert to PR_ACTIVITY
        ArrayList<HashMap<String, Object>> activityList = (ArrayList<HashMap<String, Object>>) rst.get("activityList");
        activityService.createActivity(prRevision, activityList);

        // 06. insert 후 코드정보 가공.
        HashMap<String, Object> vo = new HashMap<>();
        vo.put("CNTRCT_CHG_ID", prRevision.getCntrctChgId());
        vo.put("REVISION_ID", prRevision.getRevisionId());
        wbsService.updateWbsCd(vo);

        // 07. 기존 최종 Revision 기준 - QDB, Activity_plan 생성
        param.put("revisionId", prRevision.getRevisionId());
        Map prevRevision = revisionService.getPrevLastRevision(param);      // 7-1. 직전 Revision 정보를 가져온다.
        boolean isQdbExist = c3RService.checkQdbExists(prevRevision);       // 7-2. 직전 Revision 기준 QDB 존재여부 확인

        Map<String, Object> qdbParam = new HashMap<>();
        if (prevRevision != null && isQdbExist) {
            log.info("QDB 복사 START");
            log.info("계약변경 ID: {}", EtcUtil.nullConvert(prevRevision.get("cntrctChgId")));
            log.info("직전 REVISION ID: {}", EtcUtil.nullConvert(prevRevision.get("revisionId")));
            log.info("계약차수: {}", EtcUtil.nullConvert(prevRevision.get("cntrctPhase")));
            log.info("계약회차: {}", EtcUtil.nullConvert(prevRevision.get("cntrctChgNo")));
            log.info("리비젼순위: {}", EtcUtil.nullConvert(prevRevision.get("revRank")));

            qdbParam.put("CNTRCT_NO", param.get("cntrctNo"));
            qdbParam.put("CNTRCT_CHG_ID", prRevision.getCntrctChgId());
            qdbParam.put("REVISION_ID", prRevision.getRevisionId());
            qdbParam.put("PREV_REVISION_ID", prevRevision.get("revisionId"));
            qdbParam.put("USR_ID", param.get("usrId"));

            c3RService.insertQdbActivityPlanDataWithRevision(qdbParam);
        }

        // API 송신 (CAIROS -> PGAIA)
        rst.put("cntrctNo", cntrctNo);
        rst.put("usrId", usrId);

        /**
         * API 연동 판별변수 CAIROS -> PGAIA
         * 1. 사업이 공공(P) 경우
         * 2. MENU API 사용여부 (Y)
         * 3. 현재플랫폼이 카이로스
         */
        boolean isApiEnabled = "Y".equals(isApiYn)
                && "P".equals(pjtDiv)
                && PlatformType.CAIROS.getName().equals(platform);
        if (isApiEnabled) {
            Map<String, Object> apiVO = new HashMap<>();
            apiVO.put("prRevision", prRevision);
            apiVO.put("reqBody", rst);
            apiVO.put("prevRevision", prevRevision);
            apiVO.put("isQdbExist", isQdbExist);
            Map result = invokeCairos2Pgaia("CAGA2002", apiVO);

            String resultCode = result.get("resultCode").toString();
            String resultMsg = result.get("resultMsg").toString();
            if (!"00".equals(resultCode)) {
                log.error("Cairos2Pgaia - CAGA2001 Error {}", resultMsg);

                // msg.api.001 - API 통신이 원활하지 않습니다. 관리자에게 문의하길 바랍니다.
                String message = messageSource.getMessage("msg.api.001", null, LocaleContextHolder.getLocale());
                throw new GaiaBizException(ErrorType.INTERFACE, "[Cairos2Pgaia - CAGA2001]" + message);
            }
        }

        // TODO API LOG 추가 (PR -> CA, CA -> GA)
    }

    // TODO 반환 타입 정의
    // TODO 파라미터 정의
    public void updatePrimaveraActivity() {
        // 1. List<Activity> 정보를 가져온다.
        // List activityList = activityService.getActivityListForPrimavera();

        // 2. Primavera 로 정보를 전송한다. (구현 필요)
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("login_id", usrId);
//        requestBody.put("platform", platform);
//        requestBody.put("workType", "GAPR0040");

//        Map<String, Object> respMap = apiService.primaveraApiGetPost(requestBody).getDetails();
//        LinkedHashMap<String, Object> rst = (LinkedHashMap<String, Object>) respMap.get("data");


        // 3. 수행 결과를 반환한다.

    }


    // ----------------------------------------API통신--------------------------------------------

    /**
     * CAGA2001=REVISION, WBS, ACTIVITY 입력
     * CAGA2002=REVISION, WBS, ACTIVITY 수정
     * CAGA2003=REVISION, WBS, ACTIVITY 삭제
     *
     * @param transactionId
     * @param params
     * @return
     */
    @Transactional
    public Map receiveInterfaceService(String transactionId, Map params) {
        log.info("receiveInterfaceService - {}", transactionId);
        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "00");
        result.put("resultMsg", "정상 처리되었습니다/");

        if (MapUtils.isEmpty(params)) {
            result.put("resultCode", "01");
            result.put("resultMsg", "params is empty");
        }

        try {
            if("CAGA2001".equals(transactionId) || "CAGA2002".equals(transactionId)) {

                /* 사용 변수 set */
                // 01. 직전 Revision 데이터
                Map<String, Object> prevRevision = objectMapper.convertValue(params.get("prevRevision"), Map.class);
                // 02. Revision 데이터
                PrRevision prRevision = objectMapper.convertValue(params.get("prRevision"), PrRevision.class);
                // 03. WBS, Activity 데이터
                LinkedHashMap<String, Object> rst = (LinkedHashMap<String, Object>) params.get("reqBody");
                // 04. 직전 Revision QDB 존재여부
                boolean isQdbExist = objectMapper.convertValue(params.get("isQdbExist"), Boolean.class);


                String cntrctNo = rst.get("cntrctNo").toString();
                String usrId = rst.get("usrId").toString();

                // 00. 초기화 - Update 하려는 revision이 최종버전일 경우 계약건에 대한 setLastRevisionYn("N") 처리
                if("Y".equals(prRevision.getLastRevisionYn())) {
                    revisionService.updateLastYn(cntrctNo);
                }

                // 01. PR_REVISION insert / update
                if ("CAGA2001".equals(transactionId)) {
                    prRevision.setDltYn("N");
                    revisionService.insertRevision(prRevision);
                } else {
                    prRevision.setChgId(usrId);
                    prRevision.setChgDt(LocalDateTime.now());
                    revisionService.updateRevision(prRevision);
                }

                // 02. 초기화 (삭제)
                HashMap<String, Object> deleteVO = new HashMap<>();
                deleteVO.put("CNTRCT_CHG_ID", prRevision.getCntrctChgId());
                deleteVO.put("REVISION_ID", prRevision.getRevisionId());
                activityService.deleteActivity(deleteVO);
                wbsService.deleteWbs(deleteVO);

                // 03. insert to PR_WBS
                ArrayList<HashMap<String, Object>> wbsList = (ArrayList<HashMap<String, Object>>) rst.get("wbsList");
                wbsService.createWbs(prRevision, wbsList);

                // 04. insert to PR_ACTIVITY
                ArrayList<HashMap<String, Object>> activityList = (ArrayList<HashMap<String, Object>>) rst.get("activityList");
                activityService.createActivity(prRevision, activityList);

                // 05. insert 후 코드정보 가공.
                HashMap<String, Object> vo = new HashMap<>();
                vo.put("CNTRCT_CHG_ID", prRevision.getCntrctChgId());
                vo.put("REVISION_ID", prRevision.getRevisionId());
                wbsService.updateWbsCd(vo);

                // 06. 기존 최종 Revision 기준 - QDB, Activity_plan 생성
                Map<String, Object> qdbParam = new HashMap<>();
                if (prevRevision != null && isQdbExist) {
                    log.info("계약변경 ID: {}", prevRevision.get("cntrctChgId").toString());
                    log.info("직전 REVISION ID: {}", prevRevision.get("revisionId").toString());
                    log.info("계약차수: {}", prevRevision.get("cntrctPhase").toString());
                    log.info("계약회차: {}", prevRevision.get("cntrctChgNo").toString());
                    log.info("리비젼순위: {}", prevRevision.get("revRank").toString());

                    qdbParam.put("CNTRCT_CHG_ID", prRevision.getCntrctChgId());
                    qdbParam.put("REVISION_ID", prRevision.getRevisionId());
                    qdbParam.put("PREV_REVISION_ID", prevRevision.get("revisionId"));
                    qdbParam.put("USR_ID", usrId);
                    c3RService.insertQdbActivityPlanDataWithRevision(qdbParam);
                }


            } else if ("CAGA2003".equals(transactionId)) {

                // DO BUSINESS LOGIC
                List<DeleteRevisionInput> delRevisionList = objectMapper.convertValue(params.get("delRevisionList"), new TypeReference<>() {});
                String usrId = params.get("usrId").toString();

                // REVISION 논리삭제
                revisionService.deleteRevision(delRevisionList, usrId);

                // WBS, ACTIVITY 논리삭제
                HashMap<String, Object> deleteVO = new HashMap<>();
                deleteVO.put("CNTRCT_CHG_ID", delRevisionList.getFirst().getCntrctChgId());
                deleteVO.put("REVISION_ID", delRevisionList.getFirst().getRevisionId());
                deleteVO.put("USR_ID", usrId);
                activityService.updateActivityDeleteState(deleteVO);
                wbsService.updateWbsDeleteState(deleteVO);
            }
        }  catch (GaiaBizException ex) {
            result.put("resultCode", "01");
            result.put("resultMsg", ex.getMessage());
        }

        return result;
    }

}
