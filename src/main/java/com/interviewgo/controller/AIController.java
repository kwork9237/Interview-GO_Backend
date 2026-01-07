package com.interviewgo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.ai.AIRequestDTO;
import com.interviewgo.dto.ai.AIResponseDTO;
import com.interviewgo.jwt.JwtTokenProvider;
import com.interviewgo.service.ai.ApiAIService;
import com.interviewgo.service.ai.LocalAIService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AIController {
	private final LocalAIService localAi;
	private final ApiAIService apiAi;
	private final JwtTokenProvider provider;
	
	// API 모드
	// chat model 전용 답변
	@PostMapping("/server/chat")
	public ResponseEntity<?> chatResponseAPI(
			@RequestHeader("Authorization") String token,
			@RequestBody AIRequestDTO data
			) {

		// 토큰에서 member uid 추출
		Long mbUid = provider.getMemberUid(token.substring(7));
		
		// Service에 처리 요청
		AIResponseDTO.Chat res = apiAi.requestGemini(data.getQuery(), data.getUuid(), mbUid);

		return ResponseEntity.ok(Map.of("data", res));
	}
	
	// chat model + tts 처리
	// 위쪽 목소리 성별 및 말하기 속도 설정 변수 필요함. (기능구현 고민중)
	// 만약 TTS Service 구현이 제외되면 해당 Mapping는 필요없음
//	@PostMapping("/server/tts")
//	public ResponseEntity<?> ttsResponseAPI(
//			@RequestHeader("Authorization") String token,
//			@RequestBody AIRequestDTO data
//			) throws Exception {
//
//		// 토큰 검증
//		GlobalCheckUtil.checkToken(token);
//		GlobalCheckUtil.checkUuid(data.getUuid());
//		
//		// 토큰에서 member uid 추출
//		Long mbUid = provider.getMemberUid(token.substring(7));
//		
//		// Service에 처리 요청
//		AIResponseDTO.Chat res = apiAi.requestGemini(data.getQuery(), data.getUuid(), mbUid);
//		byte[] audoiBytes = apiAi.requestGoogleTTS(res.getAnswer());
//
//		// 결과값 반환
//		Map<String, Object> response = new HashMap<>();
//		response.put("data", res);
//		response.put("audio", Base64.getEncoder().encodeToString(audoiBytes));
//		
//		return ResponseEntity.ok(response);
//	}
	
	// Local API 모드
	@PostMapping("/local/chat")
	public ResponseEntity<?> chatResponseLocal(
			@RequestHeader("Authorization") String token,
			@RequestBody AIRequestDTO data
			) {

		// 토큰에서 member uid 추출
		Long mbUid = provider.getMemberUid(token.substring(7));
		
	    // 결과 반환 + 파이선 서버 호출
	    return ResponseEntity.ok(Map.of("data", localAi.requestGemma(data.getQuery(), data.getUuid(), mbUid))); 
	}
	
	// 디버그 전용 (재사용 가능성 있으므로 주석 처리)
//	private final InterviewService service;
//	
//	@PostMapping("/debug/chat")
//	public ResponseEntity<?> debug(
//			@RequestHeader("Authorization") String token,
//			@RequestBody AIRequestDTO data
//			) {
//		
//		Long mbUid = provider.getMemberUid(token.substring(7));
//		service.recordHistory(data.getUuid(), mbUid, data.getQuery(), "", 0);
//		
//		Map<String, Object> x = new HashMap<>();
//		x.put("answer", "debug answer");
//		x.put("score", 99.9);
//		x.put("feedback", "debug feedback");
//		x.put("isLast", true);
//		
//		service.recordHistory(data.getUuid(), mbUid, "debug answer", "debug feedback", 99.9);
//		
//		return ResponseEntity.ok(Map.of("data", x));
//	}
}
