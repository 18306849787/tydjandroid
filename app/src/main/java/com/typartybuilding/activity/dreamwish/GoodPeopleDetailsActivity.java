package com.typartybuilding.activity.dreamwish;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.GoodPeopleAcAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.TaDreamWishAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.dreamwish.DreamWishData;
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

public class GoodPeopleDetailsActivity extends RedTitleBaseActivity {

    private String TAG = "GoodPeopleDetailsActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.textView_no_more)
    TextView textNoMore;          //没有更多的 文字提示

    private String userName;
    private int userId;

    private List<DreamWishData.WishData> dataList = new ArrayList<>();
    private TaDreamWishAdapter adapter ;

    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 20;
    private int pageCount;

    private boolean isRefresh;     //是否 在 下拉刷新
    private boolean isLoadMore;    //是否 在 上拉加载更多


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_people_details);
        ButterKnife.bind(this);
        //获取上一个页面传递的数据
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userId = intent.getIntExtra("userId",-1);
        //设置标题栏名称
        textTitle.setText(userName);

        initRecyclerView();
        setRefreshLayout();
        //获取心愿数据
        getMyOrTaWishData();

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TaDreamWishAdapter(dataList,this);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line_edufilm));
        recyclerView.addItemDecoration(dividerLine);

        recyclerView.setFocusable(false);
        recyclerView.setAdapter(adapter);

    }

    private void setRefreshLayout(){

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                getMyOrTaWishData();
                //textNoMore.setVisibility(View.GONE);
                //refreshLayout.setEnableLoadMore(true);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount){
                    getMyOrTaWishData();
                    isLoadMore = true;

                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                    //textNoMore.setVisibility(View.VISIBLE);
                    //refreshLayout.setEnableLoadMore(false);
                }
            }
        });
    }

    private void initData(DreamWishData dreamWishData){

        //下拉刷新，需清空数据
        if (isRefresh){
            if (dataList.size() > 0){
                dataList.clear();
            }
            isRefresh = false;
            adapter.notifyDataSetChanged();
        }

        int startItem = dataList.size();
        DreamWishData.WishData [] wishData = dreamWishData.data.rows;
        if (wishData != null){
            for (int i = 0; i < wishData.length; i++){
                dataList.add(wishData[i]);
                adapter.notifyItemInserted(startItem + i);
            }
            //adapter.notifyDataSetChanged();
        }

    }

    private void getMyOrTaWishData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getMyOrTaWishData(pageNo,pageSize,userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DreamWishData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DreamWishData dreamWishData) {
                        int code = Integer.valueOf(dreamWishData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){

                            pageCount = dreamWishData.data.pageCount;
                            pageNo++;
                            //关闭下拉刷新
                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            initData(dreamWishData);

                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(dreamWishData.message);

                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(GoodPeopleDetailsActivity.this,dreamWishData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();

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
