package com.interviewgo.controller;

import com.interviewgo.dto.ExamDto;
import com.interviewgo.mapper.ExamMapper;
import com.interviewgo.mapper.ExamModel;
import com.interviewgo.service.ExamService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exams")
@CrossOrigin(origins = "http://localhost:3000")
public class ExamController {

	private final ExamMapper ExamDao;
    private final ExamService examService;

//    @GetMapping
//    public List<ExamModel> getAllExams() {
//        return examService.getAllExams();
//    }

    @GetMapping("/language/{langId}")
    public List<ExamModel> getExamsByLanguage(@PathVariable int langId) {
        return examService.getExamsByLanguage(langId);
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ExamDto getExam(@PathVariable("id") int id) {
        return ExamDao.getExamDetail(id); // 조회만
    }

    // 조회수 증가 (POST)
    @PostMapping("/{id}/increase-view")
    public void increaseView(@PathVariable int id) {
        examService.increaseViewCount(id); // 1씩 증가
    }

}
