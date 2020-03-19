package com.codepros.prohub.model;

public class News {

    int newsId;
    int propId;
    String newsTitle;
    String content;
    String imageUrl;
    String createTime;
    String targetViewer;
    Boolean hideFlag;


    public News(){++newsId;}
    public News(int propId, String newsTitle, String content, String imageUrl, String createTime, String targetViewer, Boolean hideFlag) {
        ++newsId;
        this.propId = propId;
        this.newsTitle = newsTitle;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createTime = createTime;
        this.targetViewer = targetViewer;
        this.hideFlag = hideFlag;
    }


    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public int getPropId() {
        return propId;
    }

    public void setPropId(int propId) {
        this.propId = propId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTargetViewer() {
        return targetViewer;
    }

    public void setTargetViewer(String targetViewer) {
        this.targetViewer = targetViewer;
    }

    public Boolean getHideFlag() {
        return hideFlag;
    }

    public void setHideFlag(Boolean hideFlag) {
        this.hideFlag = hideFlag;
    }


}
