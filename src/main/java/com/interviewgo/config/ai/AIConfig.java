package com.interviewgo.config.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AIConfig {
	// ApiAIService 의 chatClient 생성자 제거용
	@Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
	
	
	// 설정에서 관리
	@Value("${ai-module.dev.url}")
	private String baseurl;

    // LocalAIService의 생성자 제거용
    @Bean
    WebClient localAiWebClient() {
        return WebClient.builder()
                .baseUrl(baseurl)
                .defaultHeader("ngrok-skip-browser-warning", "69420")
                .build();
    }
}
