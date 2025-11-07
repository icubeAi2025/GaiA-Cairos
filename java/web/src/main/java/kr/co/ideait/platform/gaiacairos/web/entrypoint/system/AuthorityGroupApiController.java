package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.system.AuthorityGroupComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.AuthorityGroupService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAuthorityGroupUsers;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup.AuthorityGroupMybatisParam.AuthorityGroupUserInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup.AuthorityGroupDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.authoritygroup.AuthorityGroupForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@IsUser
@RestController
@RequestMapping("/api/system/authority-group")
public class AuthorityGroupApiController extends AbstractController {

    @Autowired
    AuthorityGroupService authorityGroupService;
    
    @Autowired
    AuthorityGroupComponent authorityGroupComponent;

    @Autowired
    AuthorityGroupForm authorityGroupForm;

    @Autowired
    AuthorityGroupDto authorityGroupDto;

    /**
     * 권한 그룹 리스트 조회
     */
//     @RequiredProjectSelect(superChangeable = false)
    @GetMapping("/list")
    @Description(name = "권한그룹 리스트 조회", description = "권한그룹 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getAuthorityGroupList(CommonReqVo commonReqVo, @Valid AuthorityGroupForm.AuthorityGroupSearch authorityGroupSearch,
                                        @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo,
                                        UserAuth user) {

        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("권한그룹 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        MybatisInput mybatisInput = authorityGroupSearch.toMybatisInput();

        if(langInfo != null && "ko-KR".equals(langInfo)){
            langInfo = "ko";
        }

        mybatisInput.put("lang", langInfo);
		
        return Result.ok()
                .put("authorityGroupList",
                        authorityGroupService.getAuthorityGroupList(mybatisInput).stream()
                                .map(authorityGroupDto::fromAuthorityGroupMybatis));
    }

    /**
     * 권한그룹 코드 중복 체크
     */
//     @RequiredProjectSelect(superChangeable = false)
    @GetMapping("/exist/{authorityGroupCode}")
    @Description(name = "권한그룹 코드 중복 체크", description = "권한그룹 코드 중복 체크 (true / false)", type = Description.TYPE.MEHTOD)
    public Result existAuthorityGroup(CommonReqVo commonReqVo, @PathVariable("authorityGroupCode") String authorityGroupCode,
            @RequestParam("cntrctNo") String cntrctNo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("권한그룹 코드 중복 체크");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("exist", authorityGroupService.existAuthorityGroup(cntrctNo,
                        authorityGroupCode));
    }

    /**
     * 권한그룹 조회
     */
//     @RequiredProjectSelect(superChangeable = false)
    @GetMapping("/{rghtGrpNo}")
    @Description(name = "권한그룹 조회", description = "권한그룹 조회", type = Description.TYPE.MEHTOD)
    public Result getAuthorityGroup(CommonReqVo commonReqVo, @PathVariable("rghtGrpNo") Integer rghtGrpNo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("권한그룹 조회");
        systemLogComponent.addUserLog(userLog);
        
        String systemType = platform.toUpperCase().equals("GAIA") ? "G" : "C";

        log.debug("=======================authorityGroup==================");
		log.debug("authorityGroup : {}",authorityGroupService.getAuthorityGroup(rghtGrpNo));
		log.debug("=========================================");
        return Result.ok()
                .put("authorityGroup",
                        authorityGroupService.getAuthorityGroup(rghtGrpNo)
                                .map(authorityGroupDto::fromSmAuthorityGroup))
                .put("allMenuAuthoritySetupInfo", authorityGroupService.selectAllMenuAuthoritySetupInfo(systemType, rghtGrpNo));
    }

    /**
     * 권한그룹 등록
     */
//     @RequiredProjectSelect(superChangeable = false)
    @PostMapping("/create")
    @Description(name = "권한그룹 등록", description = "권한그룹 등록", type = Description.TYPE.MEHTOD)
    public Result createAuthorityGroup(CommonReqVo commonReqVo, @RequestBody @Valid AuthorityGroupForm.AuthorityGroup authorityGroup) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("권한그룹 등록");
        systemLogComponent.addUserLog(userLog);
        
        authorityGroupComponent.createAuthorityGroup(authorityGroup);

        return Result.ok();
        //저장 후 URL 이동하는데 리턴값을 보내는 이유???? 확인 필요
//        return Result.ok()
//                .put("authorityGroup",
//                        authorityGroupService.createAuthorityGroup(smAuthorityGroup)
//                                .map(authorityGroupDto::fromSmAuthorityGroup));
    }

    /**
     * 권한그룹 수정
     */
//     @RequiredProjectSelect(superChangeable = false)
    @PostMapping("/update")
    @Description(name = "권한그룹 수정", description = "권한그룹 수정", type = Description.TYPE.MEHTOD)
    public Result updateAuthorityGroup(CommonReqVo commonReqVo, 
            @RequestBody @Valid AuthorityGroupForm.AuthorityGroupUpdate authorityGroupUpdate) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("권한그룹 수정");
        systemLogComponent.addUserLog(userLog);
        
        authorityGroupComponent.updateAuthorityGroup(authorityGroupUpdate);
        
        return Result.ok();

    }

    /**
     * 권한그룹 삭제
     */
