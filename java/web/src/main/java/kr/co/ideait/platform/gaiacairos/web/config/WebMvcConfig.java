package kr.co.ideait.platform.gaiacairos.web.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import kr.co.ideait.iframework.ThreadContext;
import kr.co.ideait.platform.gaiacairos.core.config.property.Properties;
import kr.co.ideait.platform.gaiacairos.core.config.resolver.CommonArgumentResolver;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie.CookieVO;
import kr.co.ideait.platform.gaiacairos.core.util.ThreadContextHelper;
import kr.co.ideait.platform.gaiacairos.web.config.request.ContentCachingFilter;
import kr.co.ideait.platform.gaiacairos.web.config.request.filter.XssFilter;
import kr.co.ideait.platform.gaiacairos.web.config.request.interceptor.PlatformCommonInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Properties prop;

    @Value("${xss.acceptUrls:}")
    private String[] acceptUrls;

    @Value("${gaia.url.whitelist}")
    String whiteList;

    @Value("${platform}")
    private String platform;

    @Value("${gaia.path.previewPath}")
    private String previewPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        CacheControl cacheControl = CacheControl
                .noCache();
//                .maxAge(5, TimeUnit.SECONDS);

        registry.addResourceHandler("/resources/**") // 클라이언트가 접근할 URL 패턴
                .addResourceLocations("classpath:/META-INF/resources/") // 실제 파일 시스템 경로
//                .setCachePeriod(20)
                .setCacheControl(cacheControl);

        // 클라이언트가 요청할 URL 패턴과 실제 파일 시스템 경로를 매핑
        registry.addResourceHandler("/upload/**") // 클라이언트가 접근할 URL 패턴
                .addResourceLocations(String.format("file:///%s/", previewPath)) // 실제 파일 시스템 경로
//                .setCachePeriod(20)
                .setCacheControl(cacheControl)
        ;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(platformCheckInterceptor()).addPathPatterns("/**").excludePathPatterns(whiteList.split(","));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CommonArgumentResolver());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jsonEscapeConverter());
    }

    @Bean(name="jsonView")
    public MappingJackson2JsonView jsonView() {
        return new MappingJackson2JsonView();
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonEscapeConverter() {
        ObjectMapper copy = objectMapper.copy();
//        copy.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());

        copy.setVisibility(VisibilityChecker.Std.defaultInstance()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );

        return new MappingJackson2HttpMessageConverter(copy);
    }

    @Bean
    public FilterRegistrationBean filterBean() {
        FilterRegistrationBean filterBean = new FilterRegistrationBean(new ContentCachingFilter());
        filterBean.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        filterBean.setOrder(Integer.MIN_VALUE); //필터 여러개 적용 시 순번
//        filterBean.addUrlPatterns("/*"); //전체 URL 포함
//        filterBean.addUrlPatterns("/test/*"); //특정 URL 포함
//        filterBean.setUrlPatterns(Arrays.asList(INCLUDE_PATHS)); //여러 특정 URL 포함
        filterBean.setUrlPatterns(Arrays.asList("/*"));

        return filterBean;
    }
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilter() {
        FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>(new XssFilter());
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        registrationBean.setUrlPatterns(Collections.singletonList("/*"));
//        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);

        return registrationBean;
    }

    @Bean
    public PlatformCommonInterceptor platformCheckInterceptor() {
        return new PlatformCommonInterceptor();
    }

    @Bean
    public CookieVO cookieVO() {
        return new CookieVO(platform.toUpperCase());
    }

    @Bean
    public ThreadContext threadContext() {
        return new ThreadContext();
    }

    @Bean
    public ThreadContextHelper threadContextHelper() {
        return new ThreadContextHelper();
    }
}
