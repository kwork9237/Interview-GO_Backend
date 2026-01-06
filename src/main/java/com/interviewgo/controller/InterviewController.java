package com.interviewgo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dao.interview.InterviewDAO;
import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.dto.interview.InterviewSessionDTO;
import com.interviewgo.dto.interview.InterviewSetupDTO;
import com.interviewgo.service.jwt.CustomUserDetails;
import com.interviewgo.utils.UUIDUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {
	private final InterviewDAO dao;
	
	// 면접 시작시 호출 (ssid 생성)
	@PostMapping("/setup")
	public ResponseEntity<?> setupInterview(@RequestBody InterviewSetupDTO requestData) {
        // UUID 생성
        String sessionId = UUIDUtils.GenerateUUID();

        // DB 저장 (interview_session 테이블)
        // 만약 드물게도 session 중복시 오류나면 try-catch로 간단하게 로깅 후, sessionId만 다시 생성
        // 만약 비회원일 경우 session data를 정기적으로 제거할 것인지 확인 필요
        InterviewSessionDTO sessionData = new InterviewSessionDTO();
        sessionData.setIv_ssid(sessionId);
        sessionData.setMb_uid(requestData.getMb_uid());
        dao.insertInterviewSession(sessionData);
        
        // API SERVER 로그
        System.out.println("[InterviewController] Setup Processing End");
        
        // 4. 생성된 UUID 반환
        return ResponseEntity.ok(Map.of("sid", sessionId));
    }
	
	
	// 면접 시작시 호출
	@PostMapping("/start")
	public ResponseEntity<?> startInterview(
			@RequestParam(value="sid") String ssid,
			@RequestParam(value="uid") Long mbUid
	) {
		Map<String, Object> response = new HashMap<>();
		
		// 검증
		if (!UUIDUtils.isValid(ssid) && !ssid.equals("test")) {
			// API SERVER 로그
	        System.out.println("[InterviewController] Invalid Start SSID Error");
	        System.out.println("ssid >> " + ssid);
	        
	        response.put("message", "유효하지 않은 세션입니다.");
			
			// 2026 01 05
			// 현재 32자이기만 하면 바로 session이 통과됨
			// DB 조회 후에 유효한 ssid, uid, step 인지 검증해야함.
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
		
		// 면접 시작
        String firstMsg = "안녕하세요. 자기소개 부탁드립니다.";

        // 마지막 대화 기록 불러오기
        InterviewHistoryDTO dto = dao.getLastInterviewHistoryBySsid(ssid);
        
        // 대화 기록이 없으면 기본 대화기록 삽입.
        if(dto == null) {
        	dto = new InterviewHistoryDTO();

            dto.setIv_ssid(ssid);
            
            // 26 01 06 mb uid 관련 수정 필요
            dto.setMb_uid(mbUid);
            dto.setIv_step((short) 1);
            dto.setIv_context(firstMsg);
            dto.setIv_score(0);
            dto.setIv_feedback("");

            try {
            	dao.insertInterviewHistory(dto);
            }
            catch(Exception e) {
            	System.out.println("DB에 중복되는 ssid 및 step를 insert 시도함");
            }
        }
        
        // 처음 대화값 삽입
        response.put("text", firstMsg);
        
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
		if(!UUIDUtils.isValid(ssid) && !ssid.equals("test")) {
			System.out.println("[InterviewController] Invalid History SSID Error");
			System.out.println("ssid >> " + ssid);

			return new ResponseEntity<>(
					Map.of("message", "유효하지 않은 세션입니다."),
					HttpStatus.BAD_REQUEST);
		}
		
		// 대회내역 불러오기
		List<InterviewHistoryDTO> hist = dao.getAllInterviewHistoryBySsid(ssid);
		
		return ResponseEntity.ok(Map.of("data", hist));
	}
	
	
	// 면접 포기 등
	@DeleteMapping("/dropout")
	public ResponseEntity<?> dropOutInterview(@RequestParam(value="sid") String ssid) {
		if(!UUIDUtils.isValid(ssid) && !ssid.equals("test")) {
			System.out.println("[InterviewController] Invalid History SSID Error");
			System.out.println("ssid >> " + ssid);

			return new ResponseEntity<>(
					Map.of("message", "유효하지 않은 세션입니다."),
					HttpStatus.BAD_REQUEST);
		}
		
		dao.dropOutInterviewSession(ssid);
		
		return ResponseEntity.ok(Map.of("res", "ok"));
	}
}
