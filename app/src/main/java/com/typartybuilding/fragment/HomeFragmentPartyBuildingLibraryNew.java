package com.typartybuilding.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.typartybuilding.R;
import com.typartybuilding.activity.pblibrary.PblActivity;
import com.typartybuilding.adapter.viewPagerAdapter.PblPictureAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fgPartyBuildingLibrary.FragmentAlbumList;
import com.typartybuilding.gsondata.pblibrary.PbLibraryUrlData;
import com.typartybuilding.retrofit.GeneralRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.view.WrapContentHeightViewPager;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 党建书屋
 */
public class HomeFragmentPartyBuildingLibraryNew extends BaseFragmentHome {

    private String TAG = "HomeFragmentPartyBuildingLibrary";

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.framelayout_pb_library)
    FrameLayout frameLayout;               //装fragment的布局

    @BindViews({R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5})
    TextView textView [];

    private Handler handler = new Handler();


    @Override
    protected void initViews(Bundle savedInstanceState) {

        //初始化 喜马拉雅sdk
        MyApplication.initXiMaLaYa();
        //设置下拉刷新
        setRefreshLayout();
        //加载精选推荐
        loadFragment();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        //handler.removeCallbacks(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            loadFragment();
        }
    };

    @Override
    protected void lazyLoad() {
        Log.i(TAG, "lazyLoad: ");
        //loadFragment();
        //handler.postDelayed(mRunnable,300);
    }

    @Override
    protected void stopLoad() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_party_building_library_new;
    }


    private void setRefreshLayout(){
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
    }



    /**
     * 动态加载FragMent
     */
    private void loadFragment(){
        //精选推荐
        FragmentAlbumList fragment = new FragmentAlbumList();
        fragment.setTagName(null);

        FragmentTransaction transaction;
        transaction = getChildFragmentManager().beginTransaction();

        transaction.replace(R.id.framelayout_pb_library,fragment);
        transaction.commit();
    }


    //新思想，十九大，学法规，发展历程，榜样故事
    @OnClick({R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5})
    public void onClickTextView(View view){
        switch (view.getId()){
            case R.id.textView1 :
                String tagName1 = getResources().getString(R.string.fg_pbl_str1);
                skipPblActivity(tagName1);
                break;
            case R.id.textView2 :
                String tagName2 = getResources().getString(R.string.fg_pbl_str2);
                skipPblActivity(tagName2);
                break;
            case R.id.textView3 :
                String tagName3 = getResources().getString(R.string.fg_pbl_str3);
                skipPblActivity(tagName3);
                break;
            case R.id.textView4 :
                String tagName4 = getResources().getString(R.string.fg_pbl_str4);
                skipPblActivity(tagName4);
                break;
            case R.id.textView5 :
                String tagName5 = getResources().getString(R.string.fg_pbl_str5);
                skipPblActivity(tagName5);
                break;

        }
    }

    private void skipPblActivity(String tagName){

        Intent intentAc = new Intent(getActivity(), PblActivity.class);
        intentAc.putExtra("tagName",tagName);
        startActivity(intentAc);

    }







    /*  @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }

    private void isCanLoadData(){
        if (!isInit){
            return;
        }
        //判断视图对用户是否可见
        if (getUserVisibleHint()){
            if (!isLoad) {
                lazyLoad();
                isLoad = true;
            }

        }else {
            if (isLoad){
                stopLoad();
            }
        }
    }

    private void lazyLoad(){
        loadFragment();
    }

    private void stopLoad(){

    }*/



    private void initFragmentList(){

        FragmentAlbumList fragment1 = new FragmentAlbumList();
        fragment1.setTagName(null);
        FragmentAlbumList fragment2 = new FragmentAlbumList();
        fragment2.setTagName(getResources().getString(R.string.fg_pbl_str1));
        FragmentAlbumList fragment3 = new FragmentAlbumList();
        fragment3.setTagName(getResources().getString(R.string.fg_pbl_str2));
        FragmentAlbumList fragment4 = new FragmentAlbumList();
        fragment4.setTagName(getResources().getString(R.string.fg_pbl_str3));
        FragmentAlbumList fragment5 = new FragmentAlbumList();
        fragment5.setTagName(getResources().getString(R.string.fg_pbl_str4));
        FragmentAlbumList fragment6 = new FragmentAlbumList();
        fragment6.setTagName(getResources().getString(R.string.fg_pbl_str5));

    }


}
