package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/construction/qualityinspection")
public class QualityInspectionPageController extends AbstractController {

    @Autowired
    PortalService portalService;

    @Autowired
    PortalComponent portalComponent;

    @Value("${gaia.path.previewPath}")
    String imgDir;

    /**
     * 메인
     */
    @GetMapping("")
    @Description(name = "품질검측관리 목록 조회 화면", description = "품질검측관리 목록 조회 화면", type = Description.TYPE.MEHTOD)
    public String getMain(CommonReqVo commonReqVo, @RequestParam(value = "pjtNo", required = false) String cntrctNo,
            @RequestParam(value = "pjtNo", required = false) String pjtNo, HttpServletRequest request, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("품질검측관리 목록 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        String[] btnId = { "QUA_D_01", "QUA_U_01", "QUA_C_05", "QRQ_AP_REQ", "QUA_C_12",
                "QUA_C_13", "QRP_AP_REQ", "QUA_CHKL_C_01", "QRQ_AP_REQ_D" };
        String[] btnClass = { "btn _outline", "btn _outline", "btn _fill", "btn _outline", "btn _outline",
                "btn _outline", "btn _outline", "btn _outline", "btn _outline" };
        String[] btnFun = { "onclick=\"qualityGrid.deleteQuality()\"", "onclick=\"qualityGrid.updateQuality()\"",
                "onclick=\"page.addQuality()\"", "onclick=\"qualityGrid.inspectionRequest()\"",
                "onclick=\"qualityGrid.addResult()\"", "onclick=\"qualityGrid.addAction()\"",
                "onclick=\"qualityGrid.paymentRequest()\"", "onclick=\"page.checkList()\"",
                "onclick=\"qualityGrid.cancelApproval()\"" };
        String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.042", "btn.043", "btn.044", "btn.045",
                "btn.046", "btn.066" };
        String[] btnEtc = { "id='delete'", "id='update'", "id='add'", "id='inspection'", "id='result'",
                "id='action'", "id='payment'", "id='checklist'", "id='cancelApproval'" };

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

        model.addAttribute("btnHtml", btnHtml);
        boolean isDelAuth = btnHtml.contains("qualityGrid.deleteQuality()");
        model.addAttribute("isDelAuth", isDelAuth);
        model.addAttribute("imgDir", imgDir);

