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
public class SmProjectBilling extends AbstractRdIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer pjtBilNo;
    Integer bilNo;
    Integer menuNo;
    String menuCd;
    String bilCode;
    String pjtNo;
    String cntrctNo;
    String pjtType;
    String dltYn;
}
