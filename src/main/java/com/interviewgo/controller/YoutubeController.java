package com.interviewgo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.dto.YoutubeDTO;
import com.interviewgo.service.YoutubeService;

/**
 * 유튜브 API를 통해 영상 데이터를 수집하고, 수집한 영상ID를 프론트에 제공 하는 컨트롤러.
 */
@RestController
@RequestMapping("/api/youtube")
public class YoutubeController {

    @Autowired
    private YoutubeService youtubeService;

    /**
     * [데이터 수집 API]
     * 경로: http://localhost:8080/api/admin/youtube/save
     * 기능: YouTube Data API를 호출하여 설정된 키워드별 영상을 DB에 저장합니다.
     * 결과: 총 18개(면접 꿀팁, 자기소개서, 코딩테스트 각 6개씩)의 최신 영상을 동기화합니다.
     */
    @GetMapping("/admin/save")
    public String saveVideos() {
        try {
        	// YoutubeService의 수집 로직 
            youtubeService.fetchAndSaveVideos(); // 서비스 호출
            return "✅ 유튜브 영상 18개(카테고리별 6개) 저장 완료 ";
        } catch (Exception e) {
            return "❌ 저장 중 에러 발생: " + e.getMessage();
        }
    }
    /**
     * [카테고리별 영상 조회 API]
     * @param category 조회할 영상의 카테고리 (기본값: '면접 꿀팁')
     * @return DB에서 해당 카테고리에 매칭되는 영상 리스트 반환
     * * 사용 예시: /api/youtube/check?category=자기소개서
     */
    @GetMapping("/check")
    public List<YoutubeDTO> check(@RequestParam(value = "category", required = false) String category) {
    	// 클라이언트에서 카테고리 파라미터를 보내지 않았을 경우 '면접 꿀팁'을 기본 데이터로 설정
        if (category == null || category.isEmpty()) {
            category = "면접 꿀팁"; 
        }
     // 서비스 계층을 통해 필터링된 영상 데이터를 가져와 JSON 형태로 반환
        return youtubeService.getVideosByCategory(category);
    }
}
