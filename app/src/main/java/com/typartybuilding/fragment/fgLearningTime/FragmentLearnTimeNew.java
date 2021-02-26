package com.typartybuilding.fragment.fgLearningTime;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.IAudioFocusDispatcher;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.LearnTimeAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.LearnTimeAdapterNew;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.HomeFragmentLearningTime;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.ArticleVideoData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ExoplayerUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 学习时刻    适用于  学习新思想，党员教育片，时代先锋
 */
public class FragmentLearnTimeNew extends BaseFragment {

    private String TAG = "FragmentLearnTimeNew";

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;    //下拉刷新，上拉加载
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;     //推荐视频列表

    private int pageCount;    //总的页数
    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private int pageNo = 1;
    private int pageSize = 20;
    private int urlType = 2;      //2 表示视频
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

    private List<ArticleBanner> bannerList = new ArrayList<>();
    private LearnTimeAdapterNew adapter;
    public MyLinearLayoutManager layoutManager;

    private HomeFragmentLearningTime parentFg;

    //private FragmentManager manager;
    private boolean isDestroy ;            //网络请求后，会刷新该页面，刷新时，需判断页面是否销毁
    private boolean isFirstData = true;    //判断标记，获取的第一条数据，用于页面顶端的视频

    private int flag;                      //8,表示直播视频；5 学习新思想，7时代先锋
    public FragmentTopVideo topVideo;           //学习新思想，时代先锋 的置顶视频
    public FragmentTopLiveVideo topLiveVideo;   //直播 的  置顶视频


    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //获取 父Fragment ， HomeFragmentLearningTime
        parentFg = (HomeFragmentLearningTime)getParentFragment();

        initRecyclerView();
        //设置页数,重新加载后，需设为1，集合清零
        pageNo = 1;
        pageSize = 20;
        if (bannerList.size() > 0){
            bannerList.clear();
        }
        //获取视频数据
        isDestroy = false;
        isFirstData = true;
        //设置下拉刷新
        setRefreshLayout();
        //请求数据
        getVideoData();
    }


    @Override
    public int getLayoutId() {
        return R.layout.fg_learning_time_fragment_learn_time_new;
    }


    @Override
    public void onResume() {
        super.onResume();
        //保持屏幕常亮
        /*try {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }catch (NullPointerException e){
            Log.i(TAG, "onPause: e : " + e);
            e.printStackTrace();
        }*/

        if (topVideo != null){
            topVideo.onResume();
        }
        if (topLiveVideo != null){
            topLiveVideo.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //取消屏幕常亮
       /* try {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }catch (NullPointerException e){
            Log.i(TAG, "onPause: e : " + e);
            e.printStackTrace();
        }*/

        if (topVideo != null){
            topVideo.onPause();
        }
        if (topLiveVideo != null){
            topLiveVideo.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
    }

    public void addFlagsScreenOn(){
        try {
            Activity activity = getActivity();
            if (activity != null) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void clearFlagsScreenOn(){
        try {
            Activity activity = getActivity();
            if (activity != null) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void loadFragment(int id,ArticleBanner banner){
        Log.i(TAG, "loadFragment: id : " + id);
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Log.i(TAG, "loadFragment: flag : " + flag);
        //直播 的 置顶视频
        if (flag == 8){
            topLiveVideo = new FragmentTopLiveVideo();
            topLiveVideo.setBanner(banner);
            topLiveVideo.setParentFg(parentFg);
            transaction.replace(id,topLiveVideo);
            transaction.commit();

        //  5学习新思想 和 7时代先锋 的  置顶视频
        }else if (flag == 5 || flag == 7){
            topVideo = new FragmentTopVideo();
            topVideo.setBanner(banner);
            topVideo.setParentFg(parentFg);
            transaction.replace(id,topVideo);
            transaction.commit();
        }
    }

    private void initRecyclerView(){
        layoutManager = new MyLinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LearnTimeAdapterNew(bannerList,this);
        //8,表示直播视频；5 学习新思想，7 时代先锋
        adapter.setFlag(flag);
        //adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);
    }

    private void setRefreshLayout(){
        //禁止下拉刷新
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount ){
                    getVideoData();
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
    }


    /**
     * 处理 后台获取的数据
     * @param articleVideoData
     */
    private void loadData(ArticleVideoData articleVideoData){
        if (!isDestroy) {
            if (articleVideoData.data.rows != null) {
                int size = articleVideoData.data.rows.length;
                Log.i(TAG, "loadData: size : " + size);
                int startItem = 0;
                if (!isFirstData){
                    startItem = bannerList.size()/2;
                }
                isFirstData = false;
                //pageSize = 20;

                for (int i = 0; i < size; i++) {
                    bannerList.add(articleVideoData.data.rows[i]);
                    //adapter.notifyItemInserted(startItem + i);
                }
                int itemCount = 0;
                int count = bannerList.size();
                itemCount = count/2 + 1;

                //adapter.notifyDataSetChanged();
                adapter.notifyItemRangeInserted(startItem,itemCount);
            }
        }
    }

    /**
     * 获取视频数据
     */
    private void getVideoData() {
        //flag : 5,学习新思想(学习领袖)， 7，时代先锋 ； 8 直播
        //articleType   3：党建基层，5：学习领袖，6：党员教育片，7：时代先锋，8：直播
        int articleType = flag;

        Log.i(TAG, "getVideoData: flag : " + flag);
        Log.i(TAG, "getVideoData: articleType : " + articleType);
        Log.i(TAG, "getVideoData: ");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getArticleVideo(userId, pageNo, pageSize, articleType, urlType, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleVideoData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArticleVideoData articleVideoData) {
                        int code = Integer.valueOf(articleVideoData.code);
                        Log.i(TAG, "onNext: code : " + code);

                        if (code == 0) {
                            loadData(articleVideoData);
                            pageNo++;
                            pageCount = articleVideoData.data.pageCount;
                            Log.i(TAG, "onNext: rows : " + articleVideoData.data.rows);
                            Log.i(TAG, "onNext: pageNo : " + pageNo);
                            Log.i(TAG, "onNext: pageCount : " + pageCount);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }

                        } else if (code == -1) {
                            RetrofitUtil.errorMsg(articleVideoData.message);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }

                        } else if (code == 10) {
                            RetrofitUtil.tokenLose(getActivity(), articleVideoData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //关闭上拉加载更多
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
