package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("smAuthorityGroup")
public class SmAuthorityGroup extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer rghtGrpNo;
    String rghtGrpCd;
    String pjtNo;
    String cntrctNo;
    String pjtType;
    String rghtGrpNmEng;
    String rghtGrpNmKrn;
    String rghtGrpDscrpt;
    String rghtGrpTy;
    String rghtGrpRole;
    String useYn;
    String dltYn;
}
