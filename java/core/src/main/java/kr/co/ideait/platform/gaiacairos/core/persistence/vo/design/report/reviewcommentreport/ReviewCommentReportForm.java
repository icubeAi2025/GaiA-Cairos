package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface ReviewCommentReportForm {
    // 설계 검색 조건
    ReviewCommentReportMybatisParam.ReviewReportSearchInput toReviewReportSearchInput(ReviewReportList reviewReportList);

    // 검토보고서 목록,검색
    @Data
    @EqualsAndHashCode(callSuper = false)
    class ReviewReportList extends CommonForm {
        String cntrctNo;

        // 상세 검색 조건
        String dsgnCd;
        String rgstrNm;
        String myRplyYn;
        Long startDsgnNo;
        Long endDsgnNo;
        String startRecentDt;
        String endRecentDt;
        String isuYn;
        String lesnYn;
        String atachYn;
        String rplyStatus;
        String apprerCd;
        String backchkCd;

        // 답변 일반 검색
        String keyword;
        List<String> dsgnPhaseNoList; // 설계단계
        List<String> rgstrIdList; // 검토자
        List<String> dsgnCdList; // 검토분류
    }

     // 검토보고서 목록,검색
     @Data
     @EqualsAndHashCode(callSuper = false)
     class ReviewReportDetail extends CommonForm {
         String cntrctNo;
         String dsgnNo;
     }

}
