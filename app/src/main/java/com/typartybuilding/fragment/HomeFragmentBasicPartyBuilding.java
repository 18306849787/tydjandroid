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

import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.CurrentNewsAdapter;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.ArticleVideoData;
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
import retrofit2.Retrofit;

/**
 * 基层党建 页面
 */
public class HomeFragmentBasicPartyBuilding extends BaseFragmentHome {

    private String TAG = "HomeFragmentBasicPartyBuilding";

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.nestedScrollView)
    NestedScrollView scrollView;
    @BindView(R.id.imageView_basic_pb)
    ImageView imageView;
    @BindView(R.id.textView_basic_pb)
    TextView textSite;                     //地址 显示和选择按钮
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public PopupWindow popupWindow;  //底部弹窗
    public View popView;             //弹窗布局


    private int pageNo = 1;    //分页-页码。不传则默认1
    private int pageSize = 20;  //分页-每页条数。不传则默认10
    private int pageCount ;     //新闻总共多少页
    private int loadingState;

    private Handler handler = new Handler();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

    private List<ArticleBanner> bannerList = new ArrayList<>();
    private CurrentNewsAdapter adapter;

    private TextView[] textViews = new TextView[20];    //20个 地区
    private ImageView[] imageViews = new ImageView[20]; //是否被选中的标记
    private String site;                                //存放设置后的地址
    private int index;                                //地址对应的控件的下标

    private int lastVisiblePosition = 0;

    private boolean isDestroy;
    private boolean isRefresh;       //是否 在 下拉刷新

    @Override
    protected void initViews(Bundle savedInstanceState) {

        //获取用户设置的地址的控件下标
        index = MyApplication.pref.getInt(MyApplication.prefKey1,-1);
        //初始化 地址选择弹窗
        initPopupWindow();
        //site = MyApplication.pref.getString(MyApplication.prefKey1,"");
        if (index != -1){
            site = textViews[index].getText().toString();
            textSite.setText(site);
        }

        initRecyclerView();
        //获取数据
        isDestroy = false;
        isRefresh = false;
        getArticleVideo2();

        setRefreshLayout();

    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_basic_party_building;
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
                refresh();
            }
        });
    }

    //刷新数据
    private void refresh(){

        isRefresh = true;

        Log.i(TAG, "refresh: bannerList.size() : " + bannerList.size());
        pageNo = 1;
        //lastVisiblePosition = 0;

        Log.i(TAG, "onRefresh: pageNo : " + pageNo);

        getArticleVideo2();

    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CurrentNewsAdapter(bannerList,getActivity());
        recyclerView.setFocusable(false);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE){//滑动停止
                    if(lastVisiblePosition + 1 == adapter.getItemCount()) {//滑动到最后一个item
                        if (pageNo <= pageCount && loadingState==0){
                            loadingState = 1;
                            getArticleVideo2();
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

                lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

            }
        });

    }

    private void initPopupWindow(){
        popView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popupwindow_basic_pb_choose_site, null);
        //关闭弹窗的按钮
        ImageButton btnBack = popView.findViewById(R.id.imageButton_back);
        //找到各个子控件
        findView();
        //设置点击事件
        setClick();
        //设置地址完成后的按钮
        TextView textComplete = popView.findViewById(R.id.textView_complete);

        int dpY = Utils.dip2px(getActivity(),356);
        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, dpY);
        //点击外部消失
       /* popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(false);*/
       // popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(getActivity(),1f);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });

        textComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (site != null) {
                    textSite.setText(site);
                    popupWindow.dismiss();
                    MyApplication.editor.putInt(MyApplication.prefKey1,index);
                    MyApplication.editor.apply();

                    //修改地址后，刷新数据
                    refresh();

                    Log.i(TAG, "onClick: 地址修改完成");
                }
            }
        });

    }

    /**
     * 修改地址按钮
     */
    @OnClick(R.id.framelayout)
    public void onClickTextView(){
        if (!popupWindow.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(getActivity(),0.7f);
        }
    }

    private void initData(ArticleVideoData articleVideoData){
        if (!isDestroy){

            if (isRefresh){
                if (bannerList.size() > 0){
                    bannerList.clear();
                }
                isRefresh = false;
            }

            ArticleBanner banner[] = articleVideoData.data.rows;
            Log.i(TAG, "initData: banner[] : " + banner);
            if (banner != null){
                Log.i(TAG, "initData: size : " + banner.length);
                for (int i = 0; i < banner.length; i++){
                    bannerList.add(banner[i]);
                }
                adapter.notifyDataSetChanged();
            }else {
                adapter.notifyDataSetChanged();
                adapter.setTypeItemFooter();
            }
        }
        loadingState = 0;
        /*if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }*/
    }

    /**
     *  获取 资讯文章数据， articleType 3：党建基层，5：学习领袖，6：党员教育片，7：时代先锋，8：直播
     *                     urlType     1：图片，2：视频
     */
    private void getArticleVideo2(){

        //site = MyApplication.pref.getString(MyApplication.prefKey1,"");
        index = MyApplication.pref.getInt(MyApplication.prefKey1,-1);
        String organName = null;
        if (index == -1){
            organName = textSite.getText().toString();
        }else {
            organName = textViews[index].getText().toString();
        }
        Log.i(TAG, "getArticleVideo2: organName : " + organName);
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getArticleVideo2(userId,pageNo,pageSize,3,1,token,organName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleVideoData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArticleVideoData articleVideoData) {
                        Log.i(TAG, "onNext: ");
                        int code = Integer.valueOf(articleVideoData.code);
                        Log.i(TAG, "onNext: code : " + code);

                        if (code == 0){
                            initData(articleVideoData);
                            pageNo++;
                            pageCount = articleVideoData.data.pageCount;
                            Log.i(TAG, "onNext: pageCount : " + pageCount);
                            Log.i(TAG, "onNext: pagNo : " + pageNo);
                            Log.i(TAG, "onNext: articleVideoData.data.rows : " + articleVideoData.data.rows);
                            if (pageNo > pageCount){
                                if (adapter != null) {
                                    adapter.setTypeItemFooter();
                                }
                            }
                            //关闭下拉刷新
                            if (refreshLayout != null){
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }

                        }else if (code == -1){
                            Log.i(TAG, "onNext: -1");
                            RetrofitUtil.errorMsg(articleVideoData.message);
                            //关闭下拉刷新
                            if (refreshLayout != null){
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),articleVideoData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //关闭下拉刷新
                        if (refreshLayout != null){
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






    private void findView(){
        if (popView != null){
            textViews[0] = popView.findViewById(R.id.textView1);
            textViews[1] = popView.findViewById(R.id.textView2);
            textViews[2] = popView.findViewById(R.id.textView3);
            textViews[3] = popView.findViewById(R.id.textView4);
            textViews[4] = popView.findViewById(R.id.textView5);
            textViews[5] = popView.findViewById(R.id.textView6);
            textViews[6] = popView.findViewById(R.id.textView7);
            textViews[7] = popView.findViewById(R.id.textView8);
            textViews[8] = popView.findViewById(R.id.textView9);
            textViews[9] = popView.findViewById(R.id.textView10);
            textViews[10] = popView.findViewById(R.id.textView11);
            textViews[11] = popView.findViewById(R.id.textView12);
            textViews[12] = popView.findViewById(R.id.textView13);
            textViews[13] = popView.findViewById(R.id.textView14);
            textViews[14] = popView.findViewById(R.id.textView15);
            textViews[15] = popView.findViewById(R.id.textView16);
            textViews[16] = popView.findViewById(R.id.textView17);
            textViews[17] = popView.findViewById(R.id.textView18);
            textViews[18] = popView.findViewById(R.id.textView19);
            textViews[19] = popView.findViewById(R.id.textView20);

            imageViews[0] = popView.findViewById(R.id.imageView1);
            imageViews[1] = popView.findViewById(R.id.imageView2);
            imageViews[2] = popView.findViewById(R.id.imageView3);
            imageViews[3] = popView.findViewById(R.id.imageView4);
            imageViews[4] = popView.findViewById(R.id.imageView5);
            imageViews[5] = popView.findViewById(R.id.imageView6);
            imageViews[6] = popView.findViewById(R.id.imageView7);
            imageViews[7] = popView.findViewById(R.id.imageView8);
            imageViews[8] = popView.findViewById(R.id.imageView9);
            imageViews[9] = popView.findViewById(R.id.imageView10);
            imageViews[10] = popView.findViewById(R.id.imageView11);
            imageViews[11] = popView.findViewById(R.id.imageView12);
            imageViews[12] = popView.findViewById(R.id.imageView13);
            imageViews[13] = popView.findViewById(R.id.imageView14);
            imageViews[14] = popView.findViewById(R.id.imageView15);
            imageViews[15] = popView.findViewById(R.id.imageView16);
            imageViews[16] = popView.findViewById(R.id.imageView17);
            imageViews[17] = popView.findViewById(R.id.imageView18);
            imageViews[18] = popView.findViewById(R.id.imageView19);
            imageViews[19] = popView.findViewById(R.id.imageView20);

            Log.i(TAG, "findView: index : " + index);
            if (index == -1){
                imageViews[0].setVisibility(View.VISIBLE);
            }else {
                imageViews[index].setVisibility(View.VISIBLE);
            }

        }
    }

    private void changeImgState(int i){
        for (int j = 0; j < 20; j++){
            if (j != i){
                imageViews[j].setVisibility(View.INVISIBLE);
            }
        }
    }


    private void setClick(){

        textViews[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[0].setVisibility(View.VISIBLE);
                changeImgState(0);
                site = textViews[0].getText().toString();
                index = 0;
            }
        });

        textViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[1].setVisibility(View.VISIBLE);
                changeImgState(1);
                site = textViews[1].getText().toString();
                index = 1;
            }
        });

        textViews[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[2].setVisibility(View.VISIBLE);
                changeImgState(2);
                site = textViews[2].getText().toString();
                index = 2;
            }
        });

        textViews[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[3].setVisibility(View.VISIBLE);
                changeImgState(3);
                site = textViews[3].getText().toString();
                index = 3;
            }
        });

        textViews[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[4].setVisibility(View.VISIBLE);
                changeImgState(4);
                site = textViews[4].getText().toString();
                index = 4;
            }
        });

        textViews[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[5].setVisibility(View.VISIBLE);
                changeImgState(5);
                site = textViews[5].getText().toString();
                index = 5;
            }
        });

        textViews[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[6].setVisibility(View.VISIBLE);
                changeImgState(6);
                site = textViews[6].getText().toString();
                index = 6;
            }
        });

        textViews[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 7");
                imageViews[7].setVisibility(View.VISIBLE);
                changeImgState(7);
                site = textViews[7].getText().toString();
                index = 7;
            }
        });

        textViews[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 8");
                imageViews[8].setVisibility(View.VISIBLE);
                changeImgState(8);
                site = textViews[8].getText().toString();
                index = 8;
            }
        });

        textViews[9].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[9].setVisibility(View.VISIBLE);
                changeImgState(9);
                site = textViews[9].getText().toString();
                index = 9;
            }
        });

        textViews[10].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[10].setVisibility(View.VISIBLE);
                changeImgState(10);
                site = textViews[10].getText().toString();
                index = 10;
            }
        });

        textViews[11].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[11].setVisibility(View.VISIBLE);
                changeImgState(11);
                site = textViews[11].getText().toString();
                index = 11;
            }
        });

        textViews[12].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[12].setVisibility(View.VISIBLE);
                changeImgState(12);
                site = textViews[12].getText().toString();
                index = 12;
            }
        });

        textViews[13].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[13].setVisibility(View.VISIBLE);
                changeImgState(13);
                site = textViews[13].getText().toString();
                index = 13;
            }
        });

        textViews[14].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[14].setVisibility(View.VISIBLE);
                changeImgState(14);
                site = textViews[14].getText().toString();
                index = 14;
            }
        });

        textViews[15].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[15].setVisibility(View.VISIBLE);
                changeImgState(15);
                site = textViews[15].getText().toString();
                index = 15;
            }
        });

        textViews[16].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[16].setVisibility(View.VISIBLE);
                changeImgState(16);
                site = textViews[16].getText().toString();
                index = 16;
            }
        });

        textViews[17].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[17].setVisibility(View.VISIBLE);
                changeImgState(17);
                site = textViews[17].getText().toString();
                index = 17;
            }
        });

        textViews[18].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[18].setVisibility(View.VISIBLE);
                changeImgState(18);
                site = textViews[18].getText().toString();
                index = 18;
            }
        });

        textViews[19].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViews[19].setVisibility(View.VISIBLE);
                changeImgState(19);
                site = textViews[19].getText().toString();
                index = 19;
            }
        });

    }
}
