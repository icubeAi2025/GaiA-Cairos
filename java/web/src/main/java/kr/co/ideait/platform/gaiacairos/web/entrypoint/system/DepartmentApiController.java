package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.system.DepartmentComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DepartmentService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmDepartment;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmOrganization;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentMybatisParam.GetAuthUsersDepartmentInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentMybatisParam.GetEmployeeListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentMybatisParam.GetOranizationEmployeeListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.config.property.ApplicationProp;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@IsUser
@RestController
@RequestMapping("/api/system/department")
public class DepartmentApiController extends AbstractController {

    @Autowired
    DepartmentComponent component;

    @Value("${spring.application.name}")
    String pjtType;
    
    /**
     * 부서 상세 조회 (해당 부서원만 조회, 하위부서원 제외)
     */
    @GetMapping("/{deptNo}")
    @Description(name = "부서 상세 데이터 조회", description = "해당하는 부서의 상세 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getDepartmentDetails(CommonReqVo commonReqVo, @PathVariable("deptNo") Integer deptNo, @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서 상세 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        HashMap<String,Object> result = component.getDepartmentDetailData(deptNo, langInfo);


        return Result.ok()
                .put("department",result.get("department"))
                .put("emploeeList", result.get("emploeeList"));
    }

    /**
     * 조직 구성원 리스트 검색 조회
     */
    @GetMapping("/emploee/{deptNo}/list")
    @Description(name = "부서의 소속 직원 리스트 조회", description = "부서에 속한 직원 데이터 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getDepartmentEmploeeListByDeptNo(CommonReqVo commonReqVo, @PathVariable("deptNo") Integer deptNo, @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo, DepartmentForm.DepartmentGet pjtData) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서의 소속 직원 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("employeeList",component.getDepartmentEmploeeListByDeptNo(deptNo, langInfo, pjtData));
    }

