package com.typartybuilding.activity.pbvideo;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.PbvPopularityListAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.PbvPopularityListAdapterNew;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.PbVideoBaseActivity;
import com.typartybuilding.gsondata.pbmicrovideo.PopularityListData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class PopularityListActivity extends PbVideoBaseActivity {

    private String TAG = "PopularityListActivity";

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.textView_no_more)
    TextView textNoMore;             //没有更多的 文字提示

    private PbvPopularityListAdapterNew adapter;
    private List<PopularityListData.PopularityData> dataList = new ArrayList<>();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popularity_list);
        ButterKnife.bind(this);

        initRecyclerView();
        //获取人气榜数据
        getPopularityListData();
        //设置下拉刷新
        setRefreshLayout();
    }

    private void setRefreshLayout(){
        //禁止 上拉加载更多
        refreshLayout.setEnableLoadMore(false);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getPopularityListData();
            }
        });

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PbvPopularityListAdapterNew(dataList,this);

        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);
    }

    private void initData(PopularityListData popularityListData){

        if (dataList.size() > 0){
            dataList.clear();
        }

        PopularityListData.PopularityData [] popularityData = popularityListData.data;
        if (popularityData != null){
            for (int i = 0; i < popularityData.length; i++){
                dataList.add(popularityData[i]);
            }
            adapter.notifyDataSetChanged();
        }

    }

    private void getPopularityListData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getPopularityListData(userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PopularityListData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PopularityListData popularityListData) {
                        int code = Integer.valueOf(popularityListData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            initData(popularityListData);
                            //关闭下拉刷新
                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                            }
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(popularityListData.message);
                            //关闭下拉刷新
                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(PopularityListActivity.this,popularityListData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //关闭下拉刷新
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.imageView_back)
    public void onClickBack(){
        finish();
    }

}
