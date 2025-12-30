package com.interviewgo.dto;

import org.apache.ibatis.type.Alias;

@Alias("youtube")
public class YoutubeDTO {
    private String ytKey;
    private String ytCategory;

    // 수동으로 만든 Getter와 Setter (STS가 즉시 인식합니다)
    public String getYtKey() {
        return ytKey;
    }

    public void setYtKey(String ytKey) {
        this.ytKey = ytKey;
    }

    public String getYtCategory() {
        return ytCategory;
    }

    public void setYtCategory(String ytCategory) {
        this.ytCategory = ytCategory;
    }
}
