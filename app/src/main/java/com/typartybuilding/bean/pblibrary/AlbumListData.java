package com.typartybuilding.bean.pblibrary;

import java.util.ArrayList;
import java.util.List;

public class AlbumListData {

    public int albumIds[];       //专辑id
    public String imgUrls[];     //专辑图片url
    public String playTimes[];   //专辑播放次数
    public String headLines[];   //专辑标题或名称
    public String subHeads[];    //专辑子标题或简介

    public AlbumData data1;
    public AlbumData data2;
    public AlbumData data3;

    public List<AlbumData> albumDataList = new ArrayList<>();

    public AlbumListData(List<AlbumData> albumDataList) {
        this.albumDataList = albumDataList;
    }

    public AlbumListData(AlbumData data1, AlbumData data2, AlbumData data3) {
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
    }

    public AlbumListData(int[] albumIds, String[] imgUrls, String[] playTimes, String[] headLines, String[] subHeads) {
        this.albumIds = albumIds;
        this.imgUrls = imgUrls;
        this.playTimes = playTimes;
        this.headLines = headLines;
        this.subHeads = subHeads;
    }

    public AlbumListData() {
    }
}
