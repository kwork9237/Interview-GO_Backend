package com.interviewgo.service.ai;

import java.io.File;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewgo.dto.ai.LocalAPIDTO;

/*

2025-12-29
로컬 AI 서비스 테스트용. 추후 삭제되거나 주요 처리로 사용될 예정
 
*/

@Service
public class LocalAIService {
	private final WebClient webClient;
	
//	@Value("${ai-module.dev.url}")
	private String baseurl = "http://localhost:8000";
	
	
	@Autowired
//    public LocalAIService(@Value("${ai-module.dev.url}") String baseurl) {
	public LocalAIService() {
//        this.baseurl = baseurl;
        // 빌더에서 미리 baseUrl을 설정합니다.
        this.webClient = WebClient.builder()
                .baseUrl(baseurl) 
                .defaultHeader("ngrok-skip-browser-warning", "69420")
                .build();
    }

	public LocalAPIDTO.Whisper transcribe(File audioFile) {
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(audioFile));

        return webClient.post()
                .uri("/whisper")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(LocalAPIDTO.Whisper.class)
                .block(); // 동기 처리를 위해 block 사용
	}
	
	public LocalAPIDTO.Gemma askGemma(String query) {
		query = "spring boot를 공부했습니다";
		
		LocalAPIDTO.Gemma res = webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/gemma").build())
                .bodyValue(Collections.singletonMap("query", query))
                .retrieve()
                .bodyToMono(LocalAPIDTO.Gemma.class)
                .block();
		
		System.out.println("ANS  " + res.getAnswer());
		System.out.println("FED  " + res.getFeedback());
		System.out.println("SCO  " + res.getScore());
		
		return new LocalAPIDTO.Gemma();
	}
}

