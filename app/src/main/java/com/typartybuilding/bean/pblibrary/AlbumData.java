package com.typartybuilding.bean.pblibrary;

import java.io.Serializable;

public class AlbumData implements Serializable {

    public long albumId;       //专辑id
    public String imgUrl;     //专辑图片url
    public String playTimes;   //专辑播放次数
    public String headLine;   //专辑标题或名称
    public String subHead;    //专辑子标题或简介

    public AlbumData(long albumId, String imgUrl, String playTimes, String headLine, String subHead) {
        this.albumId = albumId;
        this.imgUrl = imgUrl;
        this.playTimes = playTimes;
        this.headLine = headLine;
        this.subHead = subHead;
    }

    public AlbumData() {
    }
}
