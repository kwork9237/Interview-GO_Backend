package com.interviewgo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.interviewgo.dto.MemberDTO;
import com.interviewgo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

/**
 * MemberService
 * ----------------------------------------------------
 * 회원 관련 비즈니스 로직 담당
 * - 회원가입 처리
 * - 로그인/마이페이지용 회원 조회
 * - 비밀번호 암호화 처리
 */
@Service 
@RequiredArgsConstructor
public class MemberService {
    
    // 회원 관련 DB 접근 Mapper
    private final MemberMapper mapper;
    
    // 비밀번호 암호화를 위한 Encoder (Spring Security 제공)
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 처리
     * 1. 사용자가 입력한 비밀번호를 암호화
     * 2. 기본 권한(USER) 설정
     * 3. DB에 회원 정보 저장
     */
    public int insertMember(MemberDTO user) {
        // 평문 비밀번호 → BCrypt 해시값으로 변환
        user.setMb_password(passwordEncoder.encode(user.getMb_password()));
        
        // 신규 가입자는 기본 권한 USER로 설정
        user.setRole("USER");
        
        // 회원 정보 DB 저장
        return mapper.insertMember(user);
    }

    /**
     * 로그인용 회원 조회
     * - username(ID)을 기준으로 회원 정보 조회
     * - Spring Security 인증 및 JWT 발급 단계에서 사용
     */
    public MemberDTO getMemberByUsername(String username) {
        return mapper.getMemberByUsername(username);
    }
    
    /**
     * UID 기준 회원 조회
     * - 마이페이지, 프로필 조회 등에서 사용
     * - PK 기반 조회이므로 가장 안전하고 정확한 방식
     */
    public MemberDTO getMemberByUid(Long mbUid) {
        return mapper.getMemberByUid(mbUid);
    }
}
