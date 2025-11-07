package kr.co.ideait.platform.gaiacairos.web.entrypoint.project;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.project.ProjectComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CompanyService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DepartmentService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProjectInstall;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ProjectService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam.CompanyListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentMybatisParam.GetEmployeeListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.ProjectForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@IsUser
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/project")
public class ProjectApiController extends AbstractController {

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectComponent projectComponent;

    // 회사검색용
    @Autowired
    CompanyService systemCompanyService;

    @Autowired
    CompanyForm companyForm;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    CommonCodeDto commonCodeDto;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    DepartmentForm departmentForm;

    /**
     * TODO: 프로젝트를 선택하면 해당정보를 쿠키에 저장합니다.
     * 해당 정보는 로그아웃 시 초기화
     * 로그인 시 선택이 필요함.
     */
    @GetMapping("/select/{pjtNo}/{cntrctNo}")
    @Description(name = "프로젝트 정보(임시)", description = "프로젝트 정보(임시)", type = Description.TYPE.MEHTOD)
    public Result selectContract(CommonReqVo commonReqVo, @PathVariable("pjtNo") String pjtNo,
            @PathVariable("cntrctNo") String cntrctNo,
            HttpServletResponse response, UserAuth user) {
        // String loginId = user.getLoginId();
        // loginId 로 계약 리스트 조회 (반드시 권한이 있는 프로젝트, 계약만 설정가능하게 처리함)
        // 계약 리스트에 해당하는 계약이 있는지 확인
        // 계약이 있으면 쿠키에 저장 // 계약이 없으면 에러
        // TODO: 나중에 userId + pjtNo + cntrctNo 해시키 생성 검토
        if (pjtNo != null && cntrctNo != null) {
            cookieService.setHttpOnlyCookie(response, cookieVO.getSelectCookieName(), pjtNo + ":" + cntrctNo,
                    60 * 60 * 24);
            return Result.ok();
        }
        throw new GaiaBizException(ErrorType.BAD_REQUEST);
    }

    // ------------------------------------------------------------------------------------------------------------------

    /**
     * 현장개설 목록 조회
     * 
     * @return projectList
     */
    @GetMapping("/get/pjt-list")
    @Description(name = "현장개설요청 목록 조회", description = "현장개설요청 전체 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getProjectList(CommonReqVo commonReqVo) {
        return Result.ok().put("projectList",projectComponent.getProjectInstallList());
    }

    /**
     * 현장개설 조회
     * 
     * @return 조회한 project
     */
    @GetMapping("/get/{pjtNo}")
    @Description(name = "현장개설요청 조회", description = "현장개설요청 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getProject(CommonReqVo commonReqVo, @PathVariable("pjtNo") String pjtNo) {

        Map<String,Object> result = projectComponent.getProjectInstall(pjtNo);

        return Result.ok().put("projectInstall", result.get("projectInstall"))
                .put("attachments", result.get("attachments"));

    }

    /**
     * 현장개설 -> 신규신청
     */
    @PostMapping("/create/pjt")
    @Description(name = "현장개설요청 추가", description = "현장개설요청 데이터 추가", type = Description.TYPE.MEHTOD)
    public Result createProject(CommonReqVo commonReqVo,
            @RequestPart(value = "projectData") @Valid ProjectForm.ProjectInstall project,
            @RequestPart(value = "files", required = false) List<MultipartFile> files)
            throws IllegalStateException, IOException {

        projectComponent.createProject(project,files,commonReqVo.getApiYn());

        return Result.ok();
    }

    /**
     * 현장개설 -> 삭제
     */
    @PostMapping("/delete/{plcReqNo}")
    @Description(name = "현장개설요청 삭제", description = "현장개설요청 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteProject(CommonReqVo commonReqVo, @PathVariable("plcReqNo") String plcReqNo) {

        projectComponent.deleteProject(plcReqNo,commonReqVo.getApiYn());

        return Result.ok();
    }

    /**
     * 현장개설 -> 수정
     * 
     * @throws IOException
     * @throws IllegalStateException
     */
    @PostMapping("/update/{plcReqNo}")
    @Description(name = "현장개설요청 수정", description = "현장개설요청 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateProject(CommonReqVo commonReqVo, @PathVariable(value = "plcReqNo") String plcReqNo,
            @RequestPart(value = "projectData") @Valid ProjectForm.ProjectInstall project,
            @RequestPart(value = "files", required = false) List<MultipartFile> newFiles,
            @RequestParam(value = "removedFiles[]", required = false) List<Integer> removedFileNos,
            @RequestParam(value = "removedSnos[]", required = false) List<Integer> removedSnos)
            throws IOException {

        CnProjectInstall cnProjectInstall = projectComponent.updateProject(plcReqNo,project,newFiles,removedFileNos,removedSnos,commonReqVo.getApiYn());

        return Result.ok().put("project",cnProjectInstall);
    }

