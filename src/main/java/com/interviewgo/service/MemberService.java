package com.interviewgo.service;

import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.MemberDTO;
import com.interviewgo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberMapper mapper;
	private final PasswordEncoder passwordEncoder;

    // 아이디 중복 검사
    public boolean isUsernameAvailable(String username) {
        return mapper.countByUsername(username) == 0;
    }

    // 회원가입
    public int insertMember(MemberDTO user) {
        user.setMb_password(passwordEncoder.encode(user.getMb_password()));
        user.setRole("USER");
        return mapper.insertMember(user);
    }

    // 로그인용 조회
    public MemberDTO getMemberByUsername(String username) {
        return mapper.getMemberByUsername(username);
    }
    
    // UID용 조회
    public MemberDTO getMemberByUid(Long mbUid) {
        return mapper.getMemberByUid(mbUid);
    }

    // 임시 비밀번호 발급 메서드 추가
	@Transactional
	public String createTempPassword(MemberDTO member) {
		
        // 1. 회원 정보 확인
        int count = mapper.checkUserExists(member);
        
        if (count == 0) {
            throw new IllegalArgumentException("일치하는 회원 정보를 찾을 수 없습니다.");
        }

        // 2. 4자리 랜덤 숫자 생성 (0000 ~ 9999)
        String tempPw = String.format("%04d", new Random().nextInt(10000));

        // 3. 비밀번호 암호화 (DB 저장용)
        String encodedPw = passwordEncoder.encode(tempPw);

        // 4. DB 업데이트
        MemberDTO mem = new MemberDTO();
        mem.setUsername(member.getUsername());
        mem.setMb_password(encodedPw);
        
        mapper.updatePassword(mem);

        // 5. 사용자에게 보여줄 원본 임시비번 반환
        return tempPw;
    }

    // --- [마이페이지 기능 추가] ---

    /**
     * ✅ 코딩 테스트 풀이 기록 저장
     * 컨트롤러에서 보낸 username, ex_uid, ex_lang_uid를 받아 처리합니다.
     */
    @Transactional
    public void recordExamHistory(String username, int ex_uid, int ex_lang_uid) {
        // 1. 사용자 정보 조회
        MemberDTO member = mapper.getMemberByUsername(username);
        if (member == null) return;

        // 2. 이미 풀었는지 중복 체크
        int alreadySolved = mapper.checkExamHistoryExists(member.getMb_uid(), ex_uid);

        if (alreadySolved == 0) {
            // 3. DB에 풀이 이력 저장 (언어 ID 포함)
            mapper.insertExamHistory(member.getMb_uid(), ex_uid, ex_lang_uid);
            
            // 4. 문제 완료 카운트 증가
            mapper.incrementExamViewCount(ex_uid);
        }
    }
}