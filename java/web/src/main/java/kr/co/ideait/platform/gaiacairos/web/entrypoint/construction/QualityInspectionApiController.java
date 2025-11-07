package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.construction.QualityinspectionComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.QualityinspectionService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCntqltyCheckList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityInspection;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.qualityinspection.QualityinspectionForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.UbiReportClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/construction/qualityinspection")
public class QualityInspectionApiController extends AbstractController {

    @Autowired
    QualityinspectionComponent qualityinspectionComponent;

    @Autowired
    QualityinspectionForm qualityinspectionForm;

    @Autowired
    QualityinspectionService qualityService;

    @Autowired
    FileService fileService;

    @Autowired
    UbiReportClient ubiReportClient;

    // ---------품질 검측 관리 화면-----------

    /**
     * 품질 검측 리스트 조회
     */
    @PostMapping("/get/qualityList")
    @Description(name = "품질검측 목록 조회", description = "품질검측 데이터 전체 조회", type = Description.TYPE.MEHTOD)
    public Result getQualitylist(CommonReqVo commonReqVo, @RequestBody QualityinspectionForm.CreateCntCheckList param) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("품질검측 데이터 전체 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("qualitylist",
                qualityService.getQualityList(param.getCntrctNo(), param.getSearchValue(),
                        param.getSelectedWorkType()));
    }

    /**
     * 품질 검측 삭제
     */
    @PostMapping("/delete/qualityList")
    @Description(name = "품질검측 삭제", description = "품질검측 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteQuality(CommonReqVo commonReqVo,
            @RequestBody @Valid QualityinspectionForm.QualityList qualityList) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("품질검측 데이터 삭제");
        systemLogComponent.addUserLog(userLog);

        for (int i = 0; i < qualityList.getQualityList().size(); i++) {
            qualityinspectionComponent.deleteQuality(qualityList.getQualityList().get(i), commonReqVo.getUserId());
        }
        return Result.ok();
    }

    /**
     * 검측요청
     */
    @PostMapping("/request/inspection")
    @Description(name = "검측요청", description = "검측요청", type = Description.TYPE.MEHTOD)
    public Result inspectionRequest(CommonReqVo commonReqVo,
            @RequestBody List<Map<String, Object>> paramList, HttpServletRequest request) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("검측요청");
        systemLogComponent.addUserLog(userLog);

        qualityinspectionComponent.inspectionRequestList(commonReqVo, paramList, request);
        return Result.ok();
    }

    /**
     * 결재요청
     */
    @PostMapping("/request/payment")
    @Description(name = "결재요청", description = "결재요청", type = Description.TYPE.MEHTOD)
    public Result paymentRequest(CommonReqVo commonReqVo,
            @RequestBody @Valid QualityinspectionForm.QualityList qualityList) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결재요청");
        systemLogComponent.addUserLog(userLog);

        qualityinspectionComponent.paymentRequest(commonReqVo, qualityList.getQualityList(), qualityList.getCmnCdNmKrn(),
                commonReqVo.getApiYn(), commonReqVo.getPjtDiv());

        return Result.ok();
    }

    /**
     * 결재취소
     */
    @PostMapping("/cancel/payment")
    @Description(name = "결재취소", description = "결재취소", type = Description.TYPE.MEHTOD)
    public Result cancelPayment(CommonReqVo commonReqVo,
            @RequestBody @Valid QualityinspectionForm.QualityList qualityList) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결재취소");
        systemLogComponent.addUserLog(userLog);

        Map<String, Object> param = new HashMap<>();
        param.put("qualityList", qualityList.getQualityList());
        param.put("apiYn", commonReqVo.getApiYn());
        param.put("pjtDiv", commonReqVo.getPjtDiv());

        String usrId = commonReqVo.getUserId();

        Result result = null;
        try {
            qualityinspectionComponent.cancelPayment(param, usrId, commonReqVo);
            result = Result.ok();
        } catch (GaiaBizException e) {
            log.error("품질검측 승인 취소 중 오류 발생, 오류 메세지 = " + e.getMessage());
            result = Result.ok().put("resultMsg", e.getMessage());
        }

        return result;
    }

    /**
     * 감리목록 조회
     */
    @PostMapping("/get/supervisionList")
    @Description(name = "감리 목록 조회", description = "감리 목록 조회", type = Description.TYPE.MEHTOD)
    public Result getSupervisionList(CommonReqVo commonReqVo, @RequestBody Map<String, Object> param) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("감리 목록 조회");
        systemLogComponent.addUserLog(userLog);

        String cntrctNo = (String) param.get("cntrctNo");
        String searchValue = (String) param.get("searchValue");
        String pjtNo = commonReqVo.getPjtNo();
        String pjtType = commonReqVo.getPlatform().toUpperCase();
        return Result.ok().put("supervisionList",
                qualityService.getSupervisionList(cntrctNo, searchValue, pjtNo, pjtType));
    }

    // --------품질 검측 추가/수정 화면-------------

    /**
     * 품질 검측 조회
     */
    @GetMapping("/get/quality/{cntrctNo}/{qltyIspId}")
    @Description(name = "품질검측 조회", description = "품질검측 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getQuality(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo,
            @PathVariable("qltyIspId") String qltyIspId) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("품질검측 단일 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        Map<String, Object> resultMap = qualityinspectionComponent.getQuality(cntrctNo, qltyIspId);
        return Result.ok()
                .put("quality", resultMap.get("quality"))
                .put("activitys", resultMap.get("activitys"))
                .put("checkList", resultMap.get("checkList"))
                .put("attachments", resultMap.get("attachments"))
                .put("photoList", resultMap.get("photoList"))
                .put("photoAttachments", resultMap.get("photoAttachments"));
    }

    /**
     * 품질 검측 추가(검측, Activity, checkList, 파일, 사진)
     */
    @PostMapping("/create/quality")
    @Description(name = "품질검측 추가", description = "품질검측, Activity, Checklist, 파일, 사진 데이터 추가", type = Description.TYPE.MEHTOD)
    public Result createQuality(CommonReqVo commonReqVo, HttpServletRequest request,
            @RequestPart("quality") QualityinspectionForm.CreateQuality quality,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("품질검측 데이터 추가");
        systemLogComponent.addUserLog(userLog);

        qualityinspectionComponent.createQuality(commonReqVo, quality, commonReqVo.getUserId(), files, photos);
        return Result.ok();
    }

    /**
     * 품질 검측 수정
     */
    @PostMapping("/update/quality")
    @Description(name = "품질검측 수정", description = "품질검측, Activity, Checklist, 파일, 사진 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateQuality(CommonReqVo commonReqVo,
            @RequestPart("quality") QualityinspectionForm.UpdateQuality update,
            @RequestPart(value = "files", required = false) List<MultipartFile> newFiles,
            @RequestParam(value = "removedFiles[]", required = false) List<Integer> removedFileNos,
            @RequestParam(value = "removedSnos[]", required = false) List<Integer> removedSnos,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("품질검측 데이터 수정");
        systemLogComponent.addUserLog(userLog);

        CwQualityInspection updated = qualityinspectionComponent.updateQuality(commonReqVo, update,
                newFiles, removedFileNos,
                removedSnos, photos);
        return Result.ok().put("updateQuality", updated);
    }

    /**
     * Activity 선택 - 목록
     */
    @PostMapping("/activity/list")
    @Description(name = "Activity 목록", description = "품질검측 추가화면 Activity 전체 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getActivityList(CommonReqVo commonReqVo, @RequestBody Map<String, Object> param) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("Activity 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        String cntrctNo = (String) param.get("cntrctNo");
        String searchValue = (String) param.get("searchValue");
        return Result.ok().put("activityList", qualityService.getActivityList(cntrctNo, searchValue));
    }

    /**
     * 체크리스트 가져오기
     * 추가 화면 , 새창 화면
     */
    @PostMapping("/get/checklist")
    @Description(name = "체크리스트 목록", description = "품질검측 추가화면 체크리스트 전체 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getAllCheckList(CommonReqVo commonReqVo,
            @RequestBody @Valid QualityinspectionForm.CreateCntCheckList check) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("체크리스트 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("checkList",
                qualityService.getAllCheckList(check.getCntrctNo(), check.getCnsttyCd(), check.getUpCnsttyCd(),
                        check.getSearchValue()));

    }

    /**
     * 셀렉트 박스
     */
    @PostMapping("/selectbox")
    @Description(name = "셀렉트 박스", description = "품질검측 추가화면 공종 셀렉트 박스 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result makeSelectBox(CommonReqVo commonReqVo,
            @RequestBody @Valid QualityinspectionForm.CreateCntCheckList construct) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("공종 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("selectBox",
                qualityService.makeSelectBox(construct.getCntrctNo(), construct.getUpCnsttyCd()));
    }

    // -----------체크리스트 관리 화면-----------

    /**
     * 트리 목록 불러오기(공통)
     */
    @GetMapping("/checkList/treeList")
    @Description(name = "공종 목록 불러오기", description = "체크리스트 관리 화면 공통인 경우 공종 목록 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getTreeList(CommonReqVo commonReqVo, @RequestParam("cntrctNo") String cntrctNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("전체 공종 목록 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        String check = "common";
        return Result.ok().put("treeList", qualityService.getTreeList(cntrctNo, check));
    }

    /**
     * 트리 목록 불러오기(계약)
     */
    @GetMapping("/checkList/treeList/contract")
    @Description(name = "공종 목록 불러오기", description = "체크리스트 관리 화면 계약인 경우 공종 목록 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getContractTreeList(CommonReqVo commonReqVo, @RequestParam("cntrctNo") String cntrctNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("계약의 공종 목록 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        String check = "contract";
        return Result.ok().put("treeList", qualityService.getTreeList(cntrctNo, check));
    }

    /*
     * 공종 추가
     */
    @PostMapping("/create/work")
    @Description(name = "품질검측 공종 추가", description = "하위 공종 추가", type = Description.TYPE.MEHTOD)
    public Result createWork(CommonReqVo commonReqVo, @RequestBody QualityinspectionForm.CreateCntCheckList work) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("공종 추가");
        systemLogComponent.addUserLog(userLog);

        CwCntqltyCheckList cwWork = qualityinspectionForm.toEntity(work);
        cwWork.setChklstId(UUID.randomUUID().toString());
        cwWork.setCnsttyYn("Y"); // 공종
        cwWork.setDltYn("N");
        cwWork.setCnsttyCd(work.getCnsttyCd());
        qualityService.createWork(cwWork);
        return Result.ok();
    }

    /**
     * 공종 수정
     */
    @PostMapping("/update/work")
    @Description(name = "품질검측 공종 수정", description = "하위 공종 수정", type = Description.TYPE.MEHTOD)
    public Result updateWork(CommonReqVo commonReqVo, @RequestBody QualityinspectionForm.CreateCntCheckList update) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("공종 수정");
        systemLogComponent.addUserLog(userLog);

        CwCntqltyCheckList oldWork = qualityService.getCwCntqltyCheckList(update.getCntrctNo(), update.getChklstId()); // 수정할
                                                                                                                       // 공종

        List<CwCntqltyCheckList> checkLists = qualityService.getCheckLists(oldWork.getCntrctNo(),
                oldWork.getCnsttyCd()); // 수정할 체크리스트들

        // 공종 수정
        oldWork.setCnsttyNm(update.getCnsttyNm()); // 공종 명 수정

        // 체크 리스트 수정
        for (int i = 0; i < checkLists.size(); i++) {
            checkLists.get(i).setCnsttyNm(update.getCnsttyNm());
            qualityService.createWork(checkLists.get(i));
        }

        qualityService.createWork(oldWork);
        return Result.ok();
    }

    /**
     * 공종 삭제
     */
    @PostMapping("/delete/work")
    @Description(name = "품질검측 공종 삭제", description = "하위 공종 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteWork(CommonReqVo commonReqVo, @RequestBody QualityinspectionForm.CreateCntCheckList delete) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("공종 삭제");
        systemLogComponent.addUserLog(userLog);

        qualityService.deleteWork(delete.getCntrctNo(), delete.getChklstId());
        return Result.ok();
    }

    /*
     * 공종 코드 중복체크
     */
    @PostMapping("/checkCode")
    @Description(name = "품질검측 공종코드 중복체크", description = "공종코드 중복체크", type = Description.TYPE.MEHTOD)
    public Result checkCode(CommonReqVo commonReqVo, @RequestBody QualityinspectionForm.CreateCntCheckList checkCode) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("공종코드 중복체크");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("result",
                qualityService.checkCode(checkCode.getCnsttyCd()));
    }

    /**
     * 체크 리스트 조회
     */
    @PostMapping("/get/check")
    @Description(name = "체크리스트 조회", description = "체크리스트 수정 시 해당 체크리스트의 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getCheck(CommonReqVo commonReqVo, @RequestBody QualityinspectionForm.CreateCntCheckList get) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("체크리스트 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("check", qualityService.getCheck(get.getChklstId()));
    }

    /**
     * 체크 리스트 추가
     */
    @PostMapping("/create/check")
    @Description(name = "체크리스트 추가", description = "체크리스트 관리 화면 체크 리스트 데이터 추가", type = Description.TYPE.MEHTOD)
    public Result createCheck(CommonReqVo commonReqVo, @RequestBody QualityinspectionForm.CreateCntCheckList check) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("체크리스트 추가");
        systemLogComponent.addUserLog(userLog);

        CwCntqltyCheckList cwCheck = qualityinspectionForm.toEntity(check);
        cwCheck.setChklstId(UUID.randomUUID().toString());
        cwCheck.setCnsttyYn("N"); // 체크리스트
        cwCheck.setDltYn("N");
        qualityService.createCheck(cwCheck);
        return Result.ok();
    }

    /**
     * 체크 리스트 수정
     */
    @PostMapping("/update/check")
    @Description(name = "체크리스트 수정", description = "체크리스트 관리 화면 체크 리스트 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateCheck(CommonReqVo commonReqVo, @RequestBody QualityinspectionForm.CreateCntCheckList update) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("체크리스트 수정");
        systemLogComponent.addUserLog(userLog);

        CwCntqltyCheckList oldCheck = qualityService.getCheck(update.getChklstId());
        oldCheck.setChklstDscrpt(update.getChklstDscrpt());
        oldCheck.setChklstBssCd(update.getChklstBssCd());
        qualityService.createCheck(oldCheck);
        return Result.ok();
    }

    /**
     * 체크 리스트 삭제
     */
    @PostMapping("/delete/check/{cntrctNo}")
    @Description(name = "체크리스트 삭제", description = "체크리스트 관리 화면 체크 리스트 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteCheck(CommonReqVo commonReqVo, @PathVariable(value = "cntrctNo") String cntrctNo,
            @RequestBody QualityinspectionForm.ChklstIdList chklstIds) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("체크리스트 삭제");
        systemLogComponent.addUserLog(userLog);

        for (int i = 0; i < chklstIds.getChklstIds().size(); i++) {
            CwCntqltyCheckList delete = qualityService.getCheck(chklstIds.getChklstIds().get(i));
            qualityService.deleteCheck(delete);
        }
        return Result.ok();
    }

    /**
     * 체크 리스트 목록 가져오기(그리드)
     */
    @PostMapping("/checkList/gridList")
    @Description(name = "체크리스트 데이터 조회", description = "체크리스트 전체 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getGridList(CommonReqVo commonReqVo, @RequestBody QualityinspectionForm.CreateCntCheckList grid) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("체크리스트 전체 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("gridList",
                qualityService.getGridList(grid.getCntrctNo(), grid.getCnsttyCd(), grid.getSearchValue(),
                        grid.getUseType()));
    }

    // ---------검측 결과 등록 화면----------

    /**
     * 검측 결과 등록/수정
     */
    @PostMapping("/create/result")
    @Description(name = "검측결과 등록/수정", description = "검측결과 데이터 등록/수정", type = Description.TYPE.MEHTOD)
    public Result addResult(CommonReqVo commonReqVo, HttpServletRequest request,
            @RequestPart("quality") QualityinspectionForm.UpdateQuality result) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("검측결과 데이터 등록/수정");
        systemLogComponent.addUserLog(userLog);

        qualityinspectionComponent.addResult(result, commonReqVo.getUserId());
        return Result.ok();
    }

    /**
     * 조치 사항 등록/수정
     */
    @PostMapping("/create/Action")
    @Description(name = "조치사항 등록/수정", description = "조치사항 데이터 등록/수정", type = Description.TYPE.MEHTOD)
    public Result addAction(CommonReqVo commonReqVo,
            @RequestPart("quality") QualityinspectionForm.UpdateQuality action) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("조치사항 데이터 등록/수정");
        systemLogComponent.addUserLog(userLog);

        qualityinspectionComponent.addAction(action);
        return Result.ok();
    }

    /**
     * 품질검측 첨부파일 다운로드(공통)
     */
    @GetMapping("/{fileNo}/{sno}/file-download")
    @Description(name = "품질검측 첨부파일 다운로드", description = "품질검측 첨부파일 다운로드", type = Description.TYPE.MEHTOD)
    public ResponseEntity<Resource> fileDownload(CommonReqVo commonReqVo,
            @PathVariable("fileNo") Integer fileNo,
            @PathVariable("sno") Integer sno) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("품질검측 첨부파일 다운로드");

        systemLogComponent.addUserLog(userLog);

        return qualityService.fileDownload(fileNo, sno);
    }

    /**
     * PDF변환(임시)
     */
    @GetMapping("/pdf")
    public void makeQualityPdf(@RequestParam("qltyIspId") String qltyIspId,
            @RequestParam("cntrctNo") String cntrctNo) {
        CwQualityInspection inspection = qualityService.getQuality(qltyIspId);
        qualityService.makeQualityPdf(inspection);
    }
}
