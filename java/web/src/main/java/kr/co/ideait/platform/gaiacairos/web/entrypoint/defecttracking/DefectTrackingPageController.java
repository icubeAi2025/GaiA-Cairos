package kr.co.ideait.platform.gaiacairos.web.entrypoint.defecttracking;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/defectTracking")
public class DefectTrackingPageController extends AbstractController {

    @Autowired
    PortalComponent portalComponent;

    // 대시보드
    @GetMapping("/dashboard")
    @Description(name = "결함 추적 관리 > 대시보드 화면", description = "결함 추적 관리 > 대시보드 화면 페이지", type = Description.TYPE.MEHTOD)
    public String dashboard(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("결함추적 대시보드 화면 접속");
		systemLogComponent.addUserLog(userLog);

        return "page/defecttracking/tool/dashboard";
    }

    // 결함 추적 관리
    @GetMapping("/defectTracking")
    @Description(name = "결함 추적 관리 화면", description = "결함 추적 관리 메인 화면 페이지", type = Description.TYPE.MEHTOD)
    public String defectTracking(CommonReqVo commonReqVo, Model model,
                                @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 관리 목록 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 삭제, 수정, 추가 버튼
        String[] rescId = { "DT_DFCCY_D", "DT_DFCCY_U", "DT_DFCCY_C" };
        String[] btnClass = { "btn", "btn", "btn _fill" };
        String[] btnFun = { "onclick=\"page.deleteDfccy()\"", "onclick=\"page.moveToUpdateDfccy()\"", "onclick=\"page.moveToCreateDfccy()\"" };
        String[] btnMsg = { "btn.002", "btn.003", "btn.001" };
        String[] btnEtc = { "id=\"delete_dfccy\"", "id=\"update_dfccy\"", "id=\"create_dfccy\""};

        String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg, btnEtc);

        model.addAttribute("btnHtml", btnHtml);

        boolean isDelAuth = btnHtml.contains("delete");
        model.addAttribute("isDelAuth", isDelAuth);

