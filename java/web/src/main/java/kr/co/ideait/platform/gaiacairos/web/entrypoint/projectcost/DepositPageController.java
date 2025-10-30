package kr.co.ideait.platform.gaiacairos.web.entrypoint.projectcost;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.iframework.annotation.Description;
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
public class DepositPageController extends AbstractController {

        @Autowired
        PortalService portalService;

        @Autowired
        PortalComponent portalComponent;

        /**
         * 사업비관리 > 선금 및 공제금 Main
         */
        @GetMapping("/deposit")
        @Description(name = "선급금 및 공제금 화면", description = "선급금 및 공제금 화면 페이지", type = Description.TYPE.MEHTOD)
        public String getDeposit(CommonReqVo commonReqVo, @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo, HttpServletRequest request,
                        Model model) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("선급금 및 공제금 화면 접속");
                systemLogComponent.addUserLog(userLog);

                // 상단 기본버튼
                String[] btnId = { "DEP_D_01", "DEP_U_01", "DEP_C_02", "DEP_AP_REQ" };
                String[] btnClass = { "btn", "btn", "btn _fill", "btn" };
                String[] btnFun = { "onclick=\"goLink('del', 0);\"", "onclick=\"goLink('edit', 0);\"",
                                "onclick=\"goLink('add', 0);\"", "onclick=\"chkStatus('E', '"
                                                + messageSource.getMessage("btn.027", null,
                                                                LocaleContextHolder.getLocale())
                                                + "');\"" };
                String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.027" };

                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

                model.addAttribute("btnHtml", btnHtml);
                boolean isDelAuth = btnHtml.contains("goLink('del', 0)");
                model.addAttribute("isDelAuth", isDelAuth);

                return "page/projectcost/deposit/deposit";
        }

        /**
         * 사업비관리 > 선금 및 공제금 추가 및 상세
         */
        @GetMapping("/deposit/detail")
        @Description(name = "선급금 및 공제금 추가 및 상세조회 화면", description = "선급금 및 공제금 추가 및 상세조회 화면 페이지", type = Description.TYPE.MEHTOD)
        public String getDepositDetail(CommonReqVo commonReqVo,
                        HttpServletRequest request, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "sType", required = false) String sType,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                        @RequestParam(value = "sNo", required = false) Integer sNo,
                        @RequestParam("pjtNo") String pjtNo) {

                if ("d".equals(type)) {
                        model.addAttribute("header", true);
                } else if ("p".equals(type)) {
                        model.addAttribute("header", false);
                }

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("선금 및 공제금 추가 및 상세 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/projectcost/deposit/deposit_u";
        }
}
