package com.typartybuilding.fragment.fgPartyBuildingVideo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.PbHotBotActivity;
import com.typartybuilding.activity.pbvideo.HotBotActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.PbvHotBotAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.pbmicrovideo.HotWordData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;

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
 * 党建微视页面  党建热搜
 */
public class FragmentHotBotNew extends BaseFragment {

    private String TAG = "FragmentHotBotNew" ;

    @BindViews({R.id.textView1, R.id.textView2, R.id.textView3})
    TextView [] textViews;
    @BindViews({R.id.constraintLayout1, R.id.constraintLayout2, R.id.constraintLayout3,})
    ConstraintLayout [] constraintLayouts;    //三个榜单的布局
    @BindViews({R.id.view1, R.id.view2})
    View [] views;                            //分割线

    private List<HotWordData.HotWord> dataList = new ArrayList<>();

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 3;

    private boolean isDestroy;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //获取热搜数据
        isDestroy = false;
        getHotWordData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_video_fragment_hot_bot_new;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
    }

    //下拉刷新
    public void refreshData(){
        getHotWordData();
    }

    private void initView(){
        if (dataList.size() == 0){
            //榜单布局不可见
            constraintLayouts[0].setVisibility(View.GONE);
            constraintLayouts[1].setVisibility(View.GONE);
            constraintLayouts[2].setVisibility(View.GONE);
            views[0].setVisibility(View.GONE);
            views[1].setVisibility(View.GONE);
        }else if (dataList.size() == 1){
            textViews[0].setText(dataList.get(0).hotWord);

            constraintLayouts[0].setVisibility(View.VISIBLE);
            constraintLayouts[1].setVisibility(View.GONE);
            constraintLayouts[2].setVisibility(View.GONE);
            views[0].setVisibility(View.GONE);
            views[1].setVisibility(View.GONE);
        }else if (dataList.size() == 2){
            textViews[0].setText(dataList.get(0).hotWord);
            textViews[1].setText(dataList.get(1).hotWord);

            constraintLayouts[0].setVisibility(View.VISIBLE);
            constraintLayouts[1].setVisibility(View.VISIBLE);
            constraintLayouts[2].setVisibility(View.GONE);
            views[0].setVisibility(View.VISIBLE);
            views[1].setVisibility(View.GONE);

        }else if (dataList.size() == 3){
            textViews[0].setText(dataList.get(0).hotWord);
            textViews[1].setText(dataList.get(1).hotWord);
            textViews[2].setText(dataList.get(2).hotWord);

            constraintLayouts[0].setVisibility(View.VISIBLE);
            constraintLayouts[1].setVisibility(View.VISIBLE);
            constraintLayouts[2].setVisibility(View.VISIBLE);
            views[0].setVisibility(View.VISIBLE);
            views[1].setVisibility(View.VISIBLE);
        }
    }

    private void initData(HotWordData hotWordData){
        if (!isDestroy){

            if (dataList.size() > 0){
                dataList.clear();
            }

            HotWordData.HotWord hotWords[] = hotWordData.data.rows;
            if (hotWords != null){
                for (int i = 0; i < hotWords.length; i++){
                    dataList.add(hotWords[i]);
                }
                //初始化页面
                initView();
            }
        }

    }

    private void getHotWordData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getHotWordData(pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HotWordData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HotWordData hotWordData) {
                        int code = Integer.valueOf(hotWordData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            initData(hotWordData);

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(hotWordData.message);

                        }else if (code == 10){
                            Log.i(TAG, "onNext: message : " + hotWordData.message);
                            RetrofitUtil.tokenLose(getActivity(),hotWordData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e ; " + e);
                        RetrofitUtil.requestError();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @OnClick(R.id.textView_more)
    public void onClickMore(){
        Intent intentAc = new Intent(getActivity(), HotBotActivity.class);
        startActivity(intentAc);
    }

    @OnClick({R.id.constraintLayout1, R.id.constraintLayout2, R.id.constraintLayout3})
    public void onClickItem(View view){
        switch (view.getId()){
            case R.id.constraintLayout1 :
                skipPbHotBotActivity(textViews[0].getText().toString());
                break;
            case R.id.constraintLayout2 :
                skipPbHotBotActivity(textViews[1].getText().toString());
                break;
            case R.id.constraintLayout3 :
                skipPbHotBotActivity(textViews[2].getText().toString());
                break;
        }
    }

    private void skipPbHotBotActivity(String hotWord){
        Intent intentAc = new Intent(getActivity(), PbHotBotActivity.class);
        intentAc.putExtra("hotWord", hotWord);
        startActivity(intentAc);
    }


}
