package kr.co.ideait.platform.gaiacairos.web.entrypoint.api;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ideait.iframework.FormatUtil;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.auth.AuthComponent;
import kr.co.ideait.platform.gaiacairos.comp.document.DocumentComponent;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.PaymentService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.SSOLoignTokenService;
import kr.co.ideait.platform.gaiacairos.core.config.security.TokenService;
import kr.co.ideait.platform.gaiacairos.core.constant.KeyConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.*;
import kr.co.ideait.platform.gaiacairos.web.entrypoint.auth.AuthPageController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/webApi/test")
public class TigerController extends AbstractController {

    @Autowired
    DocumentComponent documentComponent;

    @Autowired
    EurecaClient eurecaClient;

    @Autowired
    PaymentService paymentService;

    @Autowired
    ICubeClient iCubeClient;

    @Autowired
    DocumentServiceClient documentServiceClient;

    @Autowired
    UbiReportClient ubiReportClient;
    @Autowired
    InvokeServiceClient invokeServiceClient;


    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    SSOLoignTokenService ssoLoignTokenService;
    @Autowired
    TokenService tokenService;
    @Autowired
    CookieService cookieService;
    @Autowired
    AuthComponent authComponent;

    @PostConstruct
    public void init() {
//        final Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
//        final RequestMappingInfo result = handlerMethods.keySet().stream()
//                .filter(v -> v.getMatchingCondition(request) != null)
//                .findAny()
//                .orElseThrow(() -> new IllegalArgumentException("Invalid Argument"));

    }

    /**
     */
    @PostMapping("/make-login-token")
    @ApiResponse(description = "")
    public Result makeLoginToken(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pPlatform = MapUtils.getString(params, "platform");
        String tokenType = MapUtils.getString(params, "type");
        String id = MapUtils.getString(params, "id");
        String token;
        String cookieName;

        Map<String, String> sqlParams = Maps.newHashMap();
        sqlParams.put("loginId", id);
        SmUserInfo userInfo = authComponent.selectUser(sqlParams);
        String usrId = "";

        if ("oci".equals(pPlatform)) {
            usrId = userInfo.getOciUsrId();

            if ("old".equals(tokenType)) {
                cookieName = KeyConstants.AUTH_KEY_OLD;
                token = ssoLoignTokenService.makeLoginTokenOld(usrId);
            } else {
                cookieName = KeyConstants.AUTH_KEY_NEW;
                token = ssoLoignTokenService.makeLoginTokenNew(Duration.ofMillis(6000000), usrId);
            }
        } else {
            usrId = userInfo.getNcpUsrId();
            cookieName = KeyConstants.AUTH_KEY_NCP;
            token = ssoLoignTokenService.makeLoginTokenNew(Duration.ofMillis(6000000), usrId);
        }


        log.info("params : {} token: {}", params, token);
        cookieService.setHttpOnlyCookie(response, cookieName, token);

//        AuthPageController authPageController = (AuthPageController)applicationContext.getBean("authPageController");
//        if ("oci".equals(platform)) {
//            authPageController.newSsoLogin("", response, request);
//        } else {
//            authPageController.ncpSsoLogin("", response, request);
//        }

        return Result.ok();
    }

    /**
     */
    @GetMapping("/user-info")
    @ApiResponse(description = "문서 삭제")
    @Description(name = "문서 삭제", description = "문서 삭제", type = Description.TYPE.MEHTOD)
    public Result auth(@RequestParam Map<String, Object> params, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(true);
        log.info("userInfo : {}", session.getAttribute("userInfo"));

        String acecssToken = invokeServiceClient.generateSSOToken();
        log.info("acecssToken : {}", acecssToken);

        UserAuth userAuthentication = tokenService.parse(acecssToken);
        log.info("userAuthentication : {}", userAuthentication);

        return Result.ok();
    }

    /**
     * 문서 삭제
     */
    @PostMapping("/delete-doc")
    @ApiResponse(description = "문서 삭제")
    @Description(name = "문서 삭제", description = "문서 삭제", type = Description.TYPE.MEHTOD)
    public Result removeDocument( @RequestBody Map<String, Object> params, HttpServletRequest request) throws Exception {
        log.info("params : {}", params);

        return documentServiceClient.removeDocument(params);
    }

