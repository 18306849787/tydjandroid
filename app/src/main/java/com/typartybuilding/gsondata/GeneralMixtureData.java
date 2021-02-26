package com.typartybuilding.gsondata;

import com.typartybuilding.gsondata.MixtureData;

/**
 *  我的收藏，我的足迹，搜索，党建热搜  通用接收数据 对象
 */
public class GeneralMixtureData {

    public String code;
    public Data data;
    public String message;


    public class Data{

        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        public MixtureData rows[];

        public int totalCount;

    }
}
