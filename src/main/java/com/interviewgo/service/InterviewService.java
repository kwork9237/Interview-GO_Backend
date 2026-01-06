package com.interviewgo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.dto.interview.InterviewSessionDTO;
import com.interviewgo.mapper.interview.InterviewHistoryMapper;
import com.interviewgo.mapper.interview.InterviewSessionMapper;
import com.interviewgo.utils.UUIDUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewService {
	private final InterviewHistoryMapper historyMapper;
	private final InterviewSessionMapper sessionMapper;
	
	
	// 인터뷰 세션 관련 처리
	@Transactional
	public String setupInterview(Long memberUid) {
		
		// 랜덤 UUID 생성
		String uuid = UUIDUtil.GenerateUUID();
		
		// session db에 데이터 삽입
		InterviewSessionDTO interviewSessionData = new InterviewSessionDTO();
		interviewSessionData.setIv_ssid(uuid);
		interviewSessionData.setMb_uid(memberUid);
		
		sessionMapper.insertInterviewSession(interviewSessionData);
		
		return uuid;
	}
	
	// 인터뷰 시작
	@Transactional
	public String startInterview(String uuid, Long memberUid) {

		// 면접 출력 메시지
        String firstMsg = "안녕하세요. 자기소개 부탁드립니다.";

        // 마지막 대화 기록 불러오기
        InterviewHistoryDTO dto = historyMapper.getLastInterviewHistoryBySsid(uuid);
        
        // 대화 기록이 없으면 기본 대화기록 삽입.
        if(dto == null) {
        	dto = new InterviewHistoryDTO();

            dto.setIv_ssid(uuid);
            dto.setMb_uid(memberUid);
            dto.setIv_step((short) 1);
            dto.setIv_context(firstMsg);
            dto.setIv_score(0);
            dto.setIv_feedback("");

            try {
            	historyMapper.insertInterviewHistory(dto);
            }
            catch(Exception e) {
            	System.out.println("DB에 중복되는 ssid 및 step를 insert 시도함");
            }
        }
		
		return firstMsg;
	}
	
	// 대화내역 불러오기
	public List<InterviewHistoryDTO> getHistory(String ssid) {
        return historyMapper.getAllInterviewHistoryBySsid(ssid);
    }
	
	// 면접 중도 이탈
	@Transactional
	public void dropOutInterviewSession(String uuid) {
		sessionMapper.deleteInterviewSession(uuid);
		historyMapper.deleteInterviewHistoryBySsid(uuid);
	}
}
