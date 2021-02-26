package com.typartybuilding.bean.pblibrary;

public class TrackData {


    public String imgUrl;
    public long trackId;
    public String headLine;
    public int playTimes;
    public int playDuration;
    public Long date;

    public TrackData(String imgUrl,long trackId, String headLine, int playTimes, int playDuration, long date) {
        this.imgUrl = imgUrl;
        this.trackId = trackId;
        this.headLine = headLine;
        this.playTimes = playTimes;
        this.playDuration = playDuration;
        this.date = date;
    }

    public TrackData() {
    }
}
