package com.typartybuilding.utils;

import com.typartybuilding.base.MyApplication;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-05
 * @describe
 */
public class UserUtils {

    public static UserUtils userUtils;

    private UserUtils() {

    }

    public static UserUtils getIns() {
        if (userUtils == null) {
            userUtils = new UserUtils();
        }
        return userUtils;
    }


    public String getToken(){
        return MyApplication.pref.getString(MyApplication.prefKey8_login_token, "");
    }

    public String getUserId(){
        return String.valueOf(MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1));
    }

    public String getUserType(){
        return  String.valueOf(MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1));
    }

    public boolean isTourist(){
       return MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1)==3;
    }
}
