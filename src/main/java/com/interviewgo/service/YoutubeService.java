package com.interviewgo.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.interviewgo.dto.YoutubeDTO;
import com.interviewgo.mapper.YoutubeMapper;

/**
 * YouTube Data API v3를 연동하여 면접 및 취업 관련 영상 데이터를 자동 수집하고
 * 카테고리별 큐레이션 기능을 제공.
 */
@Service
public class YoutubeService {

    @Autowired
    private YoutubeMapper youtubeMapper;

    // 설정한 API 키 가져오기
    // secret.yml에서 youtube api key 를 지정함.
    //예시)
    // youtube:
    //   api:
    //     key: [youtube api key]
    
    @Value("${youtube.api.key}")
    private String apiKey;

    // 핵심 키워드 3개를 설정해 검색 진행 
    private final String[] CATEGORIES = {
        "면접 꿀팁", 
        "개발자 기술면접", 
        "자기소개서 작성법" 
    };

    /**
     * 카테고리별로 영상을 6개씩 검색해서 DB에 저장하는 핵심 메서드
     */
    
    // 월요일 새벽 5시마다 정보 초기화 
    @Scheduled(cron = "0 0 5 * * MON" )
    @Transactional // 데이터 삭제와 저장이 하나의 작업 단위로 실행되도록 보장
    public void fetchAndSaveVideos() {
    	// 1. 중복 방지 및 데이터 최신화를 위해 기존 저장된 영상 리스트를 모두 삭제
    	youtubeMapper.deleteAllVideos();
    	
        RestTemplate restTemplate = new RestTemplate();

        for (String category : CATEGORIES) {
        	// 한글 검색어 URL 인코딩 처리 (UTF-8)
        	String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8);
            
        	String url = String.format(
        	    "https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s" //검색어 입력
        		+"&type=video&videoDuration=medium&maxResults=6" //영상 6개 가져오기
        	    +"&videoEmbeddable=true"	// 퍼오기 가능한 영상인지
        	    +"&videoSyndicated=true"	// 다른 플랫폼에서 재생 가능한지 여부  
        		+"&key=%s",					// api key 삽입 
        	    encodedCategory, apiKey
        	);

            try {
                // API 호출 및 JSON 파싱
                JsonNode root = restTemplate.getForObject(url, JsonNode.class);
                JsonNode items = root.path("items");

                for (JsonNode item : items) {
                    YoutubeDTO dto = new YoutubeDTO();
                    
                    // 비디오 ID 추출 (yt_key)
                    String videoId = item.path("id").path("videoId").asText();
                    dto.setYtKey(videoId); 
                    
                    // 검색어 저장 (yt_category)
                    dto.setYtCategory(category);

                    // MyBatis를 통해 DB 저장
                    youtubeMapper.insertVideo(dto);
                }
                System.out.println(" [" + category + "] 카테고리 6개 저장 완료");

            } catch (Exception e) {
            	// 특정 카테고리 수집 실패 시에도 다른 카테고리 수집이 중단되지 않도록 예외 처리
                System.err.println(" [" + category + "] 처리 중 에러 발생: " + e.getMessage());
            }
        }
    }
    // 리액트 화면 출력용 조회 메서드 
    public List<YoutubeDTO> getVideosByCategory(String category) {
        // MyBatis Mapper를 호출해서 해당 카테고리의 영상 6개를 가져옵니다.
        return youtubeMapper.selectVideosByCategory(category);
    }
}