package com.interviewgo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.service.InterviewService;
import com.interviewgo.service.jwt.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {
	private final InterviewService service;
	
	// 면접 시작시 호출 (ssid 생성)
	@PostMapping("/setup")
	public ResponseEntity<?> setupInterview(Authentication auth) {

		// 토큰에서 member uid 추출
		CustomUserDetails data = (CustomUserDetails) auth.getPrincipal();
		Long mbUid = data.getMb_uid();
		
		System.out.println(mbUid);

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
			Authentication auth,
			@RequestParam(value="sid") String ssid
			) {
		
		// 토큰에서 멤버 uid 추출
		CustomUserDetails data = (CustomUserDetails) auth.getPrincipal();
		Long mbUid = data.getMb_uid();
		
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
			Authentication auth,
			@RequestParam(value="sid") String ssid
			) {

		CustomUserDetails data = (CustomUserDetails) auth.getPrincipal();
		Long mbUid = data.getMb_uid();

		// 대회내역 불러온 것을 바로 반환		
		return ResponseEntity.ok(Map.of("data", service.getHistory(ssid, mbUid)));
	}
	
	
	// 면접 포기 등
	@DeleteMapping("/dropout")
	public ResponseEntity<?> dropOutInterview(
			Authentication auth,
			@RequestParam(value="sid") String ssid
			) {
		
		CustomUserDetails data = (CustomUserDetails) auth.getPrincipal();
		Long mbUid = data.getMb_uid();
		
		service.dropOutInterviewSession(ssid, mbUid);
		
		return ResponseEntity.ok(Map.of("res", "ok"));
	}
}
