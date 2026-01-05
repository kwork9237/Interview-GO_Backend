package com.interviewgo.dto;

import lombok.Data;

@Data
public class ExamDto {
	private int ex_uid;
	private int ex_lanq_uid;
	private String ex_title;
	private String ex_content;
	private short ex_level;
	private String ex_answer_list;
	private Short ex_answer_correct;
	private int view_count;
}
