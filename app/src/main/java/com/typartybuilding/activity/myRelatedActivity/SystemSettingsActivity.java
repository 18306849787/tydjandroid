package com.typartybuilding.activity.myRelatedActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.loginRelatedActivity.LoginActivity;
import com.typartybuilding.activity.pbmap.ServiceCenterActivity;
import com.typartybuilding.adapter.viewPagerAdapter.ServiceCenterVpAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ActivityCollectorUtil;
import com.typartybuilding.utils.GlideCacheUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.utils.appmanager.AppManageHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SystemSettingsActivity extends RedTitleBaseActivity {

    private String TAG = "SystemSettingsActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindView(R.id.button1)
    Button btnClearCache;            //清除缓存
    @BindView(R.id.textView_cache)
    TextView textCache;              //缓存的大小

    @BindView(R.id.button2)
    Button btnExit;                 //退出登陆
    @BindView(R.id.button3)
    Button btnAgreement;            //用户协议

    public PopupWindow popupWindow1;  //底部弹窗  ,清除缓存
    public View popView1;             //弹窗布局

    public PopupWindow popupWindow2;  //底部弹窗  ,退出登陆
    public View popView2;             //弹窗布局


    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

    private String cache;      //图片缓存大小

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings);
        ButterKnife.bind(this);
        textTitle.setText("系统设置");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取glide 的缓存
        cache = GlideCacheUtil.getInstance().getCacheSize(this);
        textCache.setText(cache);
        //初始化弹窗
        initPopupWindow1();
        initPopupWindow2();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (popupWindow1 != null){
            if (popupWindow1.isShowing()){
                popupWindow1.dismiss();
            }
        }
        if (popupWindow2 != null){
            if (popupWindow2.isShowing()){
                popupWindow2.dismiss();
            }
        }
    }

    //清除缓存 和 退出登陆  , 用户协议
    @OnClick({R.id.button1, R.id.button2,R.id.button3})
    public void onClickBtn(View view){
        switch (view.getId()){
            case R.id.button1:
                showPopupWindow1();
                break;
            case R.id.button2:
                showPopupWindow2();
               /* exitLogin();
                //调退出登陆接口时，按钮不可再点击
                btnExit.setEnabled(false);*/
                break;
            case R.id.button3:
                Intent intentAc = new Intent(this,UserAgreementActivity.class);
                startActivity(intentAc);
                break;
        }
    }

    //清除缓存
    private void showPopupWindow1(){
        if (!popupWindow1.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            popupWindow1.showAtLocation(popView1, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
    }

    //退出登陆
    private void showPopupWindow2(){
        if (!popupWindow2.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            popupWindow2.showAtLocation(popView2, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
    }

    //清除缓存弹窗
    private void initPopupWindow1(){
        popView1 = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_system_setting, null);
        popupWindow1 = new PopupWindow(popView1,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView textView = popView1.findViewById(R.id.textView);
        Button btnConfirm = popView1.findViewById(R.id.button_comfirm);
        Button btnCancel = popView1.findViewById(R.id.button_cancel);

        //确定删除所有233MB缓存？离线内容及图片均会被清除
        textView.setText("确定删除所有" + cache + "缓存？离线内容及图片均会被清除");

        popupWindow1.setTouchable(true);
        //点击外部消失
        popupWindow1.setOutsideTouchable(true);
        popupWindow1.setFocusable(true);

        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(SystemSettingsActivity.this,1f);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow1.isShowing()) {
                    popupWindow1.dismiss();
                }
            }
        });

        //确定
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlideCacheUtil.getInstance().clearImageAllCache(SystemSettingsActivity.this);
                if (popupWindow1.isShowing()) {
                    popupWindow1.dismiss();
                }
                //获取glide 的缓存
                cache = GlideCacheUtil.getInstance().getCacheSize(SystemSettingsActivity.this);
                textCache.setText(cache);
                //初始化弹窗
                initPopupWindow1();
                initPopupWindow2();

            }
        });
    }

    //退出登陆
    private void initPopupWindow2(){
        popView2 = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_system_setting, null);
        popupWindow2 = new PopupWindow(popView2,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView textView = popView2.findViewById(R.id.textView);
        Button btnConfirm = popView2.findViewById(R.id.button_comfirm);
        Button btnCancel = popView2.findViewById(R.id.button_cancel);

        textView.setText("确定退出当前登录？");

        popupWindow2.setTouchable(true);
        //点击外部消失
        popupWindow2.setOutsideTouchable(true);
        popupWindow2.setFocusable(true);

        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(SystemSettingsActivity.this,1f);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow2.isShowing()) {
                    popupWindow2.dismiss();
                }
            }
        });

        //确定
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitLogin();
                //调退出登陆接口时，按钮不可再点击
                btnExit.setEnabled(false);
                if (popupWindow2.isShowing()) {
                    popupWindow2.dismiss();
                }
            }
        });
    }



    private void exitLogin(){
        Log.i(TAG, "exitLogin: userId : " + userId);
        Log.i(TAG, "exitLogin: token : " + token);
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.exitLogin(userId, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReciMsgData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ReciMsgData reciMsgData) {
                        int code = Integer.valueOf(reciMsgData.code);
                        Log.i(TAG, "onNext: code : " + reciMsgData.code);
                        if (code == 0){
                            Intent intentAc = new Intent(SystemSettingsActivity.this, LoginActivity.class);
                            startActivity(intentAc);
                            //登陆状态清0
                            MyApplication.editor.putInt(MyApplication.prefKey7_login_state,0);
                            MyApplication.editor.apply();
                            //销毁该页面 和 HomeActivity
                            finish();
//                            ActivityCollectorUtil.finishAll();
                            AppManageHelper.finishOtherActivity(LoginActivity.class);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(reciMsgData.message);
                            btnExit.setEnabled(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        btnExit.setEnabled(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }




}
