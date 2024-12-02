package com.cos.spring_security1.config.oauth;

import com.cos.spring_security1.config.auth.PrincipalDetails;
import com.cos.spring_security1.config.oauth.provider.GoogleUserInfo;
import com.cos.spring_security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.spring_security1.model.User;
import com.cos.spring_security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


// 여기서 구글 로그인이 완료된 후의 후처리를 해줘야 함
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public PrincipalOauth2UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    // 구글로부터 받은 userRequest 데이터(액세스 토큰 + 사용자 프로필 정보)에 대한 후처리되는 함수
    // 함수 종료 시, @AuthenticationPrincipal 어노테이션이 만들어진다
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration = " + userRequest.getClientRegistration());  // registrationId로 어떤 OAuth로 로그인했는지 확인 가능
        System.out.println("getAccessToken = " + userRequest.getAccessToken().getTokenValue());

        /* 구글로그인 버튼 클릭 -> 구글 로그인 page -> 로그인 완료 -> code 리턴(리턴을 oauth2-client 라이브러리가 받아준다) -> 코드를 통해서 access token 요청
           userRequest 정보 -> userRequest 정보를 사용해 loadUser 함수를 호출해서 구글로부터 회원 프로필 받아오기 */
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("getAttributes = " + super.loadUser(userRequest).getAttributes());  // 사용자 정보

        // 강제로 회원가입
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else {
            System.out.println("우리는 구글 로그인만 지원해요!");
        }

//        String provider = userRequest.getClientRegistration().getRegistrationId();  // google
        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;  // google_{providerId}
        String password = bCryptPasswordEncoder.encode("비밀번호");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            System.out.println("구글 로그인이 최초입니다.");
            userEntity = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .provider(provider)
                .providerId(providerId)
                .build();

            userRepository.save(userEntity);
        } else {
            System.out.println("로그인을 이미 한 적이 있습니다. 당신은 자동 회원가입이 되어 있습니다.");
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
