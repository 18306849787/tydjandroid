package com.typartybuilding.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.RoundImageView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayAudioActivityNew extends BaseActivity {

    private String TAG = "PlayAudioActivityNew";

    @BindView(R.id.circle_img_head)
    CircleImageView headImg;       //头像
    @BindView(R.id.imageView_attention)
    ImageView attention;           //关注
    @BindView(R.id.textView_like)
    TextView textLike;             //点赞
    @BindView(R.id.imagebutton_share)
    ImageButton btnShare;          //分享

    //新增
    @BindView(R.id.textView_title)
    TextView textTitle;             //标题栏
    @BindView(R.id.textView_headline)
    TextView textHeadLine;          //音频标题
    @BindView(R.id.imageView_track_pic)
    RoundImageView roundImageView;   //音频封面
    @BindView(R.id.imageView_bg)
    ImageView imageViewBg;           //音频封面图下的阴影图片，没有封面图片时，不显示该阴影图

    @BindView(R.id.textView_time)
    TextView nowTime;               //音频播放的时间
    @BindView(R.id.textView_duration)
    TextView playDuration;           //音频时长

    @BindView(R.id.seekBar)
    SeekBar seekBar;                //进度条
    @BindView(R.id.imageView_last)
    ImageView imgLast;              //快退
    @BindView(R.id.imageView_play)
    ImageView btnPlay;              //播放按钮
    @BindView(R.id.imageView_next)
    ImageView imgNext;              //快进


   /* @BindView(R.id.textView_name)
    TextView textName;             //姓名
    @BindView(R.id.textView_abstract)
    TextView textAbstrack;         //音频简介*/

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private MicroVideo microVideo;
    private MediaPlayer mediaPlayer;

    private Handler mHandler = new Handler();

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio_new);
        ButterKnife.bind(this);
        //获取上一个activity传递过来的 图片 数据
        //Intent intent = getIntent();
        //microVideo = (MicroVideo)intent.getSerializableExtra("MicroVideo");
        microVideo = MyApplication.microVideo;
        //设置标题栏
        if (microVideo != null ){
            textTitle.setText(microVideo.userName);
        }

        mediaPlayer = new MediaPlayer();
        //音频准备前，seekbar不可拖动
        seekBar.setEnabled(false);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
        changeNavigationBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        MyApplication.microVideo = null;
        if (mHandler != null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private void initView(){
        if (microVideo != null) {
            //加载头像
            Glide.with(this).load(microVideo.userHeadImg)
                    .apply(MyApplication.requestOptions2)
                    .into(headImg);
            //是否关注
            if (microVideo.isFollowed == 1) {
                attention.setVisibility(View.INVISIBLE);
            }else {
                attention.setVisibility(View.VISIBLE);
            }
            //点赞
            if (microVideo.isPraise == 1){
                textLike.setSelected(true);
            }else {
                textLike.setSelected(false);
            }
            //点赞数
            textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum));

            //设置音频标题
            textHeadLine.setText(microVideo.visionTitle);
            if (microVideo.videoCover == null){
                imageViewBg.setVisibility(View.INVISIBLE);
            }
            //加载音频封面图
            Glide.with(this).load(microVideo.videoCover)
                    .apply(MyApplication.requestOptionsAudio)
                    .into(roundImageView);
            //用户名
            //textName.setText(microVideo.userName);
            //textAbstrack.setText(microVideo.visionTitle);
            //添加浏览历史记录，更新浏览量/播放量 1：咨询文章，2：党建微视
            RetrofitUtil.browseMicro(microVideo.visionId);
        }
    }

    @OnClick(R.id.imagebutton_back)
    public void onClickBack(){
        finish();
    }

    //分享
    @OnClick(R.id.imagebutton_share)
    public void onClickShare(){
        if (userType == 1 || userType == 2) {
            if (microVideo != null) {
                String visionTitle = microVideo.visionTitle;
                if (visionTitle == null || visionTitle == "") {
                    visionTitle = getResources().getString(R.string.mic_defaul_title);
                }

                JshareUtil.showBroadView(this, visionTitle, "", microVideo.visionFileUrl,
                        2, microVideo.visionId);
            }

        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    //关注
    @OnClick(R.id.imageView_attention)
    public void onClickAttention(){
        if (userType == 1 || userType == 2) {
            if (microVideo != null) {
                RetrofitUtil.addFocus(microVideo.userId, microVideo.userName, microVideo.userHeadImg, attention);
                microVideo.isFollowed = 1;
            }
        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    //点赞
    @OnClick(R.id.textView_like)
    public void onClickLike(){
        if (userType == 1 || userType == 2) {
            if (microVideo != null) {
                //取消点赞
                if (textLike.isSelected()) {
                    textLike.setSelected(false);
                    if (microVideo.isPraise == 0) {
                        //点赞数减1,以前点过赞
                        textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum));
                        RetrofitUtil.cancelPraise(microVideo.visionId, microVideo.userId);
                    } else {
                        textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum - 1));
                        RetrofitUtil.cancelPraise(microVideo.visionId, microVideo.userId);
                    }
                    microVideo.isPraise = 0;
                    microVideo.visionPraisedNum += -1;

                } else {
                    //点赞
                    textLike.setSelected(true);
                    if (microVideo.isPraise == 1) {
                        textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum));
                        RetrofitUtil.microPraise(microVideo.visionId, microVideo.userId);
                    } else {
                        //点赞数加1
                        textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum + 1));
                        RetrofitUtil.microPraise(microVideo.visionId, microVideo.userId);
                    }
                    microVideo.isPraise = 1;
                    microVideo.visionPraisedNum += 1;
                }
            }
        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }


    private boolean isFirst = true;
    @OnClick(R.id.imageView_play)
    public void onClickBtn(){

        if (isFirst) {
            if (!btnPlay.isSelected()){
                btnPlay.setSelected(true);
            }
            progressBar.setVisibility(View.VISIBLE);
            startPlayAudio();
            isFirst = false;
        }else {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                btnPlay.setSelected(false);
            }else {
                mediaPlayer.start();
                btnPlay.setSelected(true);
            }

        }
    }

    /**
     * 更新进度条
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer != null){
                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);
                nowTime.setText(Utils.formatTime(mCurrentPosition/1000));
                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void setSeekBar(){
        seekBar.setEnabled(true);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mHandler.removeCallbacks(mRunnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    updateSeekBar();

                }
            }
        });
    }

    private void startPlayAudio(){
        if (microVideo == null){
            return;
        }
        try {
            Log.i(TAG, "startPlayAudio: visionFileUrl : " + microVideo.visionFileUrl);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(microVideo.visionFileUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.INVISIBLE);
                    //设置音频时长
                    int duration = mediaPlayer.getDuration();
                    Log.i(TAG, "onPrepared: duration ; " + duration);
                    playDuration.setText(Utils.formatTime(duration/1000));
                    mediaPlayer.start();
                    setSeekBar();
                    updateSeekBar();
                }
            });

        }catch (IOException e){
            Log.i(TAG, "startPlayAudio: ");
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完后，可重新播放
                mHandler.removeCallbacks(mRunnable);
                btnPlay.setSelected(false);
                seekBar.setProgress(0);
                seekBar.setEnabled(false);
                nowTime.setText("00:00");
                isFirst = true;

            }
        });

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                    progressBar.setVisibility(View.VISIBLE);

                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                return true;

            }
        });

    }

    private int currentPos;
    //短按
    @OnClick({R.id.imageView_last, R.id.imageView_next})
    public void onClick(View view){
        switch (view.getId()){
            //快退
            case R.id.imageView_last:

                break;

            //快进
            case R.id.imageView_next:

                break;
        }
    }

    //长按
  /*  @OnLongClick({R.id.imageView_last, R.id.imageView_next})
    public void onLongClick(View view) {
        switch (view.getId()) {
            //快退
            case R.id.imageView_last:

                break;

            //快进
            case R.id.imageView_next:
                break;
        }
    }*/


    //点击头像，跳转到详情
    @OnClick(R.id.circle_img_head)
    public void headOnclick(){
        if (microVideo == null){
            return;
        }
        if (userType == 3){
            MyApplication.remindVisitor(this);
        }else {
            //if (myUserId != mixtureData.userId) {
            Intent intent = new Intent(this, UserDetailsActivity.class);
            intent.putExtra("userId", microVideo.userId);
            intent.putExtra("userName", microVideo.userName);
            startActivity(intent);
            // }
        }
    }


    /*private void initAudio(){
        try {
            Log.i(TAG, "startPlayAudio: visionFileUrl : " + microVideo.visionFileUrl);
            mediaPlayer.setDataSource(microVideo.visionFileUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.INVISIBLE);
                    //设置音频时长
                    int duration = mediaPlayer.getDuration();
                    Log.i(TAG, "onPrepared: duration ; " + duration);
                    playDuration.setText(Utils.formatTime(duration/1000));
                    nowTime.setText(Utils.formatTime(0));
                    setSeekBar();

                    //mediaPlayer.start();
                    //updateSeekBar();
                }
            });

        }catch (IOException e){
            Log.i(TAG, "startPlayAudio: ");
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完后，可重新播放
                btnPlay.setVisibility(View.VISIBLE);
                isComplet = true;

            }
        });

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                    progressBar.setVisibility(View.VISIBLE);

                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                return true;

            }
        });
    }

*/



}










