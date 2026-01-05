package com.interviewgo.service.ai;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.protobuf.ByteString;

// 구글 TTS API를 이용하기 위한 서비스

@Service
public class GoogleTTSService {
	//application.properties 에 정의해둔 값 가져옴
	@Value("${google.cloud.credentials.location}")
	private String credentialsPath;
	
	// TTS 결과를 byte 로 반환
	public byte[] synthesize(String text) throws Exception {
		
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