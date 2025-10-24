package kr.co.ideait.platform.gaiacairos.web.entrypoint.portal;

import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.UserService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/portal")
public class PotalPageController extends AbstractController {

	@Autowired
    UserService userService;
	
	@Autowired
	PortalService portalService;
	
    // @GetMapping("")
    // public String portalLogin(
    // @CookieValue(name = "portal-x-auth", required = false) String loginId,
    // @Valid PortalForm.PortalLoginGet ref) {
    // log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    // log.debug("ref : >>>>>>>>" + ref.toString());
    // log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    //
    // if (loginId != null) {
    // String redirectUrl = ref.getRedirectUrl();
    // int idx = loginId.indexOf(":");
    // String id = loginId.substring(0, idx);
    // if (redirectUrl != null) {
    // redirectUrl += (redirectUrl.contains("?") ? "&" : "?");
    // if (ref.getLastPage() != null) {
    // redirectUrl += "&lastPage=" + ref.getLastPage();
    // }
    // } else {
    // redirectUrl = "/portal";
    // }
    // String redirect = redirectUrl != null ? redirectUrl : "/portal";
    // log.debug("-------------------------------------------------");
    // log.debug("redirect : >>>> " + redirect);
    // log.debug("-------------------------------------------------");
    // return "redirect:" + redirect;
    // } else {
    // log.debug("-------------------------------------------------");
    // log.debug(" 로그인 페이지로 이동 ");
    // log.debug("-------------------------------------------------");
    // return "page/portal/login";
    // }
    // }

//    /**
//     * 통합문서관리 PDF 미리보기 팝업
//     */
//    @GetMapping("/pdf-file/preview")
//    public String mainPdfPreviewPopup(CommonReqVo commonReqVo) {
//        return "sub/guide_pdf_view";
//    }
}
