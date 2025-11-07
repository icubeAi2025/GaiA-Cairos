package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/construction/chiefinspectionreport")
public class ChiefInspectionReportPageController extends AbstractController {

    @Autowired
    PortalComponent portalComponent;

    @Value("${gaia.path.previewPath}")
    String imgDir;


    @GetMapping("")
    @Description(name = "책임감리일지 목록 화면", description = "책임감리일지 목록 화면 페이지", type = Description.TYPE.MEHTOD)
    public String getCheifInspectionReportList(CommonReqVo commonReqVo, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("책임감리일지 목록 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 메뉴 관리에서 등록한 버튼 아이디
        String[] btnId = { "CFINSP_D_01", "CFINSP_U_02", "CFINSP_C_03", "CFINSP_C_02" };

        // 버튼에 사용하는 클래스
        String[] btnClass = { "btn", "btn", "btn _fill", "btn" };

        // 버튼에 사용하는 함수
        String[] btnFun = { "onclick=\"reportGrid.deleteReport()\"", "onclick=\"reportGrid.updateReport()\"",
                "onclick=\"reportGrid.addReport()\"", "onclick=\"reportGrid.copyReport()\""};

        String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.035"};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

        model.addAttribute("btnHtml", btnHtml);
        boolean isDelAuth = btnHtml.contains("reportGrid.deleteReport()");
        boolean isAddAuth = btnHtml.contains("reportGrid.addReport()");
        model.addAttribute("isDelAuth", isDelAuth);
        model.addAttribute("isAddAuth", isAddAuth);
        model.addAttribute("imgDir", imgDir);

        return "page/construction/chiefinspectionreport/chiefinspectionreport";
    }

    @GetMapping("/detail")
    @Description(name = "책임감리일지 상세조회 화면", description = "책임감리일지 상세조회 화면 페이지", type = Description.TYPE.MEHTOD)
    public String getCheifInspectionReport(CommonReqVo commonReqVo, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("책임감리일지 상세조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if (!"old".equals(btnAuthType)) {
            // 메뉴 관리에서 등록한 버튼 아이디
            String[] btnId = { "CFINSP_U_02"};

            // 버튼에 사용하는 클래스
            String[] btnClass = {"btn"};

            // 버튼에 사용하는 함수
            String[] btnFun = { "onclick=\"page.update()\""};

            String[] btnMsg = { "btn.003"};

            String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

            model.addAttribute("btnHtml", btnHtml);
        }

        return "page/construction/chiefinspectionreport/chiefinspectionreport_r";
    }

    @GetMapping("/create")
    @Description(name = "책임감리일지 등록 화면", description = "책임감리일지 등록 화면 페이지", type = Description.TYPE.MEHTOD)
    public String createCheifInspectionReport(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("책임감리일지 추가 화면 접속");
        systemLogComponent.addUserLog(userLog);


        return "page/construction/chiefinspectionreport/chiefinspectionreport_c";
    }

    @GetMapping("/update")
    @Description(name = "책임감리일지 수정 화면", description = "책임감리일지 수정 화면 페이지", type = Description.TYPE.MEHTOD)
    public String updateCheifInspectionReport(CommonReqVo commonReqVo, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("책임감리일지 수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/chiefinspectionreport/chiefinspectionreport_u";
    }
}
