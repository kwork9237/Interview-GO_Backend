package com.interviewgo.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.interviewgo.dto.YoutubeDTO;
import com.interviewgo.mapper.YoutubeMapper;

@Service
public class YoutubeService {

    @Autowired
    private YoutubeMapper youtubeMapper;

    // 설정한 API 키 가져오기
    // secret.yml에서 youtube api key 를 지정하고 properties로 불러 왔습니다.
    //예시)
    // youtube:
    //   api:
    //     key: [youtube api key]
    
    @Value("${youtube.api.key}")
    private String apiKey;

    // 카테고리 (검색어) 리스트
    private final String[] CATEGORIES = {
        "면접 꿀팁", 
        "개발자 기술면접", 
        "자기소개서 작성법" 
    };

    /**
     * 카테고리별로 영상을 6개씩 검색해서 DB에 저장하는 핵심 메서드
     */
    @Transactional
    public void fetchAndSaveVideos() {
    	youtubeMapper.deleteAllVideos();
    	
        RestTemplate restTemplate = new RestTemplate();

        for (String category : CATEGORIES) {
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
                    // DTO 구조(ytKey, ytCategory)에 데이터 담기
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
                System.err.println(" [" + category + "] 처리 중 에러 발생: " + e.getMessage());
            }
        }
    }
    
    public List<YoutubeDTO> getVideosByCategory(String category) {
        // MyBatis Mapper를 호출해서 해당 카테고리의 영상 6개를 가져옵니다.
        return youtubeMapper.selectVideosByCategory(category);
    }
}