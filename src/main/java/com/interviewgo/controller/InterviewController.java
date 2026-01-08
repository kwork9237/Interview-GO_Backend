package com.interviewgo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.jwt.JwtTokenProvider;
import com.interviewgo.service.InterviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {
	private final InterviewService service;
	private final JwtTokenProvider provider;
	
	// 면접 시작시 호출 (ssid 생성)
	@PostMapping("/setup")
	public ResponseEntity<?> setupInterview(@RequestHeader(name = "Authorization") String token) {

		// 토큰에서 member uid 추출
		Long mbUid = provider.getMemberUid(token.substring(7));

		// member uid 삽입 및 생성된 세션 uuid 반환 (초기값은 db에 저장됨)
		String uuid = service.setupInterview(mbUid);
        
        // API SERVER 로그
        System.out.println("[InterviewController] Setup Processing End");
        
        // 4. 생성된 UUID 반환
        return ResponseEntity.ok(Map.of("sid", uuid));
    }
	
	
	// 면접 시작시 호출
	@GetMapping("/start")
	public ResponseEntity<?> startInterview(
			@RequestHeader(name = "Authorization") String token,
			@RequestParam(value="sid") String ssid
			) {
		
		// 토큰에서 멤버 uid 추출
		Long mbUid = provider.getMemberUid(token.substring(7));
		
		// service에서 초기 인터뷰 세팅
		String str = service.startInterview(ssid, mbUid);
        
        // API SERVER 로그
        System.out.println("[InterviewController] Start Processing End");
        
        // 2. 프론트에 첫 질문 던지기
        return ResponseEntity.ok(Map.of("text", str));
    }
	
	
	// 채팅 기록 유지 (새로고침 시 데이터 증발 방지)
	@GetMapping("/history")
	public ResponseEntity<?> getChatHistory(
			@RequestHeader(name = "Authorization") String token,
			@RequestParam(value="sid") String ssid
			) {

		Long mbUid = provider.getMemberUid(token.substring(7));

		// 대회내역 불러온 것을 바로 반환		
		return ResponseEntity.ok(Map.of("data", service.getHistory(ssid, mbUid)));
	}
	
	
	// 면접 포기 등
	@DeleteMapping("/dropout")
	public ResponseEntity<?> dropOutInterview(
			@RequestHeader(name = "Authorization") String token,
			@RequestParam(value="sid") String ssid
			) {
		
		Long mbUid = provider.getMemberUid(token.substring(7));
		
		service.dropOutInterviewSession(ssid, mbUid);
		
		return ResponseEntity.ok(Map.of("res", "ok"));
	}
}
