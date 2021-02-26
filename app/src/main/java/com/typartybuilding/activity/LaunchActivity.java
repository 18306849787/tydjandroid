package com.typartybuilding.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.typartybuilding.R;
import com.typartybuilding.activity.loginRelatedActivity.LoginActivity;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.TyUrlData;
import com.typartybuilding.retrofit.GeneralRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ActivityCollectorUtil;
import com.typartybuilding.utils.ExoplayerUtil;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class LaunchActivity extends BaseActivity {

    private String TAG = "LaunchActivity";

    @BindView(R.id.textureView)
    TextureView textureView;     //播放视频
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.textView_skip)
    TextView textSkip;

    private int loginState;
    private Handler handler = new Handler();

    private ExoplayerUtil exoplayerUtil;
    private boolean isPlaying = false;      //是否已经开始播放视频了，

    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private String videoUrl;   //过渡视频
    private String gifUrl = MyApplication.pref.getString(MyApplication.prefKey15_launch_gif,"");     //启动页gif


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        //获取登陆状态
        loginState = MyApplication.pref.getInt(MyApplication.prefKey7_login_state,0);

        //hideStatusBar();
        //changeNavigationBar();
        //加载启动页
        loadData();
        //test();
        //加载图片
        //loadData();
        //获取启动页gif图和过渡视频
       /* if (token != "") {
            getGifVideoUrl();
        }else{
            loadData();      //加载默认gif
        }*/
        getGifVideoUrl();
        //初始化exoplayer
        //initExoplayer();
        //延时进入登录页面
        delaySkip();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCollectorUtil.addActivity(this);
        if (exoplayerUtil != null) {
            //第一次创建页面，不执行；播放过程中切到后台，再进入才执行
            if (isPlaying) {
                if (!exoplayerUtil.isPlaying()) {
                    exoplayerUtil.setPlayWhenReady(true);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoplayerUtil != null) {
            if (exoplayerUtil.isPlaying()) {
                exoplayerUtil.setPlayWhenReady(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null){
            handler.removeCallbacks(mRunable);
            handler = null;
        }
        if (exoplayerUtil != null) {
            exoplayerUtil.release();
        }
    }


    private void initExoplayer(){
        exoplayerUtil = new ExoplayerUtil(textureView,this);
        exoplayerUtil.setVideoUrl(videoUrl);
        //exoplayerUtil.initPlayerForNative();
        exoplayerUtil.initPlayer();
        Log.i(TAG, "initExoplayer: ");

        exoplayerUtil.addEventListener(new Player.EventListener() {
//            @Override
//            public void onTimelineChanged(Timeline timeline, Object manifest) {
//
//            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState){
                    case PlaybackState.STATE_PLAYING :
                        //Log.i(TAG, "onPlayerStateChanged: STATE_PLAYING");
                        progressBar.setVisibility(View.INVISIBLE);

                        break;
                    case PlaybackState.STATE_PAUSED :
                        //Log.i(TAG, "onPlayerStateChanged: STATE_PAUSED");
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    //播放完成
                    case PlaybackState.STATE_FAST_FORWARDING :
                        Log.i(TAG, "onPlayerStateChanged: STATE_FAST_FORWARDING");
                        //视频播放完成，跳转到下一个页面
                        skipNextAc();
                        break;

                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

//            @Override
//            public void onPositionDiscontinuity() {
//
//            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }

    //加载启动页gif 图
    private void loadData(){
        //加载图片
        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.launch_img);
        if (gifUrl != ""){
            Glide.with(this).load(gifUrl)
                    .apply(requestOptions)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                            if (drawable instanceof GifDrawable) {
                                GifDrawable gifDrawable = (GifDrawable) drawable;
                                gifDrawable.setLoopCount(1);
                                imageView.setImageDrawable(drawable);
                                gifDrawable.start();
                            } else {
                                imageView.setImageDrawable(drawable);
                            }
                        }
                    });
        }/*else {
            Glide.with(this).load(gifUrl)
                    .apply(requestOptions)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                            if (drawable instanceof GifDrawable) {
                                GifDrawable gifDrawable = (GifDrawable) drawable;
                                gifDrawable.setLoopCount(1);
                                imageView.setImageDrawable(drawable);
                                gifDrawable.start();
                            } else {
                                imageView.setImageDrawable(drawable);
                            }
                        }
                    });
        }*/

    }

    //跳转到下一个页面
    private void skipNextAc(){
        if (loginState == 1){
            Intent intentAc = new Intent(LaunchActivity.this,HomeAct.class);
            startActivity(intentAc);
        }else {
            Intent intentAc = new Intent(LaunchActivity.this, LoginActivity.class);
            startActivity(intentAc);
        }
        finish();
    }

    //启动页 ，延时进入登陆页面 或 首页
    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            //skipNextAc();
            //启动页面完成，播放视频
            if (exoplayerUtil != null) {
                Log.i(TAG, "run:  exoplayerUtil : " + exoplayerUtil);
                imageView.setVisibility(View.INVISIBLE);
                textureView.setVisibility(View.VISIBLE);
                //跳过按钮可见
                textSkip.setVisibility(View.VISIBLE);
                exoplayerUtil.setPlayWhenReady(true);
                isPlaying = true;
            }else {
                skipNextAc();
            }
        }
    };
    private void delaySkip(){

       handler.postDelayed(mRunable,5000);

    }

    //点击跳过按钮
    @OnClick(R.id.textView_skip)
    public void onClickSkip(){
        skipNextAc();
    }


    private void getGifVideoUrl(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.getUrl(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TyUrlData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TyUrlData tyUrlData) {
                        int code = Integer.valueOf(tyUrlData.code);
                        if (code == 0){
                            videoUrl = tyUrlData.data.index_video;
                            String gifUrl = tyUrlData.data.index_gif;
                            if (videoUrl != null){
                                initExoplayer();
                            }
                            //将gif url存入本地
                            MyApplication.editor.putString(MyApplication.prefKey15_launch_gif,gifUrl);
                            MyApplication.editor.apply();
                            //加载启动页
                            //loadData();
                            if (gifUrl != null&&!LaunchActivity.this.isDestroyed()){
                                Glide.with(LaunchActivity.this).load(gifUrl);
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(tyUrlData.message);
                            //加载启动页
                            //loadData();
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(LaunchActivity.this,tyUrlData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //加载启动页
                        //loadData();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    //用于兼容性测试
    private void test(){
        MyApplication.editor.putInt(MyApplication.pretKey9_login_userId,1);
        MyApplication.editor.putInt(MyApplication.prefKey10_login_userType,1);
        MyApplication.editor.putString(MyApplication.prefKey8_login_token,"1b9fa36c98ab54c8d699eccc268c4ca3");
        MyApplication.editor.putString(MyApplication.prefKey12_login_headImg,"http://47.97.126.122:8080/upload/user/headImg/1_head_img.png");
        MyApplication.editor.putString(MyApplication.prefKey13_login_userName,"2E39B79CC3");
        MyApplication.editor.putString(MyApplication.prefKey5_phone,"15208197594");
        //记录登陆状态
        MyApplication.editor.putInt(MyApplication.prefKey7_login_state,1);
        MyApplication.editor.apply();


        Intent intentAc = new Intent(LaunchActivity.this,HomeAct.class);
        startActivity(intentAc);


    }
}
