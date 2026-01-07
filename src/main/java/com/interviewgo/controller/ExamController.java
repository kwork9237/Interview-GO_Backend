package com.interviewgo.controller;

import com.interviewgo.dto.ExamDTO;
import com.interviewgo.mapper.ExamMapper;
import com.interviewgo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exams")
@CrossOrigin(origins = "http://localhost:3000") 
public class ExamController {

    private final ExamMapper ExamDao;
    private final MemberService memberService;

    @GetMapping("/{id}")
    public ExamDTO getExam(@PathVariable(name = "id") int uid) {
        return ExamDao.getExamDetailByUid(uid);
    }

    @PostMapping("/{id}/complete")
    public void completeExam(@PathVariable(name = "id") int uid, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            
            // ✅ DB에서 실제 등록된 문제 정보를 가져와서 외래키(언어ID)를 정확히 맞춤
            ExamDTO examData = ExamDao.getExamDetailByUid(uid);
            
            if (examData != null) {
                // application.properties 설정 덕분에 exLangUid에 정확한 값이 담깁니다.
                memberService.recordExamHistory(username, uid, examData.getExLangUid());
            }
        } else {
            ExamDao.updateExamViewCount(uid);
        }
    }

    @GetMapping
    public Map<String, Object> getAllExams(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "lang", required = false, defaultValue = "전체") String lang
    ) {
        int offset = page * size;
        int totalElements = ExamDao.getExamCount(lang);
        List<ExamDTO> content = ExamDao.getExamListWithPaging(lang, size, offset);

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalPages", (int) Math.ceil((double) totalElements / size));
        response.put("totalElements", totalElements);
        return response;
    }
}