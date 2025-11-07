package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CwPayMngId.class)
@DynamicUpdate
public class CwPayMng extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "기성순번", description = "", type = Description.TYPE.FIELD)
    Long payprceSno;

    @Description(name = "계약변경 ID", description = "", type = Description.TYPE.FIELD)
    String cntrctChgId;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "기성회차", description = "", type = Description.TYPE.FIELD)
    Long payprceTmnum;

    @Description(name = "기성년월", description = "", type = Description.TYPE.FIELD)
    String payprceYm;

    @Comment("전회누계금액")
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "전회누계금액", description = "", type = Description.TYPE.FIELD)
    Long prevAcmtlAmt;

    @Comment("금회기성금액")
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "금회기성금액", description = "", type = Description.TYPE.FIELD)
    Long thtmAcomAmt;

    @Comment("잔여금액")
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "잔여금액", description = "", type = Description.TYPE.FIELD)
    Long remndrAmt;

    @Comment("선급금 공제")
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "선급금정산금액", description = "", type = Description.TYPE.FIELD)
    Long ppaymnyCacltAmt;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "지체상금금액", description = "", type = Description.TYPE.FIELD)
    Long dfrcmpnstAmt;

    @Comment("노무비 공제")
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "유보금액", description = "", type = Description.TYPE.FIELD)
    Long rsrvAmt;

    @Comment("금회 지급금액")
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "금회지급금액", description = "", type = Description.TYPE.FIELD)
    Long thtmPaymntAmt;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "누계기성율", description = "", type = Description.TYPE.FIELD)
    Long acmtlAcomRate;

    @Description(name = "기성신청일", description = "", type = Description.TYPE.FIELD)
    String payApprvlDate;

    @Description(name = "검사일", description = "", type = Description.TYPE.FIELD)
    String inspctDate;

    @Description(name = "대금지급일", description = "", type = Description.TYPE.FIELD)
    String paymntDate;

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

    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmrk;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    @Description(name = "유레카전송여부", description = "", type = Description.TYPE.FIELD)
    String eurecaSendYn;
}
