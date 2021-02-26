package com.typartybuilding.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.ChoicenessAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.FindFascinatingAdapterNew;
import com.typartybuilding.adapter.recyclerViewAdapter.PbMicroVideoAdapter;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentFindFascinatingNew;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentHotBotNew;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentPopularityListNew;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.pbmicrovideo.FindFascinatingData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 党建微视 页面
 */
public class HomeFragmentPartyBuildingVideoNew1 extends BaseFragmentHome {

    private String TAG = "HomeFragmentPartyBuildingVideoNew1";

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FragmentHotBotNew fgHotBot;
    private FragmentPopularityListNew fgPopularity;
    //private FragmentManager fragmentManager;

    private PbMicroVideoAdapter adapter;

    private List<MicroVideo> dataList = new ArrayList<>();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 20;
    private int pageCount;

    private boolean isDestroy;
    private boolean isRefresh;      //是否 在 下拉刷新
    private boolean isLoadMore;     //是否在 上拉加载更多
    private boolean isFirst;        //是否 第一次获取数据

    private boolean isCreate;       //是否是第一次创建，第一不刷新数据，播放微视后，返回需刷新数据

    @Override
    protected void initViews(Bundle savedInstanceState) {
        fgHotBot = new FragmentHotBotNew();
        fgPopularity = new FragmentPopularityListNew();
        //获取发现精彩数据
        isDestroy = false;
        isRefresh = false;
        isLoadMore = false;
        isFirst = true;
        isCreate = true;

        initRecyclerView();
        getFindFascinatingData();
        setRefreshLayout();
    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_party_building_video_new1;
    }

    @Override
    protected void lazyLoad() {
       /* fgHotBot = new FragmentHotBotNew();
        fgPopularity = new FragmentPopularityListNew();
        //获取发现精彩数据
        isDestroy = false;
        isRefresh = false;
        isLoadMore = false;
        isFirst = true;
        isCreate = true;

        initRecyclerView();
        getFindFascinatingData();
        setRefreshLayout();*/
    }

    @Override
    protected void stopLoad() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isCreate){
            if (adapter != null){
                adapter.refreshData();
            }
        }
        isCreate = false;
    }

    private void initRecyclerView(){
        //瀑布流
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL){
        };
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PbMicroVideoAdapter(dataList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);
    }

    public void loadFragment(int i,int id){
        try {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (i == 0){
                transaction.replace(id,fgHotBot);
            }else if (i == 1){
                transaction.replace(id,fgPopularity);
            }
            transaction.commit();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                fgHotBot.refreshData();
                fgPopularity.refreshData();
                //刷新微视
                isRefresh = true;
                isFirst = true;
                pageNo = 1;
                getFindFascinatingData();
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                Log.i(TAG, "onLoadMore: ");
                if (pageNo <= pageCount ){
                    isLoadMore = true;
                    getFindFascinatingData();
                }else {
                    refreshLayout.finishLoadMore(500);
                    Utils.noMore();
                }
            }
        });
    }

    //关闭下拉刷新
    private void closeRefresh(){
        if (refreshLayout != null){
            refreshLayout.finishRefresh(true);
        }
    }

    //关闭上拉加载更多
    private void closeLoadMore(){
        if (refreshLayout != null){
            refreshLayout.finishLoadMore(true);
        }
    }

    private void initData(FindFascinatingData findFascinatingData){
        Log.i(TAG, "initData: ");
        if (!isDestroy){
            if (isRefresh){
                if (dataList.size() > 0){
                    dataList.clear();
                }
                isRefresh = false;
                adapter.notifyDataSetChanged();
            }
            int startItem = dataList.size();

            MicroVideo microVideo[] = findFascinatingData.data.rows;
            if (microVideo != null){
                //第一次加载数据或刷新数据，使用全局刷新，加载更多，使用局部刷新
                if (isFirst){
                    for (int i = 0; i < microVideo.length; i++){
                        if (i == 0) {
                            dataList.add(microVideo[i]);
                            dataList.add(microVideo[i]);
                            dataList.add(microVideo[i]);
                            dataList.add(microVideo[i]);
                        }else {
                            dataList.add(microVideo[i]);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    isFirst = false;
                }else {
                    for (int i = 0; i < microVideo.length; i++){
                        dataList.add(microVideo[i]);
                        //adapter.notifyItemInserted(startItem + i);
                    }
                    int itemCount = dataList.size();
                    adapter.notifyItemRangeInserted(startItem,itemCount);
                }

            }
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
                                closeRefresh();
                            }

                            initData(findFascinatingData);

                            Log.i(TAG, "onNext: isLoadMore : " + isLoadMore);
                            //关闭上拉加载更多
                            if (isLoadMore){
                                closeLoadMore();
                                isLoadMore = false;
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(findFascinatingData.message);
                            //关闭下拉刷新
                            if (isRefresh){
                                closeRefresh();
                            }
                            //关闭上拉加载更多
                            if (isLoadMore){
                                closeLoadMore();
                                isLoadMore = false;
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),findFascinatingData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //关闭下拉刷新
                        if (isRefresh){
                            closeRefresh();
                        }

                        //关闭上拉加载更多
                        if (isLoadMore){
                            closeLoadMore();
                            isLoadMore = false;
                        }

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



}
