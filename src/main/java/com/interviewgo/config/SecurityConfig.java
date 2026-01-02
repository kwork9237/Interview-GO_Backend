package com.interviewgo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsUtils; // 추가된 임포트

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
            // 1. CORS 설정 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//          .cors(cors -> cors.disable()) // 개발 중엔 disable, 운영 시 필요하면 enable
            
            // 2. CSRF 비활성화
              .csrf(AbstractHttpConfigurer::disable)
//            .csrf().disable() // CSRF 비활성화
            
            // 3. 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                // Pre-flight Request(OPTIONS)는 무조건 허용
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // 로그인, 회원가입, 메인 페이지는 인증 없이 접근 가능
//                .requestMatchers("/**").permitAll()
              .requestMatchers("/join", "/login", "/find-password", "/").permitAll()
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // 4. 불필요한 기본 설정 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            
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
    
    // CORS 설정을 위한 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. 프론트엔드 주소 허용 (정확한 주소 필수)
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        
        // 2. 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // 3. 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));
        
        // 4. [중요] 클라이언트(React)에서 접근할 수 있는 헤더 지정
        // 이 설정이 없으면 프론트에서 Authorization 헤더를 읽지 못하거나 CORS 에러가 발생할 수 있음
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Set-Cookie");
        
        // 5. 쿠키/인증 정보 포함 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}