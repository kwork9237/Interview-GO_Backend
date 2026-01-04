package com.interviewgo.service.ai;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.mapper.interview.InterviewHistoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AIResponseProcessingService {
	// AI 반환값 공용 처리
	
	// DB 상호작용 빠른 테스트용
	private final InterviewHistoryMapper historyDAO;
	
	@Transactional
    public short recordHistory(String ssid, String context, String feedback, double score) {
        // 1. 기존 히스토리 정보 가져오기 (Step 계산 등을 위해)
        InterviewHistoryDTO hist = historyDAO.getInterviewHistoryById(ssid);
        
        short step = (short) (hist.getIv_step() + 1);
        
        // 2. 데이터 세팅
        hist.setIv_context(context);
        hist.setIv_feedback(feedback != null ? feedback : "");
        hist.setIv_score(score);
        
        // 단계(Step) 증가
        hist.setIv_step(step);

        // 3. DB 저장
        historyDAO.insertInterviewHistory(hist);
        
        return step;
    }
}
