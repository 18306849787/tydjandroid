package com.typartybuilding.activity.myRelatedActivity;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.MixtureDataAdapterMy;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralMixtureData;
import com.typartybuilding.gsondata.MixtureData;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.SwipeItemLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;

public class MyFootprintActivity extends RedTitleBaseActivity {

    private String TAG = "MyFootprintActivity";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;    //下拉刷新，上拉加载

    @BindView(R.id.imageView_no)
    ImageView imageViewNo;       //没有关注

    private MixtureDataAdapterMy adapter;
    private List<MixtureData> dataList = new ArrayList<>();

    private int pageNo = 1;
    private int pageSize = 20;
    private int pageCount;

    private PopupWindow popupWindow;  //弹窗  ，提醒用户 是否要 清除所有足迹
    private View popView;             //弹窗布局
    private Button btnConfirm;       //弹窗确认按钮
    private Button btnCancel;        //弹窗取消按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_footprint);
        ButterKnife.bind(this);

        initRecyclerView();
        getMyFootprint();
        //初始化，删除足迹的弹窗
        initPopupWindow();
        //设置上拉加载更多
        setRefreshLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //设置侧滑 点击监听
        recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        /*adapter = new MixtureDataAdapter(dataList,this);
        adapter.setFlag(1);*/
        adapter = new MixtureDataAdapterMy(dataList,this,2);//2，表示我的足迹
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);
    }

    private void setRefreshLayout(){
        //禁止下拉刷新
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount ){
                    getMyFootprint();
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
    }

    private void initData(GeneralMixtureData generalMixtureData){

        int startItem = dataList.size();
        MixtureData data[] = generalMixtureData.data.rows;
        if (data != null){
            for (int i = 0; i < data.length; i++) {
                dataList.add(data[i]);
                //adapter.notifyItemInserted(startItem + i);
            }
            int itemCount = dataList.size() - startItem;
            adapter.notifyItemRangeInserted(startItem,itemCount);
            //adapter.notifyDataSetChanged();

            recyclerView.setVisibility(View.VISIBLE);
            imageViewNo.setVisibility(View.INVISIBLE);
        }else {
            recyclerView.setVisibility(View.INVISIBLE);
            imageViewNo.setVisibility(View.VISIBLE);
        }

    }

    private void getMyFootprint(){
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getMyFootprint(pageNo,pageSize,userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralMixtureData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralMixtureData generalMixtureData) {
                        int code = Integer.valueOf(generalMixtureData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            pageCount = generalMixtureData.data.pageCount;
                            pageNo++;
                            initData(generalMixtureData);

                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }


                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalMixtureData.message);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyFootprintActivity.this,generalMixtureData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        if (e instanceof HttpException){
                            HttpException exception = (HttpException)e;
                            try {
                                String json = exception.response().errorBody().string();
                                Log.i(TAG, "onError: json ： " + json);
                            }catch (IOException E){

                            }
                        }

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

    @OnClick(R.id.imageView_back)
    public void onClickBack(){
        finish();
    }

    @OnClick(R.id.textView_delAll)
    public void onClickDelAll(){
        showPopupWindow();
        //delAllFootprint();
    }

    public void delSingleFootprint(int position){
        if (adapter != null){
            Log.i(TAG, "delSingleFootprint: size : " + dataList.size());
            dataList.remove(position);
            Log.i(TAG, "delSingleFootprint: size : " + dataList.size());
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, dataList.size());
        }
    }

    private void delAllFootprint(){

        if (dataList != null){
            dataList.clear();
            RetrofitUtil.delAllFootprint();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.INVISIBLE);
            imageViewNo.setVisibility(View.VISIBLE);
        }

    }

    //初始化弹窗
    private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_delete_mic_video, null);
        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnConfirm = popView.findViewById(R.id.button_comfirm);
        btnCancel = popView.findViewById(R.id.button_cancel);

        TextView textTitle = popView.findViewById(R.id.textView_phonenum);
        TextView text = popView.findViewById(R.id.textView_reminder);
        textTitle.setText("清除足迹");
        text.setText("确认清除所有足迹吗？");

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(MyFootprintActivity.this,1f);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

    }

    private void showPopupWindow(){
        if (!popupWindow.isShowing()){
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
        //确认删除所有 足迹
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delAllFootprint();
                popupWindow.dismiss();
            }
        });
    }


}
