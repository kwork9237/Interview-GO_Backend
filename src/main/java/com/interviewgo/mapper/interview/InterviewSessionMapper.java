package com.interviewgo.mapper.interview;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.interviewgo.dto.interview.InterviewSessionDTO;

@Mapper
public interface InterviewSessionMapper {
	// 세션 삽입
	int insertInterviewSession(InterviewSessionDTO interviewSession);
	
	// 채팅 세션 선택 (현재 사용하지 않음)
	InterviewSessionDTO getInterviewSessionCount(@Param("iv_ssid")String uuid);
	
	// 단일 세션 삭제 (현재 사용하지 않음)
	int deleteInterviewSession(@Param("iv_ssid")String uuid);
}
