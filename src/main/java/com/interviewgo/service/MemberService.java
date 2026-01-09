package com.interviewgo.service;

import java.util.Random;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.member.LoginResponseDTO;
import com.interviewgo.dto.member.MemberDTO;
import com.interviewgo.jwt.JwtTokenProvider;
import com.interviewgo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // 아이디 중복 검사
    public boolean isUsernameAvailable(String username) {
        return memberMapper.countByUsername(username) == 0;
    }

    // 회원가입
    public int insertMember(MemberDTO user) {
        user.setMb_password(passwordEncoder.encode(user.getMb_password()));
        user.setRole("USER");
        return memberMapper.insertMember(user);
    }

    // 로그인용 조회
    public MemberDTO getMemberByUsername(String username) {
        return memberMapper.getMemberByUsername(username);
    }
    
    // UID용 조회
    public MemberDTO getMemberByUid(Long mbUid) {
        return memberMapper.getMemberByUid(mbUid);
    }

    // 임시 비밀번호 발급 메서드 추가
	@Transactional
	public String createTempPassword(MemberDTO member) {
		
        // 1. 회원 정보 확인
        MemberDTO mbData = memberMapper.getMemberForPasswordReset(member);
        
        if (mbData == null) {
            throw new IllegalArgumentException("일치하는 회원 정보를 찾을 수 없습니다.");
        }

        // 2. 4자리 랜덤 숫자 생성 (0000 ~ 9999)
        String tempPw = String.format("%04d", new Random().nextInt(10000));

        // 3. 비밀번호 암호화 (DB 저장용)
        String encodedPw = passwordEncoder.encode(tempPw);

        // 4. DB 업데이트
        mbData.setMb_password(encodedPw);
        
        memberMapper.updatePasswordByUid(mbData);

        // 5. 사용자에게 보여줄 원본 임시비번 반환
        return tempPw;
    }
	
	
	public LoginResponseDTO loginProcess(String username, String password) {
		Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        // 2. DB에서 상세 정보 조회 (아이콘, 닉네임 등을 위해 필요)
        MemberDTO loginMember = getMemberByUsername(username);
        
        // 3. JWT 토큰 생성
        // token에 member uid까지 넣기 위해 순서를 변경함
        String token = jwtTokenProvider.createToken(authentication.getName(), "USER", loginMember.getMb_uid());
        
        // 멤버 정보 입력
        LoginResponseDTO.UserInfo userInfo = LoginResponseDTO.UserInfo.from(loginMember);

        // 프론트엔드가 사용할 유저 정보를 builder 패턴으로 반환
        return LoginResponseDTO.builder()
	            .token(token)
	            .user(userInfo)
	            .build();
	}
}