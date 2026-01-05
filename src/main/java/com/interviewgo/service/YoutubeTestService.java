package com.interviewgo.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class YoutubeTestService {

	@Value("${youtube.api.key}") // secret.yml의 youtube -> api -> key 구조와 일치해야 함
	private String apiKey;

    public String searchVideos(String query) {
        try {
            YouTube youtube = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    null)
                    .setApplicationName("Interview-GO")
                    .build();

            YouTube.Search.List search = youtube.search().list(Collections.singletonList("snippet"));
            search.setKey(apiKey);
            search.setQ(query);
            search.setType(Collections.singletonList("video"));
            search.setMaxResults(5L);

            SearchListResponse response = search.execute();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "에러 발생: " + e.getMessage();
        }
    }
}