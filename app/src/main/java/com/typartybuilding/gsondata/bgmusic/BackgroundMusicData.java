package com.typartybuilding.gsondata.bgmusic;

/**
 * 背景音乐
 */

public class BackgroundMusicData {

    public String code;
    public Data data;
    public String message;


    public class Data{
        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        public MusicData[] rows;

        public int totalCount;
    }

    public class MusicData{

         public int bgmDuration;       //15, 时长
         public int bgmId;
         public String bgmImg;         //http:sdfs", 图片
         public String bgmName;         //nanana",名称
         public String bgmUrl;          //http:sdfs", 背景音乐URL
         public long  createTime;       // 1560583593000,
         public int  examineStatus;     // 审核状态
         public int  examineUid;         // 1,
         public String examineUser;      //:审核人员
         public String rejectCause;      // null
    }
}
