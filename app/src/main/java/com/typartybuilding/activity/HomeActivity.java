package com.typartybuilding.activity;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.typartybuilding.R;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fragmentbottomnavigation.FragmentHome;
import com.typartybuilding.fragment.fragmentbottomnavigation.FragmentMy;
import com.typartybuilding.fragment.FragmentTitle;
import com.typartybuilding.fragment.HomeFragmentLearningTime;
import com.typartybuilding.fragment.fgLearningTime.FragmentDistanceEducation;
import com.typartybuilding.fragment.fgLearningTime.FragmentEducationFilm;
import com.typartybuilding.fragment.fgLearningTime.FragmentLearnTimeNew;
import com.typartybuilding.fragment.fragmentbottomnavigation.FragmentPartyHome;
import com.typartybuilding.fragment.fragmentbottomnavigation.MineFragment;
import com.typartybuilding.utils.ActivityCollectorUtil;
import com.typartybuilding.view.NoSlideViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseActivity {

    private String TAG = "HomeActivity";

    @BindView(R.id.constraintLayout_bottom)
    public LinearLayout layoutBottom;       //底部导航栏

    /*@BindView(R.id.framelayout_title)
    public FrameLayout frameLayoutTitle;      //用于动态加载fragment 的布局,标题栏*/

    @BindView(R.id.viewpager_home_ac)
    public NoSlideViewPager viewPager;
    @BindViews({R.id.textView_home,R.id.textView_zgzj,R.id.textView_my})
    View [] tabViews;
    /*@BindView(R.id.imageView_plus)
    ImageView imgPlus;                //底部中央的加号图片*/

    private FragmentTitle fragmentTitle;              //标题栏
    private FragmentTransaction transaction;
    private List<Fragment> fragmentList = new ArrayList<>();

    private View currentView;         //HomeActivity页面底部 当前被选中的View
    private PopupWindow popupWindow;  //底部弹窗
    private View popView;             //弹窗布局

    private WebView webViewDisEdu;     //远程教育 网页


    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        ActivityCollectorUtil.finishAll();

        //初始化弹窗
        //initPopupWindow();
        initFragmentList();
        loadFragment(0);   //加载首页
        //setViewPagerAdapter();
        //setPagerChangeListener();
        //viewPager.setCurrentItem(0);
        //设置 首页 被选中
        currentView = tabViews[0];
        setViewSelect(tabViews[0]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCollectorUtil.addActivity(this);
        if (popupWindow != null){
            if (popupWindow.isShowing()){
                popupWindow.dismiss();
            }
        }
    }

    //刷新头像
    public void refreshHeadImg(){
        if (fragmentTitle != null){
            fragmentTitle.refreshHeadImg();
        }
    }

    /**
     * 设置头像可点击
     * @param headImg
     */
    public void setFragmentTitle(CircleImageView headImg){
        headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType == 3){      //访客 不可以进入 我的页面
                    MyApplication.remindVisitor(HomeActivity.this);
                }else {
                    viewPager.setCurrentItem(1);
                    setViewSelect(tabViews[1]);
                }
            }
        });
    }

    /**
     * 动态加载FragMent
     */
    private void loadFragment(int i){
        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout,fragmentList.get(i));
        transaction.commit();
    }

    public void switchFragment(int i) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();/*.setCustomAnimations(
                android.R.anim.fade_in, R.anim.slide_in_from_bottom);*/
        if (!fragmentList.get(i).isAdded()) {    // 先判断是否被add过
            transaction.add(R.id.frameLayout, fragmentList.get(i)).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.show(fragmentList.get(i)).commit(); // 隐藏当前的fragment，显示下一个
        }
        for (int j = 0; j < 3; j ++){
            if (j != i){
                if (fragmentList.get(j).isVisible()){
                    transaction.hide(fragmentList.get(j));
                }
            }
        }
    }

    /**
     * 设置 底部 View 的选择状态
     */
    private void setViewSelect(View view){
        if (view != null && view != currentView){
            currentView.setSelected(false);
        }
        view.setSelected(true);
        currentView = view;
    }

    private void initFragmentList(){
        Fragment fragment1 = new FragmentHome();  //页面 "首页"
        Fragment fragment3 = new MineFragment();    //页面 "我的"
        Fragment fragment2 = new FragmentPartyHome();      //组工之家
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);
    }

   /* private void setViewPagerAdapter(){
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
    }

    private void setPagerChangeListener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //在 "我的" 页面，需去掉标题栏
                if (i == 1){
                    //frameLayoutTitle.setVisibility(View.GONE);
                }else if (i == 0){
                    //frameLayoutTitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }*/

    @OnClick({R.id.textView_home,R.id.textView_my,R.id.textView_zgzj})
    public void setTabClick(View view){

        switch (view.getId()){
            case R.id.textView_home:
                //viewPager.setCurrentItem(0);
                switchFragment(0);
                setViewSelect(view);
                break;
            case R.id.textView_zgzj:
                //viewPager.setCurrentItem(1);
                switchFragment(1);
                setViewSelect(view);
                break;
            case R.id.textView_my:

                if (userType == 3){      //访客 不可以进入 我的页面
                    MyApplication.remindVisitor(this);
                }else {
                    //viewPager.setCurrentItem(2);
                    switchFragment(2);
                    setViewSelect(view);
                }
        }
    }


   /* @OnClick(R.id.imageView_plus)
    public void onClickPlus(){
        if (userType == 3){
            MyApplication.remindVisitor(this);
        }else {
            if (!imgPlus.isSelected()) {
                imgPlus.setSelected(true);
            } else {
                imgPlus.setSelected(false);
            }

            if (!popupWindow.isShowing()) {
                //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
                int dpY = Utils.dip2px(this, 120);
                popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
                //改变屏幕透明度
                Utils.backgroundAlpha(this, 0.7f);
            } else {
                popupWindow.dismiss();
            }
        }
    }*/

   /* private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_my_camera_record, null);
        TextView textCamera = popView.findViewById(R.id.textView_camera);  //拍摄
        TextView textRecord = popView.findViewById(R.id.textView_record);  //录音
        ImageView plus = popView.findViewById(R.id.imageView_plus);     //点击 隐藏弹窗

        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(HomeActivity.this,1f);
                if (imgPlus.isSelected()){
                    imgPlus.setSelected(false);
                }
            }
        });
        //拍摄
        textCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(HomeActivity.this, Camera2Activity.class);
                startActivity(intentAc);
            }
        });
        //朗读厅
        textRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intentAc = new Intent(HomeActivity.this, RecordActivity.class);
                Intent intentAc = new Intent(HomeActivity.this, BookListActivity.class);
                startActivity(intentAc);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                    imgPlus.setSelected(false);

                }
            }
        });
    }*/


    private FragmentLearnTimeNew fgLiveVideo1;
    private FragmentLearnTimeNew fgNewIdeal1;
    private FragmentEducationFilm  fgEduFilm1;
    private FragmentLearnTimeNew fgPioneer1;
    /**
     * 获取 两个webview，在返回事件中使用
     */
    private void getWebView(){

        FragmentHome fragmentHome = null;
        HomeFragmentLearningTime fgLearnTime = null;
        int currentItem = 0;

        if (fragmentList != null && fragmentList.size() > 0) {
            fragmentHome = (FragmentHome) fragmentList.get(0);
        }

       /* if (fragmentHome != null && fragmentHome.fragmentList != null) {
            fgLearnTime = (HomeFragmentLearningTime) fragmentHome.fragmentList.get(1);
        }*/
        if (fragmentHome != null) {
            FragmentStatePagerAdapter pagerAdapter = (FragmentStatePagerAdapter) fragmentHome.viewPager.getAdapter();
            fgLearnTime = (HomeFragmentLearningTime) pagerAdapter.instantiateItem(fragmentHome.viewPager, 1);
            currentItem = fragmentHome.viewPager.getCurrentItem();
        }

        if (currentItem == 1) {
            if (fgLearnTime != null && fgLearnTime.fragmentList != null) {
                int index = fgLearnTime.currentFg;
                if (index == 0) {
                    //直播
                    fgLiveVideo1 = (FragmentLearnTimeNew) fgLearnTime.fragmentList.get(0);
                }else if (index == 1) {
                    //新思想
                    fgNewIdeal1 = (FragmentLearnTimeNew) fgLearnTime.fragmentList.get(1);
                }else if (index == 2) {
                    //党员教育片
                    fgEduFilm1 = (FragmentEducationFilm) fgLearnTime.fragmentList.get(2);
                }else if (index == 3) {
                    //时代先锋
                    fgPioneer1 = (FragmentLearnTimeNew) fgLearnTime.fragmentList.get(3);
                }else if (index == 4) {
                    //远程教育
                    FragmentDistanceEducation fgDisEdu = (FragmentDistanceEducation) fgLearnTime.fragmentList.get(4);
                    webViewDisEdu = fgDisEdu.webView;
                }
            }
        }

       /* HomeFragmentTyOrganization fgTyOrg = (HomeFragmentTyOrganization) fragmentHome.fragmentList.get(5);
        webViewTyOrg = fgTyOrg.webView;*/

    }

    //一直返回，回到桌面，不销毁返回栈Task
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        getWebView();
        //进入远程教育网页，按返回键，可返回到上一个网页
        if (webViewDisEdu != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && webViewDisEdu.canGoBack()) {
                webViewDisEdu.goBack();//返回上个页面

                webViewDisEdu = null;
                return true;
            }

        }
        //进入太原组工网页，按返回键，可返回到上一个网页
       /* if (webViewTyOrg != null){
            if (keyCode == KeyEvent.KEYCODE_BACK && webViewTyOrg.canGoBack()) {
                webViewTyOrg.goBack();//返回上个页面
                return true;
            }
        }*/

        //在直播页面，是否在全屏播放
        if (fgLiveVideo1 != null && fgLiveVideo1.topLiveVideo.isFullScreen){
            if (keyCode == KeyEvent.KEYCODE_BACK){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                fgLiveVideo1 = null;
                return true;
            }
        }
        if (fgNewIdeal1 != null && fgNewIdeal1.topVideo.isFullScreen){
            if (keyCode == KeyEvent.KEYCODE_BACK){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fgNewIdeal1 = null;
                return true;
            }
        }
        if (fgEduFilm1 != null && fgEduFilm1.isFullScreen){
            if (keyCode == KeyEvent.KEYCODE_BACK){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fgEduFilm1 = null;
                return true;
            }
        }
        if (fgPioneer1 != null && fgPioneer1.topVideo.isFullScreen){
            if (keyCode == KeyEvent.KEYCODE_BACK){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fgPioneer1 = null;
                return true;
            }
        }

        Log.i(TAG, "onKeyDown: keyCode : " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }





}
