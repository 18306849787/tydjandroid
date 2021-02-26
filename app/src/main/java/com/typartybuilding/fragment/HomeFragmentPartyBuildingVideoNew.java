package com.typartybuilding.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentFindFascinating;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentFindFascinatingNew;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentHotBot;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentHotBotNew;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentPopularityList;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentPopularityListNew;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 党建微视 页面
 */
public class HomeFragmentPartyBuildingVideoNew extends BaseFragmentHome {

    private String TAG = "HomeFragmentPartyBuildingVideoNew";

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;

    private FragmentHotBotNew fgHotBot;
    private FragmentPopularityListNew fgPopularity;
    private FragmentFindFascinatingNew fgFindFasci;

    private FragmentManager fragmentManager;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        fragmentManager = getChildFragmentManager();
        fgHotBot = (FragmentHotBotNew)fragmentManager.findFragmentById(R.id.fragment1);
        fgPopularity = (FragmentPopularityListNew)fragmentManager.findFragmentById(R.id.fragment2);
        fgFindFasci = (FragmentFindFascinatingNew)fragmentManager.findFragmentById(R.id.fragment3);

        setRefreshLayout();
    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_party_building_video_new;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void stopLoad() {

    }


    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                fgHotBot.refreshData();
                fgPopularity.refreshData();
                fgFindFasci.refreshData();
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                Log.i(TAG, "onLoadMore: fgFindFasci : " + fgFindFasci);
                fgFindFasci.loadMore();
            }
        });
    }

    //关闭下拉刷新
    public void closeRefresh(){
        if (refreshLayout != null){
            refreshLayout.finishRefresh(true);
        }
    }

    //关闭上拉加载更多
    public void closeLoadMore(){
        if (refreshLayout != null){
            refreshLayout.finishLoadMore(true);
        }
    }

    //没有更多了
    public void noMore(){
        if (refreshLayout != null){
            refreshLayout.finishLoadMore(200);
            Utils.noMore();
        }
    }




}
