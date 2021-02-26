package com.typartybuilding.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.http.PUT;


/**
 * @author chengchunhuiit@163.com
 * @date 2020-08-22
 * @describe
 */
public class BaseListAct extends BaseAct {

    @BindView(R.id.commont_title)
    public TextView mTitleTv;
    @BindView(R.id.commont_back_close)
    public ImageView mBackClose;
    @BindView(R.id.commont_recyclerview)
    public RecyclerView recyclerView;
    @BindView(R.id.commont_smart_refresh)
    public SmartRefreshLayout refreshLayout;
    @BindView(R.id.commont_title_rl)
    public RelativeLayout mCommonTitleRl;

    public int pageNo = 1;
    public int pageCount;

    @Override
    public void initData() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .navigationBarColor(R.color.white)
                .navigationBarDarkIcon(true)
                .init();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                loadingData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount) {
                    loadingData();
                } else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.commont_back_close)
    public void onClickClosePager() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onClickClosePager();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_common_list;
    }
}
