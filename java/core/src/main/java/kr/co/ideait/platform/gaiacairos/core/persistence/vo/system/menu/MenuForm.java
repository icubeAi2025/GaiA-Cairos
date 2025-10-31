package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBilling;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmButtonAuthority;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenu;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface MenuForm {

    SmMenu toSmMenu(Menu menu);

    SmMenu toSmMenu(MenuMove menuMove);

    SmButtonAuthority toSmButtonAuthority(BtnAuthority btnAuthority);

    SmBilling toSmBilling(Billing billing);

    void updateSmMenu(MenuUpdate menu, @MappingTarget SmMenu smMenu);

    /**
     * 메뉴 생성 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class Menu extends CommonForm {
        Integer menuNo;
        @NotBlank
        String menuCd;
        @NotBlank
        String upMenuCd;
        @NotBlank
        String menuNm;
        String menuDscrpt;
        String menuUrl;
        @Size(max = 1)
        @NotBlank
        String menuUseYn;
        Short menuDsplyOrdr;
        Short menuLvl;
        String dltYn;
        String lkYn;
        String iconNm;
        @NotBlank
        String menuDiv;
        String menuApi;
    }

    /**
     * 메뉴 수정 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class MenuUpdate extends CommonForm {
        @NotNull
        Integer menuNo;
        @NotBlank
        String menuNm;
        String menuDscrpt;
        String menuUrl;
        String menuUseYn;
        Short menuDsplyOrdr;
        String lkYn;
        String iconNm;
        @NotBlank
        String menuDiv;
        String menuApi;
        String menuCd;
    }

    /**
     * 메뉴 이동 form
     */
    @Data
    class MenuMove{
        @NotNull
        Short menuDsplyOrdr;
        @NotNull
        Short menuLvl;
        @NotNull
        String upMenuCd;
        @NotNull
        String menuCd;
    }

    /**
     * 메뉴 리스트 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class MenuCdList extends CommonForm {
        List<String> menuCdList;
    }

    /**
     * 메뉴 버튼 권한 리스트 form
     */
    @Data
    class MenuBtnAuthNoList {
        List<Integer> menuBtnAuthNoList;
    }

    /**
     * 메뉴 버튼 권한 생성 form
     */
    @Data
    class BtnAuthority{
        @NotNull
        Integer menuNo;
        @NotNull
        String menuCd;
        Integer btnNo;
        String btnUrl;
        @NotNull
        String btnNmKrn;
        @NotNull
        String btnId;
        // String btnNmEng;
        List<String> rghtKindList;
        @NotNull
        String useYn;

        List<String> removedRghtKindList;
    }

    /**
     * 메뉴 유료기능 생성 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class Billing extends CommonForm {
        Integer menuNo;
        String menuCd;
        @Size(max = 5)
        String bilCode;
    }

    /**
     * 메뉴 유료기능 리스트 form
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    class MenuBillingNoList extends CommonForm {
        List<Integer> bilNoList;
    }

    
}
