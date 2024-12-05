package com.cos.security_jwt.config;

import com.cos.security_jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.security_jwt.filter.MyFilter1;
import com.cos.security_jwt.filter.MyFilter3;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.addFilterBefore(new MyFilter3(), SecurityContextHolderFilter.class);  // security filter 보다 기본 필터가 먼저 실행되도록 설정할 수 있다

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // session 을 사용하지 않겠다(stateless 서버로 만들겠다)
            )
            .formLogin(formLogin -> formLogin.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .with(new CustomSecurityFilterManager(), CustomSecurityFilterManager::build)
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/api/v1/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
                .anyRequest().permitAll()
            );

        return http.build();
    }

    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                .addFilter(corsFilter)  // @CrossOrigin(인증X), 시큐리티 필터에 등록(인증O)
                .addFilter(new JwtAuthenticationFilter(authenticationManager));

            super.configure(http);
        }

        public HttpSecurity build() {
            return getBuilder();
        }
    }
}