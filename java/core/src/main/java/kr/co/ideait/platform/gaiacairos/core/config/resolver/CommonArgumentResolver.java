package kr.co.ideait.platform.gaiacairos.core.config.resolver;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.CommonTraceConstants;
import kr.co.ideait.iframework.helper.CustomHttpRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class CommonArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(CommonReqVo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpRequest = servletRequestAttribute.getRequest();

        String url = CustomHttpRequestUtils.getFullRequestUrl(httpRequest);

        CommonReqVo header = (CommonReqVo) RequestContextHolder.currentRequestAttributes().getAttribute(CommonTraceConstants.REQID.REQUEST_COMMON_HEADER.name(), RequestAttributes.SCOPE_REQUEST);

        if (header != null) {
            header.setReqUrl(url);
        }

        return header;
    }
}