package com.interviewgo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.jwt.JwtTokenProvider;
import com.interviewgo.dto.FindPwResponse;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    
    // username 중복검사
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkIdDuplicate(@RequestParam("username") String username) {
        boolean isAvailable = memberService.isUsernameAvailable(username);
        return ResponseEntity.ok(isAvailable);
    }
    
	@PostMapping("/join")
	 public ResponseEntity<String> signup(@RequestBody MemberDTO user) {
		System.out.println("컨트롤로 진입");
		System.out.println("username:"+ user.getUsername());
		System.out.println("mb_password:"+ user.getMb_password());
		System.out.println("mb_nickname:"+ user.getMb_nickname());
		System.out.println("mb_pnumber:"+ user.getMb_pnumber());
		
        int result = memberService.insertMember(user);
        if(result == 1) System.out.println("회원가입 성공");
        
        
        return ResponseEntity.ok("회원가입 성공");
    }
	
	// 로그인 API - JWT 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO user) {
    	System.out.println("login 컨트롤로 진입");

    	System.out.println("UNAME " + user.getUsername());
    	System.out.println("UPASSWORD " + user.getMb_password());
    	
        // 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getMb_password()));

        // 인증 성공 시 JWT 발급
        String token = jwtTokenProvider.createToken(authentication.getName(), "USER");
        
        System.out.println("token:"+ token);
        
//        return ResponseEntity.ok().body(new TokenResponse(token)); // 토큰을 JSON 형태로 반환(key가 있는 형태)
		return ResponseEntity.ok(token);                             // token 문자열만 반환(key가 없는 형태)   
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
