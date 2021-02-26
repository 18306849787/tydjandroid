package com.typartybuilding.gsondata.tyorganization;

import java.io.Serializable;

public class TyOrganizationData {

    public String code;
    public Data data;
    public String message;

    public class Data{

        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        public OrganizationData [] rows;

        public int totalCount;
    }

    public class OrganizationData implements Serializable  {

        public int gwId;
        public String gwTitle;
        public String gwDetailUrl;
        public int browseTimes;
        public long publishDate;     //发布时间
        public String picUrls;

    }


}
