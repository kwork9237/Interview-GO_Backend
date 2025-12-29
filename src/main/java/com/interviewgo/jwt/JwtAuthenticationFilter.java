// 확인을 위해 주석처리
/*
 * package com.interviewgo.jwt;
 * 
 * import java.io.IOException;
 * 
 * import org.springframework.security.authentication.
 * UsernamePasswordAuthenticationToken; import
 * org.springframework.security.core.context.SecurityContextHolder; import
 * org.springframework.web.filter.OncePerRequestFilter;
 * 
 * import com.interviewgo.service.jwt.CustomUserDetails; import
 * com.interviewgo.service.jwt.CustomUserDetailsService;
 * 
 * import jakarta.servlet.FilterChain; import jakarta.servlet.ServletException;
 * import jakarta.servlet.http.HttpServletRequest; import
 * jakarta.servlet.http.HttpServletResponse;
 * 
 * public class JwtAuthenticationFilter extends OncePerRequestFilter { private
 * final JwtTokenProvider jwtTokenProvider; private final
 * CustomUserDetailsService userDetailsService;
 * 
 * public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
 * CustomUserDetailsService userDetailsService) { this.jwtTokenProvider =
 * jwtTokenProvider; this.userDetailsService = userDetailsService; }
 * 
 * @Override protected void doFilterInternal(HttpServletRequest request,
 * HttpServletResponse response, FilterChain filterChain) throws
 * ServletException, IOException {
 * 
 * // 1) Authorization 헤더 읽기 (예: "Authorization: Bearer eyJhbGci...") String
 * header = request.getHeader("Authorization");
 * 
 * // 2) 헤더가 존재하고 "Bearer "로 시작하는지 확인 if (header != null &&
 * header.startsWith("Bearer ")) { // "Bearer " 접두사를 제거하여 실제 토큰 문자열을 얻음 String
 * token = header.substring(7); try { // 3) 토큰 유효성 검사 (서명, 만료시간 등) if
 * (jwtTokenProvider.validateToken(token)) { // 4) 토큰에서 username(또는 식별자) 추출
 * String username = jwtTokenProvider.getUsername(token);
 * System.out.println("[JWT 필터] 유효한 토큰입니다. username: " + username);
 * 
 * // 5) username 기반으로 UserDetails 로드 (DB에서 사용자 조회) CustomUserDetails
 * userDetails = (CustomUserDetails)
 * userDetailsService.loadUserByUsername(username);
 * 
 * // 6) Authentication 객체 생성 // - principal: userDetails (인증된 사용자 정보) // -
 * credentials: null (이미 토큰으로 인증되었으므로 비밀번호는 사용하지 않음) // - authorities:
 * userDetails.getAuthorities() (권한/롤 정보) UsernamePasswordAuthenticationToken
 * authentication = new UsernamePasswordAuthenticationToken( userDetails, null,
 * userDetails.getAuthorities() );
 * 
 * // 7) SecurityContext 에 인증 객체를 저장 — 이후의 Spring Security 처리에서 "인증된 사용자"로 인식됨
 * SecurityContextHolder.getContext().setAuthentication(authentication); } else
 * { System.out.println("[JWT 필터] 유효하지 않은 토큰입니다."); } } catch (Exception e) {
 * System.out.println("[JWT 필터] 예외 발생: " + e.getMessage()); } }
 * 
 * // 8) 다음 필터로 요청을 전달 filterChain.doFilter(request, response); } }
 */