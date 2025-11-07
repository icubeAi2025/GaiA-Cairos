package kr.co.ideait.platform.gaiacairos.comp.system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.iframework.MaskingUtil;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CompanyService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.MypageService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.UserService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmCompany;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.ProfileDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.StringHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserComponent extends AbstractComponent {
    @Autowired
    private UserService userService;

    @Autowired
    private MypageService mypageService;

    @Autowired
    UserForm userForm;

    @Autowired
    private CompanyService companyService;

    /**
     * 사용자 목록 조회 (일반 사용자)
     * @param userListInput
     * @return
     */
    public Page<UserMybatisParam.UserOutput> getUserList(UserMybatisParam.UserListInput input, boolean isAdmin) {
        HashMap<String,Object> serviceResult = userService.getUserList(input,isAdmin);

        List<UserMybatisParam.UserOutput> userOutputs = objectMapper.convertValue(serviceResult.get("userOutputs"), new TypeReference<List<UserMybatisParam.UserOutput>>() {});
        long totalCount = (long)serviceResult.get("totalCount");

        // 20250404 순회 - 마스킹 처리 추가
        // 20250819 코드 이동
        for (UserMybatisParam.UserOutput user : userOutputs) {
            user.setLoginId(MaskingUtil.maskEmail(StringHelper.decodeSafeText(user.getLoginId())));
            user.setUsrNm(MaskingUtil.maskName(StringHelper.decodeSafeText(user.getUsrNm())));
            user.setEmailAdrs(MaskingUtil.maskEmail(StringHelper.decodeSafeText(user.getEmailAdrs())));
            user.setPhoneNo(MaskingUtil.maskPhoneNumber(user.getPhoneNo()));
        }

        return new PageImpl<>(userOutputs, input.getPageable(), totalCount);
    }

    /**
     * 사용자 상세 조회
     * @param usrId
     * @return
     */
    public UserMybatisParam.UserOutput getUserDetail(String usrId) {

        UserMybatisParam.UserOutput userOutput = userService.getUserDetail(usrId);

        // 직인 정보 추가
        List<ProfileDto.Profile> profileList = mypageService.selectProfileFileList(usrId);
        ProfileDto.Profile profile = null;
        if(!profileList.isEmpty()) {
            for(ProfileDto.Profile file : profileList) {
                if("Y".equals(file.getStampYn())){
                    profile = file;
                }
            }

            if(profile != null) {
                userOutput.setFileDiskNm(profile.getFileDiskNm());
                userOutput.setFileDiskPath(profile.getFileDiskPath());
                userOutput.setFileOrgNm(profile.getFileOrgNm());
            }
        }

        return userOutput;
    }

    /**
     * 사용자 등록
     * @param syncUserIds
     * @return
     */
    @Transactional
    public String createUserInfo(List<UserForm.SyncUserIds> syncUserIds, String apiYn) {
        Map response = Maps.newHashMap();
            
    	List<Map<String, Object>> userIdList = EtcUtil.convertListToMapList(syncUserIds);

        List<UserMybatisParam.SyncUserInfo> syncUserInfoList = userService.selectSyncUserInfoList(userIdList,platform.toUpperCase());
        syncUserInfoList.forEach(syncUserInfo -> {
            SmCompany smCompany = null;
            if(syncUserInfo.getOciUsrId() != null){
                smCompany = companyService.createCompany(syncUserInfo.getCorpNo(),"O");
            }
            else if(syncUserInfo.getNcpUsrId() != null){
                smCompany = companyService.createCompany(syncUserInfo.getCorpNo(),"N");
            }
            if(smCompany != null){
                syncUserInfo.setCorpNo(smCompany.getCorpNo());
            }
        });

        List<UserMybatisParam.SyncUserInfo> apiUserInfo = userService.insertSyncUserInfo(syncUserInfoList, platform.toUpperCase());

        Map<String, Object> invokeParams = Maps.newHashMap();
        log.info("apiUserInfo 쏩니다.>>>>>>>>>>>>> {} syncUserInfoList: {}", apiUserInfo, syncUserInfoList);
        invokeParams.put("syncUserInfoList", syncUserInfoList);
        if("Y".equals(apiYn)) {
        	if(PlatformType.PGAIA.getName().equals(platform)){
        		response = invokePgaia2Cairos("GACAM070202", invokeParams);
        	}else {
        		response = invokeCairos2Pgaia("CAGAM070202", invokeParams);
        	}

            if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
                throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
            }
        }

        return "success";
    }

    /**
     * 사용자 삭제
     * @param userIdList
     * @return
     */
    @Transactional
    public Map deleteUserInfo(List<String> userIdList, String apiYn) {
        Map response = Maps.newHashMap();
        response.put("resultCode", "00");


        userService.deleteUser(userIdList);

        Map<String, Object> invokeParams = Maps.newHashMap();
        invokeParams.put("userIdList", userIdList);
        invokeParams.put("userId", UserAuth.get(true).getUsrId());

        //API 통신
        if("Y".equals(apiYn)){
            if(PlatformType.GAIA.getName().equals(platform) || PlatformType.CAIROS.getName().equals(platform)){
                response = invokeCairos2Pgaia("CAGAM07020202", invokeParams);

                if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
                    throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
                }
            }
        }

        return response;
    }

    /**
     * 사용자 정보 수정
     * @param updateUser
     * @return
     */
    @Transactional
    public Map updateUserInfo(UserForm.UserUpdate updateUser, MultipartFile stampFile, CommonReqVo commonReqVo) {
        String userId = commonReqVo.getUserId();
        String apiYn = commonReqVo.getApiYn();
        Map response = Maps.newHashMap();
        response.put("resultCode", "00");

        // usrId로 사용자 정보 객체 조회
        SmUserInfo updatedUser = new SmUserInfo();
        userForm.updateSmUserInfo(updateUser, updatedUser);
        updatedUser.setChgId(userId);
        // 변경된 정보로 userInfo 업데이트
        int returnData = userService.updateUser(updatedUser, userId);

        ProfileDto.Profile savedFile = null;
        // 직인 변경 사항 있으면 처리
        if(stampFile != null) {
            savedFile = mypageService.updateProfileFile(stampFile, updateUser.getUsrId(), userId,null);
        }

        if(returnData == 0){
            response.put("resultCode", "01");
        }

        Map<String, Object> invokeParams = Maps.newHashMap();
        invokeParams.put("smUserInfo", updatedUser);
        invokeParams.put("savedFile", savedFile);

        Map<String, Object> fileMap = Maps.newHashMap();
        fileMap.put("stampFile", stampFile);



        //API 통신
        if("Y".equals(apiYn)){
            if(PlatformType.GAIA.getName().equals(platform) || PlatformType.CAIROS.getName().equals(platform)){
                response = invokeCairos2Pgaia("CAGAM07020203", invokeParams, fileMap);

                if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
                    throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
                }
            }
        }

        return response;
    }

    @Transactional
    public Map receiveInterfaceService(String transactionId, Map params) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "00");

        try{
            // 처리 대상 transactionId만 허용
            // 사용자 등록
            if ("GACAM070202".equals(transactionId) || "CAGAM070202".equals(transactionId)) {
                List<UserMybatisParam.SyncUserInfo> syncUserInfoList = objectMapper.convertValue(params.get("syncUserInfoList"), new TypeReference<List<UserMybatisParam.SyncUserInfo>>() {});
                syncUserInfoList.forEach(syncUserInfo -> {
                    SmCompany smCompany = null;
                    if(syncUserInfo.getOciUsrId() != null){
                        smCompany = companyService.createCompany(syncUserInfo.getCorpNo(),"O");
                    }
                    else if(syncUserInfo.getNcpUsrId() != null){
                        smCompany = companyService.createCompany(syncUserInfo.getCorpNo(),"N");
                    }

                    if(smCompany != null){
                        syncUserInfo.setCorpNo(smCompany.getCorpNo());
                    }
                });
                userService.insertSyncUserInfo(syncUserInfoList, platform.toUpperCase());

                result.put("resultCode", "01");
                result.put("resultMsg", "추가할 사용자가 존재하지 않습니다.");
                return result;
            }
            // 사용자 삭제
            else if("GACAM07020202".equals(transactionId) || "CAGAM07020202".equals(transactionId)){
                List<String> userIdList = objectMapper.convertValue(params.get("userIdList"), new TypeReference<List<String>>() {});
                String userId = MapUtils.getString(params, "userId");

                userService.deleteUser(userIdList, userId);
            }
            // 사용자 수정
            else if("CAGAM07020203".equals(transactionId)){
                SmUserInfo updatedUser = objectMapper.convertValue(params.get("smUserInfo"), new TypeReference<SmUserInfo>() {});
                ProfileDto.Profile savedFile = objectMapper.convertValue(params.get("savedFile"), ProfileDto.Profile.class);
                MultipartFile stampFile = (MultipartFile)params.get("stampFile");

                userService.updateUser(updatedUser,updatedUser.getChgId());

                // 직인 변경 사항 있으면 처리
                if(stampFile != null) {
                    mypageService.updateProfileFile(stampFile, updatedUser.getUsrId(),updatedUser.getChgId(), savedFile);
                }
            }
        } catch (GaiaBizException e) {
            log.error(e.getMessage(), e);
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }
}
