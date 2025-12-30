package com.interviewgo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.interviewgo.dto.YoutubeDTO;

@Mapper
public interface YoutubeMapper {
	 // 이 메서드 선언이 있어야 Service의 빨간 줄이 사라집니다
	 void insertVideo(YoutubeDTO dto);
	 
	 List<YoutubeDTO> selectVideosByCategory(String category);
	 
	 void deleteAllVideos();
	
}
