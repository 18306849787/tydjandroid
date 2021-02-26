package com.typartybuilding.gsondata.personaldata;

import java.io.Serializable;

/**
 *  个人基础信息
 */

public class PersonalInfo implements Serializable{

    public String code ;
    public Data data;
    public String message;



    public class Data implements Serializable {
         public long createTime;         //": 1558110755000,
         public String faceRecognition;
         public String headImg;
         public int userIntegral;

         public HmUserParty hmUserParty;

         public String idCard;             //": "510213123213232323",
         public String nickName;           //": "岳不群",
         public String password;           //": "b8a8874e789bcb86c821ed23c4c8953f",
         public String phone;
         public String token;
         public int userId;
         public int userType;
         public String username;
         public boolean signed;
         public String address;
         public String userLevel;
    }

    public class HmUserParty implements Serializable{
         public int examineStatus;    //: 0,
         public String idCard;        //": "510213123213232323",
         public String partyOrg;      //": "1",
         public String phone;         //": "15208197594",
         public String reaName;       //": "2",
         public String rejectCause;    //": null,
         public int userId;
    }
}
