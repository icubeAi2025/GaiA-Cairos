package kr.co.ideait.platform.gaiacairos.web.entrypoint.project;

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
@RequestMapping("/project/subcontract")
public class SubcontractPageController extends AbstractController {

        @Autowired
        PortalService portalService;

        @Autowired
        PortalComponent portalComponent;

        // 하도급 목록
        @GetMapping("")
        @Description(name = "하도급 관리 > 하도급,하도급 계약변경 목록 화면", description = "하도급 관리 > 하도급,하도급 계약변경 목록 화면", type = Description.TYPE.MEHTOD)
        public String subcontract(CommonReqVo commonReqVo, Model model) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("하도급관리 목록 조회 화면 접속");
                systemLogComponent.addUserLog(userLog);

                // 하도급 excel x
                String[] btnId = { "SUBCNTRCT_D_01", "SUBCNTRCT_U_02", "SUBCNTRCT_C_02" };
                String[] btnClass = { "btn _outline", "btn _outline", "btn _fill" };
                String[] btnFun = { "onclick='subGrid.subDelete()'", "onclick='subGrid.subUpdate()'",
                        "onclick='subGrid.subCreate()'" };
                String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.023" };

                String btnHtml = portalComponent.selectBtnAuthorityList( btnId, btnClass, btnFun, btnMsg);

                // 하도급 계약변경
                String[] btnId2 = { "SUBCNTRCT_CHG_D_01", "SUBCNTRCT_CHG_U_02", "SUBCNTRCT_CHG_C_02" };
                String[] btnClass2 = { "btn _outline", "btn _outline", "btn _fill" };
                String[] btnFun2 = { "onclick='changeGrid.changeDelete()'", "onclick='changeGrid.changeUpdate()'",
                        "onclick='changeGrid.changeCreate()'" };
                String[] btnMsg2 = { "btn.002", "btn.003", "btn.001" };

                String btnHtml2 = portalComponent.selectBtnAuthorityList( btnId2, btnClass2, btnFun2, btnMsg2);

                boolean isSubDelAuth = btnHtml.contains("subDelete");
                boolean isChgDelAuth = btnHtml.contains("changeDelete");
                model.addAttribute("btnHtml", btnHtml);
                model.addAttribute("btnHtml2", btnHtml2);
                model.addAttribute("isSubDelAuth", isSubDelAuth);
                model.addAttribute("isChgDelAuth", isChgDelAuth);

                return "page/project/subcontract/subcontract";
        }

        // 하도급 추가
        @GetMapping("/create")
        @Description(name = "하도급 추가 화면", description = "하도급 추가 화면", type = Description.TYPE.MEHTOD)
        public String createSubcontractForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                @RequestParam(value = "pjtNo", required = false) String pjtNo, @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("하도급관리 하도급 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/project/subcontract/subcontract_c";
        }

        // 하도급 수정
        @GetMapping("/update")
        @Description(name = "하도급 수정 화면", description = "하도급 수정 화면", type = Description.TYPE.MEHTOD)
        public String updateSubcontractForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                @RequestParam(value = "pjtNo", required = false) String pjtNo, @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("하도급관리 하도급 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/project/subcontract/subcontract_u";
        }

        // 하도급 조회
        @GetMapping("/read")
        @Description(name = "하도급 상세 조회 화면", description = "하도급 상세 조회 화면", type = Description.TYPE.MEHTOD)
        public String readSubcontractForm(CommonReqVo commonReqVo,  Model model) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("하도급관리 하도급 상세조회 화면 접속");
                systemLogComponent.addUserLog(userLog);

                String[] btnId = { "SUBCNTRCT_U_02" };
                String[] btnClass = { "btn" };
                String[] btnFun = { "onclick='page.enableInputs()'" };
                String[] btnMsg = { "btn.003" };
                String[] btnEtc = { "id='action-button'" };

                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

                model.addAttribute("btnHtml", btnHtml);

                return "page/project/subcontract/subcontract_r";
        }

        // 하도급 계약변경 추가
        @GetMapping("/changeCreate")
        @Description(name = "하도급 계약변경 추가 화면", description = "하도급 계약변경 추가 화면", type = Description.TYPE.MEHTOD)
        public String createSubcontractChangeForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                @RequestParam(value = "pjtNo", required = false) String pjtNo, @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("하도급관리 하도급계약변경 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/project/subcontract/subcontractChange_c";
        }

        // 하도급 계약변경 수정
        @GetMapping("/changeUpdate")
        @Description(name = "하도급 계약변경 수정 화면", description = "하도급 계약변경 수정 화면", type = Description.TYPE.MEHTOD)
        public String updateSubcontractChangeForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                @RequestParam(value = "pjtNo", required = false) String pjtNo, @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("하도급관리 하도급계약변경 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/project/subcontract/subcontractChange_u";
        }

        // 하도급 계약변경 조회
        @GetMapping("/changeRead")
        @Description(name = "하도급 계약변경 상세 조회 화면", description = "하도급 계약변경 상세 조회 화면", type = Description.TYPE.MEHTOD)
        public String readSubcontractChangeForm(CommonReqVo commonReqVo, Model model) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("하도급관리 하도급계약변경 상세 조회 화면 접속");
                systemLogComponent.addUserLog(userLog);


                String[] btnId = { "SUBCNTRCT_CHG_U_02" };
                String[] btnClass = { "btn" };
                String[] btnFun = { "onclick='page.enableInputs()'" };
                String[] btnMsg = { "btn.003" };
                String[] btnEtc = { "id='action-button'" };

                String btnHtml = portalComponent.selectBtnAuthorityList( btnId, btnClass, btnFun, btnMsg, btnEtc);
                model.addAttribute("btnHtml", btnHtml);


                return "page/project/subcontract/subcontractChange_r";
        }

}
