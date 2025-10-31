package kr.co.ideait.platform.gaiacairos.web.entrypoint.defecttracking;

import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.DefectTrackingComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiency;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingForm.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/defecttraking/tool/defectTracking")
public class DefectTrackingApiController extends AbstractController {

    @Autowired
    DefectTrackingComponent defectTrackingComponent;

    @Autowired
    DefectTrackingForm defectTrackingForm;

    @Autowired
    DefectTrackingDto defectTrackingDto;


    /**
     * 결함단계 리스트 조회
     * @param commonReqVo
     * @param dfccyPhaseListGet
     * @return
     */
    @GetMapping("/dfccyPhase/list")
    @Description(name = "결함단계 리스트 조회", description = "결함단계 설정에서 정의한 결함단계 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result dfccyPhaseList(CommonReqVo commonReqVo, @Valid DfccyPhaseListGet dfccyPhaseListGet) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 결함단계 리스트 조회");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("dfccyPhaseList", defectTrackingComponent.getDfccyPhaseList(dfccyPhaseListGet.getCntrctNo(), dfccyPhaseListGet.getDfccyPhaseCd())
                                                                            .stream().map(defectTrackingDto::toDfccyPhase));
    }


    /**
     * 결함추적관리 - 결함 목록 조회
     * @param commonReqVo
     * @param defectTrackingListGet
     * @param langInfo
     * @return
     */
    @GetMapping("/list")
    @Description(name = "결함 목록 조회", description = "결함 데이터 리스트 조회 - tuiGrid 반환 구조에 맞춰서 반환.", type = Description.TYPE.MEHTOD)
    public GridResult getDfccyList(CommonReqVo commonReqVo,
                                   @Valid DefectTrackingListGet defectTrackingListGet,
                                   @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 결함 목록 조회");

        systemLogComponent.addUserLog(userLog);

        return GridResult.ok(defectTrackingComponent.getDfccyListToGrid(defectTrackingForm.toDfccySearchInput(defectTrackingListGet), langInfo));

    }


    /**
     * 결함추적관리 - 결함 상세 조회
     * @param commonReqVo
     * @param defectTrackingListGet
     * @param langInfo
     * @return
     */
    @PostMapping("/detail")
    @Description(name = "결함 상세 조회", description = "해당하는 결함 데이터 상세 조회 - 확인, 답변, 종결 데이터 포함 조회", type = Description.TYPE.MEHTOD)
    public Result getDfccyDetail(CommonReqVo commonReqVo,
                                 @RequestBody @Valid DefectTrackingListGet defectTrackingListGet,
                                 @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 선택한 결함 데이터 상세 조회");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("dfccyDetail", defectTrackingComponent.getDfccyList(defectTrackingForm.toDfccySearchInput(defectTrackingListGet), langInfo));
    }


    /**
     * 결함 추적관리 - 작성자 목록 조회
     * @param commonReqVo
     * @param cntrctNo
     * @param dfccyPhaseNo
     * @return
     */
    @GetMapping("/rgstr-list/{cntrctNo}/{dfccyPhaseNo}")
    @Description(name = "결함 작성자 목록 조회", description = "결함 작성자 목록 조회", type = Description.TYPE.MEHTOD)
    public Result getRgstrList(CommonReqVo commonReqVo,
                               @PathVariable("cntrctNo") String cntrctNo,
                               @PathVariable("dfccyPhaseNo") String dfccyPhaseNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 결함 작성자 목록 조회");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("rgstrList", defectTrackingComponent.getRgstrList(cntrctNo, dfccyPhaseNo));
    }


    // ---------- 결함 추가/수정 ----------
    /**
     * Activity 선택 - 목록
     * @param commonReqVo
     * @param params
     * @return
     */
    @PostMapping("/activity/list")
    @Description(name = "결함 추적 관리 > 결함 추가/수정 - Activity 리스트 조회", description = "결함을 추가할 때 해당 Activity 선택을 위한 Activity 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getActivityList(CommonReqVo commonReqVo, @RequestBody Map<String, Object> params) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 결함 추가/수정 - Activity 리스트 조회");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("activityList", defectTrackingComponent.getActivityList(params));
    }


    /**
     * Activity 선택 - 검색
     * @param commonReqVo
     * @param params
     * @return
     */
    @PostMapping("/activity/listSearch")
    @Description(name = "결함 추적 관리 > 결함 추가/수정 - Activity 리스트 검색", description = "결함을 추가할 때 해당 Activity 선택을 위한 Activity 리스트 검색", type = Description.TYPE.MEHTOD)
    public Result getActivityListSearch(CommonReqVo commonReqVo, @RequestBody Map<String, Object> params) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 결함 추가/수정 - Activity 리스트 검색");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("activityListSearch", defectTrackingComponent.getActivityListSearch(params));
    }


    /**
     * 결함 추적 관리 > 결함 조회
     * @param commonReqVo
     * @param cntrctNo
     * @param dfccyPhaseNo
     * @param dfccyNo
     * @param langInfo
     * @return
     */
    @GetMapping("/dfccy/{cntrctNo}/{dfccyPhaseNo}/{dfccyNo}")
    @Description(name = "결함 추적 관리 > 결함 수정 - 결함 조회", description = "결함 수정 시, 해당 결함의 기존 데이터 정보 조회", type = Description.TYPE.MEHTOD)
    public Result getDfccy(CommonReqVo commonReqVo,
                           @PathVariable("cntrctNo") String cntrctNo,
                           @PathVariable("dfccyPhaseNo") String dfccyPhaseNo,
                           @PathVariable("dfccyNo") String dfccyNo,
                           @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 결함 수정 - 결함 조회");

        systemLogComponent.addUserLog(userLog);
        return Result.ok().put("returnMap", defectTrackingComponent.getDfccy(cntrctNo, dfccyPhaseNo, dfccyNo, langInfo));
    }


    /**
     * 결함 추적 관리 > 결함 추가
     * @param commonReqVo
     * @param dfccy
     * @param files
     * @return
     */
    @PostMapping("/dfccy/create")
    @Description(name = "결함 추적 관리 > 결함 추가", description = "결함을 추가할 때 결함 정보와 첨부파일을 저장", type = Description.TYPE.MEHTOD)
    public Result createDfccy(CommonReqVo commonReqVo,
                              @RequestPart("dfccy") CreateUpdateDfccy dfccy,
                              @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 결함 추가");

        systemLogComponent.addUserLog(userLog);

        defectTrackingComponent.createDeficiency(defectTrackingForm.toDeficiency(dfccy), defectTrackingForm.toDeficiencyActivityList(dfccy.getActivity()), files);
        return Result.ok();
    }


    /**
     * 결함 추적 관리 > 결함 수정
     * @param commonReqVo
     * @param update
     * @param newFiles
     * @return
     */
    @PostMapping("/dfccy/update")
    @Description(name = "결함 추적 관리 > 결함 수정", description = "결함을 수정할 때 결함 정보와 첨부파일을 저장", type = Description.TYPE.MEHTOD)
    public Result updateDfccy(CommonReqVo commonReqVo,
                              @RequestPart("dfccy") CreateUpdateDfccy update,
                              @RequestPart(value = "files", required = false) List<MultipartFile> newFiles) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 결함 수정");

        systemLogComponent.addUserLog(userLog);

        DtDeficiency oldDeficiency = defectTrackingComponent.getDfccy(update.getCntrctNo(), update.getDfccyNo());
        defectTrackingComponent.updateDeficiency(defectTrackingForm.updateDeficiency(update, oldDeficiency), defectTrackingForm.toDeficiencyActivityList(update.getActivity()), newFiles, update.getDelFileList());
        return Result.ok();
    }


    /**
     * 결함 삭제
     * @param commonReqVo
     * @param dfccyNoList
     * @return
     */
    @PostMapping("/delete")
    @Description(name = "결함 삭제", description = "결함 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteDeficiencyList(CommonReqVo commonReqVo, @RequestBody @Valid DfccyNoList dfccyNoList) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 결함 삭제");

        systemLogComponent.addUserLog(userLog);

        defectTrackingComponent.deleteDeficiencyList(dfccyNoList.getDfccyNoList());
        return Result.ok();
    }


    /**
     * 결함 확인 리스트 조회
     * @param commonReqVo
     * @param dfccyNo
     * @return
     */
    @GetMapping("/confirm-list")
    @Description(name = "결함 확인 리스트 조회", description = "결함 상세 조회 시, 해당 결함의 결함 확인 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getDeficiencyConfirmList(CommonReqVo commonReqVo, @RequestParam("dfccyNo") String dfccyNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 > 결함 상세 조회 시, 해당 결함의 결함 확인 리스트 조회");

        systemLogComponent.addUserLog(userLog);
        return Result.ok().put("dtConfirm", defectTrackingComponent.getDeficiencyConfirmList(dfccyNo));
    }


    /**
     * 결함추적 메뉴 첨부파일 다운로드(공통)
     * @param commonReqVo
     * @param fileNo
     * @param sno
     * @return
     */
    @GetMapping("/{fileNo}/{sno}/file-download")
    @Description(name = "결함추적 메뉴 첨부파일 다운로드", description = "결함추적 메뉴 첨부파일 다운로드", type = Description.TYPE.MEHTOD)
    public ResponseEntity<Resource> fileDownload(CommonReqVo commonReqVo,
                                                 @PathVariable("fileNo") Integer fileNo,
                                                 @PathVariable("sno") Short sno) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함추적 메뉴 첨부파일 다운로드");

        systemLogComponent.addUserLog(userLog);

        return defectTrackingComponent.fileDownload(fileNo, sno);
    }
}
