package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;


import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog.UserLogDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog.UserLogForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog.UserLogMybatisParam.UserLogListInput;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
@RequestMapping("/api/system/user-log")
public class UserLogApiController extends AbstractController {

    @Autowired
    UserLogForm userLogForm;

    @Autowired
    UserLogDto userLogDto;

    /**
     * 사용자 로그 목록 조회 (검색)
     */
    @GetMapping("/user-log-list")
    @Description(name = "사용자 로그 목록 조회", description = "사용자 로그 목록 조회 - tuiGrid 반환 구조에 맞춰 반환.", type = Description.TYPE.MEHTOD)
    public GridResult getUserLogList(CommonReqVo commonReqVo, @Valid UserLogForm.UserLogListGet userLogListGet,
                                     @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("사용자 로그 목록 조회");
        systemLogComponent.addUserLog(userLog);

        UserLogListInput input = userLogForm.toUserLogListInput(userLogListGet);
        if(langInfo != null && "ko-KR".equals(langInfo)){
            langInfo = "ko";
        }

        input.setLang(langInfo);

        return GridResult.ok(systemLogComponent.getUserLogList(input)
                .map(userLogDto::fromSmUserLogOutput));
    }


    /**
     * 사용자 로그 상세 화면
     */
    @GetMapping("/{logNo}")
    @Description(name = "사용자 로그 상세 화면", description = "사용자 로그 상세 화면", type = Description.TYPE.MEHTOD)
    public Result getUserLog(CommonReqVo commonReqVo, @PathVariable("logNo") Long logNo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("사용자 로그 상세 화면");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("userLog", systemLogComponent.getUserLog(logNo));
    }
}
