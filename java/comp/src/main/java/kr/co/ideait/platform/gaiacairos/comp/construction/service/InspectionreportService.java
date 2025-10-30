package kr.co.ideait.platform.gaiacairos.comp.construction.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;

import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DocumentManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwInspectionReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwDailyReportRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwInspectionReportRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.InspectionreportMybatisParam.InspectionreportOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.ICubeClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.UbiReportClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InspectionreportService extends AbstractGaiaCairosService {

    @Autowired
    CwDailyReportRepository dailyReportRepository;

    @Autowired
    CwInspectionReportRepository inspectionReportRepository;

    @Autowired
    ApprovalRequestService approvalRequestService;

    @Autowired
    DocumentServiceClient documentServiceClient;

    @Autowired
    FileService fileService;

    @Autowired
    private ICubeClient iCubeClient;

    @Autowired
    private UbiReportClient ubiReportClient;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    DocumentManageService documentmanageService;

    @Value("${gaia.path.previewPath}")
    private String previewPath;
    /**
     * 감리일지 ID 만들기(해당 날짜, 해당 업무구분)
     */
    public Long getDailyReportId(String cntrctNo) {
        return inspectionReportRepository.findMaxDailyReportIdBy();
    }

    /**
     * 감리일지 목록 데이터 조회
     */
    public List<InspectionreportOutput> getReportList(String year, String month, String searchValue, String selectValue,
            String cntrctNo, String workType, String rgstrId) {

        if ("W".equals(workType) || "type".equals(workType)) { // '전체' 혹은 '업무 구분'일 시 null
            workType = null;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("year", year);
        params.put("month", month);
        params.put("searchValue", searchValue);
        params.put("selectValue", selectValue);
        params.put("cntrctNo", cntrctNo);
        params.put("workType", workType);
        params.put("rgstrId", rgstrId);
        params.put("workcode", CommonCodeConstants.WTYPE_CODE_GROUP_CODE);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.inspectionreport.getInspectionReportList",
                params);
    }

    /**
     * 감리일지 목록 데이터 조회
     */
    public List<String> getReportYears(String cntrctNo) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.inspectionreport.getYear", cntrctNo);
    }

    /**
     * 감리 일지 추가
     */
    public void createReport(CwInspectionReport report) {
        inspectionReportRepository.saveAndFlush(report);
    }

    /**
     * 감리 일지 지시사항 삭제
     */
    public void deleteCommentResult(MybatisInput input) {
        mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.inspectionreport.deleteCommentResult",
                input);

    }

    // /**
    // * 감리 일지 보고일자 & 업무 구분 중복 확인
    // */
    // public boolean chkReportDateAndWorkAndUserId(String cntrctNo, String
    // dailyReportDate, String workCd,
    // String userId) {
    // CwInspectionReport exist = inspectionReportRepository
    // .findByDailyReportDateAndCntrctNoAndWorkCdAndRgstrIdAndDltYn(dailyReportDate,
    // cntrctNo, workCd, userId, "N");
    // boolean result = exist == null ? true : false;
    // return result;
    // }

    /**
     * 작업 보고 ID 찾기
     */
    public Long getReportId(String cntrctNo, String dailyReportDate) {
        Long reportId = dailyReportRepository.findDailyReportDateByCntrctNo(cntrctNo, dailyReportDate);
        return reportId;
    }

    /**
     * 감리 일지 데이터 조회(쿼리)
     */
    public InspectionreportOutput getReportData(String cntrctNo, Long dailyReportId) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("dailyReportId", dailyReportId);
        params.put("code", CommonCodeConstants.WTYPE_CODE_GROUP_CODE);
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.inspectionreport.getInspectionReport",
                params);
    }

    /**
     * 감리 일지 데이터 조회(JPA)
     */
    public CwInspectionReport getInspectionData(String cntrctNo, Long dailyReportId) {
        return inspectionReportRepository.findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo, dailyReportId, "N");
    }

    /**
     * 감리일지 복사된 데이터 조회
     */
    public CwInspectionReport getCopyReportData(String cntrctNo, Long dailyReportId) {
        return inspectionReportRepository.findByCntrctNoAndDailyReportId(cntrctNo, dailyReportId).get(0);
    }

    /**
     * 감리일지 복사된 데이터 조회
     */
    public List<CwInspectionReport> getReportList(String cntrctNo, String dailyReportDate) {
        return inspectionReportRepository.findByCntrctNoAndDailyReportDateAndDltYn(cntrctNo, dailyReportDate, "N");
    }

    /**
     * 감리 일지 삭제
     */
    public void deleteReport(CwInspectionReport delete) {
        inspectionReportRepository.updateDelete(delete);
        if (delete.getApDocId() != null) {
            approvalRequestService.deleteApDoc(delete.getApDocId());
        }
    }
    
    /**
     * 특정 날짜의 감리일지들 상태값 작성완료인지 체크
     */
    public boolean checkComplete(String dailyReportDate){
         return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.inspectionreport.isAllApproved", dailyReportDate);
    }

    public Map<String, String> makeInspectionReportDoc(String cntrctNo, String dailyReportId, String imgDir, String baseUrl) {
        String[] reportIds = {"/inspection-report/inspection_report.jrf"};
        HashMap<String,String> reportParams = new HashMap<>();
        reportParams.put("p_cntrctNo",cntrctNo);
        reportParams.put("p_dailyReportId",dailyReportId);

        reportParams.put("p_imgDir",imgDir);
        reportParams.put("p_baseUrl",baseUrl);

        HashMap<String,String> callbackInfo = new HashMap<>();
        callbackInfo.put("pdfName","pdf_doc");
        callbackInfo.put("reqKey","req_cd");
        callbackInfo.put("reqValue",dailyReportId);

        String callbackUrl = baseUrl;

        if("local".equals(activeProfile)) {
            callbackUrl= "http://dsjung.idea-platform.net:8091";
        }
        else{
            if("GAIA".equals(platform.toUpperCase())){
                callbackUrl = apiGaiaDomain;
            }
            else if("PGAIA".equals(platform.toUpperCase())){
                callbackUrl = apiPGaiaDomain;
            }
            else if("CAIROS".equals(platform.toUpperCase())){
                callbackUrl = apiCairosDomain;
            }
        }
        callbackInfo.put("callbackUrl", callbackUrl + "/interface/inspectionReportDoc/callback-result");

        ubiReportClient.export(reportIds, reportParams, callbackInfo);

        return null;
    }

    /**
     * 감리 일지 hwpx 변수에 데이터 매핑 후 윈도우 서버 호출(pdf)
     */
