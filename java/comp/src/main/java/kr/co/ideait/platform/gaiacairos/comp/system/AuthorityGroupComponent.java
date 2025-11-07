package kr.co.ideait.platform.gaiacairos.comp.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ideait.platform.gaiacairos.comp.system.service.AuthorityGroupService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup.AuthorityGroupForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthorityGroupComponent extends AbstractComponent {

	@Autowired
	private AuthorityGroupService authorityGroupService;
	
	@Autowired
    AuthorityGroupForm authorityGroupForm;
	
	/**
     * 권한그룹 추가
     * @param 
     * @return
     */
    @Transactional
    public boolean createAuthorityGroup(AuthorityGroupForm.AuthorityGroup authorityGroup) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
		}
    	boolean reTurnValue = true;
    	
    	// 그룹 코드는 uuid로 생성.
        authorityGroup.setRghtGrpCd(UUID.randomUUID().toString());
        SmAuthorityGroup smAuthorityGroup = authorityGroupForm.toAuthorityGroup(authorityGroup);
        
        SmAuthorityGroup reTurnSmAuthorityGroup = authorityGroupService.createAuthorityGroup(smAuthorityGroup);
        
        log.info("authorityGroup : >>>>> {}", authorityGroup.toString());
        
        if(!Objects.isNull(reTurnSmAuthorityGroup)) {
        	
        	List<String> insertRghtKindData = authorityGroup.getRghtKind();
        	
        	if(!insertRghtKindData.isEmpty()) {
        		
        		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
            	
            	for(String menuCdRghtKind : insertRghtKindData) {
            		String[] arrayData = menuCdRghtKind.split(",");
            		
            		Map<String, Object> map = new HashMap<String, Object>();
            		map.put("menuCd", arrayData[0]);
            		map.put("rghtKind", arrayData[1]);
            		map.put("rghtGrpCd", authorityGroup.getRghtGrpCd());
            		map.put("rgstrId", userAuth.getUsrId());
            		listMap.add(map);
            	}  
            	
            	authorityGroupService.insertGroupAllMenuAuthorityInfo(listMap);        		
        	}
        } else {
        	
        	//필요 시 예외처리 가능
        	reTurnValue = false;
        }

        return reTurnValue;
    }
    
    /**
     * 권한그룹 수정
     * @param 
     * @return
     */
    @Transactional
    public boolean updateAuthorityGroup(AuthorityGroupForm.AuthorityGroupUpdate authorityGroupUpdate) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
		}
    	boolean reTurnValue = true;
    	
    	SmAuthorityGroup reTurnSmAuthorityGroup = null;
    	
    	SmAuthorityGroup smAuthorityGroup = authorityGroupService.getAuthorityGroup(authorityGroupUpdate.getRghtGrpNo());
        if (smAuthorityGroup != null) {
            authorityGroupForm.updateSmAuthorityGroup(authorityGroupUpdate, smAuthorityGroup);
            
            reTurnSmAuthorityGroup = authorityGroupService.updateAuthorityGroup(smAuthorityGroup);
        }
        
        if(!Objects.isNull(reTurnSmAuthorityGroup)) {
        	
        	List<String> insertRghtKindData = authorityGroupUpdate.getAddRghtKind();
        	List<String> deleteRghtKindData = authorityGroupUpdate.getDelRghtKind();
        	
        	if(!insertRghtKindData.isEmpty()) {
        	
	        	List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
	        	
	        	for(String menuCdRghtKind : insertRghtKindData) {
	        		String[] arrayData = menuCdRghtKind.split(",");
	        		
	        		Map<String, Object> map = new HashMap<String, Object>();
	        		map.put("menuCd", arrayData[0]);
	        		map.put("rghtKind", arrayData[1]);
	        		map.put("rghtGrpCd", reTurnSmAuthorityGroup.getRghtGrpCd());
	        		map.put("rgstrId", userAuth.getUsrId());
	        		listMap.add(map);
	        	}  
	        	
	        	authorityGroupService.insertGroupAllMenuAuthorityInfo(listMap);
        	}
        	
        	if(!deleteRghtKindData.isEmpty()) {
	        	
	        	for(String menuCdRghtKind : deleteRghtKindData) {
	        		String[] arrayData = menuCdRghtKind.split(",");
	        		
	        		authorityGroupService.deleteAuthorityGroupRghtKind(arrayData[0], reTurnSmAuthorityGroup.getRghtGrpCd(), arrayData[1]);
	        	}
        	}
        } else {
        	
        	//필요 시 예외처리 가능
        	reTurnValue = false;
        }

        return reTurnValue;
    }
    
    /**
     * 권한그룹 삭제
     * @param 
     * @return
     */
    @Transactional
    public boolean deleteAuthorityGroup(AuthorityGroupForm.AuthorityGroupNoList authorityGroupNoList) {
    	
    	boolean reTurnValue = true;
    	
    	List<Integer> delAuthorityGroupNoList = authorityGroupNoList.getAuthorityGroupNoList();
    	
    	if(!delAuthorityGroupNoList.isEmpty()) {
    		
    		authorityGroupService.deleteAuthorityGroupList(delAuthorityGroupNoList);
    		
    		for(int delRghtGrpCd : delAuthorityGroupNoList) {
    			authorityGroupService.deleteAuthorityGroupRghtKind(delRghtGrpCd);
    		}
    		
    	} else {
    		
    		//필요 시 예외처리 가능
    		reTurnValue = false;
    	}    	

        return reTurnValue;
    }


}
