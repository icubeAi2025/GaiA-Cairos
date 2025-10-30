package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/progress/progressstatus")
public class ProgressstatustPageController extends AbstractController {

    /**
     * 공정현황
     */
    @GetMapping("")
    @Description(name = "공정현황 화면", description = "공정현황 화면", type = Description.TYPE.MEHTOD)
    public String getMain(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("공정현황 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/progress/progressstatus/progressstatus";
    }
}

