package kr.co.ideait.platform.gaiacairos.web.config;

import jakarta.servlet.DispatcherType;
import kr.co.ideait.platform.gaiacairos.web.config.request.filter.AccessLoggingFilter;
import kr.co.ideait.platform.gaiacairos.web.config.request.filter.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import kr.co.ideait.platform.gaiacairos.core.config.property.Properties;
import kr.co.ideait.platform.gaiacairos.core.exception.ExceptionAdvice;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    AuthFilter authFilter;

    @Autowired
    ExceptionAdvice exceptionAdvice;

    @Autowired
    Properties properties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 스프링시큐리티가 생성하지도않고 기존것을 사용하지도 않음. JWT 같은토큰방식을 쓸때 사용하는 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)) // 스프링시큐리티가 항상 세션을 생성
                .authorizeHttpRequests(authz -> {
                    //forward에도 기본으로 인증이 걸리게 설정 해결
                    authz.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                            .requestMatchers("/", "/assets/**", "images/**").permitAll()
                            // ADMIN만 접근 가능
                            .requestMatchers("/actuator/**").hasRole("ADMIN")
                            .anyRequest().authenticated();
                })
                .exceptionHandling(exHandler -> {
                    exHandler.accessDeniedHandler(exceptionAdvice::handleAuthentication);
                    exHandler.authenticationEntryPoint(exceptionAdvice::handleAuthentication);
                })
                .logout(logout -> logout.disable())
                .addFilterBefore(new AccessLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        // .addFilterAfter(exFilter, ExceptionTranslationFilter.class);

        http.sessionManagement((session) -> {
            session.sessionFixation().migrateSession();
            session.sessionFixation().changeSessionId();
        });

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return ((web) -> {
            web.ignoring().requestMatchers(
//                    "/" // 메인
//                    ,
                    "/assets/**", "/webjars/**", "/upload/**", "*.ico" // 리소스
                    , "/login", "/error/**", "/portal", "/api/login/**", "/api/logout", "/webApi/**", "/eurecaWebApi/**", "/interface/**", "/auth/**", "/linkLogin", "/ncpLogin", "/link/**", "/api/portal/change-lang/**", "/api/util/kma-weather", "/login/dummy", "/api/portal/new-use-request"); // 로그인, 에러, 암호화모듈 등 공통
        });
    }

}
