package kr.co.ideait.platform.gaiacairos.web.entrypoint.safety;

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
@RequestMapping("/safetymgmt")
public class SafetymgmtPageController extends AbstractController {

    @Autowired
    PortalService portalService;

    @Autowired
    PortalComponent portalComponent;

    // ---------안전점검-----------

    /**
     * 안전점검 목록
     */
    @GetMapping("/check")
    @Description(name = "안전점검 목록 조회 화면", description = "안전점검 목록 조회 화면", type = Description.TYPE.MEHTOD)
    public String getCheck(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
            @RequestParam(value = "pjtNo", required = false) String pjtNo,
            @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 목록 화면 접속");
        systemLogComponent.addUserLog(userLog);

        String[] btnId = { "SAF_D_01", "SAF_U_01", "SAF_C_03", "SAF_AP_RES", "SAF_RES_C_01", "SAF_INSL_C_01",
                "SAF_AP_REQ" };

        String[] btnClass = { "btn _outline", "btn _outline", "btn _fill", "btn _outline", "btn _outline",
                "btn _outline",
                "btn _outline" };

        String[] btnFun = { "onclick=\"safetyGrid.deleteSafety()\"", "onclick=\"safetyGrid.updateSafety()\"",
                "onclick=\"page.addSafety()\"", "onclick=\"safetyGrid.reportRequest()\"",
                "onclick=\"safetyGrid.addInspectionResult()\"",
                "onclick=\"page.mngInspectionlist()\"", "onclick=\"safetyGrid.approvalRequest()\"" };

        String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.061", "btn.057", "btn.059", "btn.027" };

        String[] btnEtc = { "id='delete'", "id='update'", "id='addSafetyBtn'", "id='report'", "id='result'",
                "id='list'",
                "id='approval'" };

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

        model.addAttribute("btnHtml", btnHtml);
        boolean isDelAuth = btnHtml.contains("safetyGrid.deleteSafety()");
        model.addAttribute("isDelAuth", isDelAuth);

        return "page/safety/check/safetycheck";
    }

    /**
     * 안전점검 조회
     */
    @GetMapping("/check/inspection")
    @Description(name = "안전점검 상세 조회 화면", description = "안전점검 상세 조회 화면", type = Description.TYPE.MEHTOD)
    public String getInspection(@RequestParam(value = "type", required = false, defaultValue = "d") String type,
            CommonReqVo commonReqVo, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 상세 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("d".equals(type)) {
            model.addAttribute("header", true);
        } else if ("p".equals(type)) {
            model.addAttribute("header", false);
        }

        String[] btnId = { "SAF_U_01" };
        String[] btnClass = { "btn _outline" };
        String[] btnFun = { "onclick=\"page.updateSafety()\"" };
        String[] btnMsg = { "btn.003" };
        String[] btnEtc = { "id='update'" };

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
        model.addAttribute("btnHtml", btnHtml);

        return "page/safety/check/safetycheck_r";
    }

    /**
     * 안전점검 추가/수정
     */
    @GetMapping("/check/add")
    @Description(name = "안전점검 추가,수정 화면", description = "안전점검 추가,수정 화면", type = Description.TYPE.MEHTOD)
    public String addCheck(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
            @RequestParam(value = "type", required = false, defaultValue = "d") String type,
            @RequestParam(value = "pjtNo", required = false) String pjtNo,
            @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 추가, 수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("d".equals(type)) {
            model.addAttribute("header", true);
        } else if ("p".equals(type)) {
            model.addAttribute("header", false);
        }

        return "page/safety/check/safetycheck_cu";
    }

    /**
     * 점검항목 추가 모달
     */
    @GetMapping("/check/add/modal")
    @Description(name = "점검항목 추가 모달", description = "안전점검 추가,수정 화면 점검항목 추가 모달", type = Description.TYPE.MEHTOD)
    public String addModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("점검항목 추가 모달 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/check/safetycheck_cu_inspection_modal";
    }

    /**
     * 점검결과 작성
     */
    @GetMapping("/check/add/result")
    @Description(name = "점검결과 추가,수정 화면", description = "점검결과 추가,수정 화면", type = Description.TYPE.MEHTOD)
    public String addResult(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("점검결과 추가,수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/check/safetycheck_result_cu";
    }

    /**
     * 사진 추가 모달
     */
    @GetMapping("/photo/modal")
    @Description(name = "사진 추가 모달", description = "점검결과 추가,수정 화면 사진 추가 모달", type = Description.TYPE.MEHTOD)
    public String photoModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("사진 추가 모달 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/check/safetycheck_photo_modal";
    }

    /**
     * 점검리스트 관리
     */
    @GetMapping("/check/mgmtlist")
    @Description(name = "점검리스트 관리 화면", description = "점검리스트 관리 화면", type = Description.TYPE.MEHTOD)
    public String getMgmtList(CommonReqVo commonReqVo,
            @RequestParam(value = "type", required = false, defaultValue = "d") String type,
            Model model) {
        if ("d".equals(type)) {
            model.addAttribute("header", true);
        } else if ("p".equals(type)) {
            model.addAttribute("header", false);
        }

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("점검리스트 관리 화면");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/check/safetycheck_list_mgmt";
    }

    /**
     * 점검리스트 관리 - 하위 공종 추가(모달)
     */
    @GetMapping("/check/work/modal")
    @Description(name = "하위 공종 추가,수정 모달", description = "점검리스트 관리 화면 하위 공종 추가,수정 모달", type = Description.TYPE.MEHTOD)
    public String workModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("하위 공종 추가,수정 모달");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/check/safetycheck_list_work_modal";
    }

