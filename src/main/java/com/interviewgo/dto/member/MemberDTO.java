package com.interviewgo.dto.member;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

// 회원 DTO (팀원 버전으로 통일)
@Data
@Alias("member")
public class MemberDTO {
    private Long mb_uid;        // PK
    private String username;    // 아이디
    private String mb_password; // 비밀번호 (이제 프론트에서도 'mb_password'로 보내야 함)
    private String mb_nickname; // 닉네임
    private String mb_pnumber;  // 전화번호
    private String mb_icon;     // 프로필 이미지 경로
    private String role;        // 권한 (USER/ADMIN)
    private Date mb_date;       // 가입일
}