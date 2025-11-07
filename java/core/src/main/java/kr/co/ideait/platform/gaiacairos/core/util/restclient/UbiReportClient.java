package kr.co.ideait.platform.gaiacairos.core.util.restclient;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractClient;
import kr.co.ideait.platform.gaiacairos.core.config.property.GaiaProp;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie.CookieVO;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



@Slf4j
@Component
@RequiredArgsConstructor
public class UbiReportClient extends AbstractClient {


    @Autowired
    private GaiaProp gaia;

    @Autowired
    CookieService cookieService;

    @Autowired
    private CookieVO cookieVO;

    @Value("${report.exportUrl}")
    private String reportExportUrl;

    @Value("${report.saveUrl}")
    private String reportSaveUrl;

    @PostConstruct
    public void init() {
//        uploadPath = gaia.getPath().getUpload();
    }

    public void export(String[] reportIds, Map<String, String> reportParams, Map<String, String> callbackInfo) {
        log.info("reportIds: {} reportParams: {} callbackInfo: {}", reportIds, reportParams, callbackInfo);

        HttpServletRequest request = null;

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            request = ((ServletRequestAttributes) requestAttributes).getRequest();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("x-auth", callbackInfo.get("x-auth"));

        Map<String, String> requestParams = new HashMap<>(callbackInfo);

        try {
            StringBuilder stringBuilder = new StringBuilder();
            reportParams.forEach((key, value) -> {
                stringBuilder.append(key).append("#").append(value).append("#");
            });

            if (request != null && !requestParams.containsKey("x-auth")) {
                String xAuthToken = generateSSOToken();
                headers.put("x-auth", xAuthToken);
                requestParams.put("x-auth", xAuthToken);
            }

//            requestParams.put("reqKey", callbackInfo.get("reqKey"));
//            requestParams.put("reqValue", callbackInfo.get("reqValue"));
//            requestParams.put("pdfName", callbackInfo.get("pdfArgName"));
//            requestParams.put("callbackUrl", callbackInfo.get("callbackUrl"));
            requestParams.put("file", String.join(",", reportIds));
            requestParams.put("arg", stringBuilder.toString());
            requestParams.put("platform", platform);
            requestParams.put("envMode", activeProfile);

            restClientUtil.sendPost(reportExportUrl, headers, requestParams).subscribe();
        } catch (GaiaBizException e) {
            log.error("Exception fail", e);
        }
    }

    public File download(String[] reportIds, Map<String, String> reportParams, String fileName) {
        log.info("reportIds: {} reportParams: {} callbackInfo: {}", reportIds, reportParams);

        final String filePath = String.format("%s/%s", gaia.getPath().getUpload(), getUploadPathByWorkType(FileUploadType.TEMP));
        HttpServletRequest request = null;

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            request = ((ServletRequestAttributes) requestAttributes).getRequest();
        }

        Map<String, String> headers = new HashMap<>();
        Map<String, String> requestParams = new HashMap<>();

        File file = null;

        try {
            StringBuilder stringBuilder = new StringBuilder();
            reportParams.forEach((key, value) -> {
                stringBuilder.append(key).append("#").append(value).append("#");
            });

            if (request != null && !requestParams.containsKey("x-auth")) {
                headers.put("x-auth", cookieService.getCookie(request, cookieVO.getTokenCookieName()));
                requestParams.put("x-auth", cookieService.getCookie(request, cookieVO.getTokenCookieName()));
            }

            requestParams.put("file", String.join(",", reportIds));
            requestParams.put("arg", stringBuilder.toString());
            requestParams.put("platform", platform);
            requestParams.put("envMode", activeProfile);

            file = restClientUtil.downloadFile(reportSaveUrl, requestParams, filePath, fileName);
        } catch (GaiaBizException e) {
            log.error("Exception fail", e);
        }

        return file;
    }
}