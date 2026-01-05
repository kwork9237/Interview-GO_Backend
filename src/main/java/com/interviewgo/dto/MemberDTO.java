package com.interviewgo.dto;

import java.sql.Date;

import org.apache.ibatis.type.Alias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

// 회원 DTO
@Data
@Alias("member")
public class MemberDTO {
	private Long mb_uid;
	private String username;
	
	// JSON 요청/응답에서 "password"라는 키로 값을 받기 위해 사용
	// 프론트엔드에서는 password라는 이름으로 보내지만,
	// 서버 내부에서는 mb_password 필드를 사용하기 때문에 매핑을 맞춰준다.
	@JsonProperty("password") 
	private String mb_password;
	
	private String mb_nickname;
	private String mb_pnumber;
	private String mb_icon;	
	private String role;
	private Date mb_date;
}
