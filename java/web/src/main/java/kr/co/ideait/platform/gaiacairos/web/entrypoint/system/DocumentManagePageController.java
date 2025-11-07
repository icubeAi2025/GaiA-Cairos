package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.iframework.annotation.Description.TYPE;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@IsUser
@Controller
@RequestMapping("/system/document")
public class DocumentManagePageController extends AbstractController {

    @Autowired
    PortalService portalService;

    @Autowired
    PortalComponent portalComponent;

	@GetMapping("")
	@Description(name = "착공계 관리 메인페이지", description = "착공계 관리 메인 페이지로 이동", type = TYPE.MEHTOD)
	public String home(CommonReqVo commonReqVo, @RequestParam("pjtNo") String pjtNo, @RequestParam("cntrctNo") String cntrctNo, Model model) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("착공계 관리 메인페이지 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/system/document/document";
	}
	
    @GetMapping("/property")
    @Description(name = "착공계 문서의 속성 데이터를 관리하는 페이지로 이동한다", description = "착공계 문서의 속성 데이터를 관리하는 페이지로 이동한다", type = TYPE.MEHTOD)
    public String naviPropertyPopup(CommonReqVo commonReqVo, @RequestParam("pjtNo") String pjtNo, @RequestParam("cntrctNo") String cntrctNo, Model model) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("착공계 문서의 속성 데이터를 관리하는 페이지");
        systemLogComponent.addUserLog(userLog);

        return "page/system/document/property/property_popup";
    }

    @GetMapping("/html")
    @Description(name = "착공계 문서의 HTML 양식 데이터를 관리하는 페이지로 이동한다", description = "착공계 문서의 HTML 양식 데이터를 관리하는 페이지로 이동한다", type = TYPE.MEHTOD)
    public String naviHtmlPopup(CommonReqVo commonReqVo, @RequestParam("pjtNo") String pjtNo, @RequestParam("cntrctNo") String cntrctNo, Model model) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("착공계 문서의 HTML 양식 데이터를 관리하는 페이지");
        systemLogComponent.addUserLog(userLog);

        return "page/system/document/html/html_popup";
    }

    @GetMapping("/pdf-view")
    @Description(name = "TODO", description = "",type = TYPE.MEHTOD)
    public String pdfViewPopup(CommonReqVo commonReqVo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("TODO");
        systemLogComponent.addUserLog(userLog);

        return "page/system/document/common/common_document_pdf_preview";
    }


    @GetMapping("/test")
    @Description(name = "TODO", description = "",type = TYPE.MEHTOD)
    public String test() {
    	return "page/system/document/document2";
    }

}
