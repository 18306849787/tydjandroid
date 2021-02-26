package com.typartybuilding.gsondata.personaldata;

import com.typartybuilding.gsondata.MicroVideo;

/**
 *  TA 的微视  信息
 */

public class TaMicroVideo {

    public String code;
    public Data data;
    public String message;



    public class Data {
        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        public MicroVideo[] rows;

        public int totalCount;


    }

    /*public class MicroVideo implements Serializable {

        public int visionId;         // 1, 主键ID
        public int userId;           //用户ID”,
        public String userName;      //用户昵称",
        public String userHeadImg;    //"头像",
        public String visionTitle;    //":"标题"，
        public int visionType;        //":"1：图片，2：视频，3：音频",
        public String visionFileUrl;   //":"图片、视频或音频，图片多图以逗号隔开",
        public int visionBrowseTimes;    //":1，浏览次数
        public int visionPraisedNum;      //":1,获赞
        public int isPraise;              //": 0, 是否已点赞 0：否，1：是，
        public int isFollowed;            //": 0, 是否已关注 0：否，1：是
    }*/
}
