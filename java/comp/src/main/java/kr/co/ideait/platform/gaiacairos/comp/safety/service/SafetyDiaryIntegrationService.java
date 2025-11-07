package kr.co.ideait.platform.gaiacairos.comp.safety.service;

import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DocumentManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.UbiReportClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 안전일지 외부 인터페이스 연계 서비스
 */
@Slf4j
@Service
public class SafetyDiaryIntegrationService extends AbstractGaiaCairosService {

    @Autowired
    UbiReportClient ubiReportClient;

    @Autowired
    SafetyDiaryService safetyDiaryService;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    DocumentManageService documentManageService;

    @Autowired
    DocumentServiceClient documentServiceClient;

    private static final String DEFAULT_MAPPER_PATH = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetydiary";

    /**
     * 안전일지 통합문서 - 승인 완료시 연계
     * @param safetyDiaryId
     * @param cntrctNo
     * @param xAuth
     * @param cookie
     */
    @Async("taskExecutor")
    public void makeSafetyDiaryPdf(String safetyDiaryId, String cntrctNo, String xAuth, String cookie) {
        log.info("makeSafetyDiaryPdf: 안전일지 문서 변환 시작. safetyDiaryId = {}}", safetyDiaryId);

        try {
            if (safetyDiaryId == null) {
                throw new IllegalArgumentException("makeSafetyDiaryPdf: ID 가 유효하지 않습니다.");
            }

            String[] reportIds = {"safety-report/safety_report.jrf"};

            Map<String, String> reportParams = new HashMap<>();
            reportParams.put("p_safeDiaryId", safetyDiaryId);
            reportParams.put("p_baseUrl", apiCairosDomain.replaceAll("/+$", "") + "/");

            // dev: /home/dev/storage/upload/, stg: /home/ubuntu/GAIA/upload/ prod: /home/ubuntu/GAIA/upload/
            String imgDir = previewPath.replaceAll("(upload[/\\\\]?).*$", "");

            reportParams.put("p_imgDir", imgDir);
            reportParams.put("cntrctNo", cntrctNo);

            Map<String, String> callbackInfo = new HashMap<>();
            String accessToken = StringUtils.defaultString(xAuth, cookie);
            callbackInfo.put("x-auth", accessToken);
            callbackInfo.put("reqKey", "safetyDiaryId");
            callbackInfo.put("reqValue", safetyDiaryId);
            callbackInfo.put("pdfName", "pdf_doc");
            String apiUrl = "";

            if("GAIA".equals(platform.toUpperCase())){
                apiUrl = apiGaiaDomain;
            }
            else if("PGAIA".equals(platform.toUpperCase())){
                apiUrl = apiPGaiaDomain;
            }
            else if("CAIROS".equals(platform.toUpperCase())){
                apiUrl = apiCairosDomain;
            }
            
            callbackInfo.put("callbackUrl", apiUrl + "/interface/safetyDiaryDoc/callback-result");


            log.info("makeSafetyDiaryPdf: 데이터 세팅 완료. safetyDiaryId = {}, reportParams = {}, callbackInfo = {}", safetyDiaryId, cntrctNo, reportParams, callbackInfo);
            ubiReportClient.export(reportIds, reportParams, callbackInfo);

        } catch (RuntimeException e) {
            throw new GaiaBizException(ErrorType.ETC, "안전일지 리포트 변환 중 오류 발생. error = ", e);
        }
    }

