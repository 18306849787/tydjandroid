package com.typartybuilding.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.DreamWishAdapter;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.dreamwish.DreamWishData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 圆梦心愿 页面
 */
public class HomeFragmentDreamWish extends BaseFragmentHome {

    private String TAG = "HomeFragmentDreamWish";

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private DreamWishAdapter adapter;
    private List<DreamWishData.WishData> dataList = new ArrayList<>();

    private int pageNo = 1;    //分页-页码。不传则默认1
    private int pageSize = 20;  //分页-每页条数。不传则默认10
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageCount ;     //新闻总共多少页
    private int loadingState;

    private int lastVisiblePosition = 0;

    private boolean isDestroy;
    private boolean isRefresh;          //是否是 下拉刷新

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initRecyclerView();
        setRefreshLayout();
        //获取圆梦心愿数据
        isDestroy = false;
        isRefresh = false;
        getWishData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_dream_wish;
    }

    @Override
    protected void lazyLoad() {
        //getWishData();
    }

    @Override
    protected void stopLoad() {

    }

    @Override
    public void onResume() {
        super.onResume();
        //从星愿详情页面，返回后，刷新修改的数据
        if (adapter != null){
            adapter.notifyItemChanged(adapter.currentPos);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
    }


    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
               /* if (dataList.size() > 0){
                    dataList.clear();
                }*/
                //adapter.notifyDataSetChanged();
                pageNo = 1;

                Log.i(TAG, "onRefresh: pageNo : " + pageNo);
                getWishData();

            }
        });
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DreamWishAdapter(dataList,getActivity());
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line_dream_wish));
        recyclerView.addItemDecoration(dividerLine);

        //adapter.setHasStableIds(true);
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
                            getWishData();
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

            }
        });
    }

    private void initData(DreamWishData dreamWishData){
        if (!isDestroy){
            //下拉刷新，需清空数据
            if (isRefresh){
                if (dataList.size() > 0){
                    dataList.clear();
                }
                isRefresh = false;
                Log.i(TAG, "initData: 清空集合数据");
            }

            DreamWishData.WishData [] wishData = dreamWishData.data.rows;
            if (wishData != null){
                for (int i = 0; i < wishData.length; i++){
                    dataList.add(wishData[i]);
                }
                adapter.notifyDataSetChanged();
            }
            loadingState = 0;
        }

    }

    private void getWishData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getWishData(pageNo,pageSize,token)
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
                            initData(dreamWishData);
                            pageCount = dreamWishData.data.pageCount;
                            pageNo++;
                            if (pageNo > pageCount){
                                if (adapter != null) {
                                    adapter.setTypeItemFooter();
                                }
                            }

                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(dreamWishData.message);

                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),dreamWishData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();

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