//    @Transactional
//    public Map<String, String> makeInspectionReportDoc(CwInspectionReport inspectionReport) {
//        log.info("makeInspectionDoc: 감리일지 문서 변환 시작. report = {}", inspectionReport);
//        Map<String, String> result = new HashMap<>();
//        ObjectMapper om = new ObjectMapper();
//        result.put("result", "fail");
//
//        try {
//            if (inspectionReport == null) {
//                throw new IllegalArgumentException("makeInspectionReportDoc: inspectionReport null");
//            }
//
//            //hwpx 원본 조회, 감리일지: APP01
//            HashMap<String,Object> docResult = documentmanageService.getCbgnAndProperties("APP01");
//            ConstructionBeginsDocDto cbgnDto = (ConstructionBeginsDocDto)docResult.get("cbgnDto");
//
//            if (cbgnDto == null) {
//                throw new GaiaBizException(ErrorType.NO_DATA, "문서 템플릿이 존재하지 않습니다.");
//            }
//
//            String docTemplateDiskNm = cbgnDto.getOrgnlDocDiskNm();
//            String docTemplateDiskPath = cbgnDto.getOrgnlDocDiskPath();
//
//            Resource resource = fileService.getFile(docTemplateDiskPath, docTemplateDiskNm);
//            if (!resource.exists() || !resource.isFile()) {
//                log.error("makeInspectionReportDoc: 템플릿 리소스를 찾을 수 없음. path = {}, name = {}", docTemplateDiskPath,
//                        docTemplateDiskNm);
//                result.put("result", "fail");
//                result.put("resultMsg", "문서 템플릿 파일이 존재하지 않아 생성에 실패했습니다.");
//                return result;
//            }
//
//            // Multipart 데이터 구성
//            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
//            Map<String, Object> defaultParamsData = selectDefaultData(inspectionReport);
//
//            for (Map.Entry<String, Object> entry : defaultParamsData.entrySet()) {
//                String key = entry.getKey();
//                Object value = entry.getValue();
//                log.info("makeInspectionDoc: 데이터 구성 key = {}, value = {}", key, value);
//                try {
//                    // selctOne 결과 - 감리일지 데이터
//                    if (value instanceof Map mapVal) {
//                        for (Object k : mapVal.keySet()) {
//                            Object v = mapVal.get(k);
//                            if (v instanceof String[] array) {
//                                for (String s : array) {
//                                    // 배열이면 반복해서 같은 키로 여러 값 추가 "k":["s1","s2","s3",] << hwp 에 newline 적용할 수 있음 (s1
//                                    // newlien s2 newline)
//                                    multipartBodyBuilder.part(String.valueOf(k), s);
//                                }
//                            } else {
//                                // "k1":"v1", "k2":"v2", "k3":"v3"
//                                multipartBodyBuilder.part(String.valueOf(k), String.valueOf(v));
//                            }
//                        }
//                    } else if (value instanceof List listVal) { // selctList 결과 - 직인 데이터
//                        if ("stmpList".equals(key)) {
//                            for (Object obj : listVal) {
//                                if (obj instanceof Map map) {
//                                    try {
//                                        // stmp_type (직인타입), file_disk_path, file_disk_nm 값을 map에서 꺼냄
//                                        String stmpNo = String.valueOf(map.get("stmp_type")); // 'smtp1' / 'smtp2'
//                                        String diskPath = String.valueOf(map.get("file_disk_path")); // 파일의 저장 경로
//                                        String diskName = String.valueOf(map.get("file_disk_nm")); // 실제 저장된 파일 이름
//
//                                        log.info("makeInspectionReportDoc: stmpNo = {}, diskPath = {}, diskName = {}",
//                                                stmpNo, diskPath, diskName);
//
//                                        // 파일 경로와 이름으로 파일을 읽어옴 (Resource 객체로 반환)
//                                        Resource image = fileService.getFile(diskPath, diskName);
//
//                                        // 파일이 존재하면 multipart body에 파일을 추가
//                                        if (image.exists()) {
//                                            multipartBodyBuilder
//                                                    // form-data에 stmpNo를 name으로, 파일을 값으로 추가
//                                                    .part(stmpNo, new InputStreamResource(image.getInputStream()))
//                                                    // Content-Disposition 헤더를 수동으로 지정 (한글 파일명 깨짐 방지)
//                                                    .header("Content-Disposition", String.format(
//                                                            "form-data; name=%s; filename=%s",
//                                                            stmpNo,
//                                                            URLEncoder.encode(image.getFilename(),
//                                                                    StandardCharsets.UTF_8) // 파일명 인코딩
//                                                    ));
//                                        }
//                                    } catch (IOException e) {
//                                        log.warn("makeInspectionReportDoc: 이미지 직인 첨부 실패", e);
//                                    }
//                                }
//                            }
//                        } else {
//                            // 나머지 Array 내 Object 세팅
//                            // "key":[{"k1":"v1"}, {"k2":"v2"}, {}..]
//                            String json = om.writeValueAsString(value);
//                            multipartBodyBuilder.part(key, json);
//                        }
//                    } else {
//                        // 기타
//                        multipartBodyBuilder.part(key, value != null ? value.toString() : "");
//                    }
//                } catch (Exception e) {
//                    log.warn("makeInspectionDoc: 파라미터 처리 중 예외 발생 key = {}, value = {}", key, value, e);
//                }
//            }
//
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//            String accessToken = StringUtils.defaultString(request.getHeader("x-auth"), cookieService.getCookie(request, cookieVO.getTokenCookieName()));
//
//            // 추가 파라미터 설정
//            String docCd = "D009801"; // 문서종류(D0098) + 정보문서관리 > 네비게이션 구분(01: 통합문서관리)
//            multipartBodyBuilder.part("doc_cd_list", docCd);
//            multipartBodyBuilder.part("req_cd", inspectionReport.getDailyReportId());
//            multipartBodyBuilder.part("xauth", accessToken);
//            multipartBodyBuilder.part("cllbck_url", apiLinkDomain + "/webApi/inspectionReportDoc/callback-result");
//            multipartBodyBuilder
//                    .part(docCd, new InputStreamResource(resource.getInputStream()))
//                    .header("Content-Disposition", String.format("form-data; name=%s; filename=%s", docCd,
//                            URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8)));
//
//            iCubeClient.convertHwpxToPdf(multipartBodyBuilder);
//
//        } catch (Exception e) {
//            log.error("makeInspectionDoc: 문서 생성 중 알 수 없는 예외 발생", e);
//            throw new RuntimeException();
//        }
//        result.put("result", "success");
//        return result;
//    }

    // 감리일지 Pdf에 매핑할 데이터 조회
    public Map<String, Object> selectDefaultData(CwInspectionReport inspectionReport) {
        log.info("selectDefaultData: 감리일지 pdf 문서에 매핑할 데이터 조회 시작. report = {}", inspectionReport);
        Map<String, Object> result = new HashMap<>();

        try {
            // 감리일지 데이터 조회
            Map<String, Object> params = new HashMap<>();
            params.put("cntrctNo", inspectionReport.getCntrctNo());
            params.put("dailyReportId", inspectionReport.getDailyReportId());
            params.put("code", CommonCodeConstants.PSTN_CODE_GROUP_CODE);
            Object InspectionData = mybatisSession.selectOne(
                    "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.inspectionreport.getInspectionPdfData",
                    params);
            result.put("Inspection", InspectionData);

            // 직인 데이터 조회
            Map<String, Object> userParams = new HashMap<>();
            userParams.put("rgstrId", inspectionReport.getRgstrId());
            userParams.put("apprvlId", inspectionReport.getApprvlId());
            List<Map<String, Object>> stmpList = mybatisSession.selectList(
                    "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.inspectionreport.getInspectionPdfStmp",
                    userParams);
            result.put("stmpList", stmpList);

        } catch (GaiaBizException e) {
            log.error("selectDefaultData: 데이터 조회 중 예외 발생", e);
        }
        return result;
    }

    // 완성된 감리일지 문서 DISK 저장 및 DB 업데이트
    public Map<String, String> updateDiskFileInfo(List<MultipartFile> pdfFile, Long dailyReportId, String accessToken) throws IOException {
        Map<String, String> result = new HashMap<>();

        // dailyReportId로 해당 계약의 프로젝트 번호, 계약 번호 조회
          Map<String, Object> resultMap = mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.inspectionreport.selectPjtCntrctNo",
                dailyReportId);

        // 해당 계약번호를 가진 감리일지 조회
        CwInspectionReport inspectionReport = inspectionReportRepository.findByCntrctNoAndDailyReportIdAndDltYn(
                MapUtils.getString(resultMap, "cntrct_no"),
                dailyReportId, "N");

        // 통합문서 관리 PdF 속성 데이터 저장
        List<DocumentForm.PropertyData> propertyData = new ArrayList<>();

        // 속성 코드 조회(감리일지 cmnCd:2)
        SmComCode smComCode = commonCodeService
                .getCommonCodeByGrpCdAndCmnCd(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE, "2");

        // 01: 통합문서관리 - navi_id 생성
        final String navId = String.format("nav_%s_%s_01", MapUtils.getString(resultMap, "cntrct_no"),
                smComCode.getAttrbtCd3());

        // 감리일지: APP01
        List<CbgnPropertyDto> properties = null;
        HashMap<String, Object> cbgnAndProperties = documentmanageService.getCbgnAndProperties("APP01");
        if(cbgnAndProperties.get("properties") != null){
            properties = (List<CbgnPropertyDto>) cbgnAndProperties.get("properties");
        } else {
            throw new GaiaBizException(ErrorType.NO_DATA, "속성 코드가 존재하지 않습니다.");
        }

        // 속성 데이터 저장
        propertyData = savePdfPropertyDataToDoc(properties, inspectionReport);

        DocumentForm.DocCreateEx requestParams = new DocumentForm.DocCreateEx();
        requestParams.setNaviId(navId);
        requestParams.setNaviDiv("01"); // 01: 통합문서관리
        requestParams.setPjtNo(MapUtils.getString(resultMap, "pjt_no"));
        requestParams.setCntrctNo(MapUtils.getString(resultMap, "cntrct_no"));
        requestParams.setNaviPath("감리일지");
        requestParams.setNaviNm("감리일지");
        requestParams.setUpNaviNo(0);
        requestParams.setUpNaviId("");
        requestParams.setNaviLevel((short) 1);
        requestParams.setNaviType("FOLDR");
        requestParams.setNaviFolderType("2");
        requestParams.setNaviFolderKind(smComCode.getAttrbtCd3());
        requestParams.setProperties(documentmanageService.parseToPropertyCreate(properties, navId)); // 네비게이션 생성
        requestParams.setPropertyData(propertyData);
        requestParams.setRgstrId(inspectionReport.getRgstrId());
        requestParams.setDocNm( // 제목 없을시 보고서 번호로 pdf파일명
            StringUtils.isNotBlank(inspectionReport.getTitle())
                ? inspectionReport.getTitle() + ".pdf"
                : inspectionReport.getReportNo() + ".pdf"
        );
        requestParams.setDocId(inspectionReport.getDocId());

        Map<String, String> newHeaders = Maps.newHashMap();
        newHeaders.put("x-auth", accessToken);

