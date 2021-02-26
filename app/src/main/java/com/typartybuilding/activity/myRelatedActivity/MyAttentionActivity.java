package com.typartybuilding.activity.myRelatedActivity;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.MyAttentionAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.personaldata.MyFocusData;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MyAttentionActivity extends RedTitleBaseActivity {

    private String TAG = "MyAttentionActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;            //标题

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;    //下拉刷新，上拉加载
    @BindView(R.id.recyclerView_my_attention)
    RecyclerView recyclerView;
    @BindView(R.id.imageView_no)
    ImageView imageViewNo;       //没有关注


    private List<MyFocusData.FocusPeople> dataList = new ArrayList<>();
    private MyAttentionAdapter adapter;

    private boolean isDestroy;

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 20;
    private int pageCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_attention);
        ButterKnife.bind(this);
        textTitle.setText("我的关注");
        isDestroy = false;
        initRecyclerView();
        getFocusData();
        //设置上拉加载更多
        setRefreshLayout();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAttentionAdapter(dataList,this);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);
    }

    private void setRefreshLayout(){
        //禁止下拉刷新
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount ){
                    getFocusData();
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
    }

    private void initData(MyFocusData myFocusData){
        if (!isDestroy) {
            int startItem = dataList.size();
            MyFocusData.FocusPeople focusPeople[] = myFocusData.data.rows;
            Log.i(TAG, "initData: focusPeople[] : " + focusPeople);
            if (focusPeople != null) {
                for (int i = 0; i < focusPeople.length; i++) {
                    dataList.add(focusPeople[i]);
                }
                int itemCount = dataList.size() - startItem;
                adapter.notifyItemRangeInserted(startItem,itemCount);
                //adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                imageViewNo.setVisibility(View.INVISIBLE);
            } else {
                recyclerView.setVisibility(View.INVISIBLE);
                imageViewNo.setVisibility(View.VISIBLE);
            }

        }

    }

    private void getFocusData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getFocusData(userId,pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MyFocusData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MyFocusData myFocusData) {
                        int code = Integer.valueOf(myFocusData.code);
                        Log.i(TAG, "onNext: code ; " + code);
                        if (code == 0){
                            pageCount = myFocusData.data.pageCount;
                            pageNo++;
                            initData(myFocusData);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(myFocusData.message);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyAttentionActivity.this,myFocusData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //关闭上拉加载更多
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
