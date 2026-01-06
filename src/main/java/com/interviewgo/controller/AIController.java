package com.interviewgo.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.ai.AIResponseDTO;
import com.interviewgo.service.ai.ApiAIService;
import com.interviewgo.service.ai.LocalAIService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AIController {
	private final LocalAIService localAi;
	private final ApiAIService apiAi;
	
	// API 모드
	// chat model 전용 답변
	@GetMapping("/server/chat")
	public Map<String, Object> chatResponseAPI(
			@RequestParam(value="q") String query,
			@RequestParam(value="sid")String ssid
			) {
		
		// Service에 처리 요청
		AIResponseDTO.Chat res = apiAi.requestGemini(query, ssid);

		return Map.of("data", res);
	}
	
	// chat model + tts 처리
	// 위쪽 목소리 성별 및 말하기 속도 설정 변수 필요함. (기능구현 고민중)
	// 만약 TTS Service 구현이 제외되면 해당 Mapping는 필요없음
	@PostMapping("/server/tts")
	public Map<String, Object> ttsResponseAPI(
			@RequestParam(value="q") String query,
			@RequestParam(value="sid") String ssid
			) throws Exception {

		// Service 에 처리 요청
		AIResponseDTO.Chat res = apiAi.requestGemini(query, ssid);
		byte[] audoiBytes = apiAi.requestGoogleTTS(res.getAnswer());

		// 결과값 반환
		Map<String, Object> response = new HashMap<>();
		response.put("data", res);
		response.put("audio", Base64.getEncoder().encodeToString(audoiBytes));
		
		return response;
	}
	
	// Local API 모드
	@GetMapping("/local/chat")
	public Map<String, Object> chatResponseLocal(
			@RequestParam(value="q")String query, 
			@RequestParam(value="sid")String ssid
			) {
		
	    // 결과 반환 + 파이선 서버 호출
	    return Map.of("data", localAi.requestGemma(query, ssid)); 
	}
	
	// 디버그 전용 (재사용 가능성 있으므로 주석 처리)
//	@GetMapping("/debug")
//	public Map<String, Object> debug(
//			@RequestParam(value="q")String query, 
//			@RequestParam(value="sid")String ssid
//			) {
//
//		Map<String, Object> rs = new HashMap<>();
//		rs.put("answer", "DEBUG ANSWER");
//		rs.put("score", 10);
//		rs.put("feedback", "DEBUG FEEBACK");
//		rs.put("isLast", true);
//		
//		Map<String, Object> response = new HashMap<>();
//		response.put("data", rs);
//		
//		
//		return response;
//	}
}
