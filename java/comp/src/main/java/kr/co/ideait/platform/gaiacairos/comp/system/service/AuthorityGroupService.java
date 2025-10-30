package kr.co.ideait.platform.gaiacairos.comp.system.service;

import java.util.*;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroupUsers;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmAuthorityGroupRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmAuthorityGroupUsersRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup.AuthorityGroupMybatisParam.AuthorityGroupUserInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup.AuthorityGroupMybatisParam.AuthorityGroupUsersOutput;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorityGroupService extends AbstractGaiaCairosService {

    @Autowired
    SmAuthorityGroupRepository smAuthorityGroupRepository;

    @Autowired
    SmAuthorityGroupUsersRepository smAuthorityGroupUsersRepository;

    public List<MybatisOutput> getAuthorityGroupList(MybatisInput mybatisParams) {
        mybatisParams.put("grpTyCd", CommonCodeConstants.ATYPE_CODE_GROUP_CODE);
        mybatisParams.put("grpRoleCd", CommonCodeConstants.ROLE_CODE_GROUP_CODE);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.selectAuthorityGroupList", mybatisParams);
    }

    /**
     * 권한 그룹 조회
     * @param authorityGroupNo
     * @return
     */
    public SmAuthorityGroup getAuthorityGroup(int authorityGroupNo) {
        return smAuthorityGroupRepository.findById(authorityGroupNo).orElse(null);
    }

    /**
     * 권한그룹 추가 > 중복체크
     * @param cntrctNo
     * @param authorityGroupCode
     * @return
     */
    public boolean existAuthorityGroup(String cntrctNo, String authorityGroupCode) {
        return smAuthorityGroupRepository.existsByCntrctNoAndRghtGrpCd(cntrctNo, authorityGroupCode);
    }

    /**
     * 권한그룹 추가
     * @param smAuthorityGroup
     * @return
     */
    public SmAuthorityGroup createAuthorityGroup(SmAuthorityGroup smAuthorityGroup) {
        smAuthorityGroup.setDltYn("N");
        return smAuthorityGroupRepository.save(smAuthorityGroup);
    }

    /**
     * 권한 그룹 수정
     * @param smAuthorityGroup
     * @return
     */
    public SmAuthorityGroup updateAuthorityGroup(SmAuthorityGroup smAuthorityGroup) {
        return smAuthorityGroupRepository.save(smAuthorityGroup);
    }

    /**
     * 권한그룹 삭제
     * @param authorityGroupNoList
     */
    @Transactional
    public void deleteAuthorityGroupList(List<Integer> authorityGroupNoList) {
        smAuthorityGroupRepository.findAllById(authorityGroupNoList).forEach(authorityGroup -> {
            smAuthorityGroupRepository.updateDelete(authorityGroup);
        });
    }

    /**
     * 해당 권한 그룹의 사용자 조회
     * @param mybatisInput
     * @return
     */
    // 해당 권한그룹이 회사/ 부서/ 사용자/ 롤 인지에 따라서 조회해 오는 내용이 상이함. (추후 확인 필요)
    public List<AuthorityGroupUsersOutput> getAuthorityGroupUserList(AuthorityGroupUserInput mybatisInput) {
        //공통 코드 그룹 파라미터 추가
        mybatisInput.setPstnCd(CommonCodeConstants.PSTN_CODE_GROUP_CODE);
        mybatisInput.setRatngCd(CommonCodeConstants.RANK_CODE_GROUP_CODE);
        mybatisInput.setFlagCd(CommonCodeConstants.FLAG_CODE_GROUP_CODE);
        
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.selectAuthorityGroupUserList",
                mybatisInput);
    }

    /**
     * 해당 권한 그룹의 사용자 조회 (Grid 페이징)
     * @param mybatisInput
     * @return
     */
    // 해당 권한그룹이 회사/ 부서/ 사용자/ 롤 인지에 따라서 조회해 오는 내용이 상이함. (추후 확인 필요)
    @Transactional
    public Page<AuthorityGroupUsersOutput> getAuthorityGroupUserGridList(AuthorityGroupUserInput mybatisInput) {
        //공통 코드 그룹 파라미터 추가
        mybatisInput.setPstnCd(CommonCodeConstants.PSTN_CODE_GROUP_CODE);
        mybatisInput.setRatngCd(CommonCodeConstants.RANK_CODE_GROUP_CODE);
        mybatisInput.setFlagCd(CommonCodeConstants.FLAG_CODE_GROUP_CODE);

        List<AuthorityGroupUsersOutput> contents = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.selectAuthorityGroupUserList", mybatisInput);
        
        Long totalCount = getAuthorityGroupUserListCount(mybatisInput);

        return new PageImpl<>(contents, mybatisInput.getPageable(), totalCount);
    }

    @Transactional
    public List<SmAuthorityGroupUsers> createAuthorityGroupUsers(List<SmAuthorityGroupUsers> authorityGroupUsersList) {
        // TODO: 해당 권한그룹 사용자가 존재할 경우는 SKIP하게 수정 필요
        List<SmAuthorityGroupUsers> savedAuthorityGroupUsersList = new ArrayList<>();
        authorityGroupUsersList.forEach(authorityGroupUsers -> {
            savedAuthorityGroupUsersList.add(smAuthorityGroupUsersRepository.save(authorityGroupUsers));
        });
        return savedAuthorityGroupUsersList;
    }

    @Transactional
    public void deleteAuthorityGroupUsers(List<Integer> authorityGroupUserNoList) {
        smAuthorityGroupUsersRepository.deleteAllById(authorityGroupUserNoList); // real delete
    }

    /**
     * 부서(조직) 번호(authNo)로 권한 그룹 사용자 삭제
     * @param authNoList
     * @param rghtGrpUsrType
     */
    @Transactional
    public void deleteAuthorityGroupUsersByAuthNoAndRghtGrpUsrTy(List<Integer> authNoList, String rghtGrpUsrType) {
        MybatisInput input = MybatisInput.of().add("authNoList", authNoList)
                .add("rghtGrpUsrTy", rghtGrpUsrType);
        mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.deleteAuthorityGroupUserList", input);
    }

    /**
     * 해당 그룹의 권한그룹 사용자 총 개수 조회
     * @param input
     * @return
     */
    public Long getAuthorityGroupUserListCount(AuthorityGroupUserInput input) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.selectAuthorityGroupUserListCount", input);
    }

    /**
     * 해당 계약에 해당 사용자가 속한 권한 그룹들 조회
     * 
     * @param usrId
     * @param cntrctNo
     * @return
     */
    public List<SmAuthorityGroup> getAuthorityGroupListByUsrIdAndCntrctNo(String usrId, String cntrctNo) {
        HashMap<String,Object> mybatisParam = new HashMap<>();
        mybatisParam.put("usrId", usrId);
        mybatisParam.put("cntrctNo", cntrctNo);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.selectAuthorityGroupListByUsrIdAndCntrctNo", mybatisParam);
    }
    
    
    
    
    
    
    
    
    
    
    
    /**권한 설정 로직 추가로 인한 신규 쿼리들 추가 Start */
    
    /**
     * 권한 그룹 추가시 전체 메뉴의 권한 설정 정보 가져오기
     * @param systemType (GaiA / CaiROS 여부)
     * @return
     */
    public List<Map<String, Object>> selectAllMenuAuthorityInfo(String systemType){
    	
    	MybatisInput input = MybatisInput.of().add("systemType", systemType).add("cmnGrpCd", CommonCodeConstants.KIND_CODE_GROUP_CODE);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.selectAllMenuAuthorityInfo", input);
    }
    
    /**
     * 권한 그룹 추가시 설정한 메뉴 권한 저장하기
     * @param List<Map<String, Object>> 저장할 권한 정보(메뉴코드, 권한코드, 권한그룹코드) 리스트
     * @return
     */
    public int insertGroupAllMenuAuthorityInfo(List<Map<String, Object>> param){

        return mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.insertGroupAllMenuAuthorityInfo", param);
    }
    
    /**
     * 권한 그룹 수정시 설정된 메뉴 권한 정보 가져오기
     * @param systemType (GaiA / CaiROS 여부)
     * @return
     */
    public List<Map<String, Object>> selectAllMenuAuthoritySetupInfo(String systemType, int rghtGrpNo){
    	
    	MybatisInput input = MybatisInput.of().add("systemType", systemType).add("cmnGrpCd", CommonCodeConstants.KIND_CODE_GROUP_CODE).add("rghtGrpNo", rghtGrpNo);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.selectAllMenuAuthoritySetupInfo", input);
    }
    
    /**
     * 그룹에 메뉴권한 삭제(수정으로 삭제)
     * @param menuCd
     * @param rghtGrpCd
     * @param rghtKind
     * @return
     */
	public void deleteAuthorityGroupRghtKind(String menuCd, String rghtGrpCd, String rghtKind) {

        UserAuth userAuth = UserAuth.get(true);
        if(userAuth == null){
            throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
        }
        MybatisInput input = MybatisInput.of().add("menuCd", menuCd).add("rghtGrpCd", rghtGrpCd).add("rghtKind", rghtKind).add("rgstrId", userAuth.getUsrId());
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.deleteAuthorityGroupRghtKind", input);
	}
	
	/**
     * 그룹에 메뉴권한 삭제(그리드에서 다중 또는 단건 삭제)
     * @param rghtGrpNo
     * @return
     */
	public void deleteAuthorityGroupRghtKind(int rghtGrpNo) {
        UserAuth userAuth = UserAuth.get(true);
        if(userAuth == null){
            throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
        }
		MybatisInput input = MybatisInput.of().add("rghtGrpNo", rghtGrpNo).add("rgstrId", userAuth.getUsrId());
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.gridDeleteAuthorityGroupRghtKind", input);
	}

    public List<SmAuthorityGroupUsers> getAuthorityGroupUserListByRghtGrpCd(String rghtGrpCd) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.authority_group.selectAuthorityGroupUserListByRghtGrpCd", rghtGrpCd);
    }


    /**권한 설정 로직 추가로 인한 신규 쿼리들 추가 End */

}