    /**
     * 안전일지 통합문서 - 완성된 안전일지 문서 DISK 저장 및 DB 업데이트
     * @param pdfFile
     * @param safetyDiaryId
     * @param accessToken
     * @return
     * @throws IOException
     */
    public Map<String, String> updateDiskFileInfo(List<MultipartFile> pdfFile, String safetyDiaryId, String accessToken) throws IOException {
        Map<String, String> result = new HashMap<>();

        log.info("updateDiskFileInfo: 안전일지 문서 DISK 저장 및 업데이트 safetyDiaryId = {}", safetyDiaryId);

        // 안전일지 정보 조회
        Map<String, Object> safetyDiary = safetyDiaryService.getSafetyDiaryDetail(Map.of("safeDiaryId", safetyDiaryId));
        log.info("updateDiskFileInfo: 안전일지 조회 성공 safetyDiaryId = {}, result = {}", safetyDiaryId, safetyDiary);

        // 통합문서 관리 PdF 속성 데이터 저장
        // 속성 코드 조회
        SmComCode smComCode = commonCodeService.getCommonCodeByGrpCdAndCmnCd(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE, "8");

        // 속성 코드 조회
        List<CbgnPropertyDto> properties = null;
        HashMap<String,Object> docResult = documentManageService.getCbgnAndProperties("APP05");
        if(docResult.get("properties") != null) {
            // 속성 데이터 저장
            properties = (List<CbgnPropertyDto>) docResult.get("properties");

        } else {
            throw new GaiaBizException(ErrorType.NO_DATA, "속성 코드가 존재하지 않습니다.");
        }

        List<DocumentForm.PropertyData> propertyData = new ArrayList<>();
        propertyData = this.savePdfPropertyDataToDoc(properties, safetyDiary);




        final String navId = String.format("nav_%s_%s_01", MapUtils.getString(safetyDiary, "cntrct_no"), smComCode.getAttrbtCd3());

        String cntrctNo = MapUtils.getString(safetyDiary, "cntrct_no");
        String pjtNo = this.getPjtNoByCntrctNo(Map.of("cntrctNo", cntrctNo));
        String rgstrId = MapUtils.getString(safetyDiary, "rgstr_id");
        String docNm = MapUtils.getString(safetyDiary, "repo_no") + ".pdf";

        DocumentForm.DocCreateEx requestParams = new DocumentForm.DocCreateEx();
        requestParams.setNaviId(navId);
        requestParams.setNaviDiv("01");
        requestParams.setPjtNo(pjtNo);
        requestParams.setCntrctNo(cntrctNo);
        requestParams.setNaviPath("안전일지");
        requestParams.setNaviNm("안전일지");
        requestParams.setUpNaviNo(0);
        requestParams.setUpNaviId("");
        requestParams.setNaviLevel((short) 1);
        requestParams.setNaviType("FOLDR");
        requestParams.setNaviFolderType("8");
        requestParams.setNaviFolderKind(smComCode.getAttrbtCd3());
        requestParams.setProperties(documentManageService.parseToPropertyCreate(properties, navId));
        requestParams.setPropertyData(propertyData);
        requestParams.setRgstrId(rgstrId);
        requestParams.setDocNm(docNm);

        Map<String, String> newHeaders = Maps.newHashMap();
        newHeaders.put("x-auth", accessToken);

        log.info("updateDiskFileInfo: 안전일지 PDF 문서 및 속성 저장 param = {}", requestParams);
        List<DcStorageMain> createFileResultList = documentServiceClient.createFile(requestParams, pdfFile, newHeaders);

        // 안전일지에 docId 업데이트
        if(createFileResultList != null) {
            DcStorageMain dcStorageMain = createFileResultList.get(0);
            result.put("cntrctNo", cntrctNo);
            result.put("safeDiaryId", safetyDiaryId);
            result.put("docId", dcStorageMain.getDocId());
        }
        return result;
    }

    /**
     * 안전일지 통합문서 - 통합문서 속성데이터 저장
     * @param properties
     * @param safetyDiary
     * @return
     */
    public List<DocumentForm.PropertyData> savePdfPropertyDataToDoc(List<CbgnPropertyDto> properties, Map<String, Object> safetyDiary) {
        log.info("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 properties = {}, cwDailyReport = {}", properties, safetyDiary);
        List<DocumentForm.PropertyData> insertList = new ArrayList<>();

        try {

            for (CbgnPropertyDto property : properties) {
                String attrbtCd = property.getAttrbtCd();

                String apprvlStatsTxt = "A".equals(MapUtils.getString(safetyDiary, "apprvl_stats")) ? "승인" : "반려";
                String safeDiaryId = MapUtils.getString(safetyDiary, "safe_diary_id");
                String repoDt = MapUtils.getString(safetyDiary, "repo_dt");
                String repoNo = MapUtils.getString(safetyDiary, "repo_no");
                String rgstrId = MapUtils.getString(safetyDiary, "rgstr_id");
                String chgId = MapUtils.getString(safetyDiary, "chg_id");

                if (attrbtCd != null) {
                    String attrbtCntnts = null;

                    switch (attrbtCd) {
                        case "safeDiaryId":
                            attrbtCntnts = safeDiaryId;
                            break;
                        case "repoDt":
                            attrbtCntnts = repoDt;
                            break;
                        case "repoNo":
                            attrbtCntnts = repoNo;
                            break;
                        case "apprvlStatsTxt":
                            attrbtCntnts = apprvlStatsTxt;
                            break;
                    }

                    if (attrbtCntnts != null && !attrbtCntnts.isBlank()) {
                        DocumentForm.PropertyData row = new DocumentForm.PropertyData();
                        row.setAttrbtCd(attrbtCd);
                        row.setAttrbtCntnts(attrbtCntnts);
                        row.setRgstrId(rgstrId);
                        row.setChgId(chgId);

                        insertList.add(row);
                    }
                }
            }
            log.info("savePdfPropertyDataToDoc: 데이터 저장 결과 = {}", insertList);

        } catch (RuntimeException e) {
            log.warn("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 중 오류 발생 메세지 = {}", e.getMessage());
            insertList = null;
        }
        return insertList;
    }

