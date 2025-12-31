package com.interviewgo.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.InterviewHistoryDTO;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.mapper.MyPageMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageMapper myPageMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * [내부 DTO 클래스]
     * 면접 기록을 날짜별로 그룹핑하여 프론트엔드에 전달하기 위한 DTO.
     * (별도 파일 생성 없이 서비스 내부에서 관리)
     */
    @Getter @Setter
    public static class InterviewGroupDTO {
        private String interviewDate;        
        private List<InterviewHistoryDTO> qnaList; 
    }

    /**
     * 면접 기록 그룹핑 조회
     * - DB에서 조회한 낱개 질문들을 '날짜(시간 포함)' 기준으로 묶어서 반환
     * - LinkedHashMap 사용: 면접 본 순서(최신순) 유지
     */
    public List<InterviewGroupDTO> getGroupedInterviewHistory(Long mbUid) {
        List<InterviewHistoryDTO> rawList = myPageMapper.selectInterviewHistory(mbUid);

        // Key: 날짜시간 문자열, Value: 질문 리스트
        Map<String, List<InterviewHistoryDTO>> groupedMap = new LinkedHashMap<>();

        for (InterviewHistoryDTO dto : rawList) {
            // Timestamp 타입을 문자열로 변환하여 Key로 사용
            String keyDate = (dto.getIv_date() != null) ? dto.getIv_date().toString() : "Unknown Date"; 
            
            groupedMap.putIfAbsent(keyDate, new ArrayList<>());
            groupedMap.get(keyDate).add(dto);
        }

        // Map -> List 변환
        List<InterviewGroupDTO> resultList = new ArrayList<>();
        for (String date : groupedMap.keySet()) {
            InterviewGroupDTO group = new InterviewGroupDTO();
            group.setInterviewDate(date);
            group.setQnaList(groupedMap.get(date));
            
            resultList.add(group);
        }

        return resultList;
    }
    
    // 회원 상세 정보 조회
    public MemberDTO getMemberInfo(Long mbUid) {
        return myPageMapper.getMemberInfo(mbUid);
    }

    /**
     * 회원 정보 수정
     * - 닉네임 변경 시 중복 체크 로직 포함
     * - Transactional 적용: 중간에 오류 발생 시 롤백
     */
    @Transactional 
    public boolean updateMember(MemberDTO member) {
        // 닉네임이 존재하고 비어있지 않은 경우 중복 체크 수행
        if (member.getMb_nickname() != null && !member.getMb_nickname().isEmpty()) {
             // 본인의 현재 닉네임이 아닐 경우에만 중복 여부 확인
             int count = myPageMapper.checkNicknameDuplicate(member.getMb_nickname(), member.getMb_uid());
             if (count > 0) {
                 throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
             }
        }
        return myPageMapper.updateMember(member) > 0;
    }

    // 닉네임 사용 가능 여부 확인 (단순 체크)
    public boolean isNicknameAvailable(String nickname, Long mbUid) {
        return myPageMapper.checkNicknameDuplicate(nickname, mbUid) == 0;
    }

    // 코딩 테스트 기록 조회
    public List<ExamHistoryDTO> getExamHistory(Long mbUid) {
        return myPageMapper.selectExamHistory(mbUid);
    }

    // 회원 탈퇴 (비밀번호 검증 포함)
    public boolean withdrawMember(Long mbUid, String inputPassword) {
        String dbPassword = myPageMapper.selectPassword(mbUid);
        
        // 비밀번호 불일치 시 탈퇴 불가
        if (dbPassword == null || !passwordEncoder.matches(inputPassword, dbPassword)) {
            return false;
        }
        
        return myPageMapper.deleteMember(mbUid) > 0;
    }
}