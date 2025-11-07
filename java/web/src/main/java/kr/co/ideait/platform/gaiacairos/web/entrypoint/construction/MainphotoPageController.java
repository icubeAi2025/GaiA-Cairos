package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@IsUser
@Controller
@RequestMapping("/construction")
public class MainphotoPageController extends AbstractController {

    /**
     * 시공관리 > 주요 공정사진 Main
     */
    @GetMapping("/mainphoto")
    @Description(name = "주요 공정 사진 화면", description = "주요 공정 사진 화면 페이지", type = Description.TYPE.MEHTOD)
    public String getMainphoto(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("주요 공정 사진 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/mainphoto/mainphoto";
    }
}
