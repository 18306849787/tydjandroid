package com.typartybuilding.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.ChoicenessAdapter;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fgChoiceness.FragmentPicture;
import com.typartybuilding.fragment.fragmentbottomnavigation.FragmentHome;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;
import com.typartybuilding.gsondata.choiceness.ChoicenessNewData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;

/**
 *  精选 页面
 */
public class HomeFragmentChoicenessNew extends BaseFragmentHome {

    private String TAG = "HomeFragmentChoicenessNew";

    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout refreshLayout;

    /*@BindView(R.id.scrollview_home_fg_ch)
    public NestedScrollView scrollView;*/


    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;      //页面的父布局
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.textView_no_more)
    TextView textNoMore;             //没有更多的 文字提示

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    public FragmentHome parentFg;
    private FragmentPicture fgPicture;

    //时事新闻
    private List<ArticleBanner> dataList = new ArrayList<>();
    private ChoicenessAdapter adapter;

    private int pageNo = 1;
    private int pageSize = 30;
    private int pageCount;

    private boolean isRefresh;     //是否 在 下拉刷新
    private boolean isLoadMore;    //是否 在 上拉加载更多
    private boolean isDestroy;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //获取父fragment
        parentFg = (FragmentHome) getParentFragment();
        //获取子fragment
        FragmentManager manager = getChildFragmentManager();
        fgPicture = (FragmentPicture)manager.findFragmentById(R.id.fragment_pic);

        //请求数据前，页面不可见，有进度条
        linearLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        isDestroy = false;
        isRefresh = false;
        isLoadMore = false;

        initRecyclerView();
        //获取轮播图数据
        getChoicenessData();
        //获取混排数据
        getChoicenessNewData();
        setRefreshLayout();

    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_choiceness_new;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void stopLoad() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChoicenessAdapter(dataList,getActivity());

        //解决RecyclerView 使用Glide加载图片调动notifyDataSetChanged()图片闪烁问题
        //adapter.setHasStableIds(true);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);

        recyclerView.setFocusable(false);
        recyclerView.setAdapter(adapter);

    }

    private void setRefreshLayout(){

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                //刷新轮播图，热门视频，微视
                getChoicenessData();
                //刷新时事新闻
                getChoicenessNewData();

                //textNoMore.setVisibility(View.GONE);
                //refreshLayout.setEnableLoadMore(true);
                //progressBar.setVisibility(View.GONE);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount){
                    //获取时事新闻
                    getChoicenessNewData();
                    isLoadMore = true;
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                    //textNoMore.setVisibility(View.VISIBLE);
                    //refreshLayout.setEnableLoadMore(false);
                }
            }
        });

    }

    //获取轮播图 数据,并刷新页面
    private void getArticleBanner(ChoicenessData choicenessData){

        int bannerSize = choicenessData.data.articleBanner.length;
        if (bannerSize > 0) {
            List<ArticleBanner> bannerList = new ArrayList<>();
            for (int i = 0; i < bannerSize; i++) {
                bannerList.add(choicenessData.data.articleBanner[i]);
            }
            fgPicture.loadData(bannerList);
        }
    }

    //处理轮播图数据
    private void initData(ChoicenessData choicenessData){
        if (!isDestroy) {
            //获取轮播图 数据,并刷新页面
            getArticleBanner(choicenessData);
            //页面刷新完毕
            linearLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            //滚动到顶部
            //scrollView.scrollTo(0, 0);
        }
    }

    //以前的接口，只获取 轮播图
    private void getChoicenessData(){

        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofit = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofit.getChoicenessData(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChoicenessData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ChoicenessData choicenessData) {
                        int code = Integer.valueOf(choicenessData.code);
                        Log.i(TAG, "onNext: 获取轮播图code : " + code);
                        if (code == 0) {

                            initData(choicenessData);

                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(choicenessData.message);
                            //关闭下拉刷新
                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),choicenessData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                        RetrofitUtil.requestError();
                        //关闭下拉刷新
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(true);
                        }

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    //处理 时事新闻数据
    private void initData(ChoicenessNewData choicenessNewData){
        if (!isDestroy){

            if (isRefresh){
                int size = dataList.size();
                if (dataList.size() > 0){
                    dataList.clear();
                }
                isRefresh = false;
                if (adapter != null){
                    adapter.notifyDataSetChanged();
                    //adapter.notifyItemRangeRemoved(0,size);
                    //adapter.notifyItemRangeChanged(0,size);
                }
            }

            int startItem = dataList.size();
            ArticleBanner [] rows = choicenessNewData.data.rows;
            Log.i(TAG, "initData: length : " + rows.length);
            if (rows != null && rows.length > 0){
                int length = rows.length;
                for (int i = 0; i < length; i++){
                    dataList.add(rows[i]);
                    adapter.notifyItemInserted(startItem + i);
                }
                /*int size = dataList.size();
                if (adapter != null){
                    //adapter.notifyDataSetChanged();
                    adapter.notifyItemRangeChanged(startItem,size);
                }*/
            }
            //页面刷新完毕
            linearLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    //新接口 ， 获取混排 的新闻数据
    private void getChoicenessNewData(){

        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofit = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofit.getChoicenessNewData(pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChoicenessNewData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ChoicenessNewData choicenessNewData) {
                        int code = Integer.valueOf(choicenessNewData.code);
                        Log.i(TAG, "onNext: 获取混排新闻数据code ： " + code);
                        if (code == 0) {

                            pageNo++;
                            pageCount = choicenessNewData.data.pageCount;

                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                            }

                            initData(choicenessNewData);

                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(choicenessNewData.message);
                            //关闭下拉刷新
                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),choicenessNewData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                        RetrofitUtil.requestError();
                        //关闭下拉刷新
                        if (isRefresh){
                            refreshLayout.finishRefresh(true);
                        }
                        if (isLoadMore){
                            refreshLayout.finishLoadMore(true);
                            isLoadMore = false;
                        }

                        if (e instanceof HttpException){
                            HttpException exception = (HttpException)e;
                            try {
                                String json = exception.response().errorBody().string();
                                Log.i(TAG, "onError: json ： " + json);
                            }catch (IOException E){

                            }
                        }

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


}
