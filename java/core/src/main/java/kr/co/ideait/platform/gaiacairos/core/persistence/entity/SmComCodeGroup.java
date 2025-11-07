package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Table(name = "sm_com_code_group", schema = "gaia_cmis")
@Data
@EqualsAndHashCode(callSuper = true)
public class SmComCodeGroup extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer cmnGrpNo;
    String cmnGrpCd;
    String cmnCd;
    String cmnCdNmEng;
    String cmnCdNmKrn;
    Short cmnCdDsplyOrdr;
    String cmnCdDscrpt;
    Integer upCmnGrpNo;
    String upCmnGrpCd;
    String publicYn;
    Short cmnLevel;
    String useYn;
    String dltYn;

}
