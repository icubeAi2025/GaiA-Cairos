package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.construction.MainmtrlReqfrmComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.mainmtrlReqfrm.MainmtrlReqfrmForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/construction/mainmtrlreqfrm")
public class MainmtrlReqfrmApiController extends AbstractController {

    @Autowired
    MainmtrlReqfrmComponent mainmtrlReqfrmComponent;

    /*
     * 주요자재 검수 요청 목록 조회
     */
    @GetMapping("/list")
    @Description(name = "주요자재 검수 요청 목록 조회", description = "프로젝트의 계약별 주요자재 검수 요청 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getMainmtrlReqfrmList(MainmtrlReqfrmForm.MainmtrlReqfrm mainmtrlReqfrmInput) {
        log.debug("mainmtrlReqfrmInput = {}", mainmtrlReqfrmInput);

        return GridResult
                .ok(mainmtrlReqfrmComponent.getMainmtrlReqfrmList(mainmtrlReqfrmInput));

    }

    /*
     * 주요자재 검수 상세 조회
     */
    @GetMapping("/get/{cntrctNo}/{reqfrmNo}")
    @Description(name = "주요자재 검수 요청 상세 조회", description = "주요자재 검수 요청 상세 조회", type = Description.TYPE.MEHTOD)
    public Result getMainmtrlReqfrm(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo,
            @PathVariable("reqfrmNo") String reqfrmNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요자재 검수 요청 상세 조회");
        systemLogComponent.addUserLog(userLog);

        Map<String, Object> resultMap = mainmtrlReqfrmComponent.getMainmtrlReqfrm(cntrctNo, reqfrmNo);

        return Result.ok().put("mainmtrlReqfrm", resultMap.get("mainmtrlReqfrm"))
                .put("photoList", resultMap.get("photoList")).put("attachments", resultMap.get("attachments"))
                .put("photoAttachments", resultMap.get("photoAttachments")).put("mainmtrls", resultMap.get("mainmtrls"));
    }

    /**
     * 주요자재 검수요청서 추가
     */
    @PostMapping("/create")
    @Description(name = "주요자재 검수요청서 추가", description = "주요자재 검수요청서, 자재, 파일, 사진 데이터 추가", type = Description.TYPE.MEHTOD)
    public Result createMainmtrlReqfrm(CommonReqVo commonReqVo, HttpServletRequest request,
            @RequestPart("mainmtrlreqfrm") MainmtrlReqfrmForm.MainmtrlReqfrm input,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요자재 검수요청서 데이터 추가");
        systemLogComponent.addUserLog(userLog);

        mainmtrlReqfrmComponent.createMainmtrlReqfrm(input, files, photos, commonReqVo.getUserId());
        return Result.ok();
    }

    /**
     * 주요자재 검수요청서 수정
     */
    @PostMapping("/update")
    @Description(name = "주요자재 검수요청서 수정", description = "주요자재 검수요청서, 자재, 파일, 사진 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateMainmtrlReqfrm(CommonReqVo commonReqVo, HttpServletRequest request,
            @RequestPart("mainmtrlreqfrm") MainmtrlReqfrmForm.MainmtrlReqfrm input,
            @RequestPart(value = "files", required = false) List<MultipartFile> newFiles,
            @RequestParam(value = "removedFiles[]", required = false) List<Integer> removedFileNos,
            @RequestParam(value = "removedSnos[]", required = false) List<Integer> removedSnos,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) throws IOException {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요자재 검수요청서 데이터 추가");
        systemLogComponent.addUserLog(userLog);

        mainmtrlReqfrmComponent.updateMainmtrlReqfrm(input, newFiles, removedFileNos, removedSnos, photos, commonReqVo.getUserId());
        return Result.ok();
    }

    /**
     * 주요자재 검수요청서 삭제
     */ 
    @PostMapping("/deleteMtrlReqfrm")
    @Description(name = "주요자재 검수요청서 삭제", description = "주요자재 검수요청서 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteMainmtrlReqfrm(CommonReqVo commonReqVo,
            @RequestBody MainmtrlReqfrmForm.MainmtrlReqfrmList input) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요자재 검수요청서 삭제");
        systemLogComponent.addUserLog(userLog);

