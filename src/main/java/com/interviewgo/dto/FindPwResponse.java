package com.interviewgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindPwResponse {
	private String tempPassword;		 // 프론트엔드가 받을 임시 비밀번호
    private String message;              // 메세지
}
