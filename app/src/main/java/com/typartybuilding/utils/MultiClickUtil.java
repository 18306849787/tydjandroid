package com.typartybuilding.utils;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-06-03
 * @describe
 */
public class MultiClickUtil {
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isNormalClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

}
