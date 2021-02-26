package com.typartybuilding.activity.quanminlangdu.utils;

import com.typartybuilding.utils.UserUtils;

import org.jetbrains.annotations.Contract;

public class Config {

    public static final String SERVER_URL = "http://39.100.55.119:8080/";

    public static final String GET_BOOK_LIST = "app/read/queryByPage";
    public static final String GET_BGM_LIST = "app/readBgm/selectListWithPage";
    public static final String GET_BOOK_DETAIL = "app/read/queryDetail";
    public static final String ADD_READ_COUNT = "app/read/addRead";

    private static String token = "7993ce9189712d1249bf2c2297cfa412";
    private static String userId = "18";

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        Config.userId = userId;
    }

    public static void setToken(String token){
        Config.token = token;
    }

    @Contract(pure = true)
    public static String getToken(){
        return UserUtils.getIns().getToken();
    }

    public static String getActionUrl(String action){
        return SERVER_URL + action;
    }

    public static final int SEARCH_BOOK_LIST_SUCCESS = 1;
    public static final int SEARCH_BOOK_LIST_FAIL = 2;
    public static final int SEARCH_BOOK_LIST_MORE_SUCCESS = 3;

    public static final int MUSIC_SEARCH_ITEM_SUCCESS = 4;
    public static final int MUSIC_SEARCH_ITEM_FAIL = 5;
    public static final int MUSIC_SEARCH_MORE_ITEM_SUCCESS = 6;
    public static final int MUSIC_CHOOSE_RESULT_OK = 7;
    public static final int MUSIC_CHOOSE_REQUEST = 8;

    public static final int SEARCH_BOOK_DETAIL = 9;

    public static final int AUDIO_PLAY_OVER = 10;
    public static final int AUDIO_PLAY_TIME = 11;
    public static final int PLAY_TIME_DO = 12;
}
