package com.typartybuilding.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.Utils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayAudioActivity extends BaseActivity {

    private String TAG = "PlayAudioActivity";

    @BindView(R.id.circle_img_head)
    CircleImageView headImg;       //头像
    @BindView(R.id.imageView_attention)
    ImageView attention;           //关注
    @BindView(R.id.textView_like)
    TextView textLike;             //点赞
    @BindView(R.id.imagebutton_share)
    ImageButton btnShare;          //分享
    @BindView(R.id.textView_name)
    TextView textName;             //姓名
    @BindView(R.id.textView_abstract)
    TextView textAbstrack;         //音频简介

    @BindView(R.id.imagebutton_play)
    ImageButton btnPlay;          //播放按钮
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private MicroVideo microVideo;
    private MediaPlayer mediaPlayer;

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);
        ButterKnife.bind(this);
        //获取上一个activity传递过来的 图片 数据
        Intent intent = getIntent();
        //microVideo = (MicroVideo)intent.getSerializableExtra("MicroVideo");
        microVideo = MyApplication.microVideo;
        mediaPlayer = new MediaPlayer();
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
    }

    private void initView(){
        if (microVideo != null) {
            //加载头像
            Glide.with(this).load(microVideo.userHeadImg)
                    .apply(MyApplication.requestOptions)
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
            //用户名
            textName.setText(microVideo.userName);
            textAbstrack.setText(microVideo.visionTitle);
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

            JshareUtil.showBroadView(this,microVideo.visionTitle,"",microVideo.visionFileUrl,
                    2,microVideo.visionId);

        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    //关注
    @OnClick(R.id.imageView_attention)
    public void onClickAttention(){
        if (userType == 1 || userType == 2) {
           /* if (attention.isSelected()) {
                attention.setSelected(false);
                RetrofitUtil.delFocus(microVideo.userId);
            } else {
                attention.setSelected(true);
                RetrofitUtil.addFocus(microVideo.userId, microVideo.userName, microVideo.userHeadImg);
            }*/

            RetrofitUtil.addFocus(microVideo.userId, microVideo.userName, microVideo.userHeadImg,attention);
            microVideo.isFollowed = 1;
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
    private boolean isComplet;
    @OnClick(R.id.imagebutton_play)
    public void onClickBtn(){
        btnPlay.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if (isFirst) {
            startPlayAudio();
            isFirst = false;
        }else {
            //播放完后，重新播放
            if (!mediaPlayer.isPlaying() && isComplet) {
                startPlayAudio();
            }
            //暂停后，播放
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                btnPlay.setVisibility(View.INVISIBLE);
            }
        }
    }

    //点击播放背景图片，停止播放
    @OnClick(R.id.imageView_bg)
    public void onClickImg(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            btnPlay.setVisibility(View.VISIBLE);
        }
    }

    //点击头像，跳转到详情
    @OnClick(R.id.circle_img_head)
    public void headOnclick(){
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

    private void startPlayAudio(){

        try {
            Log.i(TAG, "startPlayAudio: visionFileUrl : " + microVideo.visionFileUrl);
            mediaPlayer.setDataSource(microVideo.visionFileUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.INVISIBLE);
                    mediaPlayer.start();

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


}