        return "page/defecttracking/tool/defecttracking/defecttracking_main";
    }

    // 결함 추적 관리 > 결함 추가, 수정
    @GetMapping("/defectTracking/form")
    @Description(name = "결함 추적 관리 > 결함 추가, 수정화면", description = "결함 추적 관리 > 결함 추가, 수정 화면 페이지", type = Description.TYPE.MEHTOD)
    public String defectTrackingForm(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("결함추적 추가, 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

        return "page/defecttracking/tool/defecttracking/defecttracking_cu_popup";
    }

    // 결함 추적 관리 > 결함 상세보기
    @GetMapping("/defectTracking/form/detail")
    @Description(name = "결함 추적 관리 > 결함 상세보기화면", description = "결함 추적 관리 > 결함 상세보기 화면 페이지", type = Description.TYPE.MEHTOD)
    public String defectTrackingDetailForm(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("결함추적 관리 상세 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

        return "page/defecttracking/tool/defecttracking/defecttracking_detail_popup";
    }

    // 답변
    @GetMapping("/responses")
    @Description(name = "결함 추적 관리 > 답변 화면", description = "결함 추적 관리 > 답변 화면 페이지", type = Description.TYPE.MEHTOD)
    public String responses(CommonReqVo commonReqVo, Model model) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 답변관리 목록 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        String[] rescId = { "DT_RESPONSE_D", "DT_RESPONSE_C_M" };
        String[] btnClass = { "btn", "btn _fill" };
        String[] btnFun = { "onclick=\"page.deleteList()\"", "onclick=\"page.confirmList()\"" };
        String[] btnMsg = { "item.dfccy.028", "item.dfccy.029" };
        String[] btnEtc = { "id=\"deleteBtn\"", "id=\"confirmBtn\"" };

        String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg, btnEtc);
        model.addAttribute("btnHtml", btnHtml);

        boolean isDelAuth = btnHtml.contains("delete");
        model.addAttribute("isDelAuth", isDelAuth);

        return "page/defecttracking/tool/responses/responses";
    }

    // 답변 입력창
    @GetMapping("/responsesForm")
    @Description(name = "결함 추적 관리 > 답변 추가,수정 화면", description = "결함 추적 관리 > 답변 추가,수정 화면 페이지", type = Description.TYPE.MEHTOD)
    public String responsesForm(CommonReqVo commonReqVo, Model model) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 답변관리 입력, 수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        String[] rescId = { "DT_RESPONSE_D", "DT_RESPONSE_C_02" };
        String[] btnClass = { "btn", "btn _fill" };
        String[] btnFun = { "onclick=\"response.delete()\"", "onclick=\"response.save()\"" };
        String[] btnMsg = { "item.dfccy.028", "btn.006" };
        String[] btnEtc = { "id=\"cu_deleteBtn\"", "" };

        String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg, btnEtc);
        model.addAttribute("btnHtml", btnHtml);

        return "page/defecttracking/tool/responses/responses_cu";
    }

    // 확인
    @GetMapping("/verification")
    @Description(name = "결함 추적 관리 > 확인 조회 화면", description = "결함 추적 관리 > 확인 조회 페이지", type = Description.TYPE.MEHTOD)
    public String verification(CommonReqVo commonReqVo, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 확인 관리 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 의견삭제, QA종료, 관리관 종료 버튼
        String[] rescId = { "DT_CONFIRM_D_02", "DT_CONFIRM_C_QA", "DT_CONFIRM_C_SPVS" };
        String[] btnClass = { "btn _outline", "btn _fill", "btn _fill"};
        String[] btnFun = { "onclick=\"page.deleteAll()\"", "onclick=\"page.finishAll('qa')\"", "onclick=\"page.finishAll('sv')\"" };
        String[] btnMsg = { "item.dfccy.052", "item.dfccy.053", "item.dfccy.054" };
        String[] btnEtc = { "id=\"deleteBtn\"", "id=\"finishQaBtn\"", "id=\"finishSvBtn\"" };

        String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg, btnEtc);
        model.addAttribute("btnHtml", btnHtml);

        boolean isSpvs = btnHtml.contains("finishAll('sv')");
        model.addAttribute("isSpvs", isSpvs);

        boolean isQa = btnHtml.contains("finishAll('qa')");
        model.addAttribute("isQa", isQa);

        boolean isDelAuth = btnHtml.contains("delete");
        model.addAttribute("isDelAuth", isDelAuth);

        return "page/defecttracking/tool/verification/verification";
    }

    // 확인 입력창
    @GetMapping("/verificationForm")
    @Description(name = "결함 추적 관리 > 확인 추가, 수정화면", description = "결함 추적 관리 > 확인 추가, 수정 화면 페이지", type = Description.TYPE.MEHTOD)
    public String verificationForm(CommonReqVo commonReqVo, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 확인 추가 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 의견 저장 버튼
        String[] rescId = { "DT_CONFIRM_C" };
        String[] btnClass = { "btn _fill" };
        String[] btnFun = { "onclick=\"confirmPage.save()\"" };
        String[] btnMsg = { "btn.006" };
        String saveBtnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg);
        model.addAttribute("saveBtnHtml", saveBtnHtml);

        // QA 의견 저장 버튼
        String[] qaRescId = { "DT_CONFIRM_C_QA" };
        String[] qaBtnClass = { "" };
        String[] qaBtnFun = { "onclick=\"confirmPage.saveHitory('qa_cd')\"" };
        String[] qaBtnMsg = { "item.dfccy.082" };
        String[] qaBtnEtc = { "id=\"qa_save\"" };
        String qaBtnHtml = portalComponent.selectBtnAuthorityList(qaRescId, qaBtnClass, qaBtnFun, qaBtnMsg, qaBtnEtc);
        model.addAttribute("qaBtnHtml", qaBtnHtml);

        // 관리관 의견 저장 버튼
        String[] svRescId = { "DT_CONFIRM_C_SPVS" };
        String[] svBtnClass = { ""};
        String[] svBtnFun = { "onclick=\"confirmPage.saveHitory('spvs_cd')\"" };
        String[] svBtnMsg = { "item.dfccy.082" };
        String[] svBtnEtc = { "id=\"spvs_save\"" };
        String svBtnHtml = portalComponent.selectBtnAuthorityList(svRescId, svBtnClass, svBtnFun, svBtnMsg, svBtnEtc);
        model.addAttribute("svBtnHtml", svBtnHtml);

        boolean isSaveAuth = saveBtnHtml.contains("save");
        model.addAttribute("isSaveAuth", isSaveAuth);

        return "page/defecttracking/tool/verification/verification_cu";
    }

    // 종결
    @GetMapping("/termination")
    @Description(name = "종결 관리 화면", description = "종결 화면 페이지", type = Description.TYPE.MEHTOD)
    public String termination(CommonReqVo commonReqVo, Model model,
                            @RequestParam("pjtNo") String pjtNo,
                            @RequestParam("cntrctNo") String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 종결관리 목록 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 삭제 버튼
        String[] rescId = { "DT_TERM_D", "DT_TERM_ALL_FIN" };
        String[] btnClass = { "btn", "btn _fill" };
        String[] btnFun = { "onclick=\"page.deleteAll()\"", "onclick=\"page.finishAll()\""};
        String[] btnMsg = { "item.dfccy.032", "item.dfccy.015" };
        String[] btnEtc = { "id=\"deleteBtn\"", "id=\"finishBtn\"" };
        String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg, btnEtc);
        model.addAttribute("btnHtml", btnHtml);

        boolean isDelAuth = btnHtml.contains("delete");
        model.addAttribute("isDelAuth", isDelAuth);

        return "page/defecttracking/tool/termination/termination";
    }

    // 종결 입력창
    @GetMapping("/terminationForm")
    @Description(name = "종결 관리 입력 화면", description = "종결 관리 입력 화면 페이지", type = Description.TYPE.MEHTOD)
    public String terminationForm(CommonReqVo commonReqVo, Model model) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 종결관리 입력 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 의견 저장 버튼
        String[] rescId = { "DT_TERM_C" };
        String[] btnClass = { "btn _fill" };
        String[] btnFun = { "onclick=\"termination.save()\"" };
        String[] btnMsg = { "btn.006" };
        String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg);
        model.addAttribute("btnHtml", btnHtml);

        return "page/defecttracking/tool/termination/termination_cu";
    }

    // 결함 단계 설정
    @GetMapping("/setting")
    @Description(name = "결함 추적 관리 > 결함 단계 설정 화면", description = "결함 추적 관리 > 결함 단계 설정 화면 페이지", type = Description.TYPE.MEHTOD)
    public String setting(CommonReqVo commonReqVo, Model model) {

    	Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("결함추적 단계 설정 관리 화면 접속");
		systemLogComponent.addUserLog(userLog);

        // 삭제, 수정, 추가 버튼
        String[] rescId = { "DT_SETTING_D", "DT_SETTING_U_01", "DT_SETTING_C_01" };
        String[] btnClass = { "btn _outline", "btn _outline", "btn _fill" };
        String[] btnFun = { "onclick=\"phase.delete()\"", "onclick=\"phase.modify()\"", "onclick=\"phase.add()\"" };
        String[] btnMsg = { "btn.002", "btn.003", "btn.001" };
        String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg);
        model.addAttribute("btnHtml", btnHtml);

        boolean isAuth = btnHtml.contains("delete");
        model.addAttribute("isAuth", isAuth);

        return "page/defecttracking/tool/setting/setting";
    }


    // 결함 단계 추가
    @GetMapping("/setting/add")
    @Description(name = "결함 추적 관리 > 결함 단계 추가 화면", description = "결함 추적 관리 > 결함 단계 추가 화면 페이지", type = Description.TYPE.MEHTOD)
    public String settingAdd(CommonReqVo commonReqVo, Model model,
                            @RequestParam(value = "page", required = false) String page) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 단계 추가 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("p".equals(page)) {
            model.addAttribute("header", false);
        } else {
            model.addAttribute("header", true);
        }

        return "page/defecttracking/tool/setting/setting_c";
    }

    // 결함 단계 상세 조회
    @GetMapping("/setting/detail")
    @Description(name = "결함 추적 관리 > 결함 단계 상세 조회 화면", description = "결함 추적 관리 > 결함 단계 상세 조회 화면", type = Description.TYPE.MEHTOD)
    public String settingDetail(CommonReqVo commonReqVo, Model model) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 단계 상세 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        model.addAttribute("header", false);

        // 수정 페이지 이동 버튼
        String[] rescId = { "DT_SETTING_U_01" };
        String[] btnClass = { "btn" };
        String[] btnFun = { "onclick=\"detail.modify()\"" };
        String[] btnMsg = { "btn.003" };
        String[] btnEtc = { "id=\"updateBtn\"" };
        String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg, btnEtc);
        model.addAttribute("btnHtml", btnHtml);

        return "page/defecttracking/tool/setting/setting_r";
    }

    // 결함 단계 수정
    @GetMapping("/setting/edit")
    @Description(name = "결함 추적 관리 > 결함 단계 수정 화면", description = "결함 추적 관리 > 결함 단계 수정 화면 페이지", type = Description.TYPE.MEHTOD)
    public String settingEdit(CommonReqVo commonReqVo, Model model,
                                @RequestParam(value = "page", required = false) String page) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 단계 수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("p".equals(page)) {
            model.addAttribute("header", false);
        } else {
            model.addAttribute("header", true);
        }

        return "page/defecttracking/tool/setting/setting_u";
    }

    // Activity 새창
    @GetMapping("/activity")
    @Description(name = "결함 추적 관리 > Activity 새창", description = "결함 추적 관리 > Activity 새창 페이지", type = Description.TYPE.MEHTOD)
    public String activity(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 Activity 팝업 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/defecttracking/tool/common_activity";
    }

    /**
     * 결함 추가 > Activity 선택(새창)
     */
    @GetMapping("/selectActivity")
    @Description(name = "결함 추적 관리 > 결함 추가 > Activity 선택 새창", description = "결함 추적 관리 > 결함 추가 > Activity 선택 새창 페이지", type = Description.TYPE.MEHTOD)
    public String selectActivity(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적관리 Activity 팝업 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/defecttracking/tool/defecttracking/defecttracking_select_activity_pop_up";
    }

    // 결함 요약
    @GetMapping("/summary")
    @Description(name = "결함 추적 관리 > 결함 요약 화면", description = "결함 추적 관리 > 결함 요약 화면 페이지", type = Description.TYPE.MEHTOD)
    public String summary(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함추적 보고서 결함요약 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/defecttracking/report/summary/summary";
    }

    // 결함 보고서
    @GetMapping("/defectreport")
    @Description(name = "결함 추적 관리 > 결함보고서 조회 화면", description = "결함 추적 관리 > 결함보고서 조회 화면", type = Description.TYPE.MEHTOD)
    public String defectReport(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함보고서 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/defecttracking/report/defectreport/defectreport";
    }

    // 결함 상세조회
    @GetMapping("/defectreportDetail")
    @Description(name = "결함 추적 관리 > 결함보고서 상세 조회 화면", description = "결함 추적 관리 > 결함보고서 상세 조회 화면", type = Description.TYPE.MEHTOD)
    public String defectreportDetail(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함보고서 상세 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/defecttracking/report/defectreport/defectreport_detail";
    }

    // 결함 상세조회 팝업
    @GetMapping("/defectreportDetailPopup")
    @Description(name = "결함 추적 관리 > 결함보고서 상세 조회 새창 화면", description = "결함 추적 관리 > 결함보고서 상세 조회 새창 화면", type = Description.TYPE.MEHTOD)
    public String defectreportDetailPopup(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("결함보고서 상세 조회 새창 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/defecttracking/report/defectreport/defectreport_detail_popup";
    }

}
