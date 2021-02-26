package com.typartybuilding.gsondata.loginregister;

public class VisitorData {

    public String code;
    public Data data;
    public String message;




    public class Data{
        public String createTime;
        public String nickName;
        public String token;
        public int userId;
        public int userType;    //1：普通用户，2：认证党员，3：临时账号

    }
}
