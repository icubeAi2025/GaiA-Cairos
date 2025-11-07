package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CwDailyReportResourceId.class)
@DynamicUpdate
public class CwDailyReportResource extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "작업보고ID", description = "", type = Description.TYPE.FIELD)
    Long dailyReportId;

    @Description(name = "자원유형코드", description = "", type = Description.TYPE.FIELD)
    String rsceTpCd;

    @Id
    @Description(name = "자원순번", description = "", type = Description.TYPE.FIELD)
    Integer rsceSno;

    @Description(name = "자원코드", description = "", type = Description.TYPE.FIELD)
    String rsceCd;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "총수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal totalQty;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "계획수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal planQty;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "실적수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal actualQty;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "누계수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal acmtlQty;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "잔여수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal remndrQty;

    @Description(name = "관급자재여부", description = "", type = Description.TYPE.FIELD)
    String govsplyMtrlYn;

    @Description(name = "주자재표시", description = "", type = Description.TYPE.FIELD)
    String mainRsceDsply;

    @Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
    String cntrctChgId;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    @Description(name = "수동추가여부", description = "", type = Description.TYPE.FIELD)
    String manualYn;
}
