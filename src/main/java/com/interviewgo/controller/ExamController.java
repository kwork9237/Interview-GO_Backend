package com.interviewgo.controller;

import com.interviewgo.dto.ExamDTO;
import com.interviewgo.mapper.ExamMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exams")
public class ExamController {

	private final ExamMapper ExamDao;

	// 기본 페이지에서 모든 시험문제 출력
    @GetMapping
    public List<ExamDTO> getAllExams() {
    	// 나중에 페이지네이션 구현 필요
        return ExamDao.getExamList();
    }

    // 언어 종류벌 시험 문제 출력
    // 페이지네이션 구현 필요
    @GetMapping("/language/{langId}")
    public List<ExamDTO> getExamsByLanguage(@PathVariable int langId) {
        return ExamDao.getExamDetailByLanguage(langId);
    }

    // 상세 조회 (id 기반)
    @GetMapping("/{id}")
    public ExamDTO getExam(@PathVariable("id") int uid) {
        return ExamDao.getExamDetailByUid(uid);
    }

    // 조회수 증가 (POST)
    @PostMapping("/{id}/increase-view")
    public void increaseView(@PathVariable int id) {
    	// 조회수 1 증가
    	ExamDao.updateExamViewCount(id);
    }
}
