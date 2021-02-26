package com.typartybuilding.activity.loginRelatedActivity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.adapter.fragmentAdapter.FragmentAdapterLoginActivity;
import com.typartybuilding.base.LoginRelatedBaseActivity;
import com.typartybuilding.fragment.loginregister.FragmentFaceLogin;
import com.typartybuilding.fragment.loginregister.FragmentLogin;
import com.typartybuilding.utils.ActivityCollectorUtil;
import com.typartybuilding.utils.appmanager.AppManageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends LoginRelatedBaseActivity {

    private String TAG = "LoginActivity";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentAdapterLoginActivity fragmentAdapterLoginActivity;
    private FragmentLogin fragmentLogin;
    private FragmentFaceLogin fragmentFaceLogin;

    private Timer timer;
    private TimerTask task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        ActivityCollectorUtil.finishAll();
        AppManageHelper.finishOtherActivity(LoginActivity.class);
        //延时1 秒 申请app需要的权限
        delay();
        initView();
    }

    private void delay(){
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                requestPermission();
            }
        };
        timer.schedule(task,2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        if (task != null){
            task.cancel();
            task = null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCollectorUtil.addActivity(this);
    }

    private void initView(){
        tabLayout = findViewById(R.id.tablayout_login_ac);
        viewPager = findViewById(R.id.viewpager_login_ac);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentLogin = new FragmentLogin();
        fragmentFaceLogin = new FragmentFaceLogin();
        //初始化 tab的标题
        List<String> titles = new ArrayList<>();
        String accountLogin = getResources().getString(R.string.account_login);
        String faceLogin = getResources().getString(R.string.face_login);
        titles.add(accountLogin);
        titles.add(faceLogin);
        //添加碎片
        fragmentList.add(fragmentLogin);
        fragmentList.add(fragmentFaceLogin);
        viewPager.setAdapter(new FragmentAdapterLoginActivity(getSupportFragmentManager(),fragmentList,titles));
        //加载 tab 的布局
        /*View tabLeft = View.inflate(this,R.layout.layout_tablayout_tab_left_la,null);
        View tabRight = View.inflate(this,R.layout.layout_tablayout_tab_right_la,null);*/

        /*tabLayout.addTab(tabLayout.newTab().setText(titles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(1)));*/

        //为tab 添加布局
        for (int j = 0; j < titles.size(); j++){
            /*TextView textView = new TextView(this);
            textView.setText(titles.get(j));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            tabLayout.addTab(tabLayout.newTab().setCustomView(textView));*/

            tabLayout.addTab(tabLayout.newTab().setText(titles.get(j)));
           /* if (j == 0) {
                tabLayout.addTab(tabLayout.newTab().setCustomView(tabLeft));
                //tabLayout.addTab(tabLayout.newTab().setText("账号登录"));
            }else if (j == 1){
                tabLayout.addTab(tabLayout.newTab().setCustomView(tabRight));
                //tabLayout.addTab(tabLayout.newTab().setText("人脸识别登录"));
            }*/
        }

        //Utils.setTabLayoutIndicator(tabLayout);
        //Utils.reflex(tabLayout);
        //Utils.setTabWidthSame(tabLayout);
        //tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                View textView = tab.getCustomView();
               /* TextView textView = null;
                if (null != view) {
                    textView = view.findViewById(R.id.textView_tab);
                }*/
                if (null != textView && textView instanceof TextView){
                    ((TextView)textView).setTextColor(getResources().getColor(R.color.tab_black));
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                View textView = tab.getCustomView();
               /* TextView textView = null;
                if (null != view) {
                    textView = view.findViewById(R.id.textView_tab);
                }*/
                if (null != textView && textView instanceof TextView){
                    ((TextView)textView).setTextColor(getResources().getColor(R.color.tab_gray));
                }

            }
        });

    }

    //一直返回，回到桌面，不销毁返回栈Task
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: keyCode : " + keyCode);

        if (keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
