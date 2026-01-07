package com.interviewgo.dto;

import java.sql.Date;
import org.apache.ibatis.type.Alias;
import lombok.Data;

@Data
@Alias("examHistory")
public class ExamHistoryDTO {
    private int hist_uid;
    private Long mb_uid;
    private int ex_uid;
    private int ex_lang_uid;
    private Double hist_score;
    private Date hist_date;

    // ✅ 리액트에서 보여줄 필드들을 추가합니다.
    private String ex_title;      // 문제 제목
    private int ex_level;         // 난이도
    private String ex_lang_name;  // 언어 이름
}