package kr.co.ideait.platform.gaiacairos.comp.system;

import com.google.common.collect.Maps;

import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.MypageService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.UserService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.ProfileDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MyPageComponent extends AbstractComponent {

    @Autowired
    private MypageService mypageService;

    @Autowired
	PortalService portalService;

    @Autowired
    UserService userService;


    public Result getLoginUserInfo(CommonReqVo commonReqVo) {
        Result result = new Result();

        String loginId = commonReqVo.getLoginId();
//        String userId = commonReqVo.getUserId();

        UserMybatisParam.UserOutput user = mypageService.getLoginUserInfo(loginId);

        if (user != null) {
            result.put("user",user);

            List<ProfileDto.Profile> profileList = mypageService.selectProfileFileList(user.getUsrId());

            for (ProfileDto.Profile file : profileList) {
                if ("Y".equals(file.getStampYn())) {
                    result.put("stamp",file);
                }
                else if ("N".equals(file.getStampYn())) {
                    result.put("logo",file);
                }
            }
        }

        result.put("deptAndAuthInfo", mypageService.getDeptAndAutorityGrpInfo(loginId, platform));

        PortalMybatisParam.MainComprehensiveProjectInput param = new PortalMybatisParam.MainComprehensiveProjectInput();
        param.setLoginId(UserAuth.get(true).getLogin_Id());
        param.setUsrId(UserAuth.get(true).getUsrId());
        param.setConPstatsCd(CommonCodeConstants.CON_PSTATS_CODE_GROUP_CODE);
        param.setPjtType(platform.toUpperCase());
        param.setPjtDiv(platform.toUpperCase().substring(0, 1));
        result.put("cntrctList",mypageService.selectNormalMainComprehensiveProjectList(param,true));

        return result;
    }

    @Transactional
    public Map<String, Object> updateProfile(SmUserInfo user, String userId, MultipartFile stampFile, boolean flagOfDeleteStamp, CommonReqVo commonReqVo) {
        Map<String, Object> invokeParams = Maps.newHashMap();
        Map<String, Object> fileMap = Maps.newHashMap();
        Map<String,Object> result = Maps.newHashMap();

        user.setUsrId(userId);
        user.setUseYn("Y");

        if(userService.updateUser(user,userId) == 1){
            result.put("updateUser", "성공");
            user.setChgId(userId);
            invokeParams.put("user", user);
            if(stampFile != null){
                ProfileDto.Profile savedFile = mypageService.updateProfileFile(stampFile,userId,userId,null);
                if(savedFile != null){
                    result.put("stampFile", savedFile);
                    invokeParams.put("savedFile",savedFile);
                    fileMap.put("stampFile", stampFile);
                }
                else{
                    result.put("FAIL", "직인 데이터 수정 실패");
                }
            }
            else{
                if(flagOfDeleteStamp){
                    mypageService.deleteProfileFile(0,userId,userId);
                }
            }
        }
        else{
            result.put("FAIL", "유저 데이터 수정 실패");
        }

        if ("Y".equals(commonReqVo.getApiYn())) {
            Map response = Maps.newHashMap();

            if (PlatformType.PGAIA.getName().equals(platform)){
                response = invokePgaia2Cairos("GACA_MY001", invokeParams, fileMap);
            }
            else if(PlatformType.GAIA.getName().equals(platform) || PlatformType.CAIROS.getName().equals(platform)){
                response = invokeCairos2Pgaia("CAGA_MY001", invokeParams, fileMap);
            }

            if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
                throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
            }
        }

        return result;
    }


    public HashMap<String, Object> getFileResource(int fileNo) throws IOException {
        return mypageService.getFileResource(fileNo);
    }

    @Transactional
    public Map receiveInterfaceService(String transactionId, Map params) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "00");

        // 처리 대상 transactionId만 허용
        // 사용자 등록
        if ("GACA_MY001".equals(transactionId) || "CAGA_MY001".equals(transactionId)) {
            SmUserInfo user = objectMapper.convertValue(params.get("user"), SmUserInfo.class);
            MultipartFile stampFile = (MultipartFile)params.get("stampFile");
            ProfileDto.Profile savedFile = objectMapper.convertValue(params.get("savedFile"), ProfileDto.Profile.class);

            Map<String, Object> resp = Maps.newHashMap();

            if(userService.updateUser(user,user.getChgId()) == 1){
                if(stampFile != null){
                    ProfileDto.Profile profile = mypageService.updateProfileFile(stampFile,user.getUsrId(),user.getChgId(),savedFile);
                    if(profile == null){
                        resp.put("FAIL", "직인 데이터 수정 실패");
                    }
                }
            }
            else{
                result.put("FAIL", "유저 데이터 수정 실패");
            }

            String failMsg = (String)resp.get("FAIL");

            if (failMsg != null) {
                result.put("resultCode", "01");
                result.put("resultMsg", failMsg);
                return result;
            }

            return result;
        }

        return result;
    }
}
