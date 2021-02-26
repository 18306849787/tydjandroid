package com.typartybuilding.fragment.fgPartyBuildingVideo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.UserDetailsActivity;
import com.typartybuilding.activity.pbvideo.HotBotActivity;
import com.typartybuilding.activity.pbvideo.PopularityListActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.PbvPopularityListAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.pbmicrovideo.PopularityListData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.view.RoundImageView;

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
 * 党建微视页面  人气榜单
 */
public class FragmentPopularityListNew extends BaseFragment {

    private String TAG = "FragmentPopularityList";

    @BindViews({R.id.round_imageView1,R.id.round_imageView2,R.id.round_imageView3,R.id.round_imageView4,R.id.round_imageView5})
    RoundImageView [] imgHeads;
    @BindViews({R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4,R.id.textView5})
    TextView [] textNames;

    private List<PopularityListData.PopularityData> dataList = new ArrayList<>();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

    private boolean isDestroy;

    @Override
    protected void initViews(Bundle savedInstanceState) {

        //获取人气榜数据
        isDestroy = false;
        getPopularityListData();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_video_fragment_popularity_list_new;
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

    //下拉刷新
    public void refreshData(){
        getPopularityListData();
    }

    private void initView(){
        int size = dataList.size();
        for (int i = 0; i < size; i++){
            Glide.with(getActivity()).load(dataList.get(i).headImg)
                .apply(MyApplication.requestOptions2)
                .into(imgHeads[i]);
            textNames[i].setText(dataList.get(i).nickName);
        }
    }

    private void initData(PopularityListData popularityListData){
        if (!isDestroy){

            if (dataList.size() > 0){
                dataList.clear();
            }

            PopularityListData.PopularityData [] popularityData = popularityListData.data;
            if (popularityData != null){
                int length = popularityData.length;
                //只取 5 条数据
                if (length > 5){
                    length = 5;
                }
                for (int i = 0; i < length; i++){
                    dataList.add(popularityData[i]);
                }
                initView();
            }
        }
    }

    private void getPopularityListData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getPopularityListData(userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PopularityListData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PopularityListData popularityListData) {
                        int code = Integer.valueOf(popularityListData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            initData(popularityListData);

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(popularityListData.message);

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),popularityListData.message);
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

    //更多
    @OnClick(R.id.textView_more)
    public void onClickMore(){
        Intent intentAc = new Intent(getActivity(), PopularityListActivity.class);
        startActivity(intentAc);
    }

    //点击头像，跳转到用户详情
    @OnClick({R.id.round_imageView1,R.id.round_imageView2,R.id.round_imageView3,R.id.round_imageView4,R.id.round_imageView5})
    public void onClickHead(View view){
        int size = dataList.size();
        switch (view.getId()){
            case R.id.round_imageView1 :
                if (size >= 1){
                    skipUserDetails(dataList.get(0));
                }
                break;
            case R.id.round_imageView2 :
                if (size >= 2){
                    skipUserDetails(dataList.get(1));
                }
                break;
            case R.id.round_imageView3 :
                if (size >= 3){
                    skipUserDetails(dataList.get(2));
                }
                break;
            case R.id.round_imageView4 :
                if (size >= 4){
                    skipUserDetails(dataList.get(3));
                }
                break;
            case R.id.round_imageView5 :
                if (size >= 5){
                    skipUserDetails(dataList.get(4));
                }
                break;
        }
    }

    private void skipUserDetails(PopularityListData.PopularityData data){
        Intent intentAc = new Intent(getActivity(), UserDetailsActivity.class);
        intentAc.putExtra("userId",data.userId);
        intentAc.putExtra("userName",data.nickName);
        startActivity(intentAc);
    }

}
