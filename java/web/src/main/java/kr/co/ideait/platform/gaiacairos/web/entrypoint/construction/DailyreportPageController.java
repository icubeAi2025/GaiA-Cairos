package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

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

import java.util.Objects;

@IsUser
@Controller
@RequestMapping("/construction")
public class DailyreportPageController extends AbstractController {

        @Autowired
        PortalService portalService;

        @Autowired
        PortalComponent portalComponent;

        /**
         * 시공관리 > 작업일지 리스트
         */
        @GetMapping("/dailyreport")
        @Description(name = "작업일지 리스트", description = "작업일지 리스트 페이지", type = Description.TYPE.MEHTOD)
        public String getDailyreport(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {

                // 사용자 로그 추가
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("작업일지 리스트 접속");
                
                // 상단 기본버튼 삭제, 수정, 추가, 복사, 승인요청, 승인취소
                String[] btnId = { "DAILYREPORT_D_01", "DAILYREPORT_U_01", "DAILYREPORT_C_01", "DAILYREPORT_C_01", "DAY_AP_REQ", "DAY_AP_CANCEL" };
                String[] btnMsg = { "btn.002", "btn.003", "btn.001", "btn.035", "btn.045", "btn.065" };
                String[] btnClass = { "btn", "btn", "btn _fill", "btn", "btn", "btn" };

                String[] btnFun = { "onclick=\"goLink('del', 0);\"", "onclick=\"goLink('edit', 0);\"", "onclick=\"goLink('add', 0);\"", "onclick=\"copy('b', '');\"",
                                        "onclick=\"chkStatus('E', '" + messageSource.getMessage("btn.045", null, LocaleContextHolder.getLocale()) + "');\"",
                        "onclick=\"chkStatus('C', '" + messageSource.getMessage("btn.065", null, LocaleContextHolder.getLocale()) + "');\"" };

                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);
                        
                boolean isDelAuth = btnHtml.contains("goLink('del', 0)");
                boolean isAddAuth = btnHtml.contains("copy('b', '')");

                String imgDir = previewPath.replaceAll("(upload[/\\\\]?).*$", "");

                model.addAttribute("isDelAuth", isDelAuth ? "Y" : "N");
                model.addAttribute("isAddAuth", isAddAuth ? "Y" : "N");
                model.addAttribute("imgDir", imgDir);
                
                model.addAttribute("btnHtml", btnHtml);
                
                return "page/construction/dailyreport/dailyreport";
        }
        
