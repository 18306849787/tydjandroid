package com.typartybuilding.activity.learntime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
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
import com.typartybuilding.gsondata.ArticleVideoData;
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

public class EduFilmActivity extends RedTitleBaseActivity {

    private String TAG = "HotVideoActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;             //标题

   /* @BindView(R.id.progress_bar)
    ProgressBar progressBar;*/

    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int articleLabel;
    private String articleLabelName;

    private int pageNo = 1;    //分页-页码。不传则默认1
    private int pageSize = 20;  //分页-每页条数。不传则默认10
    private int pageCount ;     //新闻总共多少页


    private List<ArticleBanner> videoDataList = new ArrayList<>();
    private HotVideoAcAdapter adapter;

    private boolean isDestory;     //是否已经销毁
    private boolean isRefresh;     //是否 进行 下拉刷新


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edufilm);
        ButterKnife.bind(this);
        //获取上一个 页面 传递的数据
        Intent intent = getIntent();
        articleLabel = intent.getIntExtra("articleLabel",-1);
        articleLabelName = intent.getStringExtra("articleLabelName");
        //设置标题
        textTitle.setText(articleLabelName);
        initRecyclerView();
        isDestory = false;
        isRefresh = false;
        //获取视频数据
        getArticleVideo3();
        //显示进度条
        //progressBar.setVisibility(View.VISIBLE);
        setRefreshLayout();
    }

    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                getArticleVideo3();
                //textNoMore.setVisibility(View.GONE);
                //refreshLayout.setEnableLoadMore(true);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount) {
                    getArticleVideo3();
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                    //textNoMore.setVisibility(View.VISIBLE);
                    //refreshLayout.setEnableLoadMore(false);
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

    }

    private void initData(ArticleVideoData articleVideoData) {
        if (!isDestory) {

            if (isRefresh){
                if (videoDataList.size() > 0){
                    videoDataList.clear();
                }
                isRefresh = false;
                adapter.notifyDataSetChanged();
            }

            int startItem = videoDataList.size();
            if (articleVideoData.data.rows != null) {
                Log.i(TAG, "loadData: pageCount : " + pageCount);
                int size = articleVideoData.data.rows.length;
                for (int i = 0; i < size; i++) {
                    videoDataList.add(articleVideoData.data.rows[i]);
                }
                int itemCount = videoDataList.size();
                adapter.notifyItemRangeChanged(startItem,itemCount);
                //adapter.notifyDataSetChanged();

            }
            //progressBar.setVisibility(View.GONE);

        }

    }


    /**
     * 获取视频数据
     */
    private void getArticleVideo3(){
        //articleType   3：党建基层，5：学习领袖，6：党员教育片，7：时代先锋，8：直播
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getArticleVideo3(userId,pageNo,pageSize,6,2,token,articleLabel)
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

                        if (code == 0){
                            initData(articleVideoData);
                            pageNo++;
                            //获取总页数
                            pageCount = articleVideoData.data.pageCount;

                            //关闭下拉刷新
                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                                refreshLayout.finishLoadMore(true);
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(articleVideoData.message);

                            //关闭下拉刷新
                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                                refreshLayout.finishLoadMore(true);
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(EduFilmActivity.this,articleVideoData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //关闭下拉刷新
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(true);
                            refreshLayout.finishLoadMore(true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }





    private void getMoreHotVideo(){

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
                            //initData(moreHotVideoData);
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
                            RetrofitUtil.tokenLose(EduFilmActivity.this,moreHotVideoData.message);
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


                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



}
