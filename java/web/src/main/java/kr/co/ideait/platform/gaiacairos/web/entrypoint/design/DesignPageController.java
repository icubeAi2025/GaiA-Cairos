package kr.co.ideait.platform.gaiacairos.web.entrypoint.design;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/design")
public class DesignPageController extends AbstractController {

    @Autowired
    PortalService portalService;

    @Autowired
    PortalComponent portalComponent;

    // 대시보드
    @GetMapping("/dashboard")
    @Description(name = "설계 검토 관리 > 대시보드 화면", description = "설계 검토 관리 > 대시보드 화면 페이지", type = Description.TYPE.MEHTOD)
    public String dashboard(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계검토 툴 대시보드 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/design/tool/dashboard";
    }

    // 설계 검토 관리
    @GetMapping("/designReview")
    @Description(name = "설계 검토 관리 화면", description = "설계 검토 관리 메인 화면 페이지", type = Description.TYPE.MEHTOD)
    public String designReview(CommonReqVo commonReqVo, Model model, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                               @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계 검토 관리 화면 접속");

        String cuBtnHtml = "";
        // 상단 기본버튼
        String[] btnId = {"DM_DSGN_D_01", "DM_DSGN_U", "DM_DSGN_C"};
        String[] btnClass = {"btn", "btn", "btn _fill"};
        String[] btnFun = {"onclick=\"page.deleteDsgn()\"", "onclick=\"page.moveToUpdateDsgn()\"", "onclick=\"page.moveToCreateDsgn()\""};
        String[] btnMsg = {"btn.002", "btn.003", "btn.001"};
        String[] btnEtc = {"id=\"delete_dsgn\"", "id=\"update_dsgn\"", "id=\"create_dsgn\""};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

        model.addAttribute("btnHtml", btnHtml);

        boolean isAuth = btnHtml.contains("delete");
        model.addAttribute("isDelAuth", isAuth);

//            String[] cuBtnId = {"DM_DSGN_CU_01"};
//            String[] cuBtnClass = {"btn _fill"};
//            String[] cuBtnFun = {"onclick=\"dsgnInput.save()\""};
//            String[] cuBtnMsg = {"btn.006"};
//            String[] cuBtnEtc = {"id=\"action-button\""};
//
//            cuBtnHtml = portalComponent.selectBtnAuthorityList(cuBtnId, cuBtnClass, cuBtnFun, cuBtnMsg, cuBtnEtc);
//
//            model.addAttribute("cuBtnHtml", cuBtnHtml);


        systemLogComponent.addUserLog(userLog);
        return "page/design/tool/designreview/designreview_main";
    }

    // 설계 검토 관리 > 추가, 수정 폼
    @GetMapping("/designReview/form")
    @Description(name = "설계 검토 관리 > 추가, 수정화면", description = "설계 검토 관리 > 추가, 수정 화면 페이지", type = Description.TYPE.MEHTOD)
    public String designReviewForm(CommonReqVo commonReqVo, Model model, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                   @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계 검토 관리의 추가, 수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

//        String[] cuBtnId = {"DM_DSGN_CU_01"};
//        String[] cuBtnClass = {"btn _fill"};
//        String[] cuBtnFun = {"onclick=\"dsgnInputPop.save()\""};
//        String[] cuBtnMsg = {"btn.006"};
//        String[] cuBtnEtc = {"id=\"action-button\""};
//
//        String cuBtnHtml = portalComponent.selectBtnAuthorityList(cuBtnId, cuBtnClass, cuBtnFun, cuBtnMsg, cuBtnEtc);
//
//        model.addAttribute("cuBtnHtml", cuBtnHtml);


        return "page/design/tool/designreview/designreview_cu_popup";
    }

