package com.typartybuilding.activity.dreamwish;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.pbvideo.HotBotActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.GoodPeopleAcAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.PbvHotBotAdapterNew;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.dreamwish.GoodPeopleData;
import com.typartybuilding.gsondata.pbmicrovideo.HotWordData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class GoodPeopleActivity extends WhiteTitleBaseActivity {

    private String TAG = "GoodPeopleActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.constraintLayout_curr_user)
    ConstraintLayout layoutUser;      //当前用户心愿数据布局，非党员，不显示
    @BindView(R.id.imageView_head)
    CircleImageView imgHead;        //头像
    @BindView(R.id.textView_name)
    TextView textName;              //名字
    @BindView(R.id.textView_ranking)
    TextView textRank;              //排名
    @BindView(R.id.textView_dream_num)
    TextView textDreamNum;          //完成的心愿的数

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.textView_no_more)
    TextView textNoMore;             //没有更多的 文字提示

    private List<GoodPeopleData.GoodPeople> dataList = new ArrayList<>();
    private GoodPeopleAcAdapter adapter;

    private GoodPeopleData.GoodPeople goodPeople;   //当前用户的排名信息

    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 30;
    private int pageCount;

    private boolean isRefresh;     //是否 在 下拉刷新
    private boolean isLoadMore;    //是否 在 上拉加载更多
    private boolean isFirst = true;       //是否是 第一次请求数据

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_people);
        ButterKnife.bind(this);
        textTitle.setText("好人榜");

        initRecyclerView();
        setRefreshLayout();
        //获取好人榜数据
        getGoodPeopleData();

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GoodPeopleAcAdapter(dataList,this);
        recyclerView.setFocusable(false);
        recyclerView.setAdapter(adapter);

    }

    private void setRefreshLayout(){

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                getGoodPeopleData();
                //textNoMore.setVisibility(View.GONE);
                //refreshLayout.setEnableLoadMore(true);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount){
                    getGoodPeopleData();
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

    //当前用户的信息
    private void initView(){
        if (goodPeople != null){
            //加载头像
            Glide.with(this).load(goodPeople.headImg)
                    .apply(MyApplication.requestOptions2)
                    .into(imgHead);
            textName.setText(goodPeople.username);
            //排名
            if (goodPeople.ranking > 99){
                textRank.setText(99 + "+");
            }else if(goodPeople.ranking == 0) {
                textRank.setText("");
            }else {
                textRank.setText(goodPeople.ranking + "");
            }
            //完成的心愿数
            textDreamNum.setText(goodPeople.aspirationFinish + "");
        }
    }

    private void initData(GoodPeopleData goodPeopleData){

        if (isRefresh){
            if (dataList.size() > 0){
                dataList.clear();
            }
            isRefresh = false;
            isFirst = true;
        }

        GoodPeopleData.GoodPeople goodPeoples[] = goodPeopleData.data.rows;

        if (isFirst) {
            //判断，当前用户是否 为党员，非党员 无好人榜数据
            if (userType == 2) {
                //取出第一条数据，第一条为当前用户排名信息
                if (goodPeoples != null && goodPeoples.length >= 1) {
                    Log.i(TAG, "initData: goodPeoples.length : " + goodPeoples.length);
                    goodPeople = goodPeoples[0];
                    //设置当前用户页面
                    initView();
                }
                if (goodPeoples != null && goodPeoples.length >= 2) {
                    for (int i = 1; i < goodPeoples.length; i++) {
                        dataList.add(goodPeoples[i]);
                    }
                    adapter.notifyDataSetChanged();
                }
                isFirst = false;
            }else {
                //非党员，不显示 心愿布局
                layoutUser.setVisibility(View.GONE);
                if (goodPeoples != null ) {
                    for (int i = 0; i < goodPeoples.length; i++) {
                        dataList.add(goodPeoples[i]);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

        }else {
            if (goodPeoples != null ) {
                for (int i = 0; i < goodPeoples.length; i++) {
                    dataList.add(goodPeoples[i]);
                }
                adapter.notifyDataSetChanged();
            }
        }

    }

    private void getGoodPeopleData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getGoodPeopleData(pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GoodPeopleData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GoodPeopleData goodPeopleData) {
                        int code = Integer.valueOf(goodPeopleData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        Log.i(TAG, "onNext: rows : " + goodPeopleData.data.rows);
                        if (code == 0){
                            pageCount = goodPeopleData.data.pageCount;
                            pageNo++;

                            //关闭下拉刷新
                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }

                            initData(goodPeopleData);

                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(goodPeopleData.message);
                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == 10){

                            RetrofitUtil.tokenLose(GoodPeopleActivity.this,goodPeopleData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e ; " + e);
                        RetrofitUtil.requestError();

                        if (isRefresh){
                            refreshLayout.finishRefresh(true);
                        }
                        if (isLoadMore){
                            refreshLayout.finishLoadMore(true);
                            isLoadMore = false;
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
