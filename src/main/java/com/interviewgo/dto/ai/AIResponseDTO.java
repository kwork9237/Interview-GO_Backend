package com.interviewgo.dto.ai;

import lombok.Data;

// AI의 답변 DTO
public class AIResponseDTO {
	
	// Whisper AI 반환값
	@Data
	public static class Whisper {
		private String query;
		private String language;
		private Double probability;
	}

	// Gemini, Python Local Server 반환값
	@Data
	public static class Chat {
		private String answer;
		private Double score;
		private String feedback;
		private boolean isLast;
	}
}
