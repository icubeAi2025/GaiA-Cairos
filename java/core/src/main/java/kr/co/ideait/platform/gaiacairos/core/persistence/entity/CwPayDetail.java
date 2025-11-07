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
@IdClass(CwPayDetailId.class)
@DynamicUpdate
public class CwPayDetail extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "기성순번", description = "", type = Description.TYPE.FIELD)
    Long payprceSno;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "세부공종순번", description = "", type = Description.TYPE.FIELD)
    Long dtlCnsttySn;

    @Description(name = "자원유형코드", description = "", type = Description.TYPE.FIELD)
    String rsceTpCd;

    @Description(name = "자원코드", description = "", type = Description.TYPE.FIELD)
    String rsceCd;

    @Description(name = "세부공종명", description = "", type = Description.TYPE.FIELD)
    String dtlCnsttyNm;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "금회기성수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal thtmAcomQty;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "금회기성금액", description = "", type = Description.TYPE.FIELD)
    Long thtmAcomAmt;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "전회누계수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal  prevAcmtlQty;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "전회누계금액", description = "", type = Description.TYPE.FIELD)
    Long prevAcmtlAmt;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