        return "page/construction/qualityinspection/qualityinspection";
    }

    /**
     * 조회
     */
    @GetMapping("/getQuality")
    @Description(name = "품질검측 상세 조회 화면", description = "품질검측 상세 조회 화면", type = Description.TYPE.MEHTOD)
    public String getQuality(@RequestParam(value = "type", required = false, defaultValue = "d") String type,
            CommonReqVo commonReqVo, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("품질검측 상세 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("d".equals(type.toString())) {
            model.addAttribute("header", true);
        } else if ("p".equals(type.toString())) {
            model.addAttribute("header", false);
        }

        String[] btnId = { "QUA_U_01" };
        String[] btnClass = { "btn _outline" };
        String[] btnFun = { "onclick=\"page.updateQuality()\"" };
        String[] btnMsg = { "btn.003" };
        String[] btnEtc = { "id='update'" };

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
        model.addAttribute("btnHtml", btnHtml);

        return "page/construction/qualityinspection/qualityinspection_r";
    }

    /**
     * 품질 검측 추가/수정
     */
    @GetMapping("/addQuality")
    @Description(name = "품질검측 추가,수정 화면", description = "품질검측 추가,수정 화면", type = Description.TYPE.MEHTOD)
    public String add(CommonReqVo commonReqVo,
            @RequestParam(value = "type", required = false, defaultValue = "d") String type,
            Model model) {
        if ("d".equals(type.toString())) {
            model.addAttribute("header", true);
        } else if ("p".equals(type.toString())) {
            model.addAttribute("header", false);
        }

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("품질검측 추가,수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_cu";
    }

    /**
     * Activity 선택(새창)
     */
    @GetMapping("/selectActivity")
    @Description(name = "Activity 선택(새창)", description = "품질검측 추가,수정 화면 Activity 선택 창", type = Description.TYPE.MEHTOD)
    public String selectActivity(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("품질검측 Activity 선택 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_select_activity_pop_up";
    }

    /**
     * 검측결과 등록
     */
    @GetMapping("/addResult")
    @Description(name = "검측결과 등록 화면", description = "검측결과 등록 화면", type = Description.TYPE.MEHTOD)
    public String addResult(CommonReqVo commonReqVo,
            @RequestParam(value = "type", required = false, defaultValue = "d") String type,
            Model model) {
        if ("d".equals(type.toString())) {
            model.addAttribute("header", true);
        } else if ("p".equals(type.toString())) {
            model.addAttribute("header", false);
        }

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("검측결과 등록 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_result_cu";
    }

    /**
     * 조치 사항 등록
     */
    @GetMapping("/addAction")
    @Description(name = "조치사항 등록 화면", description = "조치사항 등록 화면", type = Description.TYPE.MEHTOD)
    public String addAction(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("조치사항 등록 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_action_cu";
    }

    /**
     * 조치사항 등록 모달
     */
    @GetMapping("/action/modal")
    @Description(name = "조치사항 등록 모달 창", description = "조치사항 화면 조치사항 등록 모달 창", type = Description.TYPE.MEHTOD)
    public String actionModal(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("조치사항 등록 모달 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_action_modal";
    }

    /**
     * 체크리스트 가져오기(새창)
     */
    @GetMapping("/selectChecklist")
    @Description(name = "체크리스트 가져오기(새창)", description = "품질검측 추가,수정 화면 체크리스트 선택 창", type = Description.TYPE.MEHTOD)
    public String selectCheckList(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("체크리스트 가져오기 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_select_checklist_pop_up";
    }

    /**
     * 결재결과 등록
     */
    @GetMapping("/addPayment")
    @Description(name = "결재결과 등록 화면", description = "결재결과 등록 화면", type = Description.TYPE.MEHTOD)
    public String addPayment(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결재결과 등록 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_payment_r";
    }

    /**
     * 체크리스트 관리
     */
    @GetMapping("/checklist")
    @Description(name = "체크리스트 관리 화면", description = "체크리스트 관리 화면", type = Description.TYPE.MEHTOD)
    public String checkList(CommonReqVo commonReqVo,
            @RequestParam(value = "type", required = false, defaultValue = "d") String type,
            Model model) {
        if ("d".equals(type.toString())) {
            model.addAttribute("header", true);
        } else if ("p".equals(type.toString())) {
            model.addAttribute("header", false);
        }

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("체크리스트 관리 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_checklist_cu";
    }

    /**
     * 체크리스트 관리 - 하위 공종 추가(모달)
     */
    @GetMapping("/work/modal")
    @Description(name = "하위 공종 추가 모달", description = "체크리스트 관리 화면 하위 공종 추가 모달", type = Description.TYPE.MEHTOD)
    public String workModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("하위 공종 추가 모달 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_work_modal";
    }

    /**
     * 체크리스트 관리 - 검측 체크 리스트(모달)
     */
    @GetMapping("/checklist/modal")
    @Description(name = "검측 체크 리스트 추가,수정 모달", description = "체크리스트 관리 화면 검측 체크 리스트 추가,수정 모달", type = Description.TYPE.MEHTOD)
    public String checkListModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("검측 체크 리스트 추가,수정 모달 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_checklist_modal";
    }

    /**
     * 사진 추가 - 사진 추가 모달
     */
    @GetMapping("/photo")
    @Description(name = "사진 추가 모달", description = "품질검측 추가,수정 사진 추가 모달", type = Description.TYPE.MEHTOD)
    public String photoModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("사진 추가, 수정 모달 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinspection_photo_modal";
    }

    @GetMapping("/request")
    @Description(name = "검측요청 모달", description = "품질검측 검측요청 모달", type = Description.TYPE.MEHTOD)
    public String requestModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("품질검측 검측요청 모달");
        systemLogComponent.addUserLog(userLog);

        return "page/construction/qualityinspection/qualityinsepction_request_modal";
    }

}
