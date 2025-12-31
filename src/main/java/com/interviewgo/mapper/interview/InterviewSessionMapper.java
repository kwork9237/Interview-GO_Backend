package com.interviewgo.mapper.interview;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interviewgo.dto.interview.InterviewConfigDTO;
import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.dto.interview.InterviewSessionDTO;

@Mapper
public interface InterviewSessionMapper {
	// 인터뷰 설정 관련 DB 매퍼
	int insertInterviewSession(InterviewSessionDTO interviewSession);
	InterviewConfigDTO selectInterviewSession(@Param("iv_ssid")String uuid);
	int deleteInterviewSession(@Param("iv_ssid")String uuid);
}
