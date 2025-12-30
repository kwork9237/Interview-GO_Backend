package com.interviewgo.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.InterviewHistoryDTO;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.mapper.MyPageMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // 생성자 주입 (Autowired 대체)
public class MyPageService {

    private final MyPageMapper myPageMapper;
    private final PasswordEncoder passwordEncoder; // 비밀번호 검증용

    // 1. 회원 정보 조회 (수정 전, 정보 불러오기용)
    public MemberDTO getMemberInfo(Long mbUid) {
        // null 리턴하면 에러납니다! 매퍼를 통해 DB에서 조회해서 리턴해야 합니다.
        return myPageMapper.getMemberInfo(mbUid);
    }

    // 2. 회원 정보 수정
    @Transactional // 트랜잭션 처리 (중간에 에러나면 롤백)
    public boolean updateMember(MemberDTO member) {
        // 닉네임을 변경하는 경우 중복 체크
        if (member.getMb_nickname() != null && !member.getMb_nickname().isEmpty()) {
             // 내 닉네임과 같으면 중복 체크 패스, 다를 때만 체크
             int count = myPageMapper.checkNicknameDuplicate(member.getMb_nickname(), member.getMb_uid());
             if (count > 0) {
                 throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
             }
        }
        return myPageMapper.updateMember(member) > 0;
    }

    // 3. 닉네임 사용 가능 여부 확인
    public boolean isNicknameAvailable(String nickname, Long mbUid) {
        return myPageMapper.checkNicknameDuplicate(nickname, mbUid) == 0;
    }

    // 4. 코딩 테스트 기록 조회
    public List<ExamHistoryDTO> getExamHistory(Long mbUid) {
        return myPageMapper.selectExamHistory(mbUid);
    }

    // 5. 면접 연습 기록 조회
    public List<InterviewHistoryDTO> getInterviewHistory(Long mbUid) {
        return myPageMapper.selectInterviewHistory(mbUid);
    }
    
    // 6. 회원 탈퇴 (비밀번호 검증 포함)
    public boolean withdrawMember(Long mbUid, String inputPassword) {
        // DB에 저장된 암호화된 비밀번호 가져오기
        String dbPassword = myPageMapper.selectPassword(mbUid);
        
        // 비밀번호가 없거나(유령회원?), 입력한 비번과 DB 비번이 매칭되지 않으면 실패
        // 반드시 matches(평문, 암호문) 함수를 써야 합니다.
        if (dbPassword == null || !passwordEncoder.matches(inputPassword, dbPassword)) {
            return false;
        }
        
        // 비밀번호가 맞으면 삭제 진행
        return myPageMapper.deleteMember(mbUid) > 0;
    }
}