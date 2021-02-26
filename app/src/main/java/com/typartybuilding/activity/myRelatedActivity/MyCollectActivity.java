package com.typartybuilding.activity.myRelatedActivity;

import android.support.annotation.NonNull;
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
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.MixtureDataAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.MixtureDataAdapterMy;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.MixtureData;
import com.typartybuilding.gsondata.GeneralMixtureData;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MyCollectActivity extends RedTitleBaseActivity {

    private String TAG = "MyCollectActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;    //下拉刷新，上拉加载

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.imageView_no)
    ImageView imageViewNo;       //没有收藏

    private MixtureDataAdapterMy adapter;
    private List<MixtureData> dataList = new ArrayList<>();

    private int pageNo = 1;
    private int pageSize = 20;
    private int pageCount;

    private PopupWindow popupWindow;  //弹窗  ，提醒用户 是否要 取消收藏
    private View popView;             //弹窗布局
    private Button btnConfirm;       //弹窗确认按钮
    private Button btnCancel;        //弹窗取消按钮

    public boolean isLoadFinished;   //是否已经 加载完所有页了，避免在删除时，又取加载数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        ButterKnife.bind(this);
        textTitle.setText("我的收藏");
        initRecyclerView();
        getCollectData();
        //初始化，取消收藏的弹窗
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
       /* adapter = new MixtureDataAdapter(dataList,this);
        adapter.setFlag(1);*/
        adapter = new MixtureDataAdapterMy(dataList,this,1);//1 表示我的收藏
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
                    getCollectData();
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
        Log.i(TAG, "initData: data : " + data);
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

    private void getCollectData(){
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getCollectData(pageNo,pageSize,userId,token)
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
                            RetrofitUtil.tokenLose(MyCollectActivity.this,generalMixtureData.message);
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

    private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_delete_mic_video, null);
        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnConfirm = popView.findViewById(R.id.button_comfirm);
        btnCancel = popView.findViewById(R.id.button_cancel);

        TextView textTitle = popView.findViewById(R.id.textView_phonenum);
        TextView text = popView.findViewById(R.id.textView_reminder);
        textTitle.setText("取消收藏");
        text.setText("确认取消收藏吗？");

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(MyCollectActivity.this,1f);
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

    /**
     * 显示弹窗，删除微视类 收藏
     */
    public void delMicVision(int position,int visionId,int visionUid){
        if (!popupWindow.isShowing()){
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
        //确认删除微视频
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求后台删除收藏
                RetrofitUtil.cancelPraise(visionId,visionUid);
                //刷新页面
                refreshLayout(position);
                popupWindow.dismiss();
            }
        });
    }

    /**
     *  显示弹窗，删除资讯类 收藏
     * @param position
     * @param collectTargetId
     */
    public void delArticle(int position,int collectTargetId){
        if (!popupWindow.isShowing()){
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
        //确认删除 资讯类 收藏
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求后台删除收藏
                RetrofitUtil.delCollect(collectTargetId);
                //刷新页面
                refreshLayout(position);
                popupWindow.dismiss();
            }
        });
    }

    private void refreshLayout(int position){
        if (adapter != null){
            dataList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, dataList.size());

            Log.i(TAG, "refreshLayout: 刷新页面position : " + position);
        }
    }

}
