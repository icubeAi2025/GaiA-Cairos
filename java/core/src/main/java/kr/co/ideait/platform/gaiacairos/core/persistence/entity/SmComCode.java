package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Table(name = "sm_com_code", schema = "gaia_cmis")
@Alias("smComCode")
@Data
@EqualsAndHashCode(callSuper = true)
public class SmComCode extends AbstractRudIdTime {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description(name = "SM_공통코드", description = "", type = Description.TYPE.FIELD)
    String cmnCdNo;
    Integer cmnGrpNo;
    String cmnGrpCd;
    String cmnCd;
    String cmnCdNmEng;
    String cmnCdNmKrn;
    Short cmnCdDsplyOrder;
    String cmnCdDscrpt;
    String attrbtCd1;
    String attrbtCd2;
    String attrbtCd3;
    String attrbtCd4;
    String attrbtCd5;
    String useYn;
    String dltYn;

    public static SmComCode ofGroupNo(int cmnGrpNo) {
        SmComCode smComCode = new SmComCode();
        smComCode.setCmnGrpNo(cmnGrpNo);
        return smComCode;
    }

}
