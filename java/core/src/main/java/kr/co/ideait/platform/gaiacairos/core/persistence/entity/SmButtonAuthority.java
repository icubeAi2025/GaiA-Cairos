package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class SmButtonAuthority extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer btnNo;
    String btnId;
    Integer menuNo;
    String menuCd;
    String btnUrl;
    String btnNmEng;
    String btnNmKrn;
    String rghtKind;
    String useYn;
	String dltYn;
}
