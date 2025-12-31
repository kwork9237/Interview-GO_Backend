package com.interviewgo.dto;

import java.sql.Timestamp; // 👈 import 변경 (Date -> Timestamp)
import org.apache.ibatis.type.Alias;
import lombok.Data;

@Data
@Alias("interviewHistory")
public class InterviewHistoryDTO {
    private int iv_uid;
    private Long mb_uid;
    private int iv_step;
    private String iv_question;
    private String iv_answer;
    private double iv_score;
    private String iv_feedback;
    private String iv_memo;
    private Timestamp iv_date; 
}