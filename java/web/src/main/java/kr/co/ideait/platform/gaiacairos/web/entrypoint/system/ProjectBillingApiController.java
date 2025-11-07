package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ideait.platform.gaiacairos.comp.system.service.ProjectBillingService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmProjectBilling;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.projectbilling.ProjectBillingDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.projectbilling.ProjectBillingForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@IsUser
@RestController
@RequestMapping("/api/system/projectbilling")
public class ProjectBillingApiController extends AbstractController {

    @Autowired
    ProjectBillingService projectBillingService;

    @Autowired
    ProjectBillingDto projectBillingDto;

    @Autowired
    ProjectBillingForm projectBillingForm;

     /**
     * 유료 기능용 Tree 조회
     */
    // @IsAdmin
    @GetMapping("/cmis-project-list")
    @Description(name = "계약 리스트 tree 조회", description = "유료 기능용 계약 정보 Tree 조회", type = Description.TYPE.MEHTOD)
    public Result getAdminDepartmentTree(CommonReqVo commonReqVo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("계약 리스트 tree 조회");
        systemLogComponent.addUserLog(userLog);

        log.debug("==============================================");
		log.debug("cmis-project-list");
    	log.debug("==============================================");
        return Result.ok().put("treeList", projectBillingService.getCmisProjectList());
    }

    /**
     * 프로젝트의 유료 기능 목록 조회
     */
    @GetMapping("/{cntrctNo}/project-billing-list")
    @Description(name = "계약번호에 따른 유료 기능 리스트 조회", description = "계약번호에 따른 유료 기능 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getProjectuBillingList(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("계약번호에 따른 유료 기능 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        log.debug("==============================================");
		log.debug("project-billing-list" + cntrctNo);
    	log.debug("==============================================");

        return Result.ok()
                .put("billingList", projectBillingService.getProjectuBillingList(cntrctNo).stream()
                        .map(projectBillingDto::fromSmProjectBillingMybatis));
    }

    /**
     * 프로젝트의 유료 기능 목록 등록 및 삭제
     */
    @PostMapping("/create")
    @Description(name = "계약번호에 따른 유료 기능 등록 및 삭제", description = "계약번호에 따른 유료 기능 등록 및 삭제", type = Description.TYPE.MEHTOD)
    public Result createProjectBilling(CommonReqVo commonReqVo, @RequestPart(value = "param") Map<String, Object> param) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("계약번호에 따른 유료 기능 등록 및 삭제");
        systemLogComponent.addUserLog(userLog);


        ObjectMapper mapper = new ObjectMapper();  
        List<ProjectBillingForm.ProjectBilling> pjtBills = mapper.convertValue(param.get("createBil"), new TypeReference<List<ProjectBillingForm.ProjectBilling>>() {});
        List<Integer> delPjtBils = mapper.convertValue(param.get("deleteBil"), new TypeReference<List<Integer>>() {});

        log.debug("==============================================");
		log.debug("createProjectBilling : "+ delPjtBils);
    	log.debug("==============================================");

        if (pjtBills.size () > 0) {
            pjtBills.forEach((bill) -> {
                SmProjectBilling bi = projectBillingForm.toSmProjectBilling(bill);
                projectBillingService.createProjectBilling(bi).map(projectBillingDto::fromSmProjectBilling);
            });
        }

        if (delPjtBils.size() > 0)
            projectBillingService.deleteProjectBilling(delPjtBils);

        return Result.ok();
    }
}
