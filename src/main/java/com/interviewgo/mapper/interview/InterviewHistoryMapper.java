package com.interviewgo.mapper.interview;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interviewgo.dto.interview.InterviewHistoryDTO;

@Mapper
public interface InterviewHistoryMapper {
	
	// 마지막 채팅 데이터 가져오기 (다음 step로 넘어가기 위함)
	InterviewHistoryDTO getLastInterviewHistoryBySsid(@Param("iv_ssid") String uuid);
	
	// 채팅 가져오는 용도로만 사용 (새로고침시 데이터 삭제 방지)
	List<InterviewHistoryDTO> getAllInterviewHistoryBySsid(@Param("iv_ssid") String uuid);
	
	// 면접 기록 삽입 (단일 채팅, AI 반환값)
	int insertInterviewHistory(InterviewHistoryDTO interviewHistory);	
	
	// 세션 Id 기반 면접기록 삭제
	int deleteInterviewHistoryBySsid(@Param("iv_ssid") String uuid);
}
