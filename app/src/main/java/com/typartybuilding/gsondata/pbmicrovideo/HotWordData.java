package com.typartybuilding.gsondata.pbmicrovideo;

/**
 *   党建热搜
 */
public class HotWordData {

    public String code;
    public Data data;
    public String message;

    public class Data{
        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        public HotWord rows[];

        public int totalCount;
    }

    public class HotWord{
        public int hotWordId;        // 1, 主键ID
        public String hotWord;       // “热词”,
        public int searchTimes;      //  搜索次数
        public String wordType;      // "1",  1：系统置顶，0：用户搜索
    }
}
