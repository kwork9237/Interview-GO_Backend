package com.interviewgo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.dto.MemberDTO;

/**
 * MemberMapper
 * ----------------------------------------------------
 * 회원 관련 DB 접근 (통합 버전)
 * - 조원 기능: ID 중복체크, 비밀번호 찾기, 기본 CRUD
 * - 내 기능: 마이페이지, 활동 기록 관리, 회원 탈퇴 로직
 */
@Mapper
public interface MemberMapper {

    // ====================================================
    // 1. 로그인 & 회원가입 (공통)
    // ====================================================

    /**
     * 회원가입
     * @param member 회원 정보
     * @return 성공 시 1
     */
    int insertMember(MemberDTO member);

    /**
     * 로그인용 회원 조회 (Spring Security)
     * - username(ID) 기준으로 회원 정보 조회
     */
    MemberDTO getMemberByUsername(String username);

    /**
     * 아이디 중복 확인 (조원 기능)
     * @return 0이면 사용 가능, 1 이상이면 중복
     */
    int countByUsername(String username);


    // ====================================================
    // 2. 비밀번호 찾기 (조원 기능)
    // ====================================================

    /**
     * 회원 존재 여부 확인 (비밀번호 찾기용)
     * - 아이디, 전화번호 등이 일치하는지 확인
     * @return 1이면 존재, 0이면 없음
     */
    int checkUserExists(MemberDTO member);

    /**
     * 비밀번호 업데이트 (임시 비밀번호 발급 등)
     */
    void updatePassword(MemberDTO member);


    // ====================================================
    // 3. 마이페이지 (내 기능)
    // ====================================================

    /**
     * UID 기준 회원 조회
     * - 마이페이지 및 내부 로직에서 PK로 조회할 때 사용
     */
    MemberDTO getMemberByUid(Long mbUid);

    /**
     * 회원 정보 수정
     * - 닉네임, 전화번호, 프로필 아이콘 등 수정
     */
    int updateMember(MemberDTO member);

    /**
     * 현재 비밀번호 조회
     * - 정보 수정 시 기존 비밀번호 검증용
     */
    String selectPassword(Long mb_uid);

    /**
     * 닉네임 중복 체크
     * - 내 UID(mb_uid)는 제외하고 다른 사람과 겹치는지 확인
     */
    int checkNicknameDuplicate(
        @Param("nickname") String nickname,
        @Param("mb_uid") Long mb_uid
    );

    /**
     * [추가됨] 마이페이지용 비밀번호 변경 (mb_uid 기준)
     * - 이름을 'updatePasswordByUid'로 변경해서 충돌을 피합니다.
     */
    void updatePasswordByUid(@Param("mb_uid") Long mbUid, @Param("mb_password") String password);
    
    
    // ====================================================
    // 4. 활동 기록 조회 (내 기능)
    // ====================================================

    /**
     * 코딩 테스트 기록 조회
     */
    List<ExamHistoryDTO> selectExamHistory(Long mb_uid);

    /**
     * 면접 연습 기록 조회
     */
    List<InterviewHistoryDTO> selectInterviewHistory(Long mb_uid);


    // ====================================================
    // 5. 회원 탈퇴 (내 기능)
    // - 자식 데이터(기록) 먼저 삭제 후 부모(회원) 삭제
    // ====================================================

    /**
     * 1단계: 코딩 테스트 기록 삭제
     */
    int deleteExamHistory(Long mb_uid);

    /**
     * 1단계: 면접 연습 기록 삭제
     */
    int deleteInterviewHistory(Long mb_uid);

    /**
     * 2단계: 회원 정보 삭제
     */
    int deleteMember(Long mb_uid);

	
	// 사용자 가져오기
	MemberDTO getMember(String memberUid);
	
	// 유저네임 기반 멤버 가져오기
	MemberDTO getMemberById(String username);
}
