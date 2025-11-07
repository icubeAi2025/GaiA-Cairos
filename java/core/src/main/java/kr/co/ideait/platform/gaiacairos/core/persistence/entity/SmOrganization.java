package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("smOrganization")
public class SmOrganization extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer orgNo;
    Integer deptNo;
    String usrId;
    String loginId;
    String ratngCd;
    String pstnCd;
    String flag;
    String corpNo;
    LocalDateTime startDt;
    LocalDateTime endDt;
    String dltYn;
    String deptUuid;
}
