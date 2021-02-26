package com.typartybuilding.base;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;


public class RedTitleBaseActivity extends AppCompatActivity {

   /* //申请相关权限
    private String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION};

    private List<String> mPermissionList = new ArrayList<>();*/

    private String TAG = "LoginRelatedBaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
    }

    public void setStatusBar() {
        //设置 标题栏和状态栏 的颜色一致
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.title));
            //导航栏
            //window.setNavigationBarColor(this.getResources().getColor());
        }

        //设置状态栏和背景融合
      /*  if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //更改状态栏字体颜色
        StatusBarUtil.setStatusBarMode(this,true, R.color.white_bg);*/
    }

    //全屏看图片时，设置状态栏的颜色和图片背景一致
    public void setStatusBar2() {
        //设置 标题栏和状态栏 的颜色一致
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPhotoView));
            //导航栏
            //window.setNavigationBarColor(this.getResources().getColor());
        }
    }

   /* public void requestPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermissionList.size() > 0) {
                mPermissionList.clear();
            }

            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);//添加还未授予的权限
                }
            }
            if (mPermissionList.size() > 0) {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                boolean hasPermissionDismiss = false;//有权限没有通过
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == -1) {
                        hasPermissionDismiss = true;
                    }
                }
                if (hasPermissionDismiss){
                    Toast.makeText(this,"未授权相关功能无法使用",Toast.LENGTH_SHORT).show();
                }

        }
    }*/


}
