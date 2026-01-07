package com.interviewgo.dto;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private Long mb_uid;            // 회원 고유 번호 (누구의 비번을 바꿀지)
    private String currentPassword; // 사용자가 입력한 현재 비밀번호 (검증용)
    private String newPassword;     // 새로 바꿀 비밀번호
}