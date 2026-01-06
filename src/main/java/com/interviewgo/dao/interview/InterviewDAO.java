package com.interviewgo.dao.interview;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.dto.interview.InterviewSessionDTO;
import com.interviewgo.mapper.interview.InterviewHistoryMapper;
import com.interviewgo.mapper.interview.InterviewSessionMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InterviewDAO {
	private final InterviewHistoryMapper history;
	private final InterviewSessionMapper session;
	
	// InterviewHistory 처리
	
	// 마지막 채팅 데이터 return
	public InterviewHistoryDTO getLastInterviewHistoryBySsid(String uuid) {
		return history.getLastInterviewHistoryBySsid(uuid);
	}
	
	// 채팅 목록 가져오기 (새로고침시 데이터 삭제 방지)
	public List<InterviewHistoryDTO> getAllInterviewHistoryBySsid(String uuid) {
		return history.getAllInterviewHistoryBySsid(uuid);
	}
	
	// 면접 기록 삽입 (단일 채팅, AI 반환값)
	public int insertInterviewHistory(InterviewHistoryDTO interviewHistory) {
		return history.insertInterviewHistory(interviewHistory);
	}
	
	// 면접 기록 삭제 (세션 ID 기반)
	public int deleteInterviewHistoryBySsid(String uuid) {
		return history.deleteInterviewHistoryBySsid(null);
	}
	
	
	//--------------------------------------------------------------
	// InterviewSession 처리
	
	// 신규 세션 삽입
	public int insertInterviewSession(InterviewSessionDTO interviewSession) {
		return session.insertInterviewSession(interviewSession);
	}
	
	// 채팅 세션 선택 (현재 사용하지 않음)
	public InterviewSessionDTO getInterviewSessionCount(String uuid) {
		return session.getInterviewSessionCount(uuid);
	}
	
	// 단일 세션 삭제 (현재 사용하지 않음)
	public int deleteInterviewSession(String uuid) {
		return session.deleteInterviewSession(uuid);
	}
	
	//--------------------------------------------------------------
	// 공통 처리
	
	// 면접 중도 이탈
	public void dropOutInterviewSession(String uuid) {
		deleteInterviewSession(uuid);
		deleteInterviewHistoryBySsid(uuid);
	}
}
