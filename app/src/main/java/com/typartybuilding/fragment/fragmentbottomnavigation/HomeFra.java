package com.typartybuilding.fragment.fragmentbottomnavigation;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeAct;
import com.typartybuilding.activity.choiceness.SearchActivity;
import com.typartybuilding.activity.second.PlayVideoAct;
import com.typartybuilding.activity.second.TextDetailAct;
import com.typartybuilding.activity.second.homepager.DynamicAct;
import com.typartybuilding.activity.second.homepager.SysMessageAct;
import com.typartybuilding.adapter.DynamicAdapter;
import com.typartybuilding.adapter.RecommentAdapter;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.HomeDynamicBean;
import com.typartybuilding.bean.HomeFraBannerBean;
import com.typartybuilding.bean.HomeRecommentBean;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.loader.HomePagerLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.utils.UserUtils;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.WidgetActivityView;
import com.typartybuilding.view.WidgetViewPagerAdapter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-03
 * @describe
 */
public class HomeFra extends BaseFra {

    @BindView(R.id.home_dynamic_rv)
    RecyclerView mDynamicRv;
    @BindView(R.id.home_recommend_rv)
    RecyclerView mRecommentRv;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;    //下拉刷新，上拉加载

    @BindView(R.id.home_banner)
    WidgetActivityView widgetActivityView;
    @BindView(R.id.home_fra_title1)
    TextView mTitleView1;
    @BindView(R.id.learn_dynamic_tv)
    TextView mDynamicTv;
    @BindView(R.id.learn_recommend_tv)
    TextView mRecommendTv;


    DynamicAdapter dynamicAdapter;
    RecommentAdapter recommentAdapter;
    HomePagerLoader homePagerLoader;

    private int mPageNo = 1;
    private int mPageCount;
    @Override
    public void initData() {
//        mTitleView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/SourceHanSansCN-Medium.otf"));
//        mTitleView1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/SourceHanSansCN-Medium.otf"));
//        mDynamicTv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/SourceHanSansCN-Heavy.otf"));
//        mRecommendTv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/SourceHanSansCN-Medium.otf"));
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageNo = 1;
                loadingData();
            }
        });
//        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mPageNo <= mPageCount) {
                    getHomeRecomment(mPageNo);
                } else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
        homePagerLoader = new HomePagerLoader();
        mDynamicRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecommentRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        dynamicAdapter = new DynamicAdapter();
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
                                .navigation(getActivity());
            }
        });
        mDynamicRv.setAdapter(dynamicAdapter);


        recommentAdapter = new RecommentAdapter();
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
                            .navigation(getActivity());
                }

            }
        });
        mRecommentRv.setAdapter(recommentAdapter);
//        widgetActivityView.
    }


    @Override
    public void loadingData() {
        super.loadingData();
        requestBanner();
        getHomeDynamic();
        getHomeRecomment(mPageNo);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()&&dynamicAdapter!=null
                &&(dynamicAdapter.getData()==null||dynamicAdapter.getData().size()==0)){
            loadingData();
        }
    }

    @OnClick({R.id.home_frg_search,R.id.home_frg_msg,R.id.learn_dynamic_all,R.id.learn_recommend_all})
    void Onclick(View view) {
        switch (view.getId()){
            case R.id.home_frg_search:
                Intent intentAc = new Intent(getActivity(), SearchActivity.class);
                startActivity(intentAc);
                break;
            case R.id.home_frg_msg:
                if (UserUtils.getIns().isTourist()){
                    MyApplication.remindVisitor(getActivity());
                }else {
                    ARouter.getInstance().build(SysMessageAct.PATH).navigation(getActivity());
                }
                break;
            case R.id.learn_dynamic_all:
                ARouter.getInstance().build(DynamicAct.PATH).navigation(getActivity());
                break;
//            case R.id.learn_recommend_all:
//                ARouter.getInstance().build(RecommentAct.PATH).navigation(getActivity());
//                break;
        }
    }


    private void requestBanner() {

        homePagerLoader.getBanner(UserUtils.getIns().getToken()).subscribe(
                new RequestCallback<HomeFraBannerBean>() {
                    @Override
                    public void onSuccess(HomeFraBannerBean homeFraBannerBean) {
                        if (homeFraBannerBean.getArticleBanner() != null
                                && homeFraBannerBean.getArticleBanner().size() > 0) {
                            widgetActivityView.initAdapter(homeFraBannerBean.getArticleBanner(), true, new WidgetViewPagerAdapter.OnClickListen() {
                                @Override
                                public void onClick(int position) {
                                    ARouter.getInstance().build(TextDetailAct.PATH)
                                            .withString(TextDetailAct.URL, homeFraBannerBean.getArticleBanner().get(position).articleDetailUrl)
                                            .withInt(TextDetailAct.ARTICLE_TYPE, 1)
                                            .withInt(TextDetailAct.ARTICLE_ID, homeFraBannerBean.getArticleBanner().get(position).articleId)
                                            .withInt(TextDetailAct.URL_TYPE, 1)
                                            .navigation(getActivity());
                                }
                            });
                        }
                    }

                    @Override
                    public void onFail(Throwable e) {

                    }

                }
        );
    }

    private void getHomeDynamic() {
        homePagerLoader.getHomeDynamic(3).subscribe(new RequestCallback<HomeDynamicBean>() {
            @Override
            public void onSuccess(HomeDynamicBean homeDynamicBean) {
                dynamicAdapter.setNewData(homeDynamicBean.getRows());
            }

            @Override
            public void onFail(Throwable e) {
                Log.e("", "");
            }
        });
    }


    private void getHomeRecomment(int pageNo) {
        homePagerLoader.getHomeRecomment(pageNo).subscribe(new RequestCallback<HomeRecommentBean>() {
            @Override
            public void onSuccess(HomeRecommentBean homeDynamicBean) {
                mPageCount = homeDynamicBean.getPageCount();
                if (pageNo == 1){
                    refreshLayout.finishRefresh();
                    recommentAdapter.setNewData(homeDynamicBean.getRows());
                }else {
                    recommentAdapter.addData(homeDynamicBean.getRows());
                    refreshLayout.finishLoadMore();
                }
                mPageNo++;

            }

            @Override
            public void onFail(Throwable e) {
                Log.e("", "");
                refreshLayout.finishLoadMore();
                refreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_fra_home;
    }
}
