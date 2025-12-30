package com.interviewgo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.InterviewHistoryDTO;
import com.interviewgo.dto.MemberDTO;

@Mapper
public interface MyPageMapper {

    // 1. 회원 정보 수정 (팀원 코드 대신 이거 씁니다)
    int updateMember(MemberDTO member);

    // 2. 비밀번호 확인 (탈퇴 전용)
    String selectPassword(Long mb_uid);

    // 3. 닉네임 중복 체크
    // (파라미터 2개 이상일 때는 @Param 필수!)
    int checkNicknameDuplicate(@Param("nickname") String nickname, @Param("mb_uid") Long mb_uid);

    // 4. 회원 탈퇴
    int deleteMember(Long mb_uid);

    // 5. 기록 조회
    List<ExamHistoryDTO> selectExamHistory(Long mb_uid);
    List<InterviewHistoryDTO> selectInterviewHistory(Long mb_uid);
}