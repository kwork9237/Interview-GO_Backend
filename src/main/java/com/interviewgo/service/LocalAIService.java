package com.interviewgo.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.interviewgo.dto.LocalAPIDTO;

/*

2025-12-29
로컬 AI 서비스 테스트용. 추후 삭제되거나 주요 처리로 사용될 예정
 
*/

@Service
public class LocalAIService {
	private final WebClient webClient;
	
	@Value("${ai-module.dev.url}")
	private String baseurl;
	
	// 기본값 초기화
	public LocalAIService() {
		this.webClient = WebClient.builder()
				.baseUrl(baseurl)
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
		return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/gemma")
                        .queryParam("query", query) // FastAPI가 @app.post에서 query: str로 받음
                        .build())
                .retrieve()
                .bodyToMono(LocalAPIDTO.Gemma.class)
                .block();
	}
}
