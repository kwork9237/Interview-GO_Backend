package com.interviewgo.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import com.interviewgo.service.jwt.CustomUserDetails;
import com.interviewgo.service.jwt.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {

        // 1. Authorization 헤더 읽기
        String header = request.getHeader("Authorization");

        // 2. Bearer 토큰 존재 여부 확인
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // 3. 토큰 유효성 검사
                if (jwtTokenProvider.validateToken(token)) {
                    // 4. 토큰에서 username 추출
                    String username = jwtTokenProvider.getUsername(token);
                    System.out.println("[JWT 필터] 유효한 토큰입니다. username: " + username);

                    // 5. UserDetails 로드
                    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

                    // 6. 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                           userDetails, null, userDetails.getAuthorities()
                    );

                    // 7. SecurityContext에 저장 (인증 완료)
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.out.println("[JWT 필터] 유효하지 않은 토큰입니다.");
                }
            } catch (Exception e) {
                System.out.println("[JWT 필터] 예외 발생: " + e.getMessage());
            }
        }

        // 8. 다음 필터로 전달
        filterChain.doFilter(request, response);
    }
    
    // 필터링 제외
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = request.getServletPath();
//        
//        // 패턴을 리스트로 관리하면 편리합니다.
//        List<String> skipPaths = List.of(
//            "/api/ai/**",
//            "/api/interview/**"
//        );
//
//        return skipPaths.stream()
//                .anyMatch(p -> pathMatcher.match(p, path));
//    }
}