package com.cos.spring_security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인에 등록된다
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/user/**").authenticated()  // /user 라는 url로 요청이 들어오면 인증이 필요하다
                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")  // /manager로 들어오는 url 은 MANAGER 인증 또는 ADMIN 인증이 필요하다
                .requestMatchers("/admin/**").hasRole("ADMIN")  // /admin으로 들어오는 url 은 ADMIN 인증이 필요하다
                .anyRequest().permitAll()  // 나머지 url은 전부 권한 허용
            );

        return http.build();
    }
}
