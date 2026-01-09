package com.interviewgo.dto.member;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Alias("memberUpdate")
public class MemberUpdateDTO {
	private Long mb_uid;
	
	@JsonProperty("nickname")
	private String mb_nickname;
	
	@JsonProperty("pnumber")
	private String mb_pnumber;
	
	@JsonProperty("check_password")
	private String mb_password;
	
	private String mb_icon;
}
