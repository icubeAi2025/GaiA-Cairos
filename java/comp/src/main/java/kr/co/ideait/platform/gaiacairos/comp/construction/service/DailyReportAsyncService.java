package kr.co.ideait.platform.gaiacairos.comp.construction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.api.service.ApiService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DocumentManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwDailyReportRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.DailyreportMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.ConstructionBeginsDocDto;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.ICubeClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.UbiReportClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class DailyReportAsyncService extends AbstractGaiaCairosService {

    @Autowired
    DocumentManageService documentManageService;

    @Autowired
    DocumentServiceClient documentServiceClient;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    private FileService fileService;

    @Autowired
    ApiService apiService;

    @Autowired
    private ICubeClient iCubeClient;

    @Autowired
    private UbiReportClient ubiReportClient;

    @Autowired
    CwDailyReportRepository cwDailyReportRepository;

    // 작업일지 hwpx 변수에 데이터 매핑 후 윈도우 서버 호출
    @Async("taskExecutor")
    public void makeDailyreportDoc(CwDailyReport report, String xAuth, String cookie) {
        log.info("makeDailyreportDoc: 작업일지 문서 변환 시작. report = {}", report);
        ObjectMapper om = new ObjectMapper();

        try {

            if (report == null) {
                throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,"makeDailyreportDoc: CwDailyReport null");
            }

            // hwpx 원본 조회
            HashMap<String,Object> docResult = documentManageService.getCbgnAndProperties("APP02");
            ConstructionBeginsDocDto cbgnDto = null;
            if(docResult.get("cbgnDto") != null) {
                cbgnDto = (ConstructionBeginsDocDto)docResult.get("cbgnDto");
                log.info("makeDailyreportDoc: cbgnDto = {}", cbgnDto);
            } else {
                throw new GaiaBizException(ErrorType.NO_DATA, "문서 템플릿이 존재하지 않습니다.");
            }

            String docTemplateDiskNm = cbgnDto.getOrgnlDocDiskNm();
            String docTemplateDiskPath = cbgnDto.getOrgnlDocDiskPath();

            Resource resource = fileService.getFile(docTemplateDiskPath, docTemplateDiskNm);
            if (resource == null || !resource.exists() || !resource.isFile()) {
                log.error("makeDailyreportDoc: 템플릿 리소스를 찾을 수 없음. path = {}, name = {}", docTemplateDiskPath, docTemplateDiskNm);

                throw new GaiaBizException(ErrorType.NO_DATA, "템플릿 리소스 미존재");
            }

            String resourceFilename = resource.getFilename();
            String extension = "";
            if (resourceFilename != null && resourceFilename.contains(".")) {
                extension = resourceFilename.substring(resourceFilename.lastIndexOf('.') + 1);
            }
            // doc_nm 에 보고서 제목으로 파일명 저장

            // Multipart 데이터 구성
            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            Map<String, Object> defaultParamsData = selectDefaultData(report);

            for (Map.Entry<String, Object> entry : defaultParamsData.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                log.info("makeDailyreportDoc: 데이터 구성 key = {}, value = {}", key, value);
                try {
                    // selctOne 결과
                    if (value instanceof Map mapVal) {

                        Set<Map.Entry> innerEntrySet = mapVal.entrySet();
                        for (Map.Entry e : innerEntrySet) {
                            Object k = e.getKey();
                            Object v = e.getValue();
                            if (v instanceof String[] array) {
                                for (String s : array) {
                                    // 배열이면 반복해서 같은 키로 여러 값 추가 "k":["s1","s2","s3",] << hwp 에 newline 적용할 수 있음 (s1 newlien s2 newline)
                                    multipartBodyBuilder.part(String.valueOf(k), s);
                                }
                            } else {
                                // "k1":"v1", "k2":"v2", "k3":"v3"
                                multipartBodyBuilder.part(String.valueOf(k), String.valueOf(v));
                            }
                        }

                        // selectList 결과
                    } else if (value instanceof List listVal) {
                        if ("dailyReportPhotoInfo".equals(key)) {
                            // 이미지 정보 - "dailyReportPhotoInfo":[{""file_mapping_no":"", "activity_nm":"", "titl_nm":""":"", "activity_nm":"", "titl_nm":""},{},{}]
                            // 이미지 파일 - "file_mapping_no": binary
                            List<Map<String, Object>> infoList = new ArrayList<>();

                            for (Object obj : listVal) {
                                if (obj instanceof Map map) {
                                    String fileMappingNo = String.valueOf(map.get("file_mapping_no"));
                                    String activityNm = map.get("activity_nm") == null ? "" : String.valueOf(map.get("activity_nm"));
                                    String titleNm = String.valueOf(map.get("titl_nm"));

                                    // 이미지 파일 제외하고 모두 jsonObject 로 저장
                                    infoList.add(Map.of(
                                            "file_mapping_no", fileMappingNo,
                                            "activity_nm", activityNm,
                                            "titl_nm", titleNm
                                    ));

                                    try {
                                        // 파일 첨부
                                        String diskPath = String.valueOf(map.get("file_disk_path"));
                                        String diskName = String.valueOf(map.get("file_disk_nm"));

                                        Resource image = fileService.getFile(diskPath, diskName);
                                        if (image != null && image.exists()) {
                                            multipartBodyBuilder
                                                    .part(fileMappingNo, new InputStreamResource(image.getInputStream()))
                                                    .header("Content-Disposition", String.format(
                                                            "form-data; name=%s; filename=%s",
                                                            fileMappingNo,
                                                            URLEncoder.encode(StringUtils.defaultString(image.getFilename()), StandardCharsets.UTF_8)));
                                        }
                                    } catch (IOException e) {
                                        log.warn("makeDailyreportDoc: 이미지 첨부 실패 fileMappingNo = {} / {}", fileMappingNo, e.getMessage());
                                    }
                                }
                            }
                            // JSON으로 변환해 multipart에 포함
                            String json = om.writeValueAsString(infoList);
                            multipartBodyBuilder.part("dailyReportPhotoInfo", json, MediaType.APPLICATION_JSON);

                        } else if("stmps".equals(key)){
                            // 직인 처리
                            for (Object obj : listVal) {
                                if (obj instanceof Map map) {

                                    try {

                                        String stmpNo = String.valueOf(map.get("stmp_type"));
                                        // 파일 첨부
                                        String diskPath = String.valueOf(map.get("file_disk_path"));
                                        String diskName = String.valueOf(map.get("file_disk_nm"));
                                        log.info("makeDailyreportDoc: stmpNo = {}, diskPath = {}, diskName = {}", stmpNo, diskPath, diskName);
                                        Resource image = fileService.getFile(diskPath, diskName);
                                        if (image != null && image.exists()) {
                                            multipartBodyBuilder
                                                    .part(stmpNo, new InputStreamResource(image.getInputStream()))
                                                    .header("Content-Disposition", String.format(
                                                            "form-data; name=%s; filename=%s",
                                                            stmpNo,
                                                            URLEncoder.encode(StringUtils.defaultString(image.getFilename()), StandardCharsets.UTF_8)));
                                        }
                                    } catch (IOException e) {
                                        log.warn("makeDailyreportDoc: 이미지 직인 첨부 실패 {}", e.getMessage());
                                    }
                                }
                            }
                        } else {
                            // 나머지 Array 내 Object 세팅
                            // "key":[{"k1":"v1"}, {"k2":"v2"}, {}..]
                            String json = om.writeValueAsString(value);
                            multipartBodyBuilder.part(key, json);
                        }
                    } else {
                        // 기타
                        multipartBodyBuilder.part(key, value != null ? value.toString() : "");
                    }
                } catch (GaiaBizException e) {
                    log.warn("makeDailyreportDoc: 파라미터 처리 중 예외 발생 key = {}, value = {}, e = {}", key, value, e.getMessage());
                } catch (JsonProcessingException e) {
                    log.error("Json Parsing Failed : {}", e.getMessage());
                }
            }


            String accessToken = StringUtils.defaultString(xAuth, cookie);

            // 추가 파라미터 설정
            String docCd = "D009901";
            multipartBodyBuilder.part("doc_cd_list", docCd);
//            multipartBodyBuilder.part("req_cd", report.getDailyReportId() + "^_^" + accessToken);
            multipartBodyBuilder.part("req_cd", report.getDailyReportId());
            multipartBodyBuilder.part("xauth", accessToken);
            multipartBodyBuilder.part("cllbck_url", apiLinkDomain + "/webApi/dailyreportDoc/callback-result");
            multipartBodyBuilder
                    .part(docCd, new InputStreamResource(resource.getInputStream()))
                    .header("Content-Disposition", String.format("form-data; name=%s; filename=%s", docCd, URLEncoder.encode(StringUtils.defaultString(resource.getFilename()), StandardCharsets.UTF_8)));

            iCubeClient.convertHwpxToPdf(multipartBodyBuilder);

        } catch (GaiaBizException | IOException e) {
            log.error("makeDailyreportDoc: 문서 생성 중 알 수 없는 예외 발생 : {}", e.getMessage());
        }


    }

    @Async("taskExecutor")
    public void makeDailyreportPdf(CwDailyReport report, String xAuth, String cookie) {
        log.info("makeDailyreportPdf: 작업일지 문서 변환 시작. report = {}", report);

        try {

            if (report == null) {
                throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,"makeDailyreportPdf: CwDailyReport null");
            }
            String[] reportIds = {"daily-report/daily_report.jrf"};

            Map<String, String> reportParams = new HashMap<>();
            reportParams.put("cntrctNo", report.getCntrctNo());
            reportParams.put("dailyReportId", report.getDailyReportId().toString());
            reportParams.put("url", apiCairosDomain.replaceAll("/+$", "") + "/");

            // dev: /home/dev/storage/upload/, stg: /home/ubuntu/GAIA/upload/ prod: /home/ubuntu/GAIA/upload/
            String imgDir = previewPath.replaceAll("(upload[/\\\\]?).*$", "");

            reportParams.put("imgDir", imgDir);

            Map<String, String> callbackInfo = new HashMap<>();
            String accessToken = StringUtils.defaultString(xAuth, cookie);
            callbackInfo.put("x-auth", accessToken);
            callbackInfo.put("reqKey", "dailyReportId");
            callbackInfo.put("reqValue", report.getDailyReportId().toString());
            callbackInfo.put("pdfName", "pdf_doc");
            String apiUrl = "";
            if(("local".equals(activeProfile))) {
                apiUrl= "http://minju.idea-platform.net:8091";
            } else if("GAIA".equals(platform.toUpperCase())){
                apiUrl = apiGaiaDomain;
            }
            else if("PGAIA".equals(platform.toUpperCase())){
                apiUrl = apiPGaiaDomain;
            }
            else if("CAIROS".equals(platform.toUpperCase())){
                apiUrl = apiCairosDomain;
            }
            callbackInfo.put("callbackUrl", apiUrl + "/interface/dailyreportDoc/callback-result");


            log.info("makeDailyreportPdf: 데이터 세팅 완료. reportIds = {}, reportParams = {}, callbackInfo = {}", report, reportParams, callbackInfo);
            ubiReportClient.export(reportIds, reportParams, callbackInfo);
        } catch (GaiaBizException e) {
            throw new GaiaBizException(ErrorType.ETC, "작업일지 리포트 변환 중 오류 발생. error = {}", e.getMessage());
        }


    }

    // 완성된 작업일지 문서 DISK 저장 및 DB 업데이트
    public Map<String, String> updateDiskFileInfo(List<MultipartFile> pdfFile, Long dailyReportId, String accessToken){
        Map<String, String> result = new HashMap<>();

        log.info("updateDiskFileInfo: 작업일지 문서 DISK 저장 및 업데이트 dailyReportId = {}", dailyReportId);

        // 기본 정보 조회
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("dailyReportId", dailyReportId);
        Map<String, Object> resultMap = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyReportSimple", infoMap);
        log.info("updateDiskFileInfo: 작업일지 dailyReportId 로 cntrctNo, pjtNo 조회 성공 dailyreport = {}, result = {}", dailyReportId, resultMap);

        CwDailyReport report = cwDailyReportRepository.findByCntrctNoAndDailyReportIdAndDltYn(MapUtils.getString(resultMap, "cntrct_no"), dailyReportId, "N");
        log.info("updateDiskFileInfo: 작업일지 조회 성공 result = {}", report);


        // 통합문서 관리 PdF 속성 데이터 저장
//        // 속성 코드 조회
        SmComCode smComCode = commonCodeService.getCommonCodeByGrpCdAndCmnCd(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE, "1");
//        log.info("updateDiskFileInfo: 작업일지 PDF 속성 코드 조회 성공 result = {}", smComCode);
//        // 속성 코드 PropertyCreate 형식으로 파싱
//        List<DocumentForm.PropertyCreate> propertyCreateData = commonCodeService.createPropertyListForCommonCode(smComCode.getCmnGrpCd(), smComCode.getCmnCd(), dailyReportId.toString());
//        log.info("updateDiskFileInfo: 작업일지 PDF 속성 코드 PropertyCreate 파싱 성공 result = {}", propertyCreateData);
//

        // 속성 코드 조회
        HashMap<String,Object> docResult = documentManageService.getCbgnAndProperties("APP02");
        List<DocumentForm.PropertyData> propertyData = null;
        List<CbgnPropertyDto> properties = null;
        if(docResult.get("properties") != null) {
            // 속성 데이터 저장
            properties = (List<CbgnPropertyDto>) docResult.get("properties");
            propertyData = this.savePdfPropertyDataToDoc(properties, report);
        } else {
            throw new GaiaBizException(ErrorType.NO_DATA, "속성 코드가 존재하지 않습니다.");
        }

        final String navId = String.format("nav_%s_%s_01", MapUtils.getString(resultMap, "cntrct_no"), smComCode.getAttrbtCd3());

        DocumentForm.DocCreateEx requestParams = new DocumentForm.DocCreateEx();
        requestParams.setNaviId(navId);
        requestParams.setNaviDiv("01");
        requestParams.setPjtNo(MapUtils.getString(resultMap, "pjt_no"));
        requestParams.setCntrctNo(MapUtils.getString(resultMap, "cntrct_no"));
        requestParams.setNaviPath("작업일지");
        requestParams.setNaviNm("작업일지");
        requestParams.setUpNaviNo(0);
        requestParams.setUpNaviId("");
        requestParams.setNaviLevel((short) 1);
        requestParams.setNaviType("FOLDR");
        requestParams.setNaviFolderType("1");
        requestParams.setNaviFolderKind(smComCode.getAttrbtCd3());
        requestParams.setProperties(documentManageService.parseToPropertyCreate(properties, navId));
        requestParams.setPropertyData(propertyData);
        requestParams.setRgstrId(report.getRgstrId());
        requestParams.setDocNm(report.getReportNo()+".pdf");

        Map<String, String> newHeaders = Maps.newHashMap();
        newHeaders.put("x-auth", accessToken);

        log.info("updateDiskFileInfo: 작업일지 PDF 문서 및 속성 저장 param = {}", requestParams);
        List<DcStorageMain> createFileResultList = documentServiceClient.createFile(requestParams, pdfFile, newHeaders);


        // 작업일지에 docId 업데이트
        if(createFileResultList != null){
            DcStorageMain dcStorageMain= createFileResultList.get(0);
            result.put("cntrctNo", MapUtils.getString(resultMap, "cntrct_no"));
            result.put("dailyReportId", dailyReportId.toString());
            result.put("docId", dcStorageMain.getDocId());
        }
        return result;
    }

    // 통합문서관리의 속성 데이터 저장
    public List<DocumentForm.PropertyData> savePdfPropertyDataToDoc(List<CbgnPropertyDto> properties, CwDailyReport report) {
        log.info("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 properties = {}, cwDailyReport = {}", properties, report);
        List<DocumentForm.PropertyData> insertList = new ArrayList<>();
        try {

            for (CbgnPropertyDto property : properties) {
                String attrbtCd = property.getAttrbtCd();
                String apprvlStatsTxt = "A".equals(report.getApprvlStats()) ? "승인" : "반려";
                if (attrbtCd != null) {
                    String attrbtCntnts =
                            attrbtCd.equals("workReportNo")         ? report.getReportNo() :
                                    attrbtCd.equals("dailyReportDate")      ? report.getDailyReportDate() :
                                            attrbtCd.equals("title")                ? report.getTitle() :
                                                    attrbtCd.equals("apprvlStatsTxt")       ? apprvlStatsTxt :
                                                            null;

                    if (attrbtCntnts != null && !attrbtCntnts.isBlank()) {
                        DocumentForm.PropertyData row = new DocumentForm.PropertyData();
                        row.setAttrbtCd(attrbtCd);
                        row.setAttrbtCntnts(attrbtCntnts);
                        row.setRgstrId(report.getRgstrId());
                        row.setChgId(report.getChgId());

                        insertList.add(row);
                    }
                }
            }
            log.info("savePdfPropertyDataToDoc: 데이터 저장 결과 = {}", insertList);

        } catch (GaiaBizException e) {
            log.warn("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 중 오류 발생 메세지 = {}", e.getMessage());
            insertList = null;
        }
        return insertList;
    }

    // hwpx 문서에 매핑할 데이터 조회
    public Map<String, Object> selectDefaultData(CwDailyReport report) {
        log.info("selectDefaultData: 문서에 매핑할 데이터 조회 시작. report = {}", report);
        Map<String, Object> result = new HashMap<>();


        try {
            // 사용자 조회
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("cntrctNo", report.getCntrctNo());
            userMap.put("usrId", report.getApprvlReqId());
            Object userData = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectUsersByDepartment", userMap);
            result.put("user", userData);
            log.info("selectDefaultData: 부서별 사용자 조회 성공 {}", userData);

            // 직인 조회
            Map<String, Object> stampMap = new HashMap<>();
            stampMap.put("usrId", report.getApprvlReqId());
            stampMap.put("cntrctNo", report.getCntrctNo());
            List<Map<String, Object>> stmpList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectStmpInfoByCntrctNo", stampMap);
            result.put("stmps", stmpList);
            log.info("selectDefaultData: 작업일지 직인 조회 성공 - {}건", stmpList.size());

            // 기본 정보 조회
            Map<String, Object> infoMap = new HashMap<>();
            infoMap.put("cntrctNo", report.getCntrctNo());
            infoMap.put("dailyReportId", report.getDailyReportId());
            Map<String, Object> resultMap = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyReportBasicInfo", infoMap);

            // 액티비티 정보 조회
            String[] types = {"TD", "TM"};
            for (String type : types) {
                DailyreportMybatisParam.DailyreportFormTypeSelectInput param = new DailyreportMybatisParam.DailyreportFormTypeSelectInput();
                param.setCntrctNo(report.getCntrctNo());
                param.setDailyReportId(report.getDailyReportId());
                param.setWorkDtType(type);

                List<Map<String, Object>> part = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectActivityData", param);

                List<String> tmp = new ArrayList<>();
                for (Map<String, Object> row : part) {
                    String pathNm = String.valueOf(row.getOrDefault("path_nm", "")).trim();
                    String actNm  = String.valueOf(row.getOrDefault("activity_nm", "")).trim();
                    if (pathNm.isEmpty() && actNm.isEmpty()) continue;
                    tmp.add(pathNm + " | " + actNm);
                }

                // 배열로 변환 + trim (윈도우서버에서 newline 하기 위함)
                String[] activityArray = tmp.stream()
                        .map(String::trim)
                        .toArray(String[]::new);

                if ("TD".equals(type)) {
                    resultMap.put("activity_nm1", activityArray);
                } else { // "TM"
                    resultMap.put("activity_nm2", activityArray);
                }

            }

            result.put("dailyReportBasicInfoAndActivities", resultMap);
            log.info("selectDefaultData: 작업일지 기본 정보 및 액티비티 조회 성공");


            // 자원 조회 (M: 자재, L: 인력, E: 장비)
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("dailyReportId", report.getDailyReportId());
            for (String type : List.of("M", "L", "E")) {
                resMap.put("rsceTpCd", type);
                List<Map<String, Object>> list = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyReportResources", resMap);
                result.put(type.toLowerCase(), list);
                log.info("selectDefaultData: {} 조회 성공 - {}건", type, list.size());
            }

            // 공정사진 조회
            Map<String, Object> photoMap = new HashMap<>();
            photoMap.put("dailyReportId", report.getDailyReportId());
            List<Map<String, Object>> photoList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyReportPhotoInfo", photoMap);
            result.put("dailyReportPhotoInfo", photoList);
            log.info("selectDefaultData: 작업일지 공정사진 조회 성공 - {}건", photoList.size());
        } catch (GaiaBizException e) {
            log.error("selectDefaultData [데이터 조회 중 예외 발생] : {}", e.getMessage());
        }

        return result;
    }




    @Async("taskExecutor")
    // 승인 완료 후 전달 받은 daily report의 activity 를 pr_activity에 저장함
    // actual_bgn_date 가 있는 경우에만 저장함
    public void updatePrActivityFromDailyReport(CwDailyReport report) {
        if (report == null || report.getCntrctNo() == null || report.getDailyReportId() == null) {
            log.warn("updatePrActivityFromDailyReport: 데이터 누락: report 또는 필수 값이 null");
            throw new GaiaBizException(ErrorType.NO_DATA, "필수 데이터 미존재");
        }
        log.info("updatePrActivityFromDailyReport: prAcitivity 업데이트 진행 cntrctNo = {}, dailyReportId = {}", report.getCntrctNo(), report.getDailyReportId());


        DailyreportMybatisParam.DailyreportFormTypeSelectInput input = new DailyreportMybatisParam.DailyreportFormTypeSelectInput();
        input.setCntrctNo(report.getCntrctNo());
        input.setDailyReportId(report.getDailyReportId());
        input.setWorkDtType("TD");      // 금일
        input.setActualBgnDateYn("Y"); 	// 쿼리에 where 조건 추가 함수

        try {
            List<Map<String, Object>> mapList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectActivityData", input);
            mapList.forEach(row -> row.put("chg_id", report.getApprvlId()));

            if (mapList.isEmpty()) {
                log.info("updatePrActivityFromDailyReport: 조회된 데이터 없음");
            }
            if (ObjectUtils.isEmpty(mapList)) {
                log.info("업데이트할 activity 데이터가 존재하지 않음");
            }else {
                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.updateActualDate", mapList);
            }

        } catch (GaiaBizException e) {
            log.error("updatePrActivityFromDailyReport 중 오류 발생: {}", e.getMessage());
        }

    }

    /**
     * Pr Activity 수정
     *
     * @param dailyReportActivity
     */
