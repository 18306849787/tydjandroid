package com.typartybuilding.gsondata.choiceness;

import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.MicroVideo;

import java.io.Serializable;

public class ChoicenessNewData {

    public String code;
    public Data data;
    public String message;


    public class Data{

        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        //public ChoicenessNew[] rows;

        public ArticleBanner[] rows;

        public int totalCount;
    }


    public class ChoicenessNew{

        public ArticleBanner articleBanner;
        public MicroVideo [] visionList;
    }



}
