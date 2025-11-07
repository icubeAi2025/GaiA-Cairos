package kr.co.ideait.platform.gaiacairos.web.entrypoint.project;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/project/organization")
public class OrganizationPageController extends AbstractController {

    @Autowired
    PortalService portalService;

    @Autowired
    PortalComponent portalComponent;

    /**
     * 조직도
     */
    @GetMapping("")
    @Description(name = "조직도 목록 조회 화면", description = "조직도 목록 조회 화면", type = Description.TYPE.MEHTOD)
    public String organization(CommonReqVo commonReqVo,
                               HttpServletRequest request, Model model,
                               @RequestParam(value = "pjtNo", required = false) String pjtNo,
                               @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("조직도 조직 목록 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 조직 추가 수정 삭제
        String[] btnId = {"ORG_D_01", "ORG_U_02", "ORG_C_02"};
        String[] btnClass = {"btn _outline", "btn _outline", "btn _fill"};
        String[] btnFun = {"onclick='page.org.delete()'", "onclick='page.org.update()'",
                "onclick='page.org.create()'"};
        String[] btnMsg = {"btn.002", "btn.003", "btn.001"};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

        // 조직도이미지 추가 수정 삭제
        String[] btnId2 = {"ORG_IMG_C_01", "ORG_IMG_D_01"};
        String[] btnClass2 = {"btn icon_btn _outline", "btn icon_btn _outline"};
        String[] btnFun2 = {"", ""};
        String[] btnEtc2 = {"id='addFileButton'", "id='removeAllButton'"};
        String[] btnIcon2 = {"ic ic-picture-one", "ic ic-delete"};
        String[] btnTooltip2 = {"item.org.015", "item.org.016"};
        String[] btnBlind2 = {"", ""};

        String btnHtml2 = portalComponent.selectBtnAuthorityListWithIcon(btnId2, btnClass2, btnFun2, btnEtc2, btnIcon2, btnTooltip2, btnBlind2);

        model.addAttribute("btnHtml", btnHtml);
        model.addAttribute("btnHtml2", btnHtml2);
        boolean isDelAuth = btnHtml.contains("delete");
        model.addAttribute("isDelAuth", isDelAuth); // 삭제 권한이 있으면 Y 없으면 N


        return "page/project/organization/organization";
    }

    /**
     * 조직도 등록
     */
    @GetMapping("/create")
    @Description(name = "조직도 > 조직 추가 화면", description = "조직도 > 조직 추가 화면", type = Description.TYPE.MEHTOD)
    public String createOrgPage(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                                @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("조직도 조직 추가 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/project/organization/organization_c";
    }

    /**
     * 조직도 수정
     */
    @GetMapping("/update")
    @Description(name = "조직도 > 조직 수정 화면", description = "조직도 > 조직 수정 화면", type = Description.TYPE.MEHTOD)
    public String updateOrgPate(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                                @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("조직도 조직 수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/project/organization/organization_u";
    }

    /**
     * 조직도 조회
     */
    @GetMapping("/read")
    @Description(name = "조직도 > 조직 조회 화면", description = "조직도 > 조직 조회 화면", type = Description.TYPE.MEHTOD)
    public String getReadOrgForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                                 @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                 @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("조직도 조직 상세조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        String[] btnId = {"ORG_U_02"};
        String[] btnClass = {"btn"};
        String[] btnFun = {"onclick='page.update()'"};
        String[] btnMsg = {"btn.003"};
        String[] btnEtc = {"id='action-button'"};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
        model.addAttribute("btnHtml", btnHtml);

        return "page/project/organization/organization_r";
    }
}
