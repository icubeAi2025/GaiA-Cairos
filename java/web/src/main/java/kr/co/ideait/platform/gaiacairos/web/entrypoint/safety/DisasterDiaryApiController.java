package kr.co.ideait.platform.gaiacairos.web.entrypoint.safety;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.safety.DisasterDiaryComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report.DisasterDiaryForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report.DisasterDiaryRequest;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/safety/disaster-diary")
public class DisasterDiaryApiController extends AbstractController {

    @Autowired
    DisasterDiaryComponent disasterDiaryComponent;

    /**
     * 재해일지 목록 조회
     * @param commonReqVo
     * @param param
     * @return
     */
    @PostMapping("/list")
    @Description(name = "재해일지 목록 조회", description = "프로젝트의 계약별 재해일지 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getDisasterDiaryList(CommonReqVo commonReqVo, @RequestBody DisasterDiaryForm.disasterDiaryListParam param) {
        Page<Map<String, Object>> resultList = disasterDiaryComponent.getDisasterDiaryList(commonReqVo, param);

        return GridResult.ok(resultList);
    }

    /**
     * 재해일지 추가
     * @param commonReqVo
     * @param report
     * @return
     */
    @PostMapping("/create")
    @Description(name = "재해일지 추가", description = "재해일지 추가", type = Description.TYPE.MEHTOD)
    public Result createDisasterDiary(
            CommonReqVo commonReqVo,
            @RequestBody DisasterDiaryRequest report) {

        // Log
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("재해일지 추가");
        systemLogComponent.addUserLog(userLog);

        log.info("createDisasterDiary: 재해일지 추가. params = {}", report.toString());
        disasterDiaryComponent.addDisasterDiary(report, commonReqVo);

        return Result.ok();

    }

    /**
     * 재해일지 상세조회
     * @param commonReqVo
     * @param request
     * @return
     */
    @GetMapping("/detail")
    @Description(name = "재해 일지 상세조회", description = "재해일지 상세조회", type = Description.TYPE.MEHTOD)
    public Result getSafetyDiary(CommonReqVo commonReqVo,
                                 @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                                 @RequestParam(value = "disasId", required = false) String disasId,
                                 HttpServletRequest request) {

        Map<String, Object> disasterDiaryData = disasterDiaryComponent.getDisasterDiary(cntrctNo, disasId);
        Result result = Result.ok();

        // 1. 재해일지 상세조회
        result.put("data", disasterDiaryData);

        return result;
    }

    /**
     * 재해일지 수정
     * @param commonReqVo
     * @param request
     * @param report
     * @return
     */
    @PostMapping("/update")
    @Description(name = "재해일지 수정", description = "재해일지 수정", type = Description.TYPE.MEHTOD)
    public Result updateDisasterDiary(
            CommonReqVo commonReqVo,
            HttpServletRequest request,
            @RequestBody DisasterDiaryRequest report) {

        // Log
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("재해일지 수정");
        systemLogComponent.addUserLog(userLog);

        log.info("updateDisasterDiary: 재해일지 수정. params = {}", report.toString());
        disasterDiaryComponent.modifyDisasterDiary(report, commonReqVo);

        return Result.ok();

    }

    /**
     * 재해일지 삭제
     * @param commonReqVo
     * @param request
     * @param param
     * @return
     */
    @PostMapping("/delete/list")
    @Description(name = "재해일지 삭제(다건)", description = "재해일지 삭제(다건)", type = Description.TYPE.MEHTOD)
    public Result deleteDisasterDiaryList(
            CommonReqVo commonReqVo,
            HttpServletRequest request,
            @RequestBody DisasterDiaryForm.disasterDiaryDeleteParam param) {

        // Log
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("재해일지 삭제");
        systemLogComponent.addUserLog(userLog);

        if(param.getCntrctNo() == null){
            param.setCntrctNo(commonReqVo.getCntrctNo());
        }

        log.info("deleteDisasterDiary: 재해일지 삭제. params = {}", param.toString());
        disasterDiaryComponent.deleteDisasterDiaryList(param, commonReqVo);

        return Result.ok();

    }

    /**
     * 재해일지 삭제
     * @param commonReqVo
     * @param request
     * @param param
     * @return
     */
    @PostMapping("/delete")
    @Description(name = "재해일지 삭제", description = "재해일지 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteDisasterDiary(
            CommonReqVo commonReqVo,
            HttpServletRequest request,
            @RequestBody Map<String,Object> param) {

        // Log
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("재해일지 삭제");
        systemLogComponent.addUserLog(userLog);

        if(param.get("disasterDiaryDeleteParam") == null){
            throw new GaiaBizException(ErrorType.BAD_REQUEST, "삭제할 재해일지 정보가 없습니다.");
        }

        List<Map<String, Object>> disasterDiaryDeleteParam = (List<Map<String, Object>>) param.get("disasterDiaryDeleteParam");
        String cntrctNo = (String) disasterDiaryDeleteParam.getFirst().get("cntrct_no");
        String disasId = (String) disasterDiaryDeleteParam.getFirst().get("disas_id");

        log.info("deleteDisasterDiary: 재해일지 삭제. params = {}", param);
        disasterDiaryComponent.deleteDisasterDiary(disasId, cntrctNo, commonReqVo);

        return Result.ok();

    }


}
