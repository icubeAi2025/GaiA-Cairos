package kr.co.ideait.platform.gaiacairos.comp.safety;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ContractstatusService;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.DisasterDiaryService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContract;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report.DisasterDiaryForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report.DisasterDiaryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class DisasterDiaryComponent extends AbstractComponent {

    @Autowired
    DisasterDiaryService disasterDiaryService;

    @Autowired
    ContractstatusService contractService;

    /**
     * 재해일지 목록 조회
     * @param commonReqVo
     * @param param
     * @return
     */
    public Page<Map<String, Object>> getDisasterDiaryList(CommonReqVo commonReqVo, DisasterDiaryForm.disasterDiaryListParam param) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> paramMap = mapper.convertValue(param, Map.class);
        paramMap.put("pageable", param.getPageable());

        List<Map<String, Object>> list = disasterDiaryService.getDisasterDiaryList(paramMap);

        Long totalCnt = 0L;
        if(list.size() > 0) {
            totalCnt = (Long) list.getFirst().get("total_count");
        }

        return new PageImpl<>(list, param.getPageable(), totalCnt);
    }

    /**
     * 재해일지 추가
     * @param report
     * @param commonReqVo
     */
    @Transactional
    public void addDisasterDiary(DisasterDiaryRequest report, CommonReqVo commonReqVo) {
        String usrId = commonReqVo.getUserId();
        String disasterDiaryId = UUID.randomUUID().toString();
        String cntrctNo = report.getDisasterDiary().getCntrctNo();

        Map<String, Object> disasterDiary = EtcUtil.convertObjectToMap(report.getDisasterDiary());
        List<Map<String, Object>> disasterDiaryUserList = EtcUtil.convertListToMapList(report.getDisasterUserList());

        // 1. 재해일지 생성
        disasterDiary.put("disasId", disasterDiaryId);
        disasterDiary.put("rgstrId", usrId);
        disasterDiaryService.addDisasterDiary(disasterDiary);

        // 2. 재해일지 인원 생성
        for (Map<String, Object> disasterDiaryUser : disasterDiaryUserList) {
            // 재해일련번호 SET
            disasterDiaryUser.put("disasId", disasterDiaryId);
            disasterDiaryUser.put("usrId", usrId);
            disasterDiaryService.addDisasterDiaryPersonnel(disasterDiaryUser);
        }

        // 3. 계약 안전사고일 수정
        this.updateSftyAcdntDate(cntrctNo);

    }


    /**
     * 재해일지 상세 조회
     * @param cntrctNo
     * @param diaryId
     * @return
     */
    public Map<String, Object> getDisasterDiary(String cntrctNo, String diaryId) {
        Map<String, Object> input = Map.of("cntrctNo", cntrctNo, "disasId", diaryId);

        //1. 재해일지 - 본문
        Map<String, Object> disasterDiary = disasterDiaryService.getDisasterDiary(input);

        //2. 재해일지 - 재해인원
        List<Map<String, Object>> disasterUserList = disasterDiaryService.getListDisasterDiaryPersonnel(input);

        return Map.of(
                "disasterDiary", disasterDiary,
                "disasterUserList", disasterUserList
        );
    }

    /**
     * 재해일지 수정
     * @param report
     * @param commonReqVo
     */
    @Transactional
    public void modifyDisasterDiary(DisasterDiaryRequest report, CommonReqVo commonReqVo) {
        String usrId = commonReqVo.getUserId();
        String disasterDiaryId = report.getDisasterDiary().getDisasId();
        String cntrctNo = report.getDisasterDiary().getCntrctNo();

        Map<String, Object> disasterDiary = EtcUtil.convertObjectToMap(report.getDisasterDiary());
        List<Map<String, Object>> disasterDiaryUserList = EtcUtil.convertListToMapList(report.getDisasterUserList());
        List<Integer> deletedDisasterUserSeqList= report.getDeletedDisasterSeqList();

        // 1. 재해일지 수정
        disasterDiary.put("usrId", usrId);
        disasterDiaryService.updateDisasterDiary(disasterDiary);

        // 2. 재해인원 수정
        // 2-1. 추가된 인원 생성 및 수정된 정보 적용
        for (Map<String, Object> disasterDiaryUser : disasterDiaryUserList) {
            if(disasterDiaryUser.get("disasVicSeq") != null){
                // 수정
                disasterDiaryUser.put("disasId", disasterDiaryId);
                disasterDiaryUser.put("usrId", usrId);
                disasterDiaryService.updateDisasterDiaryPersonnel(disasterDiaryUser);
            }
            else{
                // 추가
                disasterDiaryUser.put("disasId", disasterDiaryId);
                disasterDiaryUser.put("usrId", usrId);
                disasterDiaryService.addDisasterDiaryPersonnel(disasterDiaryUser);
            }
        }

        // 2-2. 삭제된 인원 삭제 처리
        if(!deletedDisasterUserSeqList.isEmpty()){
           Map<String, Object> input = Map.of(
                   "deleteDisasterSeqList", deletedDisasterUserSeqList,
                   "usrId", usrId,
                   "disasId", disasterDiaryId
           );

           disasterDiaryService.deleteDisasterDiaryPersonnel(input);
        }

        // 3. 계약 안전사고일 수정
        this.updateSftyAcdntDate(cntrctNo);
    }

    /**
     * 재해일지 삭제 - 다건
     * @param params
     * @param commonReqVo
     */
    @Transactional
    public void deleteDisasterDiaryList(DisasterDiaryForm.disasterDiaryDeleteParam params, CommonReqVo commonReqVo) {
        String cntrctNo = params.getCntrctNo();
        List<String> deletedDiaryIdList = params.getDeletedDisasIdList();

        for (String deletedDiaryId : deletedDiaryIdList) {

            this.deleteDisasterDiary(deletedDiaryId, cntrctNo, commonReqVo);
        }

        // 계약 안전사고일 수정
        this.updateSftyAcdntDate(cntrctNo);

    }

    /**
     * 재해일지 삭제 - 단건
     * @param disasId
     * @param cntrctNo
     * @param commonReqVo
     */
    public void deleteDisasterDiary(String disasId, String cntrctNo, CommonReqVo commonReqVo) {
        String usrId = commonReqVo.getUserId();

        Map<String, Object> input = Map.of(
                "disasId", disasId,
                "usrId", usrId
        );

        disasterDiaryService.deleteDisasterDiary(input);
        disasterDiaryService.deleteDisasterDiaryPersonnel(input);

        // 계약 안전사고일 수정
        this.updateSftyAcdntDate(cntrctNo);
    }

    /**
     * 계약의 안전사고일자 변경 처리
     * @param cntrctNo
     */
    private void updateSftyAcdntDate(String cntrctNo) {
        // 3-1. 가장 최근 재해일자 조회
        LocalDateTime recentlyDisasterDate = disasterDiaryService.getRecentlyDisasterDateByCntrctNo(cntrctNo);

        // 3-2. 계약의 안전사고일자 조회
        CnContract cnContract = contractService.getByCntrctNo(cntrctNo);
        if(cnContract == null) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "계약정보가 존재하지 않습니다.");
        }
        LocalDateTime sftyAcdntDt = cnContract.getSftyAcdntDt();

        // 비교 후, 계약의 안전사고일자 변경.
        // 3-3. 동기화: 값이 다르면 갱신 (null 포함 비교)
        if (!java.util.Objects.equals(sftyAcdntDt, recentlyDisasterDate)) {
            contractService.updateSftyAcdntDt(cntrctNo, recentlyDisasterDate);
        }
    }

}
