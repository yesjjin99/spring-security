package com.cos.spring_security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인에 등록된다
public class SecurityConfig {

    @Bean  // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

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

        http.formLogin(form -> form
            .loginPage("/loginForm")
            .loginProcessingUrl("/login")  // /login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해준다 (컨트롤러에 /login 을 만들지 않아도 됨)
            .defaultSuccessUrl("/")  // /loginForm 을 요청해서 로그인을 하게 되면 "/"로 보내주지만, 특정 페이지를 요청해서 로그인하게 되면 그 페이지 그대로 보내준다
        );  // 권한이 없는 경우 로그인 페이지로 이동하도록 설정
        return http.build();
    }
}
