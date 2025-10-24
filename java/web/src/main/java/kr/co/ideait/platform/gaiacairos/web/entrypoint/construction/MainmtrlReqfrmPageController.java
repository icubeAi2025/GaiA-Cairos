package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;

@Controller
@RequestMapping("/construction/mainmtrlreqfrm")
public class MainmtrlReqfrmPageController extends AbstractController {

    @Autowired
    PortalComponent portalComponent;

    @Value("${gaia.path.previewPath}")
    String imgDir;

    /**
     * 메인
     */
    @RequestMapping("")
    public String main(CommonReqVo commonReqVo, @RequestParam(value = "pjtNo", required = false) String cntrctNo,
            @RequestParam(value = "pjtNo", required = false) String pjtNo, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("주요자재 검수요청서 목록 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        String[] btnId = { "MREQ_D_01", "MREQ_U_01", "MREQ_C_02", "MREQ_AP_REQ", "MREQ_R_09", "MREQ_AP_PREQ", "MREQ_AP_REQ_D" };
        String[] btnClass = { "btn _outline", "btn _outline", "btn _fill", "btn _outline", "btn _outline", "btn _outline", "btn _outline" };
        String[] btnFun = { "onclick=\"page.deleteMtrlReqfrm()\"", "onclick=\"page.updateMtrlReqfrm()\"",
                "onclick=\"page.addMtrlReqfrm()\"", "onclick=\"page.inspectionRequest()\"",
                "onclick=\"page.addMtrlReqfrmResult()\"", "onclick=\"page.paymentRequest()\"",
                "onclick=\"page.cancelPayment()\"" };
        String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.067", "btn.068", "btn.045", "btn.066" };
        String[] btnEtc = { "id='delete'", "id='update'", "id='add'", "id='inspection'", "id='result'",
                "id='action'", "id='payment'" };

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

        model.addAttribute("btnHtml", btnHtml);
        boolean isDelAuth = btnHtml.contains("page.deleteMtrlReqfrm()");
        model.addAttribute("isDelAuth", isDelAuth);
        model.addAttribute("imgDir", imgDir);

        return "page/construction/mainmtrlreqfrm/mainmtrlreqfrm";
    }

    /**
     * 주요자재 검수요청서 검측요청 모달
     */
    @GetMapping("/request")
    @Description(name = "검측요청 모달", description = "주요자재 검수요청서 검측요청 모달", type = Description.TYPE.MEHTOD)
    public String requestModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("검측요청 모달 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/mainmtrlreqfrm/mainmtrlreqfrm_request_modal";
    }

    /**
     * 조회
     */
    @RequestMapping("/getMtrlReqfrm")
    @Description(name = "품질검측 상세 조회 화면", description = "주요자재 검수요청서 상세 조회 화면", type = Description.TYPE.MEHTOD)
    public String getMtrlReqfrm(@RequestParam(value = "type", required = false, defaultValue = "d") String type,
            CommonReqVo commonReqVo, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("주요자재 검수요청서 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("d".equals(type.toString())) {
            model.addAttribute("header", true);
        } else if ("p".equals(type.toString())) {
            model.addAttribute("header", false);
        }

        return "page/construction/mainmtrlreqfrm/mainmtrlreqfrm_r";
    }

    /**
     * 주요자재 검수요청서 추가
     */
    @GetMapping("/addMtrlReqfrm")
    @Description(name = "주요자재 검수요청서 추가 화면", description = "주요자재 검수요청서 추가 화면", type = Description.TYPE.MEHTOD)
    public String addMtrlReqfrm(CommonReqVo commonReqVo,
            @RequestParam(value = "type", required = false, defaultValue = "d") String type,
            Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("주요자재 검수요청서 추가 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("d".equals(type.toString())) {
            model.addAttribute("header", true);
        } else if ("p".equals(type.toString())) {
            model.addAttribute("header", false);
        }

        return "page/construction/mainmtrlreqfrm/mainmtrlreqfrm_c";
    }

    /**
     * 주요자재 검수요청서 수정
     */
    @GetMapping("/updateMtrlReqfrm")
    @Description(name = "주요자재 검수요청서 수정 화면", description = "주요자재 검수요청서 수정 화면", type = Description.TYPE.MEHTOD)
    public String updateMtrlReqfrm(CommonReqVo commonReqVo,
            @RequestParam(value = "type", required = false, defaultValue = "d") String type,
            Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("주요자재 검수요청서 수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("d".equals(type.toString())) {
            model.addAttribute("header", true);
        } else if ("p".equals(type.toString())) {
            model.addAttribute("header", false);
        }

        return "page/construction/mainmtrlreqfrm/mainmtrlreqfrm_u";
    }

    @GetMapping("/photo")
    @Description(name = "사진 추가 모달", description = "주요자재 검수요청서 추가,수정 사진 추가 모달", type = Description.TYPE.MEHTOD)
    public String photoModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("사진 추가, 수정 모달 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/mainmtrlreqfrm/mainmtrlreqfrm_photo_modal";
    }

    /**
     * 주요자재 검수요청서 검수결과 등록
     */
    @GetMapping("/addMtrlReqfrmResult")
    @Description(name = "주요자재 검수결과 등록 화면", description = "주요자재 검수결과 등록 화면", type = Description.TYPE.MEHTOD)
    public String addMtrlReqfrmResult(CommonReqVo commonReqVo,
                                @RequestParam(value = "type", required = false, defaultValue = "d") String type,
                                Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("주요자재 검수결과 등록 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("d".equals(type.toString())) {
            model.addAttribute("header", true);
        } else if ("p".equals(type.toString())) {
            model.addAttribute("header", false);
        }

        return "page/construction/mainmtrlreqfrm/mainmtrlreqfrm_result_cu";
    }

    /**
     * 주요자재 검수요청서 주요자재 모달
     */
    @GetMapping("/mainmtrlList")
    @Description(name = "주요자재 모달", description = "주요자재 검수요청서 주요자재 모달", type = Description.TYPE.MEHTOD)
    public String mainmtrlListModal(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("주요자재 모달 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/mainmtrlreqfrm/mainmtrlreqfrm_list_modal";
    }
}
