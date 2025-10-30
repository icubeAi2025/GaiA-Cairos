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
@IdClass(CwDailyReportQdbId.class)
@DynamicUpdate
public class CwDailyReportQdb extends AbstractRudIdTime {

    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "작업보고ID", description = "", type = Description.TYPE.FIELD)
    Long dailyReportId;

    @Id
    @Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
    String cntrctChgId;

    @Id
    @Description(name = "리비전ID", description = "", type = Description.TYPE.FIELD)
    String revisionId;

    @Id
    @Description(name = "ActivityID", description = "", type = Description.TYPE.FIELD)
    String activityId;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "세부공종순번", description = "", type = Description.TYPE.FIELD)
    Long dtlCnsttySn;

    @Description(name = "공종코드", description = "", type = Description.TYPE.FIELD)
    String cnsttyCd;

    @Description(name = "자원유형코드", description = "", type = Description.TYPE.FIELD)
    String rsceTpCd;

    @Description(name = "자원코드", description = "", type = Description.TYPE.FIELD)
    String rsceCd;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "작업수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal workQty;

    @Description(name = "원본자원코드", description = "", type = Description.TYPE.FIELD)
    String orgnlRsceCd;

    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmrk;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
}
