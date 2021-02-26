package com.typartybuilding.activity.second;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
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

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.gyf.immersionbar.ImmersionBar;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayVideoDetailActivity;
import com.typartybuilding.adapter.RecommentAdapter;
import com.typartybuilding.adapter.RecommentVideoAdapter;
import com.typartybuilding.base.BaseAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.RecommendData;
import com.typartybuilding.loader.MineLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ExoplayerUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.UserUtils;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-20
 * @describe 视频播放页
 */
@Route(path = PlayVideoAct.PTAH)
public class PlayVideoAct extends BaseAct {
    public static final String PTAH = "/act/play_video";

    public final static String URL = "url";
    public final static String ARTICLE_TYPE = "articleType";
    public final static String ARTICLE_ID = "articleId";
    public final static String URL_TYPE = "urlType";

    @Autowired
    public int articleId;


    @BindView(R.id.play_video_recomment)
    RecyclerView mRecyclerView;


    @BindView(R.id.imageButton_play)
    ImageButton btnPlay;
    @BindView(R.id.videoView)
    TextureView videoView;            //播视频

    @BindView(R.id.imageView_pic)
    ImageView mVideoPic;
    @BindView(R.id.text_detail_collection)
    ImageButton mSelectCollection;
    @BindView(R.id.play_video_content)
    TextView play_video_content;
    @BindView(R.id.play_video_title)
    TextView play_video_title;
    @BindView(R.id.play_video_date)
    TextView play_video_date;
    @BindView(R.id.play_video_vistor_num)
    TextView play_video_vistor_num;

    //视频播放
    @BindView(R.id.imageView_back)
    ImageView mBackIv;
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
    @BindView(R.id.top_bar)
    ConstraintLayout mTopBar;

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType, -1);
    private ExoplayerUtil exoplayerUtil;
    boolean isShow;
    boolean isFirst;
    boolean isStart;
    private int videoWidth;           //视频宽度
    private int videoHeight;          //视频高度
    private float ratio;               //宽高比
    private int height;                //竖屏时，videoview 的高度
    private boolean isFullScreen;
    private Handler mHandler = new Handler();
    private MineLoader mMineLoader;
    private ArticleBanner banner;
    @Override
    public int getLayoutId() {
        return R.layout.layout_act_play_video;
    }


    @Override
    public void initData() {
        mMineLoader = new MineLoader();
        ImmersionBar.with(this)
                .transparentStatusBar()
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .navigationBarColor(R.color.white)
                .navigationBarDarkIcon(true)
                .init();
        isFirst = true;
        isStart = false;
        exoplayerUtil = new ExoplayerUtil(videoView, this);
    }

    @Override
    public void loadingData() {
        super.loadingData();
        getVideoDetails(articleId);


    }


    private void getVideoDetails(int articleId) {
        mMineLoader.getVideoDetails(articleId).subscribe(new RequestCallback<ArticleBanner>() {
            @Override
            public void onSuccess(ArticleBanner articleBanner) {
                Log.e("", "");
                banner = articleBanner;
                getRcomment();
                loadVideoData();
            }

            @Override
            public void onFail(Throwable e) {
                Log.e("", "");
            }
        });
    }



    private void loadVideoData() {

        if (banner != null) {
            //加载封面图片
            Glide.with(this).load(banner.videoCover)
                    .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                    .into(mVideoPic);
            play_video_title.setText(banner.articleTitle);
            play_video_vistor_num.setText("浏览量: " + Utils.formatPlayTimes(banner.browseTimes));
            play_video_date.setText(Utils.formatDate(banner.publishDate, "yyyy.MM.dd"));
            play_video_content.setText(banner.articleProfile);
            //判断是否收藏
            if (banner.isCollect == 1) {
                mSelectCollection.setSelected(true);
            } else {
                mSelectCollection.setSelected(false);
            }
            //全屏时的标题
            lcHeadLine.setText(banner.articleTitle);
            //先获取播放视频控件的高度
            ViewUtil.getViewWidth(videoView, new ViewUtil.OnViewListener() {
                @Override
                public void onView(int mWidth, int mHeight) {
                    height = mHeight;
//                    Log.i(TAG, "onView: height : " + height);
                    //初始化播放器
                    setExoplayer();
                }
            });
        }
    }



    private void setExoplayer(){
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
                        //初始化，开始播放时的状态
                        initPlayState();
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case PlaybackState.STATE_PAUSED :
                        if (!isFirst) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        break;
                    //播放完成
                    case PlaybackState.STATE_FAST_FORWARDING :
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
//                Log.i(TAG, "onVideoSizeChanged: width : " + width);
//                Log.i(TAG, "onVideoSizeChanged: height : " + height);
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
        seekBar.setMax((int)duration);

        nowTime.setText(Utils.formatTime(mCurrentPosition/1000));
        playDuration.setText(Utils.formatTime((int)duration/1000));
        updateSeekBar();
        //隐藏进度条
        delayHideSeekBar();
    }


    //根据视频尺寸，重新设置VideoView的宽高；竖屏时设置，横屏屏时，全屏播放
    private void refreshVideoViewSize(){
        ratio = (float)videoWidth/(float)videoHeight;
//        Log.i(TAG, "refreshVideoViewSize: ratio : " + ratio);

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
//
//        Log.i(TAG, "refreshVideoViewSize: params.height: " + params.height);
//        Log.i(TAG, "refreshVideoViewSize: params.width : " + params.width);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.articleBanner = null;

        mHandler.removeCallbacks(mRunnable);
        mHandler.removeCallbacks(mRunnable2);
        exoplayerUtil.release();
    }


    private void setSeekBar() {
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


    public void getRcomment() {
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getRecommendData(banner.articleType, articleId, banner.urlType, UserUtils.getIns().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RecommendData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RecommendData recommendData) {
                        int code = Integer.valueOf(recommendData.code);
                        if (code == 0) {
                            loadData(recommendData);
                        } else if (code == -1) {
                            RetrofitUtil.errorMsg(recommendData.message);
                        } else if (code == 10) {
                            RetrofitUtil.tokenLose(PlayVideoAct.this, recommendData.message);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void loadData(RecommendData recommendData) {
        if (recommendData != null && recommendData.data != null && recommendData.data.length > 0) {
//            mLikeLook.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        RecommentVideoAdapter recommentAdapter = new RecommentVideoAdapter();
        recommentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBanner rowsBean = (ArticleBanner) adapter.getData().get(position);
                ARouter.getInstance().build(PlayVideoAct.PTAH)
                        .withInt(TextDetailAct.ARTICLE_ID, rowsBean.articleId)
                        .navigation(PlayVideoAct.this);
            }
        });
        recommentAdapter.setNewData(new ArrayList<>(Arrays.asList(recommendData.data)));
        mRecyclerView.setAdapter(recommentAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        showSeekBar();
        startLayout();
        btnPlay.setSelected(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        //取消屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (exoplayerUtil.isPlaying()){
            exoplayerUtil.setPlayWhenReady(false);
        }
    }

    /**
     * 更新进度条
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (videoView != null) {
                int mCurrentPosition = (int) exoplayerUtil.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);
                nowTime.setText(Utils.formatTime(mCurrentPosition / 1000));
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

    private void delayHideSeekBar() {
        mHandler.removeCallbacks(mRunnable2);
        mHandler.postDelayed(mRunnable2, 3000);
    }

    //第一进入页面，进度条不可见，播放按钮可见
    private void startLayout() {
        layoutSeekBar.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.VISIBLE);
    }

    //显示进度条
    private void showSeekBar() {
        layoutSeekBar.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.VISIBLE);
        isShow = true;
    }

    //隐藏进度条
    private void hideSeekBar() {
        layoutSeekBar.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.INVISIBLE);
        isShow = false;
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isFullScreen) {
            //切换为竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullScreen.setSelected(false);
            isFullScreen = false;
        } else {
            finish();
        }
    }

    //反回按钮
    @OnClick(R.id.text_detail_back)
    public void onClickFinish() {
//        if (isFullScreen) {
//            //切换为竖屏
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            fullScreen.setSelected(false);
//            isFullScreen = false;
//        } else {
            finish();
//        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //配置文件中设置 android:configChanges="orientation|screenSize|keyboardHidden" 不然横竖屏切换的时候重新创建又重新播放
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBackIv.setVisibility(View.VISIBLE);
            lcHeadLine.setVisibility(View.VISIBLE);
            mTopBar.setVisibility(View.GONE);
            if (ratio > 1) {
                /*ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                videoView.setLayoutParams(params);*/
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
                int height = Utils.getScreenHeight();
                params.height = height;
                params.width = (int) (height * ratio);
                videoView.setLayoutParams(params);

            } else if (ratio <= 1) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
                int height = Utils.getScreenHeight();
                params.height = height;
                params.width = (int) (height * ratio);
                videoView.setLayoutParams(params);
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//显示顶部状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (ratio > 1) {
               /* ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                params.dimensionRatio = "16:9";
                videoView.setLayoutParams(params);*/
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
                params.height = height;
                params.width = (int) (height * ratio);
                videoView.setLayoutParams(params);

            } else if (ratio <= 1) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) videoView.getLayoutParams();
                params.height = height;
                params.width = (int) (height * ratio);
                videoView.setLayoutParams(params);
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏不 显示顶部状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            mBackIv.setVisibility(View.GONE);
            lcHeadLine.setVisibility(View.GONE);
            mTopBar.setVisibility(View.VISIBLE);
            fullScreen.setSelected(false);
            isFullScreen = false;
            //竖屏时，隐藏标题