//    @Transactional
//    public void updatePrActivity(List<CwDailyReportActivity> dailyReportActivity) {
//        dailyreportService.updatePrActivity(dailyReportActivity);
//    }


    @Async("taskExecutor")
    // 승인 완료 후 pr_activity 업데이트하여 P6 에 전달함
    public void updateP6FromPrActivity(String cntrctNo, String dailyReportId, String rgstrId) {
        log.info("updateP6FromPrActivity: P6 업데이트 진행");

        // 기본 파라미터 유효성 검사
        if (cntrctNo == null || cntrctNo.isEmpty() || dailyReportId == null || dailyReportId.isEmpty()) {
            log.warn("updateP6FromPrActivity: 필수 파라미터 누락 cntrctNo={}, dailyReportId={}", cntrctNo, dailyReportId);

            throw new GaiaBizException(ErrorType.NO_DATA, "필수 데이터 미존재");
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("login_id", rgstrId);                         // 필수
            requestBody.put("platform", platform);                                              // 필수
            requestBody.put("workType", "WBPR0080");

            Map<String, String> input = new HashMap<>();
            input.put("cntrctNo", cntrctNo);
            input.put("dailyReportId", dailyReportId);
            log.info("updateP6FromPrActivity: p6 에 전달할 activity 조회 진행 cntrctNo = {}, dailyReportId = {}", cntrctNo, dailyReportId);
            List<Map<String, Object>> mapList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectActivityListForP6", input);

            if (mapList == null || mapList.isEmpty()) {
                log.warn("updateP6FromPrActivity: 조회된 activity 데이터 없음");
                return;
            }

            // activityData 내부 Map
            Map<String, Object> activityData = new HashMap<>();

            // activities List
            List<Map<String, Object>> activities = new ArrayList<>();
            for (Map<String, Object> row : mapList) {
                Map<String, Object> activity = new HashMap<>();

                // 필수값 유효성 확인
                if (row.get("p6_project_obj_id") == null || row.get("actual_bgn_date") == null) {
                    log.warn("updateP6FromPrActivity: activityId 또는 actualStartDate 누락 row = {}", row);
                    continue;
                }

                activity.put("activityId", row.get("p6_activity_obj_id"));
                activity.put("actualStartDate", row.get("actual_bgn_date"));

                // actual_end_date가 있을 경우만 추가
                if (row.get("actual_end_date") != null && !row.get("actual_end_date").toString().isEmpty()) {
                    activity.put("actualFinishDate", row.get("actual_end_date").toString());
                }

                activities.add(activity);
            }

            // activities 없으면 P6 연동에서 오류 발생함
            if(!activities.isEmpty()) {
                activityData.put("activities", activities);

                requestBody.put("activityData", activityData);

                log.info("updateP6FromPrActivity: P6 에 데이터 전달 param = {}", requestBody);
                Map<String, Object> respMap = apiService.primaveraApiGetPost(requestBody).getDetails();
                LinkedHashMap<String, Object> rst = (LinkedHashMap<String, Object>) respMap.get("data");
                log.info("updateP6FromPrActivity: P6 에 데이터 전달 성공 result = {}", rst);
            }

        } catch (GaiaBizException e) {
            log.error("updateP6FromPrActivity: 예외 발생 {}", e.getMessage());
        }
    }

    @Async("taskExecutor")
    // 승인 취소 후 pr_activity 초기화 후 P6 에 전달
    public void updateP6FromCanceledDailyReport(Map<String, Object> paramMap) {
        log.info(" P6 액티비티 초기화 진행");

        // 기본 파라미터 유효성 검사
        if (paramMap.get("p6ActivityObjIdList") == null) {      
            log.warn("P6 초기화할 액티비티 데이터 없음");
            return;
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("login_id", paramMap.get("chgId"));  // 필수
            requestBody.put("platform", platform);                   // 필수
            requestBody.put("workType", "WBPR0080");

            // activities List
            List<Map<String, Object>> activities = new ArrayList<>();
            for (String p6ActivityObjId : (List<String>) paramMap.get("p6ActivityObjIdList")) {
                Map<String, Object> activity = new HashMap<>();

                // 필수값 유효성 확인
                if (p6ActivityObjId == null) {  
                    log.warn("p6_activity_obj_id 누락 ");
                    continue;
                }

                activity.put("activityId", p6ActivityObjId);
                activity.put("actualFinishDate", null); // 초기화

                activities.add(activity);
            }

            // activities 없으면 P6 연동에서 오류 발생함
            if(!activities.isEmpty()) {
                Map<String, Object> activityData = new HashMap<>();
                activityData.put("activities", activities);

                requestBody.put("activityData", activityData);

                log.info("P6에 데이터 전달 param = {}", requestBody);
                Map<String, Object> respMap = apiService.primaveraApiGetPost(requestBody).getDetails();
                LinkedHashMap<String, Object> rst = (LinkedHashMap<String, Object>) respMap.get("data");
                log.info("P6에 데이터 전달 성공 result = {}", rst);
            }

        } catch (GaiaBizException e) {
            log.error("updateP6FromCanceledDailyReport: 예외 발생 {}", e.getMessage());
        }
    }
}
