package com.typartybuilding.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.widget.Toast;

import com.typartybuilding.base.MyApplication;
import com.typartybuilding.utils.NetworkStateUtil;

public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (NetworkStateUtil.isNetWorkConnected(context)) {
            if(NetworkStateUtil.isWifiConnected(context)){

            }
            if(NetworkStateUtil.isMobileConnected(context)){

            }
        }else{
            Toast.makeText(MyApplication.getContext(),"网络未连接",Toast.LENGTH_SHORT).show();
        }


    }
}
