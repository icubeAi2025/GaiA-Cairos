package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.system.UserComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.UserService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserMybatisParam.UserListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user.UserForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.iframework.annotation.Description;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/system/user")
public class UserApiController extends AbstractController {

    @Autowired
    UserService userService;

    @Autowired
    UserComponent userComponent;

    @Autowired
    UserForm userForm;

    @Autowired
    UserDto userDto;

    @GetMapping("/userInfo")
    @Description(name = "TODO", description = "",type = Description.TYPE.MEHTOD)
    public Result getUserInfo(CommonReqVo commonReqVo, HttpServletRequest request) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("TODO");
        systemLogComponent.addUserLog(userLog);

        // 시스템 타입 설정
        String newPjtType = "";
        if (platform.equals("gaia")) {
            newPjtType = "PGAIA";
        } else {
            newPjtType = platform.toUpperCase();
        }

        String[] param = commonReqVo.getUserParam();
        return Result.ok()
                .put("userId", param[0])
                .put("userType", param[1])
                .put("systemType", newPjtType);
    }

    /**
     * 사용자 목록
     */
    @GetMapping("/list")
    @Description(name = "사용자 목록 조회", description = "사용자 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getUserList(CommonReqVo commonReqVo, @Valid UserForm.UserListGet userListGet,
                                  HttpServletRequest request,
                                  @CookieValue(name = "lang", required = false) String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("사용자 목록 조회");
        systemLogComponent.addUserLog(userLog);


        String[] param = commonReqVo.getUserParam();
        UserListInput input = userForm.toUserListInput(userListGet);
        input.setUserId(param[0]);
        input.setUserType(param[1]);
        input.setSystemType(platform.toUpperCase());
        input.setLang(langInfo);
        return GridResult.ok(userComponent.getUserList(input,"ADMIN".equals(param[1])));
    }

    @GetMapping("/exist/{loginId}")
    @Description(name = "사용자 아이디 중복 체크", description = "사용자 추가시 아이디 중복 체크", type = Description.TYPE.MEHTOD)
    public Result checkLoginId(CommonReqVo commonReqVo, @PathVariable("loginId") String loginId) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("사용자 아이디 중복 체크");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("exist",
                userService.existLoginId(loginId));
    }

    /**
     * 사용자 추가
     */
    @Deprecated
    @PostMapping("/create")
    @Description(name = "사용자 추가", description = "사용자 추가", type = Description.TYPE.MEHTOD)
    public Result createUser(CommonReqVo commonReqVo, @Valid @RequestBody UserForm.UserCreate user) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("사용자 추가");
        systemLogComponent.addUserLog(userLog);

        SmUserInfo smUserInfo = userForm.toSmUserInfo(user);
        return Result.ok().put("user",
                userService.createUser(smUserInfo).map(userDto::toUserCreated));
    }

    /**
     * 사용자 수정
     */
    @PostMapping("/update")
    @Description(name = "사용자 수정", description = "사용자 수정", type = Description.TYPE.MEHTOD)
    public Result updateUser(CommonReqVo commonReqVo, @Valid @RequestPart("user") UserForm.UserUpdate user, @RequestPart(value = "stampFile", required = false) MultipartFile stampFile) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("사용자 수정");
        systemLogComponent.addUserLog(userLog);

        if(user.getUsrId() == null){
            throw new GaiaBizException(ErrorType.BAD_REQUEST, "사용자 정보가 존재하지 않습니다.");
        }

        Map response = userComponent.updateUserInfo(user, stampFile ,commonReqVo);
        String resultCd = response.get("resultCode").toString();

        if(!"00".equals(resultCd)){
            return Result.nok(ErrorType.INTERNAL_SERVER_ERROR, "서버 상의 이유로 삭제에 실패하였습니다.");
        }
        return Result.ok();
    }

    /**
     * 사용자 조회
     */
    @GetMapping("/{usrId}")
    @Description(name = "사용자 상세조회", description = "사용자 상세조회", type = Description.TYPE.MEHTOD)
    public Result getUser(CommonReqVo commonReqVo, @PathVariable("usrId") String usrId) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("사용자 상세조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("user", userComponent.getUserDetail(usrId));
    }

    /**
     * 사용자 삭제
     */
    @PostMapping("/delete")
    @Description(name = "사용자 삭제", description = "사용자 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteUser(CommonReqVo commonReqVo, @RequestBody @Valid UserForm.UserList userIdList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("사용자 삭제");
        systemLogComponent.addUserLog(userLog);

        Map response = userComponent.deleteUserInfo(userIdList.getUserList(), commonReqVo.getApiYn());
        String resultCd = response.get("resultCode").toString();

        if(!"00".equals(resultCd)){
            return Result.nok(ErrorType.INTERNAL_SERVER_ERROR, "서버 상의 이유로 삭제에 실패하였습니다.");
        }
        return Result.ok();
    }

    /**
     * 오라클 사용자와 정보 동기화
     *
     * [변경사항]
     * 20250424 동기화 -> 관리자 검색/추가 방식 변경으로 인한 deprecated
     */
    @GetMapping("/synUser")
    @Deprecated
    public strictfp Result syncOracleUser(CommonReqVo commonReqVo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("TODO");
        systemLogComponent.addUserLog(userLog);


        log.debug("사용자 정보 동기화 하러 오라클 조회!!");

        List<Map<String, Object>> oracleUserList = userService.getOracleUserList();

        log.debug("조회 카운트 : " + oracleUserList.size());

        int syncCount = 0;

        if (oracleUserList.size() > 0) {

            syncCount = oracleUserList.size();

            int insertResult = userService.setOracleUserList(oracleUserList);

            if (insertResult == (oracleUserList.size() + 500 - 1) / 500) {
                log.debug("사용자 정보 입력을 완료하였습니다.");
            }
            /**
             * for(int i = 0; i < oracleUserList.size(); i++){
             * 
             * Map<String, Object> oraUserInfo = oracleUserList.get(i);
             * 
             * log.debug("=============================" + i + "번째
             * 데이터================================");
             * log.debug("USR_ID : >>>>>>> " + oraUserInfo.get("USR_ID"));
             * log.debug("LOGIN_ID : >>>>>>> " + oraUserInfo.get("LOGIN_ID"));
             * log.debug("USR_NM : >>>>>>> " + oraUserInfo.get("USR_NM"));
             * log.debug("RATNG_CD : >>>>>>> " + oraUserInfo.get("RATNG_CD"));
             * log.debug("PSTN_CD : >>>>>>> " + oraUserInfo.get("PSTN_CD"));
             * log.debug("PSTN_NM : >>>>>>> " + oraUserInfo.get("PSTN_NM"));
             * log.debug("PHONE_NO : >>>>>>> " + oraUserInfo.get("PHONE_NO"));
             * log.debug("TEL_NO : >>>>>>> " + oraUserInfo.get("TEL_NO"));
             * log.debug("EMAIL_ADRS : >>>>>>> " + oraUserInfo.get("EMAIL_ADRS"));
             * log.debug("USE_YN : >>>>>>> " + oraUserInfo.get("USE_YN"));
             * log.debug("DLT_YN : >>>>>>> " + oraUserInfo.get("DLT_YN"));
             * log.debug("RGST_DT : >>>>>>> " + oraUserInfo.get("RGST_DT"));
             * log.debug("RGSTR_ID : >>>>>>> " + oraUserInfo.get("RGSTR_ID"));
             * log.debug("CHG_DT : >>>>>>> " + oraUserInfo.get("CHG_DT"));
             * log.debug("CHG_ID : >>>>>>> " + oraUserInfo.get("CHG_ID"));
             * log.debug("===============================================================================");
             * }
             */
        }
        return Result.ok().put("syncCount", syncCount);
    }

    /* 20250424 추가 */

    /**
     * 사용자 목록정보 가져오기 (From. EURECA)
     * @param syncUserListSearch
     * @return
     */
    @GetMapping("/get-sync-user-list")
    @Description(name = "(PLATFORM) 사용자 목록 조회", description = "PLATFORM에 등록된 사용자 정보목록을 조회한다.", type = Description.TYPE.MEHTOD)
    public GridResult getWbsGenUserSyncList(CommonReqVo commonReqVo, @Valid UserForm.SyncUserListSearch syncUserListSearch) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("PLATFORM 사용자 목록 조회");
        systemLogComponent.addUserLog(userLog);


        UserMybatisParam.SyncUserListInput syncUserListInput = userForm.toSyncUserListInput(syncUserListSearch);
        return GridResult.ok(userService.selectUserSyncList(syncUserListInput));
    }

    /**
     * 사용자 정보 등록 (From. PLATFORM)
     * @param syncUserIds
     * @param request
     * @return
     */
    @PostMapping("/user-sync-create")
    @Description(name = "(PLATFORM) 사용자 등록", description = "PLATFORM에 등록된 사용자 정보를 사용자 DB에 추가한다.", type = Description.TYPE.MEHTOD)
    public Result createSyncUserInfo(CommonReqVo commonReqVo, @RequestBody List<UserForm.SyncUserIds> syncUserIds, HttpServletRequest request) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("PLATFORM 사용자 등록");
        systemLogComponent.addUserLog(userLog);

        String result = userComponent.createUserInfo(syncUserIds, commonReqVo.getApiYn());

        if("success".equals(result)) {
            return Result.ok();
        }else{
            return Result.nok(ErrorType.INTERNAL_SERVER_ERROR, "저장에 실패하였습니다.");
        }

        // Object to Map
//        List<Map<String, Object>> userInfoList = EtcUtil.convertListToMapList(syncUserIds);
//        return Result.ok().put("insertCount",userService.insertSyncUserInfo(userInfoList));
    }

}
