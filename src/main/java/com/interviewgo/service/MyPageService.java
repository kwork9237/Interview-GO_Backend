package com.interviewgo.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.dto.PasswordUpdateDTO;
import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.mapper.MemberMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class MyPageService {

	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;

	@Getter
	@Setter
	public static class InterviewGroupDTO {
		private String interviewDate;
		private List<InterviewHistoryDTO> qnaList;
	}

	// 회원 정보 조회
	public MemberDTO getMemberInfo(Long mbUid) {
		return memberMapper.getMemberByUid(mbUid);
	}

	// 회원 정보 수정
	@Transactional
	public boolean updateMember(MemberDTO member) {
		if (member.getMb_nickname() != null && !member.getMb_nickname().isEmpty()) {
			int count = memberMapper.checkNicknameDuplicate(member.getMb_nickname(), member.getMb_uid());
			if (count > 0)
				throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
		}
		return memberMapper.updateMember(member) > 0;
	}

	// 닉네임 중복 확인
	public boolean isNicknameAvailable(String nickname, Long mbUid) {
		return memberMapper.checkNicknameDuplicate(nickname, mbUid) == 0;
	}

	// 회원 탈퇴
	@Transactional
	public boolean withdrawMember(Long mbUid, String inputPassword) {
		String dbPassword = memberMapper.selectPassword(mbUid);
		if (dbPassword == null || !passwordEncoder.matches(inputPassword, dbPassword)) {
			return false;
		}
		memberMapper.deleteExamHistory(mbUid);
		memberMapper.deleteInterviewHistory(mbUid);
		return memberMapper.deleteMember(mbUid) > 0;
	}

	// 기록 조회 메서드들 [2026-01-06 수정]
	public List<InterviewGroupDTO> getGroupedInterviewHistory(Long mbUid) {
		// 1. DB에서 전체 질문/답변 리스트를 가져옴
		List<InterviewHistoryDTO> rawList = memberMapper.selectInterviewHistory(mbUid);

		// 2. iv_ssid(세션 ID)를 키로 사용하는 Map 생성
		Map<String, List<InterviewHistoryDTO>> groupedMap = new LinkedHashMap<>();

		for (InterviewHistoryDTO dto : rawList) {
			String sessionKey = (dto.getIv_ssid() != null) ? dto.getIv_ssid() : dto.getIv_date().toString();

			groupedMap.putIfAbsent(sessionKey, new ArrayList<>());
			groupedMap.get(sessionKey).add(dto);
		}

		// 3. 최종 결과 리스트 생성
		List<InterviewGroupDTO> resultList = new ArrayList<>();
		for (Map.Entry<String, List<InterviewHistoryDTO>> entry : groupedMap.entrySet()) {
			List<InterviewHistoryDTO> sessionQnas = entry.getValue();

			InterviewGroupDTO group = new InterviewGroupDTO();
			// 해당 세션의 대표 날짜 (첫 번째 질문의 날짜) 설정
			group.setInterviewDate(sessionQnas.get(0).getIv_date().toString());
			group.setQnaList(sessionQnas);

			resultList.add(group);
		}
		return resultList;
	}

	public List<ExamHistoryDTO> getExamHistory(Long mbUid) {
		return memberMapper.selectExamHistory(mbUid);
	}

	// 비밀번호 변경 로직 [2026-01-06 추가]
	@Transactional
	public void updatePassword(PasswordUpdateDTO dto) {
		String dbPassword = memberMapper.selectPassword(dto.getMb_uid());

		if (dbPassword == null) {
			throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
		}

		// 1. 현재 비밀번호 검증 (입력한 비번 vs DB 암호화된 비번)
		if (!passwordEncoder.matches(dto.getCurrentPassword(), dbPassword)) {
			throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
		}

		// 2. 새 비밀번호 암호화
		String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());

		// 3. DB 업데이트
		memberMapper.updatePasswordByUid(dto.getMb_uid(), encodedNewPassword);
	}
}