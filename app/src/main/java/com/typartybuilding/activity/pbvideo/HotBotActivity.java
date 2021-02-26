package com.typartybuilding.activity.pbvideo;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.typartybuilding.adapter.recyclerViewAdapter.PbvHotBotAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.PbvHotBotAdapterNew;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.PbVideoBaseActivity;
import com.typartybuilding.gsondata.pbmicrovideo.HotWordData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

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

public class HotBotActivity extends PbVideoBaseActivity {

    private String TAG = "HotBotActivity";

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private PbvHotBotAdapterNew adapter;
    private List<HotWordData.HotWord> dataList = new ArrayList<>();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 50;
    private int pageCount;

    private boolean isRefresh;     //是否 在 下拉刷新
    private boolean isLoadMore;    //是否 在 上拉加载更多

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_bot);
        ButterKnife.bind(this);

        initRecyclerView();
        setRefreshLayout();
        //获取热搜数据
        getHotWordData();

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PbvHotBotAdapterNew(dataList,this);
        recyclerView.setFocusable(false);
        recyclerView.setAdapter(adapter);

    }

    private void setRefreshLayout(){

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                getHotWordData();
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount){
                    getHotWordData();
                    isLoadMore = true;
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
    }


    private void initData(HotWordData hotWordData){

        if (isRefresh){
            if (dataList.size() > 0){
                dataList.clear();
            }
            isRefresh = false;
            adapter.notifyDataSetChanged();
        }

        int startItem = dataList.size();
        HotWordData.HotWord hotWords[] = hotWordData.data.rows;
        if (hotWords != null){
            for (int i = 0; i < hotWords.length; i++){
                dataList.add(hotWords[i]);
                adapter.notifyItemInserted(startItem + i);
            }
            //adapter.notifyDataSetChanged();
        }

    }

    private void getHotWordData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getHotWordData(pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HotWordData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HotWordData hotWordData) {
                        int code = Integer.valueOf(hotWordData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            pageCount = hotWordData.data.pageCount;
                            pageNo++;

                            //关闭下拉刷新
                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            initData(hotWordData);

                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(hotWordData.message);
                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == 10){
                            Log.i(TAG, "onNext: message : " + hotWordData.message);
                            RetrofitUtil.tokenLose(HotBotActivity.this,hotWordData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e ; " + e);
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


    @OnClick(R.id.imageView_back)
    public void onClickBack(){
        finish();
    }
}
