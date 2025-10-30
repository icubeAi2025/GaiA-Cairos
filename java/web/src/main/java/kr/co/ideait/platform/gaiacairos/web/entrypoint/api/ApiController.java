package kr.co.ideait.platform.gaiacairos.web.entrypoint.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.iframework.FormatUtil;
import kr.co.ideait.iframework.file.CustomMultipartFile;
import kr.co.ideait.platform.gaiacairos.comp.chaggonggye.helper.ChagGongGyeDocHelper;
import kr.co.ideait.platform.gaiacairos.comp.chaggonggye.service.ChagGongGyeDocService;
import kr.co.ideait.platform.gaiacairos.comp.construction.ChiefInspectionReportComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.DailyreportComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.DailyreportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.InspectionreportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.MainmtrlReqfrmService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.QualityinspectionService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ProjectService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.PaymentService;
import kr.co.ideait.platform.gaiacairos.comp.resources.ResourceComponent;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetyDiaryIntegrationService;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetyDiaryService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.config.property.EurecaProp;
import kr.co.ideait.platform.gaiacairos.core.config.wrapper.MultipartFileWrapper;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.util.BizServiceInvoker;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tika.utils.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;


/**
 * GAIA CMIS API 통신용
 */
@Slf4j
@RestController
@RequestMapping({"/webApi", "/interface"})
@RequiredArgsConstructor
public class ApiController extends AbstractController {

    private final ObjectMapper objectMapper;

    private final SystemLogComponent systemLogComponent;

    private final ResourceComponent resourceComponent;

//	private final TokenService tokenService;

    private final ProjectService projectService;

    private final PaymentService paymentService;

    private final ChagGongGyeDocService chagGongGyeDocService;

    private final BizServiceInvoker bizServiceInvoker;

    private final EurecaProp eurecaProp;

    private final ChagGongGyeDocHelper chagGongGyeDocHelper;

    private final DailyreportService dailyreportService;

    private final DailyreportComponent dailyreportComponent;

    private final ChiefInspectionReportComponent chiefInspectionReportComponent;

    private final InspectionreportService inspectionreportService;

    private final QualityinspectionService qualityService;

    private final SafetyDiaryIntegrationService safetyDiaryIntegrationService;

    private final MainmtrlReqfrmService mainmtrlReqfrmService;

    String eureca2CairosKey;

    @PostConstruct
    public void init(){
        eureca2CairosKey = eurecaProp.getEureca2CairosKey();
    }


//	@PostMapping("/oAuthToken")
//    public String getToken(@RequestBody Map reqBody) {
//		
//        log.debug("------------------------------------------------------------------------");
//        log.debug("------------------------------------------------------------------------");
//        log.debug("id 		: >>>>> " + reqBody.get("id"));
//        log.debug("login_id : >>>>> " + reqBody.get("login_id"));
//        log.debug("reqBody : >>>>> " + reqBody.toString());
//        log.debug("------------------------------------------------------------------------");
//        log.debug("------------------------------------------------------------------------");
//
//        return tokenService.apiGenerate((String) reqBody.get("id"), (String) reqBody.get("login_id"));
//    }

	/**
     * GAIA와 CAIROS간 API 호출
     * @param 
     * @return
     */
	@PostMapping(value = "/retrieveApi/{transactionId}",  produces="application/json", consumes="application/json")
    public ResponseEntity<Map<String, Object>> getGaiaRetrieveApi(@PathVariable("transactionId") String transactionId, @RequestBody Map reqBody) {

        Map<String, Object> result = bizServiceInvoker.invoke(transactionId, reqBody);

        return ResponseEntity.ok(result);
    }

