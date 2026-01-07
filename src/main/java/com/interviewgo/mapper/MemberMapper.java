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

    int insertMember(MemberDTO member);

    MemberDTO getMemberByUsername(String username);

    int countByUsername(String username);


    // ====================================================
    // 2. 비밀번호 찾기 (조원 기능)
    // ====================================================

    int checkUserExists(MemberDTO member);

    void updatePassword(MemberDTO member);


    // ====================================================
    // 3. 마이페이지 (내 기능)
    // ====================================================

    MemberDTO getMemberByUid(Long mbUid);

    int updateMember(MemberDTO member);

    String selectPassword(Long mb_uid);

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
     * 코딩 테스트 기록 조회 (마이페이지용)
     */
    List<ExamHistoryDTO> selectExamHistory(Long mb_uid);


    /**
     * ✅ [수정] 코딩 테스트 풀이 기록 저장
     * DB의 ex_lang_uid 컬럼이 NOT NULL이므로 ex_lang_uid 파라미터를 추가
     */
 // ✅ 파라미터 매핑을 명확히 함
    void insertExamHistory(
        @Param("mb_uid") Long mb_uid, 
        @Param("ex_uid") int ex_uid, 
        @Param("ex_lang_uid") int ex_lang_uid
    );

    int checkExamHistoryExists(
        @Param("mb_uid") Long mb_uid, 
        @Param("ex_uid") int ex_uid
    );

    /**
     * 문제 완료 인원(view_count) 증가
     */
    int incrementExamViewCount(int ex_uid);

    /**
     * 면접 연습 기록 조회
     */
    List<InterviewHistoryDTO> selectInterviewHistory(Long mb_uid);


    // ====================================================
    // 5. 회원 탈퇴 (내 기능)
    // ====================================================
    
    // 코딩테스트삭제
    int deleteExamHistory(Long mb_uid);

    // 면접연습기록삭제
    int deleteInterviewHistory(Long mb_uid);

    // 회원정보삭제
    int deleteMember(Long mb_uid);

    MemberDTO getMember(String memberUid);
    
    MemberDTO getMemberById(String username);
}