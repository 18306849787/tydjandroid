package com.typartybuilding.activity.second.homepager;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.typartybuilding.activity.NewsDetailActivity;
import com.typartybuilding.activity.PlayVideoDetailActivity;
import com.typartybuilding.activity.second.PlayVideoAct;
import com.typartybuilding.activity.second.TextDetailAct;
import com.typartybuilding.adapter.DynamicAdapter;
import com.typartybuilding.adapter.RecommentAdapter;
import com.typartybuilding.base.BaseListAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.HomeRecommentBean;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.loader.HomePagerLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.UserUtils;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-13
 * @describe
 */
@Route(path = RecommentAct.PATH)
public class RecommentAct extends BaseListAct {
    public static final String PATH = "/act/recomment_list";

    HomePagerLoader homePagerLoader;
    RecommentAdapter recommentAdapter;

    @Override
    public void initData() {
        super.initData();
        mTitleTv.setText("每日推荐");
        homePagerLoader = new HomePagerLoader();
        recommentAdapter = new RecommentAdapter();
        recyclerView.setAdapter(recommentAdapter);
        recommentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBanner articleBanner = (ArticleBanner) adapter.getData().get(position);
                if (articleBanner.urlType ==2){
                    ARouter.getInstance().build(PlayVideoAct.PTAH)
                            .withString(PlayVideoAct.URL, articleBanner.videoUrl)
                            .withInt(PlayVideoAct.ARTICLE_TYPE, articleBanner.articleType)
                            .withInt(PlayVideoAct.ARTICLE_ID, articleBanner.articleId)
                            .withInt(PlayVideoAct.URL_TYPE, 2)
                            .navigation();
                }else {
                    ARouter.getInstance().build(TextDetailAct.PATH)
                            .withString(TextDetailAct.URL, articleBanner.articleDetailUrl)
                            .withInt(TextDetailAct.ARTICLE_TYPE, 1)
                            .withInt(TextDetailAct.ARTICLE_ID, articleBanner.articleId)
                            .withInt(TextDetailAct.URL_TYPE, 1)
                            .navigation(RecommentAct.this);
                }
            }
        });
    }

    @Override
    public void loadingData() {
        super.loadingData();
        getHomeRecomment();
    }

    private void getHomeRecomment() {
        homePagerLoader.getHomeRecomment(pageNo,20).subscribe(new RequestCallback<HomeRecommentBean>() {
            @Override
            public void onSuccess(HomeRecommentBean homeDynamicBean) {
                if (pageNo==1){
                    recommentAdapter.setNewData(homeDynamicBean.getRows());
                }else {
                    recommentAdapter.addData(homeDynamicBean.getRows());
                }
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
                    refreshLayout.finishRefresh();
                }
            }
        });
    }
}
