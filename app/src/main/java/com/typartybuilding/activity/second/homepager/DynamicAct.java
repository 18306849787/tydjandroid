package com.typartybuilding.activity.second.homepager;

import android.support.annotation.NonNull;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.second.TextDetailAct;
import com.typartybuilding.adapter.DynamicAdapter;
import com.typartybuilding.base.BaseListAct;
import com.typartybuilding.bean.HomeDynamicBean;
import com.typartybuilding.loader.HomePagerLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import butterknife.OnClick;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-13
 * @describe
 */
@Route(path = DynamicAct.PATH)
public class DynamicAct extends BaseListAct {
    public static final String PATH = "/act/dynamic_list";

    HomePagerLoader homePagerLoader;
    DynamicAdapter dynamicAdapter;


    @Override
    public void initData() {
        super.initData();
        mTitleTv.setText("组工动态");
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount) {
                    getHomeDynamic();
                } else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
        homePagerLoader = new HomePagerLoader();
        dynamicAdapter = new DynamicAdapter();
        recyclerView.setAdapter(dynamicAdapter);
        dynamicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                HomeDynamicBean.RowsBean rowsBean = (HomeDynamicBean.RowsBean) adapter.getData().get(position);
                ARouter.getInstance().build(TextDetailAct.PATH)
                        .withString(TextDetailAct.URL, rowsBean.getGwDetailUrl())
                        .withInt(TextDetailAct.ARTICLE_TYPE, 1)
                        .withInt(TextDetailAct.ARTICLE_ID, rowsBean.getGwId())
                        .withString(TextDetailAct.GW_QUOTATION_TITLE, rowsBean.getGwTitle())
                        .withString("shareType", "dynamic")
                        .withInt("detailType",3)
                        .navigation(DynamicAct.this);
            }
        });

    }

    @Override
    public void loadingData() {
        super.loadingData();
        getHomeDynamic();
    }

    @OnClick({R.id.commont_back_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commont_back_close:
                finish();
                break;
        }

    }


    private void getHomeDynamic() {
        homePagerLoader.getDynamic(pageNo, 20).subscribe(new RequestCallback<HomeDynamicBean>() {
            @Override
            public void onSuccess(HomeDynamicBean homeDynamicBean) {
                pageCount = homeDynamicBean.getPageCount();
                if (pageNo==1){
                    dynamicAdapter.setNewData(homeDynamicBean.getRows());
                }else {
                    dynamicAdapter.addData(homeDynamicBean.getRows());
                }
                if (refreshLayout != null) {
                    if (pageNo == 1) {
                        refreshLayout.finishRefresh();
                    } else {
                        refreshLayout.finishLoadMore();
                    }
                }

                    pageNo++;


            }

            @Override
            public void onFail(Throwable e) {
                RetrofitUtil.requestError();
                if (refreshLayout != null) {
                    refreshLayout.finishLoadMore();
                    refreshLayout.finishRefresh();
                }
            }
        });
    }

}
