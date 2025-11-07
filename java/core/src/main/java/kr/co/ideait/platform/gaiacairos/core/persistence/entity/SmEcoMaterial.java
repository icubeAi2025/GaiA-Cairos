package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "sm_eco_material", schema = "gaia_cmis")
@Data
@EqualsAndHashCode(callSuper = true)
public class SmEcoMaterial extends AbstractRudIdTime {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String ecoId;
    String cntrctNo;
    String gnrlexpnsCd;
    String rsceNm;
    String specNm;
    String unit;
    String ecoTpCd;
    String preCert;
    String finalCert;
    String makrNm;
    String certRsn;
    String rmrk;
    String dltYn;

}
