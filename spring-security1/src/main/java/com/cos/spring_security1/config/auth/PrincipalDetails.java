package com.cos.spring_security1.config.auth;

import com.cos.spring_security1.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

/*
시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다
로그인 진행이 완료되면 시큐리티 session 을 만들어준다 (Security ContextHolder 라는 key 값에다가 세션 정보를 저장한다)
시큐리티가 가지고 있는 세션에 들어갈 수 있는 오브젝트 타입 => Authentication 타입의 객체
Authentication 안에 User 정보가 있어야 된다
User 오브젝트의 타입 => UserDetails 타입 객체
*/

/*
Security Session(시큐리티가 가지고 있는 세션 영역) => Authentication 객체 => UserDetails(PrincipalDetails)
UserDetails 를 통해 User 객체에 접근할 수 있음
*/

/*
UserDetails 만들기
일반로그인할 때, OAuth 로그인할 때마다 UserDetails, OAuth2User 를 따로따로 컨트롤러에서 받아주기 복잡하니 PrincipalDetails 로 이 두개를 묶어서
컨트롤러에서 Authentication 으로부터 PrincipalDetails 타입만 받아오면 되도록!
*/

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;  // 컴포지션 : 다른 객체의 인스턴스를 더 큰 클래스의 일부로(인스턴스 변수로) 포함시키는 방식
    private Map<String, Object> attributes;

    // 일반 로그인 시 사용
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth 로그인 시 사용
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 해당 User의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().toString();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 사이트에서 1년동안 회원이 로그인을 안하면 -> 휴먼 계정으로 하기로 한 경우 false
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return null;
    }
}
