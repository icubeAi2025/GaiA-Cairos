package kr.co.ideait.platform.gaiacairos.comp.auth;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.ideait.platform.gaiacairos.comp.auth.service.AuthService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthComponent extends AbstractComponent {
	
	@Autowired
	AuthService authService;
	
	/**
	 * 사용자 존재 여부 조회
	 * 
	 * @param 	Map<String, String> (ociID 또는 ncpId 또는 loginId) 
	 * @return 	SmUserInfo
	 * @throws
	 */
	public SmUserInfo selectUser(Map<String, String> params) {

		return authService.selectUser(params);
	}
	
	/**
	 * 로그인 사용자 정보 조회
	 *
	 * @param 	String (사용자아이디)
	 * @return 	Map<String, Object>
	 * @throws
	 */
	public Map<String, Object> loginUserInfo(String usrId) {
		MybatisInput input = MybatisInput.of().add("usrId", usrId);

		return authService.loginUserInfo(input);
	}
	
	/**
     * 접근 URI에 대한 허용여부를 체크하기 위해 접근 가능 URI 목록 조회
     * @return 	List<Map<String, Object>>
     */
    public List<String> getAccessUriList(){ 
    	
    	//사용중인 역할 목록 가져오기 (그룹코드만 넣으면 서브쿼리로 이용시 조회 시간이 10초 이상 걸림.. 원인 파악 불가) IN절 대입으로 변경
    	MybatisInput authorityGroupParam = MybatisInput.of().add("pjtNo", UserAuth.get(true).getPjtNo())
											    			.add("cntrctNo", UserAuth.get(true).getCntrctNo())
											    			.add("pjtType", pjtType)
											    			.add("usrId", UserAuth.get(true).getUsrId());
    			
    	List<String> codeList = authService.getAuthorityGroupList(authorityGroupParam);    	
    	
    	MybatisInput accessUriParam = MybatisInput.of().add("cmnGrpCd", CommonCodeConstants.KIND_CODE_GROUP_CODE)
        													.add("codeList", codeList);

        return authService.getAccessUriList(accessUriParam);
    }
    
    /**
     * 리소스 아이디로 권한 유무 가져오기
     * @param 	resc_id 	(프로그램ID)
     * @return 	String Y or N
     */
    public String getAccessIdCheck(String resc_id){
    	
    	String accessYn = "Y";    	
    	
    	if(!UserAuth.get(true).isAdmin()) {
    		//사용중인 역할 목록 가져오기 (그룹코드만 넣으면 서브쿼리로 이용시 조회 시간이 10초 이상 걸림.. 원인 파악 불가) IN절 대입으로 변경
        	MybatisInput authorityGroupParam = MybatisInput.of().add("pjtNo", UserAuth.get(true).getPjtNo())
    											    			.add("cntrctNo", UserAuth.get(true).getCntrctNo())
    											    			.add("pjtType", pjtType)
    											    			.add("usrId", UserAuth.get(true).getUsrId());
        			
        	List<String> codeList = authService.getAuthorityGroupList(authorityGroupParam);    	
        	
        	MybatisInput accessIdCheckParam = MybatisInput.of().add("cmnGrpCd", CommonCodeConstants.KIND_CODE_GROUP_CODE)
        														.add("rescId", resc_id) 
            													.add("codeList", codeList);
        	
        	Map<String, Object> returnValue = authService.getAccessIdCheck(accessIdCheckParam);
        	
        	accessYn = (String) returnValue.get("auth_yn");    		
    	}

        return accessYn;
    }

}
