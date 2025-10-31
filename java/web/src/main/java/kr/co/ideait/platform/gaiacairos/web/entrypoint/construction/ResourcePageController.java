package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;


import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@IsUser
@Controller
@RequestMapping("/construction")
public class ResourcePageController extends AbstractController {

    /**
     * 시공관리 > 자원투입현황 (Monthly)
     */
    @GetMapping("/resource")
    @Description(name = "자원 투입 현황 화면(Monthly)", description = "자원 투입 현황 화면(Monthly) 페이지", type = Description.TYPE.MEHTOD)
    public String resource(CommonReqVo commonReqVo) {
        // 공통로그 
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("자원투입현황 목록 화면 접속");
        systemLogComponent.addUserLog(userLog);
        
        return "page/construction/resource/resource";
    }

    /**
     * 시공관리 > 자원투입현황 (Monthly) 상세페이지
     */
    @GetMapping("/resource/detail/month")
    @Description(name = "자원 투입 현황 상세 화면(Monthly)", description = "자원 투입 현황 상세 화면(Monthly) 페이지", type = Description.TYPE.MEHTOD)
    public String getMonthlyDetailResource(CommonReqVo commonReqVo, 
            Model model,
            @RequestParam(value = "rsceTpCd") String rsceTpCd,
            @RequestParam(value = "cntrctNo") String cntrctNo,
            @RequestParam(value = "currentMonth") String currentMonth
    ) {
        // 공통로그 
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("자원투입현황 상세(월별) 화면 접속");
        systemLogComponent.addUserLog(userLog);
        
        return "page/construction/resource/resource_r_monthly";
    }

    /**
     * 시공관리 > 자원투입현황 (Daily) 상세페이지
     */
    @GetMapping("/resource/detail/day")
    @Description(name = "자원 투입 현황 상세 화면(Daily)", description = "자원 투입 현황 상세 화면(Daily) 페이지", type = Description.TYPE.MEHTOD)
    public String getResourceDaily(CommonReqVo commonReqVo) {
        // 공통로그 
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("자원투입현황 상세(일별) 화면 접속");
        systemLogComponent.addUserLog(userLog);
        
        return "page/construction/resource/resource_r_daily";
    }
}
