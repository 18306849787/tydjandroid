package com.typartybuilding.fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.typartybuilding.R;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fgChoiceness.FragmentCurrentNews;
import com.typartybuilding.fragment.fgChoiceness.FragmentHotVideo;
import com.typartybuilding.fragment.fgChoiceness.FragmentMicVideo;
import com.typartybuilding.fragment.fgChoiceness.FragmentPicture;
import com.typartybuilding.fragment.fragmentbottomnavigation.FragmentHome;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 *  精选 页面
 */
public class HomeFragmentChoiceness extends BaseFragmentHome {

    private String TAG = "HomeFragmentChoiceness";

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.scrollview_home_fg_ch)
    public NestedScrollView scrollView;

    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;      //页面的父布局
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    public FragmentHome parentFg;
    private FragmentPicture fgPicture;
    private FragmentHotVideo fgHotVideo;
    private FragmentMicVideo fgMicVideo;
    private FragmentCurrentNews fgCurrentNews;

    //热门视频数据，子fragment使用
    public List<ArticleBanner> videoList = new ArrayList<>();

    private boolean isDestroy;

    @Override
    protected void initViews(Bundle savedInstanceState) {

        //获取父fragment
        parentFg = (FragmentHome) getParentFragment();
        //获取子fragment
        FragmentManager manager = getChildFragmentManager();
        fgPicture = (FragmentPicture)manager.findFragmentById(R.id.fragment_pic);
        fgHotVideo = (FragmentHotVideo)manager.findFragmentById(R.id.fragment_hot_video);
        fgMicVideo = (FragmentMicVideo)manager.findFragmentById(R.id.fragment_mic_video);
        fgCurrentNews = (FragmentCurrentNews)manager.findFragmentById(R.id.fragment_current_news);
        //请求数据前，页面不可见，有进度条
        linearLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        //请求数据
        isDestroy = false;

        getChoicenessData();
        //设置下拉刷新
        setRefreshLayout();

    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_choiceness;
    }

    @Override
    protected void lazyLoad() {
        //getChoicenessData();
    }

    @Override
    protected void stopLoad() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
    }



    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新轮播图，热门视频，微视
                getChoicenessData();
                //刷新时事新闻
                fgCurrentNews.refreshCurrentNews();
                progressBar.setVisibility(View.GONE);

            }
        });
    }


    private void initData(ChoicenessData choicenessData){
        if (!isDestroy) {
            //获取轮播图 数据,并刷新页面
            getArticleBanner(choicenessData);
            //获取热门视频 数据，并刷新页面
            getArticleVideo(choicenessData);
            //获取精彩微视 数据， 并刷新页面
            getmicroVision(choicenessData);

            //页面刷新完毕
            linearLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            //滚动到顶部
            scrollView.scrollTo(0, 0);

        }

    }

    private void getChoicenessData(){
        Log.i(TAG, "getChoicenessData: ");

        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofit = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofit.getChoicenessData(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChoicenessData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ChoicenessData choicenessData) {
                        int code = Integer.valueOf(choicenessData.code);
                        Log.i(TAG, "onNext: ");
                        if (code == 0) {

                            initData(choicenessData);
                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(choicenessData.message);
                            //关闭下拉刷新
                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),choicenessData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                        RetrofitUtil.requestError();
                        //关闭下拉刷新
                        if (refreshLayout != null) {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    //获取轮播图 数据,并刷新页面
    private void getArticleBanner(ChoicenessData choicenessData){

        int bannerSize = choicenessData.data.articleBanner.length;
        if (bannerSize > 0) {
            List<ArticleBanner> bannerList = new ArrayList<>();
            for (int i = 0; i < bannerSize; i++) {
                bannerList.add(choicenessData.data.articleBanner[i]);
            }
            fgPicture.loadData(bannerList);
        }
    }

    //获取热门视频 数据，并刷新页面
    private void getArticleVideo(ChoicenessData choicenessData) {
        int size = choicenessData.data.articleVideo.length;
        if (size > 0){
            if (videoList.size() != 0) {
                videoList.clear();
            }
            for (int i = 0; i < size; i++) {
                videoList.add(choicenessData.data.articleVideo[i]);
            }
            Log.i(TAG, "getArticleVideo: videoList size : " + videoList.size());
            fgHotVideo.loadData(videoList);
        }
    }

    //获取精彩微视 数据， 并刷新页面
    private void getmicroVision(ChoicenessData choicenessData){
        int size = choicenessData.data.microVision.length;
        if (size > 0) {
            if (fgMicVideo.visionList.size() > 0) {
                fgMicVideo.visionList.clear();
            }
            List<ChoicenessData.MicroVision> visionList = fgMicVideo.visionList;
            for (int i = 0; i < size; i++) {
                visionList.add(choicenessData.data.microVision[i]);
            }
            fgMicVideo.loadData();
        }

    }


}
