package com.interviewgo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.MemberDTO;
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
}
