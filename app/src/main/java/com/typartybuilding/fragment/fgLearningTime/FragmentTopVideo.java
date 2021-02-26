package com.typartybuilding.fragment.fgLearningTime;


import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
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
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.HomeFragmentLearningTime;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ExoplayerUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.utils.ViewUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *  普通视频 的置顶视频
 */
public class FragmentTopVideo extends BaseFragment {

    private String TAG = "FragmentTopVideo";

    @BindView(R.id.exoPlayerView)
    TextureView videoView;             //播视频

    @BindView(R.id.imageView_pic)
    ImageView imageView;            //视频封面图片
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

    //全屏时 隐藏
    @BindView(R.id.constraintLayout_abstract)
    ConstraintLayout layoutAbstract;            //简介布局
    @BindView(R.id.view)
    View view;                                 //分割线

    private ArticleBanner banner ;    //置顶视频数据

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    private Handler mHandler = new Handler();
    private boolean isShow;          //进度条和播放按钮是否显示
    public boolean isFullScreen;    //是否是全屏

    private int videoWidth ;           //视频宽度
    private int videoHeight ;          //视频高度
    private float ratio;               //宽高比
    private int height;                //竖屏时，videoview 的高度
    private ExoplayerUtil exoplayerUtil;

    private boolean isStart = false;      //是否开始播放了
    private boolean isFirst = true;    //是否是第一次播放，即重新进入改页面

    private HomeFragmentLearningTime parentFg;
    private HomeActivity activity;
    private FragmentLearnTimeNew fgLearnTimeNew;

    public void setBanner(ArticleBanner banner) {
        this.banner = banner;
    }

    public void setParentFg(HomeFragmentLearningTime parentFg) {
        this.parentFg = parentFg;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        activity = (HomeActivity)getActivity();
        fgLearnTimeNew = (FragmentLearnTimeNew) getParentFragment();
        isFirst = true;
        isStart = false;
        exoplayerUtil = new ExoplayerUtil(videoView,getActivity());
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_learning_time_top_video;
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
            if (fgLearnTimeNew != null){
                fgLearnTimeNew.clearFlagsScreenOn();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mRunnable);
        mHandler.removeCallbacks(mRunnable2);
        exoplayerUtil.release();
    }

    /**
     * 收藏按钮
     */
    @OnClick(R.id.imageButton_collect)
    public void onCollect(){
        if (userType == 1 || userType == 2) {
            if (btnCollect.isSelected()) {
                btnCollect.setSelected(false);
                RetrofitUtil.delCollect(banner.articleId);
                banner.isCollect = 0;

            } else {
                btnCollect.setSelected(true);
                RetrofitUtil.addCollect(banner.articleId);
                banner.isCollect = 1;
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


    private void initView(){
        if (banner != null){
            Log.i(TAG, "initView: banner.articleTitle  : " + banner.articleTitle);
            Log.i(TAG, "initView: headLine : " + headLine);
            headLine.setText(banner.articleTitle);
            //是否收藏
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
            //初始化播放器
            //setExoplayer();
        }
    }

    private void setExoplayer(){
        //获取16 ：9 的高度
        //height = videoView.getHeight();
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
//
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
        //updateSeekBar();
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
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
        int height = 0;
        if (isFullScreen){
            height = videoView.getHeight();
        }else {
            height = this.height;
        }
        Log.i(TAG, "refreshVideoViewSize: height : " + height);
        params.height = height;
        params.width = (int)(height*ratio);
        videoView.setLayoutParams(params);
    }



    @OnClick(R.id.imageButton_play)
    public void onClickPlay(){
        if (banner != null) {
            if (isFirst) {
                //增加浏览次数
                RetrofitUtil.addBrowsing(1, banner.articleId);
                //增加播放量
                banner.browseTimes += 1;
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
                if (fgLearnTimeNew != null){
                    fgLearnTimeNew.addFlagsScreenOn();
                }
            } else {
                btnPlay.setSelected(false);
                exoplayerUtil.setPlayWhenReady(false);
                //取消屏幕常亮
                if (fgLearnTimeNew != null){
                    fgLearnTimeNew.clearFlagsScreenOn();
                }
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
            //滚动到顶端
            fgLearnTimeNew.recyclerView.scrollToPosition(0);
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
                //Log.i(TAG, "run: mCurrentPosition : " + mCurrentPosition);
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
        //recyclerView.setVisibility(View.GONE);
        //imageView.setVisibility(View.VISIBLE);
    }

    //显示
    private void showLayout(){
        layoutAbstract.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        //recyclerView.setVisibility(View.VISIBLE);
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
            //禁止recyclerview 滑动
            fgLearnTimeNew.layoutManager.setScrollEnabled(false);
            //禁止 上拉加载更多
            fgLearnTimeNew.refreshLayout.setEnableLoadMore(false);

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
            //设置recyclerview 可以滑动
            fgLearnTimeNew.layoutManager.setScrollEnabled(true);
            //设置 上拉加载更多
            fgLearnTimeNew.refreshLayout.setEnableLoadMore(true);
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
        //System.out.println("======onConfigurationChanged===");
    }


}
