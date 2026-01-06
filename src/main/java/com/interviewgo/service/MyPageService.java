package com.interviewgo.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.mapper.MemberMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Getter @Setter
    public static class InterviewGroupDTO {
        private String interviewDate;
        private List<InterviewHistoryDTO> qnaList;
    }

    // 회원 정보 조회
    public MemberDTO getMemberInfo(Long mbUid) {
        return memberMapper.getMemberByUid(mbUid);
    }

    // 회원 정보 수정
    @Transactional
    public boolean updateMember(MemberDTO member) {
        if (member.getMb_nickname() != null && !member.getMb_nickname().isEmpty()) {
            int count = memberMapper.checkNicknameDuplicate(member.getMb_nickname(), member.getMb_uid());
            if (count > 0) throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        return memberMapper.updateMember(member) > 0;
    }

    // 🚨 [에러 해결 부분] 닉네임 중복 확인 메서드 추가
    public boolean isNicknameAvailable(String nickname, Long mbUid) {
        // 중복된 개수가 0이면 사용 가능(true), 아니면 불가능(false)
        return memberMapper.checkNicknameDuplicate(nickname, mbUid) == 0;
    }

    // 회원 탈퇴
    @Transactional
    public boolean withdrawMember(Long mbUid, String inputPassword) {
        String dbPassword = memberMapper.selectPassword(mbUid);
        if (dbPassword == null || !passwordEncoder.matches(inputPassword, dbPassword)) {
            return false;
        }
        memberMapper.deleteExamHistory(mbUid);
        memberMapper.deleteInterviewHistory(mbUid);
        return memberMapper.deleteMember(mbUid) > 0;
    }

    // 기록 조회 메서드들
    public List<ExamHistoryDTO> getExamHistory(Long mbUid) {
        return memberMapper.selectExamHistory(mbUid);
    }

    public List<InterviewGroupDTO> getGroupedInterviewHistory(Long mbUid) {
        List<InterviewHistoryDTO> rawList = memberMapper.selectInterviewHistory(mbUid);
        Map<String, List<InterviewHistoryDTO>> groupedMap = new LinkedHashMap<>();

        for (InterviewHistoryDTO dto : rawList) {
            String keyDate = (dto.getIv_date() != null) ? dto.getIv_date().toString() : "Unknown Date";
            groupedMap.putIfAbsent(keyDate, new ArrayList<>());
            groupedMap.get(keyDate).add(dto);
        }

        List<InterviewGroupDTO> resultList = new ArrayList<>();
        for (String date : groupedMap.keySet()) {
            InterviewGroupDTO group = new InterviewGroupDTO();
            group.setInterviewDate(date);
            group.setQnaList(groupedMap.get(date));
            resultList.add(group);
        }
        return resultList;
    }
}