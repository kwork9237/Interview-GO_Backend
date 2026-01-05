package com.interviewgo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.FindPwResponse;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.jwt.JwtTokenProvider;
import com.interviewgo.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // =============================================================
    // 1. 아이디 중복 확인 (조원 코드에서 가져옴)
    // =============================================================
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkIdDuplicate(@RequestParam("username") String username) {
        boolean isAvailable = memberService.isUsernameAvailable(username);
        return ResponseEntity.ok(isAvailable);
    }

    // =============================================================
    // 2. 회원가입
    // =============================================================
    @PostMapping("/join")
    public ResponseEntity<String> signup(@RequestBody MemberDTO user) {
        // 요청 확인용 로그
        System.out.println("회원가입 요청: " + user.getUsername());
        
        // 회원 정보 DB 저장
        int result = memberService.insertMember(user);
        
        // 처리 결과에 따른 응답 반환
        if(result == 1) {
            return ResponseEntity.ok("회원가입 성공");
        } else {
            return ResponseEntity.status(500).body("회원가입 실패");
        }
    }

    // =============================================================
    // 3. 로그인 (내 코드 유지 - mb_icon 및 유저 정보 포함 필수!)
    // =============================================================
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody MemberDTO user) {
        System.out.println("로그인 요청: " + user.getUsername());
        
        try {
            // 1️⃣ 인증 수행
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getMb_password())
            );

            // 2️⃣ JWT 토큰 생성
            String token = jwtTokenProvider.createToken(authentication.getName(), "USER");
            
            // 3️⃣ DB에서 상세 정보 조회 (아이콘, 닉네임 등을 위해 필요)
            MemberDTO loginMember = memberService.getMemberByUsername(user.getUsername());
            
            // 4️⃣ 응답 데이터 구성 (Token + User Info)
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "로그인 성공");

            // 프론트엔드가 사용할 유저 정보 Map 구성
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("mb_uid", loginMember.getMb_uid());
            userInfo.put("username", loginMember.getUsername());
            userInfo.put("mb_nickname", loginMember.getMb_nickname());
            userInfo.put("mb_pnumber", loginMember.getMb_pnumber());
            userInfo.put("mb_icon", loginMember.getMb_icon()); // ✅ 핵심: 아이콘 정보 포함
            userInfo.put("role", loginMember.getRole());
            
            response.put("user", userInfo);

            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 틀렸습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 에러: " + e.getMessage());
        }
    }

    // =============================================================
    // 4. 비밀번호 찾기 (조원 코드에서 가져옴)
    // =============================================================
    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestBody MemberDTO request) {
        try {
            // 임시 비밀번호 생성 서비스 호출
            String tempPw = memberService.createTempPassword(request);
            
            // 성공 응답
            return ResponseEntity.ok(new FindPwResponse(tempPw, "임시 비밀번호 발급 성공"));

        } catch (IllegalArgumentException e) {
            // 일치하는 회원 없음
            return ResponseEntity.badRequest().body(new FindPwResponse(null, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new FindPwResponse(null, "서버 오류 발생"));
        }
    }
}