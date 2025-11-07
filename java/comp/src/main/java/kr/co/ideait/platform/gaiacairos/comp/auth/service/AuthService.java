package kr.co.ideait.platform.gaiacairos.comp.auth.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService extends AbstractGaiaCairosService {
	
	/**
	 * 사용자 존재 여부 조회
	 * 
	 * @param 	Map<String, String> (ociID 또는 ncpId 또는 loginId) 
	 * @return 	SmUserInfo
	 * @throws
	 */
	public SmUserInfo selectUser(Map<String, String> params) {

		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.auth.selectUser", params);
	}
	
	/**
	 * 로그인 사용자 정보 조회
	 *
	 * @param 	String (사용자아이디)
	 * @return 	Map<String, Object>
	 * @throws
	 */
	public Map<String, Object> loginUserInfo(MybatisInput input) {

		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.auth.selectLoginUser", input);
	}
	
	/**
     * 권한그룹 목록 가져오기
     * @param 	systemType(GaiA / CaiROS 여부)
     * @param 	pjtNo 	(프로젝트번호)
     * @param 	cntrctNo 	(계약번호)
     * @param 	usrId 	(사용자ID)
     * @return	List<String>
     */
    public List<String> getAuthorityGroupList(MybatisInput input){

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.auth.selectMyAuthorityGroupList", input);
    }
    
    /**
     * 접근 URI에 대한 허용여부를 체크하기 위해 접근 가능한 전체 URI 목록 조회
     * @param 	List<String> 	(권한그룹 목록)
     * @param 	cmnGrpCd 		(공통코드그룹코드)
     * @return	List<Map<String, Object>>
     */
    public List<String> getAccessUriList(MybatisInput input){

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.auth.selectAuthResourceUriList", input);
    }
    
    /**
     * 리소스 아이디로 권한 유무 가져오기
     * @param 	resc_id 		(프로그램ID)	
     * @param 	List<String> 	(권한그룹 목록)
     * @param 	cmnGrpCd 		(공통코드그룹코드)
     * @return 	Map<String, Object>
     */
    public Map<String, Object> getAccessIdCheck(MybatisInput input){

        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.auth.selectAuthOne", input);
    }

}
