package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

public interface ReviewsummaryMybatisParam {

    @Data
    @Alias("reviewsummaryListInput")
    public class ReviewsummaryListInput extends MybatisPageable {
        String cntrctNo;
    }

    @Data
    @Alias("reviewsummaryListOutput")
    public class ReviewsummaryListOutput {
        String cnsttyNm;
        String rgstrNm;
        String rgstrId;

        String dsgnCd;
        String dsgnCnt;
        String dsgnPer;
        String replyAgreeCnt;
        String replyDisagreeCnt;
        String replyCheckCnt;
        String replyInfoCnt;
        String replyTotalCnt;
        String apPendingCnt;
        String apOnholdCnt;
        String backClosedCnt1;
        String backClosedCnt2;
    }

    @Data
    @Alias("reviewsummaryRgstrListOutput")
    public class ReviewsummaryRgstrListOutput {
        String usrId;
        String usrNm;
    }

}
