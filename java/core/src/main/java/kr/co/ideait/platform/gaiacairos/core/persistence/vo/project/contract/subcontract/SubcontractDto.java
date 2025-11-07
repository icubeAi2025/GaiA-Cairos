package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontractChange;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface SubcontractDto {
    // 목록 조회용
    Subcontract fromCnSubcontractOutput(SubcontractMybatisParam.SubcontractListOutput subcontractListOutput);

    // 수정용
    Subcontract fromCnSubcontract(CnSubcontract cnSubcontract);

    @Data
    public class Subcontract {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;
        @Description(name = "하도급업체ID", description = "", type = Description.TYPE.FIELD)
        Long scontrctCorpId;
        @Description(name = "하도급계약번호", description = "", type = Description.TYPE.FIELD)
        String scontrctCntrctNo;
        @Description(name = "하도급계약명", description = "", type = Description.TYPE.FIELD)
        String scontrctCntrctNm;
        @Description(name = "원도급업체번호", description = "", type = Description.TYPE.FIELD)
        String gcontrctCorpNo;
        @Description(name = "하도급업체번호", description = "", type = Description.TYPE.FIELD)
        String scontrctCorpNo;
        @Description(name = "하도급업체사업자번호", description = "", type = Description.TYPE.FIELD)
        String scontrctCorpBsnsmnNo;
        @Description(name = "하도급업체명", description = "", type = Description.TYPE.FIELD)
        String scontrctCorpNm;
        @Description(name = "하도급공종코드", description = "", type = Description.TYPE.FIELD)
        String scontrctCcnsttyCd;
        @Description(name = "하도급업종코드", description = "", type = Description.TYPE.FIELD)
        String scontrctIndstrytyCd;
        @Description(name = "계약일자", description = "", type = Description.TYPE.FIELD)
        String cntrctDate;
        @Description(name = "계약시작일자", description = "", type = Description.TYPE.FIELD)
        String cntrctBgnDate;
        @Description(name = "계약종료일자", description = "", type = Description.TYPE.FIELD)
        String cntrctEndDate;
        @Description(name = "하도급계약금액", description = "", type = Description.TYPE.FIELD)
        Long scontrctCntrctAmt;
        @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
        String rmrk;

        @Description(name = "사업자번호", description = "", type = Description.TYPE.FIELD)
        String bsnsmnNo;
        @Description(name = "업체명", description = "", type = Description.TYPE.FIELD)
        String corpNm;

        @Description(name = "하도급공종코드 한글명", description = "", type = Description.TYPE.FIELD)
        String scontrctCcnsttyCdKrn;
        @Description(name = "하도급업종코드 한글명", description = "", type = Description.TYPE.FIELD)
        String scontrctIndstrytyCdKrn;
        @Description(name = "계약명", description = "", type = Description.TYPE.FIELD)
        String cntrctNm;
        @Description(name = "공종코드 한글명", description = "", type = Description.TYPE.FIELD)
        String cnsttyCdKrn;
        @Description(name = "이전계약금액", description = "", type = Description.TYPE.FIELD)
        Long preCntrctAmt;
        @Description(name = "계약변경차수", description = "", type = Description.TYPE.FIELD)
        String cntrctChgNo;
    }

    SubcontractChange fromCnSubcontractChangeOutput(SubcontractMybatisParam.SubcontractChangeListOutput subcontractChangeListOutput);

    // 수정용
    SubcontractChange fromCnSubcontractChange(CnSubcontractChange cnSubcontractChange);

    @Data
    public class SubcontractChange {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;
        @Description(name = "하도급업체ID", description = "", type = Description.TYPE.FIELD)
        Long scontrctCorpId;
        @Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
        Long cntrctChgId;
        @Description(name = "계약변경차수", description = "", type = Description.TYPE.FIELD)
        String cntrctChgNo;
        @Description(name = "계약변경구분", description = "", type = Description.TYPE.FIELD)
        String cntrctChgType;
        @Description(name = "계약변경승인일자", description = "", type = Description.TYPE.FIELD)
        String chgApprDate;
        @Description(name = "계약변경일자", description = "", type = Description.TYPE.FIELD)
        String cntrctChgDate;
        @Description(name = "준공일자", description = "", type = Description.TYPE.FIELD)
        String chgCbgnDate;
        @Description(name = "공사기간", description = "", type = Description.TYPE.FIELD)
        Long chgConPrd;
        @Description(name = "변경계약금액", description = "", type = Description.TYPE.FIELD)
        Long cntrctAmt;
        @Description(name = "지체상금율", description = "", type = Description.TYPE.FIELD)
        Long dfrcmpnstRate;
        @Description(name = "부가세율", description = "", type = Description.TYPE.FIELD)
        Long vatRate;
        @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
        String rmrk;
        @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
        String dltYn;

        @Description(name = "사업자번호", description = "", type = Description.TYPE.FIELD)
        String bsnsmnNo;
        @Description(name = "업체명", description = "", type = Description.TYPE.FIELD)
        String corpNm;
        @Description(name = "하도급계약번호", description = "", type = Description.TYPE.FIELD)
        String scontrctCntrctNo;
        @Description(name = "원도급업체번호", description = "", type = Description.TYPE.FIELD)
        String gcontrctCorpNo;
        @Description(name = "히도급업체명", description = "", type = Description.TYPE.FIELD)
        String scontrctCorpNm;
        @Description(name = "하도급업체 사업자번호", description = "", type = Description.TYPE.FIELD)
        String scontrctCorpBsnsmnNo;
        @Description(name = "계약시작일자", description = "", type = Description.TYPE.FIELD)
        String cntrctBgnDate;
        @Description(name = "하도급공종코드", description = "", type = Description.TYPE.FIELD)
        String scontrctCcnsttyCd;
        @Description(name = "하도급공종코드 한글명", description = "", type = Description.TYPE.FIELD)
        String scontrctCcnsttyCdKrn;
        @Description(name = "하도급업종코드", description = "", type = Description.TYPE.FIELD)
        String scontrctIndstrytyCd;
        @Description(name = "하도급공종코드 한글명", description = "", type = Description.TYPE.FIELD)
        String scontrctIndstrytyCdKrn;
        @Description(name = "계약변경구분 한글명", description = "", type = Description.TYPE.FIELD)
        String cntrctChgTypeKrn;
        @Description(name = "", description = "", type = Description.TYPE.FIELD)
        String cmnCdNmKrn;
        @Description(name = "이번차수 볁경 계약 금액액", description = "", type = Description.TYPE.FIELD)
        Long preCntrctAmt;
        @Description(name = "최초계약금액", description = "", type = Description.TYPE.FIELD)
        Long fstCntrctAmt;
        @Description(name = "계약명", description = "", type = Description.TYPE.FIELD)
        String cntrctNm;
    }
}
