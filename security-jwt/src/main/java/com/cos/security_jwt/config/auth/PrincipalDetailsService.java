package com.cos.security_jwt.config.auth;

import com.cos.security_jwt.model.User;
import com.cos.security_jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http://localhost:8080/login 요청이 올 때 PrincipalDetailsService의 loadUserByUsername이 동작한다! (Spring Security의 기본적인 로그인 요청 주소가 /login 이기 때문)

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("PrincipalDetailsService의 loadUserByUsername()");
        User userEntity = userRepository.findByUsername(username);

        return new PrincipalDetails(userEntity);
    }
}
