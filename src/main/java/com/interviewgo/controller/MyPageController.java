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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.ExamHistoryDTO;
import com.interviewgo.dto.MemberDTO;
import com.interviewgo.service.MemberService;
import com.interviewgo.service.MyPageService;

import lombok.RequiredArgsConstructor;

/**
 * [마이페이지 컨트롤러]
 *
 * 담당 역할
 * 1. 내 정보 조회 및 수정
 * 2. 시험 / 면접 기록 조회
 * 3. 회원 탈퇴
 * 4. 기본 아이콘 목록 제공
 *
 * ※ 실제 비즈니스 로직은 Service 계층에서 처리
 */
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController { 
    
    // 마이페이지 관련 로직 전담 서비스
    private final MyPageService myPageService;
    
    // 비밀번호 검증용 (BCrypt)
    private final PasswordEncoder passwordEncoder;
    
    // 회원 정보 조회용 서비스
    private final MemberService memberService;

    // =================================================================================
    // 1. 내 프로필 정보 조회
    // =================================================================================
    @GetMapping("/profile")
    public ResponseEntity<MemberDTO> getProfile(@RequestParam("mb_uid") Long mbUid) {
        System.out.println("마이페이지 프로필 조회 요청: " + mbUid);
        
        // UID 기준으로 회원 정보 조회
        MemberDTO member = memberService.getMemberByUid(mbUid);
        
        if (member != null) {
            // 보안상 비밀번호는 응답에서 제거
            member.setMb_password(null);
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    // =================================================================================
    // 2. 회원 정보 수정
    // =================================================================================
    @PutMapping("/update")
    public ResponseEntity<?> updateMember(@RequestBody Map<String, Object> payload) {
        System.out.println("회원수정 요청 데이터: " + payload);
        
        try {
            // 1. mb_uid 안전하게 파싱 (프론트 타입 불일치 대비)
            String uidStr = String.valueOf(payload.get("mb_uid"));
            if (uidStr == null || uidStr.equals("null")) {
                return ResponseEntity.badRequest().body("회원 UID가 누락되었습니다.");
            }
            Long mbUid = Long.parseLong(uidStr);

            // 2. 수정 요청 값 추출
            String nickname = (String) payload.get("nickname");
            String pnumber = (String) payload.get("pnumber");
            String checkPassword = (String) payload.get("check_password");
            String mbIcon = (String) payload.get("mb_icon");

            // 3. 기존 회원 정보 조회
            MemberDTO member = myPageService.getMemberInfo(mbUid);
            if (member == null) {
                return ResponseEntity.status(404).body("존재하지 않는 회원입니다.");
            }

            // 4. 비밀번호 검증
            // - 입력한 비밀번호 vs DB에 저장된 암호화 비밀번호
            if (checkPassword == null ||
                !passwordEncoder.matches(checkPassword, member.getMb_password())) {
                return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
            }

            // 5. 수정할 정보만 DTO에 담아서 전달
            MemberDTO updateDTO = new MemberDTO();
            updateDTO.setMb_uid(mbUid);
            updateDTO.setMb_nickname(nickname);
            updateDTO.setMb_pnumber(pnumber);
            updateDTO.setMb_icon(mbIcon);

            myPageService.updateMember(updateDTO);
            
            return ResponseEntity.ok("회원 정보가 수정되었습니다.");

        } catch (Exception e) {
            // 실제 에러 원인 확인용
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
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
        // 본인 닉네임은 제외하고 중복 검사
        boolean isAvailable = myPageService.isNicknameAvailable(nickname, mb_uid);
        return ResponseEntity.ok(isAvailable);
    }

    // =================================================================================
    // 4. 코딩 테스트 기록 조회
    // =================================================================================
    @GetMapping("/exam-history")
    public ResponseEntity<List<ExamHistoryDTO>> getExamHistory(
            @RequestParam("mb_uid") Long mb_uid) {
        return ResponseEntity.ok(myPageService.getExamHistory(mb_uid));
    }

    // =================================================================================
    // 5. 면접 연습 기록 조회 (회차별 그룹화)
    // =================================================================================
    @GetMapping("/interview-history")
    public ResponseEntity<?> getInterviewHistory(@RequestParam("mb_uid") Long mbUid) {
        List<MyPageService.InterviewGroupDTO> list =
                myPageService.getGroupedInterviewHistory(mbUid);
        return ResponseEntity.ok(list);
    }

    // =================================================================================
    // 6. 회원 탈퇴
    // =================================================================================
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdrawMember(@RequestBody MemberDTO member) {
        boolean isDeleted =
                myPageService.withdrawMember(
                        member.getMb_uid(),
                        member.getMb_password()
                );

        if (isDeleted) {
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("비밀번호 불일치 또는 오류");
        }
    }

    // =================================================================================
    // 7. 기본 아이콘 목록 조회
    // =================================================================================
    @GetMapping("/default-icons")
    public ResponseEntity<List<String>> getDefaultIcons() {
        try {
            // classpath 내 static/images 폴더 리소스 조회
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();
            Resource[] resources =
                    resolver.getResources("classpath:/static/images/*");

            // 이미지 파일명만 추출
            List<String> iconNames = Arrays.stream(resources)
                    .map(Resource::getFilename)
                    .filter(name ->
                        name != null &&
                        (name.endsWith(".png") ||
                         name.endsWith(".jpg") ||
                         name.endsWith(".jpeg"))
                    )
                    .sorted()
                    .collect(Collectors.toList());

            return ResponseEntity.ok(iconNames);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
}
