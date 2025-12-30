package com.interviewgo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        	// 
        	.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 1. CSRF 비활성화 (Rest API 기준)
            .csrf(AbstractHttpConfigurer::disable)
            
            // 3. 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll() // 인증 없이 접근 가능
                .anyRequest().authenticated()                  // 나머지는 인증 필요
            )
            
            // 4. 기본 로그인 폼 사용 (필요 없으면 disable 가능)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);
            
            // JWT 필터 등록 (개발용으로 잠깐 꺼둠)
//            .addFilterBefore(
//            		new JwtAuthenticationFilter(jwtTokenProvider, userDetailService),
//            		UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정을 위한 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 주소 허용 (3000번 포트)
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));
        
        // 쿠키나 인증 정보를 포함할지 여부
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}