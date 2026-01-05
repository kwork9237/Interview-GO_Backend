package com.interviewgo.service;

import com.interviewgo.mapper.ExamDao;
import com.interviewgo.mapper.ExamModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExamService {

    private final ExamDao examDao;

    public ExamService(ExamDao examDao) {
        this.examDao = examDao;
    }

    public List<ExamModel> getAllExams() {
        return examDao.findAll();
    }

    public List<ExamModel> getExamsByLanguage(int langId) {
        return examDao.findByLanguage(langId);
    }

    @Transactional
    public ExamModel getExamDetail(int exUid) {
        return examDao.findById(exUid); // 조회만
    }

    @Transactional
    public void increaseViewCount(int exUid) {
        examDao.increaseViewCount(exUid); // 실제 1 증가
    }

    }


