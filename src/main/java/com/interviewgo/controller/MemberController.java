package com.interviewgo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.MemberDTO;
import com.interviewgo.jwt.JwtTokenProvider;
import com.interviewgo.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController                  // REST API 컨트롤러
@RequiredArgsConstructor         // final 필드 생성자 주입
public class MemberController {

    // 회원 관련 비즈니스 로직 처리
    private final MemberService memberService;
    
    // JWT 토큰 생성 담당
    private final JwtTokenProvider jwtTokenProvider;
    
    // Spring Security 인증 처리 매니저
    private final AuthenticationManager authenticationManager;
    
    // ===============================
    // 회원가입 API
    // ===============================
    @PostMapping("/join")
    public ResponseEntity<String> signup(@RequestBody MemberDTO user) {
        
        // 요청 확인용 로그
        System.out.println("컨트롤러 진입: " + user.getUsername());
        
        // 회원 정보 DB 저장
        int result = memberService.insertMember(user);
        
        // 처리 결과에 따른 응답 반환
        if(result == 1) {
            return ResponseEntity.ok("회원가입 성공");
        } else {
            return ResponseEntity.status(500).body("회원가입 실패");
        }
    }
    
    // ===============================
    // 로그인 API (JWT 발급)
    // ===============================
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody MemberDTO user) {
        
        // 로그인 요청 확인용 로그
        System.out.println("login 컨트롤러 진입 - ID: " + user.getUsername());
        
        try {
            // 1️⃣ 사용자 인증 시도
            // - username + password 를 기반으로 인증 요청
            // - 내부적으로 CustomUserDetailsService + PasswordEncoder 사용
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getMb_password()
                )
            );

            // 2️⃣ 인증 성공 시 JWT 토큰 생성
            // - authentication.getName() == username
            // - role 은 현재 고정값 USER
            String token = jwtTokenProvider.createToken(
                authentication.getName(),
                "USER"
            );
            
            System.out.println("✅ 인증 성공! Token: " + token);
            
            // 3️⃣ DB에서 로그인한 사용자 정보 다시 조회
            // - 인증 객체에는 상세 사용자 정보가 부족하기 때문에
            // - 프론트엔드에 내려줄 데이터를 위해 별도 조회 필요
            MemberDTO loginMember =
                memberService.getMemberByUsername(user.getUsername());
            
            // 4️⃣ 응답 데이터 구성
            // - token + message + user 정보 묶어서 반환
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "로그인 성공");

            // 5️⃣ 프론트엔드에서 사용하는 user 객체 구성
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("mb_uid", loginMember.getMb_uid());     // 사용자 고유 ID
            userInfo.put("username", loginMember.getUsername());
            userInfo.put("mb_nickname", loginMember.getMb_nickname());
            userInfo.put("mb_pnumber", loginMember.getMb_pnumber());
            userInfo.put("role", loginMember.getRole());
            
            // response에 user 정보 포함
            response.put("user", userInfo); 

            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            // 비밀번호 불일치 또는 사용자 정보 오류
            System.out.println("❌ 비밀번호 불일치");
            return ResponseEntity
                .status(401)
                .body("아이디 또는 비밀번호가 틀렸습니다.");
            
        } catch (Exception e) {
            // 기타 서버 오류
            e.printStackTrace();
            return ResponseEntity
                .status(500)
                .body("서버 에러: " + e.getMessage());
        }
    }
}
