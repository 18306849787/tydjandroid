package com.typartybuilding.fragment.fgPartyBuildingVideo;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.FindFascinatingAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.pbmicrovideo.FindFascinatingData;
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
 * 党建微视页面  发现精彩
 */
public class FragmentFindFascinating extends BaseFragment {

    private String TAG = "FragmentFindFascinating";

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.imageView_find_fascinating)
    ImageView imageView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FindFascinatingAdapter adapter;
    private List<MicroVideo> dataList = new ArrayList<>();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 20;
    private int pageCount;

    private int loadingState = 0; //0:没有加载数据， 1 在加载数据
    private  int lastVisiblePosition = 0;

    private boolean isDestroy;
    private boolean isRefresh;      //是否 在 下拉刷新

    @Override
    protected void initViews(Bundle savedInstanceState) {

        //获取发现精彩数据
        isDestroy = false;
        isRefresh = false;
        setRefreshLayout();
        initRecyclerView();
        getFindFascinatingData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_video_fragment_find_fascinating;
    }

    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;

                pageNo = 1;
                lastVisiblePosition = 0;
                //adapter.setTypeItemFooterStart();
                Log.i(TAG, "onRefresh: pageNo : " + pageNo);
                getFindFascinatingData();

            }
        });
    }


    private void initRecyclerView(){
        //瀑布流
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL){

        };
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FindFascinatingAdapter(dataList,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //防止第一行到顶部有空白区域
                layoutManager.invalidateSpanAssignments();
                Log.i(TAG, "onScrollStateChanged: pageNo : " + pageNo);
                Log.i(TAG, "onScrollStateChanged: pageCount : " + pageCount);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){//滑动停止

                    if(lastVisiblePosition + 1 == adapter.getItemCount()) {//滑动到最后一个item

                        if (pageNo <= pageCount && loadingState==0){
                            loadingState = 1;
                            getFindFascinatingData();
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
                int positions[] = layoutManager.findLastVisibleItemPositions(null);
                for(int pos : positions){
                    if(pos > lastVisiblePosition){
                        lastVisiblePosition = pos;//得到最后一个可见的item的position
                        Log.i(TAG, "onScrolled: lastVisiblePosition : " + lastVisiblePosition);
                    }
                }
            }
        });
    }

    private void initData(FindFascinatingData findFascinatingData){
        if (!isDestroy){

            if (isRefresh){
                if (dataList.size() > 0){
                    dataList.clear();
                }
                isRefresh = false;
            }

            MicroVideo microVideo[] = findFascinatingData.data.rows;

            if (microVideo != null){

                for (int i = 0; i < microVideo.length; i++){
                    dataList.add(microVideo[i]);
                    //Log.i(TAG, "initData: 浏览次数 i  " + i + ": " + microVideo[i].visionBrowseTimes );
                }

                adapter.notifyDataSetChanged();
            }
            loadingState = 0;

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
                            initData(findFascinatingData);
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
                            RetrofitUtil.errorMsg(findFascinatingData.message);
                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),findFascinatingData.message);
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
