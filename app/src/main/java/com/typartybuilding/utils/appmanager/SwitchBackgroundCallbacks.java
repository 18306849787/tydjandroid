package com.typartybuilding.utils.appmanager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-10-02
 * @describe
 */
//activity生命的回调
public class SwitchBackgroundCallbacks implements Application.ActivityLifecycleCallbacks {
    private static int refCount = 0;
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        /**
         *  监听到 Activity创建事件 将该 Activity 加入list
         */
        AppManageHelper.pushActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        refCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        refCount--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (null == AppManageHelper.mActivityList || AppManageHelper.mActivityList.isEmpty()) {
            return;
        }
        if (AppManageHelper.mActivityList.contains(activity)) {
            /**
             *  监听到 Activity销毁事件 将该Activity 从list中移除
             */
            AppManageHelper.popActivity(activity);
        }
    }

    public static boolean isAppGoBackGround(){
        return refCount== 0;
    }
}
