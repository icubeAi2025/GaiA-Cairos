package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress;

import kr.co.ideait.platform.gaiacairos.comp.progress.service.ProgressstatusService;
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
@RequestMapping("api/progress/progressstatus")
public class ProgressstatusApiController extends AbstractController {

    @Autowired
    ProgressstatusService progressService;

    /**
     * 공정률 현황 목록
     * @param cntrctChgId
     */
    @PostMapping("/ProcessRate")
    @Description(name = "공정률 현황 데이터 조회", description = "공정률 현황 데이터 전체 조회", type = Description.TYPE.MEHTOD)
    public Result getProcessRate(CommonReqVo commonReqVo, @RequestBody Map<String, String> param){
        String cntrctChgId = param.get("cntrctChgId");
        String weekTypeStr = param.get("weekType");    
        Integer weekType = Integer.parseInt(weekTypeStr);
        String startDate = param.get("startDate");    // 시작 날짜
        String endDate = param.get("endDate");
        return Result.ok().put("ProcessRate", progressService.getProcessRate(cntrctChgId, weekType, startDate, endDate));
    }

    /**
     * 주요작업 현황 목록
     * @param cntrctNo
     */
    @PostMapping("/activityList")
    @Description(name = "주요작업 현황 데이터 조회", description = "주요작업 현황 데이터 전체 조회", type = Description.TYPE.MEHTOD)
    public Result getActivityList(CommonReqVo commonReqVo, @RequestBody Map<String, String> param){
        String cntrctChgId = param.get("cntrctChgId");
        String weekTypeStr = param.get("weekType");      
        Integer weekType = Integer.parseInt(weekTypeStr);
        String startDate = param.get("startDate");    // 시작 날짜
        String endDate = param.get("endDate");
        // 20250227 - 정적검사 수정 [Bad practice] ES_COMPARING_STRINGS_WITH_EQ
        if("".equals(startDate) && "".equals(endDate)) {
            startDate = null;
            endDate = null;
        }
        return Result.ok().put("activityList", progressService.getActivityList(cntrctChgId, weekType, startDate, endDate));
    }
}
