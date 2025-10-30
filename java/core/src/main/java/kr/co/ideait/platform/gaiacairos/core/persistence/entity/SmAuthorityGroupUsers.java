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
@Alias("smAuthorityGroupUsers")
public class SmAuthorityGroupUsers extends AbstractRIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer rghtGrpUsrNo;
    Integer rghtGrpNo;
    String rghtGrpCd;
    String rghtGrpUsrTy;
    Integer authNo;
}
