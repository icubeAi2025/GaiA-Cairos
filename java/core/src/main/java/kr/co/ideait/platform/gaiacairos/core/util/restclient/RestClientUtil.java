package kr.co.ideait.platform.gaiacairos.core.util.restclient;

import com.google.common.collect.Maps;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractRestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.URI;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public final class RestClientUtil implements AbstractRestClient {

    private final WebClient webClient;

    private void setContentType(RestClient.RequestHeadersUriSpec<?> restClientImpl, Map<String, String> headers) {
        if (!MapUtils.isEmpty(headers) && headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            restClientImpl.accept(MediaType.parseMediaType(headers.get(HttpHeaders.CONTENT_TYPE)));
        } else {
            restClientImpl.accept(MediaType.APPLICATION_JSON);
        }
    }

    /**
     * GET 요청을 보내고 응답을 객체로 반환
     *
     * @param targetUrl    요청을 보낼 URL
     * @param headers      요청 헤더 정보
     * @param responseType 응답을 매핑할 클래스 타입
     * @return 응답 객체
     */
    protected <T> ResponseEntity<T> sendGet(String targetUrl, Map<String, String> headers, Class<T> responseType) {
        RestClient restClient = RestClient.create();
        RestClient.RequestHeadersUriSpec<?> restClientImpl = restClient.get();

        if (headers == null) {
            headers = Maps.newHashMap();
        }

        setContentType(restClientImpl, headers);

        final Map<String, String> finalHeaders = headers;

        return restClientImpl.uri(targetUrl)
                .headers(httpHeaders -> httpHeaders.setAll(finalHeaders))
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new BizException(response.getStatusText());
                })
                .toEntity(responseType);
    }
    protected <T> ResponseEntity<T> sendGet(URI targetUrl, Map<String, String> headers, Class<T> responseType) {
        RestClient restClient = RestClient.create();
        RestClient.RequestHeadersUriSpec<?> restClientImpl = restClient.get();

        if (headers == null) {
            headers = Maps.newHashMap();
        }

        setContentType(restClientImpl, headers);

        final Map<String, String> finalHeaders = headers;

        return restClientImpl.uri(targetUrl)
                .headers(httpHeaders -> httpHeaders.setAll(finalHeaders))
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new BizException(response.getStatusText());
                })
                .toEntity(responseType);
    }

    /**
     * POST 요청을 보내고 응답을 객체로 반환
     *
     * @param targetUrl    요청을 보낼 URL
     * @param headers      요청 헤더 정보
     * @param body         요청 본문 객체
     * @param responseType 응답을 매핑할 클래스 타입
     * @return 응답 객체
     */
    protected <T> ResponseEntity<T> sendPost(String targetUrl, Map<String, String> headers, Object body, Class<T> responseType) {
        RestClient restClient = RestClient.create();
        RestClient.RequestBodyUriSpec restClientImpl = restClient.post();

        if (headers == null) {
            headers = Maps.newHashMap();
        }

//        setContentType(restClientImpl, headers);

        restClientImpl.uri(targetUrl);

        if ( headers.get( HttpHeaders.CONTENT_TYPE ) != null && !"none".equals( MapUtils.getString( headers, HttpHeaders.CONTENT_TYPE ) ) ) {
            restClientImpl.contentType( MediaType.parseMediaType( MapUtils.getString( headers, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE ) ) );
        }

        final Map<String, String> finalHeaders = headers;

        restClientImpl.headers(httpHeaders -> httpHeaders.setAll(finalHeaders));
//        restClientImpl.header("Set-Cookie", "JSESSIONID=TqfqDCYUe83CM2VBYNa2TNrW7S9gIT1rkrrsYAGx.doc24-12; KHANUSER=x3cmvnckqpbrpf" );

        ResponseEntity<T> resp = restClientImpl
//                .contentType(MediaType.parseMediaType(MapUtils.getString(headers, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)))
//                .headers(httpHeaders -> httpHeaders.addAll(CollectionUtils.toMultiValueMap(map2MultiValueMap(headers))))
//                .headers(httpHeaders -> httpHeaders.addAll(map2MultiValueMap(headers)))
//                .body(map2MultiValueMap((Map)body))
                .body(body)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new GaiaBizException(ErrorType.INTERFACE, response.getStatusText());
                })
                .toEntity(responseType);

        return resp;
    }

    /**
     * PUT 요청을 보내고 응답을 객체로 반환
     *
     * @param targetUrl    요청을 보낼 URL
     * @param headers      요청 헤더 정보
     * @param body         요청 본문 객체
     * @param responseType 응답을 매핑할 클래스 타입
     * @return 응답 객체
     */
    protected <T> ResponseEntity<T> sendPut(String targetUrl, Map<String, String> headers, Object body, Class<T> responseType) {
        RestClient restClient = RestClient.create();
        RestClient.RequestBodyUriSpec restClientImpl = restClient.put();

        if (headers == null) {
            headers = Maps.newHashMap();
        }

        setContentType(restClientImpl, headers);

        final Map<String, String> finalHeaders = headers;

        ResponseEntity<T> resp = restClientImpl
                .uri(targetUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> httpHeaders.setAll(finalHeaders))
                .body(body)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new BizException(response.getStatusText());
                })
                .toEntity(responseType);

        return resp;
    }

    /**
     * DELETE 요청을 보내고 응답을 객체로 반환
     *
     * @param targetUrl    요청을 보낼 URL
     * @param headers      요청 헤더 정보
     * @param responseType 응답을 매핑할 클래스 타입
     * @return 응답 객체
     */
    protected <T> ResponseEntity<T> sendDelete(String targetUrl, Map<String, String> headers, Class<T> responseType) {
        RestClient restClient = RestClient.create();
        RestClient.RequestHeadersUriSpec restClientImpl = restClient.delete();

        if (headers == null) {
            headers = Maps.newHashMap();
        }

        setContentType(restClientImpl, headers);

        final Map<String, String> finalHeaders = headers;

        ResponseEntity<T> resp = restClientImpl
                .uri(targetUrl)
                .headers(httpHeaders -> {
                    ((HttpHeaders) httpHeaders).setAll(finalHeaders);
                })
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new BizException(response.getStatusText());
                })
                .toEntity(responseType);

        return resp;
    }

    protected Mono<Map> postFileUpload(String url, BodyInserters.MultipartInserter multipartInserter, Map<String, String> headers) {
        return this.postFileUpload(url, multipartInserter, headers, null);
    }
    protected Mono<Map> postFileUpload(String url, BodyInserters.MultipartInserter multipartInserter, Map<String, String> headers, Map<String, String> cookies) {
//        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
//        formData.setAll(params);
        if(url == null || url.isEmpty()){
            return Mono.empty();
        }

        if (headers == null) {
            headers = Maps.newHashMap();
        }

        if (cookies == null) {
            headers = Maps.newHashMap();
        }

        final Map<String, String> finalHeaders = headers;

        return webClient
                .post()
//                .uri(url, (uriBuilder -> uriBuilder.queryParams(formData).build()))
                .uri(url)
//                .contentType(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.ACCEPT, "*/*")
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authkey)
                .headers(httpHeaders -> {
                    if (finalHeaders != null && !finalHeaders.isEmpty()) {
                        httpHeaders.setAll(finalHeaders);
                    }
                })
                .cookies(httpCookies -> {
                    if (cookies != null  && !cookies.isEmpty()) {
                        httpCookies.setAll(cookies);
                    }
                })
                .body(multipartInserter)
