package com.interviewgo.service.ai;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.mapper.interview.InterviewHistoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AIResponseProcessingService {
	private final InterviewHistoryMapper historyDAO;
	
	@Transactional
    public short recordHistory(String ssid, String context, String feedback, double score) {
        // 1. 기존 히스토리 정보 가져오기 (Step 계산 등을 위해)
        InterviewHistoryDTO hist = historyDAO.getLastInterviewHistoryBySsid(ssid);
        
        short step = (short) (hist.getIv_step() + 1);
        
        // 2. 데이터 세팅
        // 대화기록, 피드백, 점수 갱신
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