    // 사업관리에서 쓸 검색 모달창 API
    // ----------------------------------------------------------------------------------------------------------------

    // 회사 검색 : 목록
    @GetMapping("/corpNmSearch")
    @Description(name = "회사검색", description = "회사 데이터 전체 조회", type = Description.TYPE.MEHTOD)
    public GridResult corpNmSearch(CommonReqVo commonReqVo, @Valid CompanyForm.CompanyListGet companyListGet) {
        CompanyListInput input = companyForm.toCompanyListInput(companyListGet);

        return GridResult.ok(systemCompanyService.getCompanyList(input));
    }

    /**
     * 회사 검색 : 회사 그룹 리스트
     */
    @GetMapping("/companyGroupList")
    @Description(name = "회사 그룹 목록", description = "회사 그룹 데이터 전체 조회", type = Description.TYPE.MEHTOD)
    public Result getCompanyGroup(CommonReqVo commonReqVo,
            @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        String cmnGrpCd = "f306072f-b2aa-4aa6-bf40-192000832cbc";
        return Result.ok()
                .put("companyGroupList", commonCodeService.getCommonCodeListByGroupCode(cmnGrpCd).stream()
                        .map(smComCode -> {
                            CommonCodeDto.CommonCodeCombo codeCombo = commonCodeDto.fromSmComCodeToCombo(smComCode);
                            codeCombo.setCmnCdNm(
                                    langInfo.equals("en") ? smComCode.getCmnCdNmEng() : smComCode.getCmnCdNmKrn());
                            return codeCombo;
                        }));
    }

    // 사용자검색 : 목록
    @GetMapping("/ofclNmSearch")
    @Description(name = "사용자 목록", description = "사용자 데이터 전체 조회", type = Description.TYPE.MEHTOD)
    public GridResult getUserList(CommonReqVo commonReqVo, @Valid DepartmentForm.EmployeeListInput pjtData,
            @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        GetEmployeeListInput input = departmentForm.toEmployeeList(pjtData);

        return GridResult.ok(departmentService.getUserSearchEmploeeListByDeptNo(input, langInfo));

    }

    /**
     * 사용자검색 : 부서 리스트
     */
    @GetMapping("/admin-secondtree")
    @Description(name = "부서 목록", description = "부서 데이터 전체 조회", type = Description.TYPE.MEHTOD)
    public Result getAdminSecondDepartmentTree(CommonReqVo commonReqVo, HttpServletRequest request,
            DepartmentForm.DepartmentGet pjtData) {

        String deptId = null;
        String[] userParam = commonReqVo.getUserParam();

        if (userParam[2].equals("PGAIA")) {
            if (pjtData.getCntrctYn().equals("Y")) {
                deptId = "C" + pjtData.getCntrctNo();
            } else {
                deptId = "G" + pjtData.getPjtNo();
            }
        } else {
            deptId = "C" + pjtData.getCntrctNo();
        }

        return Result.ok().put("departmentList", departmentService.getAdminSecondDepartmentList(deptId));
    }

    /**
     * 사용요청: 유저가 속한 프로젝트 제외 프로젝트 목록 가져오기
     */
    @GetMapping("/useRequest/project")
    @Description(name = "프로젝트 목록 가져오기", description = "유저가 속한 프로젝트 제외 프로젝트 목록 가져오기", type = Description.TYPE.MEHTOD)
    public Result getProjectList(CommonReqVo commonReqVo, HttpServletRequest request) {
        return Result.ok().put("projectList", projectService.getGAIAProjectList(commonReqVo.getUserId()));
    }

    /**
     * 사용요청: 유저가 속한 계약 제외 계약 목록 가져오기
     */
    @GetMapping("/useRequest/contract")
    @Description(name = "계약 목록 가져오기", description = "유저가 속한 계약 제외 계약 목록 가져오기", type = Description.TYPE.MEHTOD)
    public Result getContractList(CommonReqVo commonReqVo, HttpServletRequest request) {
        return Result.ok().put("contractList", projectService.getCAIROSContractList(commonReqVo.getUserId()));
    }
}
