package kr.co.ideait.platform.gaiacairos.web.entrypoint.safety;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.safety.SafetyDiaryComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report.SafetyDiaryForm.safetyDiaryParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report.SafetyDiaryRequest;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/safety/safety-diary")
public class SafetyDiaryApiController extends AbstractController {

    @Autowired
    SafetyDiaryComponent safetyDiaryComponent;

    @PostMapping("/list")
    @Description(name = "안전일지 목록 조회", description = "프로젝트의 계약별 안전일지 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getSafetyDiaryList(CommonReqVo commonReqVo, HttpServletRequest request, @RequestBody safetyDiaryParam param) {

        return GridResult.ok(safetyDiaryComponent.getSafetyDiaryList(commonReqVo, param));
    }

    /**
     * 안전일지 추가/수정 - 개요
     *  해당 날짜의 작업일보 기준 - 근로자 조회
     * @param commonReqVo
     * @param paramMap
     * @return
     * @throws IOException
     */
    @PostMapping("/daily-report-exists")
    @Description(name = "작업일지 게시물 확인 및 안전일지 조회", description = "안전일지 근로자 수 활용을 위해 작업일보 리소스 조회", type = Description.TYPE.MEHTOD)
    public Result checkDailyReportExists(CommonReqVo commonReqVo,
                                         @RequestBody Map<String, String> paramMap) throws IOException {

        log.info("checkDailyReportExists: . params = {}", paramMap);

        String dailyReportDate = paramMap.get("dailyReportDate");                             // YYYY-MM-DD
        String dailyReportDateYmd = dailyReportDate.replace("-", "");   // YYYYMMDD
        String cntrctNo = paramMap.get("cntrctNo");
        Result result = Result.ok();

        // 1. 작업일지 게시물 존재 여부 확인
        result.put("data", safetyDiaryComponent.checkDailyReportExists(cntrctNo, dailyReportDate));
        // 2. 근로자 누계 데이터 조회
        result.put("prevCusum", safetyDiaryComponent.getPrevCusum(cntrctNo, dailyReportDate));
        // 3. 무재해 현황 데이터 조회
        result.put("zeroAccidentCampaign", safetyDiaryComponent.getZeroAccidentCampaignData(cntrctNo, dailyReportDate));
        // 4. 안전일지 존재여부

        result.put("isExistsSafetyDiary", safetyDiaryComponent.checkDuplicateSafeDiary(cntrctNo, dailyReportDateYmd));
        // 5. 해당일자 교육현황 조회
        result.put("educationStatus", safetyDiaryComponent.getEducationStatus(cntrctNo, dailyReportDateYmd));
        // 6. 해당일자 재해현황 조회
        result.put("disasterStatus", safetyDiaryComponent.getDisasterStatus(cntrctNo, dailyReportDateYmd));
        return result;

    }

    /**
     * 안전일지 복사전 validation
     * @param commonReqVo
     * @param paramMap
     * @return
     * @throws Exception
     */
    @PostMapping("/diary-exists")
    @Description(name = "안전일지 게시물 존재 여부 확인", description = "안전일지 게시물 존재 여부 확인", type = Description.TYPE.MEHTOD)
    public Result checkSafetyDiaryExists(CommonReqVo commonReqVo,
                                         @RequestBody Map<String, String> paramMap) throws Exception {
        log.info("checkSafetyDiaryExists: 안전일지 게시물 존재 여부 확인. params = {}", paramMap);

        String dailyReportDate = paramMap.get("dailyReportDate");
        String cntrctNo = paramMap.get("cntrctNo");

        return Result.ok().put("reportExists", safetyDiaryComponent.checkDuplicateSafeDiary(cntrctNo, dailyReportDate));
    }

    /**
     * 안전일지 복사
     * @param commonReqVo
     * @param paramMap
     * @return
     * @throws Exception
     */
    @PostMapping("/copy")
    @Description(name = "안전일지 복사", description = "안전일지 복사", type = Description.TYPE.MEHTOD)
    public Result copyDiary(CommonReqVo commonReqVo, @RequestBody Map<String, Object> paramMap) throws Exception {
        log.info("copyDiary: 안전일지 복사. params = {}", paramMap);

        String newSafeDiaryId = safetyDiaryComponent.copyDiary(commonReqVo, paramMap);

        return Result.ok()
                .put("success", true)
                .put("newSafeDiaryId", newSafeDiaryId);
    }

    /**
     * 안전일지 추가
     * @param commonReqVo
     * @param request
     * @param report
     * @return
     */
    @PostMapping("/create")
    @Description(name = "안전 일지 추가", description = "안전일지 추가", type = Description.TYPE.MEHTOD)
    public Result createSafetyDiary(
            CommonReqVo commonReqVo,
            HttpServletRequest request,
            @RequestBody SafetyDiaryRequest report) throws JsonProcessingException {

        log.info("createSafetyDiary: 안전일지 추가. params = {}", report.toString());
        safetyDiaryComponent.addSafetyDiary(report, commonReqVo);

        return Result.ok();

    }

