package com.typartybuilding.activity.second.lean;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.SlidingTabLayout;
import com.typartybuilding.R;
import com.typartybuilding.activity.second.interactive.kkk;
import com.typartybuilding.base.BaseAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fgPartyBuildingLibrary.FragmentAlbumList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-06
 * @describe
 */
@Route(path = PartyListenerAct.PATH)
public class PartyListenerAct extends BaseAct {
    public static final String PATH = "/act/party_listener";
    @BindView(R.id.party_education_tab)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.party_education_viewpager)
    ViewPager viewPager;


    private ArrayList<Fragment> fsRes = new ArrayList<>();
    private String[] titles = {"精选","新思想","十九大","发展历程","榜样故事"};

    @Override
    public void initData() {
        //初始化 喜马拉雅sdk
        MyApplication.initXiMaLaYa();
        FragmentAlbumList fragment1 = new FragmentAlbumList();
        fragment1.setTagName(null);
        FragmentAlbumList fragment2 = new FragmentAlbumList();
        fragment2.setTagName("新思想");
        FragmentAlbumList fragment3 = new FragmentAlbumList();
        fragment3.setTagName("十九大");
        FragmentAlbumList fragment4 = new FragmentAlbumList();
        fragment4.setTagName("发展历程");
        FragmentAlbumList fragment5 = new FragmentAlbumList();
        fragment5.setTagName("榜样故事");

        fsRes.add(fragment1);
        fsRes.add(fragment2);
        fsRes.add(fragment3);
        fsRes.add(fragment4);
        fsRes.add(fragment5);
        slidingTabLayout.setViewPager(viewPager,titles,this,fsRes);
    }


    @OnClick(R.id.party_education_back)
    void onClick() {
        finish();
    }
    @Override
    public int getLayoutId() {
        return R.layout.layout_act_party_listener;
    }
}
