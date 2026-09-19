package com.jinanmuseum.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器注册：JwtFilter 仅作用于后台接口（/api/admin/*），
 * 避免作为全局 Filter 拦截观众端接口。
 */
@Configuration
public class FilterConfig {

    @Bean
    public JwtFilter jwtFilter(JwtUtil jwtUtil) {
        return new JwtFilter(jwtUtil);
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(jwtFilter);
        registration.addUrlPatterns("/api/admin/*");
        registration.setOrder(1);
        return registration;
    }
}
