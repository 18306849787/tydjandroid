package com.typartybuilding.gsondata.personaldata;

/**
 *  TA 的微视  信息
 */

public class TaMicro {

    public String code;
    public Data data;
    public String message;





    public class Data {
        public String headImg;
        public int isFollowed;    //0, 是否关注：0：否，1：是
        public int userFollowNum;    //关注数
        public int userFollowedNum;    //被关注数
        public int userId;
        public String userName;
        public int userPraisedNum;    //获赞数

    }
}
