package com.interviewgo.dto.interview;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("interviewSession")
public class InterviewSessionDTO {
	// 인터뷰 설정 관련 DTO
	
	private String iv_ssid;
	private Long mb_uid;
	private Date created_time;
}
