package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@IsUser
@Controller
@RequestMapping("/construction/inspectionreport")
public class InspectionreportPageController extends AbstractController {

        @Autowired
        PortalService portalService;

        @Autowired
        PortalComponent portalComponent;

        @Value("${gaia.path.previewPath}")
        String imgDir;

        /**
         * 시공관리 > 감리일지 Main
         */
        @GetMapping("")
        @Description(name = "감리일지 목록 화면", description = "감리일지 목록 화면 페이지", type = Description.TYPE.MEHTOD)
        public String getInspectionReportList(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("감리일지 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                String[] btnId = { "INSPECTION_D_01", "INSPECTION_U_01", "INSPECTION_C_01", "INSPECTION_C_01",
                                "INS_AP_REQ" };

                String[] btnClass = { "btn _outline", "btn _outline", "btn _fill", "btn _outline",
                                "btn _outline" };

                String[] btnFun = { "onclick=\"reportGrid.deleteReport()\"",
                                "onclick=\"reportGrid.updateReport()\"",
                                "onclick=\"page.addReport()\"", "onclick=\"reportGrid.copyreport()\"",
                                "onclick=\"reportGrid.approvalRequest()\"" };

                String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.035", "btn.064" };

                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

                model.addAttribute("btnHtml", btnHtml);
                boolean isDelAuth = btnHtml.contains("reportGrid.deleteReport()");
                model.addAttribute("isDelAuth", isDelAuth);
                boolean isAddAuth = btnHtml.contains("reportGrid.copyreport()");
                model.addAttribute("isAddAuth", isAddAuth);
                model.addAttribute("imgDir",imgDir);

                return "page/construction/inspectionreport/inspectionreport";
        }

        /**
         * 감리일지 추가
         */
        @GetMapping("/addReport")
        @Description(name = "감리일지 추가 화면", description = "감리일지 추가 화면 페이지", type = Description.TYPE.MEHTOD)
        public String addInspectionReport(CommonReqVo commonReqVo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("감리일지 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/construction/inspectionreport/inspectionreport_c";
        }

        /**
         * 감리일지 수정
         */
        @GetMapping("/updateReport")
        @Description(name = "감리일지 수정 화면", description = "감리일지 수정 화면 페이지", type = Description.TYPE.MEHTOD)
        public String updateInspectionReport(CommonReqVo commonReqVo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("감리일지 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/construction/inspectionreport/inspectionreport_u";
        }

        /**
         * 감리일지 조회
         */
        @GetMapping("/getReport")
        @Description(name = "감리일지 상세 조회 화면", description = "감리일지 상세 조회 화면 페이지", type = Description.TYPE.MEHTOD)
        public String getInspectionReport(CommonReqVo commonReqVo, Model model) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("감리일지 상세 조회 화면 접속");
                systemLogComponent.addUserLog(userLog);

                String[] btnId = { "INSPECTION_U_01" };
                String[] btnClass = { "btn _outline" };
                String[] btnFun = { "onclick=\"updatePage()\"" };
                String[] btnMsg = { "btn.003" };

                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);
                model.addAttribute("btnHtml", btnHtml);

                return "page/construction/inspectionreport/inspectionreport_r";
        }

        /**
         * 세부공정변경 창
         */
        @GetMapping("/activity")
        @Description(name = "세부공정변경 모달창 화면", description = "세부공정변경 모달창 화면 페이지", type = Description.TYPE.MEHTOD)
        public String getActivity(CommonReqVo commonReqVo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("감리일지 세부공정변경 모달창 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/construction/inspectionreport/inspectionreport_activity_modal";
        }

        /**
         * 감리일지 추가 - 사진 추가 모달
         */
        @GetMapping("/photo")
        @Description(name = "사진 모달", description = "감리일지 사진 추가,수정 모달", type = Description.TYPE.MEHTOD)
        public String photoModal(CommonReqVo commonReqVo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("감리일지 사진 모달 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/construction/inspectionreport/inspectionreport_pic_modal";
        }
}
