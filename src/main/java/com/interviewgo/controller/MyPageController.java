package com.interviewgo.controller;

import java.util.List;
import java.io.File;
import java.util.UUID;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.service.MyPageService;

import lombok.RequiredArgsConstructor;

/**
 * [마이페이지 컨트롤러]
 * - 내 정보 조회/수정 (프로필 이미지 포함)
 * - 코딩테스트/면접 연습 기록 조회 (그룹핑 포함)
 * - 회원 탈퇴
 */
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MyPageController {

	private final MyPageService myPageService;
	private final PasswordEncoder passwordEncoder;

	@Value("${file.upload-dir}")
	private String uploadDir; // application.yml에서 설정한 이미지 저장 경로

	// =================================================================================
	// 0. 프로필 정보 조회
	// =================================================================================
	@GetMapping("/profile")
	public ResponseEntity<MemberDTO> getMemberProfile(@RequestParam("mb_uid") Long mb_uid) {
		MemberDTO member = myPageService.getMemberInfo(mb_uid);

		if (member != null) {
			// 보안상 비밀번호는 null 처리하여 프론트로 전송
			member.setMb_password(null);
			return ResponseEntity.ok(member);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// =================================================================================
	// 1. 회원 정보 수정 (프로필 이미지 파일 업로드 포함)
	// - FormData 형식으로 데이터 수신 (Json 아님)
	// - 이미지는 로컬 폴더에 저장하고 DB에는 URL 경로만 업데이트
	// =================================================================================
	@PutMapping("/update")
	public ResponseEntity<?> updateMember(
			@RequestParam("mb_uid") Long mbUid, 
			@RequestParam("nickname") String nickname,
			@RequestParam("pnumber") String pnumber, 
			@RequestParam("check_password") String checkPassword,
			// 파일은 선택 사항이므로 required = false 설정
			@RequestParam(value = "file", required = false) MultipartFile file
	) {
		try {
			// 1. 기존 정보 조회
			MemberDTO member = myPageService.getMemberInfo(mbUid);
			if (member == null) return ResponseEntity.status(404).body("회원 없음");

			// 2. 비밀번호 확인 (입력값 vs DB암호문 비교)
			if (!passwordEncoder.matches(checkPassword, member.getMb_password())) {
				return ResponseEntity.status(401).body("비밀번호 불일치");
			}

			// 3. 파일 업로드 처리
			String dbFilePath = member.getMb_icon(); // 파일이 안 넘어오면 기존 경로 유지

			if (file != null && !file.isEmpty()) {
				// 파일명 중복 방지를 위해 UUID 적용 (ex: uuid_original.jpg)
				String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

				// 실제 파일 저장 (C:/interview_go/uploads/...)
				File dest = new File(uploadDir + fileName);
				file.transferTo(dest);

				// DB에 저장할 웹 접근 경로 (http://localhost:8080/images/...)
				dbFilePath = "http://localhost:8080/images/" + fileName;
			}

			// 4. DTO 생성 및 업데이트 진행
			MemberDTO updateDTO = new MemberDTO();
			updateDTO.setMb_uid(mbUid);
			updateDTO.setMb_nickname(nickname);
			updateDTO.setMb_pnumber(pnumber);
			updateDTO.setMb_icon(dbFilePath); // 새 이미지 경로 또는 기존 경로

			myPageService.updateMember(updateDTO);

			// 변경된 이미지 경로를 반환하여 프론트에서 즉시 반영하도록 함
			return ResponseEntity.ok(dbFilePath);

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("파일 저장 실패");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("서버 오류 발생");
		}
	}

	// =================================================================================
	// 2. 닉네임 중복 확인
	// =================================================================================
	@GetMapping("/check-nickname")
	public ResponseEntity<Boolean> checkNickname(
			@RequestParam("nickname") String nickname,
			@RequestParam("mb_uid") Long mb_uid
	) {
		boolean isAvailable = myPageService.isNicknameAvailable(nickname, mb_uid);
		return ResponseEntity.ok(isAvailable);
	}

	// =================================================================================
	// 3. 코딩 테스트 기록 조회
	// =================================================================================
	@GetMapping("/exam-history")
	public ResponseEntity<List<ExamHistoryDTO>> getExamHistory(@RequestParam("mb_uid") Long mb_uid) {
		return ResponseEntity.ok(myPageService.getExamHistory(mb_uid));
	}

	// =================================================================================
	// 4. 면접 연습 기록 조회 (그룹핑 적용)
	// - 질문 3개를 하나의 날짜 그룹(InterviewGroupDTO)으로 묶어서 반환
	// =================================================================================
	@GetMapping("/interview-history")
    public ResponseEntity<?> getInterviewHistory(@RequestParam("mb_uid") Long mbUid) {
        // Service 내부 클래스(InterviewGroupDTO) 리스트를 받아옴
        List<MyPageService.InterviewGroupDTO> list = myPageService.getGroupedInterviewHistory(mbUid);
        return ResponseEntity.ok(list);
    }

	// =================================================================================
	// 5. 회원 탈퇴
	// =================================================================================
	@DeleteMapping("/withdraw")
	public ResponseEntity<String> withdrawMember(@RequestBody MemberDTO member) {
		boolean isDeleted = myPageService.withdrawMember(member.getMb_uid(), member.getMb_password());

		if (isDeleted) {
			return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
		} else {
			return ResponseEntity.badRequest().body("비밀번호 불일치 또는 오류");
		}
	}

	/* * ------------------------------------------------------------------
	 * ▼ [삭제 대상] 테스트 및 디버깅용 코드
	 * : 개발이 완료되었으므로 보안을 위해 아래 메서드는 삭제해주세요.
	 * ------------------------------------------------------------------
	 */
	@GetMapping("/pw-check")
	public String checkPw(@RequestParam("pw") String pw) {
		String encoded = passwordEncoder.encode(pw);
		System.out.println("생성된 암호문: " + encoded);
		return encoded;
	}
}