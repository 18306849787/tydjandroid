package com.typartybuilding.gsondata.loginregister;

import java.io.Serializable;

/**
 * 密码登陆 成功后，获取到的用户数据
 */

public class UserData implements Serializable {

    public String code;
    public Data data;
    public String message;



    public class Data {
        public String createTime;
        public String faceRecognition;

        public String headImg;
        public String idCard;
        public String nickName;
        public String password;

        public String phone;
        public String token;
        public int userId;
        public int userType;     //1：普通用户，2：认证党员，3：临时账号
        public int userIntegral;

    }






}
