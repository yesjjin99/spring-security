package com.cos.spring_security1.controller;

import com.cos.spring_security1.config.auth.PrincipalDetails;
import com.cos.spring_security1.model.RoleType;
import com.cos.spring_security1.model.User;
import com.cos.spring_security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller  // view 를 리턴하겠다
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    // 시큐리티 session 에는 Authentication 타입 객체가 들어갈 수 있고 (security session 안에 Authentication 객체가 들어간 순간 로그인이 된 것)
    // Authentication 객체 안에는 1. UserDetails 타입 객체 2. OAuth2User 타입 -> 2가지 타입의 객체가 들어갈 수 있다
    // UserDetails 타입 -> 일반적인 로그인을 하게 되면, UserDetails 객체가 Authentication 에 들어간다
    // OAuth2User 타입 -> OAuth 로그인을 하게 되면, OAuth2User 객체가 Authentication 에 들어간다
    @GetMapping("/test/login")
    @ResponseBody
    public String testLogin(
        Authentication authentication,  // 방법1: Authentication DI(의존성 주입)
        @AuthenticationPrincipal PrincipalDetails userDetails  // 방법2: @AuthenticationPrincipal 어노테이션을 통해 security 세션 정보에 접근할 수 있다
    ) {
        System.out.println("/test/login ====================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication = " + principalDetails.getUser());

        System.out.println("userDetails = " + userDetails.getUser());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    @ResponseBody
    public String testOauthLogin(
        Authentication authentication,
        @AuthenticationPrincipal OAuth2User oauth
    ) {
        System.out.println("/test/oauth/login ====================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication = " + oAuth2User.getAttributes());

        System.out.println("oAuth2User = " + oauth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }

    @Autowired
    public IndexController(UserRepository userRepository,
        BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // localhost:8080/
    // localhost:8080
    @GetMapping({"", "/"})
    public String index() {
        return "index";
    }

    // OAuth 로그인을 해도 PrincipalDetails 로, 일반 로그인을 해도 PrincipalDetails 로 받을 수 있다
    @GetMapping("/user")
    @ResponseBody
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails = " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    @ResponseBody
    public String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);  // 암호화된 비밀번호
        userRepository.save(user);

        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")  // 특정 메서드에 권한을 간단하게 걸고싶다면 @Secured 를 사용하면 됨
    @GetMapping("/info")
    @ResponseBody
    public String info() {
        return "개인정보";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")  // 이 메서드가 실행되기 직전에 권한을 준다 (여러 권한을 줄 수 있음)
    @GetMapping("/data")
    @ResponseBody
    public String data() {
        return "데이터정보";
    }
}
