package com.typartybuilding.gsondata.pbmicrovideo;

/**
 *  党建微视-人气榜单
 */

public class PopularityListData {

    public String code;
    public PopularityData [] data;
    public String message;


    public class PopularityData{

        public String headImg;
        public String nickName;
        public int userId;
        public int isFollowed;        // 是否已关注 0：否，1：是
    }
}
