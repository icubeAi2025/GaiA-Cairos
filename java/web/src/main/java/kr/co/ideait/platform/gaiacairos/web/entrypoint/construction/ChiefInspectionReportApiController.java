package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.construction.ChiefInspectionReportComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.InspectionReportComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.ChiefInspectionReportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.InspectionreportService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwInspectionReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.chiefinspectionreport.ChiefInspectionreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.inspectionreport.InspectionreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/construction/chiefinspectionreport")
public class ChiefInspectionReportApiController extends AbstractController {

        @Autowired
        ChiefInspectionReportComponent chiefInspectionReportComponent;

        /**
         * 책임 감리일지 목록 데이터 조회
         */
        @PostMapping("/list")
        @Description(name = "책임책임 감리일지 목록 조회", description = "책임감리일지 목록 조회", type = Description.TYPE.MEHTOD)
        public Result getReportList(CommonReqVo commonReqVo, @RequestBody ChiefInspectionreportForm.ChiefInspectionReport input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("책임감리일지 목록 조회");
                systemLogComponent.addUserLog(userLog);

                return Result.ok().put("reportList", chiefInspectionReportComponent.getReportList(input));
        }

        /**
         * 책임 감리일지 년도 데이터 조회
         */
        @PostMapping("/get/year")
        @Description(name = "책임감리일지 년도 데이터 조회", description = "책임감리일지 목록 화면 년도 데이터 조회", type = Description.TYPE.MEHTOD)
        public Result getYear(CommonReqVo commonReqVo, @RequestBody InspectionreportForm.CreateReport input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("감리일지 년도 데이터 조회");
                systemLogComponent.addUserLog(userLog);

                return Result.ok().put("yearList", chiefInspectionReportComponent.getReportYears(input.getCntrctNo()));
        }

        /**
         * 작업 일보 데이터 조회
         */
        @PostMapping("/getDailyReport")
        @Description(name = "작업 일보 데이터 조회", description = "작업 일보 데이터 조회", type = Description.TYPE.MEHTOD)
        public Result getDailyReport(CommonReqVo commonReqVo, @RequestBody InspectionreportForm.CreateReport input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업 일보 데이터 조회");
                systemLogComponent.addUserLog(userLog);

                Map<String, Object> result = chiefInspectionReportComponent.getDailyReport(input.getCntrctNo(), input.getDailyReportDate());

                return Result.ok().put("dailyReport", result.get("dailyReport")).put("reportId", result.get("reportId"));
        }

        /**
         * 책임 감리일지 상세조회 데이터 조회
         */
        @GetMapping("/detail")
        @Description(name = "책임 감리일지 상세 조회", description = "책임감리일지 상세 조회", type = Description.TYPE.MEHTOD)
        public Result getReport(CommonReqVo commonReqVo, 
                               @RequestParam(value = "cntrctNo", required = false) String cntrctNo, 
                               @RequestParam(value = "dailyReportId", required = false) Long dailyReportId,
                               @RequestParam(value = "type", required = false) String type) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("책임감리일지 상세 조회");
                systemLogComponent.addUserLog(userLog);

                Map<String, Object> result = chiefInspectionReportComponent.getReport(cntrctNo, dailyReportId);
                
