package com.typartybuilding.activity.second.lean;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayVideoDetailActivity;
import com.typartybuilding.activity.second.PlayVideoAct;
import com.typartybuilding.activity.second.TextDetailAct;
import com.typartybuilding.base.BaseAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.ArticleVideoDataNew;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.loader.LearnLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.DateUtils;
import com.typartybuilding.utils.ImageOpUtils;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-05
 * @describe
 */
@Route(path = XrxyAct.PATH)
public class XrxyAct extends BaseAct {

    public static final String PATH = "/activity/xrxy";

    @BindView(R.id.xrxy_recycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;    //下拉刷新，上拉加载

    XrxyAdapter adapter;
    LearnLoader learnLoader;
    int pageNo = 1;
    int pageCount;

    @Override
    public void initData() {
        //禁止下拉刷新
        refreshLayout.setEnableRefresh(false);
//        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount) {
                    getArticleVideoData();
                } else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
        learnLoader = new LearnLoader();
        adapter = new XrxyAdapter();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBanner data = (ArticleBanner) adapter.getData().get(position);
                ARouter.getInstance().build(PlayVideoAct.PTAH)
                        .withString(PlayVideoAct.URL, data.videoUrl)
                        .withInt(PlayVideoAct.ARTICLE_TYPE, data.articleType)
                        .withInt(PlayVideoAct.ARTICLE_ID, data.articleId)
                        .withInt(PlayVideoAct.URL_TYPE, 2)
                        .navigation();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void loadingData() {
        super.loadingData();
        getArticleVideoData();
    }

    public void getArticleVideoData() {
        learnLoader.getArticleVideoData(pageNo, 20, 5).subscribe(

                new RequestCallback<ArticleVideoDataNew.Data>() {
                    @Override
                    public void onSuccess(ArticleVideoDataNew.Data articleVideoData) {
                        if (pageNo == 1) {
                            adapter.setNewData(articleVideoData.rows);
                        } else {
                            adapter.addData(articleVideoData.rows);
                        }
                        pageCount = articleVideoData.pageCount;
                        if (refreshLayout != null) {
                            if (pageNo == 1) {
                                refreshLayout.finishRefresh();
                            } else {
                                refreshLayout.finishLoadMore();
                            }
                        }
                        if (pageNo<pageCount){
                            pageNo++;
                        }
                    }

                    @Override
                    public void onFail(Throwable e) {
                        RetrofitUtil.requestError();
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                        }
                    }
                }
        );
    }


    @OnClick(R.id.xrxy_back)
    void onClick() {
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_act_xrxy;
    }


    public class XrxyAdapter extends BaseQuickAdapter<ArticleBanner, BaseViewHolder> {

        public XrxyAdapter() {
            super(R.layout.layout_adpter_item_xrxy);
        }

        @Override
        protected void convert(BaseViewHolder helper, ArticleBanner item) {
            helper.setText(R.id.xrxy_title, item.articleTitle);
            helper.setVisible(R.id.xrxy_fra_times, item.urlType == 2);
            SuperTextView superTextView = helper.getView(R.id.xrxy_fra_times);
            superTextView.setCenterString(Utils.formatTime(item.videoDuration));

            helper.setText(R.id.xrxy_num, DateUtils.formatPlayCount(item.browseTimes) + "次播放");
            helper.setText(R.id.xrxy_time, DateFormat.format("yyyy.MM.dd", item.updateTime));
            Glide.with(mContext).load(item.videoCover)
                    .apply(ImageOpUtils.getYuanRequestOptions(2))
                    .into((ImageView) helper.getView(R.id.xrxy_fra_img));
        }
    }

}
