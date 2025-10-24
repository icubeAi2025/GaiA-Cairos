package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewsummary;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.summary.SummaryMybatisParam;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ReviewSummaryFormImpl implements ReviewSummaryForm {

    @Override
    public SummaryMybatisParam.SummaryListInput toSummaryListInput(ReviewsummaryList summaryList) {
        if ( summaryList == null ) {
            return null;
        }

        SummaryMybatisParam.SummaryListInput summaryListInput = new SummaryMybatisParam.SummaryListInput();

        summaryListInput.setCntrctNo( summaryList.getCntrctNo() );

        return summaryListInput;
    }
}