        /**
         * 시공관리 > 작업일지 추가 및 상세
         */
        @GetMapping("/dailyreport/detail")
        @Description(name = "작업일지 추가 및 상세 화면", description = "작업일지 추가 및 상세 화면 페이지", type = Description.TYPE.MEHTOD)
        public String getDailyreportDetail(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                                           @RequestParam(value = "type", required = false) String type,
                                           @RequestParam(value = "sType", required = false) String sType,
                                           @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                                           @RequestParam(value = "sNo", required = false) Integer sNo,
                                           @RequestParam(value = "pjtNo", required = false) String pjtNo) {

                String[] btnId = new String[]{};
                String[] btnClass =  new String[]{};
                String[] btnFun =  new String[]{};
                String[] btnMsg =  new String[]{};
                String[] btnEtc =  new String[]{};
                String btnHtml = "";

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                if (Objects.equals(sType, "e")) {        // 수정
                        btnId = new String[] { "DAILYREPORT_C_01" };
                        btnClass = new String[] { "btn _outline" };
                        btnFun = new String[] { "" };
                        btnMsg = new String[] { "btn.006" };
                        btnEtc = new String[] { "id='save' style='display:none'"};

                        btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
                        model.addAttribute("btnHtml", btnHtml);


                        // 금일 실적 변경 모달창
                        String[] todayBtnId = { "DAILYREPORT_R_04" };
                        String[] todayBtnClass = { "btn _outline" };
                        String[] todayBtnFun = { "" };
                        String[] todayBtnMsg = { "item.construction.025" };
                        String[] todayBtnEtc = { " id=\"chgToday\"" };

                        String todayBtnHtml = portalComponent.selectBtnAuthorityList(todayBtnId, todayBtnClass,
                                todayBtnFun,
                                todayBtnMsg,
                                todayBtnEtc);
                        model.addAttribute("todayBtnHtml", todayBtnHtml);

                        // 명일 실적 변경 모달창
                        String[] nextBtnId = { "DAILYREPORT_R_05" };
                        String[] nextBtnClass = { "btn _outline" };
                        String[] nextBtnFun = { "" };
                        String[] nextBtnMsg = { "item.construction.035" };
                        String[] nextBtnEtc = { " id=\"chgNext\"" };

                        String nextBtnHtml = portalComponent.selectBtnAuthorityList(nextBtnId, nextBtnClass, nextBtnFun,
                                nextBtnMsg,
                                nextBtnEtc);
                        model.addAttribute("nextBtnHtml", nextBtnHtml);

                        // 금일 실적 변경 저장
                        String[] chgTodayBtnId = { "DAILYREPORT_U_02" };
                        String[] chgTodayBtnClass = { "btn _fill" };
                        String[] chgTodayBtnFun = { "onclick=\"goSaveChgToday()\"" };
                        String[] chgTodayBtnMsg = { "btn.006" };

                        String chgTodayBtnHtml = portalComponent.selectBtnAuthorityList(chgTodayBtnId, chgTodayBtnClass,
                                chgTodayBtnFun,
                                chgTodayBtnMsg);
                        model.addAttribute("chgTodayBtnHtml", chgTodayBtnHtml);

                        // 명일 실적 변경 저장
                        String[] chgNextBtnId = { "DAILYREPORT_U_02" };
                        String[] chgNextBtnClass = { "btn _fill" };
                        String[] chgNextBtnFun = { "onclick=\"goSaveChgNext()\"" };
                        String[] chgNextBtnMsg = { "btn.006" };

                        String chgNextBtnHtml = portalComponent.selectBtnAuthorityList(chgNextBtnId, chgNextBtnClass,
                                chgNextBtnFun,
                                chgNextBtnMsg);
                        model.addAttribute("chgNextBtnHtml", chgNextBtnHtml);

                        userLog.setExecType("작업일지 수정 화면 접속");
                } else if (sType.equals("d")) {
                        // 상세 보기
                        btnId = new String[] { "DAILYREPORT_U_01" };
                        btnClass = new String[] { "btn _outline" };
                        btnFun = new String[] { "" };
                        btnMsg = new String[] { "btn.003" };
                        btnEtc = new String[] { "id='edit' style='display:none'"};

                        btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
                        model.addAttribute("btnHtml", btnHtml);

                        userLog.setExecType("작업일지 상세 화면 접속");
                } else if(sType.equals("a")) {
                        // 추가
                        btnId = new String[] { "DAILYREPORT_C_01" };            // 추가
                        btnClass = new String[] { "btn _outline" };
                        btnFun = new String[] { ""};
                        btnMsg = new String[] { "btn.006" };
                        btnEtc = new String[] { "id='save' style='display:none'"};

                        btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
                        model.addAttribute("btnHtml", btnHtml);

                        userLog.setExecType("작업일지 추가 화면 접속");
                }



                systemLogComponent.addUserLog(userLog);

                return "page/construction/dailyreport/dailyreport_u";
        }

        /**
         * 시공관리 > 작업일지 공정사진 등록
         */
        @GetMapping("/dailyreport-pic")
        @Description(name = "작업일지 공정사진 등록 화면", description = "작업일지 공정사진 등록 화면 페이지", type = Description.TYPE.MEHTOD)
        public String setDailyreportPic(CommonReqVo commonReqVo, Model model, @RequestParam(value = "code", required = false) String code,
                                        @RequestParam(value = "no", required = false) String no,
                                        @RequestParam(value = "type", required = false) String type) {
                return "page/construction/dailyreport/dailyreport_pic";
        }

        /**
         * 시공관리 > 작업일지 금일 실적변경
         */
        @GetMapping("/dailyreport-today")
        @Description(name = "작업일지 금일 실적변경 화면", description = "작업일지 금일 실적변경 화면 페이지", type = Description.TYPE.MEHTOD)
        public String setDailyreportToday(CommonReqVo commonReqVo,
                                          @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                                          @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                          @RequestParam(value = "code", required = false) String code,
                                          @RequestParam(value = "no", required = false) String no,
                                          @RequestParam(value = "type", required = false) String type,
                                          HttpServletRequest request, Model model
        ) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("작업일지 금일 실적 변경 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/construction/dailyreport/dailyreport_today";
        }

        /**
         * 시공관리 > 작업일지 명일 실적변경
         */
        @GetMapping("/dailyreport_next")
        @Description(name = "작업일지 명일 실적변경 화면", description = "작업일지 명일 실적변경 화면 페이지", type = Description.TYPE.MEHTOD)
        public String setDailyreportNext(HttpServletRequest request,
                                         CommonReqVo commonReqVo,
                                         Model model,
                                         @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                                         @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                         @RequestParam(value = "code", required = false) String code,
                                         @RequestParam(value = "no", required = false) String no,
                                         @RequestParam(value = "type", required = false) String type) {


                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("작업일지 명일 실적 변경 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/construction/dailyreport/dailyreport_next";
        }
}