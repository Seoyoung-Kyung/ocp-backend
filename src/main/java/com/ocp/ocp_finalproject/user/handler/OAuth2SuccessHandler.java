package com.ocp.ocp_finalproject.user.handler;

import com.ocp.ocp_finalproject.common.config.oauth.OAuth2Properties;
import com.ocp.ocp_finalproject.user.domain.Auth;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.user.enums.AuthProvider;
import com.ocp.ocp_finalproject.user.repository.AuthRepository;
import com.ocp.ocp_finalproject.user.service.oauth2.OAuth2UserInfo;
import com.ocp.ocp_finalproject.user.service.oauth2.OAuth2UserInfoFactory;
import com.ocp.ocp_finalproject.user.util.RedirectUrlBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthRepository authRepository;
    private final OAuth2Properties oAuth2Properties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException{
        log.info("OAuth 로그인 성공");

        try{
            // 1. OAuth2User에서 정보 추출
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            UserPrincipal userPrincipal = (UserPrincipal) oauthToken.getPrincipal();

            User user = userPrincipal.getUser();
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();
            AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

            log.info("Provider: {}, UserId: {}", provider, user.getId());

            // 2. OAuth2UserInfoFactory 사용
            OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, userPrincipal.getAttributes());
            String providerUserId = userInfo.getProviderId();

            // 3. DB에서 사용자 조회
            Auth auth = authRepository.findByProviderAndProviderUserId(provider, providerUserId)
                    .orElseThrow(()->new IllegalStateException("인증 정보를 찾을 수 없음"));

            // 4. 신규 가입 여부 확인
            boolean isNewUser = auth.isNewUser();

            // 5. 리다이렉트
            String successRedirectUrl = RedirectUrlBuilder.buildSuccessUrl(
                    oAuth2Properties.getSuccessUrl(),
                    user.getId(),
                    isNewUser
            );

            // 프론트엔드로 리다이렉트 (세션 쿠키가 자동으로 설정됨)
            String redirectUrl = successRedirectUrl + "&success=true";

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        }catch (Exception e){
            log.error("로그인 성공 처리 중 에러 발생", e);

            String errorUrl = RedirectUrlBuilder.buildFailureUrl(oAuth2Properties.getFailureUrl(), "로그인 처리 중 오류 발생");
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}
