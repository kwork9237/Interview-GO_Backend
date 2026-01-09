package com.interviewgo.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindPwResponseDTO {
	private String tempPassword;		 // 프론트엔드가 받을 임시 비밀번호
    private String message;              // 메세지
}
