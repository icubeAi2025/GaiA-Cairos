package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface SubcontractMybatisParam {

    // -----------------------계약목록--------------------
    @Data
    @Alias("contractListInput")
    public class ContractListInput {
        String pjtNo;
    }

    @Data
    @Alias("contractListOutput")
    public class ContractListOutput {

        String pjtNo;
        String cntrctNo;
        String cntrctNm;
        String majorCnsttyCd;
        String corpNm;

    }

    // -----------------하도급목록--------------------

    @Data
    @Alias("subcontractListInput")
    public class SubcontractListInput {
        String cntrctNo;

        String cmnGrpCdIndstryty;
        String cmnGrpCdWorkType;

    }

    @Data
    @Alias("subcontractListOutput")
    public class SubcontractListOutput {

        String cntrctNo;
        Long scontrctCorpId;
        String scontrctCntrctNo;
        String scontrctCntrctNm;
        String gcontrctCorpNo;
        String scontrctCorpNo;
        String scontrctCorpBsnsmnNo;
        String scontrctCorpNm;
        String scontrctCcnsttyCd;
        String scontrctIndstrytyCd;
        String cntrctDate;
        String cntrctBgnDate;
        String cntrctEndDate;
        Long scontrctCntrctAmt;
        String rmrk;

        String scontrctCnsttyCdKrn;
        String scontrctIndstrytyCdKrn;
        String cntrctNm;

        String bsnsmnNo;
        String corpNm;
        String cntrctChgNo;
        String cnsttyCdKrn;
    }

    // -----------------하도급 개별 조회--------------------
    @Data
    @Alias("subcontractInput")
    public class SubcontractInput {
        String cntrctNo;
        Long scontrctCorpId;

        String cmnGrpCdIndstryty;
        String cmnGrpCdWorkType;

        Long scontrctCntrctAmt;
        Long cntrctChgId;
        String updateType;
    }

    @Data
    @Alias("subcontractOutput")
    public class SubcontractOutput {

        String cntrctNo;
        Long scontrctCorpId;
        String scontrctCntrctNo;
        String scontrctCntrctNm;
        String gcontrctCorpNo;
        String scontrctCorpNo;
        String scontrctCorpBsnsmnNo;
        String scontrctCorpNm;
        String scontrctCorpAdrs;
        String scontrctTelNo;
        String scontrctFaxNo;
        String scontrctCorpCeo;
        String scontrctCcnsttyCd;
        String scontrctIndstrytyCd;
        String cntrctDate;
        String cntrctBgnDate;
        String cntrctEndDate;
        Long scontrctCntrctAmt;
        String rmrk;

        String bsnsmnNo;
        String corpNm;

        String scontrctCcnsttyCdNm;
        String scontrctIndstrytyNm;
        String cntrctNm;
        String majorCnsttyCd;
        Long preCntrctAmt;
        String cntrctChgNo;

    }
    // -----------------하도급계약변경 목록--------------------

    @Data
    @Alias("subcontractChangeListInput")
    public class SubcontractChangeListInput {
        String cntrctNo;
        Long scontrctCorpId;
        String cmnGrpCdCntrctChgType;
    }

    @Data
    @Alias("subcontractChangeListOutput")
    public class SubcontractChangeListOutput {

        String cntrctNo;
        Long scontrctCorpId;
        Long cntrctChgId;
        String cntrctChgNo;
        String cntrctChgType;
        String chgApprDate;
        String cntrctChgDate;
        String chgCbgnDate;
        Long chgConPrd;
        Long cntrctAmt;
        Long dfrcmpnstRate;
        Long vatRate;
        String rmrk;
        String dltYn;
        String cntrctChgTypeKrn;
        String cntrctBgnDate;
    }

    // -----------------하도급계약변경 개별 조회--------------------

    @Data
    @Alias("subcontractChangeInput")
    public class SubcontractChangeInput {
        String cntrctNo;
        Long scontrctCorpId;
        Long cntrctChgId;
        String cmnGrpCdWorkType;
        String cmnGrpCdIndstryty;
        String cmnGrpCdCntrctChgType;
    }

    @Data
    @Alias("subcontractChangeOutput")
    public class SubcontractChangeOutput {

        String cntrctNo;
        Long scontrctCorpId;
        Long cntrctChgId;
        String cntrctChgNo;
        String cntrctChgType;
        String chgApprDate;
        String cntrctChgDate;
        String chgCbgnDate;
        Long chgConPrd;
        Long cntrctAmt;
        Long dfrcmpnstRate;
        Long vatRate;
        String rmrk;
        String dltYn;

        String cntrctChgTypeNm;

        String bsnsmnNo;
        String corpNm;

        String scontrctCntrctNo;
        String gcontrctCorpNo;
        String scontrctCorpNm;
        String scontrctCorpBsnsmnNo;
        String cntrctBgnDate;
        String scontrctCcnsttyCd;
        String scontrctCcnsttyCdNm;
        String scontrctIndstrytyCd;
        String scontrctIndstrytyCdNm;
        String cmnCdNmKrn;
        Long preCntrctAmt;
        Long fstCntrctAmt;
        String cntrctNm;

    }
}
