package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CwPayCostCalculatorId.class)
@DynamicUpdate
public class CwPayCostCalculator extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "기성순번", description = "", type = Description.TYPE.FIELD)
    Long payprceSno;

    @Id
    @Description(name = "원가산출항목코드", description = "", type = Description.TYPE.FIELD)
    String cstCalcItCd;

    @Description(name = "상위원가산출항목코드", description = "", type = Description.TYPE.FIELD)
    String upCstCalcItCd;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "표시순서", description = "", type = Description.TYPE.FIELD)
    Long dsplyOrdr;

    @Description(name = "원가산출항목명", description = "", type = Description.TYPE.FIELD)
    String cstCalcItNm;

    @Description(name = "원가산출방법명", description = "", type = Description.TYPE.FIELD)
    String cstCalcMthdNm;

    @Description(name = "원가산출방법수식", description = "", type = Description.TYPE.FIELD)
    String cstCalcMthdNomfrmCntnts;

    @Description(name = "원가계산서표시값", description = "", type = Description.TYPE.FIELD)
    String cstCalcbllDsplyVal;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "직공비대비백분율", description = "", type = Description.TYPE.FIELD)
    Long drcnstcostCmprPt;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "제경비백분율", description = "", type = Description.TYPE.FIELD)
    Long ovrhdcstPt;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "금회금액", description = "", type = Description.TYPE.FIELD)
    Long thtmCostAm;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "전회누계금액", description = "", type = Description.TYPE.FIELD)
    Long prevCostAm;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "잔여금액", description = "", type = Description.TYPE.FIELD)
    Long remndrAm;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
