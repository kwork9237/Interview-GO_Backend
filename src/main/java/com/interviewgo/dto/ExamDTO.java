package com.interviewgo.dto;

import org.apache.ibatis.type.Alias;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Alias("exam")
public class ExamDTO {
    private int exUid;          // ex_uid와 매핑
    private int exLangUid;      // ex_lang_uid와 매핑
    private String exTitle;     // ex_title과 매핑
    private String exContent;   // ex_content와 매핑
    private short exLevel;      // ex_level과 매핑
    private String exAnswerList;
    private Short exAnswerCorrect;
    private int viewCount;      // view_count와 매핑
    private String exLangName;  // 조인해서 가져오는 언어 이름
}