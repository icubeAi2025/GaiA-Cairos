package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CwFrontMoneyId.class)
@DynamicUpdate
public class CwFrontMoney extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "선급금순번", description = "", type = Description.TYPE.FIELD)
    Long ppaymnySno;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "기성순번", description = "", type = Description.TYPE.FIELD)
    Long payprceSno;
    
    @Description(name = "대금지급구분", description = "", type = Description.TYPE.FIELD)
    String payType;

    @ColumnDefault("0")
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "지급금액", description = "", type = Description.TYPE.FIELD)
    Long ppaymnyAmt;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "누계지급금액", description = "", type = Description.TYPE.FIELD)
    Long acmtlPpaymnyAmt;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "선급금정산금액", description = "", type = Description.TYPE.FIELD)
    Long ppaymnyCacltAmt;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "선급금잔여금액", description = "", type = Description.TYPE.FIELD)
    Long ppaymnyRemndrAmt;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "지체상금금액", description = "", type = Description.TYPE.FIELD)
    Long dfrcmpnstAmt;

    @Description(name = "발생일자", description = "", type = Description.TYPE.FIELD)
    String ocrnceDate;

    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmrk;

    @Description(name = "승인상태", description = "", type = Description.TYPE.FIELD)
    String apprvlStats;

    @Description(name = "승인요청자ID", description = "", type = Description.TYPE.FIELD)
    String apprvlReqId;

    @Description(name = "승인요청일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlReqDt;

    @Description(name = "전자결재문서 ID", description = "", type = Description.TYPE.FIELD)
    String apDocId;

    @Description(name = "승인자ID", description = "", type = Description.TYPE.FIELD)
    String apprvlId;

    @Description(name = "승인일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlDt;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
