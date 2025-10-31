package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.summary;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

public interface SummaryMybatisParam {

    @Data
    @Alias("summaryListInput")
    public class SummaryListInput extends MybatisPageable {
        String cntrctNo;
    }

    @Data
    @Alias("summaryListOutput")
    public class SummaryListOutput {
        String cnsttyNm;
        String rgstrNm;

        String dfccyCd;
        String rgstrId;

        String dfccyCnt;
        String dfccyPer;
        String replyAgreeCnt;
        String replyDisagreeCnt;
        String replyCheckCnt;
        String replyDoneCnt;
        String qaPendingCnt;
        String qaOnHoldCnt;
        String qaClosedCnt;
        String spvsPendingCnt;
        String spvsOnHoldCnt;
        String spvsClosedCnt;
        String edPendingCnt;
        String edClosedCnt;
    }

    @Data
    @Alias("summaryRgstrListOutput")
    public class SummaryRgstrListOutput {
        String usrId;
        String usrNm;
    }

}
