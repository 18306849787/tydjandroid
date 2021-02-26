package com.typartybuilding.activity.second.lean;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.second.TextDetailAct;
import com.typartybuilding.base.BaseAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.PoliciesBean;
import com.typartybuilding.loader.LearnLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-06
 * @describe
 */
@Route(path = PoliciesAct.PATH)
public class PoliciesAct extends BaseAct {
    public static final String PATH = "/act/policies";

    @BindView(R.id.polic_recyc)
    RecyclerView recyclerView;
    @BindView(R.id.commont_smart_refresh)
    SmartRefreshLayout refreshLayout;

    PoliciesAdapter policiesAdapter;
    LearnLoader learnLoader;
    public int pageNo = 1;
    public int pageCount;

    @Override
    public void initData() {
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount) {
                    getData(pageNo);
                } else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });

        learnLoader = new LearnLoader();
        policiesAdapter = new PoliciesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(policiesAdapter);
        policiesAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PoliciesBean.RowsBean rowsBean = (PoliciesBean.RowsBean) adapter.getData().get(position);
                ARouter.getInstance().build(TextDetailAct.PATH)
                        .withBoolean(TextDetailAct.IS_HIDE_COLLECT,true)
                        .withBoolean(TextDetailAct.IS_HIDE_SHARE,true)
                        .withString(TextDetailAct.URL, rowsBean.getStatuteUrl())
                        .navigation(PoliciesAct.this);
            }
        });
    }

    @OnClick(R.id.polic_back)
    void onClick() {
        finish();
    }

    @Override
    public void loadingData() {
        super.loadingData();
        getData(pageNo);
    }

    private void getData(int page){
        learnLoader.getStatuteList(page, 20).subscribe(new RequestCallback<PoliciesBean>() {
            @Override
            public void onSuccess(PoliciesBean policiesBean) {
                pageCount = policiesBean.getPageCount();
                if (page == 1){
                    policiesAdapter.setNewData(policiesBean.getRows());
                }else {
                    policiesAdapter.addData(policiesBean.getRows());
                    refreshLayout.finishLoadMore();
                }
                pageNo++;

            }

            @Override
            public void onFail(Throwable e) {
                Toast.makeText(MyApplication.getContext(),"请求失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getLayoutId() {
        return R.layout.layout_act_policies;
    }


    public class PoliciesAdapter extends BaseQuickAdapter<PoliciesBean.RowsBean, BaseViewHolder> {

        public PoliciesAdapter() {
            super(R.layout.layout_item_policies);
        }

        @Override
        protected void convert(BaseViewHolder helper, PoliciesBean.RowsBean item) {
            helper.setText(R.id.item_policies, item.getStatuteTitle());
        }
    }
}
