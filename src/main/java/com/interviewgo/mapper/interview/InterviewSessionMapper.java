package com.interviewgo.mapper.interview;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.interviewgo.dto.interview.InterviewSessionDTO;

@Mapper
public interface InterviewSessionMapper {
	// 세션 삽입
	int insertInterviewSession(InterviewSessionDTO interviewSession);
	
	// 채팅 세션 선택 (현재 사용하지 않음)
	int selectInterviewSessionCount(@Param("iv_ssid")String uuid, @Param("mb_uid") Long mbUid);
	
	// 단일 세션 삭제
	int deleteInterviewSession(@Param("iv_ssid")String uuid, @Param("mb_uid") Long mbUid);
}
