package kr.co.ideait.platform.gaiacairos.web.entrypoint.dashboard;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 포탈 로그인 페이지 (테스트용/ 삭제예정)
 */
@Slf4j
@Controller
@RequestMapping("/dashboard")
public class DashboardPageController extends AbstractController {

    /**
     * 메인대시보드
     */
    @GetMapping("")
    @Description(name = "메인대시보드 화면", description = "GAIA의 종합 데이터 조회 화면 반환", type = Description.TYPE.MEHTOD)
    public String portalHome(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("메인대시보드 화면 접속");

        systemLogComponent.addUserLog(userLog);

        return "page/dashboard/main_home";
    }

    /**
     * 종합대시보드1
     */
    @GetMapping("/dashBoard_type01")
    @Description(name = "메인대시보드 화면", description = "프로젝트의 종합 데이터 조회 화면 반환", type = Description.TYPE.MEHTOD)
    public String totalDashBoard(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("종합대시보드 화면 접속");

        systemLogComponent.addUserLog(userLog);

        return "page/dashboard/dashBoard_type01";
    }

    /**
     * 친환경
     */
    @GetMapping("/eco-friendly")
    @Description(name = "메인대시보드 화면 > 친환경 목록 새창", description = "메인대시보드 화면 > 친환경 목록 새창 페이지", type = Description.TYPE.MEHTOD)
    public String ecoFriendlyMove(CommonReqVo commonReqVo, HttpServletRequest request,
            Model model) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("메인대시보드 친환경 목록 화면 접속");

        systemLogComponent.addUserLog(userLog);

        String[] userParam = commonReqVo.getUserParam();

        String viewType = "DEFAULT";

        if (userParam[1].equals("ADMIN") || userParam[2].equals("PGAIA") || userParam[2].equals("GAIA")) {
            viewType = "ADMIN";
        }

        model.addAttribute("viewType", viewType);

        return "page/dashboard/ecoFriendly";
    }

}
