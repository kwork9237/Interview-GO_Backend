package com.interviewgo.service;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.interviewgo.dto.WorkNewsDTO;
import com.interviewgo.mapper.Work24Mapper;

// ... 기존 import 중 XmlMapper, JsonNode 제거 ...

@Service
public class Work24Service {

    @Autowired private Work24Mapper work24Mapper;
    @Value("${work24.api.key}") private String apiKey;

    @Scheduled(cron = "0 0 4 * * MON")
    public void fetchJobs() {
        String url = "https://www.work24.go.kr/cm/openApi/call/wk/callOpenApiSvcInfo210L21.do?" +
                     "authKey=" + apiKey + "&callTp=L&returnType=XML&startPage=1&display=30&jobsCd=02";

        try {
            RestTemplate restTemplate = new RestTemplate();
            String xmlResponse = restTemplate.getForObject(url, String.class);

            // [수정 포인트] 자바 표준 DOM 파서 사용 (라이브러리 필요 없음)
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));
            doc.getDocumentElement().normalize();

            // <dhsOpenEmpInfo> 태그 리스트 가져오기
            NodeList nList = doc.getElementsByTagName("dhsOpenEmpInfo");

            if (nList.getLength() > 0) {
                int count = 0;
                for (int i = 0; i < nList.getLength(); i++) {
                    Element element = (Element) nList.item(i);
                    WorkNewsDTO dto = new WorkNewsDTO();

                    // 태그 이름을 직접 지정해서 값 가져오기
                    dto.setEmpSeqno(getTagValue("empSeqno", element));
                    dto.setEmpWantedTitle(getTagValue("empWantedTitle", element));
                    dto.setEmpBusiNm(getTagValue("empBusiNm", element));
                    dto.setCoClcdNm(getTagValue("coClcdNm", element));
                    dto.setEmpWantedEndt(getTagValue("empWantedEndt", element));
                    dto.setEmpWantedHomepgDetail(getTagValue("empWantedHomepgDetail", element));

                    work24Mapper.insertNews(dto);
                    count++;
                    System.out.println(count + ". 수집 번호: " + dto.getEmpSeqno());
                }
                System.out.println("정기 업데이트 완료: 총 " + count + "건 저장");
            }

        } catch (Exception e) {
            System.err.println("API 데이터 수집 중 예외 발생: " + e.getMessage());
        }
    }

    // XML 태그 안의 텍스트 값을 안전하게 가져오는 헬퍼 메서드
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        return (nodeList != null && nodeList.getLength() > 0) ? nodeList.item(0).getNodeValue() : "";
    }
    
    // 목록 조회 (Service의 일관성 유지)
    public List<WorkNewsDTO> getNewsList() {
    	return work24Mapper.selectNewsList();
    }
}