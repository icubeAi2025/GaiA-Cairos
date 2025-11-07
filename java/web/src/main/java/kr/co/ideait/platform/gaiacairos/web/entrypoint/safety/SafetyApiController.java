package kr.co.ideait.platform.gaiacairos.web.entrypoint.safety;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.safety.SafetyComponent;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetymgmtService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspectionPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwStandardInspectionList;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.CheckForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.SafetymgmtMybatisParam.SafetyListOutPut;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.SafetymgmtMybatisParam.SafetyOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/safetymgmt/check")
public class SafetyApiController extends AbstractController {

    @Autowired
    SafetyComponent safetyComponent;

    @Autowired
    SafetymgmtService safetyService;

    @Autowired
    CheckForm checkForm;

    // ---------안전점검 목록 화면-----------

    /**
     * 안전점검 리스트 조회
     */
    @PostMapping("/get/safetyList")
    @Description(name = "안전점검 리스트 조회", description = "안전점검 데이터 전체 조회", type = Description.TYPE.MEHTOD)
    public Result getSafetylist(CommonReqVo commonReqVo, @RequestBody CheckForm.Safety param) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("safetyList",
                safetyService.getSafetyList(param.getCntrctNo(), param.getSelectedStatus(), param.getSearchValue(), param.getSelectedValue()));
    }

    /**
     * 승인요청
     */
    @PostMapping("/approval/safety")
    @Description(name = "안전점검 승인요청", description = "승인요청", type = Description.TYPE.MEHTOD)
    public Result approvalSafety(CommonReqVo commonReqVo, @RequestBody CheckForm.Safety input) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 승인요청");
        systemLogComponent.addUserLog(userLog);

        safetyComponent.requestApprovalSafety(input, commonReqVo);

        return Result.ok();
    }

    /**
     * 점검결과 작성 요청
     */
    @PostMapping("/report/safety")
    @Description(name = "안전점검 점결결과 작성 요청", description = "안전점검 점결결과 작성 요청", type = Description.TYPE.MEHTOD)
    public Result reportSafety(CommonReqVo commonReqVo, @RequestBody Map<String, Object> param,
            HttpServletRequest request) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 점결결과 작성 요청");
        systemLogComponent.addUserLog(userLog);

        // if ("G".equals(commonReqVo.getPjtDiv()) &&
        // PlatformType.from(commonReqVo.getPlatform()).equals(PlatformType.CAIROS)) {
        // }

        safetyComponent.requestReportSafety(param, request, commonReqVo);
        return Result.ok();
    }

    // ---------안전점검 추가/수정 화면-----------

    /**
     * 안전점검 조회
     */
    @GetMapping("/get/safety/{cntrctNo}/{inspectionNo}")
    @Description(name = "안전점검 조회", description = "안전점검 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getSafety(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo,
            @PathVariable("inspectionNo") String inspectionNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        SafetyOutput safety = safetyService.getSafetyWithQuery(cntrctNo, inspectionNo); // 안전점검 데이터
        List<SafetyListOutPut> safetyList = safetyService.getSafetyListByQuery(cntrctNo, inspectionNo); // 점검항목 데이터
        List<CwSafetyInspectionPhoto> photoList = safetyService.getPhotoList(cntrctNo, inspectionNo); // 사진 정보 데이터
        List<CwAttachments> photoAttachments = new ArrayList<>();
        if (photoList.size() > 0) { // 사진 파일 데이터
            photoAttachments = safetyService.getPhotoFileList(photoList.get(0).getAtchFileNo()); // 사진 파일
        }
        return Result.ok().put("safety", safety).put("safetyList", safetyList).put("photoList", photoList)
                .put("photoAttachments", photoAttachments);
    }

    /**
     * 대공종 조회
     */
    @PostMapping("/get/cnsttyLvl1")
    @Description(name = "대공종 조회", description = "안전점검 추가/수정 화면 대공종 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getCnsttyLvl1(CommonReqVo commonReqVo, @RequestBody CheckForm.SafetyList work) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 추가/수정 화면 대공종 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("cnsttyList", safetyService.getCnsttyLvl1(work.getCntrctNo(), work.getUpCnsttyCd()));
    }

    /**
     * 공종조회
     */
    @GetMapping("/get/cnsttyLvl2")
    @Description(name = "공종 조회", description = "안전점검 추가/수정 화면 공종 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getCnsttyLvl2(CommonReqVo commonReqVo, @RequestParam("cntrctNo") String cntrctNo,
            @RequestParam("upCnsttyCd") String upCnsttyCd) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 추가/수정 화면 공종 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("cnsttyList", safetyService.getCnsttyLvl2(cntrctNo, upCnsttyCd));
    }

    /**
     * 안전점검 항목
     */
    @PostMapping("/get/inspectionList")
    @Description(name = "안전점검 항목 조회", description = "안전점검 추가/수정 화면 안전점검 항목 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getInspectionList(CommonReqVo commonReqVo, @RequestBody CheckForm.CnsttyCds cnsttyCd) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 추가/수정 화면 안전점검 항목 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("inspectionList",
                safetyService.getInspectionList(cnsttyCd.getCntrctNo(), cnsttyCd.getCnsttyCd()));
    }

    /**
     * 안전점검 추가
     */
    @Transactional
    @PostMapping("/create/safety")
    @Description(name = "안전점검 추가", description = "안전점검, 안전점검 항목 데이터 추가", type = Description.TYPE.MEHTOD)
    public Result createSafety(CommonReqVo commonReqVo, HttpServletRequest request,
            @RequestBody CheckForm.Safety safety) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검, 안전점검 항목 데이터 추가");
        systemLogComponent.addUserLog(userLog);

        safetyComponent.createSafety(safety, commonReqVo.getUserId());

        return Result.ok();
    }

    /**
     * 안전점검 수정
     */
    @Transactional
    @PostMapping("/update/safety")
    @Description(name = "안전점검 수정", description = "안전점검, 안전점검 항목 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateSafety(CommonReqVo commonReqVo, @RequestBody CheckForm.Safety safety) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검, 안전점검 항목 데이터 수정");
        systemLogComponent.addUserLog(userLog);

        safetyComponent.updateSafety(safety, commonReqVo);

        return Result.ok();
    }

    /**
     * 안전점검 삭제
     */
    @PostMapping("/delete/safety")
    @Description(name = "안전점검 삭제", description = "안전점검 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteSafety(CommonReqVo commonReqVo, @RequestBody CheckForm.SafetyLists safetyList) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 데이터 삭제");
        systemLogComponent.addUserLog(userLog);

        for (int i = 0; i < safetyList.getSafetyList().size(); i++) {
            safetyComponent.deleteSafetyYn(safetyList.getSafetyList().get(i).getCntrctNo(),
                    safetyList.getSafetyList().get(i).getInspectionNo());
        }

        return Result.ok();
    }

    // ---------안전점검 결과 작성-----------

    /**
     * 안전점검 결과 추가
     */
    @PostMapping("/create/result")
    @Description(name = "안전점검 결과 추가", description = "점검결과 데이터 추가", type = Description.TYPE.MEHTOD)
    public Result addResult(CommonReqVo commonReqVo, HttpServletRequest request,
            @RequestPart("result") CheckForm.Safety safety,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 점검결과 데이터 추가");
        systemLogComponent.addUserLog(userLog);

        safetyComponent.addResult(safety, photos, commonReqVo.getUserId());

        return Result.ok();
    }

    /**
     * 안전점검 결과 수정
     */
    @Transactional
    @PostMapping("/update/result")
    @Description(name = "안전점검 결과 수정", description = "점검결과 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateResult(CommonReqVo commonReqVo, @RequestPart("result") CheckForm.Safety safety,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 점검결과 데이터 수정");
        systemLogComponent.addUserLog(userLog);

        safetyComponent.updateResult(safety, photos, commonReqVo.getUserId());

        return Result.ok();
    }

    // ---------안전점검 리스트관리 화면-----------

    /**
     * 트리 목록 조회(공통)
     */
    @GetMapping("/mgmtlist/treeList")
    @Description(name = "공종 목록 불러오기", description = "안전점검 리스트 관리 화면 공통인 경우 공종 목록 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getTreeList(CommonReqVo commonReqVo, @RequestParam("cntrctNo") String cntrctNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 리스트 관리 화면 공통인 경우 공종 목록 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        String check = "common";
        return Result.ok().put("treeList", safetyService.getTreeList(cntrctNo, check));
    }

    /**
     * 트리 목록 조회(계약)
     */
    @GetMapping("/mgmtlist/treeList/contract")
    @Description(name = "공종 목록 불러오기", description = "안전점검 리스트 관리 화면 계약인 경우 공종 목록 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getContractTreeList(CommonReqVo commonReqVo, @RequestParam("cntrctNo") String cntrctNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 리스트 관리 화면 계약인 경우 공종 목록 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        String check = "contract";
        return Result.ok().put("treeList", safetyService.getTreeList(cntrctNo, check));
    }

    /**
     * 안전점검 리스트 목록(그리드) 조회
     */
    @PostMapping("/mgmtlist/gridList")
    @Description(name = "안전점검 리스트 목록 조회", description = "안전점검 리스트 관리 화면 안전점검 리스트 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getGridList(CommonReqVo commonReqVo, @RequestBody CheckForm.SafetyList grid) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 리스트 관리 화면 안전점검 리스트 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("gridList", safetyService.getGridList(grid.getCntrctNo(), grid.getCnsttyCd(),
                grid.getSearchValue(), grid.getUseType()));

    }

    /**
     * 안전점검 리스트 조회
     */
    @PostMapping("/get/list")
    @Description(name = "안전점검 리스트 데이터 조회", description = "안전점검 리스트 관리 화면 안전점검 리스트 수정 시 해당 안전점검 리스트의 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getList(CommonReqVo commonReqVo, @RequestBody CheckForm.SafetyList list) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 리스트 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("list", safetyService.getList(list.getIspLstId()));
    }

    /*
     * 공종 코드 중복체크
     */
    @PostMapping("/checkCode")
    @Description(name = "안전점검 공종코드 중복체크", description = "공종코드 중복체크", type = Description.TYPE.MEHTOD)
    public Result checkCode(CommonReqVo commonReqVo, @RequestBody CheckForm.SafetyList checkCode) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 공종코드 중복체크");
        systemLogComponent.addUserLog(userLog);

        boolean duplicated = safetyService.checkCode(checkCode.getCnsttyCd());
        return Result.ok().put("result", duplicated);
    }

    /*
     * 공종 추가
     */
    @PostMapping("/create/work")
    @Description(name = "안전점검 공종 추가", description = "하위 공종 추가", type = Description.TYPE.MEHTOD)
    public Result createWork(CommonReqVo commonReqVo, @RequestBody CheckForm.SafetyList createWork) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 하위 공종 추가");
        systemLogComponent.addUserLog(userLog);

        CwStandardInspectionList cwWork = checkForm.toStandardEntity(createWork);
        cwWork.setIspLstId(UUID.randomUUID().toString());
        cwWork.setCnsttyYn("Y"); // 공종
        cwWork.setDltYn("N");
        cwWork.setCnsttyCd(createWork.getCnsttyCd());
        safetyService.createWork(cwWork);
        return Result.ok();
    }

    /*
     * 공종 수정
     */
    @PostMapping("/update/work")
    @Description(name = "안전점검 공종 수정", description = "하위 공종 수정", type = Description.TYPE.MEHTOD)
    public Result updateWork(CommonReqVo commonReqVo, @RequestBody CheckForm.SafetyList updateWork) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 하위 공종 수정");
        systemLogComponent.addUserLog(userLog);

        CwStandardInspectionList oldWork = safetyService.getWorkByCnsttyCd(updateWork.getCntrctNo(),
                updateWork.getCnsttyCd());
        oldWork.setCnsttyNm(updateWork.getCnsttyNm());
        safetyService.createWork(oldWork);
        return Result.ok();
    }

    /*
     * 공종 삭제
     */
    @PostMapping("/delete/work")
    @Description(name = "안전점검 공종 삭제", description = "하위 공종 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteWork(CommonReqVo commonReqVo, @RequestBody CheckForm.SafetyList deleteWork) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 하위 공종 삭제");
        systemLogComponent.addUserLog(userLog);

        safetyService.deleteWork(deleteWork.getCntrctNo(), deleteWork.getIspLstId());
        return Result.ok();
    }

    /**
     * 안전점검 리스트 추가
     */
    @PostMapping("/create/list")
    @Description(name = "안전점검 리스트 추가", description = "안전점검 관리 화면 안전점검 리스트 데이터 추가", type = Description.TYPE.MEHTOD)
    public Result createList(CommonReqVo commonReqVo, @RequestBody CheckForm.SafetyList createList) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 관리 화면 안전점검 리스트 데이터 추가");
        systemLogComponent.addUserLog(userLog);

        CwStandardInspectionList cwList = checkForm.toStandardEntity(createList);
        cwList.setIspLstId(UUID.randomUUID().toString());
        cwList.setCnsttyYn("N");
        cwList.setDltYn("N");
        safetyService.createStandardList(cwList);
        return Result.ok();
    }

    /**
     * 안전점검 리스트 수정
     */
    @PostMapping("/update/list")
    @Description(name = "안전점검 리스트 수정", description = "안전점검 관리 화면 안전점검 리스트 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateList(CommonReqVo commonReqVo, @RequestBody CheckForm.SafetyList updateList) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 관리 화면 안전점검 리스트 데이터 수정");
        systemLogComponent.addUserLog(userLog);

        CwStandardInspectionList oldList = safetyService.getList(updateList.getIspLstId());
        oldList.setIspLstDscrpt(updateList.getIspLstDscrpt());
        safetyService.createStandardList(oldList);
        return Result.ok();
    }

    /**
     * 안전점검 리스트 삭제
     */
    @PostMapping("/delete/list/{cntrctNo}")
    @Description(name = "안전점검 리스트 삭제", description = "안전점검 관리 화면 안전점검 리스트 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteList(CommonReqVo commonReqVo, @PathVariable(value = "cntrctNo") String cntrctNo,
            @RequestBody CheckForm.IspLstIds ispLstIds) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 관리 화면 안전점검 리스트 데이터 삭제");
        systemLogComponent.addUserLog(userLog);

        for (int i = 0; i < ispLstIds.getIspLstIds().size(); i++) {
            CwStandardInspectionList delete = safetyService.getList(ispLstIds.getIspLstIds().get(i));
            safetyService.deleteList(delete);
        }
        return Result.ok();
    }

    // ---------기타-----------

    /**
     * 안전점검 리스트 ID
     */
    @GetMapping("/get/maxIspLstId")
    @Description(name = "안전점검 점검리스트ID 부여", description = "안전점검 관리 화면 안점점검 리스트 추가 시 해당 점검 리스트의 점검리스트ID 부여", type = Description.TYPE.MEHTOD)
    public Result getMaxIspLstId(CommonReqVo commonReqVo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("안전점검 관리 화면 안점점검 리스트 추가 시 해당 점검 리스트의 점검리스트ID 부여");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("maxIspLstId", UUID.randomUUID().toString());
    }
}
