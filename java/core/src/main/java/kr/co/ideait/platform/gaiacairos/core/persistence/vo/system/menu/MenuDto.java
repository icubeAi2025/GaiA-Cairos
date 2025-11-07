package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menu;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBilling;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmButtonAuthority;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenu;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper(config = GlobalMapperConfig.class)
public interface MenuDto {

    Menu fromSmMenu(SmMenu smMenu);

    MyMenu toMyMenu(Map<String, ?> map);

    List<Menu> fromSmMenuList(List<SmMenu> smMenuList);

    BillingCommonCode fromSmBillingMybatis(Map<String, ?> map);

    ButtonAuthority fromSmBtnAuthorityMybatis(Map<String, ?> map);

    Billing fromSmBilling(SmBilling smBilling);

    List<Billing> fromSmBillingList(List<SmBilling> smBillingList);

    List<ButtonAuthority> fromSmButtonAuthority(List<SmButtonAuthority> smButtonAuthorityList);

    MenuAuthSelectedMenuData fromSmMenuMybatis(Map<String, ?> map);

    @Data
    class Menu {
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDateTime rgstDt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDateTime chgDt;
    }

    @Getter
    @Setter
    class MyMenu extends MapDto {
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
    }

    @Data
    class Billing {
        Integer bilNo;
        Integer menuNo;
        String menuCd;
        String bilCode;
    }

    @Getter
    @Setter
    class BillingCommonCode extends MapDto {
        Integer bilNo;
        Integer menuNo;
        String menuCd;
        String bilCode;
        String bilNm; // cmnCdNmKrn
        String bilDscrpt; // cmnCdDscrpt
        LocalDateTime rgstDt;
        LocalDateTime chgDt;

        /*
         * a.bilNo,
         * a.menuNo,
         * a.menuCd,
         * a.bilCode,
         * b.cmnCdNo,
         * b.cmnGrpNo,
         * b.cmnGrpCd,
         * b.cmnCd,
         * b.cmnCdNmEng,
         * b.cmnCdNmKrn,
         * b.cmnCdDsplyOrder,
         * b.cmnCdDscrpt,
         * b.useYn
         */

    }

    @Data
    class ButtonAuthority extends MapDto{
        Integer menuNo;
        String menuCd;
        Integer btnNo;
        String btnId;
        String btnUrl;
        String btnNmKrn;
        // String btnNmEng;
        String rghtKind;
        String rghtKindList;
        String cmnCdNmKrn;
        String useYn;
        LocalDateTime rgstDt;
        LocalDateTime chgDt;
    }

    @Data
    class MenuAuthSelectedMenuData extends MapDto{
        Integer menuNo;
        String menuCd;
        String upMenuCd;
        String menuNm;
    }

}
