package com.interviewgo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewgo.jwt.JwtTokenProvider;

@RestController
@RequestMapping("/test")
public class TestController {
//    @Autowired
  //  private JwtTokenProvider jwtTokenProvider;

    // JWT 테스트 토큰 컨트롤러
//    @GetMapping("/token")
//    public String createTestToken() {
      //  return jwtTokenProvider.createToken("test@example.com", "user");
//    }
}
