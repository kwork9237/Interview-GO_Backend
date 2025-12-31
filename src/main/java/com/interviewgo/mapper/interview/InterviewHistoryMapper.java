package com.interviewgo.mapper.interview;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interviewgo.dto.interview.InterviewHistoryDTO;

@Mapper
public interface InterviewHistoryMapper {
	// 다른쪽에서 조회할 수 있게 만들어야함.
	// 실제로는 Update, Insert만 수행 (DELETE는 더미 구현 필요)
	List<InterviewHistoryDTO> getInterviewHistoryLimit(@Param("limit") int limit, @Param("mb_uid") String mbUid); 	  // 한번에 N개만 (목록)
	InterviewHistoryDTO getInterviewHistortByID(@Param("iv_ssid")String uuid);    // 상세조회용도
	
	int insertInterviewHistory(InterviewHistoryDTO interviewHistory);	// 면접 기록 삽입
	
	int deleteInterviewHistory(@Param("iv_ssid") String uuid);		// 면접 기록 삭제 (더미코드)
}
