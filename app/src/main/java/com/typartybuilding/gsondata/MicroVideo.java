package com.typartybuilding.gsondata;

import java.io.Serializable;

/**
 *  个人 微视数据 和  发现精彩 微视数据,  精选页面修改后，可用于精选页面的 微视
 */

public class MicroVideo implements Serializable {

    public int visionId;         // 1, 主键ID
    public int userId;           //用户ID”,
    public String userName;      //用户昵称",
    public String userHeadImg;    //"头像",
    public String visionTitle;    //":"标题"，
    public int visionType;        //":"1：图片，2：视频，3：音频",
    public String visionFileUrl;   //":"图片、视频或音频，图片多图以逗号隔开",
    public int visionBrowseTimes;    //":1，浏览次数  visionBrowseTimes
    public int visionPraisedNum;      //":1,获赞
    public int isPraise;              //": 0, 是否已点赞 0：否，1：是，
    public int isFollowed;            //": 0, 是否已关注 0：否，1：是
    public String videoCover;   // 视频封面

    //发现精彩中的不同内容
    public long createTime;
    public String rejectCause;
    public int visionExamineStatus;

    //精选页面 中不同内容
    public String bgmName;
    public String examineUid;
    public String examineUser;
    public String hmUser;
    public String topTime;
    public int videoDuration;


    public MicroVideo(int visionId, int userId, String userName, String userHeadImg, String visionTitle, int visionType, String visionFileUrl, String videoCover) {
        this.visionId = visionId;
        this.userId = userId;
        this.userName = userName;
        this.userHeadImg = userHeadImg;
        this.visionTitle = visionTitle;
        this.visionType = visionType;
        this.visionFileUrl = visionFileUrl;
        this.videoCover = videoCover;
    }

    public MicroVideo(int isPraise,int isFollowed,int visionPraisedNum,int visionId, int userId, String userName, String userHeadImg, String visionTitle, int visionType, String visionFileUrl, String videoCover) {
        this.isPraise = isPraise;
        this.isFollowed = isFollowed;
        this.visionPraisedNum = visionPraisedNum;
        this.visionId = visionId;
        this.userId = userId;
        this.userName = userName;
        this.userHeadImg = userHeadImg;
        this.visionTitle = visionTitle;
        this.visionType = visionType;
        this.visionFileUrl = visionFileUrl;
        this.videoCover = videoCover;
    }
}
