package kr.co.ideait.platform.gaiacairos.core.base;

import kr.co.ideait.iframework.rest.util.MultiValueMapConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;


public interface AbstractRestClient {
    default MultiValueMap<String, String> map2MultiValueMap(final Map<String, String> headers) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

        if (headers != null) {
//            for (Map.Entry<String, String> entry : headers.entrySet()) {
//                multiValueMap.put(entry.getKey(), Collections.singletonList(entry.getValue()));
//            }

            multiValueMap = MultiValueMapConverter.convert(headers);
        }

        return multiValueMap;
    }
}
