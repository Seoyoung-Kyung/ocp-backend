package com.ocp.ocp_finalproject.common.config.oauth;

import com.ocp.ocp_finalproject.user.handler.OAuth2FailureHandler;
import com.ocp.ocp_finalproject.user.handler.OAuth2SuccessHandler;
import com.ocp.ocp_finalproject.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 설정
 * - 세션 기반 인증
 * - OAuth2 소셜 로그인
 * - CORS 설정
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ========== CSRF 설정 ==========
                .csrf(AbstractHttpConfigurer::disable)

                // ========== CORS 설정 ==========
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ========== 세션 관리 ==========
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?expired")
                )
                .sessionManagement(session -> session
                        .sessionFixation().changeSessionId()
                )
                // ========== 인증/인가 설정 ==========
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/test/**",
                                "/login/oauth2/**",
                                "/login/**",
                                "/"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/auth/me",
                                "/api/v1/auth/logout",
                                "/api/v1/auth/withdraw"
                        ).authenticated()

                        // GET 요청은 공개 (읽기 전용)
                        .requestMatchers(HttpMethod.GET, "/api/v1/workflows/**").permitAll()

                        // POST, PUT, DELETE는 로그인 필요 (쓰기 작업)
                        .requestMatchers(HttpMethod.POST, "/api/v1/workflows/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/workflows/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/workflows/**").authenticated()

                        // ⭐ Site Request 권한 추가
                        .requestMatchers(HttpMethod.POST, "/api/v1/site-requests").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/site-requests/my").authenticated()
                        .requestMatchers("/api/v1/site-requests/**").hasRole("ADMIN")

                        .anyRequest().permitAll()
                )

                // ========== OAuth2 ==========
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )

                // ========== 예외 처리 ==========
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, ex) -> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"success\":false,\"message\":\"인증이 필요합니다\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, ex) -> {
                            response.setStatus(403);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"success\":false,\"message\":\"접근 권한이 없습니다\"}"
                            );
                        })
                );

        return http.build();
    }

    /**
     * CORS 설정
     * - 프론트엔드와 백엔드가 다른 포트에서 실행될 때 필요
     * - 세션 쿠키 전송을 위해 allowCredentials(true) 필수!
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin (프론트엔드 URL)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",      // Vite 개발 서버
                "http://localhost:3000",      // React 개발 서버
                "http://localhost:8080"       // 백엔드 (테스트용)
        ));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));

        // 인증 정보 포함 허용 (세션 쿠키 전송 필수!)
        configuration.setAllowCredentials(true);

        // 노출할 헤더
        configuration.setExposedHeaders(List.of(
                "Authorization", "Set-Cookie"
        ));

        // preflight 요청 캐시 시간 (1시간)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCrypt 알고리즘
    }

}
