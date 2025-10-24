package kr.co.ideait.platform.gaiacairos.web.entrypoint.projectcost;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@IsUser
@Controller
@RequestMapping("/projectcost")
public class PaymentPageController extends AbstractController {

        @Autowired
        PortalService portalService;

        @Autowired
        PortalComponent portalComponent;

        /**
         * 사업비관리 > 기성관리 Main
         */
        @GetMapping("/payment")
        @Description(name = "사업비관리 > 기성관리화면", description = "기성관리 메인페이지 화면", type = Description.TYPE.MEHTOD)
        public String getPayment(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

                if ("old".equals(btnAuthType)) {
                        String[] userParam = commonReqVo.getUserParam();

                        String[] btnId = { "BTN_M0302_D", "BTN_M0302_U", "BTN_M0302_C", "BTN_M0302_R" };

                        String[] btnClass = { "btn", "btn", "btn _fill", "btn" };

                        String[] btnFun = { "onclick=\"goLink('del', 0);\"", "onclick=\"goLink('edit', 0);\"",
                                        "onclick=\"goLink('add', 0);\"",
                                        "onclick=\"chkStatus('E', '" + messageSource.getMessage("btn.027", null,
                                                        LocaleContextHolder.getLocale()) + "');\"" };

                        String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.027" };

                        String btnHtml = portalService.selectBtnAuthorityList(userParam[1], userParam[0], userParam[2],
                                        pjtNo, cntrctNo, "M0302", btnId, btnClass, btnFun, btnMsg);

                        model.addAttribute("btnHtml", btnHtml);
                        model.addAttribute("isDelAuth", "Y");

                        // 승인,반려 버튼
                        // String[] chkBtnId = { "M0302_F_AP_A", "M0302_F_AP_R" };
                        String[] chkBtnId = {};
                        String[] chkBtnClass = { "btn", "btn" };
                        String[] chkBtnFun = {
                                        "onclick=\"chkStatus('A', '"
                                                        + messageSource.getMessage("btn.025", null,
                                                                        LocaleContextHolder.getLocale())
                                                        + "');\"",
                                        "onclick=\"chkStatus('R', '"
                                                        + messageSource.getMessage("btn.026", null,
                                                                        LocaleContextHolder.getLocale())
                                                        + "');\"",
                        };
                        String[] chkBtnMsg = { "btn.025", "btn.026" };
                        // String[] chkBtnMsg = { };

                        String chkBtnHtml = portalService.selectBtnAuthorityList(userParam[1], userParam[0],
                                        userParam[2],
                                        pjtNo, cntrctNo, "M0302", chkBtnId, chkBtnClass, chkBtnFun, chkBtnMsg);

                        model.addAttribute("chkBtnHtml", chkBtnHtml);
                        model.addAttribute("isDelAuth", "Y");
                } else {
                        String[] btnId = { "PAYMENT_D_01", "PAYMENT_U_01", "PAYMENT_C_01", "PAY_AP_REQ" };

                        String[] btnClass = { "btn", "btn", "btn _fill", "btn" };

                        String[] btnFun = { "onclick=\"goLink('del', 0);\"", "onclick=\"goLink('edit', 0);\"",
                                        "onclick=\"goLink('add', 0);\"",
                                        "onclick=\"chkStatus('E', '" + messageSource.getMessage("btn.045", null,
                                                        LocaleContextHolder.getLocale()) + "');\"" };

                        String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.045" };

                        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

                        model.addAttribute("btnHtml", btnHtml);
                        model.addAttribute("isDelAuth", "Y");

                        // 승인,반려 버튼
                        // String[] chkBtnId = { "M0302_F_AP_A", "M0302_F_AP_R" };
                        String[] chkBtnId = {};
                        String[] chkBtnClass = { "btn", "btn" };
                        String[] chkBtnFun = {
                                        "onclick=\"chkStatus('A', '"
                                                        + messageSource.getMessage("btn.025", null,
                                                                        LocaleContextHolder.getLocale())
                                                        + "');\"",
                                        "onclick=\"chkStatus('R', '"
                                                        + messageSource.getMessage("btn.026", null,
                                                                        LocaleContextHolder.getLocale())
                                                        + "');\"",
                        };
                        String[] chkBtnMsg = { "btn.025", "btn.026" };
                        // String[] chkBtnMsg = { };

                        String chkBtnHtml = portalComponent.selectBtnAuthorityList(chkBtnId, chkBtnClass, chkBtnFun,
                                        chkBtnMsg);

                        model.addAttribute("chkBtnHtml", chkBtnHtml);
                        model.addAttribute("isDelAuth", "Y");

                        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                        userLog.setLogType(LogType.VIEW.name());
                        userLog.setExecType("기성 관리 화면 접속");
                        systemLogComponent.addUserLog(userLog);
                }

                if ("cairos".equals(platform) || "gaia".equals(platform)) {
                        model.addAttribute("system", "G");
                } else {
                        model.addAttribute("system", "P");
                }

                return "page/projectcost/payment/payment";
        }

