package com.officeapp.graph_mail_backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    // ✅ 허용할 프론트엔드 Origin (Vite 개발 서버 주소)
    private static final List<String> allowedOrigins = List.of(
            "http://localhost:5173" // Vite 기본 포트
    );

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (REST API용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
                .authorizeHttpRequests(auth -> auth
                        // ✅ POST 요청 포함하여 명확하게 허용
                        .requestMatchers(HttpMethod.POST, "/api/generate-license").permitAll()
                        .requestMatchers("/api/schools", "/api/schools/**").permitAll()
                        .requestMatchers("/api/contract", "/api/contract/**").permitAll()
                        .requestMatchers("/api/files/**").permitAll()
                        .requestMatchers("/api/templates/**").permitAll()
                        .requestMatchers("/api/note-templates/**").permitAll()
                        .requestMatchers("/api/tax-invoice/**").permitAll()
                        .requestMatchers("/user-insert/**").permitAll()
                        .requestMatchers("/upload/**").permitAll()
                        .requestMatchers("/", "/login/**", "/oauth2/**", "/error").permitAll()
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
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
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins); // ✅ 허용된 프론트 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // ✅ 허용 메서드
        configuration.setAllowedHeaders(List.of("*")); // ✅ 모든 헤더 허용
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition")); // ✅ 다운받을 때 필요한 헤더 노출
        configuration.setAllowCredentials(true); // ✅ 인증 포함 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // ✅ 모든 경로에 위 설정 적용
        return source;
    }
}
