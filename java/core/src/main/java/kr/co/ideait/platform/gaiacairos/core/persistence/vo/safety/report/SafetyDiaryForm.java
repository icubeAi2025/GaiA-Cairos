package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

public interface SafetyDiaryForm {
    /*
     * 안전일지 목록 / 검색
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public class safetyDiaryParam extends CommonForm {
        String cntrctNo;
        String startDate;
        String endDate;
        String loginId;
        String apprvlStats;

        // String searchType;
        // String searchText; CommonForm에 있음
    }
}
