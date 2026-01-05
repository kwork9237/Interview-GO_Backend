package com.interviewgo.dto.interview;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("interviewHistory")
public class InterviewHistoryDTO {
	// 인터뷰 기록 관련 DTO
	
	private Long iv_uid;
	private Long mb_uid;
	private String iv_ssid;
	private short iv_step;
	private String iv_context;
	private double iv_score;
	private String iv_feedback;
	private String iv_memo;		// 삭제 가능
	private Date iv_date;
}
