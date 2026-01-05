//package com.interviewgo.controller;
//
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.interviewgo.dto.ai.LocalAPIDTO;
//import com.interviewgo.service.ai.GoogleTTSService;
//import com.interviewgo.service.ai.LocalAIService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/ai")
//public class AIAPIController {
//	private final ChatModel model;
//	private final GoogleTTSService ttsService;
//	
//	// 테스트용 서비스
//	private final LocalAIService aiservice;
//	
//	// 디버그 모드의 답변
//	private final boolean DEBUG_MODE = true;
//	
//	// 사전 정의된 프롬포트 (고정)
//	private final String prompt = "";
//	
//	// API 모드
//	// chat model 전용 답변
//	@PostMapping("/server/chat")
//	public Map<String, Object> chatResponseAPI(@RequestParam(value="q") String query) {
//		
//		// AI 답변 변수
//		String answer;
//		
//		// AI 모델 출력을 할 것인가. 정의된 출력만 할 것인가 (디버그)
//		// 답변은 answer에 저장됨.
//		if(DEBUG_MODE) answer = "[DEBUG] DEBUG RESPONSE";
//		else answer = model.call(prompt + query);
//		
//		// 결과값 반환
//		Map<String, Object> response = new HashMap<>();
//		response.put("text", answer);
//		
//		return response;
//	}
//	
//	// chat model + tts 처리
//	@PostMapping("/server/tts")
//	public Map<String, Object> ttsResponseAPI(@RequestParam(value="q") String query) throws Exception {
//		// AI 답변 변수
//		String answer;
//		
//		// 모델 처리
//		answer = model.call(prompt + query);
//		byte[] audoiBytes = ttsService.synthesize(answer);
//
//		// 결과값 반환
//		Map<String, Object> response = new HashMap<>();
//		response.put("text", answer);
//		response.put("audio", Base64.getEncoder().encodeToString(audoiBytes));
//		
//		return response;
//	}
//	
//	// Local API 모드
//	// 로컬 채팅 모드 (제미나이 API 미사용)
//	// 테스트 할 때는 RequestParam을 넣을 것.
//	@GetMapping("/local/chat")
//	public Map<String, Object> chatResponseLocal(@RequestParam(value="q")String query, @RequestParam(value="sid")String ssid) {
//	    // 1. 로컬 AI(파이썬) 서버 호출
//	    LocalAPIDTO.Gemma response = aiservice.askGemma(query);
//	    
////	    System.out.println("Answer >> " + response.getAnswer());
////	    System.out.println("Score >> " + response.getScore());
////	    System.out.println("Feedback >> " + response.getFeedback());
//	    
//	    // 결과 반환
//	    return Map.of("data", response); 
//	}
//}
//>>>>>>> a5b6c7a3dbf60e3f3ec17a6c0ec8535495b9e1c3