    /**
     * 통합검색
     */
    @GetMapping("/search")
    @ApiResponse(description = "통합검색")
    @Description(name = "통합검색", description = "통합검색", type = Description.TYPE.MEHTOD)
    public Result search( @RequestBody Map<String, Object> params, HttpServletRequest request) throws Exception {
        log.info("params : {}", params);

        params.put("refSysKey", "1");

        return Result.ok(documentComponent.search(params));
    }

    @PostMapping("/result")
    public Result testResult(@RequestParam Map<String, String> requestParams) {
        requestParams.put("parentoucode", "0000000");
        requestParams.put("orderNm", "1");

        return new Result();
    }

    @PostMapping("/test")
    public void test(@RequestBody Map<String, String> requestParams, HttpServletRequest request) throws IOException {
        requestParams.put("parentoucode", "0000000");
        requestParams.put("orderNm", "1");

//        documentServiceClient.test(requestParams, null);
    }

    @GetMapping("/create-doc")
    public void createDoc(@RequestPart(value = "docData") DocumentForm.DocCreateEx doc, @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        List<DcStorageMain> resultForDocumentService = documentServiceClient.createFile(doc, files);
        log.info("createDoc:  {}", resultForDocumentService);
    }

    @GetMapping("/eureca/1")
    public void eureca1(@RequestParam String cntrctChgId, @RequestParam Long payprceTmnum) {

        Map<String, Object> eurecaParams = paymentService.getPrgpymntDtlsForEureca(cntrctChgId, payprceTmnum);

        // 기성내역서(작성요청) 유레카 전송
        Map<String, Object> eurecaResponse = eurecaClient.registerPrgpymntDtls(eurecaParams);

        log.info("eureca response: {}", eurecaResponse);
    }

    /**
     * 계약내역서(작성완료) 조회
     * @param cntrctNo
     * @param cntrctChgId
     * @param lngtmCntnuCntrctOrd
     * @param cntrctChgOrd
     */
    @GetMapping("/eureca/retrieveCntrDtls")
    public Result retrieveCntrDtls(@RequestParam String cntrctNo, @RequestParam String cntrctChgId, @RequestParam Long lngtmCntnuCntrctOrd, @RequestParam Long cntrctChgOrd) {
        Map<String, Object> eurecaParams = Maps.newHashMap();
        eurecaParams.put("cntrctNo", cntrctNo); // 계약번호
        eurecaParams.put("cntrctChgId", cntrctChgId); // 계약변경ID
        eurecaParams.put("lngtmCntnuCntrctOrd", lngtmCntnuCntrctOrd); // 장기계속계약차수
        eurecaParams.put("cntrctChgOrd", cntrctChgOrd); // 계약변경차수

        Map<String, Object> eurecaResponse = eurecaClient.retrieveCntrDtls(eurecaParams);

        log.info("eureca response: {}", eurecaResponse);

        return Result.ok().put("response", eurecaResponse);
    }

