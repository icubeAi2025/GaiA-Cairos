package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report;

import lombok.Data;

import java.util.List;

/**
 * 안전관리 > 안전 일지 요청 사용
 */
@Data
public class DisasterDiaryRequest {
    private DisasterDiary disasterDiary;      // 본문
    private List<Integer> deletedDisasterSeqList;    // 삭제된 재해인원 seq 정보
    private List<DisasterDiaryPersonnel> disasterUserList; // 재해인원 정보

}
