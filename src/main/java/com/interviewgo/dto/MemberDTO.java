package com.interviewgo.dto;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

// 회원 DTO
@Data
@Alias("member")
public class MemberDTO {
	private Long mb_uid; 
	private String username;
	private String mb_password;
	private String mb_nickname;
	private String mb_pnumber;
	private String mb_icon;
	private String role;
	private Date mb_date;
}
