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

	public int insertMember(MemberDTO user) {
		 user.setMb_password(passwordEncoder.encode(user.getMb_password())); // 비밀번호 암호화
	     user.setRole("USER"); 											     // 기본 권한
		
		return  mapper.insertMember(user);
	}

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
