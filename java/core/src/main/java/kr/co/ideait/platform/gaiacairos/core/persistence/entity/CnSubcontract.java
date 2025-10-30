package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CnSubcontractId.class)
public class CnSubcontract extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Id
    @Column(columnDefinition = "NUMERIC")
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
    @Column(columnDefinition = "NUMERIC")
    Long scontrctCntrctAmt;
    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmrk;
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
    @Description(name = "하도급업체주소", description = "", type = Description.TYPE.FIELD)
    String scontrctCorpAdrs; // 업체주소
    @Description(name = "하도급전화번호", description = "", type = Description.TYPE.FIELD)
    String scontrctTelNo; // 전화번호
    @Description(name = "하도급팩스번호", description = "", type = Description.TYPE.FIELD)
    String scontrctFaxNo; // 팩스번호
    @Description(name = "하도급업체대표자", description = "", type = Description.TYPE.FIELD)
    String scontrctCorpCeo; // 업체대표자

}
