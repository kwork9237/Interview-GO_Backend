package com.interviewgo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.interviewgo.dto.ExamDto;

@Mapper
public interface ExamMapper {
	ExamDto getExamDetail(int exUid);
}
