package kr.co.ideait.platform.gaiacairos.web.entrypoint.safety;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.safety.SadtagComponent;
import kr.co.ideait.platform.gaiacairos.comp.safety.SafetyComponent;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetymgmtService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSadtag;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.SadtagForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;

@RestController
@RequestMapping("api/safetymgmt/sadtag")
public class SadtagApiController extends AbstractController {

    @Autowired
    SafetymgmtService safetyService;

    @Autowired
    SadtagForm sadtagForm;

    @Autowired
    SafetyComponent safetyComponent;

    @Autowired
    SadtagComponent sadtagComponent;

    /**
     * 안전지적서 목록 조회
     */
    @PostMapping("/get/sadtagList")
    @Description(name = "안전지적서 목록 조회", description = "안전지적서 데이터 전체 조회", type = Description.TYPE.MEHTOD)
    public Result getSadtagList(CommonReqVo commonReqVo, @RequestBody SadtagForm.Sadtag input) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전지적서 데이터 전체 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("sadtagList",
                safetyService.getSadTagList(input.getCntrctNo(), input.getSearchValue(), input.getSelectedStatus()));
    }

    /*
     * 안전지적서 조회
     */
    @GetMapping("/get/{cntrctNo}/{sadtagNo}")
    @Description(name = "안전지적서 조회", description = "안전지적서 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getSadtag(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo,
            @PathVariable("sadtagNo") String sadtagNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전지적서 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("sadtag", safetyService.getSadtag(cntrctNo, sadtagNo));
    }

    /**
     * 안전지적서 추가
     */
    @PostMapping("/create")
    @Description(name = "안전지적서 추가", description = "안전지적서 데이터 추가", type = Description.TYPE.MEHTOD)
    public Result createSadtag(CommonReqVo commonReqVo, @RequestBody SadtagForm.Sadtag sadtag) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전지적서 데이터 추가");
        systemLogComponent.addUserLog(userLog);

        CwSadtag cwSadtag = sadtagForm.toEntity(sadtag);
        cwSadtag.setDltYn("N");
        safetyService.createSadtag(cwSadtag);

        return Result.ok();
    }

    /**
     * 조치결과 등록/수정(권한 처리로 추가/수정과 분리)
     */
    @PostMapping("/result")
    @Description(name = "조치결과 등록", description = "조치결과 등록", type = Description.TYPE.MEHTOD)
    public Result updateResult(CommonReqVo commonReqVo, @RequestBody SadtagForm.Sadtag sadtag) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("조치결과 등록");
        systemLogComponent.addUserLog(userLog);

        CwSadtag oldSadtag = safetyService.getSadTagData(sadtag.getCntrctNo(), sadtag.getSadtagNo());
        sadtagForm.updateSadtag(sadtag, oldSadtag);
        safetyService.createSadtag(oldSadtag);
        return Result.ok();
    }

    /**
     * 안전지적서 수정
     */
    @PostMapping("/update")
    @Description(name = "안전지적서 수정", description = "안전지적서 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateSadtag(CommonReqVo commonReqVo, @RequestBody SadtagForm.Sadtag sadtag) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전지적서 데이터 수정");
        systemLogComponent.addUserLog(userLog);

        CwSadtag oldSadtag = safetyService.getSadTagData(sadtag.getCntrctNo(), sadtag.getSadtagNo());
        sadtagForm.updateSadtag(sadtag, oldSadtag);
        safetyService.createSadtag(oldSadtag);
        return Result.ok();
    }

    /**
     * 안전지적서 삭제
     */
    @PostMapping("/delete")
    @Description(name = "안전지적서 삭제", description = "안전지적서 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteSadtag(CommonReqVo commonReqVo, @RequestBody SadtagForm.Sadtags sadtagList) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전지적서 데이터 삭제");
        systemLogComponent.addUserLog(userLog);

        for (int i = 0; i < sadtagList.getSadtagList().size(); i++) {
            CwSadtag delete = safetyService.getSadTagData(sadtagList.getSadtagList().get(i).getCntrctNo(),
                    sadtagList.getSadtagList().get(i).getSadtagNo());
            sadtagComponent.deleteList(delete);
        }
        return Result.ok();
    }

    /**
     * 승인요청
     */
    @PostMapping("/approval")
    @Description(name = "안전지적서 승인요청", description = "승인요청", type = Description.TYPE.MEHTOD)
    public Result approvalSafety(CommonReqVo commonReqVo, @RequestBody SadtagForm.Sadtag sadtag) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("승인요청");
        systemLogComponent.addUserLog(userLog);

        sadtagComponent.requestApprovalSadtag(sadtag, commonReqVo.getApiYn(), commonReqVo.getPjtDiv());
        return Result.ok();
    }
}
