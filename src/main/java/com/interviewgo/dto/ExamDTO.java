package com.interviewgo.dto;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Alias("exam")
public class ExamDTO {
	@JsonProperty("exUid")
	private int ex_uid;

	// 매핑값 찾지 못함
	private int ex_lang_uid;
	
	@JsonProperty("exTitle")
	private String ex_title;
	
	@JsonProperty("exContent")
	private String ex_content;
	
	@JsonProperty("exLevel")
	private short ex_level;
	
	@JsonProperty("exAnswerList")
	private String ex_answer_list;
	
	// 매핑값 찾지 못함
	private Short ex_answer_correct;
	
	@JsonProperty("viewCount")
	private int view_count;
}
