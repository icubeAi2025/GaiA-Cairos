package kr.co.ideait.platform.gaiacairos.comp.system.service;

import kr.co.ideait.iframework.MaskingUtil;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmAuthorityGroupRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmOrganizationRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmUserInfoRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserMybatisParam.SyncUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserMybatisParam.UserInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserMybatisParam.UserListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserMybatisParam.UserOutput;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class UserService extends AbstractGaiaCairosService {

    @Autowired
    SmUserInfoRepository smUserInfoRepository;

    @Autowired
    SmAuthorityGroupRepository smAuthorityGroupRepository;

    @Autowired
    SmOrganizationRepository smOrganizationRepository;

    @Autowired
    @Qualifier("pccsSqlSessionTemplate")
    SqlSessionTemplate oraMybatisSession;

    @Autowired
    User user;

    String cmnGrpCdRatng = CommonCodeConstants.RANK_CODE_GROUP_CODE;
    String cmnGrpCdPstn = CommonCodeConstants.PSTN_CODE_GROUP_CODE;
	
	/**
	 * ORACLE 사용자 정보 조회
	 * 
	 * @param 
	 * @return List
	 * @throws
	 */
	public List<Map<String, Object>> getOracleUserList() {
		return oraMybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.selectOracleUserList");
	}
	
	/**
	 * ORACLE 사용자 정보 등록
	 * 
	 * @param  List
	 * @return int
	 * @throws
	 */
	@Transactional
	public int setOracleUserList(List<Map<String, Object>> param) {
		int resultCount = 0;
		var partedList = ListUtils.partition(param, 500);
		for (List<Map<String, Object>> paramList : partedList) {
			mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.userApiInsertAndUpdate", paramList);
			resultCount++;
		}
			
		return resultCount;
	}

    public HashMap<String, Object> getUserList(UserListInput userListInput, boolean isAdmin) {
        HashMap<String, Object> result = new HashMap<>();

        userListInput.setCmnGrpCdRatng(cmnGrpCdRatng);
        userListInput.setCmnGrpCdPstn(cmnGrpCdPstn);

        List<UserOutput> userOutputs = isAdmin ?
                mybatisSession .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.getAdminUserList", userListInput) :
                mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.getUserList", userListInput);

        result.put("userOutputs", userOutputs);

        // 250404 0 인경우 Index 0 out of bounds 예방
        long totalCount = userOutputs.size();
        if (totalCount != 0) {
            totalCount = userOutputs.get(0).getCnt();
        }
        result.put("totalCount", totalCount);

        return result;
    }

    public boolean existLoginId(String loginId) {
        return smUserInfoRepository.existsByLoginId(loginId);
    }

    public UserOutput getUserDetail(String usrId) {
        UserInput userInput = new UserInput();
        userInput.setUsrId(usrId);
        userInput.setCmnGrpCdRatng(cmnGrpCdRatng);
        userInput.setCmnGrpCdPstn(cmnGrpCdPstn);

        UserOutput userOutput = mybatisSession
                .selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.getUserDetail", userInput);

        return userOutput;
    }

    public SmUserInfo getUser(String usrId) {
        return smUserInfoRepository.findById(usrId).orElse(null);
    }

    @Transactional
    public SmUserInfo createUser(SmUserInfo user) {
        user.setUsrId(UUID.randomUUID().toString().substring(0, 20));
        user.setMngDiv("N");
        user.setDltYn("N");
        if (user.getUseYn().equals("N")) {
            smOrganizationRepository.updateFlagByUsrId("L", user.getUsrId());
        } else if (user.getUseYn().equals("Y")) {
            smOrganizationRepository.updateFlagByUsrId("I", user.getUsrId());
        }
        return smUserInfoRepository.save(user);
    }

//    [dsjung] 2025-07-25 재사용을 위해 값이 존재하는 필드들만 수정되도록 향상
//    @@@@@@@@@ 아래 위치에서 사용 중 @@@@@@@@@
//    MyPageComponent.updateProfile
    @Transactional
    public int updateUser(SmUserInfo user, String chgId) {
        log.info("user : = {}", user.toString());        
        UserMybatisParam.UpdateUserInfo updateUserInfo = new UserMybatisParam.UpdateUserInfo();

        String flag = ("N".equals(user.getUseYn()))?"L":"I";
        updateUserInfo.setFlag(flag);
        updateUserInfo.setUsrId(user.getUsrId());
        updateUserInfo.setChgId(chgId);

        user.setChgId(chgId);

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.updateSmOrganizationInfo", updateUserInfo);
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.updateUserInfo", user);
    }

    @Transactional
    public void deleteUser(List<String> userIdList) {
        userIdList.forEach(id -> {
            SmUserInfo user = smUserInfoRepository.findById(id).orElse(null);
            if (user != null) {
                smUserInfoRepository.updateDelete(user);
            }
        });
    }

    @Transactional
    public void deleteUser(List<String> userIdList, String usrId) {
        userIdList.forEach(id -> {
            SmUserInfo user = smUserInfoRepository.findById(id).orElse(null);
            if (user != null) {
                smUserInfoRepository.updateDelete(user, usrId);
            }
        });
    }

    public List<User.AccessAuthority> getAccessAuthorities(String userId) {

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.getUserAccessAuthority", userId);
    }

    public List<User.AccessMenuAuthority> getAccessMenuAuthorities(String userId) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.selectAccessMenuAuthotiry", userId);
    }

    /**
     * 3th party user 머지
     */
    public SmUserInfo mergeUser(User.PortalMe me) {
        SmUserInfo smUserInfo = smUserInfoRepository.findById(me.getUsr_id()).orElse(null);
        if (smUserInfo != null) {
            user.updateSmUserInfo(me, smUserInfo);
            smUserInfo = smUserInfoRepository.save(smUserInfo);
        } else {
            smUserInfo = user.toSmUserInfo(me);
            smUserInfo.setUseYn("Y");
            smUserInfo.setDltYn("N");
            smUserInfo = smUserInfoRepository.save(smUserInfo);
        }
        return smUserInfo;
    }
    
    
    
    
    
    
    
  

    /**
     * 동기화 사용자 정보 목록 조회
     *
     * [변경사항]
     * 20250422 OFFSET ... FETCH NEXT ... 문법 Oracle 11g 미지원(12c 이상) 으로 인한
     * 페이징처리 방식 서크쿼리 활용으로 변경
     *
     * @param syncUserListInput {WbsGenUserListInput}
     * @return
     */
    public Page<UserOutput> selectUserSyncList(UserMybatisParam.SyncUserListInput syncUserListInput) {

        String queryString = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.selectOracleUserListDev";
        List<UserOutput> userOutputs;

        //PGAIA인 경우 portal 스키마의 co_user_info 조회
        if(PlatformType.PGAIA.getName().equals(platform)){
            queryString = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.selectPortalUserListDev";
            userOutputs = mybatisSession.selectList(queryString, syncUserListInput);
        }else{
            // 리스트 조회
            userOutputs = oraMybatisSession.selectList(queryString, syncUserListInput);
        }

        if (userOutputs == null) userOutputs = java.util.Collections.emptyList();

        // 마스킹 처리
        for (UserOutput user : userOutputs) {
            user.setLoginId(MaskingUtil.maskEmail(user.getLoginId()));
            user.setUsrNm(MaskingUtil.maskName(user.getUsrNm()));
            user.setEmailAdrs(MaskingUtil.maskEmail(user.getEmailAdrs()));
            user.setPhoneNo(MaskingUtil.maskPhoneNumber(user.getPhoneNo()));
        }

        long rowNum = 0;
        if (!userOutputs.isEmpty()) {
            rowNum = userOutputs.getFirst().getTotalNum();
        }

        log.debug("조회건수 : {}" ,rowNum);

        return new PageImpl<>(
                userOutputs,
                syncUserListInput.getPageable(),
                rowNum
        );
    }

    public List<SyncUserInfo> selectSyncUserInfoList(List<Map<String, Object>> userIdList, String platform){
        List<SyncUserInfo> syncUserInfoList = null;
        if("PGAIA".equals(platform)) {
            syncUserInfoList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.getSelectedNcpUserList", Map.of("userList", userIdList));
        }else {
            syncUserInfoList = oraMybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.getSelectedOciUserList", Map.of("userList", userIdList));
        }

        if(syncUserInfoList == null){
            syncUserInfoList = Collections.emptyList();
        }

        return syncUserInfoList;
    }

    /**
     * 사용자 정보 등록 (동기화 입력)
     *
     * @param userInfoList
     * @return List<SyncUserInfo>
     * @throws
     */
    public List<SyncUserInfo> insertSyncUserInfo(List<SyncUserInfo> syncUserInfo, String platform) {

        // 01. 선택 ID 를 기준으로 사용자 정보를 가져온다.    	
    	List<SyncUserInfo> apiUserInfo;

        // 02. Platform(OCI) 유저 정보를 전달, 사용자 정보에 등록한다.
        String syncRgstrId = Objects.requireNonNull(UserAuth.get(true)).getUsrId();        
        syncUserInfo.forEach(user -> {user.setRgstrId(syncRgstrId);});
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.insertSyncUserInfo", Map.of("userList", syncUserInfo));
        
        if("PGAIA".equals(platform)) {
        	apiUserInfo = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.selectedApiOciUserList", Map.of("userList", syncUserInfo));
    	}else {
    		apiUserInfo = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.selectedApiNcpUserList", Map.of("userList", syncUserInfo));
    	}

        if(apiUserInfo == null) {
            throw new GaiaBizException(ErrorType.DATABSE_ERROR, "사용자 정보를 찾을 수 없습니다.(데이터 베이스 오류)");
        }

        apiUserInfo.forEach(user -> {user.setRgstrId(syncRgstrId);});

        return apiUserInfo;
    }

    /**
     * 사용자 정보 등록 (API 연동 입력)
     *
     * @param userInfoList
     * @return int
     * @throws
     */
    public int insertUserInfoCommon(List<SyncUserInfo> apiUserInfo) {    	
    	return mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user.insertApiUserInfo", Map.of("userList", apiUserInfo));
    }
}
