package com.interviewgo.dto.ai;

import lombok.Data;

// AI의 답변 DTO
public class AIResponseDTO {
	@Data
	public static class Whisper {
		private String query;
		private String language;
		private Double probability;
	}

	@Data
	public static class Chat {
		private String answer;
		private Double score;
		private String feedback;
	}
}
