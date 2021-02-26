package com.typartybuilding.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.FrameLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.utils.FragmentChangeManager;
import com.gyf.immersionbar.ImmersionBar;
import com.typartybuilding.R;
import com.typartybuilding.activity.loginRelatedActivity.LoginActivity;
import com.typartybuilding.base.BaseAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fragmentbottomnavigation.HomeFra;
import com.typartybuilding.fragment.fragmentbottomnavigation.InteractiveFra;
import com.typartybuilding.fragment.fragmentbottomnavigation.LearnFra;
import com.typartybuilding.fragment.fragmentbottomnavigation.MineFragment;
import com.typartybuilding.fragment.fragmentbottomnavigation.ServiceFra;
import com.typartybuilding.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-03
 * @describe
 */
public class HomeAct extends BaseAct {

    @BindView(R.id.home_bottom_tab)
    CommonTabLayout mCommonTabLayout;
    @BindView(R.id.home_fra_content)
    FrameLayout mContent;


    private ArrayList<Integer> selectedIconRes = new ArrayList<>();         //tab选中图标集合
    private ArrayList<Integer> unselectedIconRes = new ArrayList<>();       //tab未选中图标集合
    private ArrayList<String> titleRes = new ArrayList<>();                 //tab标题集合
    private ArrayList<Fragment> fsRes = new ArrayList<>();                  //fragment集合
    private ArrayList<CustomTabEntity> data = new ArrayList<>();                 //CommonTabLayout 所需数据集合

    private FragmentChangeManager mFragmentChangeManager;
    private int mCurrentPostionFra;
    private boolean isHave;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            isHave = false;
        }else {
            isHave = true;
        }
    }

    @Override
    public void initData() {
        if (isHave){
            return;
        }
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true)
                .navigationBarColor(R.color.white)
                .navigationBarDarkIcon(true)
                .init();
        selectedIconRes.add(R.mipmap.tab_home_sel);
        selectedIconRes.add(R.mipmap.tab_study_sel);
        selectedIconRes.add(R.mipmap.tab_hd_sel);
        selectedIconRes.add(R.mipmap.tab_ser_sel);
        selectedIconRes.add(R.mipmap.tab_me_sel);
        //图片未选中资源
        unselectedIconRes.add(R.mipmap.tab_home_unsel);
        unselectedIconRes.add(R.mipmap.tab_study_unsel);
        unselectedIconRes.add(R.mipmap.tab_hd_unsel);
        unselectedIconRes.add(R.mipmap.tab_ser_unsel);
        unselectedIconRes.add(R.mipmap.tab_me_unsel);
        //标题资源
        titleRes.add("首页");
        titleRes.add("学习");
        titleRes.add("互动");
        titleRes.add("服务");
        titleRes.add("我的");
        //fragment数据
        fsRes.add(new HomeFra());
        fsRes.add(new LearnFra());
        fsRes.add(new InteractiveFra());
        fsRes.add(new ServiceFra());
        fsRes.add(new MineFragment());
        //设置数据
        for (int i = 0; i < titleRes.size(); i++) {
            final int index = i;
            data.add(new CustomTabEntity() {
                @Override
                public String getTabTitle() {
                    return titleRes.get(index);
                }

                @Override
                public int getTabSelectedIcon() {
                    return selectedIconRes.get(index);
                }

                @Override
                public int getTabUnselectedIcon() {
                    return unselectedIconRes.get(index);
                }
            });
        }
        mCommonTabLayout.setTabData(data);
        mFragmentChangeManager = new FragmentChangeManager(this.getSupportFragmentManager(),R.id.home_fra_content, fsRes);

        //设置数据
//        mCommonTabLayout.setTabData((ArrayList<CustomTabEntity>) data, this, R.id.home_fra_content, fsRes);
        mCommonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if ((position == 4)&& UserUtils.getIns().isTourist()){
                    //position == 2||
                    MyApplication.remindVisitor(HomeAct.this);
                    mCommonTabLayout.setCurrentTab(mCurrentPostionFra);
                }else {
                    mCommonTabLayout.setCurrentTab(position);
                    mFragmentChangeManager.setFragments(position);
                    mCurrentPostionFra = position;
                }

            }

            @Override
            public void onTabReselect(int position) {
                Log.e("","");
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_act_home;
    }

    @Override
    public void onDestroy() {
        Log.e("cch","我被收藏了----");
        super.onDestroy();
    }
}
