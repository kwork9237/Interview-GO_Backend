package com.interviewgo.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.interviewgo.dto.ExamDTO;

@Mapper
public interface ExamMapper {
    
    // 상세 조회 - 파라미터명을 'id'로 통일
    ExamDTO getExamDetailByUid(@Param("id") int id);
    
    // 페이지네이션 리스트 조회
    List<ExamDTO> getExamListWithPaging(
        @Param("lang") String lang, 
        @Param("size") int size, 
        @Param("offset") int offset
    );

    // 전체 개수 조회
    int getExamCount(@Param("lang") String lang);
    
    // 조회수 갱신 - 파라미터명을 'id'로 통일
    int updateExamViewCount(@Param("id") int id);
}