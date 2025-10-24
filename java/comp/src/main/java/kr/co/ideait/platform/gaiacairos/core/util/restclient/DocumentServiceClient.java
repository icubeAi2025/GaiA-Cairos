package kr.co.ideait.platform.gaiacairos.core.util.restclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractBase;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractClient;
import kr.co.ideait.platform.gaiacairos.core.config.property.DocumentServiceProp;
import kr.co.ideait.platform.gaiacairos.core.config.property.EapprovalServiceProp;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie.CookieVO;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentServiceClient extends AbstractClient {

    private final RestClientUtil restClientUtil;

    private final DocumentServiceProp documentServiceProp;

    private final DocumentService documentService;


    String domain;
    String serviceId;

    @Autowired
    private EapprovalServiceProp eapprovalServiceProp;

    private static final String LOCAL_GAIA_DOMAIN = "http://localhost:8089";
    private static final String DEV_GAIA_DOMAIN = "http://dev.idea-platform.net:8089";
    private static final String STG_GAIA_DOMAIN = "http://stg.idea-platform.net:8089";
    private static final String PROD_GAIA_DOMAIN = "https://www.idea-platform.net:8101";

    private static final String LOCAL_PGAIA_DOMAIN = "http://localhost:8088";
    private static final String DEV_PGAIA_DOMAIN = "http://dev.gaia.pces.co.kr:8088";
    private static final String STG_PGAIA_DOMAIN = "http://stg.idea-platform.net:8088";
    private static final String PROD_PGAIA_DOMAIN = "https://gaia.pces.co.kr";

    @PostConstruct
    public void init() {
        cookieVO = new CookieVO(platform.toUpperCase());

        if("pgaia".equals(platform)) {
            if("local".equals(activeProfile)) {
                domain = LOCAL_PGAIA_DOMAIN;
            }
            else if("dev".equals(activeProfile)) {
                domain = DEV_PGAIA_DOMAIN;
            }
            else if("staging".equals(activeProfile)) {
                domain = STG_PGAIA_DOMAIN;
            }
            else if("prod".equals(activeProfile)) {
                domain = PROD_PGAIA_DOMAIN;
            }
        }
        else if("gaia".equals(platform)) {
            if("local".equals(activeProfile)) {
                domain = LOCAL_GAIA_DOMAIN;
            }
            else if("dev".equals(activeProfile)) {
                domain = DEV_GAIA_DOMAIN;
            }
            else if("staging".equals(activeProfile)) {
                domain = STG_GAIA_DOMAIN;
            }
            else if("prod".equals(activeProfile)) {
                domain = PROD_GAIA_DOMAIN;
            }
        }
        else{
            domain = documentServiceProp.getDomain();
        }

        serviceId = documentServiceProp.getServiceId();
    }

    public Map<String, String> getHeaderMap(Map<String, String> headers) {
        String xAuthToken = generateSSOToken();

        Map<String, String> newHeaders = Maps.newHashMap(headers);
        newHeaders.put("serviceId", serviceId);
        newHeaders.put("x-auth", xAuthToken);

        return newHeaders;
    }

    /**
     * 네비게이션 목록 생성
     * @param requestParams
     * @return
     */
    public Result createNavigationList(List<DocumentForm.NavigationCreate> requestParams) {
        Result result = new Result();

        try {
            Map<String, String> headers = new HashMap<>();

            result = restClientUtil.sendPost(
                    String.format("%s/%s", domain, documentServiceProp.getApi().getCreateNavigationList())
                    , getHeaderMap(headers)
                    , requestParams
                    , new HashMap<>()
            ).block();

            if(result != null){
                result.put("resultCode", "00");
            }
        } catch (GaiaBizException e) {
            log.error("createNavigationList:{}", e);
            result.put("resultCode", "01");
        }

        return result;
    }

//    /**
//     * 폴더유형 네비 최신 문서 정보 조회
//     */
//    public DcStorageMain getLastestDcStorageMainByFolderType(String cntrctNo, String naviDiv, String folderType) {
//        DcStorageMain dcStorageMain = null;
//
//        try {
//            Map<String, String> headers = new HashMap<>();
//            Map<String, String> requestParams = new HashMap<>();
//            requestParams.put("cntrctNo", cntrctNo);
//            requestParams.put("naviDiv", naviDiv);
//            requestParams.put("naviFolderType", folderType);
//
//            Result result = restClientUtil.sendPost(
//                    String.format("%s/%s", domain, documentServiceProp.getApi().getLastestDcStorageMainByFolderType())
//                    , getHeaderMap(headers)
//                    , requestParams
//                    , getCookieMap()
//            ).block();
//
//            if (!result.isOk() || !"00".equals(result.getDetails().get("resultCode"))) {
//                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "fail");
//            }
//
//            dcStorageMain = objectMapper.convertValue(result.getDetails().get("data"), DcStorageMain.class);
//
//        } catch (Exception e) {
//            log.error("createNavigationList:{}", e);
//        }
//
//        return dcStorageMain;
//    }

    /**
     * 문서 생성
     *
     * @param requestParams
     * @return
     */
    public List<DcStorageMain> createFile(DocumentForm.DocCreateEx requestParams, List<MultipartFile> files) {
        return this.createFile(requestParams, files, Maps.newHashMap());
    }
    public List<DcStorageMain> createFile(DocumentForm.DocCreateEx requestParams, List<MultipartFile> files, Map<String, String> headers) {
        List<DcStorageMain> result = null;

        try {
            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            multipartBodyBuilder.part("docData", requestParams).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if(file != null){
                        String docNm = requestParams.getDocNm();
                        if (docNm == null || docNm.isEmpty()) {
                            docNm = StringUtils.defaultString(file.getOriginalFilename());  // docNm이 없으면 file.getOriginalFilename() 사용
                        }

                        String fileName = docNm.replace(" ", "_");  // 띄어쓰기를 _로 바꿈
                        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);  // URL 인코딩

                        multipartBodyBuilder.part("files", file.getResource())
                                .header("Content-Disposition", String.format("form-data; name=\"files\"; filename*=UTF-8''%s", fileName));
                    }
                }
            }

            // TODO. iCube 연동 시 인증 토큰 없음. 해결방안은??
            ResponseEntity<?> res = restClientUtil.postFileUpload(
                    String.format("%s/%s", domain, documentServiceProp.getApi().getCreateFile())
                    , BodyInserters.fromMultipartData(multipartBodyBuilder.build())
                    , getHeaderMap(headers)
                    , new HashMap<>()
                    , Result.class
            ).block();
            if(res != null){
                Object body = res.getBody();
                if(body != null){
                    log.info("getBody: {}", body);
                    result = objectMapper.convertValue(((Result) body).getDetails().get("data"), new TypeReference<List<DcStorageMain>>() {});
                }
            }
        } catch (GaiaBizException e) {
            log.error("createFile:{}", e.getMessage());
        }

        return result;
    }

    public HashMap<String, Object> getDocumentMainData(String cmnGrpCd, String pjtNo, String cntrctNo, String pjtType, @NotBlank String menuId, String loginId, Boolean isAdmin, String naviId) {
        try {
            HashMap<String, Object> serviceResult = new HashMap<>();
            String url = String.format("%s/%s?pjtNo=%s&cntrctNo=%s&pjtType=%s&menuId=%s&loginId=%s&cmnGrpCd=%s&naviId=%s&isAdmin=%s",domain,documentServiceProp.getApi().getSelectDocumentMainData(),pjtNo,cntrctNo,pjtType,menuId,loginId,cmnGrpCd,naviId,isAdmin);
            Result response = restClientUtil.sendGet(url
                    , getHeaderMap(new HashMap<>())
                    , Result.class
            ).getBody();

            if(response == null){
                throw new GaiaBizException(ErrorType.ETC,"[selectDocumentMainData] 문서관리 통신 실패");
            }

            if (!response.isOk()) {
                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "fail");
            }

            List<Map<String, ?>> naviAuthority = objectMapper.convertValue(response.getDetails().get("naviAuthority"), new TypeReference<List<Map<String, ?>>>() {});
            List<String> availableFileExt = objectMapper.convertValue(response.getDetails().get("availableFileExt"), new TypeReference<List<String>>() {});

//            String navigationListStr = objectMapper.writeValueAsString(response.getDetails().get("navigationList"));

            List<Map<String,Object>> navigationList = objectMapper.convertValue(response.getDetails().get("navigationList"), new TypeReference<List<Map<String,Object>>>() {});

            serviceResult.put("naviAuthority", naviAuthority);
            serviceResult.put("availableFileExt", availableFileExt);
            serviceResult.put("navigationList", navigationList);

            return serviceResult;
        } catch (GaiaBizException e) {
            log.error("DocumentServiceClient.getDocumentMainData() : {}",e.getMessage());
        }

        return null;
    }

    public Map<String, Object> createDocSharedHistory(DocumentDto.ApprovalRequestData params) {

        try {
            Map<String, String> headers = new HashMap<>();
            String url = String.format("%s/%s", domain, documentServiceProp.getApi().getInsertDocSharedHistory());
            Result response = restClientUtil.sendPost(url
                    , getHeaderMap(headers)
                    , params
                    , new HashMap<>()
            ).block();

            if(response == null){
                throw new GaiaBizException(ErrorType.ETC,"[insertDocSharedHistory] 문서관리 통신 실패");
            }

            if(response.isOk()){
                return response.getDetails();
            }
        } catch (GaiaBizException e) {
            log.error("createNavigationList:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 문서 삭제 api
     * @param requestParams
     * @return
     *
     * ex) resquestParams.put("docIds", new String[]{"971d09e8-c18c-4a1d-a392-5e0debb10315", "552f160f-d0d8-4e4b-baf8-6c6194d6ebb5"});
     */
    public Result removeDocument(Map<String, Object> requestParams){
        Result result = Result.ok();

        try {
            Map<String, String> headers = new HashMap<>();
            Result response = restClientUtil.sendPut(
                    String.format("%s/%s", domain, documentServiceProp.getApi().getRemoveDocument())
                    , getHeaderMap(headers)
                    , requestParams
                    , Result.class
            ).getBody();

            log.info("removeDocument() response : {}", response);
        } catch (GaiaBizException e) {
            result = Result.nok(ErrorType.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return result;
    }

    /**
     * 문서 삭제 취소 api
     * @param requestParams
     * @return
     *
     * ex) resquestParams.put("docIds", new String[]{"971d09e8-c18c-4a1d-a392-5e0debb10315", "552f160f-d0d8-4e4b-baf8-6c6194d6ebb5"});
     */
    public Result rollbackRemovedDocument(Map<String, Object> requestParams){
        Result result = Result.ok();

        try {
            Map<String, String> headers = new HashMap<>();
            Result response = restClientUtil.sendPost(
                    String.format("%s/%s", domain, documentServiceProp.getApi().getRollbackRemovedDocument())
                    , getHeaderMap(headers)
                    , requestParams
                    , Result.class
            ).getBody();

            log.info("rollbackRemovedDocument() response : {}", response);
        } catch (GaiaBizException e) {
            result = Result.nok(ErrorType.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return result;
    }

    public void createApprovalDocument(Map<String, Object> requestParams){
        // TODO: 문서관리 통신 방식으로 변경 필요.
        documentService.createApprovalDocument(requestParams);
    }

    public String checkFolderExist(String naviId, String upDocId, String docNm) {
//        String safeUpDocId = URLEncoder.encode(upDocId, StandardCharsets.UTF_8);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/%s",domain,documentServiceProp.getApi().getCheckFolderExist()))
                .queryParam("naviId",naviId)
                .queryParam("upDocId",upDocId)
                .queryParam("docNm",docNm)
                .encode(StandardCharsets.UTF_8).build()
                .toUri();
        Result response = restClientUtil.sendGet(uri
                , getHeaderMap(new HashMap<>())
                , Result.class
        ).getBody();

        if(response == null){
            throw new GaiaBizException(ErrorType.ETC,"[checkFolderExist] 문서관리 통신 실패");
        }

        if (!response.isOk()) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "fail");
        }

        return (String) response.getDetails().get("checkFolderExist");
    }

    public String checkHasNavigationType(DocumentForm.@Valid CheckHasFolderType inputParam) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/%s",domain,documentServiceProp.getApi().getCheckHasNavigationType()))
                .queryParam("naviDiv",inputParam.getNaviDiv())
                .queryParam("upNaviId",inputParam.getUpNaviId())
                .queryParam("naviFolderType",inputParam.getNaviFolderType())
                .encode(StandardCharsets.UTF_8).build()
                .toUri();
        Result response = restClientUtil.sendGet(uri
                , getHeaderMap(new HashMap<>())
                , Result.class
        ).getBody();

        if(response == null){
            throw new GaiaBizException(ErrorType.ETC,"[checkHasNavigationType] 문서관리 통신 실패");
        }

        if (!response.isOk()) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "fail");
        }

        return (String) response.getDetails().get("checkHasNavigationType");
    }
}