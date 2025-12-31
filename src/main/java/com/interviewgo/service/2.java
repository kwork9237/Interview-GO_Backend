package com.interviewgo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

}
