package com.interviewgo.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewgo.dto.exam.ExamHistoryDTO;
import com.interviewgo.dto.interview.InterviewHistoryDTO;
import com.interviewgo.dto.member.MemberDTO;
import com.interviewgo.dto.member.MemberUpdateDTO;
import com.interviewgo.dto.member.PasswordUpdateDTO;
import com.interviewgo.mapper.ExamMapper;
import com.interviewgo.mapper.MemberMapper;
import com.interviewgo.mapper.interview.InterviewHistoryMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class MyPageService {

	private final MemberMapper memberMapper;
	private final ExamMapper examMapper;
	private final InterviewHistoryMapper interviewHistoryMapper;
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
	public ResponseEntity<?> updateMember(MemberUpdateDTO updateData) {
		
		// 데이터에서 UID 추출
    	Long mbUid = updateData.getMb_uid();
		
    	if(mbUid == null)
    		return ResponseEntity.badRequest().body("회원 UID가 누락되었습니다.");
		
    	// 기존 회원 정보 조회
    	MemberDTO member = getMemberInfo(mbUid);
    	if (member == null)
            return ResponseEntity.status(400).body("존재하지 않는 회원입니다.");
		
    	// 비밀번호 검증 및 초기화
    	String password = updateData.getMb_password();
        if (password == null ||
            !passwordEncoder.matches(password, member.getMb_password())
            ) {
            return ResponseEntity.status(400).body("비밀번호가 일치하지 않습니다.");
        }
		
        updateData.setMb_password(null);
		
		// 회원 이름 검증
		String mbName = updateData.getMb_nickname();
		if (mbName == null || mbName.isEmpty() || mbName.equals(""))
			return ResponseEntity.status(400).body("이름이 없거나 비어있습니다.");
		
		// 중복 이름 검사
		int count = memberMapper.checkNicknameDuplicate(mbName, member.getMb_uid());
		if (count > 0)
			return ResponseEntity.status(400).body("이미 사용 중인 닉네임입니다.");
		
		// 실제 정보 update
		try {
			memberMapper.updateMember(updateData);
			return ResponseEntity.ok("회원 정보가 수정되었습니다.");
		}
		catch (Exception e) {
            // 실제 에러 원인 확인용
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
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
		examMapper.deleteExamHistory(mbUid);
		interviewHistoryMapper.deleteInterviewHistoryByMbid(mbUid);
		return memberMapper.deleteMember(mbUid) > 0;
	}

	// 기록 조회 메서드들 [2026-01-06 수정]
	public List<InterviewGroupDTO> getGroupedInterviewHistory(Long mbUid) {
		// 1. DB에서 전체 질문/답변 리스트를 가져옴
		List<InterviewHistoryDTO> rawList = interviewHistoryMapper.selectInterviewHistory(mbUid);

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
		return examMapper.selectExamHistory(mbUid);
	}

	// 비밀번호 변경 로직 [2026-01-06 추가]
	@Transactional
	public void updatePassword(PasswordUpdateDTO dto) {
		
		MemberDTO mbData = memberMapper.getMemberByUid(dto.getMb_uid());

		if (mbData == null) {
			throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
		}

		// 1. 현재 비밀번호 검증 (입력한 비번 vs DB 암호화된 비번)
		if (!passwordEncoder.matches(dto.getCurrentPassword(), mbData.getMb_password())) {
			throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
		}

		// 2. 새 비밀번호 암호화
		String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
		mbData.setMb_password(encodedNewPassword);

		// 3. DB 업데이트
		memberMapper.updatePasswordByUid(mbData);
	}
}