//        documentServiceClient.
        List<DcStorageMain> createFileResultList = documentServiceClient.createFile(requestParams, pdfFile, newHeaders);

        // 감리일지에 docId 업데이트
        DcStorageMain dcStorageMain = null;
        if(createFileResultList != null){
            dcStorageMain = createFileResultList.get(0);
        } else {
            throw new GaiaBizException(ErrorType.NO_DATA, "문서가 존재하지 않습니다.");
        }

        result.put("cntrctNo", MapUtils.getString(resultMap, "cntrct_no"));
        result.put("dailyReportId", dailyReportId.toString());
        result.put("docId", dcStorageMain.getDocId());

        return result;
    }

    // 통합문서관리의 속성 데이터 저장
    public List<DocumentForm.PropertyData> savePdfPropertyDataToDoc(List<CbgnPropertyDto> properties, CwInspectionReport inspectionReport) {
        log.info("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 propertyCreateData = {}, cwDailyReport = {}", properties, inspectionReport);
        List<DocumentForm.PropertyData> insertList = new ArrayList<>();
        try {

            for (CbgnPropertyDto property : properties) {
                String attrbtCd = property.getAttrbtCd();

                if (attrbtCd != null) {
                    String attrbtCntnts =
                        attrbtCd.equals("workReportNo")     ? inspectionReport.getReportNo() :  // 보고서번호
                        attrbtCd.equals("dailyReportDate")  ? inspectionReport.getDailyReportDate() :   // 보고일자
                        attrbtCd.equals("title")            ? inspectionReport.getTitle() :    // 제목
                        attrbtCd.equals("apprvlStatsTxt")      ? ( 
                            "A".equals(inspectionReport.getApprvlStats()) ? "작성완료" :
                            "E".equals(inspectionReport.getApprvlStats()) ? "작성중" :
                            inspectionReport.getApprvlStats() // 그 외는 원본 값
                        ) :
                        null;

                    if (attrbtCntnts != null && !attrbtCntnts.isBlank()) {
                        DocumentForm.PropertyData row = new DocumentForm.PropertyData();
                        row.setAttrbtCd(attrbtCd);
                        row.setAttrbtCntnts(attrbtCntnts);
                        row.setRgstrId(inspectionReport.getRgstrId());
                        row.setChgId(inspectionReport.getChgId());

                        insertList.add(row);
                    }
                }
            }
            log.info("savePdfPropertyDataToDoc: 데이터 저장 결과 = {}", insertList);

            return insertList;
        } catch (GaiaBizException e) {
            log.warn("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 중 오류 발생 메세지 = {}", e.getMessage());
        }
        return insertList;
    }

    // 감리일지 docId 업데이트
    public Map<String, String> updateInspectionReportDocId(String cntrctNo, String dailyReportId, String docId) {
        log.info("updateInspectionReportDocId: 감리일지 docId 업데이트 진행 cntrctNo = {}, dailyReportId = {}, docId = {}", cntrctNo, dailyReportId, docId);
        Map<String, String> result = new HashMap<>();
        result.put("result", "fail");

        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportId", dailyReportId);
        map.put("docId", docId);
        log.info("updateInspectionReportDocId: 감리일지 docId 업데이트 param = {}", map);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.inspectionreport.updateInspectionReportDocId", map);

        result.put("result", "success");
        return result;
    }

    // /**
    // * 감리일지 전자결재 요청
    // */
    // @Transactional
    // public void requestApprovalInspectionReport(String cntrctNo, Long
    // dailyReportId, String isApiYn, String pjtDiv) {
    // log.debug("requestApprovalInspectionReport: cntrctNo = {}, dailyReportId =
    // {}", String.valueOf(cntrctNo),
    // String.valueOf(dailyReportId));
    // CwInspectionReport output =
    // inspectionReportRepository.findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo,
    // dailyReportId, "N");

    // if (output == null) {
    // throw new BizException("감리일지 정보가 없습니다.");
    // }

    // // 첫번째 결재자가 pgaia 사용자인지 체크
    // Map<String, Object> checkParams = new HashMap<>();
    // checkParams.put("pjtNo", UserAuth.get(true).getPjtNo());
    // checkParams.put("cntrctNo", cntrctNo);
    // checkParams.put("apType", "12");
    // boolean isPgaiaUser = mybatisSession.selectOne(
    // "kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkPgaiaFirstApprover",
    // checkParams);
    // // pgaia사용자 + 카이로스 플랫폼 + pgaia 프로젝트면 API통신 진행
    // boolean toApi = isPgaiaUser && "cairos".equals(platform) &&
    // "Y".equals(isApiYn) && "P".equals(pjtDiv);

    // updateInspectionApprovalStatus(output, UserAuth.get(true).getUsrId(), "E");

    // Map<String, Object> resourceMap = new HashMap<>();
    // // API 통신 필요한 경우 리소스 조회
    // // 상대 서버로 데이터를 전달하기 위한 데이터 준비
    // if (toApi) {
    // List<CwInspectionReportActivity> cwInspectionReportActivities =
    // cwInspectionReportActivityRepository
    // .findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo, dailyReportId, "N");
    // List<CwInspectionReportPhoto> cwInspectionReportPhotos =
    // cwInspectionReportPhotoRepository
    // .findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo, dailyReportId, "N");

    // resourceMap.put("activity", cwInspectionReportActivities);
    // resourceMap.put("photo", cwInspectionReportPhotos);

    // // 감리일지 첨부파일 (파일 객체 대신 메타데이터만 전송)
    // List<Map<String, Object>> inspectionFileInfo = Collections.emptyList();
    // Set<String> atchFileNos = cwInspectionReportPhotos.stream()
    // .map(CwInspectionReportPhoto::getAtchFileNo)
    // .filter(Objects::nonNull)
    // .collect(Collectors.toSet());

    // if (!atchFileNos.isEmpty()) {
    // log.info("##### Found atchFileNo list in photos: {}", atchFileNos);

    // List<CwAttachments> inspectionReportFiles = cwAttachmentsRepository
    // .findByFileNoInAndDltYn(atchFileNos, "N");
    // log.info("##### Found {} safety attachment files",
    // inspectionReportFiles.size());

    // inspectionFileInfo = convertToFileInfo(inspectionReportFiles);
    // } else {
    // log.info("##### No atchFileNo found in inspection photos");
    // }
    // resourceMap.put("inspectionFileInfo", inspectionFileInfo);
    // }
    // approvalRequestService.insertInspectionReport(output, toApi, resourceMap);
    // }

    // /**
    // * 감리일지 승인상태 변경
    // *
    // * @param report
    // * @param usrId
    // * @param apprvlStats
    // */
    // public void updateInspectionApprovalStatus(CwInspectionReport report, String
    // usrId, String apprvlStats) {
    // report.setApprvlStats(apprvlStats);
    // if ("E".equals(apprvlStats)) {
    // report.setApprvlReqId(usrId);
    // report.setApprvlReqDt(LocalDateTime.now());
    // } else {
    // report.setApprvlId(usrId);
    // report.setApprvlDt(LocalDateTime.now());
    // }
    // inspectionReportRepository.save(report);
    // }

    // /**
    // * 첨부파일 리스트 저장
    // *
    // */
    // @Transactional
    // public int createCwAttachmentsList(List<CwAttachments> cwAttachmentsList) {
    // Integer fileNo = generateFileNo(); // 가장 큰 fileNo 값을 기반으로 새 fileNo 생성
    // int sno = 1; // sno는 1부터 시작

    // for (CwAttachments cwAttachments : cwAttachmentsList) { // 파일 새로 추가
    // if (cwAttachments.getFileNo() == null) { // 기존 fileNo 없을시
    // cwAttachments.setFileNo(fileNo); // 파일들에 동일한 fileNo 설정
    // cwAttachments.setSno(sno); // 각 파일에 대해 순차적인 sno 설정
    // sno++; // 다음 파일의 sno 값 증가
    // } else { // 파일 수정
    // cwAttachments.setSno(cwAttachmentsRepository.findMaxSnoByFileNo(cwAttachments.getFileNo())
    // + 1);
    // }
    // cwAttachmentsRepository.save(cwAttachments); // 파일 저장
    // }
    // return fileNo;
    // }

    // /**
    // * FileNo생성
    // *
    // */
    // private Integer generateFileNo() {
    // Integer maxFileNo = cwAttachmentsRepository.findMaxFileNo();
    // return (maxFileNo == null ? 1 : maxFileNo + 1);
    // }

    // /**
    // * 감리일지 업데이트 BY apDocId
    // *
    // * @param apDocId
    // * @param apUsrId
    // * @param apDocStats
    // */
    // public void updateInspectionByApDocId(String apDocId, String apUsrId, String
    // apDocStats) {
    // CwInspectionReport cwInspection =
    // inspectionReportRepository.findByApDocId(apDocId).orElse(null);
    // if (cwInspection != null) {
    // String apStats = "C".equals(apDocStats) ? "A" : "R";
    // updateInspectionApprovalStatus(cwInspection, apUsrId, apStats);
    // }
    // }

    // /**
    // * 감리일지 데이터 조회
    // *
    // * @param apDocId
    // * @return
    // */
    // public Map<String, Object> selectInspectionByApDocId(String apDocId) {
    // Map<String, Object> returnMap = new HashMap<>();
    // CwInspectionReport cwInspectionReport =
    // inspectionReportRepository.findByApDocId(apDocId).orElse(null);
    // if (cwInspectionReport != null) {
    // returnMap.put("report", cwInspectionReport);
    // returnMap.put("resources", selectInspectionResource(cwInspectionReport,
    // cwInspectionReport.getCntrctNo(),
    // cwInspectionReport.getDailyReportId()));
    // }
    // return returnMap;
    // }

    // /**
    // * 감리일지 리소스 조회
    // *
    // * @param cntrctNo
    // * @param dailyReportId
    // * @return
    // */
    // public Map<String, Object> selectInspectionResource(CwInspectionReport
    // cwInspectionReport, String cntrctNo,
    // Long dailyReportId) {
    // Map<String, Object> returnMap = new HashMap<>();
    // List<CwInspectionReportActivity> cwInspectionReportActivities =
    // cwInspectionReportActivityRepository
    // .findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo, dailyReportId, "N");
    // List<CwInspectionReportPhoto> cwInspectionReportPhotos =
    // cwInspectionReportPhotoRepository
    // .findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo, dailyReportId, "N");

    // // 감리일지 첨부파일 (파일 객체 대신 메타데이터만 전송)
    // List<Map<String, Object>> inspectionFileInfo = Collections.emptyList();
    // Set<String> atchFileNos = cwInspectionReportPhotos.stream()
    // .map(CwInspectionReportPhoto::getAtchFileNo)
    // .filter(Objects::nonNull)
    // .collect(Collectors.toSet());

    // if (!atchFileNos.isEmpty()) {
    // log.info("##### Found atchFileNo list in photos: {}", atchFileNos);

    // List<CwAttachments> inspectionReportFiles = cwAttachmentsRepository
    // .findByFileNoInAndDltYn(atchFileNos, "N");
    // log.info("##### Found {} safety attachment files",
    // inspectionReportFiles.size());

    // inspectionFileInfo = convertToFileInfo(inspectionReportFiles);
    // } else {
    // log.info("##### No atchFileNo found in inspection photos");
    // }

    // returnMap.put("activity", cwInspectionReportActivities);
    // returnMap.put("photo", cwInspectionReportPhotos);
    // returnMap.put("inspectionFileInfo", inspectionFileInfo);
    // return returnMap;
    // }

    // /**
    // * 결재요청 삭제 -> 감리일지 컬럼 값 삭제 or 데이터 삭제
    // *
    // * @param apDocId
    // * @param usrId
    // * @param toApi
    // */
    // public void updatePaymentApprovalReqCancel(String apDocId, String usrId,
    // boolean toApi) {
    // CwInspectionReport cwInspectionReport =
    // inspectionReportRepository.findByApDocId(apDocId).orElse(null);

    // if (cwInspectionReport == null)
    // return;

    // if (toApi && cwInspectionReport != null) {
    // // api 통신 true -> 데이터 삭제
    // cwInspectionReportActivityRepository.deleteActivityByCntrctNoAndDailyReportId(
    // cwInspectionReport.getCntrctNo(), cwInspectionReport.getDailyReportId());
    // cwInspectionReportPhotoRepository.deletePhotoByCntrctNoAndDailyReportId(cwInspectionReport.getCntrctNo(),
    // cwInspectionReport.getDailyReportId());
    // cwAttachmentsRepository.deleteCwAttachments(cwInspectionReport.getCntrctNo(),
    // cwInspectionReport.getDailyReportId());
    // inspectionReportRepository.delete(cwInspectionReport);
    // } else {
    // inspectionReportRepository.updateApprovalStausCancel(null, null, null, null,
    // cwInspectionReport.getCntrctNo(), cwInspectionReport.getDailyReportId(),
    // usrId);
    // }
    // }

    // /**
    // * 감리일지 API통신 -> 리소스 저장
    // */
    // public void insertResourcesToApi(CwInspectionReport cwInspectionReport,
    // List<CwInspectionReportActivity> cwInspectionReportActivities,
    // List<CwInspectionReportPhoto> cwInspectionReportPhotos, List<Map<String,
    // Object>> inspectionFileInfo) {

    // cwInspectionReportRepository.save(cwInspectionReport);

    // if (cwInspectionReportActivities != null &&
    // !cwInspectionReportActivities.isEmpty()) {
    // for (CwInspectionReportActivity activity : cwInspectionReportActivities) {
    // cwInspectionReportActivityRepository.save(activity);
    // }
    // }

    // if (cwInspectionReportPhotos != null && !cwInspectionReportPhotos.isEmpty())
    // {
    // for (CwInspectionReportPhoto activity : cwInspectionReportPhotos) {
    // cwInspectionReportPhotoRepository.save(activity);
    // }
    // }

    // // 파일 정보에서 안전점검 첨부파일들 가져오기
    // log.info("##### Received inspectionFileInfo: {}", inspectionFileInfo != null
    // ? inspectionFileInfo.size() : 0);
    // if (inspectionFileInfo != null && !inspectionFileInfo.isEmpty()) {
    // log.info("##### Processing {} safety attachment file info",
    // inspectionFileInfo.size());
    // insertSafetyFileInfoToApi(cwInspectionReport, inspectionFileInfo,
    // cwInspectionReportPhotos);
    // } else {
    // log.info("##### No inspection attachment file info received");
    // }

    // }

    // private void insertSafetyFileInfoToApi(CwInspectionReport cwInspectionReport,
    // List<Map<String, Object>> safetyFileInfo, List<CwInspectionReportPhoto>
    // photoList) {
    // // 안전 점검 정보 조회
    // CwInspectionReport inspectionReport =
    // inspectionReportRepository.findByCntrctNoAndDailyReportIdAndDltYn(
    // cwInspectionReport.getCntrctNo(), cwInspectionReport.getDailyReportId(),
    // "N");

    // // 파일 번호를 가지고 있는 photo들
    // List<String> fileNoList = photoList.stream()
    // .map(CwInspectionReportPhoto::getAtchFileNo)
    // .filter(Objects::nonNull)
    // .collect(Collectors.toList());

    // if (inspectionReport == null) {
    // log.error("Deficiency not found for InspectionNo: {}",
    // inspectionReport.getDailyReportId());
    // return;
    // }

    // insertFileInfoToApi(cwInspectionReport.getCntrctNo(), safetyFileInfo,
    // fileNoList,
    // inspectionReport.getRgstrId());
    // }

    // // 파일 정보 변환 헬퍼 메소드 (JSON 직렬화 가능) - 파일 내용 포함
    // public List<Map<String, Object>> convertToFileInfo(List<CwAttachments>
    // attachments) {
    // log.info("##### Converting {} attachments to file info", attachments != null
    // ? attachments.size() : 0);

    // if (attachments == null || attachments.isEmpty()) {
    // log.info("##### No attachments to convert");
    // return Collections.emptyList();
    // }

    // List<Map<String, Object>> fileInfoList = new ArrayList<>();

    // for (CwAttachments attachment : attachments) {
    // if (attachment == null || attachment.getFileNm() == null) {
    // log.warn("##### Invalid attachment data: {}", attachment);
    // continue;
    // }

    // // 파일 경로가 없는 경우 건너뛰기
    // if (attachment.getFileDiskPath() == null || attachment.getFileDiskNm() ==
    // null) {
    // log.warn("##### Physical file path not found for attachment: {}",
    // attachment.getFileNm());
    // continue;
    // }

    // Path filePath = Paths.get(attachment.getFileDiskPath(),
    // attachment.getFileDiskNm());
    // if (!Files.exists(filePath)) {
    // log.warn("##### File not found: {}", filePath);
    // continue;
    // }

    // try {
    // log.info("##### Reading file: {}", filePath);
    // // 파일 내용을 Base64로 인코딩
    // byte[] fileContent = Files.readAllBytes(filePath);
    // String base64Content = Base64.getEncoder().encodeToString(fileContent);

    // Map<String, Object> fileInfo = new HashMap<>();
    // fileInfo.put("fileNo", attachment.getFileNo());
    // fileInfo.put("sno", attachment.getSno());
    // fileInfo.put("fileDiv", attachment.getFileDiv());
    // fileInfo.put("fileNm", attachment.getFileNm());
    // fileInfo.put("fileDiskNm", attachment.getFileDiskNm());
    // fileInfo.put("fileDiskPath", attachment.getFileDiskPath());
    // fileInfo.put("fileSize", attachment.getFileSize());
    // fileInfo.put("fileHitNum", attachment.getFileHitNum());
    // fileInfo.put("rgstrId", attachment.getRgstrId());
    // fileInfo.put("chgId", attachment.getChgId());
    // fileInfo.put("dltYn", attachment.getDltYn());
    // fileInfo.put("fileContent", base64Content); // Base64로 인코딩된 파일 내용

    // if (fileInfo.containsKey("fileDiv")) {
    // log.info("fileDiv is present with value: {}", fileInfo.get("fileDiv"));
    // } else {
    // log.info("fileDiv is missing in fileInfo map");
    // }

    // fileInfoList.add(fileInfo);
    // log.info("##### File info created for: {} (Size: {} bytes, Base64 length:
    // {})",
    // attachment.getFileNm(), fileContent.length, base64Content.length());
    // } catch (IOException e) {
    // log.error("##### Error reading file {}: {}", filePath, e.getMessage());
    // // 파일 읽기 실패 시 건너뛰기
    // continue;
    // }
    // }

    // log.info("##### Successfully converted {} attachments to file info",
    // fileInfoList.size());
    // return fileInfoList;
    // }

    // /**
    // * 공통 파일 처리 메서드
    // *
    // * @param files 처리할 파일 목록
    // * @param fileNo 연결할 파일 번호
    // * @param rgstrId 등록자 ID
    // */
    // @Transactional
    // public void insertFileInfoToApi(String cntrctNo, List<Map<String, Object>>
    // files, List<String> fileNoList,
    // String rgstrId) {
    // List<CwAttachments> cwAttachmentsList = new ArrayList<>();

    // log.info("##### FILE PROCESSING START - Type: {}, TargetId: {}, FileCount:
    // {}, FileNo: {}",
    // files != null ? files.size() : 0, fileNoList);

    // if (files != null && !files.isEmpty()) {
    // log.info("##### Starting {} file processing for {} - TargetId: {}, FileCount:
    // {}", files.size());

    // // 파일 저장 경로 설정
    // String fullPath = Path.of(uploadPath,
    // getUploadPathByWorkType(FileUploadType.INSPECTION_REPORT, cntrctNo))
    // .toString()
    // .replace("\\", "/");

    // log.info("##### {} file storage path configured - BaseDir: {}, DatePath: {},
    // FullPath: {}", fullPath);

    // for (int i = 0; i < files.size(); i++) {
    // for (Map<String, Object> fileInfo : files) {
    // String fileName = (String) fileInfo.get("fileNm");
    // log.info("##### Processing {} file info - FileName: {}, FileInfo keys: {}",
    // fileName,
    // fileInfo.keySet());

    // String fileDiv = (String) fileInfo.get("fileDiv");

    // // 파일 이름이 비어있거나 null인 경우 건너뛰기
    // if (fileName == null || fileName.trim().isEmpty()) {
    // log.warn("##### Skipping {} file with empty name");
    // continue;
    // }

    // log.info("##### Processing {} file: {} (Size: {} bytes)", fileName,
    // fileInfo.get("fileSize"));

    // try {
    // // Base64로 인코딩된 파일 내용을 디코딩
    // String base64Content = (String) fileInfo.get("fileContent");
    // if (base64Content == null || base64Content.isEmpty()) {
    // log.warn("##### No file content found for {} file: {}", fileName);
    // continue;
    // }

    // log.info("##### Base64 content length for {} file {}: {}", fileName,
    // base64Content.length());

    // byte[] fileContent = Base64.getDecoder().decode(base64Content);
    // log.info("##### Decoded {} file content: {} bytes", fileContent.length);

    // // 파일을 디스크에 저장
    // String savedFileName = generateUniqueFileName(fileName);
    // Path savedFilePath = Paths.get(fullPath, savedFileName);

    // log.info("##### Saving {} file to: {}", savedFilePath);

    // // 디렉토리가 없으면 생성
    // Files.createDirectories(savedFilePath.getParent());
    // log.info("##### Created directory: {}", savedFilePath.getParent());

    // // 파일 저장
    // Files.write(savedFilePath, fileContent);
    // log.info("##### {} file saved to disk: {}", savedFilePath);

    // String fileNo = (fileNoList != null && fileNoList.size() > i) ?
    // fileNoList.get(i) : null;

    // // String -> Integer
    // String fileNoStr = (fileNoList != null && fileNoList.size() > i) ?
    // fileNoList.get(i) : null;
    // Integer individualFileNo = null;

    // try {
    // if (fileNoStr != null && !fileNoStr.isBlank()) {
    // individualFileNo = Integer.parseInt(fileNoStr);
    // }
    // } catch (NumberFormatException e) {
    // log.warn("##### Invalid fileNo format at index {}: '{}'", i, fileNoStr);
    // }

    // CwAttachments cwAttachments = new CwAttachments();
    // cwAttachments.setFileNo(individualFileNo);
    // cwAttachments.setFileDiv(fileDiv);
    // cwAttachments.setFileNm(fileName);
    // cwAttachments.setFileDiskNm(savedFileName);
    // cwAttachments.setFileDiskPath(fullPath);
    // cwAttachments.setFileSize(((Number) fileInfo.get("fileSize")).intValue());
    // cwAttachments.setDltYn("N");

    // // fileHitNum 안전한 타입 변환
    // Object fileHitNumObj = fileInfo.get("fileHitNum");
    // if (fileHitNumObj != null) {
    // if (fileHitNumObj instanceof Integer) {
    // cwAttachments.setFileHitNum((Integer) fileHitNumObj);
    // } else if (fileHitNumObj instanceof Short) {
    // cwAttachments.setFileHitNum(((Short) fileHitNumObj).intValue());
    // } else if (fileHitNumObj instanceof BigDecimal) {
    // BigDecimal decimalValue = (BigDecimal) fileHitNumObj;
    // try {
    // cwAttachments.setFileHitNum(decimalValue.intValueExact());
    // } catch (ArithmeticException e) {
    // log.warn("##### fileHitNum has decimal part for {} file: {}, value: {}",
    // fileName, decimalValue);
    // cwAttachments.setFileHitNum(0);
    // }
    // } else if (fileHitNumObj instanceof Number) {
    // // Catch-all for Long, Double, Float, etc.
    // cwAttachments.setFileHitNum(((Number) fileHitNumObj).intValue());
    // } else {
    // log.warn("##### Unexpected fileHitNum type for {} file: {}, value: {}",
    // fileName,
    // fileHitNumObj);
    // cwAttachments.setFileHitNum(0);
    // }
    // } else {
    // cwAttachments.setFileHitNum(0);
    // }

    // cwAttachments.setRgstrId(rgstrId);
    // cwAttachments.setChgId(rgstrId);

    // cwAttachmentsList.add(cwAttachments);
    // log.info("##### {} attachment object created for file: {} - FileNo: {},
    // FileSize: {}",
    // fileName, fileNo, cwAttachments.getFileSize());
    // } catch (Exception e) {
    // log.error("##### Error processing {} file {}: {}", fileName, e.getMessage(),
    // e);
    // }
    // }
    // }

    // if (!cwAttachmentsList.isEmpty()) {
    // try {
    // log.info("##### Saving {} {} attachments to database",
    // cwAttachmentsList.size());
    // Integer savedFileNo = createCwAttachmentsList(cwAttachmentsList);
    // log.info("##### Successfully saved {} {} attachments with FileNo: {}",
    // cwAttachmentsList.size(),
    // savedFileNo);
    // } catch (Exception e) {
    // log.error("##### Error saving {} attachments to database: {}",
    // e.getMessage(), e);
    // }
    // } else {
    // log.warn("##### No valid {} attachments to save");
    // }
    // } else {
    // log.info("##### No {} files to process for {} - TargetId: {}");
    // }

    // log.info("##### FILE PROCESSING END - Type: {}, TargetId: {}, ProcessedCount:
    // {}", cwAttachmentsList.size());
    // }

    // // 고유한 파일명 생성 메서드
    // private String generateUniqueFileName(String originalFileName) {
    // String extension = "";
    // String nameWithoutExtension = originalFileName;

    // int lastDotIndex = originalFileName.lastIndexOf('.');
    // if (lastDotIndex > 0) {
    // extension = originalFileName.substring(lastDotIndex);
    // nameWithoutExtension = originalFileName.substring(0, lastDotIndex);
    // }

    // String timestamp = String.valueOf(System.currentTimeMillis());
    // return nameWithoutExtension + "_" + timestamp + extension;
    // }

}