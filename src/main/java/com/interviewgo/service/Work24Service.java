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

@Service
public class Work24Service {

    @Autowired
    private Work24Mapper work24Mapper;

    @Value("${work24.api.key}")
    private String apiKey;

    // 월요일 새벽 4시마다 db 초기화 
    @Scheduled(cron = "0 0 4 * * MON")
    public void fetchJobs() {
   
        String url = "https://www.work24.go.kr/cm/openApi/call/wk/callOpenApiSvcInfo210L21.do?" +
                     "authKey=" + apiKey + 
                     "&callTp=L&returnType=XML" +
                     "&startPage=1" + 
                     "&display=30" +  // 가져올 데이터 갯수 30으로 설정
                     "&jobsCd=02";    // 상위 카테고리 '02' 적용

        try {
            RestTemplate restTemplate = new RestTemplate();
            String xmlResponse = restTemplate.getForObject(url, String.class);

            XmlMapper xmlMapper = XmlMapper.builder().build();
            JsonNode root = xmlMapper.readTree(xmlResponse);
            JsonNode jobList = root.path("dhsOpenEmpInfo");

            if (jobList.isArray()) {
                int count = 0;
                for (JsonNode node : jobList) {
                    WorkNewsDTO dto = new WorkNewsDTO();
                    // API 응답 필드 매핑
                    dto.setEmpSeqno(node.path("empSeqno").asText());
                    dto.setEmpWantedTitle(node.path("empWantedTitle").asText());
                    dto.setEmpBusiNm(node.path("empBusiNm").asText());
                    dto.setCoClcdNm(node.path("coClcdNm").asText());
                    dto.setEmpWantedEndt(node.path("empWantedEndt").asText());
                    dto.setEmpWantedHomepgDetail(node.path("empWantedHomepgDetail").asText());

                    // DB 저장 (중복 시 UPDATE)
                    work24Mapper.insertNews(dto);
                    count++;
                    
                    // 수집되는 데이터를 로그로 바로 확인
                    System.out.println(count + ". 수집 번호: " + dto.getEmpSeqno() + " | 제목: " + dto.getEmpWantedTitle());
                }
                System.out.println("총 " + count + "개의 데이터를 수집 및 저장했습니다.");
            } else {
                System.out.println("수집된 데이터가 배열 형태가 아닙니다.");
            }

        } catch (Exception e) {
            System.err.println("API 호출 중 에러 발생: " + e.getMessage());
        }
    }
}