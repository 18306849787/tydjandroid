package com.typartybuilding.gsondata;

import com.typartybuilding.gsondata.choiceness.ChoicenessData;

/**
 * 学习时刻，所有视频数据的 接收对象
 */

public class ArticleVideoData {

    public String code;
    public Data data;
    public String message;


    public static class Data{
        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;
        public ArticleBanner rows[];
        public int totalCount;   //总共多少条

    }
}
