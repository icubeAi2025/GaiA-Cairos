package kr.co.ideait.platform.gaiacairos.comp.defecttracking;

import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.SettingService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyPhase;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficientySchedule;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.setting.SettingMybatisParam.DashboardListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.setting.SettingMybatisParam.DisplayOrderMoveInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettingComponent extends AbstractComponent {

    @Autowired
    SettingService settingService;


    /**
     * 결함단계 목록 조회
     * @param input
     * @return
     */
    public List selectDeficiencyPhaseList(MybatisInput input) {
        return settingService.selectDeficiencyPhaseList(input);
    }


    /**
     * 대시보드 결함단계 목록 조회
     * @param input
     * @param pageable
     * @return
     */
    public Page<MybatisOutput> getDashboardList(DashboardListInput input, Pageable pageable) {
        List<MybatisOutput> output = null;
        Long totalCount = null;
        input.setPageable(pageable);
        output = settingService.getDashboardList(input);
        totalCount = settingService.getDashboardListCount(input);
        return new PageImpl<>(output, input.getPageable(), totalCount);
    }


    /**
     * 결함단계 추가 (결함, 답변, 확인, 종결의 기간 설정)
     * @param dtDeficiencyPhase
     * @param scheduleArr
     * @param reqVoMap
     */
    @Transactional
    public void insertDeficiencyPhase(DtDeficiencyPhase dtDeficiencyPhase, List<DtDeficientySchedule> scheduleArr, Map<String, Object> reqVoMap) {

        Short maxDsplyOrdr = settingService.getMaxDsplyOrdr(dtDeficiencyPhase.getCntrctNo());

        // 1. 결함단계 저장
        dtDeficiencyPhase.setDfccyPhaseNo(UUID.randomUUID().toString());
        dtDeficiencyPhase.setDsplyOrdr(++maxDsplyOrdr);
        dtDeficiencyPhase.setDltYn("N");
        dtDeficiencyPhase.setRgstrId(UserAuth.get(true).getUsrId());
        dtDeficiencyPhase.setChgId(UserAuth.get(true).getUsrId());
        DtDeficiencyPhase savedPhase = settingService.saveDeficiencyPhase(dtDeficiencyPhase);

        // 2. 결함 스케쥴 저장
        scheduleArr.forEach(schedule -> {
            schedule.setCntrctNo(savedPhase.getCntrctNo());
            schedule.setDltYn("N");
            schedule.setDfccyPhaseNo(savedPhase.getDfccyPhaseNo());
            schedule.setRgstrId(UserAuth.get(true).getUsrId());
            schedule.setChgId(UserAuth.get(true).getUsrId());
        });
        scheduleArr = settingService.saveDeficientySchedule(scheduleArr);

        if(!"P".equals(reqVoMap.get("pjtDiv")) || !"Y".equals(reqVoMap.get("apiYn"))) return;

        Map<String, Object> sendParams = new HashMap<>();
        sendParams.put("phase", savedPhase);
        sendParams.put("schedule", scheduleArr);

        log.info("결함추적 > 결함단계설정 > 단계 추가 API 통신 - transactionId: GACA7500, sendParams: {},", sendParams);

        sendToApi("GACA7500", sendParams);
    }


    /**
     * 결함단계 수정 (결함, 답변, 확인, 종결의 기간 설정)
     * @param dtDeficiencyPhase
     * @param scheduleArr
     * @param reqVoMap
     */
    @Transactional
    public void updateDeficiencyPhase(DtDeficiencyPhase dtDeficiencyPhase, List<DtDeficientySchedule> scheduleArr, Map<String, Object> reqVoMap) {

        String dfccyPhaseNo = dtDeficiencyPhase.getDfccyPhaseNo();
        List<DtDeficientySchedule> updateScheduleList = new ArrayList<>();
        if (!scheduleArr.isEmpty()) {
            scheduleArr.forEach(schedule -> {
                DtDeficientySchedule findSchedule = settingService.selectDeficientySchedule(dfccyPhaseNo, schedule.getDfccyPhaseCd());
                if (findSchedule != null) {
                    findSchedule.setBgnDate(schedule.getBgnDate());
                    findSchedule.setEndDate(schedule.getEndDate());
                    findSchedule.setChgId(UserAuth.get(true).getUsrId());
                    updateScheduleList.add(findSchedule);
                }
            });
        }
        DtDeficiencyPhase findPhase = settingService.selectDeficiencyPhase(dfccyPhaseNo);
        if (findPhase != null) {
            findPhase.setDfccyPhaseNm(dtDeficiencyPhase.getDfccyPhaseNm());
            findPhase.setChgId(UserAuth.get(true).getPjtNo());
            findPhase = settingService.saveDeficiencyPhase(findPhase);
        }

        if(!"P".equals(reqVoMap.get("pjtDiv")) || !"Y".equals(reqVoMap.get("apiYn"))) return;

        Map<String, Object> sendParams = new HashMap<>();
        sendParams.put("phase", findPhase);
        sendParams.put("schedule", updateScheduleList);

        log.info("결함추적 > 결함단계설정 > 단계 수정 API 통신 - transactionId: GACA7500, sendParams: {},", sendParams);

        sendToApi("GACA7500", sendParams);
    }


    /**
    * 결함단계 삭제(결함 단계 및 일정) -> 삭제 이후 순서 재정렬
    * @param delPhaseList
    */
    @Transactional
    public void deleteDeficiencyPhase(List<String> delPhaseList, String cntrctNo, String pjtNo, boolean toApi, String usrId, Map<String, Object> reqVoMap) {
        settingService.deleteDeficiencyPhase(delPhaseList, usrId);

        // 결함단계 순서 재정렬
        sortDisplayOrder(cntrctNo, usrId);

        if(!"P".equals(reqVoMap.get("pjtDiv")) || !toApi || !"Y".equals(reqVoMap.get("apiYn"))) return;

        Map<String, Object> sendParams = new HashMap<>();
        sendParams.put("delPhaseList", delPhaseList);
        sendParams.put("cntrctNo", cntrctNo);
        sendParams.put("pjtNo", pjtNo);
        sendParams.put("usrId", usrId);
        sendParams.put("reqVoMap", reqVoMap);

        log.info("결함추적 > 결함단계설정 > 단계 삭제 API 통신 - transactionId: GACA7501, sendParams: {},", sendParams);

        sendToApi("GACA7501", sendParams);

    }


    /**
     * 결함단계 순서 변경(up, down 이동)
     * @param moveList
     * @param usrId
     * @param pjtNo
     * @param toApi
     * @param reqVoMap
     */
    @Transactional
    public void updateDisplayOrder(List<DisplayOrderMoveInput> moveList, String usrId, String pjtNo, boolean toApi, Map<String, Object> reqVoMap) {
        moveList.forEach(id -> {
            DtDeficiencyPhase findPhase = settingService.selectDeficiencyPhaseByDfccyPhaseNo(id.getDfccyPhaseNo());
            findPhase.setDsplyOrdr(id.getDsplyOrdr());
            findPhase.setChgId(usrId);
            settingService.saveDeficiencyPhase(findPhase);
        });

        if(!"P".equals(reqVoMap.get("pjtDiv")) || !toApi || !"Y".equals(reqVoMap.get("apiYn"))) return;

        Map<String, Object> sendParams = new HashMap<>();
        sendParams.put("moveList", moveList);
        sendParams.put("pjtNo", pjtNo);
        sendParams.put("usrId", usrId);
        sendParams.put("reqVoMap", reqVoMap);

        log.info("결함추적 > 결함단계설정 > 순서 변경 API 통신 - transactionId: GACA7502, sendParams: {},", sendParams);

        sendToApi("GACA7502", sendParams);
    }


    /**
    * 결함단계 순서 재정렬
    * @param cntrctNo
    */
    @Transactional
    public void sortDisplayOrder(String cntrctNo, String usrId) {
        List<DtDeficiencyPhase> findList =  settingService.selectDeficiencyPhaseListByCntrctNo(cntrctNo);

        if (!findList.isEmpty()) {
            Short newOrder = 1;
            for (DtDeficiencyPhase phase : findList) {
                phase.setDsplyOrdr(newOrder++);
                phase.setChgId(usrId);
            }
            settingService.saveDeficiencyPhaseList(findList);
        }
    }


    /**
     * API send
     * @param transactionId
     * @param sendParams
     */
    private void sendToApi(String transactionId, Map<String, Object> sendParams) {
        Map<String, Object> response = new HashMap<>();
        if("cairos".equals(platform)) {
            response = invokeCairos2Pgaia(transactionId, sendParams);
        } else if("pgaia".equals(platform)) {
            response = invokePgaia2Cairos(transactionId, sendParams);
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
    public Map<String, Object> receiveApiOfSetting(String msgId, Map<String, Object> params){
        Map<String, Object> result = new HashMap<>();
        result.put("resultCode", "00");

        try {
            log.info("결함추적 > 결함단계설정 > receive msgId: {}, 연동 params : {}", msgId, params);

            // 단계 추가 or 수정
            if("GACA7500".equals(msgId)) {
                settingService.saveDeficiencyPhase(objectMapper.convertValue(params.get("phase"), DtDeficiencyPhase.class));
                settingService.saveDeficientySchedule(objectMapper.convertValue(params.get("schedule"), new TypeReference<List<DtDeficientySchedule>>() {}));
            }

            // 단계 삭제
            if("GACA7501".equals(msgId)) {
                deleteDeficiencyPhase(
                        objectMapper.convertValue(params.get("delPhaseList"), new TypeReference<List<String>>() {}),
                        (String)params.get("cntrctNo"),
                        (String)params.get("pjtNo"),
                        false,
                        (String)params.get("usrId"),
                        (Map<String, Object>) params.get("reqVoMap")
                );
            }

            // 단계 순서 변경
            if("GACA7502".equals(msgId)) {
                updateDisplayOrder(
                        objectMapper.convertValue(params.get("moveList"), new TypeReference<List<DisplayOrderMoveInput>>() {}),
                        (String)params.get("usrId"),
                        (String)params.get("pjtNo"),
                        false,
                        (Map<String, Object>) params.get("reqVoMap")
                );
            }

        } catch (GaiaBizException e) {
            log.error("[결함 추적 - 결함단계설정] API receive 중 오류 발생: ", e);
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }

}
