package com.typartybuilding.utils;

import java.util.Locale;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-09
 * @describe
 */
public class DateUtils {


    public static String formatPlayCount(long playCount){
        String standardPlayCount = "";
        if (playCount < 0) {
            standardPlayCount = "0";
        } else if (playCount < 10000) {
            standardPlayCount = String.valueOf(playCount);
        } else if (playCount < 100000000) {
            standardPlayCount = String.format(Locale.getDefault(), "%d.%02d万", playCount / 10000, playCount % 10000 / 100);
        } else if (playCount > 100000000) {
            standardPlayCount = String.format(Locale.getDefault(), "%d.%02d亿", playCount / 100000000, playCount % 100000000 / 1000000);
        }
        return standardPlayCount;
    }
}
