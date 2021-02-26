package com.typartybuilding.gsondata.personaldata;

/**
 *  我的关注
 */

public class MyFocusData {

    public String code;
    public Data data;
    public String message;



    public class Data{
        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        public FocusPeople [] rows;

        public int totalCount;
    }

    public class FocusPeople{
        public long createTime;           //": 1558462676000,
        public int followId;              //": 1,
        public int followedUserId;        //": 2,关注用户id
        public String followedUserImg;     //": "dad",关注用户头像
        public String followedUserName;     //": "das",关注用户昵称
        public long updateTime;             //": 1558462673000,
        public int userId;                  // 用户id
}
}
