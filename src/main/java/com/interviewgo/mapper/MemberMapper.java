package com.interviewgo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.InterviewHistoryDTO;
import com.interviewgo.dto.MemberDTO;

/**
 * MemberMapper
 * ----------------------------------------------------
 * 회원 관련 DB 접근(MyBatis Mapper)
 * - 로그인 / 회원가입
 * - 마이페이지 정보 조회 및 수정
 * - 닉네임·비밀번호 검증
 * - 회원 탈퇴 및 활동 기록 관리
 */
@Mapper
public interface MemberMapper {

    // ====================================================
    // 1. 로그인 & 인증 관련
    // ====================================================

    /**
     * 회원가입
     * @param member 회원 정보 DTO
     * @return insert 성공 시 1
     */
    int insertMember(MemberDTO member);

    /**
     * 로그인용 회원 조회
     * - username(ID) 기준으로 회원 정보 조회
     * - Spring Security / JWT 인증 단계에서 사용
     */
    MemberDTO getMemberByUsername(String username);


    // ====================================================
    // 2. 마이페이지 관련
    // ====================================================

    /**
     * 마이페이지용 회원 정보 조회
     * - PK(mb_uid) 기준
     * - 외래키 연결 및 상세 정보 조회에 사용
     */
    MemberDTO getMemberInfo(Long mb_uid);

    /**
     * 회원 정보 수정
     * - 닉네임, 전화번호, 아이콘 등
     */
    int updateMember(MemberDTO member);

    /**
     * UID 기준 회원 조회
     * - 컨트롤러/서비스에서 범용적으로 사용
     */
    MemberDTO getMemberByUid(Long mbUid);


    // ====================================================
    // 3. 검증용 유틸 메서드
    // ====================================================

    /**
     * 비밀번호 조회
     * - 회원 수정 / 탈퇴 시 입력 비밀번호 검증용
     */
    String selectPassword(Long mb_uid);

    /**
     * 닉네임 중복 체크
     * - 본인(mb_uid)은 제외하고 중복 여부 확인
     */
    int checkNicknameDuplicate(
        @Param("nickname") String nickname,
        @Param("mb_uid") Long mb_uid
    );


    // ====================================================
    // 4. 회원 탈퇴
    // (중요: 자식 → 부모 순서로 삭제)
    // ====================================================

    /**
     * 1단계: 코딩 테스트 기록 삭제
     * - 회원 탈퇴 전 자식 테이블 정리
     */
    int deleteExamHistory(Long mb_uid);

    /**
     * 1단계: 면접 연습 기록 삭제
     */
    int deleteInterviewHistory(Long mb_uid);

    /**
     * 2단계: 회원 정보 삭제
     * - 모든 자식 데이터 삭제 후 실행
     */
    int deleteMember(Long mb_uid);


    // ====================================================
    // 5. 활동 기록 조회
    // ====================================================

    /**
     * 코딩 테스트 기록 조회
     */
    List<ExamHistoryDTO> selectExamHistory(Long mb_uid);

    /**
     * 면접 연습 기록 조회
     */
    List<InterviewHistoryDTO> selectInterviewHistory(Long mb_uid);
}
