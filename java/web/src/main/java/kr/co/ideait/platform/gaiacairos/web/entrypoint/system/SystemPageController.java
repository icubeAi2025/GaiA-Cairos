package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
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
@RequestMapping("/system")
public class SystemPageController extends AbstractController {

        @Autowired
        PortalService portalService;

        @Autowired
        PortalComponent portalComponent;


        /**
         * 이데아/PCES 업체 검색 모달창
         */
        @GetMapping("/company/search-company-popup")
        @Description(name = "이데아/PCES 업체 검색 모달창", description = "이데아/PCES 업체 검색 모달창", type = Description.TYPE.MEHTOD)
        public String searchCompanyPopup(CommonReqVo commonReqVo) {
                return "page/system/company/company-search-popup";
        }

        /**
         * 회사관리
         */
        // @IsAdmin
        @GetMapping("/company")
        // Role: ADMIN
        @Description(name = "회사관리 화면", description = "권한에 따른 버튼설정 후, 회사관리 화면 페이지 반환.", type = Description.TYPE.MEHTOD)
        public String company(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("회사관리 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.isEmpty() || cntrctNo == null || cntrctNo.isEmpty()) {
                        return "redirect:/";
                }

                // ==========회사관리 버튼=========//
                // 메뉴 관리에서 등록한 버튼 아이디
                String[] compBtnId = { "COMPANY_D_01", "COMPANY_U_01", "COMPANY_C_01" };
                // 버튼에 사용하는 클래스
                String[] compBtnClass = { "btn _outline", "btn _outline", "btn _fill" };
                // 버튼에 사용하는 함수
                String[] compBtnFun = { "onclick=\"page.company.deleteCompanyList()\"",
                        "onclick=\"page.company.moveUpdateCompany()\"",
                        "onclick=\"page.company.moveCreateCompany()\"" };
                // 버튼에 사용하는 메시지 아이디
                String[] compBtnMsg = { "btn.002", "btn.003", "btn.001" };

                String compBtnHtml = portalComponent.selectBtnAuthorityList(compBtnId, compBtnClass, compBtnFun, compBtnMsg);

                boolean isDelAuth = compBtnHtml.contains("delete");

                model.addAttribute("compBtnHtml", compBtnHtml);
                model.addAttribute("isDelAuth", isDelAuth);

                return "page/system/company/company";

        }

        /**
         * 회사관리 > 회사 등록
         */
        @GetMapping("/company/create")
        @Description(name = "회사관리 수정/등록 화면", description = "type에 따라 새창 또는 페이지로 수정, 등록 화면 반환.", type = Description.TYPE.MEHTOD)
        public String getCompanyForm(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        HttpServletRequest request,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("회사관리 회사 등록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.isEmpty() || cntrctNo == null || cntrctNo.isEmpty()) {
                        log.info("프로젝트 정보가 없습니다.");
                        return "redirect:/";
                }

                if (type != null) {
                        if ("p".equals(type)) {
                                model.addAttribute("header", true);
                        }
                } else {
                        model.addAttribute("header", false);
                }

                return "page/system/company/company_c";

        }

        /**
         * 회사관리 > 회사 수정
         */
        @GetMapping("/company/update")
        @Description(name = "회사관리 수정화면", description = "type에 따라 새창 또는 페이지로 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String getCompanyUpdateForm(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        HttpServletRequest request,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("회사관리 회사 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.isEmpty() || cntrctNo == null || cntrctNo.isEmpty()) {
                        log.info("프로젝트 정보가 없습니다.");
                        return "redirect:/";
                }

                if (type != null) {
                        if ("p".equals(type)) {
                                model.addAttribute("header", true);
                        }
                } else {
                        model.addAttribute("header", false);
                }


