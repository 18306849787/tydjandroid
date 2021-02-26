package com.typartybuilding.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

import com.typartybuilding.R;
import com.typartybuilding.adapter.viewPagerAdapter.PblPictureAdapter;
import com.typartybuilding.adapter.viewPagerAdapter.PictureAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fgPartyBuildingLibrary.FragmentAlbumList;
import com.typartybuilding.fragment.fgPartyBuildingLibrary.FragmentDevelopmentHistory;
import com.typartybuilding.fragment.fgPartyBuildingLibrary.FragmentExampleStory;
import com.typartybuilding.fragment.fgPartyBuildingLibrary.FragmentLearnLaws;
import com.typartybuilding.fragment.fgPartyBuildingLibrary.FragmentNewIdeas;
import com.typartybuilding.fragment.fgPartyBuildingLibrary.FragmentNineteen;
import com.typartybuilding.fragment.fgPartyBuildingLibrary.FragmentRecommend;
import com.typartybuilding.gsondata.TyUrlData;
import com.typartybuilding.gsondata.pblibrary.PbLibraryUrlData;
import com.typartybuilding.retrofit.GeneralRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.WrapContentHeightViewPager;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;
import com.ximalaya.ting.android.opensdk.util.IDbDataCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class HomeFragmentPartyBuildingLibrary extends BaseFragmentHome {

    private String TAG = "HomeFragmentPartyBuildingLibrary";

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.viewpager_pb_library)
    WrapContentHeightViewPager viewPager;
    @BindView(R.id.radiogroup_pb_library)
    RadioGroup radioGroup;
    @BindView(R.id.framelayout_pb_library)
    FrameLayout frameLayout;               //装fragment的布局
    @BindViews({R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6})
    TextView textView [];
    @BindView(R.id.textView_albumlist_headline)
    TextView textHeadLine;              //专辑列表的标题

    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

    private Handler mHandler = new Handler();
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> imgUrlList = new ArrayList<>();   //存放顶部三张图片的url
    private PblPictureAdapter adapter;

    private boolean isDestory;     //是否已经销毁
    private int currentFg;         //当前所在 fragment


    public TagList mTagList = new TagList();

    @Override
    protected void initViews(Bundle savedInstanceState) {

        //初始化 喜马拉雅sdk
        MyApplication.initXiMaLaYa();
        isDestory = false;
        //获取轮播图
        getPbLibraryUrl();
        initFragmentList();
        //设置下拉刷新
        setRefreshLayout();
        //initRadioGroup();
        //initViewPager();
        //导航第一个默认被选中
        textView[0].setSelected(true);
        loadFragment(0);
        currentFg = 0;

        //getTagList();
        Log.i(TAG, "initViews: ");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mRunnable);
        Log.i(TAG, "onDestroyView: ");

        isDestory = true;
    }


    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_party_building_library;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void stopLoad() {

    }

    private void setRefreshLayout(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新轮播图
                getPbLibraryUrl();

            }
        });
    }

    private void initRadioGroup(){

        if (radioGroup != null){
            radioGroup.removeAllViews();
        }

        for (int i = 0; i < imgUrlList.size(); i++){
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT
                    ,RadioGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setButtonDrawable(R.drawable.fg_pb_library_selector2);
            radioButton.setPadding(10,0,10,0);
            if (radioGroup != null) {
                radioGroup.addView(radioButton);
            }
        }

    }

    /**
     * 开始轮播图
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int item = viewPager.getCurrentItem();
            viewPager.setCurrentItem(item + 1);
            Log.i(TAG, "run: item : " + item);
            startSlideShow();
        }

    };

    private void startSlideShow() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 3000);
    }


    private void initViewPager(){
        if (adapter != null) {
            adapter = null;
        }
        adapter = new PblPictureAdapter(getActivity(),imgUrlList);
        if (viewPager != null) {
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);
            //两张图以上才轮播
            if (imgUrlList.size() > 1) {
                startSlideShow();
            }
            ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    //循环滑动时，i会一直增加
                    i %= imgUrlList.size();
                    Log.i(TAG, "onPageSelected: i = " + i);
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                    radioButton.setChecked(true);
                    for (int j = 0; j < imgUrlList.size(); j++) {
                        if (i != j) {
                            Log.i(TAG, "onPageSelected: j = " + j);
                            RadioButton radioButton1 = (RadioButton) radioGroup.getChildAt(j);
                            Log.i(TAG, "onPageSelected: radiobutton1 : " + radioButton1);
                            radioButton1.setChecked(false);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }
    }

    private void initData(PbLibraryUrlData pbLibraryUrlData){

        if (!isDestory) {
            if (imgUrlList.size() != 0) {
                imgUrlList.clear();
            }
            if (pbLibraryUrlData != null) {
                int size = pbLibraryUrlData.data.length;
                for (int i = 0; i < size; i++) {
                    imgUrlList.add(pbLibraryUrlData.data[i]);

                }
                initRadioGroup();
                initViewPager();
                //adapter.notifyDataSetChanged();

            }
        }
    }


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

        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);
        fragmentList.add(fragment4);
        fragmentList.add(fragment5);
        fragmentList.add(fragment6);
    }

    /**
     * 动态加载FragMent
     */
    private void loadFragment(int i){
        FragmentTransaction transaction;
        transaction = getChildFragmentManager().beginTransaction();

        transaction.replace(R.id.framelayout_pb_library,fragmentList.get(i));
        transaction.commit();
    }

    public void switchFragment(int i) {

        currentFg = i;

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();/*.setCustomAnimations(
                android.R.anim.fade_in, R.anim.slide_in_from_bottom);*/
        if (!fragmentList.get(i).isAdded()) {    // 先判断是否被add过
            transaction.add(R.id.framelayout_pb_library, fragmentList.get(i)).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.show(fragmentList.get(i)).commit(); // 隐藏当前的fragment，显示下一个
        }
        for (int j = 0; j < 6; j ++){
            if (j != i){
                if (fragmentList.get(j).isVisible()){
                    transaction.hide(fragmentList.get(j));
                }
            }
        }

    }

    /**
     * 更改 导航栏的点击状态
     */
    private void changeState(int j){
        for (int i = 0; i < 6; i++){
            if (i != j){
                textView[i].setSelected(false);
            }
        }
    }

    @OnClick({R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6})
    public void onClickTextView(View view){
        switch (view.getId()){
            case R.id.textView1 :
                textView[0].setSelected(true);
                textHeadLine.setText(R.string.fg_pbl_str6);
                changeState(0);
                //loadFragment(0);
                switchFragment(0);
                break;
            case R.id.textView2 :
                textView[1].setSelected(true);
                textHeadLine.setText(R.string.fg_pbl_str1);
                changeState(1);
                //loadFragment(1);
                switchFragment(1);
                break;
            case R.id.textView3 :
                textView[2].setSelected(true);
                textHeadLine.setText(R.string.fg_pbl_str2);
                changeState(2);
                //loadFragment(2);
                switchFragment(2);
                break;
            case R.id.textView4 :
                textView[3].setSelected(true);
                textHeadLine.setText(R.string.fg_pbl_str3);
                changeState(3);
                //loadFragment(3);
                switchFragment(3);
                break;
            case R.id.textView5 :
                textView[4].setSelected(true);
                textHeadLine.setText(R.string.fg_pbl_str4);
                changeState(4);
                //loadFragment(4);
                switchFragment(4);
                break;
            case R.id.textView6 :
                textView[5].setSelected(true);
                textHeadLine.setText(R.string.fg_pbl_str5);
                changeState(5);
                //loadFragment(5);
                switchFragment(5);
                break;
        }
    }



    private void getPbLibraryUrl(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.getPbLibraryUrl(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PbLibraryUrlData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PbLibraryUrlData pbLibraryUrlData) {
                        int code = Integer.valueOf(pbLibraryUrlData.code);
                        if (code == 0){
                            initData(pbLibraryUrlData);
                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(pbLibraryUrlData.message);
                            if (refreshLayout != null) {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),pbLibraryUrlData.message);
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
