package com.interviewgo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.interviewgo.dto.MemberDTO;

@Mapper
public interface MemberMapper {
	// 사용자 추가
	int insertMember(MemberDTO member);
	
	// 사용자 가져오기
	MemberDTO getMember(String memberUid);
	
	// 사용자 업데이트
	int updateMember(MemberDTO member);
	
	// 사용자 제거
	int deleteMember(String memberUid);
	
}
