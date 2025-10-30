package kr.co.ideait.platform.gaiacairos.web.entrypoint.safety;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/safety/safety-diary")
public class SafetyDiaryPageController extends AbstractController {

    @Autowired
    PortalComponent portalComponent;

    @Value("${gaia.path.previewPath}")
    String imgDir;
    /**
     * 안전관리 > 안전일지 목록 화면
     * @param commonReqVo
     * @param request
     * @param model
     * @return
     */
    @GetMapping("")
    @Description(name = "안전일지 목록 조회 화면", description = "권한에 따른 버튼설정 후, 안전일지 목록 화면 페이지 반환", type = Description.TYPE.MEHTOD)
    public String getSafetyDiary(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {
        // Log
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전일지 목록 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 상단 기본버튼 삭제, 수정, 추가, 복사, 승인요청
        String[] btnId = { "SAFE_REP_D01", "SAFE_REP_U01", "SAFE_REP_C01",
                "SAFE_REP_C03",
                "SAFE_REP_AP01", "SAFE_REP_AP02" };
        String[] btnClass = { "btn btn_delete", "btn", "btn _fill", "btn btn_copy", "btn btn_apprvl", "btn btn_apprvl_cancel" };
        String[] btnFun = { "",
                "onclick=\"goLink('edit', 0);\"",
                "onclick=\"goLink('add', 0);\"",
                "",
                "", "" };
        String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.035", "btn.045", "btn.065" };

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);
        boolean isDelAuth = btnHtml.contains("btn_delete");
        boolean isAddAuth = btnHtml.contains("btn_copy");
        model.addAttribute("isDelAuth", isDelAuth ? "Y" : "N");
        model.addAttribute("isAddAuth", isAddAuth ? "Y" : "N");
        model.addAttribute("btnHtml", btnHtml);
        model.addAttribute("imgDir", imgDir);

        return "page/safety/safetydiary/safetydiary";
    }

    @GetMapping("/create")
    @Description(name = "안전일지 입력 화면", description = "안전일지 입력 화면 페이지 반환", type = Description.TYPE.MEHTOD)
    public String getSafetyDiaryAddPage(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {
        // Log
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전일지 입력 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/safetydiary/safetydiary_c";
    }

    @GetMapping("/detail")
    @Description(name = "안전일지 상세조회 화면", description = "안전일지 상세조회 화면 페이지 반환", type = Description.TYPE.MEHTOD)
    public String getSafetyDiaryDetailPage(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {
        // Log
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전일지 입력 화면 접속");
        systemLogComponent.addUserLog(userLog);

        // 메뉴 관리에서 등록한 버튼 아이디
        String[] btnId = { "SAFE_REP_U01"};

        // 버튼에 사용하는 클래스
        String[] btnClass = {"btn _outline btn_update"};

        // 버튼에 사용하는 함수
        String[] btnFun = { "onclick=\"page.update()\""};

        String[] btnMsg = { "btn.003"};

        String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

        model.addAttribute("btnHtml", btnHtml);

        return "page/safety/safetydiary/safetydiary_r";
    }


    @GetMapping("/update")
    @Description(name = "안전일지 수정 화면", description = "안전일지 수정 화면 페이지 반환", type = Description.TYPE.MEHTOD)
    public String getSafetyDiaryUpdatePage(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {
        // Log
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전일지 수정 화면 접속");
        systemLogComponent.addUserLog(userLog);

        return "page/safety/safetydiary/safetydiary_u";
    }


}
