package com.interviewgo.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.interviewgo.dto.WordCloudDTO;

@Mapper
public interface WordCloudMapper {
    // 분석된 단어를 DB에 저장 또는 횟수 업데이트
    void upsertWord(WordCloudDTO dto);

    // 리액트 화면에 뿌려줄 상위 단어 리스트 조회
    List<WordCloudDTO> selectTopWords();
    
    // 주기적으로 데이터를 새로 고침하고 싶을 때 사용
    void deleteAllWords();
}