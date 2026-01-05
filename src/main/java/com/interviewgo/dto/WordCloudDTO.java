package com.interviewgo.dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("wordcloud")
public class WordCloudDTO {
    private String word;  // 추출된 명사
    private int count;    // 등장 횟수
    private String category;  // 검색 키워드 (취업, 채용 등)
}