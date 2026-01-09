package com.interviewgo.controller;

import com.interviewgo.dto.exam.ExamDTO;
import com.interviewgo.mapper.ExamMapper;
import com.interviewgo.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamMapper examMapper;
    private final ExamService examService;

    @GetMapping("/{id}")
    public ExamDTO getExamDetailData(@PathVariable(name = "id") int uid) {
        return examService.getExamDetail(uid);
    }

    @PostMapping("/{id}/complete")
    public void completeExam(@PathVariable(name = "id") int uid, Authentication authentication) {
        // 얼리 리턴 패턴
        if (authentication == null || !authentication.isAuthenticated()) {
        	examMapper.updateExamViewCount(uid);
        	return;
        }
        
        String username = authentication.getName();
        ExamDTO examData = examMapper.getExamDetailByUid(uid);
        
        if (examData != null) {
            // application.properties 설정 덕분에 exLangUid에 정확한 값이 담깁니다.
            examService.recordExamHistory(username, uid, examData.getExLangUid());
        }
    }

    @GetMapping
    public Map<String, Object> getAllExams(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "lang", required = false, defaultValue = "전체") String lang
    ) {
        
        return examService.getExamListByPage(page, size, lang);
    }
}