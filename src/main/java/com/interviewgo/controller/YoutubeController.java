package com.interviewgo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.YoutubeDTO;
import com.interviewgo.service.YoutubeService;

@RestController
@RequestMapping("/api/admin/youtube")
public class YoutubeController {

    @Autowired
    private YoutubeService youtubeService;

    // 브라우저에서 http://localhost:8080/api/admin/youtube/save 입력 시 실행
    @GetMapping("/save")
    public String saveVideos() {
        try {
            youtubeService.fetchAndSaveVideos(); // 서비스 호출
            return "✅ 유튜브 영상 18개(카테고리별 6개) 저장 프로세스 완료!";
        } catch (Exception e) {
            return "❌ 저장 중 에러 발생: " + e.getMessage();
        }
    }
    
    @GetMapping("/check")
    public List<YoutubeDTO> checkData(@RequestParam("category") String category) {
        // Service를 통해 DB에서 데이터를 가져옵니다.
        return youtubeService.getVideosByCategory(category);
    }
}
