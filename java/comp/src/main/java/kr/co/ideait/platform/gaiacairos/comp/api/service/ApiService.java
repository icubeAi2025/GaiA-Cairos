package kr.co.ideait.platform.gaiacairos.comp.api.service;

import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
public class ApiService extends AbstractGaiaCairosService {
    
    @Autowired
    WebClient webClient;


    @Value("${api.primavera.domain}")
    private String ApiDomain;

	/**
	 * primaveraApi 요청
	 *
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public Result primaveraApiGetPost(Map<String, Object> requestBody) {

		// post 메써드로 넘길 요청 본문 생성
//		Map<String, Object> userInfo = Map.of("id", usrId, "login_id", loginId);

		// msg.api.001 - API 통신이 원활하지 않습니다. 관리자에게 문의하길 바랍니다.
		String msg = messageSource.getMessage("msg.api.001", null, LocaleContextHolder.getLocale());

		try {
			var response = webClient.post()
					.uri(ApiDomain+"/webApi/returnValue/request")
					.bodyValue(requestBody)
					.retrieve()
					.bodyToMono(Map.class)
					.block();

			// 20250227 - 정적검사 수정
			if (response != null) {
				log.debug("===================================================");
				log.debug("response : >>>>> " + response.toString());
				log.debug("===================================================");
			}

			return new Result(response);
		} catch (WebClientResponseException ex) {
			String errorBody = ex.getResponseBodyAsString();
			log.warn("Middleware 실패 응답: {}", errorBody);

//			try {
//				ObjectMapper mapper = new ObjectMapper();
//				Map<String, Object> parsed = mapper.readValue(errorBody, Map.class);
//
//				// 응답 구조 기준: { success: false, message: "...", error: { ... } }
//				if (parsed.containsKey("message")) {
//					msg = (String) parsed.get("message");
//				}
//			} catch (Exception parseEx) {
//				log.warn("중계 실패 응답 파싱 실패: {}", parseEx.getMessage());
//			}


			throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, errorBody);
		} catch (RuntimeException e) {
			throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, msg);
		}

	}

}
