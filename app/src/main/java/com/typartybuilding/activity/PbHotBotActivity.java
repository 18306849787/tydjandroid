package com.typartybuilding.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.myRelatedActivity.MyCollectActivity;

import com.typartybuilding.adapter.recyclerViewAdapter.MixtureDataAdapter;

import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralMixtureData;
import com.typartybuilding.gsondata.MixtureData;
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

public class PbHotBotActivity extends RedTitleBaseActivity {

    private String TAG = "PbHotBotActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;

    private MixtureDataAdapter adapter;
    private List<MixtureData> dataList = new ArrayList<>();

    private int pageNo = 1;
    private int pageSize = 20;
    private int pageCount;

    private String hotWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pb_hot_bot);
        ButterKnife.bind(this);
        textTitle.setText("党建热搜");
        //获取上一个activity传递的 热词
        Intent intent = getIntent();
        hotWord = intent.getStringExtra("hotWord");

        initRecyclerView();
        getSearchData();
        setRefreshLayout();
    }


    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MixtureDataAdapter(dataList,this);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);
    }

    private void setRefreshLayout(){
        //禁止下拉刷新
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount ){
                    getSearchData();
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
    }

    private void initData(GeneralMixtureData generalMixtureData){
        int startItem = dataList.size();
        MixtureData data[] = generalMixtureData.data.rows;
        if (data != null){
            for (int i = 0; i < data.length; i++) {
                dataList.add(data[i]);
            }
            int itemCount = dataList.size();
            adapter.notifyItemRangeInserted(startItem,itemCount);
            //adapter.notifyDataSetChanged();
        }
    }

    private void getSearchData(){

        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getSearchData(hotWord,token,pageNo,pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralMixtureData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralMixtureData generalMixtureData) {
                        int code = Integer.valueOf(generalMixtureData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            pageCount = generalMixtureData.data.pageCount;
                            pageNo++;
                            initData(generalMixtureData);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalMixtureData.message);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(PbHotBotActivity.this,generalMixtureData.message);
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
