package com.typartybuilding.douyin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.typartybuilding.R;
import com.typartybuilding.activity.UserDetailsActivity;
import com.typartybuilding.activity.pbvideo.FindFascinatingActivity;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.pbmicrovideo.FindFascinatingData;
import com.typartybuilding.gsondata.personaldata.TaMicroVideo;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ExoplayerUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MicroVideoPlayActivity extends BaseActivity {

    private String TAG = "MicroVideoPlayActivity";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    //private List<String> urlList = new ArrayList<>();      //测试用

    private List<MicroVideo> dataList = new ArrayList<>();
    private MicroVideoPlayAdapter adapter;

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 30;
    private int visionId;          //用户点击的微视 的id
    private int visionType = 2;    //1：图片，2：视频，3：音频
    private int pageCount;

    private MicroVideo microVideo;    //用户点击的微视
    private int flag;                // 1 表示微视 来自 我的微视或他的微视

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_micro_video_play);
        ButterKnife.bind(this);
        //获取flag
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag",-1);
        //用于获取，推送传递的数据，若为null，即不是推送
        microVideo = (MicroVideo) intent.getSerializableExtra("MicroVideo");
        if (microVideo == null) {
            microVideo = MyApplication.microVideo;
        }

        if (microVideo != null){
            visionId = microVideo.visionId;
            dataList.add(microVideo);
            microVideo.visionBrowseTimes += 1;
        }

        initRecyclerView();
        //获取微视数据
        if (flag == 1){
            getMicroVideo2();
        }else {
            getFindFascinatingData2();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
        changeNavigationBar();
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //判断是否在播放微视频，在播放，页面不可见时 暂停
        if (adapter != null){
            ExoplayerUtil exoplayerUtil = adapter.getCurrentPlayer();
            if (exoplayerUtil != null){
                if (exoplayerUtil.isPlaying()){
                    exoplayerUtil.setPlayWhenReady(false);
                    adapter.getCurrentHolder().btnPlay.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       /* if (adapter != null){
            adapter.getCurrentPlayer().release();
        }*/

        Log.i(TAG, "onDestroy: exoList size : " + adapter.getExoList().size());
        if (adapter != null) {
            for (ExoplayerUtil exoplayerUtil : adapter.getExoList()){
                exoplayerUtil.release();
                exoplayerUtil = null;
                Log.i(TAG, "onDestroy: exoplayerUtil : " + exoplayerUtil);
            }
            adapter.removeAllExo();
            adapter.setExoListNull();
        }
        Log.i(TAG, "onDestroy: exoList : " + adapter.getExoList());
    }

    public void getMoreMicroVideo(){
        if (flag == 1){
            if (pageNo <= pageCount){
                getMicroVideo2();
            }
        }else {
            if (pageNo <= pageCount){
                getFindFascinatingData2();
            }
        }
    }

    private void initRecyclerView(){
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MicroVideoPlayAdapter(dataList,this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerViewPageChangeListenerHelper(snapHelper, new RecyclerViewPageChangeListenerHelper.OnPageChangeListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("MicroVideoPlayAdapter", "onPageSelected: position : " + position);
                adapter.onPageSelected();
            }
        }));

    }

    private void initData(FindFascinatingData findFascinatingData){
        int startItem = dataList.size();
        MicroVideo microVideo[] = findFascinatingData.data.rows;
        if (microVideo != null){
            for (int i = 0; i < microVideo.length; i++){
                dataList.add(microVideo[i]);
                adapter.notifyItemInserted(startItem + i);
            }
        }
    }

    private void getFindFascinatingData2(){

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getFindFascinatingData2(token,pageNo,pageSize,userId,visionId,visionType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FindFascinatingData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FindFascinatingData findFascinatingData) {
                        int code = Integer.valueOf(findFascinatingData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            pageCount = findFascinatingData.data.pageCount;
                            pageNo++;
                            initData(findFascinatingData);

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(findFascinatingData.message);

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MicroVideoPlayActivity.this,findFascinatingData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        //RetrofitUtil.requestError();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 处理 获取的微视列表
     * @param taMicroVideo
     */
    private void initData(TaMicroVideo taMicroVideo){
        int startItem = dataList.size();
        MicroVideo [] rows = taMicroVideo.data.rows;
        if (rows != null){
            for (int i = 0; i < rows.length; i++){
                dataList.add(rows[i]);
                adapter.notifyItemInserted(startItem + i);
            }
        }
    }

    /**
     *   获取微视 列表
     */
    private void getMicroVideo2(){
        int loginId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        //当前微视用户的id
        int userId = -1;
        if (microVideo != null){
            userId = microVideo.userId;
        }
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getMicroVideo2(userId,loginId,pageNo,pageSize,token,visionId,visionType)
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

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(taMicroVideo.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MicroVideoPlayActivity.this,taMicroVideo.message);
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


   /* private void initData(){
        String url1 = "http://39.100.4.85:8080/upload/micro/26/20190731/201907312354442465.mp4";
        String url2 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301330198303.mp4";
        String url3 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301422309949.mp4";
        String url4 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301407473073.mp4";
        String url5 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301406559561.mp4";
        String url6 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301408235845.mp4";
        String url7 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301421077079.mp4";
        String url8 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301403355705.mp4";
        String url9 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301407571601.mp4";
        String url10 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301408418750.mp4";
        String url11 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301407253248.mp4";
        String url12 = "http://39.100.4.85:8080/upload/micro/26/20190730/201907301408123061.mp4";

        urlList.add(url1);
        urlList.add(url2);
        urlList.add(url3);
        urlList.add(url4);
        urlList.add(url5);
        urlList.add(url6);
        urlList.add(url7);
        urlList.add(url8);
        urlList.add(url9);
        urlList.add(url10);
        urlList.add(url11);
        urlList.add(url12);
        urlList.add(url11);
        urlList.add(url2);
        urlList.add(url3);
        urlList.add(url4);
        urlList.add(url5);
        urlList.add(url6);
        urlList.add(url7);
        urlList.add(url10);
        urlList.add(url1);
        urlList.add(url8);
        urlList.add(url9);
        urlList.add(url10);
        urlList.add(url11);
        urlList.add(url1);
        urlList.add(url2);
        urlList.add(url12);
        urlList.add(url11);
        urlList.add(url3);
        urlList.add(url4);
        urlList.add(url5);
        urlList.add(url6);
        urlList.add(url7);
        urlList.add(url11);
        urlList.add(url12);
        urlList.add(url8);
        urlList.add(url9);
        urlList.add(url10);
        urlList.add(url11);
        urlList.add(url12);
    }*/


}
