package com.typartybuilding.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.choiceness.HotVideoActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.HotVideoAcAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.TyOrganizationAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.TyUrlData;
import com.typartybuilding.gsondata.choiceness.MoreHotVideoData;
import com.typartybuilding.gsondata.tyorganization.TyOrganizationData;
import com.typartybuilding.retrofit.GeneralRetrofitInterface;
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
 * 太原组工 页面
 */
public class HomeFragmentTyOrganization extends BaseFragmentHome {

    private String TAG = "HomeFragmentTyOrganization";

    /*@BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.webview_ty_organization)
    public WebView webView;
    private String tyzg_url;*/


    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    private int pageNo = 1;    //分页-页码。不传则默认1
    private int pageSize = 30;  //分页-每页条数。不传则默认10
    private int pageCount ;     //新闻总共多少页


    private List<TyOrganizationData.OrganizationData> dataList = new ArrayList<>();
    private TyOrganizationAdapter adapter;

    private boolean isDestroy;     //是否已经销毁
    private boolean isRefresh;     //是否 进行 下拉刷新
    private boolean isLoadMore;    //是否 在 上拉加载更多


    @Override
    protected void initViews(Bundle savedInstanceState) {
        //getUrl();
        isDestroy = false;
        isRefresh = false;

        initRecyclerView();
        //获取数据
        getTyOrganizationData();
        //设置下拉刷新
        setRefreshLayout();

    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_ty_organization;
    }

    @Override
    protected void lazyLoad() {
        //getTyOrganizationData();
    }

    @Override
    protected void stopLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
    }


    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                getTyOrganizationData();
                //textNoMore.setVisibility(View.GONE);
                //refreshLayout.setEnableLoadMore(true);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount){
                    getTyOrganizationData();
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

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TyOrganizationAdapter(dataList,getActivity());

        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);

    }

    private void initData(TyOrganizationData tyOrganizationData) {
        if (!isDestroy) {

            if (isRefresh){
                if (dataList.size() > 0){
                    dataList.clear();
                }
                isRefresh = false;
                adapter.notifyDataSetChanged();
            }

            int startItem = dataList.size();
            if (tyOrganizationData.data.rows != null) {
                Log.i(TAG, "loadData: pageCount : " + pageCount);
                int size = tyOrganizationData.data.rows.length;
                for (int i = 0; i < size; i++) {
                    dataList.add(tyOrganizationData.data.rows[i]);
                    adapter.notifyItemInserted(startItem + i);
                }
                //adapter.notifyDataSetChanged();
            }
        }

    }



    private void getTyOrganizationData(){
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getTyOrganizationData( pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TyOrganizationData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TyOrganizationData tyOrganizationData) {
                        int code = Integer.valueOf(tyOrganizationData.code);
                        if (code == 0){
                            pageNo++;
                            //获取总页数
                            pageCount = tyOrganizationData.data.pageCount;
                            //关闭下拉刷新
                            if (isRefresh){
                                if (refreshLayout != null) {
                                    refreshLayout.finishRefresh(true);
                                }
                            }

                            initData(tyOrganizationData);

                            if (isLoadMore){
                                if (refreshLayout != null) {
                                    refreshLayout.finishLoadMore(true);
                                    isLoadMore = false;
                                }
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(tyOrganizationData.message);
                            if (isRefresh){
                                if (refreshLayout != null) {
                                    refreshLayout.finishRefresh(true);
                                }
                            }
                            if (isLoadMore){
                                if (refreshLayout != null) {
                                    refreshLayout.finishLoadMore(true);
                                    isLoadMore = false;
                                }
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyApplication.getContext(),tyOrganizationData.message);

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();

                        if (isRefresh){
                            if (refreshLayout != null) {
                                refreshLayout.finishRefresh(true);
                            }
                        }
                        if (isLoadMore){
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
