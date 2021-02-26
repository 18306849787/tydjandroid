package com.typartybuilding.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.CurrentNewsAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.FgBasicPartyAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fgPartyBuildingMap.FragmentPbMap;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.ArticleVideoData;
import com.typartybuilding.gsondata.TyUrlData;
import com.typartybuilding.gsondata.basicparty.BasicPartyData;
import com.typartybuilding.gsondata.learntime.EducationFilmData;
import com.typartybuilding.gsondata.pbmap.RegionNameData;
import com.typartybuilding.retrofit.GeneralRetrofitInterface;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;

/**
 * 基层党建 页面
 */
public class HomeFragmentBasicPartyBuildingNew extends BaseFragmentHome {

    private String TAG = "HomeFragmentBasicPartyBuildingNew";

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

    private List<BasicPartyData.BasicParty> dataList = new ArrayList<>();
    private FgBasicPartyAdapter adapter;

    private String picUrl;      //页面顶部图片的 url

    private boolean isDestroy;
    private boolean isRefresh;       //是否 在 下拉刷新

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initRecyclerView();
        //获取数据
        isDestroy = false;
        isRefresh = false;
        //获取页面顶部的图片 url
        getPictureUrl();
        //获取新闻数据
        getBasicPartyData();
        //设置下拉刷新
        setRefreshLayout();

    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_basic_party_building_new;
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
        if (adapter != null){
            adapter.refreshBrowseTimes();
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
    }

    private void setRefreshLayout(){
        //禁止上拉加载更多
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                //清空FgBasicPartyAdapter中存放FgBasicPartyItemAdapter的 集合
                if (adapter != null && adapter.adapterList != null && adapter.adapterList.size() > 0){
                    adapter.adapterList.clear();
                }
                getPictureUrl();
                getBasicPartyData();
            }
        });
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FgBasicPartyAdapter(dataList,getActivity());
        recyclerView.setFocusable(false);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line_dream_wish));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);

    }

    //加载页面顶部图片
    private void loadPic(){
        if (!isDestroy){
            Glide.with(getActivity()).load(picUrl)
                    .apply(MyApplication.requestOptions)
                    .into(imageView);
        }
    }

    private void getPictureUrl(){

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.getUrl(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TyUrlData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TyUrlData tyUrlData) {
                        int code = Integer.valueOf(tyUrlData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            picUrl = tyUrlData.data.base_party_pic;
                            loadPic();

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(tyUrlData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyApplication.getContext(),tyUrlData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        if (e instanceof HttpException){
                            HttpException httpException = (HttpException)e;
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void initData(BasicPartyData basicPartyData){
        if (!isDestroy){
            if (dataList != null && dataList.size() > 0){
                dataList.clear();
            }
            BasicPartyData.BasicParty [] basicParties = basicPartyData.data;
            if (basicParties != null){
                for (int i = 0; i < basicParties.length; i++){
                    dataList.add(basicParties[i]);
                    Log.i(TAG, "initData: basicParties[i].rows.length : " + basicParties[i].rows.length);
                }
                if (adapter != null){
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void getBasicPartyData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getBasicPartyData(3, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BasicPartyData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BasicPartyData basicPartyData) {
                        int code = Integer.valueOf(basicPartyData.code);
                        if (code == 0){
                            initData(basicPartyData);
                            //关闭下拉刷新
                            if (isRefresh){
                                if (refreshLayout != null){
                                    refreshLayout.finishRefresh(true);
                                }
                            }
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(basicPartyData.message);
                            //关闭下拉刷新
                            if (isRefresh){
                                if (refreshLayout != null){
                                    refreshLayout.finishRefresh(true);
                                }
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyApplication.getContext(),basicPartyData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //关闭下拉刷新
                        if (isRefresh){
                            if (refreshLayout != null){
                                refreshLayout.finishRefresh(true);
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
