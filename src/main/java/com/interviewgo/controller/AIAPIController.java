// 작동확인을 위해 주석처리
/*
 * package com.interviewgo.controller;
 * 
 * import java.util.Base64; import java.util.HashMap; import java.util.Map;
 * 
 * import org.springframework.ai.chat.model.ChatModel; import
 * org.springframework.web.bind.annotation.PostMapping; import
 * org.springframework.web.bind.annotation.RequestParam; import
 * org.springframework.web.bind.annotation.RestController;
 * 
 * import com.interviewgo.dto.LocalAPIDTO; import
 * com.interviewgo.service.GoogleTTSService; import
 * com.interviewgo.service.LocalAIService;
 * 
 * import lombok.RequiredArgsConstructor;
 * 
 * @RestController
 * 
 * @RequiredArgsConstructor public class AIAPIController { private final
 * ChatModel model; private final GoogleTTSService ttsService;
 * 
 * // 테스트용 서비스 private final LocalAIService aiservice;
 * 
 * // 디버그 모드의 답변 private final boolean DEBUG_MODE = true;
 * 
 * // 사전 정의된 프롬포트 (고정) private final String prompt = "";
 * 
 * // API 모드 // chat model 전용 답변
 * 
 * @PostMapping("/chat-api") public Map<String, Object>
 * chatResponseAPI(@RequestParam(value="query") String q) {
 * 
 * // AI 답변 변수 String answer;
 * 
 * // AI 모델 출력을 할 것인가. 정의된 출력만 할 것인가 (디버그) // 답변은 answer에 저장됨. if(DEBUG_MODE)
 * answer = "[DEBUG] DEBUG RESPONSE"; else answer = model.call(prompt + q);
 * 
 * // 결과값 반환 Map<String, Object> response = new HashMap<>();
 * response.put("text", answer);
 * 
 * return response; }
 * 
 * // chat model + tts 처리
 * 
 * @PostMapping("/tts-api") public Map<String, Object>
 * ttsResponseAPI(@RequestParam(value="query") String q) throws Exception { //
 * AI 답변 변수 String answer;
 * 
 * // 모델 처리 answer = model.call(prompt + q); byte[] audoiBytes =
 * ttsService.synthesize(answer);
 * 
 * // 결과값 반환 Map<String, Object> response = new HashMap<>();
 * response.put("text", answer); response.put("audio",
 * Base64.getEncoder().encodeToString(audoiBytes));
 * 
 * return response; }
 * 
 * // Local API 모드 // 로컬 채팅 모드
 * 
 * @PostMapping("/chat-local") public String
 * chatResponseLocal(@RequestParam(value="query") String q) { LocalAPIDTO.Gemma
 * response = aiservice.askGemma(q);
 * 
 * return response.getAnswer(); }
 * 
 * // 로컬 TTS 처리
 * 
 * @PostMapping("/tts-local") public Map<String, Object> ttsResponseLocal() {
 * return null; } }
 */