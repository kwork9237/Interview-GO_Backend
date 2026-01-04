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
	public Map<String, Object> chatResponseAPI(@RequestParam(value="q") String query, @RequestParam(value="sid")String ssid) {
		AIResponseDTO.Chat res = apiAi.requestGemini(query, ssid);

		return Map.of("data", res);
	}
	
	// chat model + tts 처리
	@PostMapping("/server/tts")
	public Map<String, Object> ttsResponseAPI(
			@RequestParam(value="q") String query,
			@RequestParam(value="sid") String ssid
//			@RequestParam(value="") 
		) throws Exception {

		AIResponseDTO.Chat res = apiAi.requestGemini(query, ssid);		
		byte[] audoiBytes = apiAi.requestGoogleTTS(res.getAnswer());
		
		// 위쪽 TTS 부분에 목소리 성별 및 말하기 속도 설정 변수 필요함.

		// 결과값 반환
		Map<String, Object> response = new HashMap<>();
		response.put("data", res);
		response.put("audio", Base64.getEncoder().encodeToString(audoiBytes));
		
		return response;
	}
	
	// Local API 모드
	// 로컬 채팅 모드 (제미나이 API 미사용)
	// 테스트 할 때는 RequestParam을 넣을 것.
	@GetMapping("/local/chat")
	public Map<String, Object> chatResponseLocal(@RequestParam(value="q")String query, @RequestParam(value="sid")String ssid) {
	    // 1. 로컬 AI(파이썬) 서버 호출
//	    AIResponseDTO.Chat response = localAi.requestGemma(query, ssid);
	    
	    // 결과 반환 + 파이선 서버 호출
	    return Map.of("data", localAi.requestGemma(query, ssid)); 
	}
}
