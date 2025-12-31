package com.interviewgo.service.ai;

import java.io.FileInputStream;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import com.interviewgo.dto.ai.AIResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiAIService {
	
	//application.properties 에 정의해둔 값 가져옴
	@Value("${google.cloud.credentials.location}")
	private String credentialsPath;
	
	// 채팅 모델
	private final ChatClient chatClient;
	private final AIResponseProcessingService aiProcessing;
	
	// 사전 정의된 프롬포트 (고정)
	private String SYSTEM_PROMPT = """
			    답변은 한국어로만 하세요.

			    당신은 세상에서 가장 엄격한 면접관입니다.
			    면접과 상관없는 질문에 대해서는 점수를 낮게 주세요.
			    개선사항에 대해서는 사용자에 대한 개선사항만 말하세요.

			    질문에 대한 답변은 아래 JSON 형식으로만 하시오.
			    {
			      "answer": "질문에 대한 답변",
			      "score": 80,
			      "feedback": "개선사항"
			    }
			    점수 산정은 100점이 만점입니다.
			""";
	
	public AIResponseDTO.Chat requestGemini(String query, String ssid) {
		
		short step = aiProcessing.recordHistory(ssid, query, "", 0);
		
		//// TEST CODE

		if(step >= 6) {
			System.out.println("면접 종료 트리거입니다.");
			
			SYSTEM_PROMPT += "\n\n마지막 단계입니다. 면접을 정리하고 종합 평가를 내리세요.";
		}

		//// TEST CODE
		
		AIResponseDTO.Chat res =  chatClient.prompt()
	        .system(SYSTEM_PROMPT) // 핵심 지시만 전달
	        .user(query)
	        .call()
	        .entity(AIResponseDTO.Chat.class);
		
		aiProcessing.recordHistory(ssid, res.getAnswer(), res.getFeedback(), res.getScore());

		return res;
	}

	
	// TTS 결과를 byte 로 반환
	public byte[] requestGoogleTTS(String text) throws Exception {
		
		// TTS API 키 인증
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
		
		// TTS 설정 (인증)
		TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
				.setCredentialsProvider(() -> credentials)
				.build();
		
		// 설정 인증을 기반으로 클라이언트 생성
		try(TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(settings)) {
			
			// 입력 텍스트 설정 (input 된 text)
			SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
			
			// 목소리 설정
			VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
					.setLanguageCode("ko-KR")				// 언어 설정
					.setName("ko-KR-Neural2-A")				// 출력 모델 설정
					.setSsmlGender(SsmlVoiceGender.FEMALE)	// 출력 모델 성별 설정 (Front에서 설정 가능하게 할 예정)
					.build();
			
			// MP3 생성
			AudioConfig audioConfig = AudioConfig.newBuilder()
					.setAudioEncoding(AudioEncoding.MP3)	// 출력을 MP3 로 설정
					.setSpeakingRate(1)						// 말하기 속도 조절 (Front에서 설정 가능하게 할 예정)
					.build();
			
			// API 요청 및 응답 처리
			SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
			ByteString audioContents = response.getAudioContent();
			
			return audioContents.toByteArray();
		}
	}
}
