package kr.co.ideait.platform.gaiacairos.web.entrypoint.defecttracking;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.SettingComponent;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.TerminationComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiency;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingForm.DefectTrackingListGet;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingMybatisParam.DefectTrackingListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.termination.TerminationForm.CreateUpdateForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.termination.TerminationForm.DeleteTermination;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.termination.TerminationForm.TerminationAll;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.termination.TerminationForm.TerminationGet;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/defecttracking/termination")
public class TerminationApiController extends AbstractController {


    @Autowired
    DefectTrackingForm defectTrackingForm;

    @Autowired
    TerminationComponent terminationComponent;

    @Autowired
    SettingComponent settingComponent;


    /**
     * 종결관리 - 결함 목록 조회
     * @param commonReqVo
     * @param defectTrackingListGet
     * @param langInfo
     * @return
     */
    @PostMapping("/list")
    @Description(name = "종결관리 > 결함 목록 조회", description = "종결 처리가 가능한 결함 목록 조회 - 답변과 확인 처리가 완료된 결함 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getDfccyList(CommonReqVo commonReqVo,
                               @RequestBody @Valid DefectTrackingListGet defectTrackingListGet,
                               @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("종결관리 > 결함 목록 조회");

        systemLogComponent.addUserLog(userLog);

        Page<DefectTrackingListOutput> pageData = terminationComponent.getDfccyListToGrid(defectTrackingForm.toDfccySearchInput(defectTrackingListGet), langInfo);
        Long totalCount = pageData.getTotalElements();

        return Result.ok().put("dfccyList", pageData.getContent())
                .put("totalCount", totalCount);
    }


    /**
     * 종결관리 - 종결 입력 기간 조회
     * @param commonReqVo
     * @param terminationGet
     * @return
     */
    @PostMapping("/detail")
    @Description(name = "종결관리 > 종결 입력 기간 조회", description = "종결 입력 기간 조회", type = Description.TYPE.MEHTOD)
    public Result getDeficiencyPhase(CommonReqVo commonReqVo, @RequestBody @Valid TerminationGet terminationGet){

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("종결 입력 기간 조회");

        systemLogComponent.addUserLog(userLog);

        // 입력 값 null 체크
        if (terminationGet == null || terminationGet.getCntrctNo() == null || terminationGet.getCntrctNo().trim().isEmpty() || 
            terminationGet.getDfccyPhaseNo() == null) {
            return Result.nok(ErrorType.BAD_REQUEST, "필수 입력 값이 누락되었습니다.");
        }

        MybatisInput input = MybatisInput.of()
                .add("cntrctNo", terminationGet.getCntrctNo())
                .add("dfccyPhaseNo", terminationGet.getDfccyPhaseNo());

        List<Map<String, ?>> phase = settingComponent.selectDeficiencyPhaseList(input);

        if (phase == null || phase.isEmpty()) {
            return Result.nok(ErrorType.NOT_FOUND, "해당하는 데이터가 없습니다.");
        }

        return Result.ok().put("phase", phase);
    }


    /**
     * 종결 추가, 수정
     * @param commonReqVo
     * @param createUpdateForm
     * @param user
     * @return
     */
    @PostMapping("/save")
    @Description(name = "종결관리 > 종결 추가, 수정", description = "해당 결함 사항에 대해 종결 처리(추가, 수정)", type = Description.TYPE.MEHTOD)
    public Result createUpdateTermination(CommonReqVo commonReqVo, @RequestBody @Valid CreateUpdateForm createUpdateForm, UserAuth user){

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("종결 처리(추가, 수정)");

        systemLogComponent.addUserLog(userLog);

        // 입력값 검증 (필수값 확인)
        if (createUpdateForm == null || user == null) {
            return Result.nok(ErrorType.INVAILD_INPUT_DATA, "입력값이 올바르지 않습니다.");
        }

        DtDeficiency updateDeficiency = terminationComponent.saveTermination(createUpdateForm, user, commonReqVo);

        if(!createUpdateForm.getEdCd().equals(updateDeficiency.getEdCd())){
            return Result.nok(ErrorType.DATABSE_ERROR, "저장에 실패했습니다.");
        }

        return Result.ok();
    }


    /**
     * 종결 일괄 추가
     * @param commonReqVo
     * @param terminationAll
     * @param user
     * @return
     */
    @PostMapping("/finish-all")
    @Description(name = "종결관리 > 종결 일괄 추가", description = "해당 결함 사항에 대해 종결 처리(일괄 추가)", type = Description.TYPE.MEHTOD)
    public Result createTerminationList(CommonReqVo commonReqVo, @RequestBody @Valid TerminationAll terminationAll, UserAuth user){

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("종결 처리(일괄 추가)");

        systemLogComponent.addUserLog(userLog);

        // 입력값 검증 (필수값 확인)
        if (terminationAll == null || user == null) {
            return Result.nok(ErrorType.INVAILD_INPUT_DATA, "입력값이 올바르지 않습니다.");
        }

        if(!terminationAll.getDfccyNoList().isEmpty()) {
            terminationComponent.saveTerminationList(terminationAll, user, commonReqVo);
        }
        return Result.ok();
    }


    /**
     * 종결 처리 삭제
     * @param commonReqVo
     * @param deleteTermination
     * @param user
     * @return
     */
    @PostMapping("/delete")
    @Description(name = "종결관리 > 종결 처리 삭제", description = "해당 결함 사항에 대해 종결 처리 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteTerminationList(CommonReqVo commonReqVo, @RequestBody @Valid DeleteTermination deleteTermination, UserAuth user){

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("해당 결함 사항에 대해 종결 처리 삭제");

        systemLogComponent.addUserLog(userLog);

        // 입력값 검증 (필수값 확인)
        if (deleteTermination == null || user == null) {
            return Result.nok(ErrorType.INVAILD_INPUT_DATA, "입력값이 올바르지 않습니다.");
        }

        if(deleteTermination.getDfccyNoList().isEmpty()){
            return Result.nok(ErrorType.INVAILD_INPUT_DATA, "결함 정보가 존재하지 않습니다.");
        }

        terminationComponent.deleteTerminationList(deleteTermination, user, commonReqVo);

        return Result.ok();
    }
}
