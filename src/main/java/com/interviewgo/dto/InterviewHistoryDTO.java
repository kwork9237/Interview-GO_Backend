package com.interviewgo.dto;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

// 면접 기록
@Data
@Alias("interviewHistory")
public class InterviewHistoryDTO {
    private int iv_uid;
    private Long mb_uid;           // 누가 봤는지
    private int iv_step;           // 단계
    private String iv_question;    // 질문
    private String iv_answer;      // 내 답변
    private double iv_score;       // 점수
    private String iv_feedback;    // AI 피드백
    private String iv_memo;        // 메모
    private Date iv_date;          // 면접일
}