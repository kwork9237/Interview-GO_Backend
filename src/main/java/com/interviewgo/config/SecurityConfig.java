package com.interviewgo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailService;
    
    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CORS & CSRF 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. 세션 비활성화 (JWT 사용 시 필수)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 3. 기본 로그인 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // 4. 요청 권한 설정 (여기가 핵심!)
            .authorizeHttpRequests(auth -> auth
                // Pre-flight Request 허용
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                
                // 정적 리소스 접근 허용
                .requestMatchers("/images/**", "/static/**", "/css/**", "/js/**").permitAll()
                
                // 로그인, 회원가입 등 허용
                .requestMatchers("/join", "/login", "/find-password", "/check-id", "/").permitAll()
                
                // 유튜브, 취업 허용
                .requestMatchers("/api/work24/list", "/api/wordcloud/list", "/api/youtube/check").permitAll()
                
                // AI, 면접 관련 허용
                .requestMatchers("/api/ai/**", "/api/interview/**").permitAll()
                
                // 🚨 [추가된 부분] 아이콘 목록 조회는 로그인 없이도(또는 토큰 에러나도) 볼 수 있게 허용!
                .requestMatchers("/api/mypage/default-icons").permitAll()
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // 5. JWT 필터 등록
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider, userDetailService),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Set-Cookie");
        
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}