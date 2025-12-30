package com.interviewgo.controller.interview;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.controller.AIAPIController;
import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.mapper.interview.InterviewHistoryMapper;
import com.interviewgo.service.jwt.CustomUserDetails;
import com.interviewgo.utils.UUIDUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {
	
	// 빠른 테스트용.
	private final InterviewHistoryMapper interviewHistoryDAO;
	private final AIAPIController aiapi;
	
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
        
        
        System.out.println("InterviewController (Setup) >> " + sessionId);

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

        InterviewHistoryDTO dto = new InterviewHistoryDTO();
        
        dto.setIv_ssid(ssid);
        dto.setMb_uid(0L);
        dto.setIv_step((short) 1);
        dto.setIv_context(firstMsg);
        dto.setIv_score(0);
        dto.setIv_feedback("");
        
        // db에 정보 삽입
        interviewHistoryDAO.insertInterviewHistory(dto);
        
        // 2. 프론트에 첫 질문 던지기
        return ResponseEntity.ok(Map.of("text", firstMsg));
    }
	
	// 면접 질의응답
	@PostMapping("/answer")
	//  Map<String, Object> 반환타입
	public void ask(String query, String ssid) {
	    // 1. AI API 호출 (기존 chat-local 로직)
		aiapi.chatResponseLocal(query, ssid);

	    // 2. JSON 파싱 예시 (간단하게 Map으로 변환)
	    // Map<String, Object> aiResult = objectMapper.readValue(rawJson, Map.class);
	    
	    // 3. Mapper 직접 호출해서 저장
	    // interviewMapper.insertResponse(ssid, query, aiResult.get("answer"), aiResult.get("score"), aiResult.get("feedback"));

//	    return Map.of("data", rawJson); // 프론트에는 원본 혹은 가공된 데이터 전달
	}
}
