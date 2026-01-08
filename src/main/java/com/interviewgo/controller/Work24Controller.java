package com.interviewgo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.WorkNewsDTO;
import com.interviewgo.jwt.JwtTokenProvider;
import com.interviewgo.mapper.Work24Mapper;
import com.interviewgo.service.InterviewService;
import com.interviewgo.service.Work24Service;

import lombok.RequiredArgsConstructor;

/**
 * 고용24 오픈 API 관련 요청을 처리하는 컨트롤러입니다.
 * 외부 데이터 수집 및 프론트엔드(React) 데이터 제공 역할을 수행합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work24")
public class Work24Controller {
    private final Work24Service work24Service;
    private final Work24Mapper work24Mapper;

    /**
     * 고용24 오픈 API를 호출하여 최신 IT 채용 정보를 DB에 수집/갱신합니다.
     * 수동으로 데이터를 갱신하고 싶을 때 브라우저에서 직접 호출 가능합니다.
     */
    @GetMapping("/admin/save-jobs")
    public String saveJobs() {
        work24Service.fetchJobs();
        return "채용 공고 수집 완료!";
    }
    
    /**
     * DB에 저장된 유효한 채용 공고 리스트를 리액트에 반환합니다.
     * 마감일이 지나지 않은 최신 공고 리스트 (최대 30개)
     */
    @GetMapping("/list")
    public List<WorkNewsDTO> getJobList() {
        return work24Mapper.selectNewsList(); // DB에서 전체 목록 가져오기
    }
}
