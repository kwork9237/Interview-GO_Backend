package com.interviewgo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    // 이전에 getMemberById 여기로
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

    // 회원정보삭제
    int deleteMember(Long mb_uid);
}