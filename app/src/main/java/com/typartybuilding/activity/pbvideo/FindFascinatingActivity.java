package com.typartybuilding.activity.pbvideo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.FindFascinatingAdapterNew;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.pbmicrovideo.FindFascinatingData;
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

public class FindFascinatingActivity extends WhiteTitleBaseActivity {

    private String TAG = "FindFascinatingActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FindFascinatingAdapterNew adapter;
    private List<MicroVideo> dataList = new ArrayList<>();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 30;
    private int pageCount;

    private boolean isRefresh;      //是否 在 下拉刷新
    private boolean isLoadMore;     //是否在 上拉加载更多

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_fascinating);
        ButterKnife.bind(this);
        textTitle.setText("发现精彩");

        isRefresh = false;
        isLoadMore = false;

        initRecyclerView();
        getFindFascinatingData();
        //设置下拉刷新
        setRefreshLayout();
    }

    private void initRecyclerView(){
        //瀑布流
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL){

        };
        //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FindFascinatingAdapterNew(dataList,this);
        //adapter.setHasStableIds(true);
        //1 ,表示在党建微视页面   2 ，表示在发现精彩的 activity 页面
        adapter.setFlag(2);
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);
    }

    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                getFindFascinatingData();
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount){
                    getFindFascinatingData();
                    isLoadMore = true;
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
    }

    private void initData(FindFascinatingData findFascinatingData){

        if (isRefresh){
            if (dataList.size() > 0){
                dataList.clear();
            }
            isRefresh = false;
            if (adapter != null){
                adapter.notifyDataSetChanged();
            }
        }

        int startItem = dataList.size();

        MicroVideo microVideo[] = findFascinatingData.data.rows;
        if (microVideo != null){

            for (int i = 0; i < microVideo.length; i++){
                dataList.add(microVideo[i]);
                //Log.i(TAG, "initData: 浏览次数 i  " + i + ": " + microVideo[i].visionBrowseTimes );
                adapter.notifyItemInserted(startItem + i);
            }
            int size = dataList.size();
            //adapter.notifyItemRangeChanged(startItem,size);
            //adapter.notifyDataSetChanged();
        }


    }

    private void getFindFascinatingData(){
        Log.i(TAG, "getFindFascinatingData: ");
        Log.i(TAG, "getFindFascinatingData: pageNo : " + pageNo);
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getFindFascinatingData(token,pageNo,pageSize,userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FindFascinatingData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FindFascinatingData findFascinatingData) {
                        int code = Integer.valueOf(findFascinatingData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            pageCount = findFascinatingData.data.pageCount;
                            pageNo++;
                            Log.i(TAG, "onNext: pageCount : " + pageCount);
                            Log.i(TAG, "onNext: pageNo : " + pageNo);
                            //关闭下拉刷新
                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }

                            initData(findFascinatingData);

                            //关闭上拉加载更多
                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(findFascinatingData.message);

                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(FindFascinatingActivity.this,findFascinatingData.message);
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
                        //关闭上拉加载更多
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