//     @RequiredProjectSelect(superChangeable = false)
    @PostMapping("/delete")
    @Description(name = "권한그룹 삭제", description = "권한그룹 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteAuthorityGroup(CommonReqVo commonReqVo, @RequestBody @Valid AuthorityGroupForm.AuthorityGroupNoList authorityGroupNoList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("권한그룹 삭제");
        systemLogComponent.addUserLog(userLog);

        if(authorityGroupNoList.getAuthorityGroupNoList().size() <= 0){
                throw new GaiaBizException(ErrorType.NO_DATA, "Not Found Authority Group No List");
        }
        
        authorityGroupComponent.deleteAuthorityGroup(authorityGroupNoList);
        
        return Result.ok();
    }

    /**
     * 권한그룹 사용자 조회
     */
    @GetMapping("/users/list")
    @Description(name = "권한그룹 사용자 조회", description = "해당 권한 그룹에 속한 권한그룹 사용자 조회(부서 or 사용자)", type = Description.TYPE.MEHTOD)
    public GridResult getAuthorityGroupUserList(CommonReqVo commonReqVo, 
            @Valid AuthorityGroupForm.AuthorityGroupUserGet authorityGroupUserGet,
            @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("권한그룹 사용자 조회");
        systemLogComponent.addUserLog(userLog);

        AuthorityGroupUserInput input = authorityGroupForm.toAuthorityGroupUserInput(authorityGroupUserGet);

        if(langInfo != null && "ko-KR".equals(langInfo)){
                langInfo = "ko";
        }

        input.setLang(langInfo);

        return GridResult.ok(authorityGroupService.getAuthorityGroupUserList(input));
    }

    /**
     * 권한그룹 사용자 조회(Grid) => 메뉴 권한 관리에서 사용.
     */
    @GetMapping("/users/grid-list")
    @Description(name = "권한그룹 사용자 조회", description = "해당 권한 그룹에 속한 권한그룹 사용자 조회(부서 or 사용자) - tuiGrid 반환 구조에 맞춰서 반환.", type = Description.TYPE.MEHTOD)
    public GridResult getAuthorityGroupUserGridList(CommonReqVo commonReqVo, 
            @Valid AuthorityGroupForm.AuthorityGroupUserGridGet authorityGroupUserGet,
            @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("권한그룹 사용자 조회");
        systemLogComponent.addUserLog(userLog);

        AuthorityGroupUserInput input = authorityGroupForm.toAuthorityGroupUserInput(authorityGroupUserGet);
        
        if(langInfo != null && "ko-KR".equals(langInfo)){
            langInfo = "ko";
        }
        
        input.setLang(langInfo);

        return GridResult.ok(authorityGroupService.getAuthorityGroupUserGridList(input));
    }

    /**
     * 권한그룹 사용자 등록
     */
    @PostMapping("/users/create")
    @Description(name = "권한그룹 사용자 등록", description = "권한그룹 사용자 등록", type = Description.TYPE.MEHTOD)
    public Result createAuthorityGroupUsers(CommonReqVo commonReqVo, 
            @RequestBody @Valid AuthorityGroupForm.AuthorityGroupUserList authorityGroupUserList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("권한그룹 사용자 등록");
        systemLogComponent.addUserLog(userLog);

        if(authorityGroupUserList.getAuthorityGroupUserList().size() <= 0){
			return Result.nok(ErrorType.NO_DATA);
		}
        List<SmAuthorityGroupUsers> groupUsers = authorityGroupForm
                .toAuthorityGroupUsersList(authorityGroupUserList.getAuthorityGroupUserList());
        return Result.ok()
                .put("authorityGroupUsers",
                        authorityGroupService.createAuthorityGroupUsers(groupUsers).stream()
                                .map(authorityGroupDto::fromSmAuthorityGroupUsers));
    }

    /**
     * 권한그룹 사용자 삭제
     */
    @PostMapping("/users/delete")
    @Description(name = "권한그룹 사용자 삭제", description = "권한그룹 사용자 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteAuthorityGroupUsers(CommonReqVo commonReqVo, 
            @RequestBody @Valid AuthorityGroupForm.AuthorityGroupUserNoList authorityGroupUserNoList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("권한그룹 사용자 삭제");
        systemLogComponent.addUserLog(userLog);

        authorityGroupService.deleteAuthorityGroupUsers(
                authorityGroupUserNoList.getAuthorityGroupUserNoList());
        return Result.ok();
    }
    
    
    /**권한 설정 로직 추가로 인한 신규 쿼리들 추가 Start */
    
    /**
     * 권한 그룹 추가시 전체 메뉴의 권한 설정 정보 가져오기
     */
    @GetMapping("/all-menu-authority-info")
    @Description(name = "전메뉴권한설정 조회", description = "전체 메뉴의 설정된 권한 정보를 조회한다.", type = Description.TYPE.MEHTOD)
    public Result getAllMenuAuthorityInfo(CommonReqVo commonReqVo, @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("전메뉴권한설정 조회");
        systemLogComponent.addUserLog(userLog);
        
        String systemType = platform.toUpperCase().equals("GAIA") ? "G" : "C";

        return Result.ok().put("allMenuAuthorityInfo", authorityGroupService.selectAllMenuAuthorityInfo(systemType));
    }

    /**권한 설정 로직 추가로 인한 신규 쿼리들 추가 End */

}
