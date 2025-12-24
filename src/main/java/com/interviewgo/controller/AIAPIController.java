package com.interviewgo.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.service.GoogleTTSService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AIAPIController {
	private final ChatModel model;
	private final GoogleTTSService ttsService;
	
	private final boolean DEBUG_MODE = true;
	
	// chat model 전용 답변
	@PostMapping("/chat")
	public Map<String, Object> chatResponse(@RequestParam(value="query") String q) {
		
		// AI 답변 변수
		String answer;
		
		// 사전 정의된 프롬포트
		String prompt = "";
		
		// AI 모델 출력을 할 것인가. 정의된 출력만 할 것인가 (디버그)
		// 답변은 answer에 저장됨.
		if(DEBUG_MODE) answer = "[DEBUG] DEBUG RESPONSE";
		else answer = model.call(prompt + q);
		
		// 결과값 반환
		Map<String, Object> response = new HashMap<>();
		response.put("text", answer);
		
		return response;
	}
	
	// chat model + tts 처리
	@PostMapping("/tts")
	public Map<String, Object> ttsResponse(@RequestParam(value="query") String q) throws Exception {
		// AI 답변 변수
		String answer;
		
		// 사전 정의된 프롬포트
		String prompt = "";
		
		// 모델 처리
		answer = model.call(prompt + q);
		byte[] audoiBytes =ttsService.synthesize(answer);

		// 결과값 반환
		Map<String, Object> response = new HashMap<>();
		response.put("text", answer);
		response.put("audio", Base64.getEncoder().encodeToString(audoiBytes));
		
		return response;
	}
}
