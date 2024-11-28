package com.cos.spring_security1.config.auth;

import com.cos.spring_security1.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다
// 로그인 진행이 완료되면 시큐리티 session 을 만들어준다 (Security ContextHolder 라는 key 값에다가 세션 정보를 저장한다)
// 시큐리티가 가지고 있는 세션에 들어갈 수 있는 오브젝트 타입 => Authentication 타입의 객체
// Authentication 안에 User 정보가 있어야 된다
// User 오브젝트의 타입 => UserDetails 타입 객체

// Security Session(시큐리티가 가지고 있는 세션 영역) => Authentication 객체 => UserDetails(PrincipalDetails)
// UserDetails 를 통해 User 객체에 접근할 수 있음

public class PrincipalDetails implements UserDetails {  // UserDetails 만들기

    private User user;  // 컴포지션 : 다른 객체의 인스턴스를 더 큰 클래스의 일부로(인스턴스 변수로) 포함시키는 방식

    public PrincipalDetails(User user) {
        this.user = user;
    }

    // 해당 User의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
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
}
