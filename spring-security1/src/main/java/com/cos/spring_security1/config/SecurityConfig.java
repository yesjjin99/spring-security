package com.cos.spring_security1.config;

import com.cos.spring_security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/* OAuth2 진행
1. code 받기 (인증)
2. access token 받기 (권한 위임)
3. 사용자 프로필 정보를 가져오고
4. 그 정보를 토대로 회원가입을 자동으로 진행시키기나 or 추가적인 사용자 정보가 필요하다면 추가적인 회원가입 창이 나와서 회원가입을 진행할 수 있다
*/

@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인에 등록된다
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)  // secured 어노테이션 활성화, preAuthorize, postAuthorize 어노테이션 활성화
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    public SecurityConfig(PrincipalOauth2UserService principalOauth2UserService) {
        this.principalOauth2UserService = principalOauth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/user/**").authenticated()  // /user 라는 url로 요청이 들어오면 인증이 필요하다
                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")  // /manager로 들어오는 url 은 MANAGER 인증 또는 ADMIN 인증이 필요하다
                .requestMatchers("/admin/**").hasAnyRole("ADMIN")  // /admin으로 들어오는 url 은 ADMIN 인증이 필요하다
                .anyRequest().permitAll()  // 나머지 url은 전부 권한 허용
            );

        http
            .formLogin(form -> form
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")  // /login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해준다 (컨트롤러에 /login 을 만들지 않아도 됨)
                .defaultSuccessUrl("/")  // /loginForm 을 요청해서 로그인을 하게 되면 "/"로 보내주지만, 특정 페이지를 요청해서 로그인하게 되면 그 페이지 그대로 보내준다
            )  // 권한이 없는 경우 로그인 페이지로 이동하도록 설정
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/loginForm")
                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                    .userService(principalOauth2UserService)  // 구글 로그인이 완료된 뒤의 후처리가 필요함. Tip) 코드X. 코드를 받은 후 그 코드를 통해 (액세스 토큰 + 사용자 프로필 정보)를 한 번에 받아오는 과정까지 해준다
                )
            );

        return http.build();
    }
}
