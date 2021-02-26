package com.typartybuilding.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.PersonalMicVideoAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
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
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class UserDetailsActivity extends WhiteTitleBaseActivity {

    private String TAG = "UserDetailsActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;              //页面标题  ,用户name
    @BindView(R.id.circleImageView)
    CircleImageView imgHead;         //头像

    @BindView(R.id.imageView_attention)
    ImageView imgAttention;          //点击关注
    @BindViews({R.id.textView1_top, R.id.textView2_top, R.id.textView3_top})
    TextView textView[] ;            //关注，粉丝，获赞

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;    //下拉刷新，上拉加载
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private PersonalMicVideoAdapter adapter;
    private List<MicroVideo> dataList = new ArrayList<>();

    private int userId;           //Ta 的id
    private String userName;      //Ta 的用户名
    private String followedUserImg;

    private int loginId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    private int pageNo = 1;
    private int pageSize = 18;
    private int pageCount;   //总页数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        ButterKnife.bind(this);
        //获取上一个activity 传递的useriId
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId",-1);
        userName = intent.getStringExtra("userName");
        //设置标题，为用户名
        textTitle.setText("");

        initRecyclerView();
        //获取微视
        getMicroVideo();
        //设置上拉加载更多
        setRefreshLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取用户微视信息
        getMicroInfo();

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PersonalMicVideoAdapter(dataList,this,0);   //0 表示ta 的微视， 1 表示我的微视
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

    @OnClick(R.id.imageView_attention)
    public void onClickAttention() {
        if (userType == 1 || userType == 2) {
            //关注
            RetrofitUtil.addFocus(userId, userName, followedUserImg,imgAttention);
        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    /**
     * 显示微视信息
     * @param taMicro
     */
    private void initView(TaMicro taMicro){
        //加载头像
        Log.i(TAG, "initView: taMicro.data.headImg : " + taMicro.data.headImg);
        Glide.with(MyApplication.getContext()).load(taMicro.data.headImg)
                .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                .into(imgHead);

        textTitle.setText(taMicro.data.userName);
        //关注，粉丝，获赞
        textView[0].setText(Utils.formatPlayTimes(taMicro.data.userFollowNum));
        textView[1].setText(Utils.formatPlayTimes(taMicro.data.userFollowedNum));
        textView[2].setText(Utils.formatPlayTimes(taMicro.data.userPraisedNum));
        Log.i(TAG, "initView: taMicro.data.isFollowed : " + taMicro.data.isFollowed);
        //是否关注 0：否，1：是
        if (taMicro.data.isFollowed == 1){
            imgAttention.setVisibility(View.INVISIBLE);
        }else {
            imgAttention.setVisibility(View.VISIBLE);
        }

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
        }
    }

    /**
     * 获取 ta 的微视 信息
     */
    private void getMicroInfo(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getMicroInfo(userId,loginId,token)
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
                            followedUserImg = taMicro.data.headImg;
                            initView(taMicro);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(taMicro.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(UserDetailsActivity.this,taMicro.message);
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
        personalRetrofit.getMicroVideo(userId,loginId,pageNo,pageSize,token)
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
                            RetrofitUtil.tokenLose(UserDetailsActivity.this,taMicroVideo.message);
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
