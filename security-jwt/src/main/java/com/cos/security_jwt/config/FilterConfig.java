package com.cos.security_jwt.config;

import com.cos.security_jwt.filter.MyFilter1;
import com.cos.security_jwt.filter.MyFilter2;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 시큐리티 필터 체인에 등록하지 않고 바로 필터 걸 수 있음
// 기본적으로는 security 필터가 먼저 우선적으로 실행되고, 이후 해당 기본 필터가 실행된다

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> myFilter1() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new MyFilter1());
        bean.addUrlPatterns("/*");
        bean.setOrder(0);  // 낮은 번호가 필터 중에서 가장 먼저 실행됨
        return bean;
    }

    @Bean
    public FilterRegistrationBean<Filter> myFilter2() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new MyFilter2());
        bean.addUrlPatterns("/*");
        bean.setOrder(1);  // 낮은 번호가 필터 중에서 가장 먼저 실행됨
        return bean;
    }
}
