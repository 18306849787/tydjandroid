package com.typartybuilding.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.VideoRecommendAdapter;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.RecommendData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ExoplayerUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.utils.ViewUtil;
import com.typartybuilding.view.MyVideoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class PlayVideoDetailActivity extends BaseActivity {

    private String TAG = "PlayVideoDetailActivity";

    @BindView(R.id.videoView)
    TextureView videoView;            //播视频

    @BindView(R.id.imageView_pic)
    ImageView imageView;            //视频图片
    @BindView(R.id.imageView_back)
    ImageView imgBack;              //返回按钮
    @BindView(R.id.imageButton_play)
    ImageButton btnPlay;               //播放按钮
    @BindView(R.id.textView_headline)
    TextView headLine;               //视频标题
    @BindView(R.id.textView_play_times)
    TextView playTimes;              //播放次数
    @BindView(R.id.textView_date)
    TextView textDate;               //日期
    @BindView(R.id.imageButton_collect)
    ImageButton btnCollect;            //收藏按钮
    @BindView(R.id.imageButton_share)
    ImageButton btnShare;              //分享
    @BindView(R.id.textView_abstract)
    TextView videoAbstract;           //简介

    //播放视频
    @BindView(R.id.textView_now_time)
    TextView nowTime;                  //视频当前时间
    @BindView(R.id.textView_play_duration)
    TextView playDuration;             //播放视频时，显示的时长
    @BindView(R.id.seekBar)
    SeekBar seekBar;                   //进度条
    @BindView(R.id.imageView_full_screen)
    ImageView fullScreen;              //全屏的按钮
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;                //等待视频刷新的 进度条
    @BindView(R.id.constraintLayout_seekbar)
    ConstraintLayout layoutSeekBar;      //播放进度条和时间 的布局
    @BindView(R.id.textView_video_headline)
    TextView lcHeadLine;                 //全屏时的标题

    @BindView(R.id.recyclerView_play_video_detail)
    RecyclerView recyclerView;       //感兴趣的视频


    private VideoRecommendAdapter adapter;
    private List<ArticleBanner> bannerList = new ArrayList<>();
    private ArticleBanner banner ;      //上一个activity传递的热门视频数据
    private Handler mHandler = new Handler();

    private boolean isShow;          //进度条和播放按钮是否显示
    private boolean isFullScreen;    //是否是全屏

    private boolean isFirst = true;   //是否是第一次播放，即重新进入改页面
    private boolean isStart = false;      //是否开始播放了

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    private int videoPos;              //视频的当前位置

    private int videoWidth ;           //视频宽度
    private int videoHeight ;          //视频高度
    private float ratio;               //宽高比
    private int height;                //竖屏时，videoview 的高度

    private ExoplayerUtil exoplayerUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_detail);
        ButterKnife.bind(this);
        isFirst = true;
        isStart = false;
        exoplayerUtil = new ExoplayerUtil(videoView,this);

        banner = MyApplication.articleBanner;
        if (banner != null) {
            initView();
            initRecyclerView();
            getRecommendData();
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        showSeekBar();
        startLayout();
        btnPlay.setSelected(false);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
        //取消屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (exoplayerUtil.isPlaying()){
            exoplayerUtil.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.articleBanner = null;

        mHandler.removeCallbacks(mRunnable);
        mHandler.removeCallbacks(mRunnable2);
        exoplayerUtil.release();
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new VideoRecommendAdapter(bannerList,this,1); //1 表示普通视频
        recyclerView.setAdapter(adapter);
    }

    private void initView(){
        if (banner != null) {
            //加载封面图片
            String imgUrl = banner.videoCover;
            Log.i(TAG, "initView: imgurl : " + imgUrl);

            Glide.with(this).load(imgUrl)
                    .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                    .into(imageView);
            headLine.setText(banner.articleTitle);
            playTimes.setText(Utils.formatPlayTimes(banner.browseTimes) + "次播放");
            textDate.setText(Utils.formatDate(banner.publishDate));
            videoAbstract.setText(banner.articleProfile);
            //全屏时的标题
            lcHeadLine.setText(banner.articleTitle);
            //判断是否收藏
            if (banner.isCollect == 1){
                btnCollect.setSelected(true);
            }

            //先获取播放视频控件的高度
            ViewUtil.getViewWidth(videoView, new ViewUtil.OnViewListener() {
                @Override
                public void onView(int mWidth, int mHeight) {
                    height = mHeight;
                    Log.i(TAG, "onView: height : " + height);
                    //初始化播放器
                    setExoplayer();
                }
            });

        }

    }


    private void setExoplayer(){
        //设置exoplayer
        Log.i(TAG, "setExoplayer: videoUrl : " + banner.videoUrl);
        exoplayerUtil.setVideoUrl(banner.videoUrl);
        //设置进度条
        setSeekBar();

        exoplayerUtil.initPlayer();
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
                        Log.i(TAG, "onPlayerStateChanged: start play");
                        //初始化，开始播放时的状态
                        initPlayState();
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case PlaybackState.STATE_PAUSED :
                        if (!isFirst) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        Log.i(TAG, "onPlayerStateChanged: pause");
                        break;
                    //播放完成
                    case PlaybackState.STATE_FAST_FORWARDING :
                        Log.i(TAG, "onPlayerStateChanged: STATE_FAST_FORWARDING  ");
                        exoplayerUtil.repeatPlay();
                        btnPlay.setSelected(false);
                        btnPlay.setVisibility(View.VISIBLE);
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

        exoplayerUtil.addVideoListener(new SimpleExoPlayer.VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                Log.i(TAG, "onVideoSizeChanged: width : " + width);
                Log.i(TAG, "onVideoSizeChanged: height : " + height);
                videoWidth = width;
                videoHeight = height;
                refreshVideoViewSize();
            }

            @Override
            public void onRenderedFirstFrame() {

            }
        });

    }


    private void initPlayState(){
        //获取视频时长
        long duration = exoplayerUtil.getDuration();
        int mCurrentPosition = (int) exoplayerUtil.getCurrentPosition();
        Log.i(TAG, "duration : " + duration);
        seekBar.setMax((int)duration);

        nowTime.setText(Utils.formatTime(mCurrentPosition/1000));
        playDuration.setText(Utils.formatTime((int)duration/1000));
        updateSeekBar();
        //隐藏进度条
        delayHideSeekBar();
    }

    private void setSeekBar(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (videoView != null) {
                    mHandler.removeCallbacks(mRunnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (videoView != null) {
                    mHandler.removeCallbacks(mRunnable);
                    int progress = seekBar.getProgress();
                    exoplayerUtil.seekTo(progress);

                    updateSeekBar();

                }
            }
        });
    }

    //根据视频尺寸，重新设置VideoView的宽高；竖屏时设置，横屏屏时，全屏播放
    private void refreshVideoViewSize(){
        ratio = (float)videoWidth/(float)videoHeight;
        Log.i(TAG, "refreshVideoViewSize: ratio : " + ratio);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();

        int height = 0;
        if (isFullScreen){
            height = videoView.getHeight();
        }else {
            height = this.height;
        }

        params.height = height;
        params.width = (int)(height*ratio);
        videoView.setLayoutParams(params);

        Log.i(TAG, "refreshVideoViewSize: params.height: " + params.height);
        Log.i(TAG, "refreshVideoViewSize: params.width : " + params.width);

    }

    @OnClick(R.id.imageButton_play)
    public void onClickPlay(){
        if (banner != null) {
            if (isFirst) {
                //增加浏览次数
                //添加浏览历史记录，更新浏览量/播放量 1：咨询文章，2：党建微视
                RetrofitUtil.addBrowsing(1, banner.articleId);
                banner.browseTimes += 1;

                isFirst = false;
                isStart = true;
            }

            if (!exoplayerUtil.isPlaying()) {
                btnPlay.setSelected(true);
                updateSeekBar();
                hideSeekBar();
                imageView.setVisibility(View.INVISIBLE);
                //videoDuration.setVisibility(View.INVISIBLE);
                exoplayerUtil.setPlayWhenReady(true);
            } else {
                btnPlay.setSelected(false);
                exoplayerUtil.setPlayWhenReady(false);
            }
        }else {
            Toast.makeText(this,"视频数据为空",Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.videoView)
    public void onClickExo(){
        if (isStart) {
            if (isShow) {
                hideSeekBar();
            } else {
                showSeekBar();
                if (exoplayerUtil.isPlaying()) {
                    delayHideSeekBar();
                }
            }
        }
    }

    //分享
    @OnClick(R.id.imageButton_share)
    public void onClickShare(){
        if (banner == null){
            return;
        }
        if (userType == 1 || userType == 2) {

            JshareUtil.showBroadView(this,banner.articleTitle,banner.articleProfile,banner.videoUrl,
                    1,banner.articleId);

        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    /**
     * 收藏按钮
     */
    @OnClick(R.id.imageButton_collect)
    public void onCollect(){
        if (banner == null){
            return;
        }
        if (userType == 1 || userType == 2) {
            //取消收藏
            if (btnCollect.isSelected()) {
                btnCollect.setSelected(false);
                int code = RetrofitUtil.delCollect(banner.articleId);
                banner.isCollect = 0;
                Log.i(TAG, "onCollect: code : " + code);

            //收藏
            } else {
                btnCollect.setSelected(true);
                int code = RetrofitUtil.addCollect(banner.articleId);
                banner.isCollect = 1;
                Log.i(TAG, "onCollect: code : " + code);
            }
        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    private void getRecommendData(){
        int articleType = banner.articleType;
        int articleId = banner.articleId;
        int urlType = Integer.valueOf(banner.urlType);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getRecommendData(articleType, articleId,urlType,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RecommendData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RecommendData recommendData) {
                        int code = Integer.valueOf(recommendData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            loadData(recommendData);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(recommendData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(PlayVideoDetailActivity.this,recommendData.message);
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

    private void loadData(RecommendData recommendData){
        int size = recommendData.data.length;
        Log.i(TAG, "loadData: size : " + size);
        for (int i = 0; i < size; i++){
            bannerList.add(recommendData.data[i]);
        }
        adapter.notifyDataSetChanged();

    }


    /**
     * 横竖屏切换
     */
    @OnClick(R.id.imageView_full_screen)
    public void changeScreen(){
        if (!fullScreen.isSelected()){
            //切换为横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            fullScreen.setSelected(true);
            isFullScreen = true;
            //全屏时，显示标题
            lcHeadLine.setVisibility(View.VISIBLE);
            //更换播放暂停按钮
            btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ac_play_video_detail_selector2));
        }else {
            //切换为竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullScreen.setSelected(false);
            isFullScreen = false;
            //竖屏时，隐藏标题
            lcHeadLine.setVisibility(View.INVISIBLE);
            //更换播放暂停按钮
            btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ac_play_video_detail_selector1));
        }
    }


    /**
     * 更新进度条
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(videoView != null){
                int mCurrentPosition = (int) exoplayerUtil.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);
                nowTime.setText(Utils.formatTime(mCurrentPosition/1000));
                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }

    /**
     * 延时隐藏进度条
     */
    private Runnable mRunnable2 = new Runnable() {
        @Override
        public void run() {
            if (exoplayerUtil.isPlaying()) {
                layoutSeekBar.setVisibility(View.INVISIBLE);
                btnPlay.setVisibility(View.INVISIBLE);
                isShow = false;
            }
        }
    };
    private void delayHideSeekBar(){
        mHandler.removeCallbacks(mRunnable2);
        mHandler.postDelayed(mRunnable2,3000);
    }

    //第一进入页面，进度条不可见，播放按钮可见
    private void startLayout(){
        layoutSeekBar.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.VISIBLE);
    }

    //显示进度条
    private void showSeekBar(){
        layoutSeekBar.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.VISIBLE);
        isShow = true;
    }
    //隐藏进度条
    private void hideSeekBar(){
        layoutSeekBar.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.INVISIBLE);
        isShow = false;
    }



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isFullScreen){
            //切换为竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullScreen.setSelected(false);
            isFullScreen = false;
        }else {
            finish();
        }
    }

    //反回按钮
    @OnClick(R.id.imageView_back)
    public void onClickBack(){
        if (isFullScreen){
            //切换为竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullScreen.setSelected(false);
            isFullScreen = false;
        }else {
            finish();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //配置文件中设置 android:configChanges="orientation|screenSize|keyboardHidden" 不然横竖屏切换的时候重新创建又重新播放
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            if (ratio > 1) {
                /*ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                videoView.setLayoutParams(params);*/
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
                int height = Utils.getScreenHeight();
                params.height = height;
                params.width = (int)(height*ratio);
                videoView.setLayoutParams(params);

            }else if (ratio <= 1){
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
                int height = Utils.getScreenHeight();
                params.height = height;
                params.width = (int)(height*ratio);
                videoView.setLayoutParams(params);
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//显示顶部状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            if (ratio > 1) {
               /* ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                params.dimensionRatio = "16:9";
                videoView.setLayoutParams(params);*/
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
                params.height = height;
                params.width = (int)(height*ratio);
                videoView.setLayoutParams(params);

            }else if (ratio <= 1){
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
                params.height = height;
                params.width = (int)(height*ratio);
                videoView.setLayoutParams(params);
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏不 显示顶部状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            fullScreen.setSelected(false);
            isFullScreen = false;
            //竖屏时，隐藏标题
            lcHeadLine.setVisibility(View.INVISIBLE);
            //更换播放暂停按钮
            btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ac_play_video_detail_selector1));

        }
        System.out.println("======onConfigurationChanged===");
    }


    /* *//**
     * 设置videoview
     *//*
    private void setVideoView(){
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.INVISIBLE);
                //获取视频时长
                int duration = videoView.getDuration();
                int mCurrentPosition = videoView.getCurrentPosition();
                Log.i(TAG, "startPlay: duration : " + duration);
                seekBar.setMax(duration);
                nowTime.setText(Utils.formatTime(mCurrentPosition/1000));
                playDuration.setText(Utils.formatTime(duration/1000));
                updateSeekBar();
                //隐藏进度条
                delayHideSeekBar();

                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        videoWidth = mp.getVideoWidth();
                        videoHeight = mp.getVideoHeight();
                        refreshVideoViewSize();
                    }
                });
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isShow){
                    hideSeekBar();
                }else {
                    showSeekBar();
                    if (videoView.isPlaying()) {
                        delayHideSeekBar();
                    }
                }

                //mHandler.removeCallbacks(mRunnable);

                return false;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setSelected(false);
                showSeekBar();
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                    progressBar.setVisibility(View.VISIBLE);
                    hideSeekBar();
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    //暂停时，显示进度条
                    if (!btnPlay.isSelected()){
                        showSeekBar();
                    }
                }
                return true;
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                Log.e(TAG, "onError: what : " + what);
                Log.e(TAG, "onError: extra : " + extra );
                videoView.stopPlayback();
                isFirst = true;
                btnPlay.setSelected(false);

                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED){
                    Toast.makeText(MyApplication.getContext(),"网络服务错误",Toast.LENGTH_LONG).show();
                }else if(what == MediaPlayer.MEDIA_ERROR_UNKNOWN){
                    if (extra == MediaPlayer.MEDIA_ERROR_IO){
                        Toast.makeText(MyApplication.getContext(), "网络文件错误", Toast.LENGTH_LONG).show();

                    }else if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT){
                        Toast.makeText(MyApplication.getContext(), "网络超时", Toast.LENGTH_LONG).show();
                    }
                }


                return true;
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (videoView != null) {
                    mHandler.removeCallbacks(mRunnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (videoView != null) {
                    mHandler.removeCallbacks(mRunnable);
                    int progress = seekBar.getProgress();
                    videoView.seekTo(progress);

                    updateSeekBar();

                }
            }
        });
    }*/


}


















