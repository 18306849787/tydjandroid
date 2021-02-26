package com.typartybuilding.gsondata.choiceness;

import com.typartybuilding.gsondata.ArticleBanner;

public class MoreHotVideoData {

    public String code;
    public Data data;
    public String message;


    public class Data{
        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;
        public ArticleBanner rows[];
        public int totalCount;   //总共多少条新闻

    }

}
