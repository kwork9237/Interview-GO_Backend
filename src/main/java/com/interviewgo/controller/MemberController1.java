package com.interviewgo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.jwt.JwtTokenProvider;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
	
	@PostMapping("/join")
	 public ResponseEntity<String> signup(@RequestBody MemberDTO user) {
		System.out.println("컨트롤로 진입");
		System.out.println("username:"+ user.getUsername());
		System.out.println("mb_password:"+ user.getMb_password());
		System.out.println("mb_nickname:"+ user.getMb_nickname());
		System.out.println("mb_pnumber:"+ user.getMb_pnumber());
		
        int result =memberService.insertMember(user);
        if(result == 1) System.out.println("회원가입 성공");
        
        
        return ResponseEntity.ok("회원가입 성공");
    }
	
	// 로그인 API - JWT 발급
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody MemberDTO user) {
    	System.out.println("login 컨트롤로 진입");
    	
        // 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getMb_password()));

        // 인증 성공 시 JWT 발급
        String token = jwtTokenProvider.createToken(authentication.getName(), "USER");
        
        System.out.println("token:"+ token);
        
//        return ResponseEntity.ok().body(new TokenResponse(token)); // 토큰을 JSON 형태로 반환(key가 있는 형태)
		return ResponseEntity.ok(token);                             // token 문자열만 반환(key가 없는 형태)   
    }
	
}
