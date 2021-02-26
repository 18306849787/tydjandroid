package com.typartybuilding.bean;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-12-01
 * @describe
 */
public class GroupWorkDetailsBean {

    String gwId;
    String gwDetailUrl;
    int isCollect;
    int collectedNum;

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public String getGwDetailUrl() {
        return gwDetailUrl;
    }

    public void setGwDetailUrl(String gwDetailUrl) {
        this.gwDetailUrl = gwDetailUrl;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public int getCollectedNum() {
        return collectedNum;
    }

    public void setCollectedNum(int collectedNum) {
        this.collectedNum = collectedNum;
    }
}
