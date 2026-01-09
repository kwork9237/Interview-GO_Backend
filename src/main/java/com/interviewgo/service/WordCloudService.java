package com.interviewgo.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.interviewgo.dto.WordCloudDTO;
import com.interviewgo.mapper.WordCloudMapper;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;

@Service
public class WordCloudService {

    @Autowired private WordCloudMapper wordCloudMapper;
    
    // application.properties 또는 yml에 설정된 네이버 API 키 값 로드
    @Value("${naver.client.id}") private String clientId;
    @Value("${naver.client.secret}") private String clientSecret;

    /**
     * [메인 로직] 여러 개의 검색어를 순회하며 트렌드 데이터를 최신화합니다.
     */
    @Scheduled(cron = "0 0 3 * * MON")
    @Transactional // 도중에 에러 발생 시 롤백하여 데이터 정합성 유지
    public void updateAllTrends() {
        // 1. 새로운 분석을 시작하기 전, 기존에 저장된 워드클라우드 데이터를 모두 삭제
        wordCloudMapper.deleteAllWords();
        
        // 2. 수집 대상이 될 키워드 리스트 (원석님이 정하신 6개 키워드)
        List<String> searchKeywords = Arrays.asList("취업", "채용", "면접준비", "AI", "클라우드", "인공지능");

        // 3. 리스트를 반복문으로 돌면서 각 키워드별 뉴스 수집 및 형태소 분석 실행
        for (String keyword : searchKeywords) {
            fetchAndAnalyze(keyword);
        }
        System.out.println("🚀 모든 키워드에 대한 트렌드 분석 및 저장 완료!");
    }

    /**
     * [수집 및 분석] 특정 키워드에 대해 네이버 뉴스를 검색하고 명사를 추출하여 DB에 저장합니다.
     */
    public void fetchAndAnalyze(String keyword) {
        // 1. 네이버 뉴스 검색 API URL 설정 (최신 뉴스 30개 수집)
        String url = "https://openapi.naver.com/v1/search/news.json?query=" + keyword + "&display=30";
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        // API 호출을 위한 인증 헤더 설정
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        
        // 2. API 호출 및 JSON 응답 수신
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
        
        StringBuilder sb = new StringBuilder();
        if (response.getBody() != null) {
            // 뉴스 아이템 리스트를 돌며 제목(title) 텍스트만 추출하여 하나로 합침
            response.getBody().path("items").forEach(item -> sb.append(item.path("title").asText()).append(" "));
        }
        
        // 3. HTML 태그(<b> 등) 제거 정규식 적용
        String cleanText = sb.toString().replaceAll("<[^>]*>", ""); 

        // 4. 코모란(Komoran) 형태소 분석기를 사용하여 명사(Noun)만 추출
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        List<String> nouns = komoran.analyze(cleanText).getNouns();

        // 5. 분석에서 제외할 불용어(Stopwords) 리스트 정의
        List<String> stopWords = Arrays.asList("재단", "학년", "모집", "공고", "안내", "일시", "경북", "경남","울진","울주군","충북","교육청","취업","계고","한국보건산업","충남");

        // 6. 단어 빈도수(Count) 계산
        Map<String, Integer> wordMap = new HashMap<>();
        nouns.forEach(n -> { 
            // 단어 길이가 2자 이상이고 불용어 리스트에 포함되지 않은 경우만 카운트
            if(n.length() > 1 && !stopWords.contains(n)) { 
                wordMap.put(n, wordMap.getOrDefault(n, 0) + 1); 
            } 
        });

        // 7. 계산된 단어와 빈도수를 DTO에 담아 DB에 저장 (Upsert: 있으면 Update, 없으면 Insert)
        wordMap.forEach((word, count) -> {
            WordCloudDTO dto = new WordCloudDTO();
            dto.setWord(word);
            dto.setCount(count);
            dto.setCategory(keyword); // 어떤 검색어로 수집되었는지 카테고리 저장
            wordCloudMapper.upsertWord(dto); 
        });
    }
    
    // 목록 조회 (Service의 일관성 유지)
    public List<WordCloudDTO> getTopWords() {
    	return wordCloudMapper.selectTopWords();
    }
}