package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.system.CompanyComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmCompany;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam.CompanyListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam.UserCompanyListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/system")
public class CompanyApiController extends AbstractController {

    @Autowired
    CompanyComponent companyComponent;

    @Autowired
    CompanyForm companyForm;

    @Autowired
    CompanyDto companyDto;

    /**
     * 이데아 플랫폼/PCES 업체목록 조회
     * @param commonReqVo
     * @param companyListGet
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/company/search")
    @Description(name = "이데아 플랫폼/PCES 업체목록 조회", description = "이데아 플랫폼/PCES에 등록된 업체목록 조회한다", type = Description.TYPE.MEHTOD)
    public GridResult searchCompanyListByIdea(CommonReqVo commonReqVo, @Valid CompanyForm.CompanyListGet companyListGet, HttpServletRequest request, HttpServletResponse response) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("이데아 플랫폼/PCES 업체목록 조회");
        systemLogComponent.addUserLog(userLog);

        CompanyMybatisParam.CompanyListInput input = companyForm.toCompanyListInput(companyListGet);

        if ("IDEA".equals(companyListGet.getPlatform())) {
            return GridResult.ok(companyComponent.getCompanyListByIdea(input).map(companyDto::fromIdeaCompanyOutput));
        } else {
            return GridResult.ok(companyComponent.getCompanyListByPces(input).map(companyDto::fromPcesCompanyOutput));
        }
    }

    /**
     * 회사 목록 조회 (검색)
     * 
     * @param column
     * @param keyword
     * @return
     */
    @GetMapping("/company-list")
    @Description(name = "회사 목록 조회", description = "회사 목록 조회 - tuiGrid 반환 구조에 맞춰 반환.", type = Description.TYPE.MEHTOD)
    public GridResult getCompanyList(CommonReqVo commonReqVo, @Valid CompanyForm.CompanyListGet companyListGet, @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("회사 목록 조회");
        systemLogComponent.addUserLog(userLog);

        CompanyListInput input = companyForm.toCompanyListInput(companyListGet);

        if(langInfo != null && "ko-KR".equals(langInfo)){
            langInfo = "ko";
        }
        
        input.setLang(langInfo);
        
        return GridResult.ok(companyComponent.getCompanyList(input)
                .map(companyDto::fromSmCompanyOutput));
    }

    /**
     * 회사 목록 조회 (사용자관리에서 사용)
     * 
     * @param searchGroup
     * @return
     */
    @GetMapping("/userCompany")
    @Description(name = "회사 목록 조회", description = "회사 목록 조회 - 사용자관리에서 사용.", type = Description.TYPE.MEHTOD)
    public Result userCompanyList(CommonReqVo commonReqVo, @Valid CompanyForm.UserCompanyListGet userCompanyListGet) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("회사 목록 조회");
        systemLogComponent.addUserLog(userLog);

        UserCompanyListInput input = companyForm.toUserCompanyListInput(userCompanyListGet);
        return Result.ok().put("userComanyList", companyComponent.getUserCompanyList(input));
    }

    /**
     * 회사 조회 (회사 코드)
     * 
     * @param corpNo
     * @return
     */
    @GetMapping("/company/{corpNo}")
    @Description(name = "회사 상세 조회", description = "회사 상세 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getCompany(CommonReqVo commonReqVo, @PathVariable("corpNo") String corpNo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("회사 상세 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("company", companyDto.fromSmCompany(companyComponent.getCompany(corpNo)));
    }


    /**
     * 회사 등록
     * 
     * @param company
     * @return
     */
    @PostMapping("/company/create")
    @Description(name = "회사 등록", description = "회사 등록", type = Description.TYPE.MEHTOD)
    public Result createCompanyList(CommonReqVo commonReqVo, @RequestBody @Valid CompanyForm.Company company) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("회사 등록");
        systemLogComponent.addUserLog(userLog);

        companyComponent.createCompany(companyForm.toSmCompany(company), commonReqVo);

        return Result.ok();
    }

    /**
     * 회사 코드 중복체크
     * 
     * @param corpNo
     * @return
     */
    @GetMapping("/company/exist")
    @Description(name = "회사 코드 중복 체크", description = "회사 코드 중복 체크 (true / false)", type = Description.TYPE.MEHTOD)
    public Result checkCorpNo(CommonReqVo commonReqVo, @RequestParam("corpNo") String corpNo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("회사 코드 중복 체크");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("exist", companyComponent.checkCorpNo(corpNo));
    }

    /**
     * 회사 삭제
     * 
     * @param commonReqVo
     * @param corpNoList
     * @return
     */
    @PostMapping("/company/delete")
    @Description(name = "회사 삭제", description = "회사 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteCompany(CommonReqVo commonReqVo, @RequestBody @Valid CompanyForm.CorpNoList corpNoList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("회사 삭제");
        systemLogComponent.addUserLog(userLog);

        companyComponent.deleteCompanyList(corpNoList.getCorpNoList(), commonReqVo);
        return Result.ok();
    }

    /**
     * 회사 수정
     * 
     * @param company
     * @return
     */
    @PostMapping("/company/update")
    @Description(name = "회사 수정", description = "회사 수정", type = Description.TYPE.MEHTOD)
    public Result updateCompany(CommonReqVo commonReqVo, @RequestBody @Valid CompanyForm.CompanyUpdate company) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("회사 수정");
        systemLogComponent.addUserLog(userLog);

        SmCompany smCompany = companyComponent.getCompany(company.getCorpNo());
        if (smCompany != null) { // 데이터가 있는지 확인
            companyForm.updateSmCompany(company, smCompany); // 전달된 내용만 매핑
            return Result.ok()
                    .put("company", companyComponent.updateCompany(smCompany, commonReqVo)
                            .map(companyDto::fromSmCompany));
        }
        throw new GaiaBizException(ErrorType.NO_DATA);
    }

}
