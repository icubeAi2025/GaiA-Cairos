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
@IdClass(CnSubcontractChangeId.class)
public class CnSubcontractChange extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "하도급업체ID", description = "", type = Description.TYPE.FIELD)
    Long scontrctCorpId;
    @Id
    @Column(columnDefinition = "NUMERIC")
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
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "공사기간", description = "", type = Description.TYPE.FIELD)
    Long chgConPrd;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "변경계약금액", description = "", type = Description.TYPE.FIELD)
    Long cntrctAmt;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "지체상금율", description = "", type = Description.TYPE.FIELD)
    Long dfrcmpnstRate;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "부가세율", description = "", type = Description.TYPE.FIELD)
    Long vatRate;
    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmrk;
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
