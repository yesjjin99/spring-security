package com.cos.security_jwt.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter3 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        // id, pw가 정상적으로 들어와서 로그인이 완료되면 -> 토큰을 만들어주고 그걸 응답을 해준다
        // 요청할 때마다 header의 Authorization에 value 값으로 토큰을 가지고 온다
        // 그 때 토큰이 넘어오면, 이 토큰이 내가 만든 토큰이 맞는지만 검증하면 됨 (RSA, HS256 -> RSA라면 private key로 잠궈서 넘겨주고, 이후 토큰이 넘겨오면 이를 public key로 열어보고 정상적으로 열리면 검증 완료)
        if (request.getMethod().equals("POST")) {
            System.out.println("POST 요청됨");
            String headerAuth = request.getHeader("Authorization");
            System.out.println(headerAuth);
            System.out.println("필터3");

            if (headerAuth.equals("token")) {
                filterChain.doFilter(request, response);  // 계속 프로세스를 진행하도록 다시 체인에 넘겨줌
            } else {
                PrintWriter outPrintWriter = response.getWriter();
                outPrintWriter.println("인증안됨");
            }
        }
    }
}
