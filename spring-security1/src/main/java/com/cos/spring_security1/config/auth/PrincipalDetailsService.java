package com.cos.spring_security1.config.auth;

import com.cos.spring_security1.model.User;
import com.cos.spring_security1.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
시큐리티 설정(SecurityConfig)에서 loginProcessingUrl("/login") 로 설정함
/login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어있는 loadUserByUsername 메서드가 실행된다
*/

@Service  // 빈으로 등록해주어야 자동으로 PrincipalDetailsService의 loadUserByUsername을 호출한다
public class PrincipalDetailsService implements UserDetailsService {  // UserDetails(PrincipalDetails)를 사용해서 Authentication 만들기

    private final UserRepository userRepository;

    public PrincipalDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /* 시큐리티 session(내부 Authentication(내부 UserDetails))
       여기서 리턴되면서 -> 리턴된 값(UserDetails)이 Authentication 내부에 들어간다
       그러면서 만들어진 이 Authentication 을 시큐리티 session 에 자동으로 넣어준다! -> 로그인 완료 */
    // 함수 종료 시, @AuthenticationPrincipal 어노테이션이 만들어진다
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  // username 파라미터명으로 받아와야 제대로 동작한다 (아니면 따로 설정해주어야 함)
        User userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            return new PrincipalDetails(userEntity);
        }
        return null;
    }
}