        /**
         * 사업비관리 > 기성 추가 및 상세
         */
        @GetMapping("/payment/detail")
        @Description(name = "사업비관리 > 기성관리 추가,수정화면", description = "기성관리 기성 추가, 수정 화면", type = Description.TYPE.MEHTOD)
        public String getPaymentDetail(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "sType", required = false) String sType,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                        @RequestParam(value = "sNo", required = false) Integer sNo) {
                if ("d".equals(type)) {
                        model.addAttribute("header", true);
                } else if ("p".equals(type)) {
                        model.addAttribute("header", false);
                }

                if ("cairos".equals(platform) || "gaia".equals(platform)) {
                        model.addAttribute("system", "G");
                } else {
                        model.addAttribute("system", "P");
                }

                String[] userParam = commonReqVo.getUserParam();
                String[] pjtParam = commonReqVo.getPjtParam();

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());

                // stype a:추가 e: 수정
                if(("d").equals(sType) == false) {
                        String[] btnId = {"PAYMENT_C_02"};

                        String[] btnClass = {"btn _outline"};

                        String[] btnFun = {""};

                        // 저장
                        String[] btnMsg = {"btn.006"};

                        String[] btnEtc = {"id='save'"};


                        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
                        model.addAttribute("btnHtml", btnHtml);
                } else {
                        // 상세보기인 경우
                        String[] btnId = {"PAYMENT_U_02"};

                        String[] btnClass = {"btn _outline"};

                        String[] btnFun = {""};

                        // 저장
                        String[] btnMsg = {"btn.003"};

                        String[] btnEtc = {"id='edit'"};


                        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
                        model.addAttribute("btnHtml", btnHtml);
                }

                userLog.setExecType("기성 관리 추가, 수정 화면 접속");

                systemLogComponent.addUserLog(userLog);

                return "page/projectcost/payment/payment_u";
        }

        /**
         * 사업비관리 > 기성내역서
         */
        @GetMapping("/payment/history")
        @Description(name = "사업비관리 > 기성내역서 조회 화면", description = "기성내역서 화면", type = Description.TYPE.MEHTOD)
        public String getPaymentHistory(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                        @RequestParam(value = "sNo", required = false) Integer sNo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("기성 내역서 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if ("cairos".equals(platform) || "gaia".equals(platform)) {
                        return "page/projectcost/payment/payment_history";
                } else {
                        return "page/projectcost/payment/payment_history_pgaia";
                }
        }

        /**
         * 사업비관리 > 기성내역서 > 기성 내역수량 재집계
         */
        @GetMapping("/payment/recount")
        @Description(name = "기성내역서 재집계 화면", description = "기성내역서 재집계 조회 화면", type = Description.TYPE.MEHTOD)
        public String getPaymentRecount(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                        @RequestParam(value = "sNo", required = false) Integer sNo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("기성내역서 재집계 화면 접속");

                return "page/projectcost/payment/payment_recount";
        }
}
