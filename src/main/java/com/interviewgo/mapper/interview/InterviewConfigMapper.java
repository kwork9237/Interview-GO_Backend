package com.interviewgo.mapper.interview;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interviewgo.dto.interview.InterviewConfigDTO;
import com.interviewgo.dto.interview.InterviewHistoryDTO;

@Mapper
public interface InterviewConfigMapper {
	// 인터뷰 설정 관련 DB 매퍼
	int insertInterviewConfig(InterviewConfigDTO interviewConfig);
	InterviewConfigDTO selectInterviewConfig(@Param("iv_ssid")String uuid);
	int deleteInterviewConfig(@Param("iv_ssid")String uuid);
}
