package com.interviewgo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.interviewgo.dto.ExamDTO;

@Mapper
public interface ExamMapper {
	
	// 시험 문제 가져오기 (문제 ID 기준)
	ExamDTO getExamDetailByUid(@Param("ex_uid") int exUid);
	
	// 시험 문제 가져오기 (언어 ID 기준)
	List<ExamDTO> getExamDetailByLanguage(@Param("ex_lang_uid") int exLangUid);
	
	// 시험목록 가져오기
	// 나중에 페이지네이션 구현 필요
//	List<ExamDTO> getExamList(@Param("page") int page);
	List<ExamDTO> getExamList();
	
	// 조회수 갱신
	int updateExamViewCount(@Param("ex_uid") int exUid);
}