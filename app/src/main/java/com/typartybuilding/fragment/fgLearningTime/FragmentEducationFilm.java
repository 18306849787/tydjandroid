package com.typartybuilding.fragment.fgLearningTime;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
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
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.LearnTimeAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.LearnTimeEduFilmAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.HomeFragmentLearningTime;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.ArticleVideoData;
import com.typartybuilding.gsondata.learntime.EducationFilmData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ExoplayerUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 学习时刻    适用于  学习新思想，党员教育片，时代先锋
 */
public class FragmentEducationFilm extends BaseFragment {

    private String TAG = "FragmentEducationFilm";

    @BindView(R.id.nestedScrollView)
    NestedScrollView scrollView;

    @BindView(R.id.exoPlayerView)
    TextureView videoView;             //播视频

    @BindView(R.id.imageView_pic)
    ImageView imageView;            //视频图片
    @BindView(R.id.imageView_back)
    ImageView imgBack;              //返回按钮
    @BindView(R.id.imageButton_play)
    ImageButton btnPlay;               //播放按钮

    @BindView(R.id.textView_video_duration)
    TextView videoDuration;            //视频时长

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

    //播放视频
    @BindView(R.id.textView_now_time)
    TextView nowTime;                  //视频当前时间
    @BindView(R.id.textView_play_duration)
    TextView playDuration;             //播放视频时，显示的时长
    @BindView(R.id.seekBar)
    SeekBar seekBar;                      //进度条
    @BindView(R.id.imageView_full_screen)
    public ImageView fullScreen;                    //全屏的按钮
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;                  //等待视频刷新的 进度条
    @BindView(R.id.constraintLayout_seekbar)
    ConstraintLayout layoutSeekBar;          //播放进度条和时间 的布局
    @BindView(R.id.textView_video_headline)
    TextView lcHeadLine;                    //全屏时的标题

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;    //下拉刷新，上拉加载
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;     //推荐视频列表

    //全屏时 隐藏
    @BindView(R.id.constraintLayout_abstract)
    ConstraintLayout layoutAbstract;            //简介布局
    @BindView(R.id.view)
    View view;                                 //分割线

    private Handler mHandler = new Handler();
    private boolean isShow;          //进度条和播放按钮是否显示
    public boolean isFullScreen;    //是否是全屏

    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    private List<EducationFilmData.EducationFilm> dataList = new ArrayList<>();
    private LearnTimeEduFilmAdapter adapter;

    private ArticleBanner banner ;    //第一条数据，置顶视频

    private HomeFragmentLearningTime parentFg;
    private HomeActivity activity;

    private boolean isDestroy ;         //网络请求后，会刷新该页面，刷新时，需判断页面是否销毁
    private boolean isFirst ;          //是否是第一次播放，即重新进入改页面
    private boolean isStart ;          //是否开始播放了

    private int videoWidth ;           //视频宽度
    private int videoHeight ;          //视频高度
    private float ratio;               //宽高比
    private int height;                //竖屏时，videoview 的高度

    private ExoplayerUtil exoplayerUtil;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //获取 父Fragment ， HomeFragmentLearningTime
        parentFg = (HomeFragmentLearningTime)getParentFragment();
        activity = (HomeActivity)getActivity();
        initRecyclerView();

        exoplayerUtil = new ExoplayerUtil(videoView,getActivity());

