package com.typartybuilding.base;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.typartybuilding.R;
import com.typartybuilding.utils.StatusBarUtil;


public class PbVideoBaseActivity extends AppCompatActivity {

    private String TAG = "PbVideoBaseActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
    }

    private void setStatusBar(){

        //6.0以上系统，更改状态栏图标颜色 为 黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //更改状态栏字体颜色为黑色
            changeStausColor();
        }else {
            //设置状态栏和背景融合
            setFullScreen();
        }


    }

    private void setFullScreen(){
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void changeStausColor(){

        //6.0以上，调用系统方法
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().setStatusBarColor(Color.TRANSPARENT);

    }

}
