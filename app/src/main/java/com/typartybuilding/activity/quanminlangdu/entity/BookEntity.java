package com.typartybuilding.activity.quanminlangdu.entity;

import java.io.Serializable;

public class BookEntity implements Serializable {
    private String examineStatus;
    private String examineUid;
    private String examineUser;
    private long publishDate;
    private String readAuthor;
    private String readCover;
    private String readDetail;
    private String readFrequency;
    private String readId;
    private String readNumber;
    private String readProfile;
    private String readTitle;
    private String rejectCause;
    private long updateTime;
    private int readType; //1诗词 2文章
    private String fileUrl;
    public int getReadType() {
        return readType;
    }

    public void setReadType(int readType) {
        this.readType = readType;
    }

    public BookEntity(String examineStatus, String examineUid, String examineUser, long publishDate, String readAuthor, String readCover,
                      String readDetail, String readFrequency, String readId, String readNumber, String readProfile, String readTitle,
                      String rejectCause, long updateTime, int readType,String fileUrl) {
        this.examineStatus = examineStatus;
        this.examineUid = examineUid;
        this.examineUser = examineUser;
        this.publishDate = publishDate;
        this.readAuthor = readAuthor;
        this.readCover = readCover;
        this.readDetail = readDetail;
        this.readFrequency = readFrequency;
        this.readId = readId;
        this.readNumber = readNumber;
        this.readProfile = readProfile;
        this.readTitle = readTitle;
        this.rejectCause = rejectCause;
        this.updateTime = updateTime;
        this.readType = readType;
        this.fileUrl = fileUrl;
    }


    public BookEntity(){}

    public String getExamineStatus() {
        return examineStatus;
    }

    public void setExamineStatus(String examineStatus) {
        this.examineStatus = examineStatus;
    }

    public String getExamineUid() {
        return examineUid;
    }

    public void setExamineUid(String examineUid) {
        this.examineUid = examineUid;
    }

    public String getExamineUser() {
        return examineUser;
    }

    public void setExamineUser(String examineUser) {
        this.examineUser = examineUser;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public String getReadAuthor() {
        return readAuthor;
    }

    public void setReadAuthor(String readAuthor) {
        this.readAuthor = readAuthor;
    }

    public String getReadCover() {
        return readCover;
    }

    public void setReadCover(String readCover) {
        this.readCover = readCover;
    }

    public String getReadDetail() {
        return readDetail;
    }

    public void setReadDetail(String readDetail) {
        this.readDetail = readDetail;
    }

    public String getReadFrequency() {
        return readFrequency;
    }

    public void setReadFrequency(String readFrequency) {
        this.readFrequency = readFrequency;
    }

    public String getReadId() {
        return readId;
    }

    public void setReadId(String readId) {
        this.readId = readId;
    }

    public String getReadNumber() {
        return readNumber;
    }

    public void setReadNumber(String readNumber) {
        this.readNumber = readNumber;
    }

    public String getReadProfile() {
        return readProfile;
    }

    public void setReadProfile(String readProfile) {
        this.readProfile = readProfile;
    }

    public String getReadTitle() {
        return readTitle;
    }

    public void setReadTitle(String readTitle) {
        this.readTitle = readTitle;
    }

    public String getRejectCause() {
        return rejectCause;
    }

    public void setRejectCause(String rejectCause) {
        this.rejectCause = rejectCause;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
