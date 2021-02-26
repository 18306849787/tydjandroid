package com.typartybuilding.fragment.fgPartyBuildingVideo;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.PbvPopularityListAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.pbmicrovideo.PopularityListData;
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
 * 党建微视页面  人气榜单
 */
public class FragmentPopularityList extends BaseFragment {

    private String TAG = "FragmentPopularityList";

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.imageView_popularity_list)
    ImageView imageView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private PbvPopularityListAdapter adapter;
    private List<PopularityListData.PopularityData> dataList = new ArrayList<>();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");


    private boolean isDestroy;

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initRecyclerView();
        //获取人气榜数据
        isDestroy = false;
        setRefreshLayout();

    }

    @Override
    public void onResume() {

        super.onResume();
       if (dataList.size() > 0){
           dataList.clear();
       }

       getPopularityListData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_video_fragment_popularity_list;
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

                getPopularityListData();
            }
        });
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PbvPopularityListAdapter(dataList,getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);
    }

    private void initData(PopularityListData popularityListData){
        if (!isDestroy){

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
                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(popularityListData.message);
                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),popularityListData.message);
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
