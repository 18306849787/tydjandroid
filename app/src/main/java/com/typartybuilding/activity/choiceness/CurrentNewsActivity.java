package com.typartybuilding.activity.choiceness;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.CurrentNewsAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.CurrentNewsData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CurrentNewsActivity extends RedTitleBaseActivity {

    private String TAG = "CurrentNewsActivity ";

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.nestedScrollView)
    NestedScrollView scrollView;

    @BindView(R.id.textView_title)
    TextView textTitle;            //标题

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int pageNo = 1;    //分页-页码。不传则默认1
    private int pageSize = 20;  //分页-每页条数。不传则默认10
    private int pageCount ;     //新闻总共多少页
    private int loadingState;

    private int lastVisiblePosition = 0;

    private List<ArticleBanner> bannerList = new ArrayList<>();
    private CurrentNewsAdapter adapter;

    private boolean isDestory;     //是否已经销毁
    private boolean isRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_news);
        ButterKnife.bind(this);
        textTitle.setText(R.string.current_news_ac_str1);
        initRecyclerView();

        isDestory = false;
        isRefresh = false;
        //获取新闻数据
        getCurrentNews();
        setRefreshLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestory = true;
    }

    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                isRefresh = true;
                pageNo = 1;
                //lastVisiblePosition = 0;
                getCurrentNews();
            }
        });
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CurrentNewsAdapter(bannerList,this);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //int lastVisiblePosition = 0;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE){//滑动停止
                    if(lastVisiblePosition + 1 == adapter.getItemCount()) {//滑动到最后一个item
                        if (pageNo <= pageCount && loadingState==0){
                            loadingState = 1;
                            adapter.setTypeItemFooterStart();
                            getCurrentNews();
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
        });

    }

    private void initData(com.typartybuilding.gsondata.choiceness.CurrentNewsData currentNewsData){

        if (!isDestory) {

            if (isRefresh){
                if (bannerList.size() > 0){
                    bannerList.clear();
                }
                isRefresh = false;
                Log.i(TAG, "initData: 清空集合数据");
            }

            if (currentNewsData.data.rows != null) {
                int size = currentNewsData.data.rows.length;
                for (int i = 0; i < size; i++) {
                    bannerList.add(currentNewsData.data.rows[i]);
                }
                adapter.notifyDataSetChanged();
            }
            loadingState = 0;
        }


    }

    private void getCurrentNews(){
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getCurrentNews(userId, pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CurrentNewsData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CurrentNewsData currentNewsData) {
                        int code = Integer.valueOf(currentNewsData.code);
                        if (code == 0){
                            initData(currentNewsData);
                            //获取总页数
                            pageCount = currentNewsData.data.pageCount;
                            pageNo++;
                            if (pageNo > pageCount){
                                if (adapter != null) {
                                    adapter.setTypeItemFooter();
                                }
                            }
                            //关闭下拉刷新
                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(currentNewsData.message);
                            //关闭下拉刷新
                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(CurrentNewsActivity.this,currentNewsData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
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

                    }
                });

    }






}
