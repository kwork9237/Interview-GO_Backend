package com.interviewgo.service.ai;

//import java.io.File;
import java.util.HashMap;
import java.util.Map;

//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.interviewgo.dto.ai.AIResponseDTO;
import com.interviewgo.service.InterviewService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class LocalAIService {
	private final WebClient webClient;
	private final InterviewService service;
	
	// 위스퍼는 항상 로컬에서 돌아가야함.
//	public AIResponseDTO.Whisper requestWhisper(File audioFile) {
//		MultipartBodyBuilder builder = new MultipartBodyBuilder();
//        builder.part("file", new FileSystemResource(audioFile));
//
//        return webClient.post()
//                .uri("/whisper")
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .body(BodyInserters.fromMultipartData(builder.build()))
//                .retrieve()
//                .bodyToMono(AIResponseDTO.Whisper.class)
//                .block(); // 동기 처리를 위해 block 사용
//	}
	
	// ssid는 db에 받아서 재입력 용도임.
	public Map<String, Object> requestGemma(String query, String ssid, Long mbUid) {
		// DB에 질문만 저장
		short step = service.recordHistory(ssid, mbUid, query, "", 0);

		// 서버애 보낼 map 개체 생성
		Map<String, Object> body = new HashMap<>();
		body.put("query", query);

		// 마지막 질문일 경우 서버에 마지막이란 flag 추가
		if(step >= 6) {
			body.put("is_final", true);
		}
		
		// AI 답변을 객체에 저장
		AIResponseDTO.Chat res = webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/gemma").build())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(AIResponseDTO.Chat.class)
                .block();

		// 현재 세션의 history를 다시 가져와 DB에 답변 결과 삽입
		service.recordHistory(ssid, mbUid, res.getAnswer(), res.getFeedback(), res.getScore());
		
		// 결과 맵
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("answer", res.getAnswer());
		resultMap.put("score", res.getScore());
		resultMap.put("feedback", res.getFeedback());
		
		// 프론트쪽 ai response 분기 처리로 인해 사용해야 함.
		resultMap.put("isLast", step >= 6);

		return resultMap;
	}
}

