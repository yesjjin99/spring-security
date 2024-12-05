package com.cos.security_jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.security_jwt.config.auth.PrincipalDetails;
import com.cos.security_jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음
// /login 요청해서 username, password 전송하면 (post)
// UsernamePasswordAuthenticationFilter가 동작을 함
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter: 로그인 시도 중");

        // 1. username, password 받아서
        ObjectMapper om = new ObjectMapper();
        User user = null;
        try {
            user = om.readValue(request.getInputStream(), User.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(user);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        // 2. 정상인지 로그인 시도를 해본다. authenticationManager로 로그인 시도를 하면
        // PrincipalDetailsService가 호출되며, loadUserByUsername() 함수가 실행된다
        // 정상적으로 실행되면 authentication 객체 리턴됨
        // DB에 있는 username과 password가 일치한다
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        // 로그인이 정상적으로 되었다는 뜻
        System.out.println("로그인 완료됨: " + principalDetails.getUser().getUsername());

        // 3. PrincipalDetails를 세션에 담고 (세션에 값이 있어야 권한관리를 해준다)
        // Authentication 객체가 session 영역에 저장된다 (return 해주면 저장이 된다)
        // return의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는 것
        // 굳이 JWT를 사용하면서 세션을 만들 이유가 없지만, 단지 권한 처리 때문에 세션에 넣어준다
        return authentication;

        // 4. JWT 토큰을 만들어서 응답해주면 된다 -> successfulAuthentication() 함수에서
    }

    // attemptAuthentication() 함수 실행 후 인증이 정상적으로 되었으면, 그 뒤에 successfulAuthentication() 함수가 실행된다
    // JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨: 인증이 완료되었다");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // RSA 방식(공개키, 개인키)은 아니고 Hash 암호 방식(secret 키)
        String jwtToken = JWT.create()
            .withSubject(principalDetails.getUsername())  // 토큰 이름
            .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))  // 만료 시간
            .withClaim("id", principalDetails.getUser().getId())
            .withClaim("username", principalDetails.getUser().getUsername())
            .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }
}
