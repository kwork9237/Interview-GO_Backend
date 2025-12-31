package com.interviewgo.service.ai;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.interviewgo.dto.ai.AIResponseDTO;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class LocalAIService {
	private final WebClient webClient;
	
	private final AIResponseProcessingService aiProcessing;
	

	// 위스퍼는 항상 로컬에서 돌아가야함.
	public AIResponseDTO.Whisper requestWhisper(File audioFile) {
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(audioFile));

        return webClient.post()
                .uri("/whisper")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(AIResponseDTO.Whisper.class)
                .block(); // 동기 처리를 위해 block 사용
	}
	
	// ssid는 db에 받아서 재입력 용도임.
	public AIResponseDTO.Chat requestGemma(String query, String ssid) {
		// 테스트용 자동쿼리
		query = "spring boot를 공부했습니다";
		query = """ 
				spring boot를 통해 간단한 AI 면접 시스템을 구현했습니다.
				제가 담당한 부분은 파이썬의 Fast API를 통한 백엔드 로컬 AI 서버 구축과 시스템 프롬포트 제작과
				git 리포지토리 관리, 전체적 db 구조 설계, spring boot의 기초 구조 (패키지, 맵퍼 등) 설계,
				백엔드 AI 서버와 통신한 결과를 받아 service 레이어에서 정제, mybatis를 통해 db에 값 입력을 구현했습니다.
				
				어려웠던 부분은 파이썬 백엔드의 시스템 프롬포트 제작과 git 통합 관리, 코드 최적화였습니다.
				git 통합 관리는 여러번 연습 하니까 익숙해졌고, 코드 최적화는 중복된 코드가 있는지 재검수 하면서 간소화할 수 있는 부분을 간소화하였습니다.
				
				개인적으로 느낀 spring boot의 단점으로는 dto, dao, controller, service 레이어가 분리되어 있어서 보기는 편하나,
				소규모 프로젝트에서는 오히려 과도한 분리가 될 수 있었다는 점입니다.
				""";
		query = """ 
				시스템 프롬포트를 제작하면서 어려웠던 점은, 제가 원하는 결과를 받아내기 위해 AI에게 요구사항을 만드는 것이 어려웠습니다.
				조금만 말이 달라져도 전혀 다르게 해석하기 때문이였습니다.
				
				그리고 코드 최적화에서는 AI에게 질문을 하면 답변을 하는 부분에서 DB에 내용을 저장해야 하는데, 그 부분이 많이 중복되어 새로운
				Service 레이어를 추가하여 코드를 간소화시켰습니다.
				하지만 단 하나의 중복 코드로 인해 service 레이어를 만드는 것이 맞나? 라는 의문이 들었습니다.
				""";
		
		// DB에 질문만 저장
		short step = aiProcessing.recordHistory(ssid, query, "", 0);

		Map<String, Object> body = new HashMap<>();
		body.put("query", query);
		
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
		aiProcessing.recordHistory(ssid, res.getAnswer(), res.getFeedback(), res.getScore());

		return res;
	}
}

