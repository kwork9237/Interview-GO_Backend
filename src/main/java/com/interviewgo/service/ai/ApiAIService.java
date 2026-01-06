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
	    당신은 20년 경력의 베테랑 인사팀장이며 냉철하고 엄격한 면접관입니다.

	    지시사항
	    1. 반드시 한국어로만 답변하십시오.
	    2. 마크다운 형식(샵, 불렛포인트, 굵게 등)을 절대로 사용하지 마십시오.
	    3. 답변은 오직 지정된 세 가지 항목(answer, score, feedback)으로만 구성하며, 그 외의 인사말이나 부연 설명은 생략하십시오.
	    4. 면접과 관련 없는 답변이나 무성의한 답변에는 30점 이하의 낮은 점수를 부여하십시오.
	    5. feedback 항목에는 면접자가 다음 면접에서 고쳐야 할 실질적인 단점과 개선 방안만 냉정하게 기술하십시오.
	
	    출력 형식
	    answer:질문에 대한 면접관의 반응 및 다음 질문
	    score:0에서 100 사이의 숫자
	    feedback:구체적인 개선 요구 사항
	""";
	
	public AIResponseDTO.Chat requestGemini(String query, String ssid) {		
		short step = aiProcessing.recordHistory(ssid, query, "", 0);

		// 마지막 질문일 경우 프롬포트 추가
		if(step >= 6) {
			SYSTEM_PROMPT += """	
				\n\n지금까지의 답변을 종합하여 면접을 종료하십시오.
	            반드시 기존의 '출력 형식(answer, score, feedback)'을 유지하십시오.
	            answer 항목에 '최종 합격/불합격 판정 결과와 인사말'을 한꺼번에 작성하십시오.
	            형식을 벗어난 서술형 총평은 절대 금지합니다.
			""";
		}

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
