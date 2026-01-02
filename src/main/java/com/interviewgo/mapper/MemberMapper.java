package com.interviewgo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interviewgo.dto.MemberDTO;

@Mapper
public interface MemberMapper {
	
	// 해당 username을 가진 회원의 수를 반환 (0이면 없음, 1 이상이면 있음)
    int countByUsername(String username);
	
	// 사용자 추가
	int insertMember(MemberDTO member);
	
	// 사용자 가져오기
	MemberDTO getMember(String memberUid);
	
	// 유저네임 기반 멤버 가져오기
	MemberDTO getMemberById(String username);
	
	// 사용자 업데이트
	int updateMember(MemberDTO member);
	
	// 사용자 제거
	int deleteMember(String memberUid);
	
	// 1. 회원 존재 여부 확인 (1이면 존재, 0이면 없음)
    int checkUserExists(MemberDTO member);

    // 2. 비밀번호 업데이트
    void updatePassword(MemberDTO member);
}
