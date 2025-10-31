package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menu;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBilling;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmButtonAuthority;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenu;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class MenuFormImpl implements MenuForm {

    @Override
    public SmMenu toSmMenu(Menu menu) {
        if ( menu == null ) {
            return null;
        }

        SmMenu smMenu = new SmMenu();

        smMenu.setMenuNo( menu.getMenuNo() );
        smMenu.setMenuCd( menu.getMenuCd() );
        smMenu.setUpMenuCd( menu.getUpMenuCd() );
        smMenu.setMenuNm( menu.getMenuNm() );
        smMenu.setMenuDscrpt( menu.getMenuDscrpt() );
        smMenu.setMenuUrl( menu.getMenuUrl() );
        smMenu.setMenuUseYn( menu.getMenuUseYn() );
        smMenu.setMenuDsplyOrdr( menu.getMenuDsplyOrdr() );
        smMenu.setMenuLvl( menu.getMenuLvl() );
        smMenu.setDltYn( menu.getDltYn() );
        smMenu.setLkYn( menu.getLkYn() );
        smMenu.setIconNm( menu.getIconNm() );
        smMenu.setMenuDiv( menu.getMenuDiv() );
        smMenu.setMenuApi( menu.getMenuApi() );

        return smMenu;
    }

    @Override
    public SmMenu toSmMenu(MenuMove menuMove) {
        if ( menuMove == null ) {
            return null;
        }

        SmMenu smMenu = new SmMenu();

        smMenu.setMenuCd( menuMove.getMenuCd() );
        smMenu.setUpMenuCd( menuMove.getUpMenuCd() );
        smMenu.setMenuDsplyOrdr( menuMove.getMenuDsplyOrdr() );
        smMenu.setMenuLvl( menuMove.getMenuLvl() );

        return smMenu;
    }

    @Override
    public SmButtonAuthority toSmButtonAuthority(BtnAuthority btnAuthority) {
        if ( btnAuthority == null ) {
            return null;
        }

        SmButtonAuthority smButtonAuthority = new SmButtonAuthority();

        smButtonAuthority.setBtnNo( btnAuthority.getBtnNo() );
        smButtonAuthority.setBtnId( btnAuthority.getBtnId() );
        smButtonAuthority.setMenuNo( btnAuthority.getMenuNo() );
        smButtonAuthority.setMenuCd( btnAuthority.getMenuCd() );
        smButtonAuthority.setBtnUrl( btnAuthority.getBtnUrl() );
        smButtonAuthority.setBtnNmKrn( btnAuthority.getBtnNmKrn() );
        smButtonAuthority.setUseYn( btnAuthority.getUseYn() );

        return smButtonAuthority;
    }

    @Override
    public SmBilling toSmBilling(Billing billing) {
        if ( billing == null ) {
            return null;
        }

        SmBilling smBilling = new SmBilling();

        smBilling.setMenuNo( billing.getMenuNo() );
        smBilling.setMenuCd( billing.getMenuCd() );
        smBilling.setBilCode( billing.getBilCode() );

        return smBilling;
    }

    @Override
    public void updateSmMenu(MenuUpdate menu, SmMenu smMenu) {
        if ( menu == null ) {
            return;
        }

        if ( menu.getMenuNo() != null ) {
            smMenu.setMenuNo( menu.getMenuNo() );
        }
        if ( menu.getMenuCd() != null ) {
            smMenu.setMenuCd( menu.getMenuCd() );
        }
        if ( menu.getMenuNm() != null ) {
            smMenu.setMenuNm( menu.getMenuNm() );
        }
        if ( menu.getMenuDscrpt() != null ) {
            smMenu.setMenuDscrpt( menu.getMenuDscrpt() );
        }
        if ( menu.getMenuUrl() != null ) {
            smMenu.setMenuUrl( menu.getMenuUrl() );
        }
        if ( menu.getMenuUseYn() != null ) {
            smMenu.setMenuUseYn( menu.getMenuUseYn() );
        }
        if ( menu.getMenuDsplyOrdr() != null ) {
            smMenu.setMenuDsplyOrdr( menu.getMenuDsplyOrdr() );
        }
        if ( menu.getLkYn() != null ) {
            smMenu.setLkYn( menu.getLkYn() );
        }
        if ( menu.getIconNm() != null ) {
            smMenu.setIconNm( menu.getIconNm() );
        }
        if ( menu.getMenuDiv() != null ) {
            smMenu.setMenuDiv( menu.getMenuDiv() );
        }
        if ( menu.getMenuApi() != null ) {
            smMenu.setMenuApi( menu.getMenuApi() );
        }
    }
}
