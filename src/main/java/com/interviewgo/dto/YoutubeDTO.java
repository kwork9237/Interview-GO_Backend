package com.interviewgo.dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("youtube")
public class YoutubeDTO {
    private String ytKey;
    private String ytCategory;

}