    /**
     * GAIA와 CAIROS간 API 호출 (MultipartFile)
     * @param reqBodyJson JSON 문자열
     * @return
     */
    @PostMapping(
            value = "/retrieveApi/{transactionId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> getGaiaRetrieveApi(
            @PathVariable("transactionId") String transactionId,
            @RequestPart(value = "reqBody") String reqBodyJson,
            @RequestPart(value = "_fileInfo_") String fileInfoJson,
            HttpServletRequest request
    ) throws JsonProcessingException {
        Map<String, Object> reqBody = objectMapper.readValue(reqBodyJson, new TypeReference<>() {});
        Map<String, String> fileInfo = objectMapper.readValue(fileInfoJson, new TypeReference<>() {});

        try {
            Collection<Part> parts = request.getParts();

            for (Part part : parts) {
                String partName = part.getName();

                log.info("파라미터명:{}, contentType: {}, size: {}bytes Content-Disposition: {}",
                        partName, part.getContentType(), part.getSize(), part.getHeader("Content-Disposition"));

                String contentDisposition = part.getHeader("Content-Disposition");

                if (contentDisposition != null ? contentDisposition.contains("filename") : false && part.getSize() > 0) {
                    String fileName = URLDecoder.decode(part.getSubmittedFileName());
                    String path = String.format("%s/%s", tempPath, fileName);
                    part.write(path);

                    try {
                        MultipartFile wrappedFile = new MultipartFileWrapper(
                                new CustomMultipartFile(FileService.fileToByteArrayWithNIO(path)),
                                fileName,
                                part.getName(),
                                part.getContentType()
                        );

                        if ("ARRAY".equals(MapUtils.getString(fileInfo, partName))) {
                            List<MultipartFile> files = (List<MultipartFile>) reqBody.getOrDefault(partName, new ArrayList<>());
                            files.add(wrappedFile);
                            reqBody.put(partName, files);
                        } else {
                            reqBody.put(partName, wrappedFile);
                        }
                    } finally {
                        part.delete();
                    }

                } else {
                    log.info("name: {}, value:{}", part.getName(), request.getParameter(part.getName()));
                }
            }

        } catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            log.info("reqBody 최종: {}", reqBody);
        }

