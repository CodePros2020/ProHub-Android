package com.codepros.prohub.model;

import android.net.Uri;

public class News {

    String propId;
    String creatorPhoneNumber;
    String newsTitle;
    String content;
    String imageUrl;
    String createTime;
    String targetViewer;
    Boolean hideFlag;


    public News(){}
    public News(String propId, String creatorPhoneNumber, String newsTitle, String content, String imageUrl, String createTime, String targetViewer, Boolean hideFlag) {
        this.propId = propId;
        this.creatorPhoneNumber = creatorPhoneNumber;
        this.newsTitle = newsTitle;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createTime = createTime;
        this.targetViewer = targetViewer;
        this.hideFlag = hideFlag;
    }

    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
    }

    public String getCreatorPhoneNumber() {
        return creatorPhoneNumber;
    }

    public void setCreatorPhoneNumber(String creatorPhoneNumber) {
        this.creatorPhoneNumber = creatorPhoneNumber;
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


    public Uri getImageUri(String imgPath){
        Uri imgUri=Uri.parse(imgPath);
        return imgUri;
    }
    public String getShortDes(String desc){
        String shortDes;
        if(desc.length() > 150 ){
            shortDes=desc.substring(0,150) + "...";
        }
        else{
            shortDes=desc;
        }
        return shortDes;
    }
}