        //重新加载，集合清零，置顶视频对象置为 null
        if (dataList.size() > 0){
            dataList.clear();
        }
        banner = null;
        //初始化，标记
        isDestroy = false;
        isFirst = true;
        isStart = false;
        //设置下拉刷新
        setRefreshLayout();
        //获取 视频分类 数据
        getEducationFilmData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_learning_time_fragment_education_film;
    }

    @Override
    public void onResume() {
        super.onResume();
        showSeekBar();
        startLayout();

        if (btnPlay != null) {
            btnPlay.setSelected(false);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoplayerUtil.isPlaying()){
            exoplayerUtil.setPlayWhenReady(false);
            //取消屏幕常亮
            clearFlagsScreenOn();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
        mHandler.removeCallbacks(mRunnable);
        mHandler.removeCallbacks(mRunnable2);
        exoplayerUtil.release();
    }

    private void addFlagsScreenOn(){
        try {
            Activity activity = getActivity();
            if (activity != null) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void clearFlagsScreenOn(){
        try {
            Activity activity = getActivity();
            if (activity != null) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LearnTimeEduFilmAdapter(dataList,getActivity()); //1 表示是普通视频
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line_edufilm));
        recyclerView.addItemDecoration(dividerLine);

        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

    }

    private void setRefreshLayout(){
        //禁止下拉刷新和上拉加载更多
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);

    }

    /**
     * 收藏按钮
     */
    @OnClick(R.id.imageButton_collect)
    public void onCollect(){
        Log.i(TAG, "onCollect: userType : "  + userType);
        if (userType == 1 || userType == 2) {
            if (btnCollect.isSelected()) {
                btnCollect.setSelected(false);
                RetrofitUtil.delCollect(banner.articleId);

            } else {
                btnCollect.setSelected(true);
                RetrofitUtil.addCollect(banner.articleId);

            }
        }else if (userType == 3){
            MyApplication.remindVisitor(getActivity());
        }
    }

    /**
     * 分享按钮
     */
    @OnClick(R.id.imageButton_share)
    public void onClickShare(){
        Log.i(TAG, "onClickShare: userType : " + userType);
        if (userType == 1 || userType == 2) {

            JshareUtil.showBroadView(getActivity(),banner.articleTitle,banner.articleProfile,banner.videoUrl,
                    1,banner.articleId);

        }else if (userType == 3){
            MyApplication.remindVisitor(getActivity());
        }
    }

    private void loadData(EducationFilmData educationFilmData){
        if (!isDestroy) {
            if (educationFilmData.data != null) {
                int size = educationFilmData.data.length;
                //取出第一条数据，作为置顶视频
                if (size >= 1){
                    ArticleBanner [] articleBanners = educationFilmData.data[0].rows;
                    Log.i(TAG, "loadData: articleBanners : " + articleBanners);
                    Log.i(TAG, "loadData: articleBanners size : " + articleBanners.length);
                    if (articleBanners != null && articleBanners.length >= 1){
                        banner = articleBanners[0];
                        Log.i(TAG, "loadData: banner : " + banner);
                        //初始化 置顶视频 页面
                        initView();
                    }
                }
                //取出视频分类数据，将数据放入集合，刷新recyclerview
                for (int i = 1; i < size; i++) {
                    dataList.add(educationFilmData.data[i]);
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

            }

        }
    }

    //获取 视频分类 数据
    private void getEducationFilmData() {
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getEducationFilmData(6, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EducationFilmData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(EducationFilmData educationFilmData) {
                        int code = Integer.valueOf(educationFilmData.code);
                        if (code == 0){
                            loadData(educationFilmData);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(educationFilmData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyApplication.getContext(),educationFilmData.message);
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


    private void initView(){
        if (banner != null){
            Log.i(TAG, "initView: banner.articleTitle  : " + banner.articleTitle);
            headLine.setText(banner.articleTitle);
            //是否已经收藏
            if (banner.isCollect == 1){
                btnCollect.setSelected(true);
            }
            //全屏播放时的标题
            lcHeadLine.setText(banner.articleTitle);
            playTimes.setText(Utils.formatPlayTimes(banner.browseTimes) + "次播放");
            textDate.setText(Utils.formatDate(banner.publishDate));
            //子线程计算视频时长，并显示
            //Utils.calculateDuration(videoDuration,banner.videoUrl);
            videoDuration.setVisibility(View.VISIBLE);
            videoDuration.setText(Utils.formatTime(banner.videoDuration));
            //加载图片
            String imgUrl = banner.videoCover;

            Log.i(TAG, "initView: imgUrl : " + imgUrl);
            //加载图片
            Glide.with(getActivity()).load(imgUrl)
                    .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                    .into(imageView);
            //初始化播放器
            setExoplayer();

        }

    }


    private void setExoplayer(){
        //获取16 ：9 的高度
        height = videoView.getHeight();
        //设置exoplayer
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
        //竖屏视频，
        //if (ratio < 1) {
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
        //}
    }

    @OnClick(R.id.imageButton_play)
    public void onClickPlay(){
        if (banner != null) {
            if (isFirst) {
                //增加浏览次数
                RetrofitUtil.addBrowsing(1, banner.articleId);
                isFirst = false;
                isStart = true;
            }

            if (!exoplayerUtil.isPlaying()) {
                btnPlay.setSelected(true);
                updateSeekBar();
                hideSeekBar();
                imageView.setVisibility(View.INVISIBLE);
                videoDuration.setVisibility(View.INVISIBLE);
                exoplayerUtil.setPlayWhenReady(true);
                //保持屏幕常亮
                addFlagsScreenOn();
            } else {
                btnPlay.setSelected(false);
                exoplayerUtil.setPlayWhenReady(false);
                //取消屏幕常亮
                clearFlagsScreenOn();
            }
        }else {
            Toast.makeText(getActivity(),"视频数据为空",Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.exoPlayerView)
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

    /**
     * 横竖屏切换
     */
    @OnClick(R.id.imageView_full_screen)
    public void changeScreen(){
        if (!fullScreen.isSelected()){
            //切换为横屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            //切换为竖屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
                Log.i(TAG, "run: mCurrentPosition : " + mCurrentPosition);
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
        if (layoutSeekBar != null && btnPlay != null) {
            layoutSeekBar.setVisibility(View.INVISIBLE);
            btnPlay.setVisibility(View.VISIBLE);
        }
    }

    //显示进度条
    private void showSeekBar(){
        if (layoutSeekBar != null && btnPlay != null) {
            layoutSeekBar.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.VISIBLE);
            isShow = true;
        }
    }
    //隐藏进度条
    private void hideSeekBar(){
        layoutSeekBar.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.INVISIBLE);
        isShow = false;
    }


    //反回按钮
    @OnClick(R.id.imageView_back)
    public void onClickBack(){
        if (isFullScreen){
            //切换为竖屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullScreen.setSelected(false);
            imgBack.setVisibility(View.INVISIBLE);
            isFullScreen = false;
        }
    }

    //隐藏 滑动布局中的 其他控件
    private void hideLayout(){
        layoutAbstract.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        //imageView.setVisibility(View.VISIBLE);
    }

    //显示
    private void showLayout(){
        layoutAbstract.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //配置文件中设置 android:configChanges="orientation|screenSize|keyboardHidden" 不然横竖屏切换的时候重新创建又重新播放
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //判断视频宽高比
            if (ratio > 1) {

                //ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                //videoView.setLayoutParams(params);
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

            //禁止 viewpager 滑动
            parentFg.parentFg.viewPager.setScanScroll(false);
            //隐藏布局
            hideLayout();

            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//显示顶部状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //显示返回按钮
            imgBack.setVisibility(View.VISIBLE);
            //更改全屏按钮的状态
            fullScreen.setSelected(true);
            isFullScreen = true;
            //全屏时，显示标题
            lcHeadLine.setVisibility(View.VISIBLE);
            //更换播放暂停按钮
            btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ac_play_video_detail_selector2));
            //更改布局，横屏布局
            parentFg.landscapeLayout();

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
            //设置 viewpager 可以滑动
            parentFg.parentFg.viewPager.setScanScroll(true);
            //显示布局
            showLayout();

            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏不 显示顶部状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            //隐藏返回按钮
            imgBack.setVisibility(View.INVISIBLE);
            //更改全屏按钮的状态
            fullScreen.setSelected(false);
            isFullScreen = false;
            //竖屏时，隐藏标题
            lcHeadLine.setVisibility(View.INVISIBLE);
            //更换播放暂停按钮
            btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ac_play_video_detail_selector1));
            //更改布局，竖屏布局
            parentFg.portraitLayout();

        }
        System.out.println("======onConfigurationChanged===");
    }


}