//                .bodyValue(multipartBodyBuilder)
                .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<>() {})
                .bodyToMono(Map.class)
//                .map(JsonParser::parseString)
//                .map(JsonElement::getAsJsonObject)
                ;
    }

    protected Mono<? extends ResponseEntity<?>> postFileUpload(String url, BodyInserters.MultipartInserter multipartInserter, Map<String, String> headers, Map<String, String> cookies, Class<?> responseType) {
        if(url == null || url.isEmpty()){
            return Mono.empty();
        }

        if (headers == null) {
            headers = Maps.newHashMap();
        }

        if (cookies == null) {
            headers = Maps.newHashMap();
        }

        final Map<String, String> finalHeaders = headers;

        return webClient
                .post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.ACCEPT, "*/*")
                .headers(httpHeaders -> {
                    if (finalHeaders != null && !finalHeaders.isEmpty()) {
                        httpHeaders.setAll(finalHeaders);
                    }
                })
                .cookies(httpCookies -> {
                    if (cookies != null  && !cookies.isEmpty()) {
                        httpCookies.setAll(cookies);
                    }
                })
                .body(multipartInserter)
                .retrieve()
//                    .bodyToMono(Result.class)
                .toEntity(responseType)
                ;
    }

    /**
     * POST 요청을 보내고 응답을 객체로 반환
     *
     * @param targetUrl    요청을 보낼 URL
     * @param headers      요청 헤더 정보
     * @param body         요청 본문 객체
     * @param cookies      요청 쿠키 정보
     * @return 응답 객체
     */
    protected Mono<Result> sendPost(String targetUrl, Map<String, String> headers, Object body, Map<String, String> cookies) {
        if(targetUrl==null || targetUrl.isEmpty() ){
            return Mono.empty();
        }

        if (headers == null) {
            headers = Maps.newHashMap();
        }

        if (cookies == null) {
            headers = Maps.newHashMap();
        }

        final Map<String, String> finalHeaders = headers;

        return webClient
                .post()
                .uri(targetUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, "*/*")
                .headers(httpHeaders -> {
                    if (finalHeaders != null && !finalHeaders.isEmpty()) {
                        httpHeaders.setAll(finalHeaders);
                    }
                })
                .cookies(httpCookies -> {
                    if (cookies != null  && !cookies.isEmpty()) {
                        httpCookies.setAll(cookies);
                    }
                })
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Result.class)
                ;
    }

    /**
     * POST 요청을 보내고 응답을 객체로 반환
     *
     * @param targetUrl    요청을 보낼 URL
     * @param headers      요청 헤더 정보
     * @param body         요청 본문 객체
     * @return 응답 객체
     */
    protected Mono<Map> sendPost(String targetUrl, Map<String, String> headers, Object body) {
        if(targetUrl==null || targetUrl.isEmpty() ){
            return Mono.empty();
        }

        if (headers == null) {
            headers = Maps.newHashMap();
        }

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        Map params = (Map) body;

        params.forEach((key, value) -> {
            formData.add((String)key, (String)value);
        });

        final Map<String, String> finalHeaders = headers;

        return webClient
                .post()
                .uri(targetUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, "*/*")
                .headers(httpHeaders -> {
                    if (finalHeaders != null && !finalHeaders.isEmpty()) {
                        httpHeaders.setAll(finalHeaders);
                    }
                })
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorMap(original -> {
                    log.error("Exception fail", original);
                    return new BizException("Something went wrong", original);
                })
                ;
    }

