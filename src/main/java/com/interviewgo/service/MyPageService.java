package com.interviewgo.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.InterviewHistoryDTO;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.mapper.MemberMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * MyPageService
 * ----------------------------------------------------
 * 마이페이지 관련 비즈니스 로직 담당
 * - 회원 정보 조회 및 수정
 * - 닉네임 중복 검증
 * - 회원 탈퇴 처리
 * - 코딩 테스트 / 면접 연습 기록 조회
 */
@Service
@RequiredArgsConstructor
public class MyPageService {

	// 회원 및 기록 관련 DB 접근을 담당하는 Mapper
	private final MemberMapper memberMapper;

	// 비밀번호 검증을 위한 PasswordEncoder
	private final PasswordEncoder passwordEncoder;

	/**
	 * 면접 기록을 날짜 기준으로 묶기 위한 내부 DTO
	 * - interviewDate : 면접 진행 날짜
	 * - qnaList       : 해당 날짜의 질문/답변 목록
	 */
	@Getter
	@Setter
	public static class InterviewGroupDTO {
		private String interviewDate;
		private List<InterviewHistoryDTO> qnaList;
	}

	/**
	 * 1. 회원 정보 조회
	 * - PK(mb_uid)를 기준으로 회원 정보 조회
	 * - 마이페이지 프로필 조회 시 사용
	 */
	public MemberDTO getMemberInfo(Long mbUid) {
		return memberMapper.getMemberInfo(mbUid);
	}

	/**
	 * 2. 회원 정보 수정
	 * - 닉네임 중복 여부 검증
	 * - 수정 실패 시 전체 작업 롤백을 위해 트랜잭션 적용
	 */
	@Transactional
	public boolean updateMember(MemberDTO member) {

		// 닉네임이 전달된 경우에만 중복 체크 수행
		if (member.getMb_nickname() != null && !member.getMb_nickname().isEmpty()) {
			int count = memberMapper.checkNicknameDuplicate(
					member.getMb_nickname(),
					member.getMb_uid()
			);

			// 중복 닉네임이 존재하면 예외 발생
			if (count > 0) {
				throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
			}
		}

		// 회원 정보 업데이트 (성공 여부 반환)
		return memberMapper.updateMember(member) > 0;
	}

	/**
	 * 3. 닉네임 사용 가능 여부 확인
	 * - 현재 회원(mb_uid)을 제외한 중복 여부 검사
	 * - 프론트 실시간 중복 체크용
	 */
	public boolean isNicknameAvailable(String nickname, Long mbUid) {
		return memberMapper.checkNicknameDuplicate(nickname, mbUid) == 0;
	}

	/**
	 * 4. 회원 탈퇴 처리
	 * - 비밀번호 검증
	 * - 자식 테이블(기록) 삭제 후 회원 삭제
	 * - 트랜잭션으로 전체 작업 일관성 보장
	 */
	@Transactional
	public boolean withdrawMember(Long mbUid, String inputPassword) {

		// DB에 저장된 암호화된 비밀번호 조회
		String dbPassword = memberMapper.selectPassword(mbUid);

		// 비밀번호가 없거나, 입력한 비밀번호가 일치하지 않는 경우
		if (dbPassword == null || !passwordEncoder.matches(inputPassword, dbPassword)) {
			return false;
		}

		// 외래키 제약 조건을 피하기 위해 자식 데이터 먼저 삭제
		memberMapper.deleteExamHistory(mbUid);
		memberMapper.deleteInterviewHistory(mbUid);

		// 회원 삭제
		return memberMapper.deleteMember(mbUid) > 0;
	}

	/**
	 * 5. 코딩 테스트 기록 조회
	 * - 회원 PK 기준으로 시험 기록 목록 반환
	 */
	public List<ExamHistoryDTO> getExamHistory(Long mbUid) {
		return memberMapper.selectExamHistory(mbUid);
	}

	/**
	 * 6. 면접 연습 기록 조회 (날짜별 그룹핑)
	 * - 면접 기록을 날짜 기준으로 묶어서 반환
	 * - 프론트에서 날짜별 카드/섹션 구성에 사용
	 */
	public List<InterviewGroupDTO> getGroupedInterviewHistory(Long mbUid) {

		// 원본 면접 기록 목록 조회
		List<InterviewHistoryDTO> rawList =
				memberMapper.selectInterviewHistory(mbUid);

		// 날짜 순서를 유지하기 위해 LinkedHashMap 사용
		Map<String, List<InterviewHistoryDTO>> groupedMap = new LinkedHashMap<>();

		// 날짜 기준으로 면접 기록 그룹핑
		for (InterviewHistoryDTO dto : rawList) {
			String keyDate =
					(dto.getIv_date() != null)
					? dto.getIv_date().toString()
					: "Unknown Date";

			groupedMap.putIfAbsent(keyDate, new ArrayList<>());
			groupedMap.get(keyDate).add(dto);
		}

		// Map → List 형태로 변환
		List<InterviewGroupDTO> resultList = new ArrayList<>();
		for (String date : groupedMap.keySet()) {
			InterviewGroupDTO group = new InterviewGroupDTO();
			group.setInterviewDate(date);
			group.setQnaList(groupedMap.get(date));
			resultList.add(group);
		}

		return resultList;
	}
}