    /**
     * 안전일지 통합문서 - docId 업데이트
     * @param params
     */
    public void updateSafetyDiaryDocId(Map<String, String> params) {
        log.info("updateSafetyDiaryDocId: 안전일지 docId 업데이트 진행 params = {}", params);
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateSafetyDiaryDocId", params);
    }

    /**
     * 안전일지 통합문서 - 프로젝트 번호 조회
     * @param params
     */
    public String getPjtNoByCntrctNo(Map<String, String> params) {
        String pjtNo = mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getPjtNoByCntrctNo", params);
        log.info("getPjtNoByCntrctNo: 안전일지 프로젝트번호 조회 params = {} PJT_NO = {}", params, pjtNo);
        return pjtNo;
    }

    /**
     * 안전일지 전자결재 - 결재자가 pgaia 사용자 여부 확인
     * @param param
     * @return
     */
    public boolean isApproverFromPgaia(Map<String, Object> param) {
        String statement = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkPgaiaFirstApprover";
        return mybatisSession.selectOne(statement, param);
    }

    /**
     * 안전일지 전자결재 - 승인상태 변경
     * @param safetyDiaryId 안전일지ID
     * @param cntrctNo 계약번호
     * @param usrId 사용자명
     * @param apMap 전자결재 상태
     */
    public void updateApprovalStatus(String safetyDiaryId, String cntrctNo, String usrId, Map<String, Object> apMap) {
        Map<String, Object> param = new HashMap<>();
        param.put("safetyDiaryId", safetyDiaryId);

        String apprvlStats = apMap.get("apprvlStats").toString();
        param.put("apprvlStats", apprvlStats);

        if("E".equals(apprvlStats)) {   // 승인 요청
            param.put("apprvlReqId", usrId);
            mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateApprovalStatusRequest", param);
        } else { // 승인, 반려
            param.put("apprvlId", usrId);
            String apprvlOpin = apMap.get("apprvlOpin") != null
                    ? apMap.get("apprvlOpin").toString() : "";
            param.put("apprvlOpin", apprvlOpin);
            mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateApprovalStatusNext", param);
        }

        // 승인 완료시 PDF 문서화 처리
        if ("A".equals(apprvlStats)) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            this.makeSafetyDiaryPdf(safetyDiaryId, cntrctNo, request.getHeader("x-auth"), cookieService.getCookie(request, cookieVO.getTokenCookieName()));
        }


    }


    /**
     * 안전일지 전자결재 - apDocId 업데이트
     * @param param
     */
    public void updateApprovalDocId(Map<String, Object> param) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateApprovalDocId", param);
    }

    /**
     * 안전일지 전자결재 - apDocId 기준 안전일지 조회
     * @param apDocId
     * @return
     */
    public Map<String, Object> selectSafetyDiaryByApDocId(String apDocId) {
        Map<String, Object> safetyDiary = mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".selectSafetyDiaryByApDocId", apDocId);
        return safetyDiary;
    }


    /**
     * 안전일지 전자결재 - 승인, 반려 업데이트
     * @param apDocId
     * @param apUsrId
     * @param apDocStats
     * @param apUsrOpnin
     */
    public void updateSafetDiaryByApDocId(String apDocId, String apUsrId, String apDocStats, String apUsrOpnin) {
        // 1. apDocId 기준 안전일지 조회
        Map<String, Object> diary = mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".selectSafetyDiaryByApDocId", apDocId);

        if (diary == null) {
            throw new BizException("ApDocId와 일치하는 보고서가 없습니다 : " + apDocId);
        }

        // 2. 안전일지 상태 값 업데이트
        String safeDiaryId = MapUtils.getString(diary, "safe_diary_id");
        String cntrctNo = MapUtils.getString(diary, "cntrct_no");

        Map<String, Object> apMap = new HashMap<>();
        String apStats = "C".equals(apDocStats) ? "A" : "R";
        apMap.put("apprvlStats", apStats);
        apMap.put("apprvlOpin", apUsrOpnin);

        this.updateApprovalStatus(safeDiaryId, cntrctNo, apUsrId, apMap);
    }

    /**
     * 안전일지 전자결재 - 연관 업무 조회 (교육현황)
     * @param params
     * @return
     */
    public Map<String, Object> selectEducationDiaryByRepoDt(Map<String, Object> params) {
        Map<String, Object> resourceMap = new HashMap<>();

        // 교육현황
        List<Map<String, Object>> eduList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getEducationDiaryByRepoDt", params);
        List<Map<String, Object>> eduPersonList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getEducationDiaryPersonByRepoDt", params);

        resourceMap.put("eduList", eduList);
        resourceMap.put("eduPersonList", eduPersonList);
        return resourceMap;
    }

    /**
     * 안전일지 전자결재 - 연관 업무 조회 (재해현황)
     * @param params
     * @return
     */
    public Map<String, Object> selectDisasterDiaryByRepoDt(Map<String, Object> params) {
        Map<String, Object> resourceMap = new HashMap<>();

        // 재해현황
        List<Map<String, Object>> disasterList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getDisasterDiaryByRepoDt", params);
        List<Map<String, Object>> disasterPersonList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getDisasterDiaryPersonByRepoDt", params);

        resourceMap.put("disasterList", disasterList);
        resourceMap.put("disasterPersonList", disasterPersonList);
        return resourceMap;
    }

    /**
     * 안전일지 전자결재 - CAIROS -> PGAIA 연계 후 INSERT
     * @param diary 재해현황
     * @param workList 작업현황
     * @param patrolList 안전/점검 현황
     * @param eduList 교육일지 현황
     * @param eduPersonList 교육일지 참석인원
     * @param disasterList 재해일지
     * @param disasterPersonList 재해일지 참석인원
     */
    public void insertResourcesToApi(
            Map<String, Object> diary,
            List<Map<String, Object>> workList,
            List<Map<String, Object>> patrolList,
            List<Map<String, Object>> eduList,
            List<Map<String, Object>> eduPersonList,
            List<Map<String, Object>> disasterList,
            List<Map<String, Object>> disasterPersonList) {

        String usrId = MapUtils.getString(diary, "rgstr_id");

        // 1. 안전일지
        Map<String, Object> camelDiary = new HashMap<>();

        camelDiary.put("safeDiaryId", diary.get("safe_diary_id"));
        camelDiary.put("cntrctNo",    diary.get("cntrct_no"));
        camelDiary.put("repoDt",      diary.get("repo_dt"));
        camelDiary.put("repoNo",      diary.get("repo_no"));
        camelDiary.put("title",       diary.get("title"));
        camelDiary.put("forcAm",      diary.get("forc_am"));
        camelDiary.put("forcPm",      diary.get("forc_pm"));
        camelDiary.put("taMax",       diary.get("ta_max"));
        camelDiary.put("taMin",       diary.get("ta_min"));
        camelDiary.put("officeM",     diary.get("office_m"));
        camelDiary.put("officeF",     diary.get("office_f"));
        camelDiary.put("laborM",      diary.get("labor_m"));
        camelDiary.put("laborF",      diary.get("labor_f"));
        camelDiary.put("equipM",      diary.get("equip_m"));
        camelDiary.put("equipF",      diary.get("equip_f"));
        camelDiary.put("cusum",       diary.get("cusum"));
        camelDiary.put("gmRevOpin",   diary.get("gm_rev_opin"));
        camelDiary.put("accFreTargTm",diary.get("acc_fre_targ_tm"));
        camelDiary.put("accFreYdayTm",diary.get("acc_fre_yday_tm"));
        camelDiary.put("accFreTdayTm",diary.get("acc_fre_tday_tm"));
        camelDiary.put("accFreYearCu",diary.get("acc_fre_year_cu"));


        Object value = diary.get("ap_req_dt");
        if (value instanceof Long) { camelDiary.put("apReqDt", new Timestamp((Long) value)); }
        else { camelDiary.put("apReqDt", value); }

        camelDiary.put("apReqId",     diary.get("ap_req_id"));
        camelDiary.put("apDocId",     diary.get("ap_doc_id"));
        camelDiary.put("apprvlId",    diary.get("apprvl_id"));
        camelDiary.put("apprvlDt",    diary.get("apprvl_dt"));
        camelDiary.put("apprvlStats", diary.get("apprvl_stats"));
        camelDiary.put("usrId",       usrId);
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".addSafetyDiary", camelDiary);

        // 2. 안전일지 - 작업현황
        for (Map work : workList) {
            Map<String, Object> camelWork = new HashMap<>();
            camelWork.put("safeDiaryId", work.get("safe_diary_id"));
            camelWork.put("workId", work.get("work_id"));
            camelWork.put("workItem", work.get("work_item"));
            camelWork.put("workCheck", work.get("work_check"));
            camelWork.put("workResult", work.get("work_result"));
            camelWork.put("worker", work.get("worker"));
            camelWork.put("usrId", usrId);
            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".addSafetyDiaryWorkStatus", camelWork);
        }

        // 3. 안전일지 - 안전/순회 점검 현황
        for (Map patrol : patrolList) {
            Map<String, Object> camelPatrol = new HashMap<>();
            camelPatrol.put("safeDiaryId", patrol.get("safe_diary_id"));
            camelPatrol.put("checkId", patrol.get("check_id"));
            camelPatrol.put("checker", patrol.get("checker"));
            camelPatrol.put("checkTm", patrol.get("check_tm"));
            camelPatrol.put("checkAction", patrol.get("check_action"));
            camelPatrol.put("checkResult", patrol.get("check_result"));
            camelPatrol.put("usrId", usrId);
            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".addSafetyDiaryPatrolStatus", camelPatrol);
        }

        // 4. 교육일지 현황
        for (Map edu : eduList) {
            Map<String, Object> camelEdu = new HashMap<>();
            camelEdu.put("eduId", edu.get("edu_id"));
            camelEdu.put("cntrctNo", edu.get("cntrct_no"));
            camelEdu.put("eduDt", edu.get("edu_dt"));
            camelEdu.put("eduType", edu.get("edu_type"));
            camelEdu.put("eduSurvM", edu.get("edu_surv_m"));
            camelEdu.put("eduSurvF", edu.get("edu_surv_f"));
            camelEdu.put("eduSurvNote", edu.get("edu_surv_note"));
            camelEdu.put("eduActiM", edu.get("edu_acti_m"));
            camelEdu.put("eduActiF", edu.get("edu_acti_f"));
            camelEdu.put("eduActiNote", edu.get("edu_acti_note"));
            camelEdu.put("eduNoActiM", edu.get("edu_no_acti_m"));
            camelEdu.put("eduNoActiF", edu.get("edu_no_acti_f"));
            camelEdu.put("eduNoActiNote", edu.get("edu_no_acti_note"));
            camelEdu.put("outline", edu.get("outline"));
            camelEdu.put("subject", edu.get("subject"));
            camelEdu.put("method", edu.get("method"));
            camelEdu.put("time", edu.get("time"));
            camelEdu.put("textbook", edu.get("textbook"));
            camelEdu.put("location", edu.get("location"));
            camelEdu.put("note", edu.get("note"));
            camelEdu.put("rgstrId", edu.get("rgstr_id"));
            camelEdu.put("chgId", edu.get("chg_id"));
            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".upsertEducationDiaryList", camelEdu);
        }

        // 5. 교육일지 참석자
        for (Map person : eduPersonList) {
            Map<String, Object> camelEduPerson = new HashMap<>();
            camelEduPerson.put("eduId", person.get("edu_id"));
            camelEduPerson.put("eduVicSeq", person.get("edu_vic_seq"));
            camelEduPerson.put("eduVicOccu", person.get("edu_vic_occu"));
            camelEduPerson.put("eduVicNm", person.get("edu_vic_nm"));
            camelEduPerson.put("rgstrId", person.get("rgstr_id"));
            camelEduPerson.put("chgId", person.get("chg_id"));
            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".upsertEducationDiaryPersonList", camelEduPerson);
        }

        // 6. 재해일지
        for (Map disaster : disasterList) {
            Map<String, Object> camelDisaster = new HashMap<>();
            camelDisaster.put("disasId", disaster.get("disas_id"));
            camelDisaster.put("cntrctNo", disaster.get("cntrct_no"));
            camelDisaster.put("disasDt", disaster.get("disas_dt"));
            camelDisaster.put("disasCause", disaster.get("disas_cause"));
            camelDisaster.put("disasAction", disaster.get("disas_action"));
            camelDisaster.put("rgstrId", disaster.get("rgstr_id"));
            camelDisaster.put("chgId", disaster.get("chg_id"));
            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".upsertDisasterDiaryList", camelDisaster);
        }

        // 7. 재해일지 인원
        for (Map person : disasterPersonList) {
            Map<String, Object> camelDisasterPerson = new HashMap<>();
            camelDisasterPerson.put("disasId", person.get("disas_id"));
            camelDisasterPerson.put("disasVicSeq", person.get("disas_vic_seq"));
            camelDisasterPerson.put("diasaVicOccu", person.get("diasa_vic_occu"));
            camelDisasterPerson.put("diasaVicNm", person.get("diasa_vic_nm"));
            camelDisasterPerson.put("rgstrId", person.get("rgstr_id"));
            camelDisasterPerson.put("chgId", person.get("chg_id"));
            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".upsertDisasterDiaryPersonList", camelDisasterPerson);
        }


    }

    /**
     * 안전일지 전자결재 - 취소시 업무
     * @param apDocs
     * @param usrId
     * @param toApi
     */
    public void updateSafetyDiaryReqCancel(List<ApDoc> apDocs, String usrId, boolean toApi) {
        apDocs.forEach(apDoc -> {
            log.info("안전일지 전자결재 - toApi={} 취소시작 apDoc={} ",toApi, apDoc);
            // 1. apDocId 기준 안전일지 조회
            Map<String, Object> safetyDiary = this.selectSafetyDiaryByApDocId(apDoc.getApDocId());
            if (safetyDiary == null) return;

            if (toApi) {
                // api 통신 true -> 데이터 삭제
                Map<String, Object> param = new HashMap<>();
                param.put("safeDiaryId", MapUtils.getString(safetyDiary, "safe_diary_id", ""));
                param.put("repoDt", MapUtils.getString(safetyDiary, "repo_dt", ""));
                param.put("cntrctNo", MapUtils.getString(safetyDiary, "cntrct_no", ""));
                param.put("usrId", usrId);

                // 안전일지 논리삭제
                mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteSafetyDiary", param);
                // 안전일지-작업현황 논리삭제
                mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteSafetyDiaryWorkStatus", param);
                // 안전일지-순회/점검 논리삭제
                mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteSafetyDiaryPatrolStatus", param);
                // 교육일지 논리삭제
                mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteEducationDiaryByRepoDt", param);
                // 교육일지 인원 논리삭제
                mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteEducationDiaryPersonByRepoDt", param);
                // 재해일지 논리삭제
                mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteDisasterDiaryByRepoDt", param);
                // 재해일지 인원 논리삭제
                mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteDisasterDiaryPersonByRepoDt", param);
            } else {
                // api 통신 false -> 컬럼 값 변경
                String safeDiaryId = MapUtils.getString(safetyDiary, "safe_diary_id");
                mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateApprovalStatusCancel", Map.of("safetyDiaryId", safeDiaryId));
            }

            // FIXME

        });
    }

    /**
     * 안전일지 전자결재, 통합문서 - 초기화 처리
     * @param safetyDiary
     */
    public void cancelSafetyDiaryApproval(Map<String, Object> safetyDiary) {
        String safeDiaryId = MapUtils.getString(safetyDiary, "safe_diary_id");
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateApprovalStatusCancel", Map.of("safeDiaryId", safeDiaryId));
    }
}
