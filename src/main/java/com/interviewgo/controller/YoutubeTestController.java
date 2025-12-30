package com.interviewgo.controller;

import com.interviewgo.service.YoutubeTestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YoutubeTestController {

    @Autowired
    private YoutubeTestService youtubeService;

    // 브라우저에서 http://localhost:8080/api/search?q=면접꿀팁 처럼 호출
    @GetMapping("/api/search")
    public String search(@RequestParam(value = "q", defaultValue = "면접") String query) {
        return youtubeService.searchVideos(query);
    }
}