package com.interviewgo.service.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.interviewgo.dto.member.MemberDTO;
import com.interviewgo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

/**
 * CustomUserDetailsService
 * ----------------------------------------------------
 * Spring Security 인증 과정에서 사용자 정보를 로드하는 서비스
 *
 * - 로그인 시 username을 기준으로 DB에서 회원 정보 조회
 * - 조회한 정보를 UserDetails 구현체(CustomUserDetails)로 변환
 * - Password 비교 및 권한(Role) 검증은 Spring Security가 내부에서 처리
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // 회원 정보 조회를 위한 Mapper
    private final MemberMapper memberMapper;

    /**
     * 사용자 인증을 위해 username 기준으로 회원 정보 조회
     *
     * @param username 로그인 시 입력한 아이디
     * @return UserDetails (Spring Security 인증 객체)
     * @throws UsernameNotFoundException 회원이 존재하지 않을 경우
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // username 기준으로 DB에서 회원 정보 조회
        MemberDTO member = memberMapper.getMemberByUsername(username);

        // 조회 결과 검증
        if (member == null) {
            throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + username);
        }

        // Spring Security에서 사용하는 UserDetails 객체로 변환
        return new CustomUserDetails(
        		// 멤버 UID 추가
        		member.getMb_uid(),
                member.getUsername(),
                member.getMb_password(), // 암호화된 비밀번호
                member.getRole()
        );
    }
}