    /**
     * 점검리스트 관리 - 리스트 추가(모달)
     */
    @GetMapping("/check/list/modal")
    @Description(name = "리스트 추가,수정 모달", description = "점검리스트 관리 화면 리스트 추가,수정 모달", type = Description.TYPE.MEHTOD)
    public String listModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("리스트 추가,수정 모달");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/check/safetycheck_list_safety_modal";
    }

    // ---------안전 지적서-----------

    /**
     * 안전지적서 목록
     */
    @GetMapping("/sadtag")
    @Description(name = "안전지적서 목록 조회 화면", description = "안전지적서 목록 조회 화면", type = Description.TYPE.MEHTOD)
    public String getSadtag(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
            @RequestParam(value = "pjtNo", required = false) String pjtNo,
            @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전지적서 목록 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        String[] btnId = { "SAD_D_01", "SAD_U_02", "SAD_C_03", "SAD_C_02", "SAD_AP_REQ" };

        String[] btnClass = { "btn _outline", "btn _outline", "btn _fill", "btn _outline", "btn _outline" };

        String[] btnFun = { "onclick=\"sadtagGrid.deleteSadtag()\"", "onclick=\"sadtagGrid.updateSadtag()\"",
                "onclick=\"page.addSadtag()\"", "onclick=\"sadtagGrid.addAction()\"",
                "onclick=\"sadtagGrid.approvalRequest()\"" };

        String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.062", "btn.027" };

        String[] btnEtc = { "id='delete'", "id='update'", "id='add'", "id='addResult'", "id='approval'" };

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

        model.addAttribute("btnHtml", btnHtml);
        boolean isDelAuth = btnHtml.contains("sadtagGrid.deleteSadtag()");
        model.addAttribute("isDelAuth", isDelAuth);

        return "page/safety/sadtag/sadtag";
    }

    /**
     * 안전지적서 추가
     */
    @GetMapping("/sadtag/add")
    @Description(name = "안전지적서 추가 화면", description = "안전지적서 추가 화면", type = Description.TYPE.MEHTOD)
    public String addSadtag(CommonReqVo commonReqVo, @RequestParam(value = "type", required = false) String type,
            Model model,
            HttpServletRequest request, @RequestParam(value = "pjtNo", required = false) String pjtNo,
            @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        if ("d".equals(type)) {
            model.addAttribute("header", true);
        } else if ("p".equals(type)) {
            model.addAttribute("header", false);
        }

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전지적서 추가 화면");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/sadtag/sadtag_c";
    }

    /**
     * 안전지적서 수정
     */
    @GetMapping("/sadtag/update")
    @Description(name = "안전지적서 수정 화면", description = "안전지적서 수정 화면", type = Description.TYPE.MEHTOD)
    public String updateSadtag(CommonReqVo commonReqVo, @RequestParam(value = "type", required = false) String type,
            Model model,
            HttpServletRequest request, @RequestParam(value = "pjtNo", required = false) String pjtNo,
            @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                
        if ("d".equals(type)) {
            model.addAttribute("header", true);
        } else if ("p".equals(type)) {
            model.addAttribute("header", false);
        }

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전지적서 수정 화면");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/sadtag/sadtag_u";
    }

    /**
     * 안전지적서 조회
     */
    @GetMapping("/sadtag/read")
    @Description(name = "안전지적서 상세 조회 화면", description = "안전지적서 상세 조회 화면", type = Description.TYPE.MEHTOD)
    public String readSadtag(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전지적서 상세 조회 화면");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/sadtag/sadtag_r";
    }

    /**
     * 안전지적서 조치결과 등록/수정
     */
    @GetMapping("/sadtag/action")
    @Description(name = "안전지적서 조치결과 등록/수정 화면", description = "안전지적서 조치결과 등록/수정 화면", type = Description.TYPE.MEHTOD)
    public String addAction(CommonReqVo commonReqVo, @RequestParam(value = "type", required = false) String type,
            Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전지적서 조치결과 등록/수정 화면");
        systemLogComponent.addUserLog(userLog);

        String[] btnId = { "SAD_C_04" };

        String[] btnClass = { "btn _outline" };

        String[] btnFun = { "onclick=\"page.saveSadtag()\"" };

        String[] btnMsg = { "btn.006" };

        String[] btnEtc = { "id='update'" };

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

        model.addAttribute("btnHtml", btnHtml);

        if ("d".equals(type)) {
            model.addAttribute("header", true);
        } else if ("p".equals(type)) {
            model.addAttribute("header", false);
        }

        return "page/safety/sadtag/sadtag_result_cu";
    }

    /**
     * 점검요청 모달
     */
    @GetMapping("/request")
    @Description(name = "점검요청 모달", description = "안전점검 점검요청 모달", type = Description.TYPE.MEHTOD)
    public String requestModal(CommonReqVo commonReqVo, Model model) {
        model.addAttribute("isModal", true);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 점검요청 모달");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/check/safetycheck_request_modal";
    }
}