                return "page/system/company/company_u";

        }

        /**
         * 회사상세조회
         */
        // @IsAdmin
        @GetMapping("/company/{corpNo}")
        @Description(name = "회사관리 상세조회 화면", description = "type에 따라 새창 또는 페이지로 상세조회 화면 반환.", type = Description.TYPE.MEHTOD)
        public String getCompany(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "pjtNo") String pjtNo, @RequestParam(value = "cntrctNo") String cntrctNo,
                        HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("회사관리 회사 조회 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                if (type != null) {
                        if ("p".equals(type)) {
                                model.addAttribute("header", true);
                        }
                } else {
                        model.addAttribute("header", false);
                }

                // ==========회사 수정 버튼=========//
                String[] btnId = new String[] { "COMPANY_U_02" };
                String[] btnClass = { "btn _outline" };
                String[] btnFun = { "onclick=\"popup.moveUpdateCompany()\"" };
                String[] btnMsg = { "btn.003" };
                String[] btnEtc = { "id=\"update-button\"" };

                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
                model.addAttribute("btnHtml", btnHtml);


                return "page/system/company/company_r";

        }

        /**
         * 조직관리 > 부서관리
         */
        // Role: ADMIN, USER
        @GetMapping("/department")
        @Description(name = "부서관리 화면", description = "부서관리 화면 페이지 반환.", type = Description.TYPE.MEHTOD)
        public String department(CommonReqVo commonReqVo, @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo, HttpServletRequest request, Model model) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("부서관리 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                String[] deptBtnId = { "DEPARTMENT_D_01", "DEPARTMENT_U_03", "DEPARTMENT_C_03" };
                String[] deptBtnClass = { "btn _outline", "btn _outline", "btn _point" };
                String[] deptBtnFun = { "onclick=\"page.department.delete()\"", "onclick=\"page.department.update()\"",
                        "onclick=\"page.department.create()\"" };
                String[] deptBtnMsg = { "btn.002", "btn.003", "btn.031" };

                String deptBtnHtml = portalComponent.selectBtnAuthorityList(deptBtnId, deptBtnClass, deptBtnFun, deptBtnMsg);

                // ==========부서관리 - 소속 직원 정보 버튼=========//
                // 메뉴 관리에서 등록한 버튼 아이디
                String[] orgBtnId = { "DEPARTMENT_D_02", "DEPARTMENT_U_04", "DEPARTMENT_C_04" };
                // 버튼에 사용하는 클래스
                String[] orgBtnClass = { "btn _outline", "btn _outline", "btn _fill" };
                // 버튼에 사용하는 함수
                String[] orgBtnFun = { "onclick=\"page.department.employee.delete()\"",
                        "onclick=\"page.department.employee.update()\"",
                        "onclick=\"page.department.employee.create()\"" };
                // 버튼에 사용하는 메시지 아이디
                String[] orgBtnMsg = { "btn.002", "btn.003", "btn.001" };

                String orgBtnHtml = portalComponent.selectBtnAuthorityList(orgBtnId, orgBtnClass, orgBtnFun, orgBtnMsg);

                model.addAttribute("deptBtnHtml", deptBtnHtml);
                model.addAttribute("orgBtnHtml", orgBtnHtml);

                log.debug("======================html========================");
                log.debug("btnHtml={}", deptBtnHtml);
                log.debug("btnHtml={}", orgBtnHtml);
                log.debug("======================end========================");

                boolean isDelAuth = deptBtnHtml.contains("delete");
                model.addAttribute("isDelAuth", isDelAuth);

                return "page/system/department/department";

        }

        /**
         * 조직관리 > 부서관리 > 하위부서 추가 / 수정
         */
        // Role: ADMIN, USER
        @GetMapping("/department-popup/create")
        @Description(name = "부서관리 하위부서 추가 화면", description = "type에 따라 새창 또는 페이지로 하위부서 추가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String departmentPopupCreate(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "pjtNo") String pjtNo, @RequestParam(value = "cntrctNo") String cntrctNo,
                        HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("부서관리 하위 부서 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }


                return "page/system/department/department_c";

        }

        /**
         * 조직관리 > 부서관리 > 부서 수정
         */
        // Role: ADMIN, USER
        @GetMapping("/department-popup/update")
        @Description(name = "부서관리 하위부서 수정 화면", description = "type에 따라 새창 또는 페이지로 하위부서 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String departmentPopupUpdate(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "pjtNo") String pjtNo, @RequestParam(value = "cntrctNo") String cntrctNo,
                        HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("부서관리 부서 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                return "page/system/department/department_u";

        }

        // Role: ADMIN, USER
        @GetMapping("/department-emploee-popup")
        @Description(name = "부서관리 소속 직원 추가 화면", description = "type에 따라 새창 또는 페이지로 소속 직원 추가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String departmentEmploeePopup(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "pjtNo") String pjtNo, @RequestParam(value = "cntrctNo") String cntrctNo,
                        HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("부서관리 소속 직원 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                if (type != null) {
                        if ("p".equals(type)) {
                                model.addAttribute("header", true);
                        }
                } else {
                        model.addAttribute("header", false);
                }

                return "page/system/department/department_emploee_c";

        }

        // Role: ADMIN, USER
        @GetMapping("/department-emploee-popup/update")
        @Description(name = "부서관리 소속 직원 수정 화면", description = "소속 직원 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String departmentEmploeePopupUpdate(CommonReqVo commonReqVo, @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo, Model model, HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("부서관리 소속 직원 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                return "page/system/department/department_emploee_u";

        }

        // Role: ADMIN, USER
        @GetMapping("/department/emploee-detail-popup")
        @Description(name = "부서관리 소속 직원 상세조회 화면", description = "소속 직원 상세조회 화면 반환.", type = Description.TYPE.MEHTOD)
        public String departmentEmploeeDetailPopup(CommonReqVo commonReqVo, HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("부서관리 소속 직원 조회 화면 접속");
                systemLogComponent.addUserLog(userLog);

                String[] param = commonReqVo.getUserParam();

                return param[1].equals("ADMIN") ? "page/system/department/department_emploee_info_admin"
                                : "page/system/department/department_emploee_info_gc";
        }

        /**
         * 조직관리 > 사용자관리
         */
        // Role: ADMIN, USER
        @GetMapping("/user")
        @Description(name = "사용자관리 화면", description = "권한에 따른 버튼설정 후, 사용자관리 화면 페이지 반환.", type = Description.TYPE.MEHTOD)
        public String user(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("사용자 관리 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                String[] btnId = { "USER_C_01" };
                String[] btnClass = { "btn _fill" };
                String[] btnFun = { "onclick=\"page.user.syncOracleUser()\"" };
                String[] btnMsg = { "btn.001" };

                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);
                model.addAttribute("btnHtml", btnHtml);

                model.addAttribute("isDelAuth", "Y"); // 삭제 권한이 있으면 Y 없으면 N

                return "page/system/user/user";

        }

        @GetMapping("/user/user-sync-list")
        @Description(name = "WBSGEN - EURECA 사용자 목록 화면", description = "EURECA 사용자 목록 화면을 반환한다.", type = Description.TYPE.MEHTOD)
        public String userSyncList(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                                   @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                   @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("사용자 관리 EURECA 사용자 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                return "page/system/user/user_p";

        }

        @Deprecated
        @GetMapping("/user/create")
        @Description(name = "사용자관리 등록 화면", description = "type에 따라 새창 또는 페이지로 등록 화면 반환.", type = Description.TYPE.MEHTOD)
        public String createUserForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("사용자 관리 사용자 등록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if ("d".equals(type)) {
                        model.addAttribute("header", true);
                } else if ("p".equals(type)) {
                        model.addAttribute("header", false);
                }


                return "page/system/user/user_c";
        }

        @GetMapping("/user/update")
        @Description(name = "사용자관리 수정 화면", description = "type에 따라 새창 또는 페이지로 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String updateUserForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "type", required = false) String type) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("사용자 관리 사용자 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if ("d".equals(type)) {
                        model.addAttribute("header", true);
                } else if ("p".equals(type)) {
                        model.addAttribute("header", false);
                }

                return "page/system/user/user_u";

        }

        @GetMapping("/user/read")
        @Description(name = "사용자관리 조회 화면", description = "type에 따라 새창 또는 페이지로 조회 화면 반환.", type = Description.TYPE.MEHTOD)
        public String getReadUserForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "type", required = false) String type) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("사용자 관리 사용자 조회 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if ("d".equals(type)) {
                        model.addAttribute("header", true);
                } else if ("p".equals(type)) {
                        model.addAttribute("header", false);
                }


                String[] btnId = { "USER_U_02" };
                String[] btnClass = { "btn" };
                String[] btnFun = { "onclick='page.enableInputs()'" };
                String[] btnMsg = { "btn.003" };
                String[] btnEtc = { "id='action-button'" };

                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
                model.addAttribute("btnHtml", btnHtml);

                return "page/system/user/user_r";

        }

        /**
         * c
         * 시스템관리 > 메뉴관리
         */
        // @IsAdmin
        @GetMapping("/menu")
        @Description(name = "메뉴관리 화면", description = "권한에 따른 버튼설정 후, 메뉴관리 화면 페이지 반환.", type = Description.TYPE.MEHTOD)
        public String menu(CommonReqVo commonReqVo, Model model, HttpServletRequest request,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("메뉴 관리 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                // ==========메뉴관리 - 메뉴 버튼=========//
                String[] mBtnId = { "MENU_D_01", "MENU_U_01", "MENU_C_01" };
                String[] mBtnClass = { "btn _outline", "btn _outline", "btn _point" };
                String[] mBtnFun = { "onclick=\"page.menu.delete()\"", "onclick=\"page.menu.update()\"",
                        "onclick=\"page.menu.create()\"" };
                String[] mBtnMsg = { "btn.002", "btn.003", "btn.013" };
                String[] mBtnEtc = { "", "", "id=\"subMenuAddButton\"" };

                String mBtnHtml = portalComponent.selectBtnAuthorityList(mBtnId, mBtnClass, mBtnFun, mBtnMsg, mBtnEtc);

                // ==========메뉴관리 - 버튼 권한 버튼=========//
                String[] mbBtnId = { "MENU_D_02", "MENU_U_02", "MENU_C_03" };
                String[] mbBtnClass = { "btn _outline", "btn _outline", "btn _fill" };
                String[] mbBtnFun = { "onclick=\"page.btnAuthority.delete()\"",
                        "onclick=\"page.btnAuthority.update()\"",
                        "onclick=\"page.btnAuthority.create()\"" };
                String[] mbBtnMsg = { "btn.002", "btn.003", "btn.001" };

                String mbBtnHtml = portalComponent.selectBtnAuthorityList(mbBtnId, mbBtnClass, mbBtnFun, mbBtnMsg);

                // ==========메뉴관리 - 유료기능 버튼=========//
                String[] biBtnId = { "MENU_D_03", "MENU_C_02" };
                String[] biBtnClass = { "btn _outline", "btn _fill" };
                String[] biBtnFun = { "onclick=\"page.billing.delete()\"", "onclick=\"page.billing.create()\"" };
                String[] biBtnMsg = { "btn.002", "btn.001" };

                String biBtnHtml = portalComponent.selectBtnAuthorityList(biBtnId, biBtnClass, biBtnFun, biBtnMsg);

                // ======== 메뉴관리 - 메뉴 트리 아이콘========//
                String[] treeBtnId = { "MENU_U_04", "MENU_U_05", "MENU_D_01" };
                String[] treeBtnClass = { "icon_btn", "icon_btn", "icon_btn" };
                String[] treeBtnFun = { "onclick=\"page.menu.up()\"", "onclick=\"page.menu.down()\"",
                        "onclick=\"page.menu.delete()\"" };
                String[] treeBtnEtc = { "", "", "" };
                String[] treeBtnIcon = { "ic ic-arrow2", "ic ic-arrow2 down", "ic ic-delete" };
                String[] treeBtnToolTip = { "item.menu.021", "item.menu.022", "btn.002" };
                String[] treeBtnBlind = { "", "", "" };

                String treeIconHtml = portalComponent.selectBtnAuthorityListWithIcon(treeBtnId, treeBtnClass, treeBtnFun,
                        treeBtnEtc, treeBtnIcon, treeBtnToolTip, treeBtnBlind);

                boolean isDelAuth = mBtnHtml.contains("delete");


                model.addAttribute("mBtnHtml", mBtnHtml); // 메뉴 btn
                model.addAttribute("mbBtnHtml", mbBtnHtml); // 버튼 권한 btn
                model.addAttribute("biBtnHtml", biBtnHtml); // 유료 기능 btn
                model.addAttribute("treeIconHtml", treeIconHtml); // 메뉴 트리 icon
                model.addAttribute("isDelAuth", isDelAuth);

                return "page/system/menu/menu";

        }

        /**
         * 시스템관리 > 메뉴관리 > 하위 메뉴 추가
         */
        // @IsAdmin
        @GetMapping("/menu-popup/create")
        @Description(name = "메뉴관리 하위 메뉴 추가 화면", description = "하위 메뉴 추가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String menuPopupModal(CommonReqVo commonReqVo, Model model, HttpServletRequest request,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("메뉴 관리 하위메뉴 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                model.addAttribute("isModal", true);

                return "page/system/menu/menu_c";
        }

        /**
         * 시스템관리 > 메뉴관리 > 메뉴 수정
         */
        // @IsAdmin
        @GetMapping("/menu-popup/update")
        @Description(name = "메뉴관리 메뉴 수정 화면", description = "메뉴 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String menuPopupUpdate(CommonReqVo commonReqVo, Model model, HttpServletRequest request,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("메뉴 관리 메뉴 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                model.addAttribute("isModal", true);

                return "page/system/menu/menu_u";
        }

        // @IsAdmin
        @GetMapping("/menu-billing-popup")
        @Description(name = "메뉴관리 유료기능 추가 화면", description = "유료기능 추가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String menuBillingPopup(CommonReqVo commonReqVo, Model model, HttpServletRequest request,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("메뉴 관리 유료 기능 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                return "page/system/menu/menu_billing_c";
        }

        /**
         * 시스템관리 > 메뉴관리 > 버튼 권한 추가
         *
         */
        // @IsAdmin
        @GetMapping("/menu-btn-authority-popup/create")
        @Description(name = "메뉴관리 버튼 권한 추가 화면", description = "버튼 권한 추가가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String menuBtnAuthorityPopup(CommonReqVo commonReqVo, Model model, HttpServletRequest request,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("메뉴 관리 버튼 권한 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                return "page/system/menu/menu_btn_authority_c";

        }

        /**
         * 시스템관리 > 메뉴관리 > 버튼 권한 수정
         *
         */
        // @IsAdmin
        @GetMapping("/menu-btn-authority-popup/update")
        @Description(name = "메뉴관리 버튼 권한 수정 화면", description = "버튼 권한 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String menuBtnAuthorityPopupUpdate(CommonReqVo commonReqVo, Model model, HttpServletRequest request,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("메뉴 관리 버튼 권한 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                return "page/system/menu/menu_btn_authority_u";

        }

        /**
         * 시스템관리 > 권한그룹관리
         */
        // Role: ADMIN, USER
        @GetMapping("/authority-group")
        @Description(name = "권한그룹관리 화면", description = "권한에 따른 버튼설정 후, 권한그룹관리 화면 페이지 반환.", type = Description.TYPE.MEHTOD)
        public String groupAuthority(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("권한 그룹 관리 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                // ==========권한 그룹 버튼=========//
                // 메뉴 관리에서 등록한 버튼 아이디
                String[] grpBtnId = { "AUTHORITY_GROUP_D_01", "AUTHORITY_GROUP_U_01", "AUTHORITY_GROUP_C_01" };
                // 버튼에 사용하는 클래스
                String[] grpBtnClass = { "btn _outline", "btn _outline", "btn _fill" };
                // 버튼에 사용하는 함수
                String[] grpBtnFun = { "onclick=\"page.authorityGroup.delete()\"",
                        "onclick=\"page.authorityGroup.update()\"",
                        "onclick=\"page.authorityGroup.create()\"" };
                // 버튼에 사용하는 메시지 아이디
                String[] grpBtnMsg = { "btn.002", "btn.003", "btn.001" };

                String grpBtnHtml = portalComponent.selectBtnAuthorityList(grpBtnId, grpBtnClass, grpBtnFun, grpBtnMsg);

                // ========권한 사용자 버튼==========//
                String[] userBtnId = { "AUTHORITY_GROUP_D_02", "AUTHORITY_GROUP_C_02" };
                String[] userBtnClass = { "btn", "btn _fill" };
                String[] userBtnFun = { "onclick=\"page.authorityGroupUser.delete()\"",
                        "onclick=\"page.authorityGroupUser.create()\"" };
                String[] userBtnMsg = { "btn.002", "btn.001" };

                String userBtnHtml = portalComponent.selectBtnAuthorityList(userBtnId, userBtnClass, userBtnFun, userBtnMsg);

                model.addAttribute("grpBtnHtml", grpBtnHtml);
                model.addAttribute("userBtnHtml", userBtnHtml);
                log.debug("======================html========================");
                log.debug("btnHtml={}", grpBtnHtml);
                log.debug("btnHtml={}", userBtnHtml);
                log.debug("======================end========================");

                boolean isDelAuth = grpBtnHtml.contains("delete");
                model.addAttribute("isDelAuth", isDelAuth);

                return "page/system/authoritygroup/authority_group";

        }

        /**
         * 시스템관리 > 권한그룹관리 > 권한 그룹 추가
         */
        @GetMapping("/authority-group-popup/create")
        @Description(name = "권한그룹 추가 화면", description = "type에 따라 새창 또는 페이지로 권한 그룹 추가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String authorityGroupPopup(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "pjtNo") String pjtNo, @RequestParam(value = "cntrctNo") String cntrctNo,
                        HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("권한 그룹 관리 그룹 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                // 새창 설정
                if (type != null) {
                        if ("p".equals(type)) {
                                model.addAttribute("header", true);
                        }
                } else {
                        model.addAttribute("header", false);
                }

                return "page/system/authoritygroup/authority_group_c";


        }

        /**
         * 시스템관리 > 권한그룹관리 > 권한 그룹 수정
         */
        @GetMapping("/authority-group-popup/update")
        @Description(name = "권한그룹 수정 화면", description = "type에 따라 새창 또는 페이지로 권한 그룹 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String authorityGroupPopupUpdate(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "pjtNo") String pjtNo, @RequestParam(value = "cntrctNo") String cntrctNo,
                        HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("권한 그룹 관리 그룹 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                // 새창 설정
                if (type != null) {
                        if ("p".equals(type)) {
                                model.addAttribute("header", true);
                        }
                } else {
                        model.addAttribute("header", false);
                }


                return "page/system/authoritygroup/authority_group_u";

        }

        /**
         * 시스템관리 > 권한그룹관리 > 권한 그룹 사용자(부서) 추가
         */
        @GetMapping("/authority-group-dept-popup")
        @Description(name = "권한그룹 사용자(부서) 추가 화면", description = "권한 그룹 사용자(부서) 추가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String groupAuthorityDeptPopup(CommonReqVo commonReqVo, @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo, Model model, HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("권한 그룹 관리 그룹 사용자(부서) 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }


                return "page/system/authoritygroup/authority_group_dept_c";

        }

        /**
         * 시스템관리 > 권한그룹관리 > 권한 그룹 사용자(사용자) 추가
         */
        @GetMapping("/authority-group-user-popup")
        @Description(name = "권한그룹 사용자(사용자) 추가 화면", description = "권한 그룹 사용자(사용자) 추가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String groupAuthorityUserPopup(CommonReqVo commonReqVo, @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo, Model model, HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("권한 그룹 관리 그룹 사용자(사용자) 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                return "page/system/authoritygroup/authority_group_user_c";


        }

        /**
         * 시스템관리 > 메뉴권한관리
         */
        // @IsAdmin
        @GetMapping("/menu-authority")
        @Description(name = "메뉴권한관리 화면", description = "권한에 따른 버튼설정 후, 메뉴권한관리 화면 페이지 반환.", type = Description.TYPE.MEHTOD)
        public String menuAuthority(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("메뉴 권한 관리 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                // ==========메뉴 권한 그룹 버튼=========//
                // 메뉴 관리에서 등록한 버튼 아이디
                String[] grpBtnId = { "MENU_AUTHORITY_D_02", "MENU_AUTHORITY_U_01", "MENU_AUTHORITY_C_01" };
                // 버튼에 사용하는 클래스
                String[] grpBtnClass = { "btn _outline", "btn _outline", "btn _fill" };
                // 버튼에 사용하는 함수
                String[] grpBtnFun = { "onclick=\"page.authorityGroup.delete()\"",
                        "onclick=\"page.authorityGroup.update()\"",
                        "onclick=\"page.authorityGroup.create()\"" };
                // 버튼에 사용하는 메시지 아이디
                String[] grpBtnMsg = { "btn.002", "btn.003", "btn.001" };

                String grpBtnHtml = portalComponent.selectBtnAuthorityList(grpBtnId, grpBtnClass, grpBtnFun, grpBtnMsg);

                model.addAttribute("grpBtnHtml", grpBtnHtml);
                log.debug("======================html========================");
                log.debug("btnHtml={}", grpBtnHtml);
                log.debug("======================end========================");

                boolean isDelAuth = grpBtnHtml.contains("delete");
                model.addAttribute("isDelAuth", isDelAuth);


                return "page/system/menuauthority/menu_authority";


        }

        // @IsAdmin
        @GetMapping("/menu-authority-popup")
        @Description(name = "메뉴권한관리 메뉴 권한 추가 화면", description = "type에 따라 새창 또는 페이지로 메뉴 권한 추가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String menuAuthorityPopup(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "pjtNo") String pjtNo, @RequestParam(value = "cntrctNo") String cntrctNo,
                        HttpServletRequest request) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("메뉴 권한 관리 권한 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }

                if (type != null) {
                        if ("p".equals(type)) {
                                model.addAttribute("header", true);
                        }
                } else {
                        model.addAttribute("header", false);
                }

                return "page/system/menuauthority/menu_authority_c";

        }

        @GetMapping("/menu-authority-popup/update")
        @Description(name = "메뉴권한관리 메뉴 권한 수정 화면", description = "메뉴 권한 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String menuAuthorityPopup(CommonReqVo commonReqVo, Model model, HttpServletRequest request,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("메뉴 권한 관리 권한 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
                        return "redirect:/";
                }


                return "page/system/menuauthority/menu_authority_u";


        }

        /**
         * 시스템관리 > 기초데이터관리
         */
        // @IsAdmin
        @GetMapping("/common-code")
        @Description(name = "기초데이터 관리 화면", description = "기초데이터코드 화면 반환.", type = Description.TYPE.MEHTOD)
        public String commonCode(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("기초데이터 관리 화면 접속");
                systemLogComponent.addUserLog(userLog);

                // 그룹코드용 버튼
                String[] grpBtnId = { "COMCODE_D_01", "COMCODE_U_02", "COMCODE_C_03" };
                String[] grpBtnClass = { "btn _outline", "btn _outline", "btn _fill" };
                String[] grpBtnFun = { "onclick=\"page.groupCode.delete()\"", "onclick=\"page.groupCode.update()\"",
                        "onclick=\"page.groupCode.create()\"" };
                String[] grpBtnMsg = { "btn.002", "btn.003", "btn.001" };
                String[] grpBtnEtc = { "", "", "id='subMenuAddButton'" };

                String grpBtnHtml = portalComponent.selectBtnAuthorityList(grpBtnId, grpBtnClass, grpBtnFun, grpBtnMsg, grpBtnEtc);
                model.addAttribute("grpBtnHtml", grpBtnHtml);

                // 코드용 버튼
                String[] cdBtnId = { "COMCODE_D_02", "COMCODE_U_01", "COMCODE_C_02" };
                String[] cdBtnClass = { "btn _outline", "btn _outline", "btn _fill" };
                String[] cdBtnFun = { "onclick=\"page.code.delete()\"", "onclick=\"page.code.update()\"",
                        "onclick=\"page.code.create()\"" };
                String[] cdBtnMsg = { "btn.002", "btn.003", "btn.001" };

                String cdBtnHtml = portalComponent.selectBtnAuthorityList(cdBtnId, cdBtnClass, cdBtnFun, cdBtnMsg);
                model.addAttribute("cdBtnHtml", cdBtnHtml);


                // 트리 버튼
                String[] treeBtnId = { "COMCODE_U_03", "COMCODE_U_04", "COMCODE_D_01" };
                String[] treeBtnClass = { "icon_btn", "icon_btn", "icon_btn" };
                String[] treeBtnFun = { "onclick=\"page.groupCode.up()\"", "onclick=\"page.groupCode.down()\"",
                        "onclick=\"page.groupCode.delete()\"" };
                String[] treeBtnEtc = { "", "", "" };
                String[] treeBtnIcon = { "ic ic-arrow2", "ic ic-arrow2 down", "ic ic-delete" };
                String[] treeBtnTooltip = { "item.com.034", "item.com.035", "btn.002" };
                String[] treeBtnBlind = { "", "", "" };

                String treeBtnHtml = portalComponent.selectBtnAuthorityListWithIcon(treeBtnId, treeBtnClass, treeBtnFun,
                        treeBtnEtc, treeBtnIcon, treeBtnTooltip, treeBtnBlind);

                model.addAttribute("treeBtnHtml", treeBtnHtml);

                boolean isDelAuth = grpBtnHtml.contains("delete");
                model.addAttribute("isDelAuth", isDelAuth); // 삭제 권한이 있으면 Y 없으면 N


                return "page/system/commoncode/common_code";

        }

        /**
         * 시스템관리 > 기초데이터관리 > 그룹코드(등록/수정)
         */
        @GetMapping("/common-code/GroupForm")
        @Description(name = "기초데이터관리 코드그룹 등록,수정 화면", description = "기초데이터관리의 코드그룹 등록,수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String commonCodeGroupForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam("mode") String mode,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("기초데이터관리 코드그룹 등록,수정 화면 접속");
                systemLogComponent.addUserLog(userLog);


                return "page/system/commoncode/common_code_group_cu";

        }

        /**
         * 시스템관리 > 기초데이터관리 > 코드(등록)
         */
        @GetMapping("/common-code/createCode")
        @Description(name = "기초데이터관리 코드 등록 화면", description = "기초데이터관리의 코드 등록 화면 반환.", type = Description.TYPE.MEHTOD)
        public String commonCodeCreateForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("기초데이터관리 코드 등록 화면 접속");
                systemLogComponent.addUserLog(userLog);


                return "page/system/commoncode/common_code_c";


        }

        /**
         * 시스템관리 > 기초데이터관리 > 코드(수정)
         */
        @GetMapping("/common-code/updateCode")
        @Description(name = "기초데이터관리 코드 수정 화면", description = "기초데이터관리의 코드 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String commonCodeUpdateForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam("type") String type,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("기초데이터관리 코드 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);


                return "page/system/commoncode/common_code_u";

        }

        /**
         * 시스템관리 > 기초데이터관리 > 코드(조회)
         */
        @GetMapping("/common-code/readCode")
        @Description(name = "기초데이터관리 코드 조회 화면", description = "기초데이터관리의 코드 조회 화면 반환.", type = Description.TYPE.MEHTOD)
        public String commonCodeReadForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("기초데이터관리 코드 상세조회 화면 접속");
                systemLogComponent.addUserLog(userLog);

                // 코드용 버튼
                String[] cdBtnId = { "COMCODE_U_05" };
                String[] cdBtnClass = { "btn _outline" };
                String[] cdBtnFun = { "onclick=\"popup.enableInputs()\"" };
                String[] cdBtnMsg = { "btn.003" };
                String[] cdBtnEtc = { "id=\"action-button\"" };

                String cdBtnHtml = portalComponent.selectBtnAuthorityList(cdBtnId, cdBtnClass, cdBtnFun, cdBtnMsg, cdBtnEtc);
                model.addAttribute("cdBtnHtml", cdBtnHtml);


                return "page/system/commoncode/common_code_r";

        }

        /**
         * 시스템관리 > 유료기능관리
         */
        // @IsAdmin
        @GetMapping("/project-billing")
        @Description(name = "TODO", description = "", type = Description.TYPE.MEHTOD)
        public String paidFeature(CommonReqVo commonReqVo) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("권한그룹");
                systemLogComponent.addUserLog(userLog);

                return "page/system/projectbilling/project_billing";
        }

        /**
         * 시스템관리 > 게시판관리
         */
        // @IsAdmin
        @GetMapping("/board/noticeboard")
        @Description(name = "게시판관리 공지사항 화면", description = "게시판관리의 게시글 목록 화면 반환.", type = Description.TYPE.MEHTOD)
        public String board(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("게시판관리 공지사항 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                String[] btnId = { "BOARD_D_02", "BOARD_U_02", "BOARD_C_02" };
                String[] btnClass = { "btn _outline", "btn _outline", "btn _fill" };
                String[] btnFun = { "onclick=\"boardGrid.delete()\"", "onclick=\"boardGrid.update()\"",
                        "onclick=\"boardGrid.create()\"" };
                String[] btnMsg = { "btn.002", "btn.003", "btn.001" };

                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);
                model.addAttribute("btnHtml", btnHtml);

                boolean isDelAuth = btnHtml.contains("delete");
                model.addAttribute("isDelAuth", isDelAuth);


                return "page/system/board/noticeboard";

        }

        /**
         * 시스템관리 > 게시판관리
         */
        // @IsAdmin
        @GetMapping("/board/faqboard")
        @Description(name = "게시판관리 FAQ 화면", description = "게시판관리의 게시글 목록 화면 반환.", type = Description.TYPE.MEHTOD)
        public String faqBoard(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("게시판관리 FAQ 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                String[] btnId = { "BOARD_D_02", "BOARD_U_02", "BOARD_C_02" };
                String[] btnClass = { "btn _outline", "btn _outline", "btn _fill" };
                String[] btnFun = { "onclick=\"boardGrid.delete()\"", "onclick=\"boardGrid.update()\"",
                        "onclick=\"boardGrid.create()\"" };
                String[] btnMsg = { "btn.002", "btn.003", "btn.001" };

                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);
                model.addAttribute("btnHtml", btnHtml);

                boolean isDelAuth = btnHtml.contains("delete");
                model.addAttribute("isDelAuth", isDelAuth);


                return "page/system/board/faqboard";

        }

        @GetMapping("/board/create")
        @Description(name = "게시판관리 추가 화면", description = "게시판관리의 게시글 추가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String boardCreate(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("게시판관리 추가 화면 접속");
                systemLogComponent.addUserLog(userLog);


                return "page/system/board/board_c";
        }

        @GetMapping("/board/update")
        @Description(name = "게시판관리 수정 화면", description = "게시판관리의 게시글 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String boardUpdate(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("게시판관리 수정 화면 접속");
                systemLogComponent.addUserLog(userLog);


                return "page/system/board/board_u";
        }

        @GetMapping("/board/read")
        @Description(name = "게시판관리 조회 화면", description = "게시판관리의 게시글 조회 화면 반환.", type = Description.TYPE.MEHTOD)
        public String boardRead(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("게시판관리 상세조회 화면 접속");
                systemLogComponent.addUserLog(userLog);

//                String[] btnId = { "BOARD_U_02" };
//                String[] btnClass = { "btn _outline" };
//                String[] btnFun = { "onclick=\"page.save()\"" };
//                String[] btnMsg = { "btn.003" };
//                String[] btnEtc = { "id=\"action-button\"" };
//
//                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
//
//                model.addAttribute("btnHtml", btnHtml);

                return "page/system/board/board_r";
        }

        /**
         * 시스템관리 > 친환경 자재 설정
         */
        @GetMapping("/ecomaterial")
        @Description(name = "친환경 자재 설정 화면", description = "친환경 자재 설정메뉴의 친환경 자재 목록 화면 반환.", type = Description.TYPE.MEHTOD)
        public String ecoMaterial(CommonReqVo commonReqVo, Model model,
                                  @RequestParam(value = "pjtNo", required = false) String pjtNo,
                                  @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("친환경 자재 설정 화면 접속");

                systemLogComponent.addUserLog(userLog);

//                String[] btnId = { "ECOMATERIAL_D_01", "ECOMATERIAL_U_01", "ECOMATERIAL_C_01" };
//                String[] btnClass = { "btn _outline", "btn _outline", "btn _fill" };
//                String[] btnFun = { "onclick=\"eco.delete()\"", "onclick=\"eco.update()\"", "onclick=\"eco.create()\"" };
//                String[] btnMsg = { "btn.002", "btn.003", "btn.001" };
//
//                String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);
//
//                model.addAttribute("btnHtml", btnHtml);
//
//                boolean isDelAuth = btnHtml.contains("delete");
//                model.addAttribute("isDelAuth", isDelAuth);

                return "page/system/ecomaterial/ecomaterial";
        }

        @GetMapping("/ecomaterial/create")
        @Description(name = "친환경 자재 설정 추가 화면", description = "친환경 자재 설정메뉴의 친환경 자재 추가 화면 반환.", type = Description.TYPE.MEHTOD)
        public String ecoMaterialCreate(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("친환경 자재 추가 화면 접속");

                if ("d".equals(type)) {
                        model.addAttribute("header", true);
                } else if ("p".equals(type)) {
                        model.addAttribute("header", false);
                }

                systemLogComponent.addUserLog(userLog);


                return "page/system/ecomaterial/ecomaterial_c";
        }

        @GetMapping("/ecomaterial/update")
        @Description(name = "친환경 자재 설정 수정 화면", description = "친환경 자재 설정메뉴의 친환경 자재 수정 화면 반환.", type = Description.TYPE.MEHTOD)
        public String ecoMaterialUpdate(CommonReqVo commonReqVo, Model model,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("친환경 자재 수정 화면 접속");

                if ("d".equals(type)) {
                        model.addAttribute("header", true);
                } else if ("p".equals(type)) {
                        model.addAttribute("header", false);
                }

                systemLogComponent.addUserLog(userLog);


                return "page/system/ecomaterial/ecomaterial_u";
        }

        /**
         * 사용자 로그 관리
         */
        @GetMapping("/user-log")
        @Description(name = "사용자 로그 관리 화면", description = "권한에 따른 버튼설정 후, 사용자 로그 관리 화면 페이지 반환.", type = Description.TYPE.MEHTOD)
        public String userLog(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("사용자 로그 관리 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.isEmpty() || cntrctNo == null || cntrctNo.isEmpty()) {
                        return "redirect:/";
                }

                return "page/system/userlog/user_log";

        }

        /**
         * 사용자 로그 관리 상세 페이지
         */
        @GetMapping("/user-log/read")
        @Description(name = "사용자 로그 관리 상세 화면", description = "사용자 로그 관리 상세 반환.", type = Description.TYPE.MEHTOD)
        public String userLogRead(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                        @RequestParam(value = "type", required = false) String type) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("사용자 로그 관리 상세조회 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.isEmpty() || cntrctNo == null || cntrctNo.isEmpty()) {
                        return "redirect:/";
                }
                if (type != null && "p".equals(type)) {
                        model.addAttribute("header", true);
                }
                return "page/system/userlog/user_log_r";
        }

        /**
         * API 로그 관리
         */
        @GetMapping("/api-log")
        @Description(name = "API 로그 관리 화면", description = "권한에 따른 버튼설정 후, 사용자 로그 관리 화면 페이지 반환.", type = Description.TYPE.MEHTOD)
        public String apiLog(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo") String pjtNo,
                        @RequestParam(value = "cntrctNo") String cntrctNo) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("API 로그 관리 목록 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.isEmpty() || cntrctNo == null || cntrctNo.isEmpty()) {
                        return "redirect:/";
                }

                return "page/system/apilog/api_log";

        }

        /**
         * API 로그 관리 상세 페이지
         */
        @GetMapping("/api-log/read")
        @Description(name = "API 로그 관리 상세 화면", description = "API 로그 관리 상세 반환.", type = Description.TYPE.MEHTOD)
        public String apiLogRead(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                        @RequestParam(value = "pjtNo", required = false) String pjtNo,
                        @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                        @RequestParam(value = "type", required = false) String type) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.VIEW.name());
                userLog.setExecType("API 로그 관리 상세 조회 화면 접속");
                systemLogComponent.addUserLog(userLog);

                if (pjtNo == null || pjtNo.isEmpty() || cntrctNo == null || cntrctNo.isEmpty()) {
                        return "redirect:/";
                }
                if (type != null && "p".equals(type)) {
                        model.addAttribute("header", true);
                }
                return "page/system/apilog/api_log_r";
        }

}
