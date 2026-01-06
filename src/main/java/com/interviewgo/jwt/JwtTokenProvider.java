package com.interviewgo.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	// Secret의 jwt 키
    @Value("${jwt.secret}")
    private String secretKey;

    // Secret의 jwt 만료기간
    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    // 토큰 생성
    // 1. Claims 생성 및 사용자명(subject) 설정
    // 2. role 같은 추가 클레임을 집어넣음
    // 3. 현재 시간으로 발급시간 설정, 만료시간 계산
    // 4. HMAC-SHA256 알고리즘으로 비밀키를 이용해 서명 후 compact()함수로 문자열(token) 반환
    public String createToken(String username, String role, Long mbUid) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        claims.put("mb_uid", mbUid);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)				// payload에 claims 설정
                .setIssuedAt(now)			    // 토큰 발급 시간
                .setExpiration(validity)		// 토큰 만료 시간	
                // 서명 : 비밀값과 함께 해시값을 HS256 알고리즘으로 암호화
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // 공통 파싱 메서드
    // 1. 토큰을 파싱(parseClaimsJws)하여 검증 후 claim body 반환
    // 2. 토큰이 유효하지 않으면 예외가 발생할 수 있음
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 공용 파싱 메서드에 토큰을 입력하여 사용자명 추출
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }
    
    // 사용자 UID 추출
    public Long getMemberUid(String token) {
    	return getClaims(token).get("mb_uid", Long.class);
    }
    
    // 토큰 유효성 검사
    // 1. parser로 토큰을 파싱하여 서명 검증/만료 검사 수행
    // 2. 예외 발생시 false 반환
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())   // 비밀키로 복호화
                .build()
                .parseClaimsJws(token);  	           // 토큰 파싱 및 검증	   
            return true;                               // 유효한 token이면 true 반환
            
        // 토큰이 만료되었거나 변조된 경우 예외 발생    
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
