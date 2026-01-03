package com.ocp.ocp_finalproject.user.service;

import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.user.enums.AuthProvider;
import com.ocp.ocp_finalproject.user.service.oauth2.OAuth2UserInfo;
import com.ocp.ocp_finalproject.user.service.oauth2.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * 로그인 처리 서비스
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserOAuth2Service userOAuth2Service;

    /**
     * OAuth 로그인 성공시 호출되는 메서드
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 소셜 플랫폼에서 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 어떤 소셜 로그인인지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

        // 3. 사용자 정보 추출
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        // 4. DB에 저장 OR 업데이트
        User user = userOAuth2Service.processOAuth2User(provider,userInfo);

        // 5. Spring Security에서 사용할 Principal 객체 변환
        String nameAttributeKey = OAuth2UserInfoFactory.getUserNameAttribute(provider);
        return new UserPrincipal(user, oAuth2User.getAttributes(), nameAttributeKey);
    }
}
