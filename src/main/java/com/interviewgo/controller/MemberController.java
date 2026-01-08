package com.interviewgo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.FindPwResponse;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.dto.LoginResponseDTO;
import com.interviewgo.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    // username 중복검사
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkIdDuplicate(@RequestParam("username") String username) {
        boolean isAvailable = memberService.isUsernameAvailable(username);
        return ResponseEntity.ok(isAvailable);
    }


    // 회원가입
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

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO user) {
    	System.out.println("login 컨트롤로 진입");

    	System.out.println("UNAME " + user.getUsername());
    	System.out.println("UPASSWORD " + user.getMb_password());
    	
    	try {
    		// 인증 시도
    		LoginResponseDTO response = memberService.loginProcess(user.getUsername(), user.getMb_password());
            return ResponseEntity.ok(response);
        } 
    	catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 틀렸습니다.");
        } 
    	catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 에러: " + e.getMessage());
        }
    }
    
    // 비번 찾기
    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestBody MemberDTO request) {
        try {
            // 서비스 호출
            String tempPw = memberService.createTempPassword(request);
            
            // 성공 시: { "tempPassword": "0000", "message": "..." } 반환
            return ResponseEntity.ok(new FindPwResponse(tempPw, "임시 비밀번호 발급 성공"));

        } catch (IllegalArgumentException e) {
            // 유저 없음 에러 (400 Bad Request)

            return ResponseEntity.badRequest().body(new FindPwResponse(null, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new FindPwResponse(null, "서버 오류 발생"));
        }
    }
}