        mainmtrlReqfrmComponent.deleteMainmtrlReqfrm(input, commonReqVo);
        return Result.ok();
    }

    /**
     * 주요자재 첨부파일 다운로드
     */
    @GetMapping("/{fileNo}/{sno}/file-download")
    @Description(name = "주요자재 첨부파일 다운로드", description = "주요자재 첨부파일 다운로드", type = Description.TYPE.MEHTOD)
    ResponseEntity<Resource> fileDownload(CommonReqVo commonReqVo,
            @PathVariable("fileNo") Integer fileNo,
            @PathVariable("sno") Integer sno) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요자재 첨부파일 다운로드");

        systemLogComponent.addUserLog(userLog);

        return mainmtrlReqfrmComponent.fileDownload(fileNo, sno);
    }

    /**
     * 주요자재 조회
     */
    @PostMapping("/getMainmtrlList")
    @Description(name = "주요자재 목록 조회", description = "주요자재 목록 조회", type = Description.TYPE.MEHTOD)
    public Result getMainmtrlList(CommonReqVo commonReqVo,@RequestBody MainmtrlReqfrmForm.MainmtrlReqfrm input) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요자재 목록 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("mtrlLists",mainmtrlReqfrmComponent.getMainmtrlList(input));
    }

    /**
     * 감리목록 조회
     */
    @GetMapping("/getSupervisionList")
    @Description(name = "감리 목록 조회", description = "감리 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getSupervisionList(CommonReqVo commonReqVo, @RequestParam String searchValue) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("감리 목록 조회");
        systemLogComponent.addUserLog(userLog);

        String pjtNo = commonReqVo.getPjtNo();
        String cntrctNo = commonReqVo.getCntrctNo();
        String pjtType = commonReqVo.getPlatform().toUpperCase();

        return GridResult.ok(mainmtrlReqfrmComponent.getSupervisionList(pjtNo, cntrctNo, pjtType, searchValue));
    }

    /**
     * 검수요청
     */
    @PostMapping("/inspectionRequest")
    @Description(name = "검수요청", description = "검수요청", type = Description.TYPE.MEHTOD)
    public Result inspectionRequest(CommonReqVo commonReqVo,
            @RequestBody List<Map<String, Object>> paramList, HttpServletRequest request) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("검수요청");
        systemLogComponent.addUserLog(userLog);

        mainmtrlReqfrmComponent.inspectionRequestList(paramList, request, commonReqVo);
        return Result.ok();
    }

    /**
     * 주요자재 검수요청서 검수결과 등록/수정
     */
    @PostMapping("/addMtrlReqfrmResult")
    @Description(name = "주요자재 검수결과 등록/수정", description = "주요자재 검수결과 등록/수정", type = Description.TYPE.MEHTOD)
    public Result addMtrlReqfrmResult(CommonReqVo commonReqVo, @RequestBody MainmtrlReqfrmForm.MainmtrlReqfrm input) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요자재 검수결과 등록/수정");
        systemLogComponent.addUserLog(userLog);

        mainmtrlReqfrmComponent.addMtrlReqfrmResult(input, commonReqVo.getUserId());
        return Result.ok();
    }

    /**
     * 주요자재 검수요청서 검수결과 일부불합격 목록
     */
    @GetMapping("/getPartialFailList")
    @Description(name = "주요자재 검수결과 일부불합격 목록", description = "주요자재 검수결과 일부불합격 목록", type = Description.TYPE.MEHTOD)
    public Result getPartialFailList(CommonReqVo commonReqVo, @RequestParam String cntrctNo, String reqfrmNo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요자재 검수결과 일부불합격 목록");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("partialFailList", mainmtrlReqfrmComponent.getAddMainmtrlList(cntrctNo,reqfrmNo));
    }

    /**
     * 결재요청
     */
    @PostMapping("/paymentRequest")
    @Description(name = "주요자재 결재요청", description = "주요자재 결재요청", type = Description.TYPE.MEHTOD)
    public Result paymentRequest(CommonReqVo commonReqVo,
            @RequestBody List<Map<String, String>> paramList) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요자재 결재요청");
        systemLogComponent.addUserLog(userLog);

        mainmtrlReqfrmComponent.paymentRequest(paramList, commonReqVo);
        return Result.ok();
    }

    /**
     * 결재취소
     */
    @PostMapping("/cancelPayment")
    @Description(name = "주요자재 결재취소", description = "주요자재 결재취소", type = Description.TYPE.MEHTOD)
    public Result cancelPayment(CommonReqVo commonReqVo, @RequestBody List<Map<String, String>> paramList) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요자재 결재취소");
        systemLogComponent.addUserLog(userLog);

        Result result = null;
        try {
            mainmtrlReqfrmComponent.cancelPayment(paramList, commonReqVo);
            result = Result.ok();
        } catch (GaiaBizException e) {
            result = Result.ok().put("resultMsg", e.getMessage());
        }
        return result;
    }
}