        Map<String, Object> result = bizServiceInvoker.invoke(transactionId, reqBody);
        return ResponseEntity.ok(result);
    }


    /* ==================================================================================================================
     *
     * EURECA -> GAIA/CAIROS
     *
     * ==================================================================================================================
     */

    /**
     * 기성내역서(갱신기성)
     * @param reqParams
     * @return
     */
	@GetMapping("/eureca/retrieveRePrgpymntDtls")
    public ResponseEntity<Map<String, Object>> retrieveRePrgpymntDtls(
            @RequestParam("serviceKey") String serviceKey
            , @RequestParam("cntrctChgId") String cntrctChgId //변경계약ID
            , @RequestParam("payprceTmnum") Long payprceTmnum //기성회차
            , @RequestParam Map<String, Object> reqParams
    ) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> header = new HashMap<String, Object>();
        Map<String, Object> body = new HashMap<String, Object>();

        log.info("retrieveRePrgpymntDtls() serviceKey: {} cntrctChgId: {} payprceTmnum: {} reqParams: {}", serviceKey, cntrctChgId, payprceTmnum, reqParams);

        try {
            if (!this.eureca2CairosKey.equals(serviceKey)) {
                throw new BizException("서비스키가 유효하지 않습니다.");
            }

            body = paymentService.getPrgpymntDtlsForEureca(cntrctChgId, payprceTmnum);
            header.put("resultCode", "00");
        } catch (BizException bizException) {
            header.put("resultCode", "01");
            header.put("resultMsg", bizException.getMessage());
        } catch (Exception exception) {
            header.put("resultCode", "04");
            header.put("resultMsg", exception.getMessage());
        } finally {
            response.put("header", header);
            response.put("body", body);
            result.put("response", response);

            log.info("result : {}", result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 내역서접근권한체크
     * @param reqParams
     * @return
     */
	@GetMapping("/eureca/retrieveAccssAuthChk")
    public ResponseEntity<Map<String, Object>> retrieveAccssAuthChk(
            @RequestParam("serviceKey") String serviceKey
            //변경계약번호
            , @RequestParam(value = "cntrctChgId") String cntrctChgId
            , @RequestParam("payprceTmnum") Long payprceTmnum
            //사용자ID
            , @RequestParam("usrId") String usrId
            //권한. R(읽기), U(수정)
            , @RequestParam("auth") String auth
            , @RequestParam(value = "referer", required = false) String referer
            , @RequestParam Map<String, Object> reqParams
    ) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> header = new HashMap<String, Object>();
        Map<String, Object> body = new HashMap<String, Object>();
        UserAuth userAuth = UserAuth.get(true);
        String loginUserId = null;
        if(userAuth != null) {
            loginUserId = userAuth.getUsrId();
        }

        log.info("retrieveAccssAuthChk() serviceKey: {} cntrctChgId: {} payprceTmnum: {} usrId: {} auth: {} referer: {} reqParams: {}", serviceKey, cntrctChgId, payprceTmnum, usrId, auth, referer, reqParams);

        try {
            if (!this.eureca2CairosKey.equals(serviceKey)) {
                throw new BizException("서비스키가 유효하지 않습니다.");
            }

            body = paymentService.retrieveAccssAuthChk(cntrctChgId, payprceTmnum, usrId, auth, loginUserId);
            header.put("resultCode", "00");
        } catch (BizException bizException) {
            header.put("resultCode", "01");
            header.put("resultMsg", bizException.getMessage());
        } catch (Exception exception) {
            header.put("resultCode", "04");
            header.put("resultMsg", exception.getMessage());
        } finally {
            response.put("header", header);
            response.put("body", body);
            result.put("response", response);

            log.info("result : {}", result);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/pay-detail")
    public ResponseEntity<Map<String, Object>> getPayDetail(
            @RequestParam("serviceKey") String serviceKey
            , @RequestParam("cntrctChgId") String cntrctChgId //변경계약ID
            , @RequestParam("payprceTmnum") Long payprceTmnum //기성회차
            , @RequestParam Map<String, Object> reqParams
    ) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> header = new HashMap<String, Object>();
        Map<String, Object> body = new HashMap<String, Object>();

        log.info("retrieveRePrgpymntDtls() serviceKey: {} cntrctChgId: {} payprceTmnum: {} reqParams: {}", serviceKey, cntrctChgId, payprceTmnum, reqParams);

        try {
            if (!this.eureca2CairosKey.equals(serviceKey)) {
                throw new BizException("서비스키가 유효하지 않습니다.");
            }

            body = paymentService.tempPrgpymntDtlsForEureca(cntrctChgId, payprceTmnum);
            header.put("resultCode", "00");
        } catch (BizException bizException) {
            header.put("resultCode", "01");
            header.put("resultMsg", bizException.getMessage());
        } catch (Exception exception) {
            header.put("resultCode", "04");
            header.put("resultMsg", exception.getMessage());
        } finally {
            response.put("header", header);
            response.put("body", body);
            result.put("response", response);

            log.info("result : {}", result);
        }

        return ResponseEntity.ok(result);
    }

    /* ==================================================================================================================
     *
     * 문서 24 -> GAIA/CAIROS
     *
     * ==================================================================================================================
     */

    /**
     * 문서24 - 수요기관 검색
     * @param keyword
     * @param currentPage
     * @return
     */
    @Deprecated
    @GetMapping("/resource/doc24-search-organization")
    public ResponseEntity<Map<String, Object>> searchDoc24Organization(@RequestParam("keyword") String keyword, @RequestParam("currentPage") Integer currentPage) {

        Map<String, Object> result = resourceComponent.searchDoc24Organization(keyword, currentPage);

        log.info("doc24 searchDoc24Organization response: {}", result);

        return ResponseEntity.ok(result);
    }

    /**
     * 문서24 - 수요기관 트리 목록 조회
     * @param parentCode
     * @return
     */
    @Deprecated
    @GetMapping("/resource/doc24-organization")
    public ResponseEntity<List<Map<String, Object>>> doc24OrganizationByTree(@RequestParam(value = "parentCode", required = false) String parentCode, @RequestParam(value = "searchTreeOrgNm", required = false) String searchTreeOrgNm) {
        if (StringUtils.isEmpty(parentCode) && StringUtils.isEmpty(searchTreeOrgNm)) {
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA);
        }

        List<Map<String, Object>> result = resourceComponent.getDoc24TreeOrganization(parentCode, searchTreeOrgNm);

        log.info("doc24 getDoc24TreeOrganization response: {}", result);

        return ResponseEntity.ok(result);
    }

    /**
     * 공문 임시저장 결과 응답 callback
     * @param params
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @Deprecated
//    @PostMapping(value = "/gdoc-send-result", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Map<String, Object>> gdocSendResult(@RequestPart Map params, @RequestPart(required = false) List<MultipartFile> files, HttpServletRequest request) throws JsonProcessingException {
//    public ResponseEntity<Map<String, Object>> gdocSendResult(@RequestPart @RequestBody Map params, HttpServletRequest request) throws JsonProcessingException {
    @PostMapping(value = "/gdoc-send-result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> gdocSendResult(@RequestBody Map params, HttpServletRequest request) {
        log.info("params params: {} files: {}", params);

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            String serviceKey = MapUtils.getString(params, "req_cd");

            Log.SmApiLogDto apiLogDto = new Log.SmApiLogDto();
            apiLogDto.setApiId("IF_ICUBE_CAIROS_001");
            apiLogDto.setApiType("IN");
            apiLogDto.setServiceType("DOC24_SEND");
            apiLogDto.setServiceUuid(serviceKey);
            apiLogDto.setSourceSystemCode("iCube");
            apiLogDto.setTargetSystemCode("KAIROS");
            apiLogDto.setResultCode(200);
            apiLogDto.setReqMethod("POST");

            Map<String, String> reqHeaders = Maps.newHashMap();

            Enumeration<String> headers = request.getHeaderNames();

            if (headers != null) {
                while (headers.hasMoreElements()) {
                    String key = headers.nextElement();
                    reqHeaders.put(key, request.getHeader(key));
                }
            }

            apiLogDto.setReqHeader(objectMapper.writeValueAsString(reqHeaders));
            apiLogDto.setReqData(objectMapper.writeValueAsString(params));
            apiLogDto.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
            apiLogDto.setReqData(objectMapper.writeValueAsString(params));
            apiLogDto.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));

            if ("S".equals(params.get("res_stats"))) {
                apiLogDto.setErrorYn("N");
            } else {
                apiLogDto.setErrorYn("Y");
                apiLogDto.setErrorReason(String.format("실패코드: %s%n실패사유: %s", params.get("fail_cd"), params.get("res_dscrpt")));
            }

            apiLogDto.setRgstrId("iCube");
            apiLogDto.setChgId("iCube");

            systemLogComponent.addApiLog(apiLogDto);

            result.put("res_stats", "Y");
        } catch (GaiaBizException | JsonProcessingException bizException) {
            result.put("res_stats", "N");
        }

        return ResponseEntity.ok(result);
    }

    /* ==================================================================================================================
     *
     * 착공계 서비스 -> GAIA/CAIROS
     *
     * ==================================================================================================================
     */
    
    @PostMapping(value = "/chagGongGyeDoc/callback-result", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> sampleApi(@RequestParam("hwp_doc") List<MultipartFile> hwpFile, @RequestParam("pdf_doc") List<MultipartFile> pdfFile, @RequestParam("res_dtl_lst") String res_dtl_lst, @RequestParam("req_cd") String req_cd, HttpServletRequest request) throws IOException {
    	
        if (hwpFile == null || hwpFile.isEmpty()) {
            log.info("hwpFile is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "hwpFile must not be null or empty");
        }

        if (pdfFile == null || pdfFile.isEmpty()) {
            log.info("pdfFile is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "pdfFile must not be null or empty");
        }

        if (req_cd == null || req_cd.trim().isEmpty()) {
            log.info("req_cd is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "req_cd must not be null or empty");
        }
        
        
        String[] keyParam = req_cd.split("~");

        Map<String, Object> result = chagGongGyeDocService.updateDiskFileInfo(hwpFile, pdfFile, keyParam[0], keyParam[1]);
        
        log.info("naviId: {}", keyParam[0]);
        log.info("docId: {}", keyParam[1]);

    	log.info("req_cd: {}", req_cd);
    	log.info("params: {}", res_dtl_lst);
    	log.info("hwpFile: {}", hwpFile);
    	log.info("pdfFile: {}", pdfFile);

        // 병합 대상 확인 후 병합 트리거
        List<Map<String, Object>> mergeAttachments = chagGongGyeDocService.selectMergeAttachmentList(keyParam[1]);  // docId
        if (!mergeAttachments.isEmpty()) {
            result = chagGongGyeDocHelper.mergeChagGongGyeDoc(pdfFile, mergeAttachments, keyParam[0], keyParam[1]);  // naviId, docId
            log.info("PDF 병합 요청 수행됨");
        } else {
            log.info("병합 대상 첨부파일 없음");
        }

        log.info("result: {}", result);

        return ResponseEntity.ok(result);
    }

    /**
     * 착공계 pdf 병합 문서 처리
     * @param pdfFile
     * @param req_cd
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/chagGongGyeDoc/merge-result", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> mergeResultApi(@RequestParam("pdf_doc") List<MultipartFile> pdfFile, @RequestParam("req_cd") String req_cd, HttpServletRequest request) throws IOException {

        if (pdfFile == null || pdfFile.isEmpty()) {
            log.info("pdfFile is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "pdfFile must not be null or empty");
        }

        if (req_cd == null || req_cd.trim().isEmpty()) {
            log.info("req_cd is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "req_cd must not be null or empty");
        }

        String[] keyParam = req_cd.split("~");

        Map<String, Object> result = chagGongGyeDocService.updateDiskFileInfo(pdfFile, keyParam[0], keyParam[1]);

        log.info("naviId: {}", keyParam[0]);
        log.info("docId: {}", keyParam[1]);

        log.info("req_cd: {}", req_cd);
        log.info("pdfFile: {}", pdfFile);

        log.info("result: {}", result);

        return ResponseEntity.ok(result);
    }


    // 책임감리일지 PDF 문서화 콜백 처리
    @PostMapping(value = "/chief/callbackResult", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> chiefReportPdfResult(@RequestParam Map params, @RequestParam("pdf_doc") List<MultipartFile> pdfFile, @RequestParam("req_cd") String req_cd) throws IOException {
        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "pdfFile must not be null or empty");
        }

        if (req_cd == null || req_cd.trim().isEmpty()) {
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "req_cd must not be null or empty");
        }

        // DISK 저장 및 dc_storage_main 업데이트
        Map<String, String> result = chiefInspectionReportComponent.chiefReportPdfResult(pdfFile, NumberUtils.toLong(req_cd), MapUtils.getString(params, "x-auth"));

        return ResponseEntity.ok(result);
    }

    // 작업일지 PDF 문서화 콜백 처리
    @PostMapping(value = "/dailyreportDoc/callback-result", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<Map<String, String>> dailyreportPdfResultApi(@RequestParam Map params, @RequestParam("pdf_doc") List<MultipartFile> pdfFile, @RequestParam("dailyReportId") String dailyReportId) throws IOException {

        log.info("params: {}", params);

        if (pdfFile == null || pdfFile.isEmpty()) {
            log.info("pdfFile is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "pdfFile must not be null or empty");
        }

        if (dailyReportId == null || dailyReportId.trim().isEmpty()) {
            log.info("req_cd is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "req_cd must not be null or empty");
        }

        // DISK 저장 및 dc_storage_main 업데이트
        Map<String, String> result = dailyreportService.updateDiskFileInfo(pdfFile, NumberUtils.toLong(dailyReportId), MapUtils.getString(params, "x-auth"));


        // DailyReport docId 업데이트
        dailyreportService.updateDailyReportDocId(result.get("cntrctNo"), result.get("dailyReportId"), result.get("docId"));


        log.info("result: {}", result);

        return ResponseEntity.ok(result);
    }

    // 감리일지 PDF 문서화 콜백 처리
    @PostMapping(value = "/inspectionReportDoc/callback-result", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> inspectionReportPdfResultApi(@RequestParam Map params
//            , @RequestParam("hwp_doc") List<MultipartFile> hwpFile
            , @RequestParam("pdf_doc") List<MultipartFile> pdfFile
//            , @RequestParam("res_dtl_lst") String res_dtl_lst
            , @RequestParam("req_cd") String req_cd
//            , HttpServletRequest request
    ) throws IOException {
       
//        if (hwpFile == null || hwpFile.isEmpty()) {
//            log.info("hwpFile is null or empty");
//            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "hwpFile must not be null or empty");
//        }

        if (pdfFile == null || pdfFile.isEmpty()) {
            log.info("pdfFile is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "pdfFile must not be null or empty");
        }

        if (req_cd == null || req_cd.trim().isEmpty()) {
            log.info("req_cd is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "req_cd must not be null or empty");
        }

        // DISK 저장 및 dc_storage_main 업데이트
        Map<String, String> result = inspectionreportService.updateDiskFileInfo(pdfFile, NumberUtils.toLong(req_cd), MapUtils.getString(params, "x-auth"));

        // DailyReport docId 업데이트
        inspectionreportService.updateInspectionReportDocId(result.get("cntrctNo"), result.get("dailyReportId"), result.get("docId"));

        return ResponseEntity.ok(result);
    }

    // 품질검측 PDF 문서화 콜백 처리
    @PostMapping(value = "/qualityReportDoc/callback-result", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<Map<String, String>> qualityPdfResultApi(@RequestParam Map params, @RequestParam("pdf_doc") List<MultipartFile> pdfFile, @RequestParam("qltyIspId") String qltyIspId) throws IOException {

        log.info("params: {}", params);

        if (pdfFile == null || pdfFile.isEmpty()) {
            log.info("pdfFile is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "pdfFile must not be null or empty");
        }

        if (qltyIspId == null || qltyIspId.trim().isEmpty()) {
            log.info("req_cd is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "req_cd must not be null or empty");
        }

        // DISK 저장 및 dc_storage_main 업데이트
        Map<String, String> result = qualityService.updateDiskFileInfo(pdfFile, qltyIspId, MapUtils.getString(params, "x-auth"));


        // qualityinspection docId 업데이트
        qualityService.updateQualityDocId(result.get("cntrctNo"), result.get("qltyIspId"), result.get("docId"));


        log.info("result: {}", result);

        return ResponseEntity.ok(result);
    }

    // 안전일지 PDF 문서화 콜백 처리
    @PostMapping(value = "/safetyDiaryDoc/callback-result", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> safetyDiaryPdfResultApi(
            @RequestParam Map params,
            @RequestParam("pdf_doc") List<MultipartFile> pdfFile,
            @RequestParam("safetyDiaryId") String safetyDiaryId) throws IOException {
        log.info("/safetyDiaryDoc/callback-result START !!");
        log.info("params: {}", params);

        if (pdfFile == null || pdfFile.isEmpty()) {
            log.info("pdfFile is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "pdfFile must not be null or empty");
        }

        if (StringUtils.isEmpty(safetyDiaryId)) {
            log.info("safetyDiaryId is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "req_cd must not be null or empty");
        }

        // 1. DISK 저장 및 dc_storage_main 업데이트
        Map<String, String> result = safetyDiaryIntegrationService.updateDiskFileInfo(pdfFile, safetyDiaryId, MapUtils.getString(params, "x-auth"));


        // 2. 안전일지 docId 업데이트
        safetyDiaryIntegrationService.updateSafetyDiaryDocId(result);


        log.info("result: {}", result);

        return ResponseEntity.ok(result);
    }

    // 주요자재검수 요청서 PDF 문서화 콜백 처리
    @PostMapping(value = "/mainmtrlReportDoc/callback-result", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> mainmtrlPdfResultApi(@RequestParam Map params, @RequestParam("pdf_doc") List<MultipartFile> pdfFile, @RequestParam("reqfrmNo") String reqfrmNo) throws IOException {

        log.info("params: {}", params);

        if (pdfFile == null || pdfFile.isEmpty()) {
            log.info("pdfFile is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "pdfFile must not be null or empty");
        }

        if (reqfrmNo == null || reqfrmNo.trim().isEmpty()) {
            log.info("req_cd is null or empty");
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "req_cd must not be null or empty");
        }

        // DISK 저장 및 dc_storage_main 업데이트
        Map<String, String> result = mainmtrlReqfrmService.updateDiskFileInfo(pdfFile, reqfrmNo, MapUtils.getString(params, "x-auth"));
        log.info("result: {}", result);

        try {
            // 1초 정도 딜레이 — 상황에 맞게 조정
            Thread.sleep(1000);

            // mainmtrlReqfrm docId 업데이트
            mainmtrlReqfrmService.updateMainmtrlDocId(result.get("cntrctNo"), result.get("reqfrmNo"), result.get("docId"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("mainmtrlReqfrm docId 업데이트 중 인터럽트 발생", e);
        }

        return ResponseEntity.ok(result);
    }
}
