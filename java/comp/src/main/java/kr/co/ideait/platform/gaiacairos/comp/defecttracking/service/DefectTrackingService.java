package kr.co.ideait.platform.gaiacairos.comp.defecttracking.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiency;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtDeficiencyActivityRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtDeficiencyRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingMybatisParam.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DefectTrackingService extends AbstractGaiaCairosService {

    @Autowired
    DtDeficiencyRepository deficiencyRepository;

    @Autowired
    DtAttachmentsRepository dtAttachmentsRepository;

    @Autowired
    DtDeficiencyActivityRepository deficiencyActivityRepository;


    /**
     * 결함단계 리스트 조회
     * @param input
     * @return
     */
    public List<Map<String, ?>> getDfccyPhaseList(MybatisInput input) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.getDfccyPhaseList", input);
    }


    /**
     * 결함 리스트 조회
     * @param input
     * @return
     */
    public List<DefectTrackingListOutput> getDfccyList(MybatisInput input) {
        List<DefectTrackingListOutput> output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.getDfccyList", input);

        for (DefectTrackingListOutput item : output) {
            // 결함 첨부파일 조회 및 매핑
            if (item.getAtchFileNo() != null) {
                item.setFiles(dtAttachmentsRepository.findByFileNoAndDltYn(item.getAtchFileNo(), "N"));
            }

            // 답변 첨부파일 조회 및 매핑
            if (item.getRplyAtchNo() != null) {
                item.setReplyFiles(dtAttachmentsRepository.findByFileNoAndDltYn(item.getRplyAtchNo(), "N"));
            }
        }
        return output;
    }


    /**
     * 결함 리스트 count 조회(grid) - 페이징
     * @param input
     * @return
     */
    public Long getDfccyListCount(MybatisInput input) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.getDfccyListCount", input);
    }


    /**
     * 작성자 리스트 조회
     * @param input
     * @return
     */
    public List<RgstrListOutput> getRgstrList(MybatisInput input) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.getRgstrList", input);
    }


    /**
     * 결함 추가
     * @param deficiency
     * @return
     */
    public DtDeficiency saveDeficiency(DtDeficiency deficiency) {
        return deficiencyRepository.save(deficiency);
    }

    public List<DtDeficiency> saveDeficiencyList(List<DtDeficiency> deficiencyList) {
        return deficiencyRepository.saveAll(deficiencyList);
    }


    /**
     * Activity 목록 조회
     * @param params
     * @return
     */
    public List<Map<String, ?>> getActivityList(Map<String, Object> params) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.getActivity", params);
    }


    /**
     * Activity 목록 검색조회
     * @param params
     * @return
     */
    public List<Map<String, ?>> getActivityListSearch(Map<String, Object> params) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.getActivitySearch", params);
    }


    /**
     * Activity저장
     * @param activityList
     * @param dfccyNo
     * @param cntrctNo
     */
    public void createActicity(List<DtDeficiencyActivity> activityList, String dfccyNo, String cntrctNo, String usrId) {
        for(DtDeficiencyActivity activity : activityList){
            activity.setDltYn("N");
            activity.setDfccyNo(dfccyNo);
            activity.setCntrctNo(cntrctNo);
            activity.setRgstrId(usrId);
            if(activity.getRgstDt() == null) {
                activity.setRgstDt(LocalDateTime.now());
            }
        }
        deficiencyActivityRepository.saveAll(activityList);
    }


    /**
     * 결함번호 생성.
     * @return 고유한 결함번호
     */
    public String createDfccyNo() {
        // 1. 현재 날짜 yyyyMMdd 포맷으로 가져오기
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // 2. 오늘 날짜로 시작하는 가장 큰 dfccyNo 조회 (예: 250213XXXX)
        String lastDfccyNo = deficiencyRepository.findMaxDfccyNoByDate(datePrefix + "%");

        // 3. 일련번호 계산 (없으면 0001부터 시작)
        int newSequence = 1; // 기본값

        if (lastDfccyNo != null) {
            String lastSequence = lastDfccyNo.substring(6); // 뒤 4자리 추출
            newSequence = Integer.parseInt(lastSequence) + 1;
        }

        // 4. 4자리 포맷 적용 (ex: 0001, 0012, 1234)
        String sequenceStr = String.format("%04d", newSequence);

        // 5. 최종 결함번호 생성 (ex: 202502130001)
        return datePrefix + sequenceStr;
    }


    /**
     * 결함 수정페이지 - 결함 조회
     * @param cntrctNo
     * @param dfccyPhaseNo
     * @param dfccyNo
     * @param lang
     * @return
     */
    public DfccyUpdateOutPut getDeficiency(String cntrctNo, String dfccyPhaseNo, String dfccyNo, String lang) {
        MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
                .add("dfccyPhaseNo", dfccyPhaseNo)
                .add("dfccyNo", dfccyNo)
                .add("dfccyCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE)
                .add("lang", lang);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.getDeficiency", input);
    }


    /**
     * 결함 수정페이지 - 결함 액티비티 조회
     * @param cntrctNo
     * @param dfccyNo
     * @return
     */
    public List<DtActivityOutput> getDeficiencyActivityList(String cntrctNo, String dfccyNo) {
        MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
                .add("dfccyNo", dfccyNo);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.getDeficiencyActivityList", input);
    }


    /**
     * 결함 수정페이지 - 기존 액티비티 엔터티 조회
     * @param dfccyNo
     * @return
     */
    public List<DtDeficiencyActivity> getDeficiencyActivity(String dfccyNo) {
        return deficiencyActivityRepository.findByDfccyNoAndDltYn(dfccyNo, "N");
    }


    /**
     * Activity 삭제
     * @param oldActivities
     */
    public void deleteActivity(List<DtDeficiencyActivity> oldActivities) {
        deficiencyActivityRepository.deleteAll(oldActivities);
    }


    /**
     * 결함 엔티티 조회
     * @param cntrctNo
     * @param dfccyNo
     * @return
     */
    public DtDeficiency getDeficiency(String cntrctNo, String dfccyNo) {
        return deficiencyRepository.findByCntrctNoAndDfccyNoAndDltYn(cntrctNo, dfccyNo, "N");
    }

    public DtDeficiency getDeficiency(String dfccyNo) {
        return deficiencyRepository.findByDfccyNoAndDltYn(dfccyNo,"N").orElse(null);
    }

    public List<DtDeficiency> getDeficiencyList(String cntrctNo, List<String> dfccyNoList) {
        return deficiencyRepository.findByCntrctNoAndDfccyNoList(cntrctNo, dfccyNoList);
    }


    /**
     * 결함 삭제
     * @param deficiency
     */
    public void deleteDeficiency(DtDeficiency deficiency) {
        deficiencyRepository.updateDelete(deficiency);
    }


    /**
     * 답변관리 - 답변 확인 -> 답변 여부 update
     * @param dfccyNo
     */
    public void updateRplyYn(String dfccyNo) {
        MybatisInput input = MybatisInput.of().add("dfccyNo", dfccyNo);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.updateRplyYn", input);
    }


    /**
     * 결함 확인 리스트 조회
     * @param dfccyNo
     * @return
     */
    public List<DtConfirmOutput> getDeficiencyConfirmList(String dfccyNo) {
        List<DtConfirmOutput> confirmList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.getDfccyConfirmList", dfccyNo);

        for (DtConfirmOutput cnfrmItem : confirmList){
            if (cnfrmItem.getAtchFileNo() != null) {
                List<DtAttachments> files = dtAttachmentsRepository.findByFileNoAndDltYn(cnfrmItem.getAtchFileNo(), "N");
                cnfrmItem.setConfirmFiles(files);
            }
        }

        return confirmList;
    }

}
