package com.typartybuilding.gsondata.personaldata;


/**
 *  个人中心  党员认证   返回的数据
 */
public class PartyCertificationData {

    public String code;
    public Data data;
    public String message;


    public class Data{
        public String idCard;          //"510411199212283211",
        public String nickName;        // "李寻欢",
        public String partyOrg;        //党组织
        public int userType;           // 2,
        public String username;        //"李寻欢"
    }
}