    /**
     * 부서코드 중복 체크
     */
    // @RequiredProjectSelect(superChangeable = true)
    @GetMapping("/exist/{deptCode}")
    @Description(name = "부서코드 중복 체크", description = "부서 추가 시, 부서코드 중복 체크(true/false)", type = Description.TYPE.MEHTOD)
    public Result existDepartment(CommonReqVo commonReqVo, @PathVariable("deptCode") String deptCode, UserAuth user) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서코드 중복 체크");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("exist", component.isExistDepartment(deptCode));
    }

    /**
     * 부서정보 저장
     */
    // @RequiredProjectSelect(superChangeable = true)
    @PostMapping("/create")
    @Description(name = "부서 등록", description = "부서 등록", type = Description.TYPE.MEHTOD)
    public Result createDepartment(CommonReqVo commonReqVo, @Valid @RequestBody DepartmentForm.DepartmentCreate department, UserAuth user) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서 등록");
        systemLogComponent.addUserLog(userLog);

        String result = component.createDepartment(department,commonReqVo.getPjtDiv(),commonReqVo.getApiYn());
        if("success".equals(result)) {
            return Result.ok();
        }
        return Result.nok(ErrorType.ETC,"Logical Issue");
    }

    /**
     * 부서정보 수정
     */
    //@RequiredProjectSelect(superChangeable = true)
    @PostMapping("/update")
    @Description(name = "부서 데이터 수정", description = "해당하는 부서 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateDepartment(CommonReqVo commonReqVo, @Valid @RequestBody DepartmentForm.DepartmentUpdate department) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서 데이터 수정");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("department", component.modifyDepartment(department,commonReqVo.getPjtDiv(), commonReqVo.getApiYn()));
    }

    /**
     * 부서정보 삭제
     */
    // @RequiredProjectSelect(superChangeable = true)
    @PostMapping("/delete")
    @Description(name = "부서 데이터 삭제", description = "부서 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteDepartment(CommonReqVo commonReqVo, @Valid @RequestBody DepartmentForm.DepartmentDelete department) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서 데이터 삭제");
        systemLogComponent.addUserLog(userLog);

        if(component.removeDepartment(department,commonReqVo.getPjtDiv(), commonReqVo.getApiYn())){
            return Result.ok();
        }
        return Result.nok(ErrorType.ETC,"Logical Issue");
    }

    /**
     * 소속 직원 추가 시, 해당 부서에 속하지 않은 구성원 조회
     * @param langInfo
     * @param pjtData
     * @return
     */
    @GetMapping("/user/get")
    @Description(name = "사용자 데이터 리스트 조회", description = "해당 부서에 속하지 않은 사용자 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getDepartmentUser(CommonReqVo commonReqVo, HttpServletRequest request,
                                    @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo,
                                    DepartmentForm.DepartmentGet pjtData) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("사용자 데이터 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        String [] userParam = commonReqVo.getUserParam();
        HashMap<String,Object> result = component.getDepartmentUserListData(langInfo,pjtData,userParam[1]);

        return Result.ok().put("employeeList", result.get("employeeList"))
                            .put("optionDataMap", result.get("optionDataMap"));
    }

    /**
     * 부서 구성원(조직) 추가
     */
    @PostMapping("/user/create")
    @Description(name = "부서 소속 직원 등록", description = "해당 부서의 소속 직원 등록", type = Description.TYPE.MEHTOD)
    public Result createDepartmentUser(CommonReqVo commonReqVo, 
            @Valid @RequestBody DepartmentForm.DepartmentUserCreateList departmentUserCreateList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서 소속 직원 등록");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("departmentUserCreateList",
                component.registUserOfDepartment(departmentUserCreateList, commonReqVo));
    }

    /**
     * 부서 구성원 수정
     */
    @PostMapping("/user/update")
    @Description(name = "부서 소속 직원 데이터 수정", description = "해당 부서의 소속 직원 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateDepartmentUser(CommonReqVo commonReqVo, 
            @Valid @RequestBody DepartmentForm.DepartmentUserUpdate departmentUserUpdate) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서 소속 직원 데이터 수정");
        systemLogComponent.addUserLog(userLog);

        String result = component.modifyUserOfDepartment(departmentUserUpdate,commonReqVo);
        if("success".equals(result)){
            return Result.ok();
        }
        return Result.nok(ErrorType.ETC,"Logical Issue");
    }

    /**
     * 부서 구성원 삭제
     */
    @PostMapping("/user/delete")
    @Description(name = "부서 소속 직원 데이터 삭제", description = "해당 부서의 소속 직원 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteDepartmentUser(CommonReqVo commonReqVo, 
            @Valid @RequestBody DepartmentForm.DepartmentUserDeleteList departmentDeleteList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서 소속 직원 데이터 삭제");
        systemLogComponent.addUserLog(userLog);

        if(component.removeUserOfDepartment(departmentDeleteList,commonReqVo)){
            return Result.ok();
        }
        return Result.nok(ErrorType.ETC,"Logical Issue");
    }
    
    /**
     * 관리자용 부서관리 첫번째 트리 가져오기 (Admin 용)
     */
    // @IsAdmin
    @GetMapping("/admin-tree")
    @Description(name = "부서관리 > 프로젝트, 계약 트리 조회", description = "관리자의 경우, 모든 프로젝트, 계약 등의 계층 데이터 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getAdminDepartmentTree(CommonReqVo commonReqVo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로젝트, 계약 트리 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("treeList", component.getAdminFirstTreeData());
    }

    /**
     * GAIA용 부서관리 첫번째 트리 가져오기 (GAIA용)
     */
    //@IsAdmin
    @GetMapping("/gaia-tree")
    @Description(name = "부서관리 > 프로젝트 트리 조회", description = "GAIA 사용자의 경우, 프로젝트의 계층 데이터 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getGaiaDepartmentTree(CommonReqVo commonReqVo, HttpServletRequest request, DepartmentForm.DepartmentGet pjtData) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로젝트 트리 조회");
        systemLogComponent.addUserLog(userLog);

        String [] param = commonReqVo.getUserParam();

        return Result.ok().put("treeList", component.getGaiaTreeData(param[0],pjtData.getPjtNo(), pjtData.getCntrctNo()));
    }

    /**
     * CAIROS용 부서관리 첫번째 트리 가져오기 (CAIROS용)
     */
    //@IsAdmin
    @GetMapping("/cmis-tree")
    @Description(name = "부서관리 > 계약 트리 조회", description = "CAIROS 사용자의 경우, 계약의 계층 데이터 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getCmisDepartmentTree(CommonReqVo commonReqVo, HttpServletRequest request, DepartmentForm.DepartmentGet pjtData) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("계약 트리 조회");
        systemLogComponent.addUserLog(userLog);

        String [] param = commonReqVo.getUserParam();
        return Result.ok().put("treeList", component.getCairosTreeData(param[0], pjtData.getPjtNo(), pjtData.getCntrctNo()));
    }
    
    /**
     * 부서관리 두번째 트리 가져오기
     * Admin => deptId
     * Gaia 사용자 => deptId = 'G'+ pjt_no
     * CMIS 사용자 => deptId = 'C'+ cntrct_no
     */
    // @IsUser
    //@RequiredProjectSelect(superChangeable = true)
    @PostMapping("/admin-secondtree")
    @Description(name = "부서관리 > 부서 트리 데이터 조회", description = "부서관리 > 해당 프로젝트, 계약 등의 속한 부서 데이터 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getAdminSecondDepartmentTree(CommonReqVo commonReqVo, HttpServletRequest request, @Valid @RequestBody DepartmentForm.DepartmentGet pjtData) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서 트리 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        String [] userParam = commonReqVo.getUserParam();

        return Result.ok().put("departmentList", component.getAdminSecondTreeData(pjtData,userParam[1],pjtType));
    }

    
    /**
     * 조직 구성원 상세 조회
     */
    @GetMapping("/employee/details")
    @Description(name = "부서 소속 직원 상세 데이터 조회", description = "부서 소속 직원 상세 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getEmployeeDetails(CommonReqVo commonReqVo, @RequestParam("employeeId") String employeeId, HttpServletRequest request,
                                    @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo,
                                    DepartmentForm.DepartmentGet pjtData) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("부서 소속 직원 상세 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        String [] userParam = commonReqVo.getUserParam();

        return Result.ok().put("employeeDetailList", component.getEmployeeOfDepartmentData(employeeId,userParam[1],pjtType,langInfo,pjtData.getPjtNo(),pjtData.getCntrctNo()));
    }
    
    /**
     * 권한그룹 관리 > 권한그룹 사용자 추가 - 부서 트리 가져오기
     * 
     * GAIA => deptId = 'G'+ pjt_no
     * CAIROS => deptId = 'C'+ cntrct_no
     */
    // @IsUser
    //@RequiredProjectSelect(superChangeable = true)
    @GetMapping("/authority-users/dept-list")
    @Description(name = "권한그룹관리 > 그룹 사용자 추가 - 부서 트리 데이터 조회", description = "권한 그룹 관리 > 그룹 사용자 추가 시, 해당 프로젝트, 계약의 추가할 부서 데이터 리스트를 조회회", type = Description.TYPE.MEHTOD)
    public Result getDepartmentTree(CommonReqVo commonReqVo, DepartmentForm.DepartmentGet pjtData) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("그룹 사용자 추가 - 부서 트리 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("departmentList", component.getDepartmentListData(pjtData));
    }

    /**
     * 권한그룹 관리 > 그룹사용자(사용자) 추가 - 조직리스트 가져오기
     * 
     */
    // @IsUser
    //@RequiredProjectSelect(superChangeable = true)
    @GetMapping("/authority-users/org-list")
    @Description(name = "권한그룹관리 > 그룹 사용자 추가 - 소속 직원 리스트 조회", description = "권한 그룹 관리 > 그룹 사용자 추가 시, 해당 부서에 속한 직원 리스트를 조회", type = Description.TYPE.MEHTOD)
    public Result getOrganization(CommonReqVo commonReqVo, @RequestParam("deptId") String deptId) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("그룹 사용자 추가 - 소속 직원 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("organizationList", 
                                component.getAuthGrpUsersOrganizationList(deptId));
    }

    /**
     * 권한그룹 관리 > 그룹사용자(사용자) 추가 - 하위부서 조직리스트 가져오기
     * 
     */
    // @IsUser
    //@RequiredProjectSelect(superChangeable = true)
    @GetMapping("/authority-users/org-list/down")
    @Description(name = "권한그룹관리 > 그룹 사용자 추가 - 하위부서 소속 직원 리스트 조회", description = "권한 그룹 관리 > 그룹 사용자 추가 시, 해당 부서에 속한 하위부서의 직원 리스트를 조회", type = Description.TYPE.MEHTOD)
    public Result getDownOrganization(CommonReqVo commonReqVo, @RequestParam("deptId") String deptId) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("그룹 사용자 추가 - 하위부서 소속 직원 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("organizationList",
                                component.getAuthGrpUsersDownOrganizationList(deptId));
    }
    
}
