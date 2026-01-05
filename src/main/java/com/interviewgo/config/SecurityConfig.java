package com.interviewgo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // JWT 사용 시 세션 비활성화
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.interviewgo.jwt.JwtAuthenticationFilter;
import com.interviewgo.jwt.JwtTokenProvider;
import com.interviewgo.service.jwt.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration              // 스프링 보안 설정 클래스
@EnableWebSecurity          // Spring Security 활성화
@RequiredArgsConstructor    // final 필드 생성자 주입
public class SecurityConfig {
    
    // JWT 토큰 생성 및 검증 담당
    private final JwtTokenProvider jwtTokenProvider;
    
    // 사용자 인증 정보 조회 서비스
    private final CustomUserDetailsService userDetailService;
    
    // 비밀번호 암호화 Bean
    // 회원가입 / 로그인 시 비밀번호 비교에 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Spring Security 핵심 필터 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1️⃣ CORS 설정 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2️⃣ CSRF 비활성화
            // JWT 기반 REST API에서는 CSRF 토큰을 사용하지 않음
            .csrf(AbstractHttpConfigurer::disable)
            
            // 3️⃣ 세션 비활성화
            // 서버에 인증 상태를 저장하지 않고 JWT로만 인증 처리
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 4️⃣ 기본 로그인 방식 비활성화
            // formLogin / httpBasic 은 세션 기반 인증 방식
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // 5️⃣ URL 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                // CORS 사전 요청(OPTIONS)은 항상 허용
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                
                // 이미지 폴더와 정적 리소스는 누구나 볼 수 있게 허용
                .requestMatchers("/images/**", "/static/**", "/css/**", "/js/**").permitAll()
                
                // 인증 없이 접근 가능한 API
                .requestMatchers("/join", "/login", "/").permitAll()
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // 6️⃣ JWT 인증 필터 등록
            // 요청마다 토큰을 검사하여 SecurityContext에 인증 정보 저장
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider, userDetailService),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
    
    // AuthenticationManager Bean
    // 로그인 시 인증 처리에 사용
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    // CORS 세부 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 프론트엔드 주소
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        );
        
        // 허용할 요청 헤더
        configuration.setAllowedHeaders(List.of("*"));
        
        // 클라이언트에서 접근 가능한 응답 헤더
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Set-Cookie");
        
        // 인증 정보 포함 요청 허용
        configuration.setAllowCredentials(true);

        // 모든 경로에 대해 CORS 적용
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
