package com.interviewgo.dto.member;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDTO {
	// 로그인 토큰 및 메시지
	private String token;
	private UserInfo user;
	
	// 멤버 데이터
	@Builder
	@Getter
	public static class UserInfo{
		private Long mb_uid;
		private String username;
		private String mb_nickname;
		private String mb_pnumber;
		private String mb_icon;
		private String role;
		
		// builder 패턴 적용으로 DTO로 바로 UserInfo 생성
		public static UserInfo from(MemberDTO member) {
            return UserInfo.builder()
                    .mb_uid(member.getMb_uid())
                    .username(member.getUsername())
                    .mb_nickname(member.getMb_nickname())
                    .mb_icon(member.getMb_icon())
                    .build();
        }
	}
}