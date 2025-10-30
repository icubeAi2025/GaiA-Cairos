package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public interface DisasterDiaryForm {
    /**
     * 재해일지 목록 / 검색
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    class disasterDiaryListParam extends CommonForm {
        String cntrctNo;
        String loginId;
    }

    /**
     * 재해일지 삭제
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    class disasterDiaryDeleteParam {
        String cntrctNo;
        private List<String> deletedDisasIdList;  // 삭제된 재해일지 id 리스트
    }
}
