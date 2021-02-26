package com.typartybuilding.fragment.fgPartyBuildingVideo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.typartybuilding.R;
import com.typartybuilding.activity.pbvideo.FindFascinatingActivity;
import com.typartybuilding.activity.pbvideo.HotBotActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.FindFascinatingAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.FindFascinatingAdapterNew;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingVideoNew;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingVideoNew1;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.pbmicrovideo.FindFascinatingData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;

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
 * 党建微视页面  发现精彩
 */
public class FragmentFindFascinatingNew extends BaseFragment {

    private String TAG = "FragmentFindFascinating";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FindFascinatingAdapterNew adapter;
    private List<MicroVideo> dataList = new ArrayList<>();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 30;
    private int pageCount;

    private boolean isDestroy;
    private boolean isRefresh;      //是否 在 下拉刷新
    private boolean isLoadMore;     //是否在 上拉加载更多

    private HomeFragmentPartyBuildingVideoNew fgParent;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //获取父 Fragment
        fgParent = (HomeFragmentPartyBuildingVideoNew)getParentFragment();
        //获取发现精彩数据
        isDestroy = false;
        isRefresh = false;
        isLoadMore = false;

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
        return R.layout.fg_pb_video_fragment_find_fascinating_new;
    }

    //下拉刷新
    public void refreshData(){

        isRefresh = true;
        pageNo = 1;
        Log.i(TAG, "onRefresh: pageNo : " + pageNo);
        getFindFascinatingData();

    }

    //上拉加载更多
    public void loadMore(){
        if (pageNo <= pageCount ){
            getFindFascinatingData();
            isLoadMore = true;
        }else {
            fgParent.noMore();

        }
    }


    private void initRecyclerView(){
        //瀑布流
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL){
        };
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FindFascinatingAdapterNew(dataList,getActivity());
        adapter.setHasStableIds(true);
        //1 ,表示在党建微视页面   2 ，表示在发现精彩的 activity 页面
        adapter.setFlag(1);
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

    }

    private void initData(FindFascinatingData findFascinatingData){
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

                for (int i = 0; i < microVideo.length; i++){
                    dataList.add(microVideo[i]);
                    adapter.notifyItemInserted(startItem + i);
                }
                //adapter.notifyDataSetChanged();
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
                                fgParent.closeRefresh();
                            }

                            initData(findFascinatingData);

                            //关闭上拉加载更多
                            if (isLoadMore){
                                fgParent.closeLoadMore();
                                isLoadMore = false;
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(findFascinatingData.message);
                            //关闭下拉刷新
                            if (isRefresh){
                                fgParent.closeRefresh();
                            }

                            //关闭上拉加载更多
                            if (isLoadMore){
                                fgParent.closeLoadMore();
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
                            fgParent.closeRefresh();
                        }

                        //关闭上拉加载更多
                        if (isLoadMore){
                            fgParent.closeLoadMore();
                            isLoadMore = false;
                        }

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.textView_more)
    public void onClickMore(){
        Intent intentAc = new Intent(getActivity(), FindFascinatingActivity.class);
        startActivity(intentAc);
    }

}
