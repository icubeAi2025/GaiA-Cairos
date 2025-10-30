package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

public interface MainmtrlReqfrmMybatisParam {

    @Data
    @Alias("mainmtrlReqfrmInput")
    public class MainmtrlReqfrmInput {
        String cntrctNo;
        String workcode;
        String resultcode;
        String paymentcode;

        // 검색
        String searchValue;
        String workType;
    }

    @Data
    @Alias("mainmtrlReqfrmOutput")
    public class MainmtrlReqfrmOutput {
        String reqfrmNo;
        String cntrctNo;
        String docNo;
        String cnsttyCd;
        String cnsttyCdNmKrn;   // 공종코드 한글명
        String rxcorpNm;
        String prdnm;
        String makrNm;
        String rmrk;
        Integer atchFileNo;
        String reqId;
        String reqDt;
        String rsltYn;
        String cmId;
        String cmDt;

        String rsltCd;
        String rsltOpnin;
        String apReqId;
        String apReqDt;
        String apDocId;
        String apprvlId;
        String apprvlDt;
        String apprvlStats;
        String apOpnin;
        String dltYn;
        String docId;

        String cnsttyNm;
        String cmNm;
        String rsltNm;
        String apprvlNm;
        String apprvlStatsNm;
        String rsltCdKrn;
        String dsmValid;
    }

    @Data
    @Alias("mainmtrlReqfrmSupervisionOutput")
    public class MainmtrlReqfrmSupervisionOutput {
        String deptNo;
        String deptNm;
        String pstnCd;
        String workNm;
        String cnsttyCd;
        String usrId;
        String usrNm;
        String emailAdrs;
        String cntrctNo;
        String deptId;
        String orgNo;
        String ratngCd;
        String cntrctNm;
    }

    @Data
    @Alias("mainmtrlOutput")
    public class MainmtrlOutput {
        String gnrlexpnsCd;
        String rsceNm;
        String specNm;
        String unit;
        BigDecimal totalQty;
        BigDecimal remainQty;
        BigDecimal todayQty;
        BigDecimal passQty;
        BigDecimal failQty;
        String rmrk;
        BigDecimal useQty;
    }

}
