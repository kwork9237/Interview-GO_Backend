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

    // [조원 기능] 아이디 중복 검사
    public boolean isUsernameAvailable(String username) {
        return mapper.countByUsername(username) == 0;
    }


    // [내 기능] 회원가입
    public int insertMember(MemberDTO user) {
        user.setMb_password(passwordEncoder.encode(user.getMb_password()));
        user.setRole("USER");
        return mapper.insertMember(user);
    }

    // [내 기능] 로그인용 조회
    public MemberDTO getMemberByUsername(String username) {
        return mapper.getMemberByUsername(username);
    }
    
    // [내 기능] UID용 조회
    public MemberDTO getMemberByUid(Long mbUid) {
        return mapper.getMemberByUid(mbUid);
    }

    // 🚨 [에러 해결 부분] 임시 비밀번호 발급 메서드 추가
	@Transactional
	public String createTempPassword(MemberDTO member) {
		
        // 1. 회원 정보 확인
        int count = mapper.checkUserExists(member);
        
        if (count == 0) {
            throw new IllegalArgumentException("일치하는 회원 정보를 찾을 수 없습니다.");
        }

        // 2. 4자리 랜덤 숫자 생성 (0000 ~ 9999)
        // nextInt(10000)은 0~9999 사이의 숫자를 반환, %04d로 빈자리 0 채움
        String tempPw = String.format("%04d", new Random().nextInt(10000));

        // 3. 비밀번호 암호화 (DB 저장용)
        String encodedPw = passwordEncoder.encode(tempPw);

        // 4. DB 업데이트 (MyBatis는 명시적으로 update 호출 필요)
        MemberDTO mem = new MemberDTO();
        mem.setUsername(member.getUsername());
        mem.setMb_password(encodedPw);
        
        mapper.updatePassword(mem);

        // 5. 사용자에게 보여줄 원본 임시비번 반환
        return tempPw;
    }

}
