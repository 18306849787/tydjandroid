package com.typartybuilding.gsondata.dreamwish;

import java.io.Serializable;

/**
 *  用于  圆梦心愿 数据， 我的 和 他的 心愿数据
 */

public class DreamWishData {

    public String code;
    public Data data;
    public String message;


    public class Data{

        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        public WishData [] rows;

        public int totalCount;
    }

    public class WishData implements Serializable {
        public int aspirationId;              // 主键ID
        public String aspirationTitle;        // “标题”,
        public String aspirationDetailUrl;    // "详情富文本链接",
        public int aspirationBrowseTimes;     //浏览量
        public long aspirationPublishDate;    //9876543229876，发布时间
        public String aspirationImg;          //图片，多图以逗号隔开
        public String aspirationVoice;        //语音文件地址",
        public int needHeartNum;              //总需求
        public int heartNum;                 // 已获助力捐赠,
        public int aspirationStatus;         //0：募集爱心，1：待认领，3：心愿认领，4：心愿验收，5：验收驳回，6：心愿完成
        //public String aspirationProfile;     //简介
        public int isDonation;               //0：未捐赠，1：已捐赠
        public String provideMoney;          //奖励金
        public long expirationTime;         //过期时间，
        public String provideOrgan;                //出资机构"
        //新增需求，我的心愿 和 他的心愿 新增字段
        public String finishCredential;       //完成凭据，多图以逗号隔开
        public long receiveTime;             //认领时间
        public long applyTime;               //验收申请时间
        public String denyCause;            //我的心愿 中 验收不通过的原因

    }

}
