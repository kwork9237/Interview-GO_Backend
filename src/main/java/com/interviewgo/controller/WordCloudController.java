package com.interviewgo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.WordCloudDTO;
import com.interviewgo.service.WordCloudService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wordcloud")
public class WordCloudController {
    private final WordCloudService wordCloudService;

    // 데이터 수집 실행 (관리자용)
    @GetMapping("/admin/update-all")
    public String updateAll() {
        try {
            wordCloudService.updateAllTrends();
            return "✅ 데이터 분석 및 DB 갱신 완료!";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 데이터 갱신 중 오류 발생: " + e.getMessage();
        }
    }

    // 리액트 조회용 (사용자용)
    @GetMapping("/list")
    public List<WordCloudDTO> getList() {
        return wordCloudService.getTopWords();
    }
}
