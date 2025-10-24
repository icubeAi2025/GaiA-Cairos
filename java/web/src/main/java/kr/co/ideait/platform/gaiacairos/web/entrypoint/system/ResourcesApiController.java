package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.system.ResourcesComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources.ResourcesForm;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/system/resources")
public class ResourcesApiController extends AbstractController {
	
	@Autowired
	ResourcesComponent resourcesComponent;
	
	/**
     * 프로그램 목록조회
     */
    @GetMapping("/list")
    @Description(name = "프로그램 목록조회", description = "프로그램 관리의 목록조회입니다.", type = Description.TYPE.MEHTOD)
    public GridResult getResourcesList(CommonReqVo commonReqVo, @Valid ResourcesForm.ResourcesListForm resourcesListForm,
                                  HttpServletRequest request,
                                  @CookieValue(name = "lang", required = false) String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로그램 목록조회");     
        
        return GridResult.ok(resourcesComponent.getResourcesList(resourcesListForm));
    }
    
    /**
     * 프로그팸 정보 상세조회
     */
    @GetMapping("/resources-read")
    @Description(name = "프로그램정보 상세조회", description = "프로그램 정보를 상세조회한다.", type = Description.TYPE.MEHTOD)
    public Result getResourcesInfo(CommonReqVo commonReqVo, @Valid ResourcesForm.ResourcesReadForm resourcesReadForm,
                                  HttpServletRequest request,
                                  @CookieValue(name = "lang", required = false) String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로그램정보 상세조회");

        return Result.ok().put("resourcesInfo", resourcesComponent.getResourcesInfo(resourcesReadForm));
    }

    /**
	 * 	프로그램 정보 등록
	 */
    @GetMapping("/resources-create")
	@Description(name = "프로그램 정보 등록", description = "GAiA/CAiROS 프로그램 정보를 DB에 입력한다.", type = Description.TYPE.MEHTOD)
    public Result createResourcesInfo(CommonReqVo commonReqVo, @Valid ResourcesForm.ResourcesInsertForm resourcesInsertForm,
                                  HttpServletRequest request,
                                  @CookieValue(name = "lang", required = false) String langInfo) {  
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로그램 정보 등록");
		
		return Result.ok().put("insertCount",resourcesComponent.createResourcesInfo(resourcesInsertForm, commonReqVo.getApiYn()));
    }
    
    
    /**
     * 프로그램 아이디 중복검사
     */
    @GetMapping("/id-exist")
    @Description(name = "프로그램 아이디 중복검사", description = "등록할 프로그램 아이디를 중복검사한다.", type = Description.TYPE.MEHTOD)
    public Result getResourcesIdExist(CommonReqVo commonReqVo, @Valid ResourcesForm.ResourcesExistForm resourcesExistForm,
                                  HttpServletRequest request,
                                  @CookieValue(name = "lang", required = false) String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로그램 아이디 중복검사");
        
        return Result.ok().put("exist", resourcesComponent.getResourcesIdExist(resourcesExistForm));
    }
    
    /**
     * 프로그램 URI 중복검사
     */
    @GetMapping("/url-exist")
    @Description(name = "프로그램 URL 중복검사", description = "등록할 프로그램 URL을 중복검사한다.", type = Description.TYPE.MEHTOD)
    public Result getResourcesUrlExist(CommonReqVo commonReqVo, @Valid ResourcesForm.ResourcesExistForm resourcesExistForm,
                                  HttpServletRequest request,
                                  @CookieValue(name = "lang", required = false) String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로그램 URL 중복검사");

        return Result.ok().put("exist", resourcesComponent.getResourcesUrlExist(resourcesExistForm));
    }
    
    /**
     * 프로그램 정보 수정
     */
    @GetMapping("/resources-update")
   	@Description(name = "프로그램 정보 수정", description = "GAiA/CAiROS 프로그램 정보를 수정한다.", type = Description.TYPE.MEHTOD)
    public Result updateResourcesInfo(CommonReqVo commonReqVo, @Valid ResourcesForm.ResourcesInsertForm resourcesInsertForm,
                                 HttpServletRequest request,
                                 @CookieValue(name = "lang", required = false) String langInfo) {  
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("프로그램 정보 수정");   
		
		return Result.ok().put("updateCount",resourcesComponent.updateResourcesInfo(resourcesInsertForm, commonReqVo.getApiYn()));
    }
    
    /**
     * 프로그램 정보 삭제
     */
    @PostMapping("/resources-delete")
    @Description(name = "프로그램 정보 삭제", description = "GAiA/CAiROS 프로그램 정보를 삭제처리한다.", type = Description.TYPE.MEHTOD)
    public Result deleteResourcesInfo(CommonReqVo commonReqVo, @RequestBody @Valid ResourcesForm.RescIdList rescIdList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로그램 정보 삭제");
        
        resourcesComponent.deleteResourcesInfo(rescIdList, commonReqVo.getApiYn());
        
        return Result.ok();
    }
    
}
