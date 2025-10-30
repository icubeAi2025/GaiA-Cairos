package kr.co.ideait.platform.gaiacairos.comp.construction;

import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.common.CommonUtilComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.ChiefInspectionReportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.DailyreportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.InspectionreportService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DocumentManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.config.security.TokenService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.ChiefInspectionreportMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.chiefinspectionreport.ChiefInspectionreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.util.UtilForm;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.ICubeClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.UbiReportClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class ChiefInspectionReportComponent extends AbstractComponent {

    @Autowired
    TokenService tokenService;

    @Autowired
    ChiefInspectionReportService chiefInspectionReportService;

    @Autowired
    ChiefInspectionreportForm chiefInspectionreportForm;

    @Autowired
    InspectionreportService inspectionReportService;

    @Autowired
    DailyreportService dailyreportService;

    @Autowired
    CommonUtilComponent commonutilComponent;

    @Autowired
    private ICubeClient iCubeClient;

    @Autowired
    DocumentServiceClient documentServiceClient;

    @Autowired
    DocumentManageService documentManageService;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    private FileService fileService;

    @Autowired
    InspectionReportComponent inspectionReportComponent;

    @Autowired
    UbiReportClient ubiReportClient;

    /**
     * 책임 감리일지 목록 데이터 조회
     */
    public List<ChiefInspectionreportMybatisParam.ChiefInspectionreportOutput> getReportList(ChiefInspectionreportForm.ChiefInspectionReport chiefInspectionReport) {
        ChiefInspectionreportMybatisParam.ChiefInspectionreportInput input = chiefInspectionreportForm.toChiefInspectionreportInput(chiefInspectionReport);
        input.setCode(CommonCodeConstants.APPSTATUS_CODE_GROUP_CODE);
        return chiefInspectionReportService.getReportList(input);
    }

    /**
     * 책임 감리일지 날짜 데이터 조회
     */
    public List<String> getReportYears(String cntrctNo) {
        return chiefInspectionReportService.getReportYears(cntrctNo);
    }

    /**
     * 책임감리일지 데이터 조회
     */
    public Map<String, Object> getDailyReport(String cntrctNo, String dailyReportDate) {

        Map<String, Object> result = new HashMap<>();
        result.put("dailyReport", chiefInspectionReportService.getDailyReport(cntrctNo,dailyReportDate));
        result.put("reportId", chiefInspectionReportService.getReportId(cntrctNo,dailyReportDate));

        return result;
    }

    /**
     * 책임 감리일지 상세조회 데이터 조회
     */
    public Map<String, Object> getReport(String cntrctNo, Long reportId) {
        MybatisInput input = new MybatisInput().add("cntrctNo", cntrctNo).add("reportId", reportId);

        Map<String, Object> result = new HashMap<>();

        // 책임감리일지 상세정보
        ChiefInspectionreportMybatisParam.ChiefInspectionreportOutput output = chiefInspectionReportService.getReport(input);

        // 작업일지정보
        CwDailyReport dailyReportByDailyReport = chiefInspectionReportService.getDailyReport(cntrctNo, output.getDailyReportDate());
        // 주요 작업 현황
        if(dailyReportByDailyReport != null) {
                output.setTodayPlanBohalRate(safeBigDecimal(dailyReportByDailyReport.getTodayPlanBohalRate()));
                output.setTodayArsltBohalRate(safeBigDecimal(dailyReportByDailyReport.getTodayArsltBohalRate()));
                output.setTodayProcess(safeBigDecimal(dailyReportByDailyReport.getTodayProcess()));
                output.setAcmltPlanBohalRate(safeBigDecimal(dailyReportByDailyReport.getAcmltPlanBohalRate()));
                output.setAcmltArsltBohalRate(safeBigDecimal(dailyReportByDailyReport.getAcmltArsltBohalRate()));
                output.setAcmltProcess(safeBigDecimal(dailyReportByDailyReport.getAcmltProcess()));

            Long dailyReportIdByDailyReport = dailyReportByDailyReport.getDailyReportId();
            List<Map<String, Object>> activityList = dailyreportService.getActivity(
                    cntrctNo, dailyReportIdByDailyReport, output.getDailyReportDate(), "TD"
            );

            for (Map<String, Object> activity : activityList) {
                Integer dailyActivityId = Integer.parseInt((String) activity.get("daily_activity_id"));

                // 책임감리일지 항목 가져오기
                CwCfInspectionReportActivity cfActivity =
                        chiefInspectionReportService.getUpdateReportActivity(cntrctNo, reportId, dailyActivityId);

                if (cfActivity != null) {
                    activity.put("specialNote", cfActivity.getSpecialNote());
                    activity.put("taskContent", cfActivity.getTaskContent());
                }
            }

            result.put("activity", activityList);
        }

        result.put("report", output);

        // 감리일지 목록
        input.add("dailyReportDate", output.getDailyReportDate());
        input.put("workCdCode", CommonCodeConstants.WTYPE_CODE_GROUP_CODE);
        input.put("apprvlStatsCode", CommonCodeConstants.APPSTATUS_CODE_GROUP_CODE);
        result.put("inspection", chiefInspectionReportService.getReportListByDate(input));

        //문서 목록
        result.put("doc", chiefInspectionReportService.getReportDocList(cntrctNo,reportId));

        return result;
    }

    private BigDecimal safeBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
    }



    /**
     * 책임 감리일지 추가
     */
    @Transactional
    public Long addReport(ChiefInspectionreportForm.ChiefInspectionReport input) {

        CwCfInspectionReport cfInspection = chiefInspectionreportForm.toCwCfInspectionReport(input);
        Long dailyReportId = chiefInspectionReportService.generateDailyReportId();
        cfInspection.setDltYn("N");
        cfInspection.setChiefMgr(input.getChiefMgr());
        cfInspection.setDailyReportId(dailyReportId);
        cfInspection.setApprvlStats("E");

        //개요
        chiefInspectionReportService.saveReport(cfInspection);
        //주요작업상황(작업일지의 dailyReportId를 조회해서 Activity를 추가)

        Long dailyReportIdByDailyReport = chiefInspectionReportService.getDailyReportIdByDailyReport(input.getCntrctNo(), input.getDailyReportDate());
        if(dailyReportIdByDailyReport != null) {
            List<Map<String, Object>> activityListByDailyReport = dailyreportService.getActivity(input.getCntrctNo(), dailyReportIdByDailyReport, input.getDailyReportDate(), "TD");
            chiefInspectionReportService.createActivity(activityListByDailyReport, dailyReportId);
        }
        return dailyReportId;
    }

    /**
     * 책임 감리일지 수정
     */
    @Transactional
    public void updateReport(ChiefInspectionreportForm.ChiefInspectionReport input) {
        String cntrctNo = input.getCntrctNo();
        Long dailyReportId = input.getDailyReportId();

        // 책임감리일지 수정
        input.setApprvlStats("E");
        CwCfInspectionReport cfInspection = chiefInspectionReportService.getUpdateReport(cntrctNo,dailyReportId);
        chiefInspectionreportForm.updateReport(input,cfInspection);
        chiefInspectionReportService.saveReport(cfInspection);

        // Activity 물리 삭제
        List <CwCfInspectionReportActivity> delCfActivityList = chiefInspectionReportService.getReportActivityList(cntrctNo,dailyReportId);
        chiefInspectionReportService.deleteReportActivityList(delCfActivityList);

        // Activity 추가
        input.getActivityList().forEach(activity -> {
            CwCfInspectionReportActivity cfActivity = chiefInspectionreportForm.toCwCfInspectionReportActivity(activity);
            cfActivity.setDltYn("N");
            chiefInspectionReportService.saveReportActivity(cfActivity);
        });

        // 감리일지 수정
        input.getInspectList().forEach(inspect -> {
            CwInspectionReport inspection = inspectionReportService.getInspectionData(inspect.getCntrctNo(),inspect.getDailyReportId());
            chiefInspectionreportForm.updateInspectReport(inspect, inspection);
            inspectionReportService.createReport(inspection);
        });

        // doc 삭제
        input.getDeletedDocList().forEach(deletedDoc -> {
            CwCfInspectionReportDoc delCfDoc =  chiefInspectionReportService.getReportDoc(cntrctNo,dailyReportId,deletedDoc);
            chiefInspectionReportService.deleteReportDoc(delCfDoc);
        });

        // doc 추가/수정
        input.getDocList().forEach(doc -> {
            if(doc.getDocId() != null) {
                CwCfInspectionReportDoc cfdoc =  chiefInspectionReportService.getReportDoc(cntrctNo,dailyReportId,doc.getDocId());
                chiefInspectionreportForm.updateDoctReport(doc, cfdoc);
                chiefInspectionReportService.saveReportDoc(cfdoc);
            }else{
                CwCfInspectionReportDoc cfdoc = chiefInspectionreportForm.toCwCfInspectionReportDoc(doc);
                cfdoc.setDocId(chiefInspectionReportService.generateDocId(cntrctNo,dailyReportId));
                cfdoc.setDltYn("N");
                chiefInspectionReportService.saveReportDoc(cfdoc);
            }
        });
    }

    /**
     * 책임 감리일지 삭제
     */
    @Transactional
    public void deleteReport(ChiefInspectionreportForm.DailyReportList input) {

        for (int i = 0; i < input.getReportList().size(); i++) {
            CwCfInspectionReport delete = chiefInspectionReportService.getUpdateReport(
                    input.getReportList().get(i).getCntrctNo(),
                    input.getReportList().get(i).getDailyReportId());

            if (delete.getDocId() == null || delete.getDocId().isEmpty()) {
                MybatisInput mybatisInput = new MybatisInput();
                mybatisInput.add("cntrctNo", delete.getCntrctNo()).add("dailyReportDate", delete.getDailyReportDate());
                inspectionReportService.deleteCommentResult(mybatisInput);

                chiefInspectionReportService.deleteReport(delete);
            }
        }
    }

    /**
     * 복사전 검증
     * @param map
     * @return
     */
    public Boolean checkChiefInspectionReportExists(Map map) {
        log.info("checkChiefInspectionReportExists: 책임 감리일지 게시물 존재 여부 확인");
        return chiefInspectionReportService.checkChiefInspectionReportExists(map);
    }

    /**
     * 책임 감리일지 복사
     * @param cntrctNo 계약번호
     * @param dailyReportId 원본 일지ID
     * @param copyDate 복사할 날짜
     * @return 새로 생성된 일지ID
     */
    @Transactional
    public Long copyReport(CommonReqVo commonReqVo, String cntrctNo, Long dailyReportId, String copyDate, String chiefMgr) throws IOException {
        log.info("copyReport: 책임 감리일지 복사. cntrctNo = {}, dailyReportId = {}, copyDate = {}", cntrctNo, dailyReportId, copyDate);

        // 1. 원본 데이터 조회
        CwCfInspectionReport originalReport = chiefInspectionReportService.getUpdateReport(cntrctNo,dailyReportId);

        if (originalReport == null) {
            throw new GaiaBizException(ErrorType.NO_DATA, "원본 책임감리일지를 찾을 수 없습니다.");
        }

        // 2. 새로운 일지ID 생성
        Long newDailyReportId = chiefInspectionReportService.generateDailyReportId();

        // 3. 새로운 책임감리일지 생성
        CwCfInspectionReport newReport = new CwCfInspectionReport();
        // 복사할 필드 세팅
        newReport.setCntrctNo(cntrctNo);
        newReport.setDailyReportId(newDailyReportId);
        newReport.setDailyReportDate(copyDate);
        newReport.setReportNo(originalReport.getReportNo());
        newReport.setTitle(originalReport.getTitle());
        newReport.setChiefMgr(originalReport.getChiefMgr());

        // 기상청 데이터 조회
        UtilForm.KmaWeather kmaWeather = new UtilForm.KmaWeather();
        kmaWeather.setPjtNo(commonReqVo.getPjtNo());
        kmaWeather.setTm(copyDate.replaceAll("-", "")); // yyyyMMdd

        Map<String, Object> kma = commonutilComponent.getKmaWeather(kmaWeather);

        BigDecimal rnDay = kma.get("rn_day") != null ? (BigDecimal) kma.get("rn_day") : BigDecimal.ZERO;
        newReport.setPrcptRate(rnDay.longValue());
        newReport.setPrcptRate(rnDay.longValue());

        newReport.setAmWthr((String) kma.get("am_wf"));
        newReport.setPmWthr((String) kma.get("pm_wf"));
        newReport.setDlowstTmprtVal((String) kma.get("ta_min"));
        newReport.setDtopTmprtVal((String) kma.get("ta_max"));

        newReport.setWorkCd(originalReport.getWorkCd());
        newReport.setRmrkCntnts(originalReport.getRmrkCntnts());
        newReport.setMajorMatter(originalReport.getMajorMatter());
        newReport.setSignificantNote(originalReport.getSignificantNote());
        newReport.setCommentResult(originalReport.getCommentResult());
        newReport.setDltYn(originalReport.getDltYn());
        newReport.setChiefMgr(chiefMgr);

        newReport.setCfTimeRange(originalReport.getCfTimeRange());
        newReport.setCfMajorMatter(originalReport.getCfMajorMatter());
        newReport.setCfRmrkCntnts(originalReport.getCfRmrkCntnts());

        // 4. 저장
        chiefInspectionReportService.saveReport(newReport);

        // 5. 주요작업상황 생성
        //주요작업상황(작업일지의 dailyReportId를 조회해서 Activity를 추가)
        Long dailyReportIdByDailyReport = chiefInspectionReportService.getDailyReportIdByDailyReport(cntrctNo, copyDate);

        if(dailyReportIdByDailyReport != null) {
            List<Map<String,Object>> activityListByDailyReport = dailyreportService.getActivity(cntrctNo, dailyReportIdByDailyReport, copyDate,"TD");
            chiefInspectionReportService.createActivity(activityListByDailyReport, newDailyReportId);
        }

        log.info("copyReport: 책임 감리일지 복사 완료. newDailyReportId = {}", newDailyReportId);
        return newDailyReportId;
    }

    /**
     * 책임 감리일지 pdf
     */
    @Transactional
    public void makeReportPdf(ChiefInspectionreportForm.DailyReportList input,String cfRgstrId){
        String imgDir = input.getImgDir();
        String baseUrl = input.getBaseUrl();

        input.getReportList().forEach((report) -> {
            String[] reportIds = new String[]{"/chief-inspection-report/chief_inspection_report.jrf"};
            String cntrctNo = report.getCntrctNo();
            String dailyReportId = String.valueOf(report.getDailyReportId());
            String dailyReportDate = report.getDailyReportDate();

            Map<String, String> params = new HashMap<>();
            params.put("cntrctNo", cntrctNo);
            params.put("dailyReportId", dailyReportId);
            params.put("dailyReportDate", dailyReportDate);
            params.put("imgDir", imgDir);
            params.put("baseUrl", baseUrl);

            Map<String, String> callbackInfo = new HashMap<>();
            callbackInfo.put("reqKey","req_cd");
            callbackInfo.put("reqValue",String.valueOf(report.getDailyReportId()));
            callbackInfo.put("pdfName",  "pdf_doc");

            String apiCairosUrl = baseUrl;
            if("local".equals(activeProfile)) {
                apiCairosUrl= "http://jhkim.idea-platform.net:8091";
            }
            else{
                if("GAIA".equals(platform.toUpperCase())){
                    apiCairosUrl = apiGaiaDomain;
                }
                else if("PGAIA".equals(platform.toUpperCase())){
                    apiCairosUrl = apiPGaiaDomain;
                }
                else if("CAIROS".equals(platform.toUpperCase())){
                    apiCairosUrl = apiCairosDomain;
                }
            }
            callbackInfo.put("callbackUrl", String.format("%s/interface/chief/callbackResult", apiCairosUrl));

            ubiReportClient.export(reportIds,params,callbackInfo);

            List<CwInspectionReport> cwInspectionReportList = inspectionReportService.getReportList(cntrctNo,dailyReportDate);

            for (CwInspectionReport cwInspectionReport : cwInspectionReportList) {
                inspectionReportService.makeInspectionReportDoc(cntrctNo, String.valueOf(cwInspectionReport.getDailyReportId()), imgDir, baseUrl);

                cwInspectionReport.setApprvlId(cfRgstrId); // 책임감리 작성자 ID
                cwInspectionReport.setApprvlDt(LocalDateTime.now()); // 오늘 날짜/시간
                inspectionReportService.createReport(cwInspectionReport);
            }
        });
    }

    // 완성된 책임감리일지 문서 DISK 저장 및 DB 업데이트
    public Map<String, String> chiefReportPdfResult(List<MultipartFile> pdfFile, Long dailyReportId, String accessToken){

        //토큰으로 사용자정보 만들기
        UserAuth userAuth = tokenService.parse(accessToken);

        // 기본 정보 조회
        CwCfInspectionReport cwCfInspectionReport = chiefInspectionReportService.getCfReportByPdf(dailyReportId);

        String cntrctNo = cwCfInspectionReport.getCntrctNo();
        String pjtNo = cntrctNo.contains(".")
                ? cntrctNo.substring(0, cntrctNo.indexOf('.'))
                : cntrctNo;

        // 통합문서 관리 PdF 속성 데이터 저장
        List<DocumentForm.PropertyData> propertyData;

        // 속성 코드 조회
        SmComCode smComCode = commonCodeService.getCommonCodeByGrpCdAndCmnCd(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE, "5");
        final String navId = String.format("nav_%s_%s_01", cntrctNo, smComCode.getAttrbtCd3());


        HashMap<String,Object> cbgnAndProperties = documentManageService.getCbgnAndProperties("APP03");
        List<CbgnPropertyDto> properties = (List<CbgnPropertyDto>) cbgnAndProperties.get("properties");

        List<DocumentForm.PropertyCreate> propertyCreateData = documentManageService.parseToPropertyCreate(properties,navId);

        // 속성 데이터 저장
        propertyData = savePdfPropertyDataToDoc(propertyCreateData, cwCfInspectionReport, userAuth.getUsrId());

        DocumentForm.DocCreateEx requestParams = new DocumentForm.DocCreateEx();
        requestParams.setNaviId(navId);
        requestParams.setNaviDiv("01");
        requestParams.setPjtNo(pjtNo);
        requestParams.setCntrctNo(cntrctNo);
        requestParams.setNaviPath("책임감리일지");
        requestParams.setNaviNm("책임감리일지");
        requestParams.setUpNaviNo(0);
        requestParams.setUpNaviId("");
        requestParams.setNaviLevel((short) 1);
        requestParams.setNaviType("FOLDR");
        requestParams.setNaviFolderType("5");
        requestParams.setNaviFolderKind(smComCode.getAttrbtCd3());
        requestParams.setProperties(propertyCreateData);
        requestParams.setPropertyData(propertyData);
        requestParams.setRgstrId(cwCfInspectionReport.getRgstrId());
        requestParams.setDocNm(
                StringUtils.isNotBlank(cwCfInspectionReport.getReportNo())
                ? cwCfInspectionReport.getReportNo() +".pdf"
                : cwCfInspectionReport.getTitle()+".pdf"
        );
        requestParams.setDocId(cwCfInspectionReport.getDocId());

        Map<String, String> newHeaders = Maps.newHashMap();
        newHeaders.put("x-auth", accessToken);

        // 통합문서관리 - 책임감리일지 pdf 생성
        log.info("updateDiskFileInfo: 책임감리일지 PDF 문서 및 속성 저장 param = {}", requestParams);
        List<DcStorageMain> createFileResultList = documentServiceClient.createFile(requestParams, pdfFile, newHeaders);

        DcStorageMain dcStorageMain = null;
        if(createFileResultList != null){
            dcStorageMain = createFileResultList.getFirst();
        } else {
            throw new GaiaBizException(ErrorType.NO_DATA, "문서가 존재하지 않습니다.");
        }

        // 책임감리일지 - docId 업데이트
        return updateDocId(cntrctNo, dailyReportId.toString(), dcStorageMain.getDocId());
    }

    // 통합문서관리의 속성 데이터 저장
    public List<DocumentForm.PropertyData> savePdfPropertyDataToDoc(List<DocumentForm.PropertyCreate> propertyCreateData, CwCfInspectionReport report,String usrId) {
        log.info("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 propertyCreateData = {}, cwDailyReport = {}", propertyCreateData, report);
        List<DocumentForm.PropertyData> insertList = new ArrayList<>();
        try {

            if (propertyCreateData == null) {
                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "조회된 속성 코드 없음");
            }

            for (DocumentForm.PropertyCreate property : propertyCreateData) {
                String attrbtCd = property.getAttrbtCd();

                if (attrbtCd != null) {
                    String attrbtCntnts = null;

                    switch (attrbtCd) {
                        case "reportNo": // 보고서번호
                            attrbtCntnts = report.getReportNo();
                            break;
                        case "reportDate": //보고일자
                            attrbtCntnts = report.getDailyReportDate();
                            break;
                        case "title": // 제목
                            attrbtCntnts = report.getTitle();
                            break;
                        case "apprvlStats": // 승인상태
                            attrbtCntnts = report.getApprvlStats();
                            break;
                        default:
                            attrbtCntnts = "";
                    }

                    if (attrbtCntnts != null && !attrbtCntnts.isBlank()) {
                        DocumentForm.PropertyData row = new DocumentForm.PropertyData();
                        row.setAttrbtCd(attrbtCd);
                        row.setAttrbtCntnts(attrbtCntnts);
                        row.setRgstrId(usrId);
                        row.setChgId(usrId);

                        insertList.add(row);
                    }
                }
            }

        } catch (RuntimeException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "통합문서관리 속성 데이터 저장 중 오류 발생.");
        }
        return insertList;
    }

    // report docId 업데이트
    public Map<String, String> updateDocId(String cntrctNo, String dailyReportId, String docId) {
        Map<String, String> result = new HashMap<>();

        MybatisInput input = new MybatisInput().add("cntrctNo", cntrctNo).add("dailyReportId", dailyReportId).add("docId", docId);
        result.put("result", chiefInspectionReportService.updateDocId(input));
        log.info("updateDocId: 책임감리일지 DocId 업데이트 = {}", result);

        return result;
    }

}
