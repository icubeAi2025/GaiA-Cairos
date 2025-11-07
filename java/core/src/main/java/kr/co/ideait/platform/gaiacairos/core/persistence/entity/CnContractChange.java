package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CnContractChangeId.class)
public class CnContractChange extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Description(name = "계약변경 ID", description = "", type = Description.TYPE.FIELD)
    String cntrctChgId; // 계약변경ID

    @Description(name = "계약변경차수", description = "", type = Description.TYPE.FIELD)
    String cntrctChgNo; // 계약변경차수(계약 변경시 자동증가)

    @Description(name = "계약변경구분", description = "", type = Description.TYPE.FIELD)
    String cntrctChgType; // 계약변경구분

    @Description(name = "변경승인일자", description = "", type = Description.TYPE.FIELD)
    String chgApprDate; // 변경승인일자

    @Description(name = "계약변경일자", description = "", type = Description.TYPE.FIELD)
    String cntrctChgDate; // 계약변경일자

    @Description(name = "준공일자", description = "", type = Description.TYPE.FIELD)
    String chgCbgnDate; // 준공일자

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "공사기간", description = "", type = Description.TYPE.FIELD)
    Double chgConPrd; // 공사기간

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "변경계약금액", description = "", type = Description.TYPE.FIELD)
    Double cntrctAmt; // 변경계약금액

    @Description(name = "변경금차준공일자", description = "", type = Description.TYPE.FIELD)
    String chgThisCbgnDate; // 변경금차준공일자

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "변경금차공사기간", description = "", type = Description.TYPE.FIELD)
    Double chgThisConPrd; // 변경금차공사기간

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "변경금차계약금액", description = "", type = Description.TYPE.FIELD)
    Double thisCntrctAmt; // 변경 금차 계약 금액

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "지체상금율", description = "", type = Description.TYPE.FIELD)
    Double dfrcmpnstRate; // 지체상금율

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "부가세율", description = "", type = Description.TYPE.FIELD)
    Double vatRate; // 부가세율

    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmrk; // 비고

    @Description(name = "최종변경여부", description = "", type = Description.TYPE.FIELD)
    String lastChgYn; // 최종변경여부

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "계약 차수", description = "", type = Description.TYPE.FIELD)
    Integer cntrctPhase; // 계약 차수

    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate() {
        if (this.chgConPrd == null) {
            this.chgConPrd = 0.0;
        }
        if (this.cntrctAmt == null) {
            this.cntrctAmt = 0.0;
        }
        if (this.dfrcmpnstRate == null) {
            this.dfrcmpnstRate = 0.0;
        }
        if (this.vatRate == null) {
            this.vatRate = 0.0;
        }
    }
}
