package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress;

import kr.co.ideait.platform.gaiacairos.comp.progress.service.ActualtoplanService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@IsUser
@RestController
@RequestMapping("api/progress/actualtoplan")
public class ActualToPlanApiController extends AbstractController {

    @Autowired
    ActualtoplanService actualservice;

    /**
     * 계획대비실적 차트
     * 
     * @param cntrctChgId
     */
    @PostMapping("/activityList")
    @Description(name = "계획대비실적 차트", description = "계획대비실적 차트 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getActivityList(CommonReqVo commonReqVo, @RequestBody Map<String, String> param) {
        String cntrctChgId = param.get("cntrctChgId");
        return Result.ok().put("activityList", actualservice.getActivityList(cntrctChgId));
    }

    /**
     * 계획대비실적 차트 연도
     * 
     * @param cntrctChgId
     */
    @PostMapping("/chartYear")
    @Description(name = "계획대비실적 차트 연도", description = "계획대비실적 차트 연도 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getYear(CommonReqVo commonReqVo, @RequestBody Map<String, String> param) {
        String cntrctChgId = param.get("cntrctChgId");
        return Result.ok().put("year", actualservice.getYear(cntrctChgId));
    }

    /**
     * 일정, 그래프 데이터
     */
    @PostMapping("/actualtoplanData")
    @Description(name = "일정, 그래프 데이터", description = "계획대비실적 일정, 그래프 데이터", type = Description.TYPE.MEHTOD)
    public Result getData(CommonReqVo commonReqVo, @RequestBody Map<String, String> param) {

        return Result.ok().put("data", actualservice.getData(param));
    }
}