    /**
     * 안전일지 상세조회
     * @param commonReqVo
     * @param request
     * @return
     */
    @GetMapping("/detail")
    @Description(name = "안전 일지 상세조회", description = "안전일지 상세조회", type = Description.TYPE.MEHTOD)
    public Result getSafetyDiary(CommonReqVo commonReqVo,
                                 @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
                                 @RequestParam(value = "diaryId", required = false) String diaryId,
                                 HttpServletRequest request) {

        Map<String, Object> safetyDiaryData = safetyDiaryComponent.getSafetyDiary(cntrctNo, diaryId);
        String dailyReportDate = safetyDiaryData.get("dailyReportDate").toString();                 // YYYY-MM-DD
        String dailyReportDateYmd = dailyReportDate.replace("-", "");          // YYYYMMDD
        Result result = Result.ok();

        // 1. 안전일지 상세조회
        result.put("data", safetyDiaryData);
        // 2. 근로자 누계 데이터 조회
        result.put("prevCusum", safetyDiaryComponent.getPrevCusum(cntrctNo, dailyReportDate));
        // 3. 무재해 현황 데이터 조회
        result.put("zeroAccidentCampaign", safetyDiaryComponent.getZeroAccidentCampaignData(cntrctNo, dailyReportDate));
        // 4. 해당일자 교육현황 조회
        result.put("educationStatus", safetyDiaryComponent.getEducationStatus(cntrctNo, dailyReportDateYmd));
        // 5. 해당일자 재해현황 조회
        result.put("disasterStatus", safetyDiaryComponent.getDisasterStatus(cntrctNo, dailyReportDateYmd));

        return result;
    }

    /**
     * 안전일지 수정
     * @param commonReqVo
     * @param request
     * @return
     */
    @PostMapping("/update")
    @Description(name = "안전 일지 수정", description = "안전일지 수정", type = Description.TYPE.MEHTOD)
    public Result updateSafetyDiary(
            CommonReqVo commonReqVo,
            HttpServletRequest request,
            @RequestBody SafetyDiaryRequest report) throws JsonProcessingException {

        log.info("updateSafetyDiary: 안전일지 수정. params = {}", report.toString());
        safetyDiaryComponent.modifySafetyDiary(report, commonReqVo);

        return Result.ok();
    }

    /**
     * 안전일지 삭제
     * @param commonReqVo
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @Description(name = "안전 일지 삭제", description = "안전일지 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteSafetyDiary(
            CommonReqVo commonReqVo,
            HttpServletRequest request,
            @RequestBody List<Map<String, Object>> diaryList) {


        safetyDiaryComponent.deleteSafetyDiary(commonReqVo, diaryList);
        return Result.ok();
    }

    /**
     * 안전일지 전자결재 승인요청
     * @return
     */
    @PostMapping("/request-approval")
    @Description(name = "안전일지 전자결재 승인요청", description = "안전일지 전자결재 승인요청 -> API 통신", type = Description.TYPE.MEHTOD)
    public Result requestApprovalSafetyDiary(
            CommonReqVo commonReqVo,
            HttpServletRequest request,
            @RequestBody List<Map<String, Object>> diaryList) {

        safetyDiaryComponent.requestApprovalSafetyDiary(commonReqVo, diaryList);
        return Result.ok();
    }

    /**
     * 안전일지 첨부파일 다운로드(공통)
     */
    @GetMapping("/{fileNo}/{sno}/file-download")
    @Description(name = "안전일지 첨부파일 다운로드", description = "안전일지 첨부파일 다운로드", type = Description.TYPE.MEHTOD)
    public ResponseEntity<Resource> fileDownload(CommonReqVo commonReqVo,
                                                 @PathVariable("fileNo") Integer fileNo,
                                                 @PathVariable("sno") Integer sno) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("안전일지 첨부파일 다운로드");


        return safetyDiaryComponent.fileDownload(fileNo, sno);
    }

    @PostMapping("/cancel-approval")
    @Description(name = "안전일지 승인 취소", description = "안전일지 승인 취소 및 연계 데이터 초기화", type = Description.TYPE.MEHTOD)
    public Result cancelSafetyDiaryApproval(CommonReqVo commonReqVo, @RequestBody List<Map<String, Object>> diaryList) {
        Result result;

        try {
            safetyDiaryComponent.cancelSafetyDiaryApproval(commonReqVo, diaryList);
            result = Result.ok();
        } catch (GaiaBizException e) {
            log.error("안전일지 승인 취소 중 비즈니스 오류 발생: {}", e.getMessage(), e);
            result = Result.nok(e.getErrorType(), e.getMessage());
        } catch (RuntimeException e) {
            log.error("안전일지 승인 취소 중 런타임 오류 발생: {}", e.getMessage(), e);
            result = Result.nok(ErrorType.INTERNAL_SERVER_ERROR, "안전일지 승인 취소 중 예상치 못한 오류 발생");
        }

        return result;
    }

}
