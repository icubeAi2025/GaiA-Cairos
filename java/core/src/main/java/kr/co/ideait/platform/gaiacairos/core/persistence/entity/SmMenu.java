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
@Alias("smMenu")
public class SmMenu extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer menuNo;
    String menuCd;
    String upMenuCd;
    String menuNm;
    String menuDscrpt;
    String menuUrl;
    String menuUseYn;
    Short menuDsplyOrdr;
    Short menuLvl;
    String dltYn;
    String lkYn;
    String iconNm;
    String menuDiv;
    String menuApi;
}
