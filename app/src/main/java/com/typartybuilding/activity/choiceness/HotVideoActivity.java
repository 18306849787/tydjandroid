package com.typartybuilding.activity.choiceness;

import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.HotVideoAcAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.MoreHotVideoData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class HotVideoActivity extends RedTitleBaseActivity {

    private String TAG = "HotVideoActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;             //标题
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.scrollview_hot_video_ac)
    NestedScrollView scrollView;

    @BindView(R.id.recyclerView_hot_video_ac)
    RecyclerView recyclerView;


    private int pageNo = 1;    //分页-页码。不传则默认1
    private int pageSize = 20;  //分页-每页条数。不传则默认10
    private int pageCount ;     //新闻总共多少页
    private int loadingState;

    private List<ArticleBanner> videoDataList = new ArrayList<>();
    private HotVideoAcAdapter adapter;

    private boolean isDestory;     //是否已经销毁
    private boolean isRefresh;     //是否 进行 下拉刷新


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_video);
        ButterKnife.bind(this);
        //设置标题
        textTitle.setText(R.string.hot_video_ac_str1);
        initRecyclerView();
        isDestory = false;
        isRefresh = false;
        //获取视频数据
        getMoreHotVideo();
        //显示进度条
        progressBar.setVisibility(View.VISIBLE);
        setRefreshLayout();
    }

    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                getMoreHotVideo();
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount) {
                    getMoreHotVideo();
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HotVideoAcAdapter(videoDataList,this);

        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);

        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisiblePosition = 0;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i(TAG, "onScrollStateChanged: ");
                Log.i(TAG, "onScrollStateChanged: newState : " + newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){//滑动停止

                    if(lastVisiblePosition + 1 == adapter.getItemCount()) {//滑动到最后一个item
                        if (pageNo <= pageCount && loadingState==0){
                            loadingState = 1;
                            getMoreHotVideo();
                            adapter.setTypeItemFooterStart();
                        }else if (pageNo > pageCount){
                            adapter.setTypeItemFooter();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                Log.i(TAG, "onScrolled: lastVisiblePosition : " + lastVisiblePosition);

            }
        });*/

    }

    private void initData(MoreHotVideoData moreHotVideoData) {
        if (!isDestory) {

            if (isRefresh){
                if (videoDataList.size() > 0){
                    videoDataList.clear();
                }
                isRefresh = false;
                Log.i(TAG, "initData: 清空集合数据");
            }

            if (moreHotVideoData.data.rows != null) {
                Log.i(TAG, "loadData: pageCount : " + pageCount);
                int size = moreHotVideoData.data.rows.length;
                for (int i = 0; i < size; i++) {
                    videoDataList.add(moreHotVideoData.data.rows[i]);
                }
                adapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.GONE);
            loadingState = 0;
        }

    }

    private void getMoreHotVideo(){

        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getMoreHotVideo( pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MoreHotVideoData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MoreHotVideoData moreHotVideoData) {
                        int code = Integer.valueOf(moreHotVideoData.code);
                        if (code == 0){
                            initData(moreHotVideoData);
                            pageNo++;
                            //获取总页数
                            pageCount = moreHotVideoData.data.pageCount;
                            if (pageNo > pageCount){
                                if (adapter != null) {
                                    adapter.setTypeItemFooter();
                                }
                            }
                            //关闭下拉刷新
                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                                refreshLayout.finishLoadMore();
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(moreHotVideoData.message);
                            //关闭下拉刷新
                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                                refreshLayout.finishLoadMore();
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(HotVideoActivity.this,moreHotVideoData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //关闭下拉刷新

                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(true);
                            refreshLayout.finishLoadMore();
                        }

                        /*if (refreshLayout != null) {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                        }*/
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }






}
