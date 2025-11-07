package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;


import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.util.BizServiceInvoker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/system/api-log")
public class ApiLogApiController extends AbstractController {

    @Autowired
    ApiLogForm apiLogForm;

    @Autowired
    ApiLogDto apiLogDto;

    @Autowired
    private BizServiceInvoker bizServiceInvoker;

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

    /**
     * 실패 요청 재시도
     */
    @PostMapping("/{apiLogNo}/fail-request-retry")
    @Description(name = "실패 요청 재시도", description = "실패 요청 재시도", type = Description.TYPE.MEHTOD)
    public Result failRequestRetry(@PathVariable("apiLogNo") Long apiLogNo, CommonReqVo commonReqVo, HttpServletRequest request) {
        Log.SmApiLogDto logDto = systemLogComponent.getApiLog(apiLogNo);

        if (logDto == null) {
            return Result.nok(ErrorType.NO_DATA);
        }

        if (logDto.getResultCode() == 200) {
            return Result.nok(ErrorType.INVAILD_INPUT_DATA);
        }

        if (StringUtils.isEmpty(logDto.getInvokeTranId())) {
            return Result.nok(ErrorType.INVAILD_INPUT_DATA);
        }

        Map<String, Object> result = bizServiceInvoker.invoke(logDto.getInvokeTranId(), Maps.newHashMap());

        if (!"00".equals(result.get("resultCode"))) {
            return Result.nok(ErrorType.INVAILD_INPUT_DATA);
        }

        return Result.ok(result);
    }
}