package com.interviewgo.mapper;

public class ExamModel {
    private int exUid;
    private int exLangUid;
    private String exTitle;
    private String exContent;
    private int exLevel;
    private String exAnswerList;
    private int viewCount;

    // Getter/Setter
    public int getExUid() { return exUid; }
    public void setExUid(int exUid) { this.exUid = exUid; }
    public int getExLangUid() { return exLangUid; }
    public void setExLangUid(int exLangUid) { this.exLangUid = exLangUid; }
    public String getExTitle() { return exTitle; }
    public void setExTitle(String exTitle) { this.exTitle = exTitle; }
    public String getExContent() { return exContent; }
    public void setExContent(String exContent) { this.exContent = exContent; }
    public int getExLevel() { return exLevel; }
    public void setExLevel(int exLevel) { this.exLevel = exLevel; }
    public String getExAnswerList() { return exAnswerList; }
    public void setExAnswerList(String exAnswerList) { this.exAnswerList = exAnswerList; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
}
