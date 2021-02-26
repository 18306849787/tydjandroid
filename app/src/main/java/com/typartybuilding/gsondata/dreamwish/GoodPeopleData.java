package com.typartybuilding.gsondata.dreamwish;

public class GoodPeopleData {

    public String code;
    public Data data;
    public String message;

    public class Data{

        public int pageCount;    //总共多少页
        public int pageEnd;
        public int pageNo;
        public int pageSize;
        public int pageStart;

        public GoodPeople [] rows;

        public int totalCount;
    }

    public class GoodPeople{
        public String username;
        public String headImg;
        public String phone;
        public String partyOrg;
        public int aspirationFinish;
        public int userId;
        public int ranking;       //第一条数据使用，为当前用户 的排名
    }
}
