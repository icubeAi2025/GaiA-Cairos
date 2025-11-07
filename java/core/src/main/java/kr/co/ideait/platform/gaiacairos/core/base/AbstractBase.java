package kr.co.ideait.platform.gaiacairos.core.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import kr.co.ideait.platform.gaiacairos.core.config.property.Properties;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie.CookieVO;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.InvokeServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class AbstractBase {

    @Autowired
    protected Environment environment;

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected CookieService cookieService;

    @Autowired
    protected CookieVO cookieVO;

    @Autowired
    protected Properties properties;

    @Autowired
    protected InvokeServiceClient invokeServiceClient;

    @Value("${spring.profiles.active}")
    protected String activeProfile;

    @Value("${platform}")
    protected String platform;

    @Value("${api.pgaia.domain}")
    protected String apiDomain;

    @Value("${api.pgaia.domain}")
    protected String apiPGaiaDomain;

    @Value("${api.gaia.domain}")
    protected String apiGaiaDomain;

    @Value("${api.cairos.domain}")
    protected String apiCairosDomain;

    @Value("${api.url}")
    protected String apiUrl;

    @Value("${api.requestKey}")
    protected String apiKey;

    protected String uploadPath;

    protected String uploadPersonalPath;

    protected String guidePath;

    @Value("${gaia.path.temp}")
    protected String tempPath;

    @Value("${gaia.authority.type:new}")
    protected String btnAuthType;

    @Value("${link.domain.url}")
    protected String apiLinkDomain;
    
    protected String pjtType;

    @Value("${gaia.path.previewPath}")
    protected String previewPath;

    @PostConstruct
    public void init() {
        uploadPath = properties.getGaia().getPath().getUpload();//폴더를 생성할 기본 경로
        uploadPersonalPath = properties.getGaia().getPath().getPersonal();
        guidePath = properties.getGaia().getPath().getGuide();
        pjtType = "PGAIA".equals(platform.toUpperCase()) ? "GAIA" : platform.toUpperCase();
    }

    /**
     * 파일 업로드 경로 get
     */
    public String getUploadPathByWorkType(FileUploadType fileUploadType) {
//        if ("local".equals(activeProfile) || "dev".equals(activeProfile) ) {
//            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA, "구분자를 넘겨주세요.");
//        } else {
//            return this.getUploadPathByWorkType(fileUploadType, null);
//        }
        return this.getUploadPathByWorkType(fileUploadType, null);
    }

    /**
     * 파일 업로드 경로 반환
     *
     * @param fileUploadType ({@link FileUploadType}) 업로드 하는 파일의 업무 구분
     * @param prefixDiv ({@link String}) 파일 업로드 위치(계약번호 / 프로젝트번호 / etc)<br>
     * <em>GaiA의 경우 cntrctNo에 pjtNo가 셋팅되어 있으므로 항상 cntrctNo를 활용 가능</em>
     * @return {@link String} 생성된 파일 업로드 경로 문자열
     */
    public String getUploadPathByWorkType(FileUploadType fileUploadType, String prefixDiv) {
        // 파일 경로
        String baseDirPath = fileUploadType.getDirPath(); // Enum으로 기본 디렉토리 경로 생성
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")); // 현재 날짜를 기반으로 하위 경로 추가
        String path = null;

        if (StringUtils.isEmpty(prefixDiv)) {
            path = Path.of(baseDirPath, datePath).toString().replace("\\","/");
        } else {
            if(FileUploadType.PERSONAL.equals(fileUploadType) || FileUploadType.NOTICE.equals(fileUploadType) || FileUploadType.FAQ.equals(fileUploadType)) {
                path = Path.of(baseDirPath, prefixDiv, datePath).toString().replace("\\","/");
            }
            else
            {
                path = Path.of(prefixDiv, baseDirPath, datePath).toString().replace("\\", "/"); // 전체 경로 생성
            }
        }

        log.info("baseDirPath: {} datePath: {} path : {}", baseDirPath, datePath, path);

        return path;
    }

    public String getUploadPathByWorkTypeForPersonal(String userId) {
        // 파일 경로
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")); // 현재 날짜를 기반으로 하위 경로 추가
        String path = Path.of(userId, datePath).toString().replace("\\","/");

        return path;
    }

    public String getUploadPathByWorkTypeForDocument(FileUploadType fileUploadType, String systemCode, String prefixDiv) {
        // 파일 경로
        String baseDirPath = fileUploadType.getDirPath(); // Enum으로 기본 디렉토리 경로 생성
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")); // 현재 날짜를 기반으로 하위 경로 추가
        String path = null;

        path = Path.of(baseDirPath, systemCode, prefixDiv, datePath).toString().replace("\\","/");

        return path;
    }

    public Map invokePgaia2Cairos(String transactionId, Map<String, Object> params) {
        if (!"pgaia".equals(platform)) {
            Map result = new HashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
            return result;
        }

        return invokeService(apiCairosDomain, transactionId, params, new HashMap<>());
    }

    public Map invokePgaia2Cairos(String transactionId, Map<String, Object> params, List<MultipartFile> files) {
        if (!"pgaia".equals(platform)) {
            Map result = new HashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
            return result;
        }

        Map<String, Object> fileMap = Maps.newHashMap();
        fileMap.put("files", files);

        return invokeFileUploadService(apiCairosDomain, transactionId, params, fileMap, new HashMap<>());
    }

    public Map invokePgaia2Cairos(String transactionId, Map<String, Object> params, Map<String, Object> fileMap) {
        if (!"pgaia".equals(platform)) {
            Map result = new HashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
            return result;
        }

        return invokeFileUploadService(apiCairosDomain, transactionId, params, fileMap, new HashMap<>());
    }

    public Map invokePgaia2Cairos(String transactionId, Map<String, Object> params, Map<String, Object> fileMap, Map<String, String> headers) {
        if (!"pgaia".equals(platform)) {
            Map result = new HashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
            return result;
        }

        return invokeFileUploadService(apiCairosDomain, transactionId, params, fileMap, headers);
    }

//    public Map invokeGaia2Cairos(String transactionId, Map<String, Object> params) {
//        if (!"gaia".equals(platform)) {
//            Map result = new HashMap();
//            result.put("resultCode", "01");
//            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
//            return result;
//        }
//
//        return invokeService(apiCairosDomain, transactionId, params, new HashMap<>());
//    }
//
//    public Map invokeGaia2Cairos(String transactionId, Map<String, Object> params, List<MultipartFile> files) {
//        if (!"gaia".equals(platform)) {
//            Map result = new HashMap();
//            result.put("resultCode", "01");
//            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
//            return result;
//        }
//        Map<String, Object> fileMap = Maps.newHashMap();
//        fileMap.put("files", files);
//
//        return invokeFileUploadService(apiCairosDomain, transactionId, params, fileMap, new HashMap<>());
//    }
//
//    public Map invokeGaia2Cairos(String transactionId, Map<String, Object> params, Map<String, Object> fileMap) {
//        if (!"gaia".equals(platform)) {
//            Map result = new HashMap();
//            result.put("resultCode", "01");
//            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
//            return result;
//        }
//
//        return invokeFileUploadService(apiCairosDomain, transactionId, params, fileMap, new HashMap<>());
//    }
//
//    public Map invokeGaia2Cairos(String transactionId, Map<String, Object> params, Map<String, Object> fileMap, Map<String, String> headers) {
//        if (!"gaia".equals(platform)) {
//            Map result = new HashMap();
//            result.put("resultCode", "01");
//            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
//            return result;
//        }
//
//        return invokeFileUploadService(apiCairosDomain, transactionId, params, fileMap, headers);
//    }

    public Map invokeCairos2Pgaia(String transactionId, Map<String, Object> params) {
        if (!"cairos".equals(platform)) {
            Map result = new HashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
            return result;
        }

        return invokeService(apiPGaiaDomain, transactionId, params, new HashMap<>());
    }

    public Map invokeCairos2Pgaia(String transactionId, Map<String, Object> params, List<MultipartFile> files) {
        if (!"cairos".equals(platform)) {
            Map result = new HashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
            return result;
        }

        Map<String, Object> fileMap = Maps.newHashMap();
        fileMap.put("files", files);

        return invokeFileUploadService(apiPGaiaDomain, transactionId, params, fileMap, new HashMap<>());
    }

    public Map invokeCairos2Pgaia(String transactionId, Map<String, Object> params, Map<String, Object> fileMap) {
        if (!"cairos".equals(platform)) {
            Map result = new HashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
            return result;
        }

        return invokeFileUploadService(apiPGaiaDomain, transactionId, params, fileMap, new HashMap<>());
    }

    public Map invokeCairos2Pgaia(String transactionId, Map<String, Object> params, Map<String, Object> fileMap, Map<String, String> headers) {
        if (!"cairos".equals(platform)) {
            Map result = new HashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
            return result;
        }

        return invokeFileUploadService(apiPGaiaDomain, transactionId, params, fileMap, headers);
    }

    public Map invokeGaia2Pgaia(String transactionId, Map<String, Object> params) {
        if (!"gaia".equals(platform)) {
            Map result = new HashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
            return result;
        }

        return invokeService(apiPGaiaDomain, transactionId, params, new HashMap<>());
    }

//    public Map invokeGaia2Pgaia(String transactionId, Map<String, Object> params, List<MultipartFile> files) {
//        if (!"gaia".equals(platform)) {
//            Map result = new HashMap();
//            result.put("resultCode", "01");
//            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
//            return result;
//        }
//
//        Map<String, Object> fileMap = Maps.newHashMap();
//        fileMap.put("files", files);
//
//        return invokeFileUploadService(apiPGaiaDomain, transactionId, params, fileMap, new HashMap<>());
//    }

    public Map invokeGaia2Pgaia(String transactionId, Map<String, Object> params, Map<String, Object> fileMap) {
        if (!"gaia".equals(platform)) {
            Map result = new HashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
            return result;
        }

        return invokeFileUploadService(apiPGaiaDomain, transactionId, params, fileMap, new HashMap<>());
    }

//    public Map invokeGaia2Pgaia(String transactionId, Map<String, Object> params, Map<String, Object> fileMap, Map<String, String> headers) {
//        if (!"gaia".equals(platform)) {
//            Map result = new HashMap();
//            result.put("resultCode", "01");
//            result.put("resultMsg", "올바른 플랫폼이 아닙니다.");
//            return result;
//        }
//
//        return invokeFileUploadService(apiPGaiaDomain, transactionId, params, fileMap, headers);
//    }



    private Map invokeService(final String apiDomain, String transactionId, Map<String, Object> params, Map<String, String> headers) {
//        if ("prod".equals(activeProfile) || "staging".equals(activeProfile)) {
//        if ("prod".equals(activeProfile)) {
//            Map result = new HashMap<>();
//            result.put("resultCode", "00");
//            return result;
//        }

        if (MapUtils.isEmpty(headers)) {
            headers = new HashMap<>();
            headers.put("ServiceKey", apiKey);
        }

        if (!headers.containsKey("ServiceKey")) {
            headers.put("ServiceKey", apiKey);
        }

        Map result = invokeServiceClient.invoke(String.format("%s/%s/%s", apiDomain, apiUrl, transactionId), headers, params);

        if (MapUtils.isEmpty(result)) {
            throw new GaiaBizException(ErrorType.INTERFACE, "response body is empty");
        }

        return result;
    }

    private Map invokeFileUploadService(final String apiDomain, String transactionId, Map<String, Object> params, Map<String, Object> fileMap, Map<String, String> headers) {
//        if ("prod".equals(activeProfile) || "staging".equals(activeProfile)) {
//        if ("prod".equals(activeProfile)) {
//            Map result = new HashMap<>();
//            result.put("resultCode", "00");
//            return result;
//        }

        if (MapUtils.isEmpty(headers)) {
            headers = new HashMap<>();
            headers.put("ServiceKey", apiKey);
        }

        if (!headers.containsKey("ServiceKey")) {
            headers.put("ServiceKey", apiKey);
        }

        Map<String, String> fileInfo = new HashMap<>();

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("reqBody", params).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        //[WMI]	kr.co.ideait.platform.gaiacairos.core.base.AbstractBase.invokeFileUploadService(String, String, Map, Map, Map) makes inefficient use of keySet iterator instead of entrySet iterator
        Set<Map.Entry<String, Object>> entrySet = fileMap.entrySet();
        for (Map.Entry<String,Object> entry : entrySet) {
            String key = entry.getKey();
            Object obj = entry.getValue();

            if (obj instanceof List<?> files) {
                fileInfo.put(key, "ARRAY");

                for (Object el : files) {
                    if (el instanceof MultipartFile file) {
                        String originalFilename = file.getOriginalFilename();
                        if(originalFilename == null){
                            throw new  GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "original filename is null");
                        }
                        String fileName = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
                        multipartBodyBuilder.part(key, file.getResource()).header("Content-Disposition", String.format("form-data; name=\"%s\"; filename*=UTF-8''%s", key, fileName));
                    }
                }
            } else if (obj instanceof MultipartFile file) {
                String originalFilename = file.getOriginalFilename();
                if(originalFilename == null){
                    throw new  GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "original filename is null");
                }
                fileInfo.put(key, "OBJECT");
                String fileName = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
                multipartBodyBuilder.part(key, file.getResource()).header("Content-Disposition", String.format("form-data; name=\"%s\"; filename*=UTF-8''%s", key, fileName));
            }
        }

        multipartBodyBuilder.part("_fileInfo_", fileInfo).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> result = invokeServiceClient.upload(String.format("%s/%s/%s", apiDomain, apiUrl, transactionId), headers, multipartBodyBuilder);

        log.info("API 연동 요청(transactionId: {}), result : {}", transactionId, result);

        if (MapUtils.isEmpty(result)) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "response body is empty");
        }

        return result;
    }
}
