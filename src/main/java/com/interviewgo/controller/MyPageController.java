package com.interviewgo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.InterviewHistoryDTO;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.service.MyPageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor // final이 붙은 필드 생성자를 자동으로 만들어줍니다 (Autowired 대체)
@CrossOrigin(origins = "http://localhost:3000") // React와 연결 허용
public class MyPageController {

    // final을 붙여서 필수 의존성임을 명시합니다.
    private final MyPageService myPageService;
    private final PasswordEncoder passwordEncoder;

    // 1. 회원 정보 수정 (비밀번호 확인 로직 포함)
    @PutMapping("/update")
    public ResponseEntity<?> updateMember(@RequestBody Map<String, Object> requestData) {
        try {
            // 1. 프론트엔드에서 보낸 데이터 추출
            // 숫자형 데이터는 안전하게 String 변환 후 Long으로 파싱
            Long mbUid = Long.valueOf(String.valueOf(requestData.get("mb_uid")));
            String inputPassword = (String) requestData.get("check_password"); // 사용자가 입력한 비번
            String newNickname = (String) requestData.get("nickname");
            String newPnumber = (String) requestData.get("pnumber");

            // 2. DB에서 현재 회원 정보 가져오기
            MemberDTO member = myPageService.getMemberInfo(mbUid);
            if (member == null) {
                return ResponseEntity.status(404).body("회원 정보를 찾을 수 없습니다.");
            }

            // 3. 비밀번호 검증 (입력받은 평문 vs DB에 저장된 암호문 비교)
            if (!passwordEncoder.matches(inputPassword, member.getMb_password())) {
                return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
            }

            // 4. 검증 통과 시 정보 업데이트 진행
            MemberDTO updateDTO = new MemberDTO();
            updateDTO.setMb_uid(mbUid);
            updateDTO.setMb_nickname(newNickname);
            updateDTO.setMb_pnumber(newPnumber);
            
            myPageService.updateMember(updateDTO);

            return ResponseEntity.ok("정보가 성공적으로 수정되었습니다.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 2. 닉네임 중복 확인 (실시간 체크용)
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(
            @RequestParam("nickname") String nickname, 
            @RequestParam("mb_uid") Long mb_uid) {
        
        boolean isAvailable = myPageService.isNicknameAvailable(nickname, mb_uid);
        return ResponseEntity.ok(isAvailable);
    }

    // 3. 코딩 테스트 기록 조회
    @GetMapping("/exam-history")
    public ResponseEntity<List<ExamHistoryDTO>> getExamHistory(@RequestParam("mb_uid") Long mb_uid) {
        return ResponseEntity.ok(myPageService.getExamHistory(mb_uid));
    }

    // 4. 면접 기록 조회
    @GetMapping("/interview-history")
    public ResponseEntity<List<InterviewHistoryDTO>> getInterviewHistory(@RequestParam("mb_uid") Long mb_uid) {
        return ResponseEntity.ok(myPageService.getInterviewHistory(mb_uid));
    }
    
    // 5. 회원 탈퇴 (비밀번호 확인 포함)
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdrawMember(@RequestBody MemberDTO member) {
        // 서비스 단에서 비밀번호 검증 후 삭제 처리한다고 가정
        boolean isDeleted = myPageService.withdrawMember(member.getMb_uid(), member.getMb_password());
        
        if (isDeleted) {
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않거나 처리 중 오류가 발생했습니다.");
        }
    }
}