                // type 파라미터에 따른 응답 데이터 구분
                // - type=copy: 복사용 (report만 반환)
                // - type=null 또는 기타: 상세조회용 (전체 데이터 반환)
                Result response = null;
                if ("copy".equals(type)) {
                        // 복사용: report만 반환
                        response = Result.ok().put("reportData", result.get("report"));
                } else {
                        // 상세조회용 (type이 null이거나 다른 값): 전체 데이터 반환
                        response = Result.ok().put("reportList", result);
                }
                return response;
        }

        /**
         * 책임 감리일지 추가
         */
        @PostMapping("/create")
        @Description(name = "책임감리일지 추가", description = "책임감리일지 추가", type = Description.TYPE.MEHTOD)
        public Result addReport(CommonReqVo commonReqVo, @RequestBody ChiefInspectionreportForm.ChiefInspectionReport input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("책임감리일지 추가");
                systemLogComponent.addUserLog(userLog);

                Result result = null;
                try {
                        input.setChiefMgr(commonReqVo.getUserName());   // 책임감리 이름 추가
                        Long dailyReportId = chiefInspectionReportComponent.addReport(input);
                        result = Result.ok().put("success", true).put("dailyReportId", dailyReportId);
                } catch (GaiaBizException e) {
                        result = Result.ok().put("success", false).put("message", e.getMessage());
                }
                return result;
        }

        /**
         * 책임 감리일지 수정
         */
        @PostMapping("/update")
        @Description(name = "책임감리일지 수정", description = "책임감리일지 수정", type = Description.TYPE.MEHTOD)
        public Result updateReport(CommonReqVo commonReqVo, @RequestBody ChiefInspectionreportForm.ChiefInspectionReport input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("책임감리일지 수정");
                systemLogComponent.addUserLog(userLog);


                Result result = null;
                try {
                         chiefInspectionReportComponent.updateReport(input);
                        result = Result.ok().put("success", true);
                } catch (GaiaBizException e) {
                        result = Result.ok().put("success", false).put("message", e.getMessage());
                }

                return result;
        }

        /**
         * 감리일지 삭제
         */
        @PostMapping("/delete")
        @Description(name = "책임감리일지 삭제", description = "책임감리일지 삭제", type = Description.TYPE.MEHTOD)
        public Result deleteReport(CommonReqVo commonReqVo, @RequestBody ChiefInspectionreportForm.DailyReportList input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("책임감리일지 삭제");
                systemLogComponent.addUserLog(userLog);

                chiefInspectionReportComponent.deleteReport(input);
                return Result.ok();
        }

        /**
         * 책임 감리일지 복사
         * - 존재 여부 확인
         */
        @PostMapping("/report-exists")
        @Description(name = "책임 감리일지 게시물 존재 여부 확인", description = "책임 감리일지 게시물 존재 여부 확인", type = Description.TYPE.MEHTOD)
        public Result checkDailyReportExists(CommonReqVo commonReqVo,
                                             @RequestBody Map<String, String> paramMap) throws Exception {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("책임감리일지 복사전 검증 확인");
                systemLogComponent.addUserLog(userLog);
                log.info("checkDailyReportExists: 책임감리일지 게시물 존재 여부 확인. params = {}", paramMap);

                return Result.ok().put("reportExists", chiefInspectionReportComponent.checkChiefInspectionReportExists(paramMap));
        }

        /**
         * 책임 감리일지 복사 실행
         */
        @PostMapping("/copy")
        @Description(name = "책임 감리일지 복사", description = "책임 감리일지 복사", type = Description.TYPE.MEHTOD)
        public Result copyReport(CommonReqVo commonReqVo, @RequestBody Map<String, String> paramMap) throws Exception {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("책임감리일지 복사");
                systemLogComponent.addUserLog(userLog);
                log.info("copyReport: 책임감리일지 복사. params = {}", paramMap);


                Result result = null;
                try {
                        String cntrctNo = paramMap.get("cntrctNo");
                        Long dailyReportId = Long.parseLong(paramMap.get("dailyReportId"));
                        String copyDate = paramMap.get("dailyReportDate");
                        String chiefMgr = commonReqVo.getUserName();

                        // 복사 실행
                        Long newDailyReportId = chiefInspectionReportComponent.copyReport(commonReqVo, cntrctNo, dailyReportId, copyDate, chiefMgr);
                        
                        result = Result.ok().put("success", true).put("newDailyReportId", newDailyReportId);
                } catch (GaiaBizException e) {
                        result = Result.ok().put("success", false).put("message", e.getMessage());
                }
                return result;
        }

        /**
         * 책임 감리일지 pdf생성 api (임시)
         */
        @PostMapping("/makePdf")
        @Description(name = "책임 감리일지 pdf", description = "책임 감리일지 pdf", type = Description.TYPE.MEHTOD)
        public Result makeCfReportPdf(CommonReqVo commonReqVo, @RequestBody ChiefInspectionreportForm.DailyReportList input) throws IOException {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("책임감리일지 pdf");
                systemLogComponent.addUserLog(userLog);

                chiefInspectionReportComponent.makeReportPdf(input,commonReqVo.getUserId());

                return Result.ok();
        }
}
