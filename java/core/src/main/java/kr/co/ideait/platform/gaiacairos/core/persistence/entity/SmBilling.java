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
public class SmBilling extends AbstractRdIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer bilNo;
    Integer menuNo;
    String menuCd;
    String bilCode;
    String dltYn;
}
