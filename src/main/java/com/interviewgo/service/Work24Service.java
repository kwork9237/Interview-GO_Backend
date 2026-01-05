package com.interviewgo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.interviewgo.dto.WorkNewsDTO;
import com.interviewgo.mapper.Work24Mapper;

/**
 * 고용24(워크넷) 오픈 API를 통해 채용 공고 데이터를 수집하고 관리하는 서비스 클래스.
 */
@Service
public class Work24Service {

    @Autowired
    private Work24Mapper work24Mapper;

    @Value("${work24.api.key}")
    private String apiKey;

    /**
     * 주기적 데이터 수집 스케쥴러 (매주 월요일 새벽 4시 실행)
     * 초 분 시 일 월 요일 순서로 설정되어 있습니다.
     */
    @Scheduled(cron = "0 0 4 * * MON")
    public void fetchJobs() {
        
        String url = "https://www.work24.go.kr/cm/openApi/call/wk/callOpenApiSvcInfo210L21.do?" +
                     "authKey=" + apiKey + 
                     "&callTp=L&returnType=XML" +
                     "&startPage=1" + 
                     "&display=30" +  // display=30: 데스크탑 UI 구성을 위해 한 페이지당 30개의 공고를 요청
                     "&jobsCd=02"; // jobsCd=02: 정보통신 대분류 코드를 사용하여 폭넓은 IT 공고 수집

        try {
            RestTemplate restTemplate = new RestTemplate();
            // API로부터 XML 응답 수신
            String xmlResponse = restTemplate.getForObject(url, String.class);

            // XML 데이터를 JSON 트리 구조로 변환하여 파싱
            XmlMapper xmlMapper = XmlMapper.builder().build();
            JsonNode root = xmlMapper.readTree(xmlResponse);
            JsonNode jobList = root.path("dhsOpenEmpInfo"); // 공고 목록 노드 접근

            if (jobList.isArray()) {
                int count = 0;
                for (JsonNode node : jobList) {
                    WorkNewsDTO dto = new WorkNewsDTO();
                    
                    // API 응답 필드와 DTO 객체 매핑
                    dto.setEmpSeqno(node.path("empSeqno").asText());                // 공고 고유번호
                    dto.setEmpWantedTitle(node.path("empWantedTitle").asText());    // 공고 제목
                    dto.setEmpBusiNm(node.path("empBusiNm").asText());              // 회사명
                    dto.setCoClcdNm(node.path("coClcdNm").asText());                // 기업 분류
                    dto.setEmpWantedEndt(node.path("empWantedEndt").asText());      // 마감일 (yyyyMMdd)
                    dto.setEmpWantedHomepgDetail(node.path("empWantedHomepgDetail").asText()); // 상세 URL

                    // 데이터 저장 수행 (Mapper에서 중복 데이터는 자동으로 UPDATE 처리됨)
                    work24Mapper.insertNews(dto);
                    count++;
                    
                    // 콘솔을 통해 실시간 수집 현황 모니터링
                    System.out.println(count + ". 수집 번호: " + dto.getEmpSeqno() + " | 제목: " + dto.getEmpWantedTitle());
                }
                System.out.println("정기 업데이트 완료: 총 " + count + "건 저장");
            } else {
                System.out.println("API 응답에 데이터가 존재하지 않습니다.");
            }

        } catch (Exception e) {
            System.err.println("API 데이터 수집 중 예외 발생: " + e.getMessage());
        }
    }
}