package com.typartybuilding.activity.basicparty;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.FgBasicPartyItemAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.PbvHotBotAdapterNew;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.ArticleVideoData;
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

public class BasicPartyActivity extends RedTitleBaseActivity {

    private String TAG = "BasicPartyActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List<ArticleBanner> dataList = new ArrayList<>();
    private FgBasicPartyItemAdapter adapter;

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 10;
    private int pageCount;

    private boolean isRefresh;     //是否 在 下拉刷新
    private boolean isLoadMore;    //是否 在 上拉加载更多

    private String organName;     //组织机构名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_party);
        ButterKnife.bind(this);
        //获取上一个页面传递数据
        Intent intent = getIntent();
        organName = intent.getStringExtra("organName");
        //设置标题栏
        textTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        textTitle.setText(organName);

        initRecyclerView();
        setRefreshLayout();
        //获取数据
        getArticleVideo2();
    }


    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FgBasicPartyItemAdapter(dataList,this);
        recyclerView.setFocusable(false);
        recyclerView.setAdapter(adapter);
    }

    private void setRefreshLayout(){

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                getArticleVideo2();
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount){
                    getArticleVideo2();
                    isLoadMore = true;
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);

                }
            }
        });
    }

    private void initData(ArticleVideoData articleVideoData){

        if (isRefresh){
            if (dataList.size() > 0){
                dataList.clear();
            }
            isRefresh = false;
            adapter.notifyDataSetChanged();
        }

        int startItem = dataList.size();
        ArticleBanner banner[] = articleVideoData.data.rows;
        Log.i(TAG, "initData: banner[] : " + banner);
        if (banner != null){
            Log.i(TAG, "initData: size : " + banner.length);
            for (int i = 0; i < banner.length; i++){
                dataList.add(banner[i]);
                adapter.notifyItemInserted(startItem + i);
            }
            //adapter.notifyDataSetChanged();
        }

    }


    /**
     *  获取 资讯文章数据， articleType 3：党建基层，5：学习领袖，6：党员教育片，7：时代先锋，8：直播
     *                     urlType     1：图片，2：视频
     */
    private void getArticleVideo2(){

        Log.i(TAG, "getArticleVideo2: organName : " + organName);
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getArticleVideo2(userId,pageNo,pageSize,3,null,token,organName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleVideoData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArticleVideoData articleVideoData) {
                        Log.i(TAG, "onNext: ");
                        int code = Integer.valueOf(articleVideoData.code);
                        Log.i(TAG, "onNext: code : " + code);

                        if (code == 0){

                            pageNo++;
                            pageCount = articleVideoData.data.pageCount;
                            //关闭下拉刷新
                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            initData(articleVideoData);

                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }


                        }else if (code == -1){
                            Log.i(TAG, "onNext: -1");
                            RetrofitUtil.errorMsg(articleVideoData.message);
                            //关闭下拉刷新
                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(BasicPartyActivity.this,articleVideoData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //关闭下拉刷新
                        if (isRefresh){
                            refreshLayout.finishRefresh(true);
                        }
                        if (isLoadMore){
                            refreshLayout.finishLoadMore(true);
                            isLoadMore = false;
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
