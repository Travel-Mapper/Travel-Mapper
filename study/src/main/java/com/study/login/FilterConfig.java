package com.study.login;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final LoginService loginService;

    @Bean
    public FilterRegistrationBean<JwtCheckFilter> customFilter() {
        FilterRegistrationBean<JwtCheckFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtCheckFilter(loginService));
        registrationBean.addUrlPatterns("/*"); // 필터 적용 경로 설정
        return registrationBean;
    }
}
