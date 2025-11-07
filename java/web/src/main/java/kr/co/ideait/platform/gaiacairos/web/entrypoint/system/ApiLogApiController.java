package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;


import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogMybatisParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/system/api-log")
public class ApiLogApiController extends AbstractController {

    @Autowired
    ApiLogForm apiLogForm;

    @Autowired
    ApiLogDto apiLogDto;

    /**
     * API 로그 목록 조회 (검색)
     */
    @GetMapping("/api-log-list")
    @Description(name = "API 로그 목록 조회", description = "API 로그 목록 조회 - tuiGrid 반환 구조에 맞춰 반환.", type = Description.TYPE.MEHTOD)
    public GridResult getApiLogList(CommonReqVo commonReqVo, @Valid ApiLogForm.ApiLogListGet apiLogListGet,
                                    @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        ApiLogMybatisParam.ApiLogListInput input = apiLogForm.toApiLogListInput(apiLogListGet);
        if(langInfo != null && "ko-KR".equals(langInfo)){
            langInfo = "ko";
        }

        input.setLang(langInfo);

        return GridResult.ok(systemLogComponent.getApiLogList(input)
                .map(apiLogDto::fromSmApiLogOutput));
    }

    /**
     * API 로그 상세 화면
     */
    @GetMapping("/{apiLogNo}")
    @Description(name = "API 로그 상세 화면", description = "API 로그 상세 화면", type = Description.TYPE.MEHTOD)
    public Result getDetailApiLog(CommonReqVo commonReqVo, @PathVariable("apiLogNo") Long apiLogNo) {
        return Result.ok().put("apiLog", systemLogComponent.getApiLog(apiLogNo));
    }
}