package kr.co.ideait.platform.gaiacairos.web.entrypoint.safety;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import kr.co.ideait.platform.gaiacairos.comp.safety.EducationDiaryComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.EducationDiaryForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/safety/educationdiary")
public class EducationDiaryAPIController extends AbstractController {
	
	@Autowired
	EducationDiaryComponent educationDiaryComponent;
	
	/**
     * 교육일지 목록조회
     */
	@GetMapping("/eduDiaryList")
    @Description(name = "교육일지 목록조회", description = "교육일지 목록을 조회한다.", type = Description.TYPE.MEHTOD)
    public GridResult getEducationDiaryList(CommonReqVo commonReqVo, @Valid EducationDiaryForm.EduDiaryListForm eduDiaryListForm,
    							  HttpServletRequest request,
                                  @CookieValue(name = "lang", required = false) String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("교육일지 목록조회");
        
        return GridResult.ok(educationDiaryComponent.educationDiaryList(eduDiaryListForm));
    }
    
    /**
     * 교육일지 상세조회
     */
    @GetMapping("/eduDiary-read")
    @Description(name = "교육일지 상세조회", description = "교육일지를 상세조회한다.", type = Description.TYPE.MEHTOD)
    public Result getEducationDiary(CommonReqVo commonReqVo, @Valid EducationDiaryForm.EduDiaryForm eduDiaryForm,
    							  HttpServletRequest request,
                                  @CookieValue(name = "lang", required = false) String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("교육일지 상세조회");
        
        return Result.ok().put("educationDiaryInfoList", educationDiaryComponent.educationDiary(eduDiaryForm.getEduId(), eduDiaryForm.getOpenType()));
    }
    
    /**
     * 교육일지 등록
     */
    @PostMapping("/eduDiary-add")
    @Description(name = "교육일지 등록", description = "작성한 교육일지를 등록한다.", type = Description.TYPE.MEHTOD)
    public Result createEducationDiary(CommonReqVo commonReqVo, @RequestBody EducationDiaryForm.EduDiaryCrateForm eduDiaryCrateForm,
                                  HttpServletRequest request,
                                  @CookieValue(name = "lang", required = false) String langInfo) throws JsonProcessingException {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("교육일지 등록");
        
        educationDiaryComponent.createEducationDiary(eduDiaryCrateForm);
        
        return Result.ok();
    }
    
    /**
     * 교육일지 수정
     */
    @PostMapping("/eduDiary-modify")
    @Description(name = "교육일지 수정", description = "수정한 교육일지를 저장한다.", type = Description.TYPE.MEHTOD)
    public Result modifyEducationDiary(CommonReqVo commonReqVo, @RequestBody EducationDiaryForm.EduDiaryCrateForm eduDiaryCrateForm,
                                  HttpServletRequest request,
                                  @CookieValue(name = "lang", required = false) String langInfo) throws JsonProcessingException {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("교육일지 수정");
        
        educationDiaryComponent.updateEducationDiary(eduDiaryCrateForm);
        
        return Result.ok();
    }
    
    /**
     * 교육일지 정보 삭제
     */
    @PostMapping("/eduDiary-delete")
    @Description(name = "교육일지 삭제", description = "교육일지를 삭제처리한다.", type = Description.TYPE.MEHTOD)
    public Result deleteResourcesInfo(CommonReqVo commonReqVo, @RequestBody @Valid EducationDiaryForm.eduIdListForm eduIdList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("교육일지 삭제");
        
        educationDiaryComponent.deleteEducationDiary(eduIdList);
        
        return Result.ok();
    }

}
