package com.interviewgo.dto.interview;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("interviewConfig")
public class InterviewConfigDTO {
	// 인터뷰 설정 관련 DTO
	
	private String iv_ssid;
	private Long mb_uid;
	private short ai_mode;
	private short ai_gender;
	private short ai_speed;
	private Date created_time;
}
