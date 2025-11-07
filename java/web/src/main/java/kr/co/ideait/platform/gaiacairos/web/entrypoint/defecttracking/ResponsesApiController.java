package kr.co.ideait.platform.gaiacairos.web.entrypoint.defecttracking;

import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.DefectTrackingComponent;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.ResponsesComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingForm.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingMybatisParam.DefectTrackingListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.responses.ResponsesForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.responses.ResponsesForm.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/defecttracking/responses")
public class ResponsesApiController extends AbstractController {

    @Autowired
    DefectTrackingComponent defectTrackingComponent;

    @Autowired
    ResponsesComponent responsesComponent;

    @Autowired
    ResponsesForm responsesForm;

    @Autowired
    DefectTrackingForm defectTrackingForm;


    /**
     * 답변관리 - 결함 목록 조회
     * @param commonReqVo
     * @param defectTrackingListGet
     * @param langInfo
     * @param user
     * @return
     */
    @PostMapping("/dfccyList")
    @Description(name = "결함 목록 조회", description = "계약의 결함 목록 조회", type = Description.TYPE.MEHTOD)
    public Result getDfccyList(CommonReqVo commonReqVo, @RequestBody @Valid DefectTrackingListGet defectTrackingListGet,
                               @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo, UserAuth user) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("답변관리 > 결함 목록 조회");

        systemLogComponent.addUserLog(userLog);

        Page<DefectTrackingListOutput> pageData = defectTrackingComponent.getDfccyListToGrid(defectTrackingForm.toDfccySearchInput(defectTrackingListGet), langInfo);
        Long totalCount = pageData.getTotalElements();

        return Result.ok().put("dfccyList", pageData.getContent())
                .put("totalCount", totalCount);
    }


    /**
     * 답변관리 - 입력창 기본데이터
     * @param commonReqVo
     * @param responsesGet
     * @return
     */
    @PostMapping("/detail")
    @Description(name = "결합 답변 상세조회", description = "계약의 결함 답변 상세조회", type = Description.TYPE.MEHTOD)
    public Result getDeficiencyPhase(CommonReqVo commonReqVo, @RequestBody ResponsesGet responsesGet) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("답변관리 > 결함 답변 상세조회");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("returnMap", responsesComponent.getResponses(responsesGet));
    }


    /**
     * 결함 답변 추가
     * @param commonReqVo
     * @param response
     * @param files
     * @return
     */
    @PostMapping("/save")
    @Description(name = "결함 답변 추가 및 수정", description = "계약의 결함 답변 추가 및 수정", type = Description.TYPE.MEHTOD)
    public Result saveResponses(CommonReqVo commonReqVo,
                                @RequestPart("data") ResponsesSave response,
                                @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("답변관리 > 결함 답변 추가");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("response", responsesComponent.saveResponses(responsesForm.toDtDeficiencyReply(response), files, commonReqVo));
    }


    /**
     * 결함 답변 수정
     * @param commonReqVo
     * @param response
     * @param files
     * @return
     */
    @PostMapping("/update")
    @Description(name = "결함 답변 수정", description = "계약의 결함 답변 수정", type = Description.TYPE.MEHTOD)
    public Result updateResponses(CommonReqVo commonReqVo,
                                @RequestPart("data") ResponsesSave response,
                                @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("답변관리 > 결함 답변 수정");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("response", responsesComponent.updateResponses(responsesForm.toDtDeficiencyReply(response), response.getDelFileList(), files, commonReqVo));
    }


    /**
     * 답변 삭제
     * @param commonReqVo
     * @param responsesList
     * @return
     */
    @PostMapping("/delete")
    @Description(name = "결함 답변 삭제", description = "계약의 결함 답변 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteResponses(CommonReqVo commonReqVo, @RequestBody @Valid ResponsesList responsesList) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("답변관리 > 결함 답변 삭제");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("rplyY", responsesComponent.deleteResponses(responsesList.getResponsesList()));
    }


    /**
     * 답변 확인
     * @param commonReqVo
     * @param responsesList
     * @param user
     * @return
     */
    @PostMapping("/confirm")
    @Description(name = "결함 답변 확인 처리", description = "계약의 결함 답변 확인 처리", type = Description.TYPE.MEHTOD)
    public Result confirmResponses(CommonReqVo commonReqVo, @RequestBody @Valid ResponsesList responsesList, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("답변관리 > 결함 답변 확인 처리");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("rplyY", responsesComponent.confirmResponses(responsesList.getResponsesList(), user.getUsrId(), commonReqVo));
    }

}
