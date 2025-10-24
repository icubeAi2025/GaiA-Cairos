package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class MenuDtoImpl implements MenuDto {

    @Override
    public Menu fromSmMenu(SmMenu smMenu) {
        if ( smMenu == null ) {
            return null;
        }

        Menu menu = new Menu();

        menu.setMenuNo( smMenu.getMenuNo() );
        menu.setMenuCd( smMenu.getMenuCd() );
        menu.setUpMenuCd( smMenu.getUpMenuCd() );
        menu.setMenuNm( smMenu.getMenuNm() );
        menu.setMenuDscrpt( smMenu.getMenuDscrpt() );
        menu.setMenuUrl( smMenu.getMenuUrl() );
        menu.setMenuUseYn( smMenu.getMenuUseYn() );
        menu.setMenuDsplyOrdr( smMenu.getMenuDsplyOrdr() );
        menu.setMenuLvl( smMenu.getMenuLvl() );
        menu.setDltYn( smMenu.getDltYn() );
        menu.setLkYn( smMenu.getLkYn() );
        menu.setIconNm( smMenu.getIconNm() );
        menu.setMenuDiv( smMenu.getMenuDiv() );
        menu.setMenuApi( smMenu.getMenuApi() );
        menu.setRgstDt( smMenu.getRgstDt() );
        menu.setChgDt( smMenu.getChgDt() );

        return menu;
    }

    @Override
    public MyMenu toMyMenu(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        MyMenu myMenu = new MyMenu();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            myMenu.put( key, value );
        }

        return myMenu;
    }

    @Override
    public List<Menu> fromSmMenuList(List<SmMenu> smMenuList) {
        if ( smMenuList == null ) {
            return null;
        }

        List<Menu> list = new ArrayList<Menu>( smMenuList.size() );
        for ( SmMenu smMenu : smMenuList ) {
            list.add( fromSmMenu( smMenu ) );
        }

        return list;
    }

    @Override
    public BillingCommonCode fromSmBillingMybatis(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        BillingCommonCode billingCommonCode = new BillingCommonCode();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            billingCommonCode.put( key, value );
        }

        return billingCommonCode;
    }

    @Override
    public ButtonAuthority fromSmBtnAuthorityMybatis(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        ButtonAuthority buttonAuthority = new ButtonAuthority();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            buttonAuthority.put( key, value );
        }

        return buttonAuthority;
    }

    @Override
    public Billing fromSmBilling(SmBilling smBilling) {
        if ( smBilling == null ) {
            return null;
        }

        Billing billing = new Billing();

        billing.setBilNo( smBilling.getBilNo() );
        billing.setMenuNo( smBilling.getMenuNo() );
        billing.setMenuCd( smBilling.getMenuCd() );
        billing.setBilCode( smBilling.getBilCode() );

        return billing;
    }

    @Override
    public List<Billing> fromSmBillingList(List<SmBilling> smBillingList) {
        if ( smBillingList == null ) {
            return null;
        }

        List<Billing> list = new ArrayList<Billing>( smBillingList.size() );
        for ( SmBilling smBilling : smBillingList ) {
            list.add( fromSmBilling( smBilling ) );
        }

        return list;
    }

    @Override
    public List<ButtonAuthority> fromSmButtonAuthority(List<SmButtonAuthority> smButtonAuthorityList) {
        if ( smButtonAuthorityList == null ) {
            return null;
        }

        List<ButtonAuthority> list = new ArrayList<ButtonAuthority>( smButtonAuthorityList.size() );
        for ( SmButtonAuthority smButtonAuthority : smButtonAuthorityList ) {
            list.add( smButtonAuthorityToButtonAuthority( smButtonAuthority ) );
        }

        return list;
    }

    @Override
    public MenuAuthSelectedMenuData fromSmMenuMybatis(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        MenuAuthSelectedMenuData menuAuthSelectedMenuData = new MenuAuthSelectedMenuData();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            menuAuthSelectedMenuData.put( key, value );
        }

        return menuAuthSelectedMenuData;
    }

    protected ButtonAuthority smButtonAuthorityToButtonAuthority(SmButtonAuthority smButtonAuthority) {
        if ( smButtonAuthority == null ) {
            return null;
        }

        ButtonAuthority buttonAuthority = new ButtonAuthority();

        buttonAuthority.setMenuNo( smButtonAuthority.getMenuNo() );
        buttonAuthority.setMenuCd( smButtonAuthority.getMenuCd() );
        buttonAuthority.setBtnNo( smButtonAuthority.getBtnNo() );
        buttonAuthority.setBtnId( smButtonAuthority.getBtnId() );
        buttonAuthority.setBtnUrl( smButtonAuthority.getBtnUrl() );
        buttonAuthority.setBtnNmKrn( smButtonAuthority.getBtnNmKrn() );
        buttonAuthority.setRghtKind( smButtonAuthority.getRghtKind() );
        buttonAuthority.setUseYn( smButtonAuthority.getUseYn() );
        buttonAuthority.setRgstDt( smButtonAuthority.getRgstDt() );
        buttonAuthority.setChgDt( smButtonAuthority.getChgDt() );

        return buttonAuthority;
    }
}