//    public Mono<Void> downloadFile(String fileUrl, String localFilePath) {
//        Path path = Paths.get(localFilePath);
//
//        return webClient.get()
//                .uri(fileUrl)
//                .retrieve()
//                .exchangeToFlux( clientResponse -> {
//                    return clientResponse.body(BodyExtractors.toDataBuffers());
//                })
//                .bodyToFlux(DataBuffer.class)
//                .flatMap(dataBuffer -> DataBufferUtils.write(dataBuffer, path, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
//                .then();
//    }

    protected File downloadFile(String url, Map<String, String> params, String filePath, String fileName) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        params.forEach(formData::add);

        File file = null;

        try {
            InputStreamResource block = webClient.post()
                    .uri(url)
                    .body(BodyInserters.fromFormData(formData))
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<InputStreamResource>() {
                    })
                    .block();

//            String fullPath = String.format("%s/%s", filePath, fileName);
//            Files.write(Paths.get(fullPath), block.getInputStream().readAllBytes());
//            file = Paths.get(fullPath).toFile();

            file = File.createTempFile("download", "tmp");

            FileOutputStream out = null;
            InputStream input = null;

            try {
                if (block != null) {
                    input = block.getInputStream();
                    out = new FileOutputStream(file);

                    if (input != null && out != null) {
                        StreamUtils.copy(input.readAllBytes(), out);
                    }
                }
            } finally {
                if (input != null) {
                    input.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException fail", e);
            throw new GaiaBizException(e);
        } catch (IOException e) {
            log.error("IOException fail", e);
            throw new GaiaBizException(e);
        }

        return file;
    }
}
