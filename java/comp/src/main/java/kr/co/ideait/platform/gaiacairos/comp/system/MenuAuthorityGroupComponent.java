package kr.co.ideait.platform.gaiacairos.comp.system;

import java.util.List;

import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ideait.platform.gaiacairos.comp.system.service.MenuAuthorityGroupService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenuAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menuauthoritygroup.MenuAuthorityGroupForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuAuthorityGroupComponent extends AbstractComponent {
	
	private final MenuAuthorityGroupService menuAuthorityGroupService;
	
	/**
     * 메뉴의 그룹권한 수정
     * @param syncUserIds
     * @return
     */
    @Transactional
    public String updateMenuAuthority(MenuAuthorityGroupForm.MenuAuthorityGroupRghtKindUpdate menuAuthorityGroupRghtKindUpdate) {
    	
    	String menuCd = menuAuthorityGroupRghtKindUpdate.getMenuCd();
    	String rghtGrpCd = menuAuthorityGroupRghtKindUpdate.getRghtGrpCd();
    	List<String> checkedRghtKindAndActionType = menuAuthorityGroupRghtKindUpdate.getCheckedRghtKindAndActionType();

		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
		}
    	
    	for(String actionItem : checkedRghtKindAndActionType) {	
    		String updateData[] = actionItem.split(",");
    		
    		if("I".equals(updateData[1])) {
    			menuAuthorityGroupService.insertMenuAuthorityGroupRghtKind(menuCd, rghtGrpCd, updateData[0], userAuth.getUsrId());
    		}else {
    			menuAuthorityGroupService.deleteMenuAuthorityGroupRghtKind(menuCd, rghtGrpCd, updateData[0], userAuth.getUsrId());
    			
    		}
    	}

        return "success";
    }
    
	/**
     * 메뉴에 그룹권한 추가
     * @param syncUserIds
     * @return
     */
    @Transactional
    public String insertMenuAuthority(List<SmMenuAuthorityGroup> param) {
    	UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
		}
    	for (SmMenuAuthorityGroup item : param) {
    		String rghtKind[] = item.getRghtKind().split(",");
    		for(String insertRghtKind : rghtKind) {
    			menuAuthorityGroupService.insertMenuAuthorityGroupRghtKind(item.getMenuCd(), item.getRghtGrpCd(), insertRghtKind, userAuth.getUsrId());
    		}
    	}
    	return "success";
    }
    
    /**
     * 메뉴의 그룹권한 삭제
     * @param syncUserIds
     * @return
     */
    @Transactional
    public String deleteMenuAuthority(MenuAuthorityGroupForm.MenuAuthorityGroupDeleteList param) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
		}

    	log.info("param : >>>>>> " + param.toString());
    	
    	for (String item : param.getRghtGrpCdList()) {
    		menuAuthorityGroupService.deleteMenuAuthorityGroup(param.getMenuCd(), item, userAuth.getUsrId());
    	}
    	return "success";
    }

}
