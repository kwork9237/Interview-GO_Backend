package com.interviewgo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.service.MyPageService;

import lombok.RequiredArgsConstructor;

/**
 * [마이페이지 컨트롤러]
 * - 내 정보 조회 및 수정 (프로필 이미지, 닉네임 등)
 * - 코딩테스트 및 면접 연습 기록 조회
 * - 회원 탈퇴 및 기본 아이콘 목록 제공
 */
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MyPageController {

	private final MyPageService myPageService;
	private final PasswordEncoder passwordEncoder;

	// =================================================================================
	// 1. 프로필 정보 조회
	// =================================================================================
	@GetMapping("/profile")
	public ResponseEntity<MemberDTO> getMemberProfile(@RequestParam("mb_uid") Long mb_uid) {
		MemberDTO member = myPageService.getMemberInfo(mb_uid);

		if (member != null) {
			member.setMb_password(null); // 보안상 비밀번호는 제거 후 반환
			return ResponseEntity.ok(member);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// =================================================================================
	// 2. 회원 정보 수정
	// - 닉네임, 전화번호, 비밀번호 변경
	// - 프로필 이미지는 파일 업로드 대신 서버의 정적 이미지 경로(String)를 저장
	// =================================================================================
	@PutMapping("/update")
	public ResponseEntity<?> updateMember(@RequestBody Map<String, Object> payload) {
		try {
			// 파라미터 추출
			Long mbUid = Long.valueOf(payload.get("mb_uid").toString());
			String nickname = (String) payload.get("nickname");
			String pnumber = (String) payload.get("pnumber");
			String checkPassword = (String) payload.get("check_password");
			String mbIcon = (String) payload.get("mb_icon"); // 예: "/images/identicon-1.png"

			// 1. 기존 정보 조회
			MemberDTO member = myPageService.getMemberInfo(mbUid);
			if (member == null) return ResponseEntity.status(404).body("회원 없음");

			// 2. 비밀번호 검증
			if (!passwordEncoder.matches(checkPassword, member.getMb_password())) {
				return ResponseEntity.status(401).body("비밀번호 불일치");
			}

			// 3. 업데이트 정보 설정
			MemberDTO updateDTO = new MemberDTO();
			updateDTO.setMb_uid(mbUid);
			updateDTO.setMb_nickname(nickname);
			updateDTO.setMb_pnumber(pnumber);
			updateDTO.setMb_icon(mbIcon); 

			myPageService.updateMember(updateDTO);

			return ResponseEntity.ok("회원 정보가 수정되었습니다.");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("서버 오류 발생");
		}
	}

	// =================================================================================
	// 3. 닉네임 중복 확인
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
	// 4. 코딩 테스트 기록 조회
	// =================================================================================
	@GetMapping("/exam-history")
	public ResponseEntity<List<ExamHistoryDTO>> getExamHistory(@RequestParam("mb_uid") Long mb_uid) {
		return ResponseEntity.ok(myPageService.getExamHistory(mb_uid));
	}

	// =================================================================================
	// 5. 면접 연습 기록 조회 (그룹핑 적용)
	// =================================================================================
	@GetMapping("/interview-history")
    public ResponseEntity<?> getInterviewHistory(@RequestParam("mb_uid") Long mbUid) {
        // 날짜별로 그룹핑된 면접 기록 반환
        List<MyPageService.InterviewGroupDTO> list = myPageService.getGroupedInterviewHistory(mbUid);
        return ResponseEntity.ok(list);
    }

	// =================================================================================
	// 6. 회원 탈퇴
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

	// =================================================================================
	// 7. 기본 프로필 아이콘 목록 조회 (자동화)
	// - static/images 폴더 내의 이미지 파일명을 스캔하여 리스트로 반환
	// =================================================================================
	@GetMapping("/default-icons")
	public ResponseEntity<List<String>> getDefaultIcons() {
	    try {
	        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	        
	        // classpath:/static/images/ 경로 하위의 모든 파일 검색
	        Resource[] resources = resolver.getResources("classpath:/static/images/*");

	        List<String> iconNames = Arrays.stream(resources)
	                .map(Resource::getFilename)
	                .filter(name -> name != null && (
	                    name.toLowerCase().endsWith(".png") || 
	                    name.toLowerCase().endsWith(".jpg") || 
	                    name.toLowerCase().endsWith(".jpeg")
	                ))
	                .sorted()
	                .collect(Collectors.toList());

	        return ResponseEntity.ok(iconNames);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body(new ArrayList<>());
	    }
	}
}