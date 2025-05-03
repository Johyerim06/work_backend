package com.officeapp.graph_mail_backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    // 허용할 Origin
    private static final List<String> allowedOrigins = List.of(
            "http://localhost:5173" // Vite 기본 포트
    );

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (REST API용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로
                        .requestMatchers(
                                "/api/schools/**",
                                "/api/contract/**",
                                "/api/files/**",
                                "/api/templates/**",
                                "/user-insert",
                                "/upload/**",
                                "/",
                                "/login/**",
                                "/oauth2/**",
                                "/error" // 인증 실패 시 자동 리디렉션되는 경로
                        ).permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 이동할 경로
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 이동할 경로
                );

        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins); // 허용 Origin
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // 허용 메서드
        config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        config.setExposedHeaders(List.of("Authorization", "Content-Disposition")); // 응답 헤더 노출
        config.setAllowCredentials(true); // 쿠키 허용 (프론트와 세션 공유할 때 필요)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
