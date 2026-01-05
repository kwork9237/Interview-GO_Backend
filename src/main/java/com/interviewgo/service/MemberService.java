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

        // 2. 임시 비번 생성 및 암호화
        String tempPw = String.format("%04d", new Random().nextInt(10000));
        String encodedPw = passwordEncoder.encode(tempPw);

        // 3. DB 업데이트
        MemberDTO updateDto = new MemberDTO();
        updateDto.setUsername(member.getUsername());
        updateDto.setMb_password(encodedPw);
        
        mapper.updatePassword(updateDto);

        return tempPw;
    }
}