package kr.co.ideait.platform.gaiacairos.comp.system.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;

import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.DaoUtils;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBilling;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmButtonAuthority;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenu;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmBillingRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmButtonAuthorityRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmMenuRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.MenuMybatisParam.MenuBillingListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.MenuMybatisParam.MenuBtnAuthorityListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.MenuMybatisParam.MenuMoveInput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MenuService extends AbstractGaiaCairosService {

    @Autowired
    SmMenuRepository smMenuRepository;

    @Autowired
    SmBillingRepository smBillingRepository;

    @Autowired
    SmButtonAuthorityRepository smButtonAuthorityRepository;

    // @Cacheable(value = "menu", key = "'all-menu'")
    public List<SmMenu> getMenuList() {

        if ("PGAIA".equals(platform.toUpperCase())) {
            platform = "GAIA";
        }

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.selectMenuList",
                platform.toUpperCase());
    }
    // public List getMenuList() {
    // return
    // mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.selectMenuListAll");
    // }

    // @Cacheable(value = "menuPath", key = "'all-menu'")
    public Set<String> getMenuPath() {
        List<SmMenu> menuList = getMenuList();
        Set<String> menuPath = new HashSet<>();

        menuList.stream().filter(menu -> menu.getMenuUrl() != null && !menu.getMenuUrl().isEmpty())
                .map(SmMenu::getMenuUrl).distinct().forEach(menuPath::add);
        log.debug("======================== Set<String> 보기 ========================");
        log.debug("menuPath : " + menuPath);
        log.debug("==================================================================");
        return menuPath;
    }

    /*
     * 내 메뉴
     */
    @Cacheable(value = "menu", key = "#p0")
    public List<Map<String, ?>> getMenuList(String contractNo, String userId) {
        MybatisInput input = MybatisInput.of().add("contractNo", contractNo).add("userId", userId);
        return mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.selectMyMenuList", input);
    }

    /**
     * 내가 권한이 있는 메뉴 경로
     */
    @Cacheable(value = "menuPath", key = "#p0")
    public Set<String> getMyMenuPath(String contractNo, String userId) {
        List<Map<String, ?>> myMenu = getMenuList(contractNo, userId);
        Set<String> myMenuPath = new HashSet<>();
        myMenu.stream().filter(menu -> menu.containsKey("menu_url") && !menu.get("menu_url").toString().isEmpty())
                .map(menu -> menu.get("menu_url").toString()).distinct().forEach(myMenuPath::add);
        log.debug("======================== Set<String> 보기 ========================");
        log.debug("myMenuPath : " + myMenuPath);
        log.debug("==================================================================");

        return myMenuPath;
    }

    public SmMenu getMenu(String menuCd) {
//        return smMenuRepository.findByMenuCdAndDltYn(menuCd,"N").orElse(null);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.selectSmMenuByMenuCd",menuCd);
    }

    public SmMenu createMenu(SmMenu smMenu) {
        return createMenu(smMenu, null);
    }

    public SmMenu createMenu(SmMenu smMenu, String userId) {
        // 메뉴 순서 설정
        Short maxDisplayOrder = smMenuRepository.maxMenuDsplyOrdrByUpMenuCd(smMenu.getUpMenuCd());
        if (maxDisplayOrder == null) {
            maxDisplayOrder = (short) 1;
        } else {
            maxDisplayOrder++;
        }
        smMenu.setMenuDsplyOrdr(maxDisplayOrder);
        smMenu.setDltYn("N");

        if (userId == null) {
            smMenu.setRgstrId(UserAuth.get(true).getUsrId());
            smMenu.setChgId(UserAuth.get(true).getUsrId());
        } else {
            smMenu.setRgstrId(userId);
            smMenu.setChgId(userId);
        }

        return smMenuRepository.save(smMenu);
    }

    public SmMenu updateMenu(SmMenu smMenu) {
        return updateMenu(smMenu, null);
    }

    public SmMenu updateMenu(SmMenu smMenu, String userId) {
        if (userId == null) {
            smMenu.setChgId(UserAuth.get(true).getUsrId());
        } else {
            smMenu.setChgId(userId);
        }

        log.info("smMenu : >>>>>>>>>>>>>>>>>> {}", smMenu.toString());
        return smMenuRepository.save(smMenu);
    }

    @Transactional
    public boolean upMenu(SmMenu smMenu) {
        MenuMoveInput input = new MenuMoveInput();
        input.setMenuDsplyOrdr(smMenu.getMenuDsplyOrdr());
        input.setMenuLvl(smMenu.getMenuLvl());
        input.setUpMenuCd(smMenu.getUpMenuCd());

        SmMenu upMenu = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.menuUp",
                input);

        if (upMenu == null) {
            return false;
        }

        short tmp;
        tmp = smMenu.getMenuDsplyOrdr();
        smMenu.setMenuDsplyOrdr(upMenu.getMenuDsplyOrdr());
        upMenu.setMenuDsplyOrdr(tmp);

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.updateMenuDsplyOrdr",
                smMenu);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.updateMenuDsplyOrdr",
                upMenu);

        return true;
    }

    @Transactional
    public boolean downMenu(SmMenu smMenu) {
        MenuMoveInput input = new MenuMoveInput();
        input.setMenuDsplyOrdr(smMenu.getMenuDsplyOrdr());
        input.setMenuLvl(smMenu.getMenuLvl());
        input.setUpMenuCd(smMenu.getUpMenuCd());

        SmMenu downMenu = mybatisSession
                .selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.menuDown", input);

        if (downMenu == null) {
            return false;
        }

        short tmp;
        tmp = smMenu.getMenuDsplyOrdr();
        smMenu.setMenuDsplyOrdr(downMenu.getMenuDsplyOrdr());
        downMenu.setMenuDsplyOrdr(tmp);

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.updateMenuDsplyOrdr",
                smMenu);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.updateMenuDsplyOrdr",
                downMenu);

        return true;
    }

    public boolean existMenuCode(String menuCd) {
        return smMenuRepository.existsByMenuCdAndDltYn(menuCd, "N");
    }

    public void deleteMenuList(List<String> menuCdList) {
        deleteMenuList(menuCdList, null);
    }

    public void deleteMenuList(List<String> menuCdList, String userId) {
        smMenuRepository.findAllByMenuCdIn(menuCdList).forEach(menu -> {
            if (userId == null) {
                smMenuRepository.updateDelete(menu);
            } else {
                smMenuRepository.updateDelete(menu, userId);
            }
        });
    }

    public List<Map<String, ?>> getBillingList(int menuNo) {
        MenuBillingListInput input = new MenuBillingListInput();
        input.setMenuNo(menuNo);
        input.setCmnGrpCd(CommonCodeConstants.PAID_FUNCTION_CODE_GROUP_CODE);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.selectMenuBillingList", input);
    }

    public SmBilling createBilling(SmBilling billing) {
        if (billing.getBilNo() != null) {
            billing.setBilNo(null);
        }
        billing.setDltYn("N");
        return smBillingRepository.save(billing);
    }

    public void deleteBilling(List<Integer> billingNoList) {
        deleteBilling(billingNoList, null);
    }

    public void deleteBilling(List<Integer> billingNoList, String userId) {
        smBillingRepository.findAllById(billingNoList).forEach(billingNo -> {
            if (userId == null) {
                smBillingRepository.updateDelete(billingNo);
            } else {
                smBillingRepository.updateDelete(billingNo, userId);
            }
        });
    }

    public void deleteBillingApi(List<Integer> billingNoList, String userId) {
        for (int i = 0; i < billingNoList.size(); i++) {
            SmBilling smBilling = smBillingRepository.findById(billingNoList.get(i)).orElse(null);
            if (userId == null) {
                smBilling.setDltId(UserAuth.get(true).getUsrId());
            } else {
                smBilling.setDltId(userId);
            }
            smBilling.setDltDt(LocalDateTime.now());
            smBilling.setDltYn("Y");

            smBillingRepository.save(smBilling);
        }
    }

    public boolean existBilling(SmBilling smBilling) {
        SmBilling existBilling = smBillingRepository.findByMenuNoAndDltYnAndMenuCdAndBilCode(
                smBilling.getMenuNo(),
                "N",
                smBilling.getMenuCd(),
                smBilling.getBilCode());

        return existBilling == null ? false : true;
    }


    /**
     * 메뉴권한 추가 > 선택한 메뉴 계층리스트 조회
     * 
     * @param menuCd
     * @return
     */
    public List<Map<String, ?>> getMenuBreadcrumb(String menuCd, String platform) {

        MybatisInput input = MybatisInput.of().add("menuCd", menuCd)
                .add("platform", platform.toUpperCase());

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu.selectMenuHierarchyList", input);
    }

    // ----------------------------------------API통신--------------------------------------------

    /**
     * 메뉴 수정
     */
    @Transactional
    public void updateMenuApi(SmMenu smMenuParam, String usrId) {
        // 기존 DB에 저장된 메뉴 찾기
        SmMenu smMenu = smMenuRepository.findByMenuCdAndDltYn(smMenuParam.getMenuCd(), "N");

        // 데이터 업데이트
        smMenu.setMenuNm(smMenuParam.getMenuNm());
        smMenu.setMenuDscrpt(smMenuParam.getMenuDscrpt());
        smMenu.setMenuUrl(smMenuParam.getMenuUrl());
        smMenu.setMenuUseYn(smMenuParam.getMenuUseYn());
        smMenu.setMenuDsplyOrdr(smMenuParam.getMenuDsplyOrdr());
        smMenu.setLkYn(smMenuParam.getLkYn());
        smMenu.setIconNm(smMenuParam.getIconNm());
        smMenu.setMenuDiv(smMenuParam.getMenuDiv());
        smMenu.setRgstrId(usrId);
        smMenu.setChgId(usrId);

        smMenuRepository.save(smMenu);
    }

    /**
     * 메뉴 위로 이동
     */
    @Transactional
    public void upMenuApi(SmMenu paramMenu) {
        SmMenu smMenu = smMenuRepository.findByMenuCdAndDltYn(paramMenu.getMenuCd(), "N");
        upMenu(smMenu);
    }

    /**
     * 메뉴 아래로 이동
     */
    @Transactional
    public void downMenuApi(SmMenu paramMenu) {
        SmMenu smMenu = smMenuRepository.findByMenuCdAndDltYn(paramMenu.getMenuCd(), "N");
        downMenu(smMenu);
    }

    /**
     * 유료 기능 등록
     */
    @Transactional
    public void createBillingApi(SmBilling billing) {
        SmMenu smMenu = smMenuRepository.findByMenuCdAndDltYn(billing.getMenuCd(), "N");
        billing.setMenuNo(smMenu.getMenuNo());
        createBilling(billing);
    }

}
