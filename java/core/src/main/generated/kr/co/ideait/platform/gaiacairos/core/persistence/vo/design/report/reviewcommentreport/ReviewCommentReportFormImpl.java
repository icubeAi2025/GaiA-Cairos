package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ReviewCommentReportFormImpl implements ReviewCommentReportForm {

    @Override
    public ReviewCommentReportMybatisParam.ReviewReportSearchInput toReviewReportSearchInput(ReviewReportList reviewReportList) {
        if ( reviewReportList == null ) {
            return null;
        }

        ReviewCommentReportMybatisParam.ReviewReportSearchInput reviewReportSearchInput = new ReviewCommentReportMybatisParam.ReviewReportSearchInput();

        reviewReportSearchInput.setCntrctNo( reviewReportList.getCntrctNo() );
        reviewReportSearchInput.setDsgnCd( reviewReportList.getDsgnCd() );
        reviewReportSearchInput.setRgstrNm( reviewReportList.getRgstrNm() );
        reviewReportSearchInput.setMyRplyYn( reviewReportList.getMyRplyYn() );
        reviewReportSearchInput.setStartDsgnNo( reviewReportList.getStartDsgnNo() );
        reviewReportSearchInput.setEndDsgnNo( reviewReportList.getEndDsgnNo() );
        reviewReportSearchInput.setStartRecentDt( reviewReportList.getStartRecentDt() );
        reviewReportSearchInput.setEndRecentDt( reviewReportList.getEndRecentDt() );
        reviewReportSearchInput.setIsuYn( reviewReportList.getIsuYn() );
        reviewReportSearchInput.setLesnYn( reviewReportList.getLesnYn() );
        reviewReportSearchInput.setAtachYn( reviewReportList.getAtachYn() );
        reviewReportSearchInput.setRplyStatus( reviewReportList.getRplyStatus() );
        reviewReportSearchInput.setApprerCd( reviewReportList.getApprerCd() );
        reviewReportSearchInput.setBackchkCd( reviewReportList.getBackchkCd() );
        reviewReportSearchInput.setKeyword( reviewReportList.getKeyword() );
        List<String> list = reviewReportList.getDsgnPhaseNoList();
        if ( list != null ) {
            reviewReportSearchInput.setDsgnPhaseNoList( new ArrayList<String>( list ) );
        }
        List<String> list1 = reviewReportList.getRgstrIdList();
        if ( list1 != null ) {
            reviewReportSearchInput.setRgstrIdList( new ArrayList<String>( list1 ) );
        }
        List<String> list2 = reviewReportList.getDsgnCdList();
        if ( list2 != null ) {
            reviewReportSearchInput.setDsgnCdList( new ArrayList<String>( list2 ) );
        }

        return reviewReportSearchInput;
    }
}