    /**
     * 내역서 산출 상세 조회
     * @param cntrctChgId
     * @param dtlsSn
     */
    @GetMapping("/eureca/retrieveSpcsCalcDtls")
    public Result retrieveSpcsCalcDtls(@RequestParam String cntrctChgId, @RequestParam Long dtlsSn) {
        Map<String, Object> eurecaParams = Maps.newHashMap();
        eurecaParams.put("cntrctChgId", cntrctChgId); // 계약변경ID
        eurecaParams.put("dtlsSn", dtlsSn); // 내역순번

        Map<String, Object> eurecaResponse = eurecaClient.retrieveSpcsCalcDtls(eurecaParams);

        log.info("eureca response: {}", eurecaResponse);

        return Result.ok().put("response", eurecaResponse);
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> sampleApi(@RequestParam("files") List<MultipartFile> files, @RequestParam Map params, HttpServletRequest request) throws IOException {
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        log.info("params: {} files: {}, body: {}", params, files, body);


        for (MultipartFile file : files) {
            Path filePath = Path.of("D:/", file.getOriginalFilename());

            try {
                file.transferTo(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "success");

        log.info("result: {}", result);

        return ResponseEntity.ok(result);
    }

    //    @PostMapping(value = "/ifCairosIcube001", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PostMapping(value = "/ifCairosIcube001", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/ifCairosIcube001")
    public ResponseEntity<Map<String, Object>> ifCairosIcube001(@RequestBody Map<String, Object> params, HttpServletRequest request) throws IOException, InterruptedException {
//        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//        log.info("body: {}", body);

        HashMap<String, Object> reqParams = new HashMap<>(params);

        Map<String, Object> result = null;

        String[][] users = {
//            {"ideait", "idea2357!!", "entrprsHref"}
//            ,
            {"tigerjun80", "Taichi*)9080", "gnrlHref"}
            ,
            {"jgood7070", "jgood7070!", "gnrlHref"}
            ,
            {"goatmxj48", "dkdleldj!23", "gnrlHref"}
            ,
            {"thkim610", "thk610916!", "gnrlHref"}
            ,
            {"nrwood", "S6JR8va96@*m", "gnrlHref"}
            ,
            {"wlgus8846", "qwer1234!!", "gnrlHref"}
            ,
            {"id2357", "IDEA1234!", "gnrlHref"}
//            ,
//            {"wjddns971201", "leejw12!@", "gnrlHref"}
//            ,
//            {"lgw198", "1q2w3e4r!@#", "gnrlHref"}
//            ,
//            {"goatmx", "dkdleld", "gnrlHref"}
        };

        for (int i = 0, length = users.length; i < length; i++) {
            for (int y = 0; y < 1; y++) {
                String[] user = users[i];
                reqParams.put("usr_id", user[0]);
                reqParams.put("usr_pw", user[1]);
                reqParams.put("login_usr_ty", user[2]);
                reqParams.put("gdoc_ttl", String.format("%s_%s_%s", user[0], i, FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss")) );

                result = documentComponent.sendDoc24(reqParams);
                log.info("use: {} result: {}", user[0], result);
//                Thread.sleep(3000);
            }
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/ifCairosIcube002")
    public ResponseEntity<Map<String, Object>> ifCairosIcube002() {
        HashMap<String, String> reqParams = new HashMap<>();
        // 로그인 사용자 유형
        // 필수: N
        // 유형: 2자리 코드
        // 설명: 01: 대표사용자(디폴트), 02: 업무관리자, 03: 부서(일반업무) 사용자
        reqParams.put("LOG_USER_TYPE", "");

        // 로그인 방법
        // 필수: N
        // 유형: 영문코드
        // 설명: IDPW: 계정입력(디폴트) 이 외 옵션은 추후 협의
        reqParams.put("LOG_VERIFY_TYPE", "");

        // 아이디
        // 필수: 로그인 방법이 IDPW일 경우 필수
        // 유형: 문자열
        // 설명: 문서24 로그인 정보
        reqParams.put("USER_ID", "");

        // 패스워드
        // 필수: 로그인 방법이 IDPW일 경우 필수
        // 유형: 문자열 (암호화  협의)
        // 설명: 문서24 로그인 정보
        reqParams.put("USER_PASSWORD", "");

        // 기준일자
        // 필수: N
        // 유형: 날짜(YYYY-MM-DD)
        // 설명: 접수일자 기준 특정 기준일 이후 데이터만 목록으로 제공 (미입력시 전체)
        reqParams.put("RETRIVE_DATE_AFTER", "");

        Map<String, Object> result = iCubeClient.ifCairosIcube002(reqParams);

        log.info("result: {}", result);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/ifDocu24003")
    public ResponseEntity<Map<String, Object>> ifDocu24003(
            @RequestParam("REQUEST_CODE") String requestCode, // 요청 키
            @RequestParam("SUCCESS_YN") String successYn, // 성공여부
            @RequestParam("FAILURE_TYPE_CODE") String failureTypeCode, // 실패코드
            @RequestParam("FAILURE_REASON_DESC") String failureReasonDesc, // 실패사유
            @RequestParam("GDOC_NUMBER") String gdocNumber // 문서번호

    ) {
        log.info("requestCode: {} successYn: {} failureTypeCode: {} failureReasonDesc: {} gdocNumber: {}", requestCode, successYn, failureTypeCode, failureReasonDesc, gdocNumber);
        HashMap<String, Object> result = new HashMap<>();
        log.info("result: {}", result);

        return ResponseEntity.ok(result);
    }
}