    // 설계 검토 관리 > 추가, 수정 폼 > 도서 사진 등록 폼
    @GetMapping("/designReview/dwgPhoto/modal")
    @Description(name = "설계 검토 관리 > 추가, 수정 - 도서 사진 등록 폼", description = "설계 검토 추가, 수정 시 도서 사진 등록 화면", type = Description.TYPE.MEHTOD)
    public String designReviewDwgPhotoForm(
            CommonReqVo commonReqVo
            , @RequestParam(value = "mode", required = false) String mode
            , Model model) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계 검토 관리의 추가, 수정 화면 중 검토 도서 사진 등록 모달창 접속");
        systemLogComponent.addUserLog(userLog);

        model.addAttribute("mode", mode);
        return "page/design/tool/designreview/designreview_dwg_photo_modal";
    }

    // 설계 검토 관리 > 상세보기
    @GetMapping("/designReview/form/detail")
    @Description(name = "설계 검토 관리 > 상세보기화면", description = "설계 검토 관리 > 상세보기 화면 페이지", type = Description.TYPE.MEHTOD)
    public String designReviewDetail(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계 검토 관리의 상세 보기 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/design/tool/designreview/designreview_detail_popup";
    }

    // 답변
    @GetMapping("/responses")
    @Description(name = "설계 검토 관리 > 답변관리 화면", description = "설계 검토 관리 > 답변관리 화면 페이지", type = Description.TYPE.MEHTOD)
    public String responses(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                            @RequestParam(value = "pjtNo", required = false) String pjtNo,
                            @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계 검토 관리의 답변 관리 화면 접속");
        systemLogComponent.addUserLog(userLog);

        String[] btnId = {"DM_RESPONSE_D"};
        String[] btnClass = {"btn"};
        String[] btnFun = {"onclick=\"page.deleteList()\""};
        String[] btnMsg = {"item.dfccy.028"};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);
        boolean isAuth = btnHtml.contains("delete");
        model.addAttribute("isDelAuth", isAuth);
        model.addAttribute("btnHtml", btnHtml);

        return "page/design/tool/responses/responses";
    }

    // 답변 입력창
    @GetMapping("/responsesForm")
    @Description(name = "설계 검토 관리 > 답변관리 추가, 수정화면", description = "설계 검토 관리 > 답변관리 추가, 수정 화면 페이지", type = Description.TYPE.MEHTOD)
    public String responsesForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                                @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계 검토 관리의 답변 관리 추가, 수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        String[] btnId = {"DM_RESPONSE_D", "DM_RESPONSE_CU_02"};
        String[] btnClass = {"btn", "btn _fill"};
        String[] btnFun = {"onclick=\"response.delete()\"", "onclick=\"response.save()\""};
        String[] btnMsg = {"item.dfccy.028", "btn.006"};
        String[] btnEtc = {"id=\"deleteBtn\"", ""};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
        model.addAttribute("btnHtml", btnHtml);

        return "page/design/tool/responses/responses_cu";
    }

    // 평가
    @GetMapping("/evaluation")
    @Description(name = "설계검토 > 평가 관리 화면", description = "설계검토 > 평가 관리 화면", type = Description.TYPE.MEHTOD)
    public String evaluation(CommonReqVo commonReqVo, Model model, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                             @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계검토 평가 관리 화면 접속");
        systemLogComponent.addUserLog(userLog);

        String[] btnId = {"DM_EVAL_D_02"};
        String[] btnClass = {"btn"};
        String[] btnFun = {"onclick=\"page.deleteAll();\""};
        String[] btnMsg = {"item.dsgn.030"};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);
        boolean isAuth = btnHtml.contains("delete");
        model.addAttribute("isDelAuth", isAuth);
        model.addAttribute("btnHtml", btnHtml);

        return "page/design/tool/evaluation/evaluation";
    }

    // 평가 입력창
    @GetMapping("/evaluationForm")
    @Description(name = "설계검토 > 평가 추가, 수정 화면", description = "설계검토 > 평가 추가, 수정 화면", type = Description.TYPE.MEHTOD)
    public String evaluationForm(CommonReqVo commonReqVo, Model model, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                 @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계검토 평가 입력 화면 접속");
        systemLogComponent.addUserLog(userLog);

        //평가 의견 저장 버튼
        String[] btnId = {"DM_EVAL_C_01"};
        String[] btnClass = {"btn _fill"};
        String[] btnFun = {"onclick=\"evaluation.save()\""};
        String[] btnMsg = {"btn.006"};

        String saveBtnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

        model.addAttribute("saveBtnHtml", saveBtnHtml);

        //평가자 동의 버튼
        String[] apprerBtnId = {"DM_EVAL_APPRER_01"};
        String[] apprerBtnClass = {""};
        String[] apprerBtnFun = {"onclick=\"evaluation.saveApprer();\""};
        String[] apprerBtnMsg = {"btn.006"};
        String[] apprerBtnEtc = {"id=\"apprer_save\"", "data-apprer-result=\"\""};

        String apprerBtnHtml = portalComponent.selectBtnAuthorityList(apprerBtnId, apprerBtnClass, apprerBtnFun, apprerBtnMsg, apprerBtnEtc);

        model.addAttribute("apprerBtnHtml", apprerBtnHtml);

        //평가의견 수정 버튼
        String[] evaModBtnId = {"DM_EVAL_U_01"};
        String[] evaModBtnClass = {""};
        String[] evaModBtnFun = {"onclick=\"mod()\""};
        String[] evaModBtnMsg = {"btn.003"};
        String[] evaModBtnEtc = {""};

        String evaModBtnHtml = portalComponent.selectBtnAuthorityList(evaModBtnId, evaModBtnClass,evaModBtnFun,evaModBtnMsg, evaModBtnEtc);

        model.addAttribute("evaModBtnHtml", evaModBtnHtml);

        //평가의견 삭제 버튼
        String[] evaDelBtnId = {"DM_EVAL_D"};
        String[] evaDelBtnClass = {""};
        String[] evaDelBtnFun = {"onclick=\"del()\""};
        String[] evaDelBtnMsg = {"btn.002"};
        String[] evaDelBtnEtc = {""};

        String evaDelBtnHtml = portalComponent.selectBtnAuthorityList(evaDelBtnId,evaDelBtnClass,evaDelBtnFun,evaDelBtnMsg, evaDelBtnEtc);

        model.addAttribute("evaDelBtnHtml", evaDelBtnHtml);

        model.addAttribute("USER_ID", UserAuth.get(true).getUsrId());

        return "page/design/tool/evaluation/evaluation_cu";
    }

    // 백체크
    @GetMapping("/backcheck")
    @Description(name = "설계검토 > 백체크 관리 화면", description = "설계검토 > 백체크 관리 화면", type = Description.TYPE.MEHTOD)
    public String backcheck(CommonReqVo commonReqVo, Model model, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                            @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계검토 백체크 관리 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 삭제 버튼
        String[] btnId = {"DM_BHK_D_02"};
        String[] btnClass = {"btn"};
        String[] btnFun = {"onclick=\"page.deleteAll()\""};
        String[] btnMsg = {"item.dsgn.044"};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

        model.addAttribute("btnHtml", btnHtml);

        boolean isDelAuth = btnHtml.contains("delete");
        model.addAttribute("isDelAuth", isDelAuth);

        return "page/design/tool/backcheck/backcheck";
    }

    // 백체크 입력창
    @GetMapping("/backcheckForm")
    @Description(name = "설계검토 > 백체크 추가, 수정 화면", description = "설계검토 > 백체크 추가, 수정 화면", type = Description.TYPE.MEHTOD)
    public String backcheckForm(CommonReqVo commonReqVo, Model model, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계검토 백체크 입력 화면 접속");
        systemLogComponent.addUserLog(userLog);

        //백체크 의견 저장 버튼
        String[] btnId = {"DM_BHK_C_01"};
        String[] btnClass = {"btn _fill"};
        String[] btnFun = {"onclick=\"backCheck.save()\""};
        String[] btnMsg = {"btn.006"};

        String saveBtnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

        model.addAttribute("saveBtnHtml", saveBtnHtml);

        //백체크 동의 버튼
        String[] bhkBtnId = {"DM_BHK_APPRER_01"};
        String[] bhkBtnClass = {""};
        String[] bhkBtnFun = {"onclick=\"backCheck.saveBackchk();\""};
        String[] bhkBtnMsg = {"btn.006"};
        String[] bhkBtnEtc = {"id=\"backchk_save\"", "data-backchk-result=\"\""};

        String bhkBtnHtml = portalComponent.selectBtnAuthorityList(bhkBtnId, bhkBtnClass, bhkBtnFun, bhkBtnMsg, bhkBtnEtc);

        model.addAttribute("bhkBtnHtml", bhkBtnHtml);

        //백체크 삭제 버튼
        String[] bhkDelBtnId = {"DM_BHK_D_01"};
        String[] bhkDelBtnClass = {""};
        String[] bhkDelBtnFun = {"onclick=\"del();\""};
        String[] bhkDelBtnMsg = {"btn.002"};
        String[] bhkDelBtnEtc = {""};

        String bhkDelBtnHtml = portalComponent.selectBtnAuthorityList(bhkDelBtnId,bhkDelBtnClass,bhkDelBtnFun,bhkDelBtnMsg,bhkDelBtnEtc);

        model.addAttribute("bhkDelBtnHtml", bhkDelBtnHtml);

        //백체크 수정 버튼
        String[] bhkModBtnId = {"DM_BHK_U_01"};
        String[] bhkModBtnClass = {""};
        String[] bhkModBtnFun = {"onclick=\"mod();\""};
        String[] bhkModBtnMsg = {"btn.003"};
        String[] bhkModBtnEtc = {""};

        String bhkModBtnHtml = portalComponent.selectBtnAuthorityList(bhkModBtnId,bhkModBtnClass,bhkModBtnFun,bhkModBtnMsg,bhkModBtnEtc);

        model.addAttribute("bhkModBtnHtml", bhkModBtnHtml);

        model.addAttribute("USER_ID", UserAuth.get(true).getUsrId());

        return "page/design/tool/backcheck/backcheck_cu";
    }

    // 설계 검토 단계 설정
    @GetMapping("/setting")
    @Description(name = "설계검토 > 검토 단계 관리 화면", description = "설계검토 > 검토 단계 관리 화면", type = Description.TYPE.MEHTOD)
    public String setting(CommonReqVo commonReqVo, Model model, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                          @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계검토 단계 설정 관리 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 상단 기본버튼
        String[] btnId = {"DM_SETTING_D", "DM_SETTING_U_01", "DM_SETTING_C_01"};
        String[] btnClass = {"btn _outline", "btn _outline", "btn _fill"};
        String[] btnFun = {"onclick=\"phase.delete()\"", "onclick=\"phase.modify()\"", "onclick=\"phase.add()\""};
        String[] btnMsg = {"btn.002", "btn.003", "btn.001"};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

        model.addAttribute("btnHtml", btnHtml);

        boolean isAuth = btnHtml.contains("delete");
        model.addAttribute("isDelAuth", isAuth);


        return "page/design/tool/setting/setting";
    }

    // 설계 검토 단계 추가
    @GetMapping("/setting/add")
    @Description(name = "설계검토 > 검토 단계 추가 화면", description = "설계검토 > 검토 단계 추가 화면 페이지", type = Description.TYPE.MEHTOD)
    public String settingAdd(CommonReqVo commonReqVo, Model model, @RequestParam(value = "page", required = false) String page, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                             @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계검토 단계 추가 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("p".equals(page)) {
            model.addAttribute("header", false);
        } else {
            model.addAttribute("header", true);
        }


//        // 저장 버튼
//        String[] btnId = {"DM_SETTING_C_02"};
//        String[] btnClass = {"btn _fill"};
//        String[] btnFun = {"onclick=\"detail.validate()\""};
//        String[] btnMsg = {"btn.006"};
//
//        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);
//
//        model.addAttribute("btnHtml", btnHtml);

        return "page/design/tool/setting/setting_c";
    }

    // 검토 단계 상세 조회
    @GetMapping("/setting/detail")
    @Description(name = "설계검토 > 검토 단계 상세 조회 화면", description = "설계검토 > 검토 단계 상세 조회 화면", type = Description.TYPE.MEHTOD)
    public String settingDetail(CommonReqVo commonReqVo, Model model, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("검토 단계 상세 조회 화면");
        systemLogComponent.addUserLog(userLog);

        model.addAttribute("header", false);

        // 수정 페이지 이동 버튼
        String[] btnId = {"DM_SETTING_U_01"};
        String[] btnClass = {"btn"};
        String[] btnFun = {"onclick=\"detail.modify()\""};
        String[] btnMsg = {"btn.003"};
        String[] btnEtc = {"id=\"updateBtn\""};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

        model.addAttribute("btnHtml", btnHtml);

        return "page/design/tool/setting/setting_r";
    }

    // 검토 단계 수정
    @GetMapping("/setting/edit")
    @Description(name = "설계검토 > 검토 단계 수정 화면", description = "결함 추적 관리 > 검토 단계 수정 화면", type = Description.TYPE.MEHTOD)
    public String settingEdit(CommonReqVo commonReqVo, Model model, @RequestParam(value = "page", required = false) String page, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                              @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계검토 단계 수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        if ("p".equals(page)) {
            model.addAttribute("header", false);
        } else {
            model.addAttribute("header", true);
        }

//        String[] userParam = commonReqVo.getUserParam();
//        // 저장 버튼
//        String[] btnId = {"DM_SETTING_C_01"};
//        String[] btnClass = {"btn"};
//        String[] btnFun = {"onclick=\"detail.validate()\""};
//        String[] btnMsg = {"btn.006"};
//        String[] btnEtc = {"id=\"saveBtn\""};
//
//        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
//
//        model.addAttribute("btnHtml", btnHtml);

        return "page/design/tool/setting/setting_u";
    }

    // 검토 요약
    @GetMapping("/reviewsummary")
    @Description(name = "설계 검토 관리 > 검토 요약 화면", description = "설계 검토 관리 > 검토 요약 화면 페이지", type = Description.TYPE.MEHTOD)
    public String summary(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계 검토 관리의 검토 요약 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/design/report/reviewsummary/reviewsummary";
    }

    // 검토 의견 보고서
    @GetMapping("/reviewcommentreport")
    @Description(name = "설계 검토 관리 > 검토 의견 보고서 화면", description = "설계 검토 관리 > 검토 의견 보고서 화면 페이지", type = Description.TYPE.MEHTOD)
    public String reviewcommentreport(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계 검토 관리의 검토 의견 보고서 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/design/report/reviewcommentreport/reviewcommentreport";
    }

    // 검토 의견 상세조회
    @GetMapping("/reviewcommentreportDetail")
    @Description(name = "설계 검토 관리 > 검토 의견 보고서 상세조회 화면", description = "설계 검토 관리 > 검토 의견 보고서 상세조회 화면 페이지", type = Description.TYPE.MEHTOD)
    public String reviewcommentreportDetail(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계 검토 관리의 검토 의견 보고서 상세조회 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/design/report/reviewcommentreport/reviewcommentreport_detail";
    }

    // 검토 의견 상세조회 새창
    @GetMapping("/reviewcommentreportDetailPopup")
    @Description(name = "설계 검토 관리 > 검토 의견 보고서 상세조회 새창", description = "설계 검토 관리 > 검토 의견 보고서 상세조회 새창 페이지", type = Description.TYPE.MEHTOD)
    public String reviewcommentreportDetailPopup(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("설계 검토 관리의 검토 의견 보고서 상세조회 새 창으로 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/design/report/reviewcommentreport/reviewcommentreport_detail_popup";
    }
}
