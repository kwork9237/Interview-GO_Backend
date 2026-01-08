package com.interviewgo.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interviewgo.dto.exam.ExamDTO;
import com.interviewgo.dto.exam.ExamHistoryDTO;

@Mapper
public interface ExamMapper {
    
    // 시험 세부사항을 UID 기반으로 확인
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
    
    
    // 코딩 테스트 기록 조회 (마이페이지)
    List<ExamHistoryDTO> selectExamHistory(Long mb_uid);
    
    // 시험 기록 삽입
    void insertExamHistory(
        @Param("mb_uid") Long mb_uid, 
        @Param("ex_uid") int ex_uid, 
        @Param("ex_lang_uid") int ex_lang_uid
    );
    
    // 시험문제 해결의 경우 값 증가
    int incrementExamViewCount(int ex_uid);
    
    // 시험 기록이 존재하는지 확인
    int checkExamHistoryExists(
        @Param("mb_uid") Long mb_uid, 
        @Param("ex_uid") int ex_uid
    );
    
    // 코딩테스트삭제
    int deleteExamHistory(Long mb_uid);
}