//            lcHeadLine.setVisibility(View.INVISIBLE);
            //更换播放暂停按钮
            btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ac_play_video_detail_selector1));

        }
        System.out.println("======onConfigurationChanged===");
    }

    /**
     * 收藏按钮
     */
    @OnClick(R.id.text_detail_collection)
    public void onCollect() {
        if (userType == 1 || userType == 2) {
            if (mSelectCollection.isSelected()) {
                mSelectCollection.setSelected(false);
                RetrofitUtil.delCollect(articleId);
            } else {
                mSelectCollection.setSelected(true);
                RetrofitUtil.addCollect(articleId);
            }
        } else if (userType == 3) {
            MyApplication.remindVisitor(this);
        }
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
                mVideoPic.setVisibility(View.INVISIBLE);
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

    @OnClick(R.id.text_detail_share)
    public void onClickShare() {
        if (userType == 3) {
            MyApplication.remindVisitor(this);
        } else {
            if (banner!=null){
                JshareUtil.showBroadView(this, banner.articleTitle, banner.articleProfile, banner.videoUrl,
                        1, banner.articleId);
            }
        }
    }

    /**
     * 横竖屏切换
     */
    @OnClick(R.id.imageView_full_screen)
    public void changeScreen(){
        if (!fullScreen.isSelected()){
            mBackIv.setVisibility(View.VISIBLE);
            mTopBar.setVisibility(View.GONE);
            //切换为横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            fullScreen.setSelected(true);
            isFullScreen = true;
            //全屏时，显示标题
            lcHeadLine.setVisibility(View.VISIBLE);
            //更换播放暂停按钮
            btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ac_play_video_detail_selector2));
        }else {
            mBackIv.setVisibility(View.GONE);
            mTopBar.setVisibility(View.VISIBLE);
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

}
