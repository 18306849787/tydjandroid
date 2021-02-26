package com.typartybuilding.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

/**
 * Created by le.xin on 2017/3/23.
 */

public class MyPlayerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("MyPlayerReceiver.onReceive " + intent);
        if(intent.getAction().equals("com.app.test.android.Action_Close")) {
            XmPlayerManager.release();
        } else if(intent.getAction().equals("com.app.test.android.Action_PAUSE_START")) {
            if(XmPlayerManager.getInstance(context).isPlaying()) {
                XmPlayerManager.getInstance(context).pause();
            } else {
                XmPlayerManager.getInstance(context).play();
            }
         }
    }
}
