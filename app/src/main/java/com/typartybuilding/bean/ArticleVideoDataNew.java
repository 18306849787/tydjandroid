package com.typartybuilding.bean;

import com.typartybuilding.gsondata.ArticleBanner;

import java.util.List;

/**
 * 学习时刻，所有视频数据的 接收对象
 */

public class ArticleVideoDataNew {

    public String code;
    public Data data;
    public String message;


    public class Data{
        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;
        public List<ArticleBanner> rows;
        public int totalCount;   //总共多少条

    }
}
