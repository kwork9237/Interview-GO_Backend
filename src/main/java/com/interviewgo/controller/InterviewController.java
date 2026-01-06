package com.interviewgo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.interview.InterviewSetupDTO;
import com.interviewgo.service.InterviewService;
import com.interviewgo.utils.UUIDUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {
	private final InterviewService service;
	
	// 면접 시작시 호출 (ssid 생성)
	@PostMapping("/setup")
	public ResponseEntity<?> setupInterview(@RequestBody InterviewSetupDTO requestData) {

		// request에 들어온 member uid 삽입 및 생성된 세션 uuid 반환
		String uuid = service.setupInterview(requestData.getMb_uid());
        
        // API SERVER 로그
        System.out.println("[InterviewController] Setup Processing End");
        
        // 4. 생성된 UUID 반환
        return ResponseEntity.ok(Map.of("sid", uuid));
    }
	
	
	// 면접 시작시 호출
	@PostMapping("/start")
	public ResponseEntity<?> startInterview(
			@RequestParam(value="sid") String ssid,
			@RequestParam(value="uid") Long mbUid
	) {
		Map<String, Object> response = new HashMap<>();
		
		// 검증
		if (!UUIDUtil.isValid(ssid) && !ssid.equals("test")) {
			// API SERVER 로그
	        System.out.println("[InterviewController] Invalid Start SSID Error");
	        
	        response.put("message", "유효하지 않은 세션입니다.");
			
			// 2026 01 05
			// 현재 32자이기만 하면 바로 session이 통과됨
			// DB 조회 후에 유효한 ssid, uid, step 인지 검증해야함.
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
		
		// service에서 초기 인터뷰 세팅
		String str = service.startInterview(ssid, mbUid);
        
        // 처음 대화값 삽입
        response.put("text", str);
        
        // API SERVER 로그
        System.out.println("[InterviewController] Start Processing End");
        
        // 2. 프론트에 첫 질문 던지기
        return ResponseEntity.ok(response);
    }
	
	
	// 채팅 기록 유지 (새로고침 시 데이터 증발 방지)
	@GetMapping("/history")
	public ResponseEntity<?> getChatHistory(@RequestParam(value="sid") String ssid) {
		// 대화내역이 없는 경우
		// test는 디버그 용도임
		if(!UUIDUtil.isValid(ssid) && !ssid.equals("test")) {
			System.out.println("[InterviewController] Invalid History SSID Error");
			System.out.println("ssid >> " + ssid);

			return new ResponseEntity<>(
					Map.of("message", "유효하지 않은 세션입니다."),
					HttpStatus.BAD_REQUEST);
		}
		
		// 대회내역 불러온 것을 바로 반환		
		return ResponseEntity.ok(Map.of("data", service.getHistory(ssid)));
	}
	
	
	// 면접 포기 등
	@DeleteMapping("/dropout")
	public ResponseEntity<?> dropOutInterview(@RequestParam(value="sid") String ssid) {
		if(!UUIDUtil.isValid(ssid) && !ssid.equals("test")) {
			System.out.println("[InterviewController] Invalid History SSID Error");
			System.out.println("ssid >> " + ssid);

			return new ResponseEntity<>(
					Map.of("message", "유효하지 않은 세션입니다."),
					HttpStatus.BAD_REQUEST);
		}
		
		service.dropOutInterviewSession(ssid);
		
		return ResponseEntity.ok(Map.of("res", "ok"));
	}
}
