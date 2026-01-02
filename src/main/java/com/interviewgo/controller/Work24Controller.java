package com.interviewgo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.WorkNewsDTO;
import com.interviewgo.mapper.Work24Mapper;
import com.interviewgo.service.Work24Service;

@RestController
@RequestMapping("/api/work24")
public class Work24Controller {
    @Autowired
    private Work24Service work24Service;
    
    @Autowired
    private Work24Mapper work24Mapper;

    @GetMapping("/save-jobs")
    public String saveJobs() {
        work24Service.fetchJobs();
        return "소프트웨어 공고 수집 완료!";
    }
    
 // 리액트에서 데이터 조회해가는 API
    @GetMapping("/list")
    public List<WorkNewsDTO> getJobList() {
        return work24Mapper.selectNewsList(); // DB에서 전체 목록 가져오기
    }
}
