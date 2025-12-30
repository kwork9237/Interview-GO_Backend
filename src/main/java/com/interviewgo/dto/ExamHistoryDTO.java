package com.interviewgo.dto;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

// 시험 기록 테이블
@Data
@Alias("examHistory")
public class ExamHistoryDTO {
    private int hist_uid;          
    private Long mb_uid;           // 누가 봤는지
    private String ex_title;       // 문제 제목 (exam 테이블 JOIN)
    private String ex_lang_name;   // 언어 이름 (exam_language 테이블 JOIN)
    private int ex_level;          // 난이도
    private double hist_score;     // 점수
    private Date hist_date;        // 응시일
}
