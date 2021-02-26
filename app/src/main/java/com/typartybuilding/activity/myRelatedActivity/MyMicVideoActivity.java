package com.typartybuilding.activity.myRelatedActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.PersonalMicVideoAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.personaldata.TaMicro;
import com.typartybuilding.gsondata.personaldata.TaMicroVideo;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MyMicVideoActivity extends WhiteTitleBaseActivity {

    private String TAG = "MyMicVideoActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;              //页面标题  ,用户name
    @BindView(R.id.circleImageView)
    CircleImageView imgHead;         //头像

    @BindViews({R.id.textView1_top, R.id.textView2_top, R.id.textView3_top})
    TextView textView[] ;            //关注，粉丝，获赞

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;    //下拉刷新，上拉加载
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.imageView_no)
    ImageView imageViewNo;       //没有微视

    public PersonalMicVideoAdapter adapter;
    public List<MicroVideo> dataList = new ArrayList<>();

    private int loginId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private int pageNo = 1;
    private int pageSize = 18;
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

    private int pageCount;   //总页数

    private PopupWindow popupWindow;  //弹窗  ，提醒用户 是否要删除 微视频
    private View popView;             //弹窗布局
    private Button btnConfirm;       //弹窗确认按钮
    private Button btnCancel;        //弹窗取消按钮


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mic_video);
        ButterKnife.bind(this);
        textTitle.setText("我的微视");
        initRecyclerView();
        initPopupWindow();
        //获取用户微视信息
        getMicroInfo();
        //获取微视
        getMicroVideo();
        //设置上拉加载更多
        setRefreshLayout();

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PersonalMicVideoAdapter(dataList,this,1);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line_user_details_ac));
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
                    getMicroVideo();
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
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

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(MyMicVideoActivity.this,1f);
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
     * 显示弹窗
     */
    public void showPopupWindow(int delIndex,int visionId){
        if (!popupWindow.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
        //确认删除微视频
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求后台
                delMicro(delIndex,visionId);
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 显示微视信息
     * @param taMicro
     */
    private void initView(TaMicro taMicro){
        //加载头像
        String headimg = MyApplication.pref.getString(MyApplication.prefKey12_login_headImg,"");
        Glide.with(MyApplication.getContext()).load(headimg)
                .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                .into(imgHead);
        //关注，粉丝，获赞
        textView[0].setText(Utils.formatPlayTimes(taMicro.data.userFollowNum));
        textView[1].setText(Utils.formatPlayTimes(taMicro.data.userFollowedNum));
        textView[2].setText(Utils.formatPlayTimes(taMicro.data.userPraisedNum));

    }

    /**
     * 处理 获取的微视列表
     * @param taMicroVideo
     */
    private void initData(TaMicroVideo taMicroVideo){
        int startItem = dataList.size()/3;
        MicroVideo [] rows = taMicroVideo.data.rows;
        if (rows != null){
            for (int i = 0; i < rows.length; i++){
                dataList.add(rows[i]);
            }
            int itemCount = 0;
            int size = dataList.size();
            int n = size % 3;
            if (n == 0){
                itemCount = size/3;
            }else {
                itemCount = size/3 + 1;
            }
            adapter.notifyItemRangeInserted(startItem,itemCount);
            //adapter.notifyDataSetChanged();
            //有微视时
            recyclerView.setVisibility(View.VISIBLE);
            imageViewNo.setVisibility(View.INVISIBLE);
        }else {
            //没有微视时
            recyclerView.setVisibility(View.INVISIBLE);
            imageViewNo.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 获取 微视 信息
     */
    private void getMicroInfo(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getMicroInfo(loginId,null,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TaMicro>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TaMicro taMicro) {
                        int code = Integer.valueOf(taMicro.code);
                        if (code == 0){
                            //followedUserImg = taMicro.data.headImg;
                            initView(taMicro);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(taMicro.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyMicVideoActivity.this,taMicro.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     *   获取微视 列表
     */
    private void getMicroVideo(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getMicroVideo(loginId,loginId,pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TaMicroVideo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TaMicroVideo taMicroVideo) {
                        int code = Integer.valueOf(taMicroVideo.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            //获取总页数
                            pageCount = taMicroVideo.data.pageCount;
                            initData(taMicroVideo);
                            pageNo++;

                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(taMicroVideo.message);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyMicVideoActivity.this,taMicroVideo.message);
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

    /**
     *  删除作品
     * @param visionId
     */
    private void delMicro(int delIndex,int visionId){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.delMicro(visionId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        int code = Integer.valueOf(generalData.code);
                        if (code == 0){
                            //删除成功，刷新页面
                            adapter.deleteMicVideo(delIndex);
                            adapter.notifyDataSetChanged();
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyMicVideoActivity.this,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
