package com.typartybuilding.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.dreamwish.GoodPeopleActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.DreamWishAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.DreamWishAdapterNew;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.dreamwish.DreamWishData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.UserUtils;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 圆梦心愿 页面
 */
public class HomeFragmentDreamWishNew extends BaseFragmentHome {

    private String TAG = "HomeFragmentDreamWish";

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private DreamWishAdapterNew adapter;
    private List<DreamWishData.WishData> dataList = new ArrayList<>();

    private int pageNo = 1;    //分页-页码。不传则默认1
    private int pageSize = 20;  //分页-每页条数。不传则默认10
    private int pageCount ;     //新闻总共多少页

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
        return R.layout.home_fragment_dream_wish_new;
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
            //判断，是否进入详情页面后 ，返回
            if (adapter.isNextAc) {
                adapter.notifyItemChanged(adapter.currentPos);
                //心愿认领后，该条数据不在显示
                if (dataList.get(adapter.currentPos-1).aspirationStatus == 3) {
                    dataList.remove(adapter.currentPos-1);
                    adapter.notifyItemRemoved(adapter.currentPos);
                    //数据的总数为 dataList size+1
                    adapter.notifyItemRangeChanged(adapter.currentPos, dataList.size()+1);
                }
                adapter.isNextAc = false;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
    }

    //认领心愿后，刷新数据，认领后的心愿数据，不在显示
    public void refreshWishData(int position){
        if (dataList != null){
            dataList.remove(position-1);
        }
        if (adapter != null){
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, dataList.size() + 1);
            Toast.makeText(MyApplication.getContext(),"心愿认领成功",Toast.LENGTH_SHORT).show();
        }
    }

   /* //跳转到好人榜
    @OnClick(R.id.textView_more)
    public void skipGoodPeopleAc(){
        Intent intentAc = new Intent(getActivity(), GoodPeopleActivity.class);
        startActivity(intentAc);
    }*/

    private void setRefreshLayout(){

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                getWishData();
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount){
                    getWishData();
                    //isLoadMore = true;
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });


    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DreamWishAdapterNew(dataList,getActivity(),this);
        //添加分割线
        /*DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line_dream_wish));
        recyclerView.addItemDecoration(dividerLine);*/

        //adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

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
                adapter.notifyDataSetChanged();
            }

            int startItem = dataList.size() + 1;
            DreamWishData.WishData [] wishData = dreamWishData.data.rows;
            if (wishData != null){
                for (int i = 0; i < wishData.length; i++){
                    dataList.add(wishData[i]);
                    adapter.notifyItemInserted(startItem + i);
                }
                //adapter.notifyDataSetChanged();
            }

        }

    }

    private void getWishData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getWishData(pageNo,pageSize, UserUtils.getIns().getToken())
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

                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                                refreshLayout.finishLoadMore(true);
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(dreamWishData.message);

                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                                refreshLayout.finishLoadMore(true);
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
                            refreshLayout.finishRefresh(true);
                            refreshLayout.finishLoadMore(true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
