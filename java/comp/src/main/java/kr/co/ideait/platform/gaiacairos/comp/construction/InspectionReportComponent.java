package kr.co.ideait.platform.gaiacairos.comp.construction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.file.CustomMultipartFile;
import kr.co.ideait.platform.gaiacairos.comp.common.CommonUtilComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.ChiefInspectionReportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.InspectionreportService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.config.wrapper.MultipartFileWrapper;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwInspectionReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.inspectionreport.InspectionreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.util.UtilForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InspectionReportComponent extends AbstractComponent {

    @Autowired
    InspectionreportForm inspectionReportForm;

    @Autowired
    InspectionreportService inspectionreportService;

    @Autowired
    ChiefInspectionReportService chiefInspectionReportService;

    @Autowired
    CommonUtilComponent commonutilComponent;

    // 감리일지 추가
    public void addReport(CommonReqVo commonReqVo, InspectionreportForm.CreateReport input) {
        CwInspectionReport inspection;
        log.info("addReport: 감리일지 추가: input = {}", input);

        Long dailyReportId = inspectionreportService.getDailyReportId(input.getCntrctNo()) + 1;

        log.info("addReport: 감리일지 신규 추가");
        inspection = inspectionReportForm.toEntity(input);
        inspection.setDailyReportId(dailyReportId);
        inspection.setApprvlStats("E");
        inspection.setDltYn("N");

        inspectionreportService.createReport(inspection);
    }

    // 감리일지 복사
    public CwInspectionReport copyReport(CommonReqVo commonReqVo, InspectionreportForm.CreateReport input)
            throws IOException {
        CwInspectionReport original = inspectionreportService.getCopyReportData(input.getCntrctNo(),
                input.getDailyReportId());

        Long newDailyReportId = inspectionreportService.getDailyReportId(input.getCntrctNo()) + 1;

        CwInspectionReport copied = new CwInspectionReport();

        // 기상청 데이터 조회
        UtilForm.KmaWeather kmaWeather = new UtilForm.KmaWeather();
        kmaWeather.setPjtNo(commonReqVo.getPjtNo());
        kmaWeather.setTm(input.getDailyReportDate().replaceAll("-", "")); // yyyyMMdd

        Map<String, Object> kma = commonutilComponent.getKmaWeather(kmaWeather);

        copied.setAmWthr((String) kma.get("am_wf"));
        copied.setPmWthr((String) kma.get("pm_wf"));
        copied.setDlowstTmprtVal((String) kma.get("ta_min"));
        copied.setDtopTmprtVal((String) kma.get("ta_max"));
        copied.setPrcptRate(new BigDecimal(kma.get("rn_day").toString()));
        copied.setSnowRate(new BigDecimal(kma.get("sd_max").toString()));

        // 기존 내용 복사
        copied.setCntrctNo(original.getCntrctNo());
        copied.setDailyReportDate(input.getDailyReportDate());
        copied.setReportNo(original.getReportNo());
        copied.setTitle(original.getTitle());
        copied.setWorkCd(original.getWorkCd());
        copied.setRmrkCntnts(original.getRmrkCntnts());
        copied.setSignificantNote(original.getSignificantNote());
        copied.setMajorMatter(original.getMajorMatter());
        copied.setCommentResult(original.getCommentResult());
        copied.setDailyReportId(newDailyReportId);
        copied.setApprvlStats("E");
        copied.setDltYn("N");

        inspectionreportService.createReport(copied);
        return copied;
    }

    // 감리일지 수정
    public void updateReport(CommonReqVo commonReqVo, InspectionreportForm.CreateReport input) {
        CwInspectionReport oldReport = inspectionreportService.getInspectionData(
                input.getCntrctNo(), input.getDailyReportId());

        // '작성완료'인 감리일지 수정 시 '작성중'으로 변경
        if ("A".equals(oldReport.getApprvlStats())) {
            oldReport.setApprvlStats("E");
        }

        inspectionReportForm.updateReport(input, oldReport);

        inspectionreportService.createReport(oldReport);
    }

    // 감리일지 작성완료
    public void apprvlReport(InspectionreportForm.CreateReport input) {
        CwInspectionReport oldReport = inspectionreportService.getInspectionData(
                input.getCntrctNo(), input.getDailyReportId());

        oldReport.setApprvlStats("A");
        inspectionreportService.createReport(oldReport);
    }

    // 감리일지 특정 날짜의 모든 감리일지 작성완료 체크(전부 완료시 true, 아닐시 false)
    public boolean checkComplete(String dailyReportDate) {
        return inspectionreportService.checkComplete(dailyReportDate);
    }

    /**
     * 감리일지 pdf변환
     */
    @Transactional
    public void makeInsReportPDF(String cntrctNo, String daliyReportDate, String rgstrId) {
        List<CwInspectionReport> cwInspectionReportList = inspectionreportService.getReportList(cntrctNo,
                daliyReportDate);

        boolean allSuccess = true; // pdf변환, 문서화 작업 성공 여부 플래그

        for (CwInspectionReport cwInspectionReport : cwInspectionReportList) {
            // 승인완료 이후 pdf 변환
            try {
                inspectionreportService.makeInspectionReportDoc("{cntrctNo}","{dailyReportId}","{imgDir}","{baseUrl}");
                // ====================TODO 테스트 로직=================================
                if (("local".equals(activeProfile)) || ("dev".equals(activeProfile))) {
                    List<MultipartFile> pdfFile = new ArrayList<>();
                    try {
                        File file = null;

                        if ("local".equals(activeProfile))
                            file = new File("D:/test/test.pdf");
                        if (("dev".equals(activeProfile)))
                            file = new File("/home/dev/storage/temp/cairos/감리일지 테스트.pdf");

                       try (InputStream inputStream = new FileInputStream(file)) {
                            MultipartFile wrappedFile = new MultipartFileWrapper(
                                    new CustomMultipartFile(inputStream.readAllBytes()),
                                    file.getName(),
                                    file.getName(),
                                    "multipart/form-data");
                            pdfFile.add(wrappedFile);
                        }
                    } catch (IOException ex) {
                        log.error("exception", ex);
                        allSuccess = false;
                    }

                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                            .currentRequestAttributes()).getRequest();
                    String accessToken = StringUtils.defaultString(request.getHeader("x-auth"),
                            cookieService.getCookie(request, cookieVO.getTokenCookieName()));

                    // 완성된 감리일지 문서 DISK 저장 및 DB 업데이트
                    Map<String, String> result = inspectionreportService.updateDiskFileInfo(pdfFile,
                            cwInspectionReport.getDailyReportId(), accessToken);

                    inspectionreportService.updateInspectionReportDocId(result.get("cntrctNo"),
                            result.get("dailyReportId"), result.get("docId"));
                }
            } catch (IOException e) {
                log.error("updateApprovalStatus: 문서화 작업 중 오류 발생 error = ", e.getMessage());
                allSuccess = false;
            }
            if (allSuccess) {
                cwInspectionReport.setApprvlId(rgstrId); // 책임감리 작성자 ID
                cwInspectionReport.setApprvlDt(LocalDateTime.now()); // 오늘 날짜/시간
                inspectionreportService.createReport(cwInspectionReport);
            }
        }

    }
}
