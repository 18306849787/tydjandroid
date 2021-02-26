package com.typartybuilding.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindView;


/**
 * @author chengchunhuiit@163.com
 * @date 2020-08-22
 * @describe
 */
public  class BaseListFra extends BaseFra {

    @BindView(R.id.commont_title)
    public TextView mTitleTv;
    @BindView(R.id.commont_back_close)
    public ImageView mBackClose;
    @BindView(R.id.commont_recyclerview)
    public RecyclerView recyclerView;
    @BindView(R.id.commont_smart_refresh)
    public SmartRefreshLayout  refreshLayout;
    @BindView(R.id.commont_title_rl)
    public RelativeLayout mCommonTitleRl;

    public int pageNo = 1;
    public int pageCount;
    @Override
    public void initData() {
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_fra_common_list;
    }
}
