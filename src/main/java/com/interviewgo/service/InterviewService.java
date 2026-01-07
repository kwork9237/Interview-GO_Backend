package com.interviewgo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
        String firstMsg = "안녕하세요. 저희 회사에 지원하게 된 계기와 간단한 자기소개 부탁드립니다.";

        int sessionCount = sessionMapper.getInterviewSessionCount(uuid, memberUid);
        
        if(sessionCount != 1) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없거나 이미 진행 중인 세션입니다.");
        
        // 마지막 대화 기록 불러오기
        InterviewHistoryDTO history = historyMapper.getLastInterviewHistoryBySsid(uuid, memberUid);
        
        // 대화 기록이 없으면 기본 대화기록 삽입.
        if(history == null) {
        	history = new InterviewHistoryDTO();

        	history.setIv_ssid(uuid);
        	history.setMb_uid(memberUid);
        	history.setIv_step((short) 1);
        	history.setIv_context(firstMsg);
        	history.setIv_score(0);
        	history.setIv_feedback("");

            try {
            	historyMapper.insertInterviewHistory(history);
            }
            catch(Exception e) {
            	System.out.println("DB에 중복되는 ssid 및 step를 insert 시도함");
            }
            
            return firstMsg;
        }
        
        else {
        	return history.getIv_context();
        }
	}
	
	// 대화내역 불러오기
	public List<InterviewHistoryDTO> getHistory(String ssid, Long memberUid) {
        return historyMapper.getAllInterviewHistoryBySsid(ssid, memberUid);
    }
	
	// 면접 중도 이탈
	@Transactional
	public void dropOutInterviewSession(String uuid, Long memberUid) {
		sessionMapper.deleteInterviewSession(uuid, memberUid);
		historyMapper.deleteInterviewHistoryBySsid(uuid, memberUid);
	}
	
	// 대화기록 저장
	@Transactional
    public short recordHistory(String ssid, Long mbUid, String context, String feedback, double score) {
        // 1. 기존 히스토리 정보 가져오기 (Step 계산 등을 위해)
        InterviewHistoryDTO hist = historyMapper.getLastInterviewHistoryBySsid(ssid, mbUid);
        
        short step = (short) (hist.getIv_step() + 1);
        
        // 2. 데이터 세팅
        // 대화기록, 피드백, 점수 갱신
        hist.setIv_context(context);
        hist.setIv_feedback(feedback != null ? feedback : "");
        hist.setIv_score(score);
        
        // 단계(Step) 증가
        hist.setIv_step(step);

        // 3. DB 저장
        historyMapper.insertInterviewHistory(hist);
        
        return step;
    }
}
