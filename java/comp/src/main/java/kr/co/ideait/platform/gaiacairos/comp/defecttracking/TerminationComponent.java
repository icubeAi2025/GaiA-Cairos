package kr.co.ideait.platform.gaiacairos.comp.defecttracking;

import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.DefectTrackingService;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.TerminationService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiency;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingMybatisParam.DefectTrackingListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingMybatisParam.DfccySearchInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.termination.TerminationForm.CreateUpdateForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.termination.TerminationForm.DeleteTermination;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.termination.TerminationForm.TerminationAll;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TerminationComponent extends AbstractComponent {

    @Autowired
    TerminationService terminationService;

    @Autowired
    DefectTrackingService defectTrackingService;


    /**
     * 종결관리 - 결함 목록 조회
     * @param dfccySearchInput
     * @param langInfo
     * @return
     */
    public Page<DefectTrackingListOutput> getDfccyListToGrid(DfccySearchInput dfccySearchInput, String langInfo) {
        MybatisInput input = createMybatisInput(dfccySearchInput, langInfo);
        List<DefectTrackingListOutput> output = terminationService.getDfccyList(input);
        Long totalCount = terminationService.getDfccyListCount(input);
        return new PageImpl<>(output, input.getPageable(), totalCount);
    }


    /**
     * 결함 목록/상세 조회를 위한 MybatisInput 생성
     * @param dfccySearchInput
     * @param langInfo
     * @return
     */
    private MybatisInput createMybatisInput(DfccySearchInput dfccySearchInput, String langInfo) {
        if ("my".equals(dfccySearchInput.getRgstr())) {
            dfccySearchInput.setRgstr(UserAuth.get(true).getUsrId());
        }

        String confirm = "완료";
        String ing = "진행중";
        String end = "종료";

        MybatisInput input = MybatisInput.of()
                .add("cntrctNo", dfccySearchInput.getCntrctNo())
                .add("dfccyPhaseNo", dfccySearchInput.getDfccyPhaseNo())
                .add("lang", langInfo)
                .add("pageable", dfccySearchInput.getPageable())
                .add("searchInput", dfccySearchInput)
                .add("usrId", UserAuth.get(true).getUsrId())
                .add("confirm", confirm)
                .add("ing", ing)
                .add("end", end)
                .add("rplyStatus", CommonCodeConstants.REPLY_CODE_GROUP_CODE)
                .add("qaStatus", CommonCodeConstants.QA_CODE_GROUP_CODE)
                .add("spvsStatus", CommonCodeConstants.SPVS_CODE_GROUP_CODE)
                .add("edCd", CommonCodeConstants.ED_CODE_GROUP_CODE)
                .add("dfccyCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);

        input.setPageable(dfccySearchInput.getPageable());
        return input;
    }


    /**
     * 종결 추가, 수정
     * @param createUpdateForm
     * @param user
     * @param commonReqVo
     * @return
     */
    @Transactional
    public DtDeficiency saveTermination(CreateUpdateForm createUpdateForm, UserAuth user, CommonReqVo commonReqVo) {
        DtDeficiency deficiency = defectTrackingService.getDeficiency(createUpdateForm.getCntrctNo(), createUpdateForm.getDfccyNo());
        if(deficiency == null){
            throw new GaiaBizException(ErrorType.NO_DATA, "결함 정보가 존재하지 않습니다.");
        }

        deficiency.setEdCd(createUpdateForm.getEdCd());
        deficiency.setEdRgstrId(user.getUsrId());
        deficiency.setEdRgstDt(LocalDateTime.now());
        deficiency.setChgId(user.getUsrId());
        deficiency = defectTrackingService.saveDeficiency(deficiency);

        if(!"P".equals(commonReqVo.getPjtDiv()) || !"Y".equals(commonReqVo.getApiYn())) return deficiency;

        Map<String, Object> sendParams = new HashMap<>();
        sendParams.put("deficiency", deficiency);
        sendParams.put("isList", false);

        sendToApi(sendParams);

        return deficiency;
    }


    /**
     * 종결 일괄 추가
     * @param terminationAll
     * @param user
     * @param commonReqVo
     */
    @Transactional
    public void saveTerminationList(TerminationAll terminationAll, UserAuth user, CommonReqVo commonReqVo) {
        List<DtDeficiency> deficiencyList = defectTrackingService.getDeficiencyList(terminationAll.getCntrctNo(), terminationAll.getDfccyNoList());

        if (deficiencyList.isEmpty()) {
            log.warn("조회된 결함이 없습니다. 계약번호: {}, 결함ID 리스트: {}", terminationAll.getCntrctNo(), terminationAll.getDfccyNoList());
            throw new GaiaBizException(ErrorType.NO_DATA,"해당하는 결함이 존재하지 않습니다.");
        }

        // 종결 처리 (edCd 상태 변경)
        deficiencyList.forEach(deficiency -> {
            deficiency.setEdCd(terminationAll.getEdCd());
            deficiency.setEdRgstrId(user.getUsrId());
            deficiency.setEdRgstDt(LocalDateTime.now());
        });

        deficiencyList = defectTrackingService.saveDeficiencyList(deficiencyList);

        if(!"P".equals(commonReqVo.getPjtDiv()) || !"Y".equals(commonReqVo.getApiYn())) return;

        Map<String, Object> sendParams = new HashMap<>();
        sendParams.put("deficiencyList", deficiencyList);
        sendParams.put("isList", true);

        sendToApi(sendParams);

    }


    /**
     * 종결 처리 삭제
     * @param deleteTermination
     * @param user
     * @param commonReqVo
     */
    @Transactional
    public void deleteTerminationList(DeleteTermination deleteTermination, UserAuth user, CommonReqVo commonReqVo) {
        List<DtDeficiency> deficiencyList = defectTrackingService.getDeficiencyList(deleteTermination.getCntrctNo(), deleteTermination.getDfccyNoList());

        if (deficiencyList.isEmpty()) {
            log.warn("조회된 결함이 없습니다. 계약번호: {}, 결함ID 리스트: {}", deleteTermination.getCntrctNo(), deleteTermination.getDfccyNoList());
            throw new GaiaBizException(ErrorType.NO_DATA,"해당하는 결함이 존재하지 않습니다.");
        }

        // 종결 처리 삭제 (edCd 초기화)
        deficiencyList.forEach(deficiency -> {
            deficiency.setEdCd(null);
            deficiency.setEdRgstrId(null);
            deficiency.setEdRgstDt(null);
            deficiency.setChgId(user.getUsrId());
        });

        deficiencyList = defectTrackingService.saveDeficiencyList(deficiencyList);

        if(!"P".equals(commonReqVo.getPjtDiv()) || !"Y".equals(commonReqVo.getApiYn())) return;

        Map<String, Object> sendParams = new HashMap<>();
        sendParams.put("deficiencyList", deficiencyList);
        sendParams.put("isList", true);

        sendToApi(sendParams);
    }


    /**
     * API send
     * @param sendParams
     */
    private void sendToApi(Map<String, Object> sendParams) {
        log.info("종결 저장 API통신 > sendParams: {}", sendParams);

        Map<String, Object> response = new HashMap<>();
        if("CAIROS".equals(platform.toUpperCase())) {
            response = invokeCairos2Pgaia("GACA7400", sendParams);
        } else if("PGAIA".equals(platform.toUpperCase())) {
            response = invokePgaia2Cairos("GACA7400", sendParams);
        }
        if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
            throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
        }
    }


    /**
     * API receive
     * @param msgId
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> receiveApiOfTermination(String msgId, Map<String, Object> params){
        Map<String, Object> result = new HashMap<>();
        result.put("resultCode", "00");

        try {
            log.info("결함추적 > 종결관리 > receive msgId: {}, 연동 params : {}", msgId, params);

            // 종결 추가 or 삭제
            if("GACA7400".equals(msgId)) {
                boolean isList = (boolean)params.get("isList");
                if(isList) {
                    defectTrackingService.saveDeficiencyList(objectMapper.convertValue(params.get("deficiencyList"), new TypeReference<List<DtDeficiency>>() {}));
                } else {
                    defectTrackingService.saveDeficiency(objectMapper.convertValue(params.get("deficiency"), DtDeficiency.class));
                }
            }

        } catch (GaiaBizException e) {
            log.error("[결함 추적 - 종결관리] API receive 중 오류 발생: ", e);
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }
}