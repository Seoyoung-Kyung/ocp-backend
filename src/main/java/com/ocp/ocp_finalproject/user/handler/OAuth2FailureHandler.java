package com.ocp.ocp_finalproject.user.handler;

import com.ocp.ocp_finalproject.common.config.oauth.OAuth2Properties;
import com.ocp.ocp_finalproject.user.util.RedirectUrlBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2Properties oAuth2Properties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("OAuth 로그인 실패 - {}", exception.getMessage(),exception);

        String message = getErrorMessage(exception); // 내부 구현 노출을 막기 위해 정의된 메시지 사용
        String errorMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String redirectUrl = oAuth2Properties.getFailureUrl() + "?success=false&error=" + errorMessage;

        log.info("로그인 실패 리다이렉트 : {}", redirectUrl);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String getErrorMessage(AuthenticationException exception) {
        String exceptionClass =exception.getClass().getSimpleName();

        return switch (exceptionClass){
            case "OAuth2AuthenticationException" -> "소셜 로그인 인증에 실패했습니다";
            case "InsufficientAuthenticationException" -> "인증 정보가 부족합니다";
            case "BadCredentialsException" -> "잘못된 인증 정보입니다";
            default -> "로그인에 실패했습니다";
        };
    }
}
