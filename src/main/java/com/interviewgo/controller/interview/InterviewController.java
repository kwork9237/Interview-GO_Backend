package com.interviewgo.controller.interview;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.dto.interview.InterviewSessionDTO;
import com.interviewgo.mapper.interview.InterviewHistoryMapper;
import com.interviewgo.mapper.interview.InterviewSessionMapper;
import com.interviewgo.service.jwt.CustomUserDetails;
import com.interviewgo.utils.UUIDUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {
	
	// 빠른 테스트용.
	private final InterviewHistoryMapper HistoryDAO;
	private final InterviewSessionMapper SessionDAO;
	
	// 면접 시작을 하기 위한 기본조건
	@PostMapping("/setup")
	public ResponseEntity<?> setupInterview(
//        @RequestBody InterviewSetupRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails // JWT에서 추출한 유저 정보
    ) {
		
        // 1. 유저 ID 가져오기 (로그인이 안 된 상태라면 임시 ID 1 사용)
        Long userId = (userDetails != null) ? userDetails.getMb_uid() : 1L;

        // 2. UUID 생성
        String sessionId = UUIDUtils.GenerateUUID();

        // 3. DB 저장 (interview_temp 테이블)
        InterviewSessionDTO sessionData = new InterviewSessionDTO();
        sessionData.setIv_ssid(sessionId);
        sessionData.setMb_uid(userId);
        SessionDAO.insertInterviewSession(sessionData);

//        System.out.println("InterviewController (ssid) >> " + sessionId);
//        System.out.println("InterviewController (userid) >> " + userId); 

        // 4. 생성된 UUID 반환
        return ResponseEntity.ok(Map.of("sid", sessionId));
    }
	
	
	// 면접 시작시 호출
	@PostMapping("/start")
	public ResponseEntity<?> startInterview(@RequestParam(value="sid") String ssid) {
		// ssid 검증
		if (!UUIDUtils.isValid(ssid)) {
	        // 형식이 틀리면 여기서 바로 리턴 (에러를 던지든, 특정 상태코드를 주든 선택)
	        return ResponseEntity.badRequest().body("유효하지 않은 세션입니다.");
	    }
		
		// 면접 시작
        String firstMsg = "안녕하세요. 자기소개 부탁드립니다.";

        InterviewHistoryDTO dto = HistoryDAO.getInterviewHistoryById(ssid);
        
        if(dto == null) {
        	dto = new InterviewHistoryDTO();

            dto.setIv_ssid(ssid);
            dto.setMb_uid(0L);			// 임시값임
            dto.setIv_step((short) 1);
            dto.setIv_context(firstMsg);
            dto.setIv_score(0);
            dto.setIv_feedback("");

            try {
            	HistoryDAO.insertInterviewHistory(dto);
            }
            catch(Exception e) {
            	System.out.println("DB에 중복되는 ssid 및 step를 insert 시도함");
            }
        }
        
        // 2. 프론트에 첫 질문 던지기
        return ResponseEntity.ok(Map.of("text", firstMsg));
    }
	
	
	// 채팅 기록 유지 (조회)
	@GetMapping("/history")
	public ResponseEntity<?> getChatHistory(@RequestParam(value="sid") String ssid) {
		List<InterviewHistoryDTO> hist = HistoryDAO.getAllInterviewHistoryById(ssid);
		
		return ResponseEntity.ok(Map.of("data", hist));
	}
	
}
