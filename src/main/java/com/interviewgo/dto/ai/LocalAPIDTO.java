package com.interviewgo.dto.ai;

import lombok.Getter;
import lombok.Setter;

// 로컬 API 테스트용 DTO
public class LocalAPIDTO {
	@Getter @Setter
	public static class Whisper {
		private String query;
		private String language;
		private Double probability;
	}
	
	@Getter @Setter
	public static class Gemma {
		private String answer;
		private Long score;
		private String feedback;
	}
}
