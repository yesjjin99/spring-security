package com.cos.spring_security1.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


// 여기서 구글 로그인이 완료된 후의 후처리를 해줘야 함
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    // 구글로부터 받은 userRequest 데이터(액세스 토큰 + 사용자 프로필 정보)에 대한 후처리되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration = " + userRequest.getClientRegistration());  // registrationId로 어떤 OAuth로 로그인했는지 확인 가능
        System.out.println("getAccessToken = " + userRequest.getAccessToken().getTokenValue());

        // 구글로그인 버튼 클릭 -> 구글 로그인 page -> 로그인 완료 -> code 리턴(리턴을 oauth2-client 라이브러리가 받아준다) -> 코드를 통해서 access token 요청
        // userRequest 정보 -> userRequest 정보를 사용해 loadUser 함수를 호출해서 구글로부터 회원 프로필 받아오기
        System.out.println("getAttributes = " + super.loadUser(userRequest).getAttributes());  // 사용자 정보

        OAuth2User oAuth2User = super.loadUser(userRequest);
        return super.loadUser(userRequest);
    }
}
