package com.typartybuilding.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.MixtureData;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.NetworkStateUtil;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayMicroVideoMixActivity extends BaseActivity {

    private String TAG = "PlayMicroVideoActivity";

    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.imageView)
    ImageView imageView;          //视频图片
    @BindView(R.id.imagebutton_play)
    ImageButton btnPlay;          //播放按钮
    @BindView(R.id.circle_img_head)
    CircleImageView headImg;       //头像
    @BindView(R.id.imageView_attention)
    ImageView attention;           //关注
    @BindView(R.id.textView_like)
    TextView textLike;             //点赞数量，或播放量
    @BindView(R.id.imagebutton_share)
    ImageButton btnShare;          //分享
    @BindView(R.id.textView_name)
    TextView textName;             //姓名
    @BindView(R.id.textView_abstract)
    TextView textAbstrack;         //视频简介
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    /*private ChoicenessData.MicroVision microVision ;   //精选页面的 微视频
    private MicroVideo microVideo;   //个人页面 和 发现精彩  的微视
    private int flag;                //1 精选页面 微视频；2 个人页面和发现精彩页面微视数据
    */

    private MixtureData mixtureData;

    private int myUserId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    private int videoPos;              //视频的当前位置

    private boolean isMobileData;      //是否在使用移动数据

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_micro_video);
        ButterKnife.bind(this);
        //获取视频数据
        Intent intent = getIntent();
        mixtureData = (MixtureData)intent.getSerializableExtra("MixtureData");

        Log.i(TAG, "onCreate: mixtureData : " + mixtureData);
        int flag = intent.getIntExtra("flag",-1);
        Log.i(TAG, "onCreate: flag : " + flag);

        if (mixtureData == null) {
            mixtureData = MyApplication.mixtureData;
        }

        if (mixtureData != null) {
            //注册广播接收器
            registerBroadReceiver();
            //初始化
            initView();
            //设置videoview
            setVideoView();
            //初始化 网络变化 的弹窗
            initPopupWindow();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
        changeNavigationBar();
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //imageView.setVisibility(View.VISIBLE);
        if (videoView != null){
            videoView.seekTo(videoPos);
            //播放时，切到其他页面
            if (btnPlay.getVisibility() == View.INVISIBLE){

                btnPlay.setVisibility(View.INVISIBLE);
                videoView.start();
                //暂停时，切到其他页面
            }else {
                btnPlay.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //取消屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (videoView != null){
            videoPos = videoView.getCurrentPosition();
            if (videoView.isPlaying()){
                videoView.pause();
                //videoView.stopPlayback();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        MyApplication.mixtureData = null;
        if (videoView != null){
            videoView.suspend();
        }
    }



    private void initView(){

        if (mixtureData != null) {
            textName.setText(mixtureData.userName);
            textAbstrack.setText(mixtureData.targetTitle);
            textLike.setText(Utils.formatPlayTimes2(mixtureData.praisedNum));
            Log.i(TAG, "initView: mixtureData.isFollowed : " + mixtureData.isFollowed);
            if (mixtureData.isFollowed == 1){
                attention.setVisibility(View.INVISIBLE);
            }else {
                attention.setVisibility(View.VISIBLE);
            }
            if (mixtureData.isPraise == 1){
                textLike.setSelected(true);
            }else {
                textLike.setSelected(false);
            }

            //加载头像
            Glide.with(this).load(mixtureData.userHeadImg)
                    .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                    .into(headImg);
            //子线程获取视频第一帧，并显示
            //Utils.getVideoPicture(imageView,microVision.visionFileUrl);
            Glide.with(this).load(mixtureData.videoCover)
                    .apply(MyApplication.requestOptions916)  //url为空或异常显示默认头像
                    .into(imageView);
            //添加浏览历史记录，更新浏览量/播放量 1：咨询文章，2：党建微视
            RetrofitUtil.browseMicro(mixtureData.targetId);
        }

    }

    private boolean isFirst = true;
    //播放按钮
    @OnClick(R.id.imagebutton_play)
    public void onClickPlay(){

        if (!videoView.isPlaying()){
            videoView.start();
            btnPlay.setVisibility(View.INVISIBLE);
        }

        /*if (!videoView.isPlaying()){
            imageView.setVisibility(View.INVISIBLE);
            btnPlay.setVisibility(View.INVISIBLE);
            if (isFirst) {
                progressBar.setVisibility(View.VISIBLE);
                startPlay();
                isFirst = false;
            }else {
                videoView.start();
            }
        }*/
    }

    //开始播放
    private boolean isStart;
    private void startPlay(){

        if (mixtureData != null && mixtureData .fileUrl != null) {
            Log.i("micro", "startPlay: " + mixtureData.fileUrl);
            videoView.setVideoPath(mixtureData.fileUrl);
            videoView.requestFocus();
            videoView.start();
            isStart = true;
        }else {
            Toast.makeText(MyApplication.getContext(),"视频链接为空",Toast.LENGTH_SHORT).show();
        }

    }


    @OnClick(R.id.imagebutton_back)
    public void onClickBack(){
        finish();
    }


    //分享
    @OnClick(R.id.imagebutton_share)
    public void onClickShare(){
        if (mixtureData == null){
            return;
        }
        if (userType == 1 || userType == 2) {

            JshareUtil.showBroadView(this,mixtureData.targetTitle,"",mixtureData.fileUrl,
                    2,mixtureData.targetId);

        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    //点击头像，跳转到详情
    @OnClick(R.id.circle_img_head)
    public void headOnclick(){
        if (mixtureData == null){
            return;
        }
        if (userType == 3){
            MyApplication.remindVisitor(this);
        }else {
            //if (myUserId != mixtureData.userId) {
                Intent intent = new Intent(this, UserDetailsActivity.class);
                intent.putExtra("userId", mixtureData.userId);
                intent.putExtra("userName", mixtureData.userName);
                startActivity(intent);
           // }

        }
    }

    //关注
    @OnClick(R.id.imageView_attention)
    public void onClickAttention(){
        if (mixtureData == null){
            return;
        }
        if (userType == 1 || userType == 2) {

            RetrofitUtil.addFocus(mixtureData.userId, mixtureData.userName, mixtureData.userHeadImg,attention);
            mixtureData.isFollowed = 1;

        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    //点赞
    @OnClick(R.id.textView_like)
    public void onClickLike(){
        if (userType == 1 || userType == 2) {

            if (mixtureData != null) {
                //取消点赞
                if (textLike.isSelected()) {
                    textLike.setSelected(false);
                    if (mixtureData.isPraise == 0) {
                        textLike.setText(Utils.formatPlayTimes2(mixtureData.praisedNum));
                        RetrofitUtil.cancelPraise(mixtureData.targetId, mixtureData.userId);
                    } else {
                        //点赞数减1, 以前点过赞
                        textLike.setText(Utils.formatPlayTimes2(mixtureData.praisedNum - 1));
                        RetrofitUtil.cancelPraise(mixtureData.targetId, mixtureData.userId);
                    }
                    mixtureData.isPraise = 0;
                    mixtureData.praisedNum += -1;
                } else {
                    //点赞
                    textLike.setSelected(true);
                    if (mixtureData.isPraise == 1) {
                        textLike.setText(Utils.formatPlayTimes2(mixtureData.praisedNum));
                        RetrofitUtil.microPraise(mixtureData.targetId, mixtureData.userId);
                    } else {
                        //点赞数加1
                        textLike.setText(Utils.formatPlayTimes2(mixtureData.praisedNum + 1));
                        RetrofitUtil.microPraise(mixtureData.targetId, mixtureData.userId);
                    }
                    mixtureData.isPraise = 1;
                    mixtureData.praisedNum += 1;
                }
            }

        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }



    //根据视频尺寸，重新设置VideoView的宽高；横屏时设置，竖屏时，全屏播放
    private void refreshVideoViewSize(int videoWidth,int videoHeight){
        float ratio = (float)videoWidth/(float)videoHeight;
        //横屏视频，
        if (ratio > 1 && ratio < 1.5){
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)videoView.getLayoutParams();
            params.height = 0;
            params.dimensionRatio = "4:3";
            videoView.setLayoutParams(params);
        }else if (ratio > 1 && ratio >= 1.5){
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)videoView.getLayoutParams();
            params.height = 0;
            params.dimensionRatio = "16:9";
            videoView.setLayoutParams(params);
        }else if (ratio == 1){
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)videoView.getLayoutParams();
            params.height = 0;
            params.dimensionRatio = "1:1";
            videoView.setLayoutParams(params);
        }

    }

    private void setVideoView(){
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.INVISIBLE);

                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        int videoWidth = mp.getVideoWidth();
                        int videoHeight = mp.getVideoHeight();
                        refreshVideoViewSize(videoWidth,videoHeight);
                    }
                });
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoView.isPlaying()){
                    videoView.pause();
                    btnPlay.setVisibility(View.VISIBLE);
                }else {
                    if (isStart) {
                        videoView.start();
                        btnPlay.setVisibility(View.INVISIBLE);
                    }
                }
                return false;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完了，自动重播
                Log.i(TAG, "onCompletion: 播放完成");
                videoView.start();
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                    progressBar.setVisibility(View.VISIBLE);

                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
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
                progressBar.setVisibility(View.INVISIBLE);
                btnPlay.setVisibility(View.VISIBLE);

                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED){
                    Toast.makeText(MyApplication.getContext(),"网络服务错误",Toast.LENGTH_LONG).show();
                }else if(what == MediaPlayer.MEDIA_ERROR_UNKNOWN){
                    if (extra == MediaPlayer.MEDIA_ERROR_IO){
                        Toast.makeText(MyApplication.getContext(), "网络文件错误", Toast.LENGTH_LONG).show();

                    }else if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT){
                        Toast.makeText(MyApplication.getContext(), "网络超时", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(MyApplication.getContext(), "未知错误", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }


    //网络状态弹窗
    private PopupWindow popupWindow;  //底部弹窗 ，提示正在使用流量
    private View popView;             //弹窗布局
    private IntentFilter intentFilter;
    private NetworkChangeReceive networkChangeReceiver;
    private Handler handler = new Handler();
    private boolean isRegister = true;

    //进入该页面，直接播放
    private void playOver(){
        if (!isMobileData) {
            if (isFirst) {
                progressBar.setVisibility(View.VISIBLE);
                btnPlay.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                startPlay();
                isFirst = false;
            }
        }
    }

    /**
     * 延时 检测
     */
    private void delayDetect(){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //判断是否，在用移动数据
                if (NetworkStateUtil.isMobileConnected(PlayMicroVideoMixActivity.this)){

                    if (!MyApplication.isRemind) {
                        showPopupWindow();
                        isMobileData = true;
                    }
                }
                //进入该页面，直接播放
                playOver();
            }
        },200);

    }

    /**
     *  动态注册 广播接收器，监测网络 状态
     */
    private void registerBroadReceiver(){
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceive();
        registerReceiver(networkChangeReceiver,intentFilter);
        delayDetect();
    }

    private class  NetworkChangeReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (NetworkStateUtil.isMobileConnected(PlayMicroVideoMixActivity.this)){
                Log.i(TAG, "onReceive: ");
                //第一次注册时，不执行 showPopupWindow();
                if (isRegister){
                    isRegister = false;
                    return;
                }
                showPopupWindow();
            }
        }
    }


    private void showPopupWindow(){
        //是否，已经提醒过用户了，点击了继续观看，就只提醒一次，点击退出，下次继续提醒
        if (!MyApplication.isRemind) {
            //if (!popupWindow.isShowing()) {
                //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
                popupWindow.showAtLocation(popView, Gravity.CENTER, 0, 0);
                //改变屏幕透明度
                Utils.backgroundAlpha(this, 0.7f);
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
                btnPlay.setVisibility(View.VISIBLE);
            //}

        }
    }

    /**
     *  使用移动数据 弹窗
     */
    private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_network_state, null);
        Button btnComfirm = popView.findViewById(R.id.button_comfirm);
        Button btnCancel = popView.findViewById(R.id.button_cancel);

        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(PlayMicroVideoMixActivity.this,1f);

                btnPlay.setVisibility(View.VISIBLE);
                hideStatusBar();
                changeNavigationBar();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                finish();
            }
        });

        btnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                MyApplication.isRemind = true;

                isMobileData = false;
                playOver();
            }
        });
    }



}
