package com.typartybuilding.activity.quanminlangdu.entity;

import java.io.Serializable;

public class MusicEntity implements Serializable {
    private String bgmID;
    private int bgmDuration;
    private String bgmImg;
    private String bgmName;
    private String bgmUrl;
    private int createTime;
    private String bgmProfile;

    public MusicEntity(String bgmID, int bgmDuration, String bgmImg, String bgmName, String bgmUrl, int createTime, String bgmProfile) {
        this.bgmID = bgmID;
        this.bgmDuration = bgmDuration;
        this.bgmImg = bgmImg;
        this.bgmName = bgmName;
        this.bgmUrl = bgmUrl;
        this.createTime = createTime;
        this.bgmProfile = bgmProfile;
    }

    public String getStrTime(){
        return getTimeText(this.bgmDuration);
    }
    private String getTimeText(int nowtime){
        int nmint = nowtime/60;
        int nsec = (nowtime)%60;
        StringBuilder builder = new StringBuilder();
        if (nmint<10){
            builder.append(0).append(nmint).append(":");
        }else {
            builder.append(nmint).append(":");
        }
        if (nsec<10){
            builder.append(0).append(nsec);
        }else {
            builder.append(nsec);
        }
        return builder.toString();
    }
    public String getBgmID() {
        return bgmID;
    }

    public void setBgmID(String bgmID) {
        this.bgmID = bgmID;
    }

    public int getBgmDuration() {
        return bgmDuration;
    }

    public void setBgmDuration(int bgmDuration) {
        this.bgmDuration = bgmDuration;
    }

    public String getBgmImg() {
        return bgmImg;
    }

    public void setBgmImg(String bgmImg) {
        this.bgmImg = bgmImg;
    }

    public String getBgmName() {
        return bgmName;
    }

    public void setBgmName(String bgmName) {
        this.bgmName = bgmName;
    }

    public String getBgmUrl() {
        return bgmUrl;
    }

    public void setBgmUrl(String bgmUrl) {
        this.bgmUrl = bgmUrl;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public String getBgmProfile() {
        return bgmProfile;
    }

    public void setBgmProfile(String bgmProfile) {
        this.bgmProfile = bgmProfile;
    }
}
