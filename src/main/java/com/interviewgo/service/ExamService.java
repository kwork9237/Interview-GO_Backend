package com.interviewgo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.exam.ExamDTO;
import com.interviewgo.dto.member.MemberDTO;
import com.interviewgo.mapper.ExamMapper;
import com.interviewgo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamService {
	private final MemberMapper memberMapper;
	private final ExamMapper examMapper;
	
	// 시험 기록 저장
    @Transactional
    public void recordExamHistory(String username, int ex_uid, int ex_lang_uid) {
        // 1. 사용자 정보 조회
        MemberDTO member = memberMapper.getMemberByUsername(username);
        if (member == null) return;

        // 2. 이미 풀었는지 중복 체크
        int alreadySolved = examMapper.checkExamHistoryExists(member.getMb_uid(), ex_uid);

        if (alreadySolved == 0) {
            // 3. DB에 풀이 이력 저장 (언어 ID 포함)
            examMapper.insertExamHistory(member.getMb_uid(), ex_uid, ex_lang_uid);
            
            // 4. 문제 완료 카운트 증가
            examMapper.incrementExamViewCount(ex_uid);
        }
    }
    
    // 시험 세부 데이터 조회 (Service의 일관성 유지)
    public ExamDTO getExamDetail(int id) {
    	return examMapper.getExamDetailByUid(id);
    }
    
    // 시험 목록 (페이지네이션) 조회
    public Map<String, Object> getExamListByPage(int page, int size, String lang) {
    	int offset = page * size;
        int totalElements = examMapper.getExamCount(lang);
        List<ExamDTO> content = examMapper.getExamListWithPaging(lang, size, offset);

        Map<String, Object> examData = new HashMap<>();
        examData.put("content", content);
        examData.put("totalPages", (int) Math.ceil((double) totalElements / size));
        examData.put("totalElements", totalElements);
        
        return examData;
    }
}
