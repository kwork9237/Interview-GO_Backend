package com.interviewgo.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.InterviewHistoryDTO;
import com.interviewgo.dto.MemberDTO;

@Mapper
public interface MyPageMapper {

	// 회원 상세 정보 조회
	MemberDTO getMemberInfo(Long mb_uid);

	// 회원 정보 수정 (프로필 이미지, 닉네임 등)
	int updateMember(MemberDTO member);

	// 비밀번호 조회 (탈퇴 시 검증용)
	String selectPassword(Long mb_uid);

	// 닉네임 중복 체크 (수정 시 본인 닉네임은 제외하고 체크)
	int checkNicknameDuplicate(@Param("nickname") String nickname, @Param("mb_uid") Long mb_uid);

	// 탈퇴 전 기록 삭제
	int deleteExamHistory(Long mb_uid);
	int deleteInterviewHistory(Long mb_uid);

	// 회원 탈퇴
	int deleteMember(Long mb_uid);

	// 코딩 테스트 응시 기록 조회
	List<ExamHistoryDTO> selectExamHistory(Long mb_uid);

	// 면접 연습 기록 전체 조회 (서비스 계층에서 날짜별로 그룹핑됨)
	List<InterviewHistoryDTO> selectInterviewHistory(Long mb_uid);
}