package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewsummary;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.summary.SummaryMybatisParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface ReviewSummaryForm {

    // 검토요약 조회
    SummaryMybatisParam.SummaryListInput toSummaryListInput(ReviewsummaryList summaryList);

    // 검토요약 목록, 검색
    @Data
    @EqualsAndHashCode(callSuper = false)
    class ReviewsummaryList {
        String cntrctNo;
        String summaryType;
        List<String> dsgnPhaseNoList; // 설계단계
        List<String> rgstrIdList; // 검토자
    }

}
