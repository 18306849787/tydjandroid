package com.typartybuilding.fragment.fgPartyBuildingVideo;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.PbvHotBotAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.pbmicrovideo.HotWordData;
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
 * 党建微视页面  党建热搜
 */
public class FragmentHotBot extends BaseFragment {

    private String TAG = "FragmentHotBot" ;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.imageView_hot_bot)
    ImageView imageView;
    @BindView(R.id.recyclerView_hot_bot)
    RecyclerView recyclerView;

    private PbvHotBotAdapter adapter;
    private List<HotWordData.HotWord> dataList = new ArrayList<>();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 100;
    private int pageCount;
    private int loadingState;

    private  int lastVisiblePosition = 0;

    private boolean isDestroy;
    private boolean isRefresh;     //是否 在 下拉刷新

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initRecyclerView();
        setRefreshLayout();
        //获取热搜数据
        isDestroy = false;
        isRefresh = false;
        getHotWordData();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_video_fragment_hot_bot;
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
                pageNo = 1;
                //lastVisiblePosition = 0;
                Log.i(TAG, "onRefresh: pageNo : " + pageNo);
                getHotWordData();

            }
        });
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PbvHotBotAdapter(dataList,getContext());
        recyclerView.setFocusable(false);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //lastVisiblePosition = 0;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE){//滑动停止
                    Log.i(TAG, "onScrollStateChanged: lastVisiblePosition : " + lastVisiblePosition);
                    Log.i(TAG, "onScrollStateChanged: adapter.getItemCount() : " + adapter.getItemCount());
                    if(lastVisiblePosition + 1 == adapter.getItemCount()) {//滑动到最后一个item
                        if (pageNo <= pageCount && loadingState==0){
                            loadingState = 1;
                            adapter.setTypeItemFooterStart();
                            getHotWordData();

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




    private void initData(HotWordData hotWordData){
        if (!isDestroy){

            if (isRefresh){
                if (dataList.size() > 0){
                    dataList.clear();
                }
                isRefresh = false;
            }

            HotWordData.HotWord hotWords[] = hotWordData.data.rows;
            if (hotWords != null){
                for (int i = 0; i < hotWords.length; i++){
                    dataList.add(hotWords[i]);
                }
                adapter.notifyDataSetChanged();
            }
            loadingState = 0;
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
                            initData(hotWordData);
                            Log.i(TAG, "onNext: pageNo : " + pageNo);
                            Log.i(TAG, "onNext: pageCount ; " + pageCount);
                            if (pageNo > pageCount){
                                if (adapter != null){
                                    adapter.setTypeItemFooter();
                                }
                            }

                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(hotWordData.message);
                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }

                        }else if (code == 10){
                            Log.i(TAG, "onNext: message : " + hotWordData.message);
                            RetrofitUtil.tokenLose(getActivity(),hotWordData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e ; " + e);
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
