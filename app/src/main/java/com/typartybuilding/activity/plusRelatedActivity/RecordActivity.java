package com.typartybuilding.activity.plusRelatedActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.recording.RecordingItem;
import com.typartybuilding.recording.RecordingService;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class RecordActivity extends WhiteTitleBaseActivity {

    private String TAG = "RecordActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;

    @BindView(R.id.chronometer)
    Chronometer recodTimer;        //录音计时器

   /* @BindView(R.id.textView_timer)
    TextView textTimer;            //播放过程中显示时间*/

    @BindView(R.id.imageButton_play_pause)
    ImageButton btnPlayPause;     //播放 暂停 按钮

    @BindView(R.id.textView_play_pause)
    TextView textPlayPause;       //显示播放暂停
    @BindView(R.id.textView_long_click)
    TextView textLongClick;       //长按提示语

    @BindView(R.id.imageButton_long_click)
    ImageButton btnLongClick;     //长按 录音 按钮
    @BindView(R.id.imageView_upload)
    ImageView imgUpload;          //上传按钮
    @BindView(R.id.textView_upload)
    TextView textUpload;          //上传文字提示

    private String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private List<String> mPermissionList = new ArrayList<>();

    //录音相关和播放录音
    private Intent intentService;    //录音的服务
    private MediaPlayer mMediaPlayer = null;

    int play = R.drawable.ldt_btn_canplay ;  //播放按钮
    int pause = R.drawable.ldt_btn_pause ;   //暂停按钮
    private long recordingTime = 0;// 播方录音时，记录下来的总时间，重新播放时，计时器减去该时间

    private int miss;      //用于计时


    private PopupWindow popupWindow;  //底部弹窗
    private View popView;             //弹窗布局
    private EditText editSay;         //发布时，说点什么
    private  ImageView imgBack;       //关闭弹窗
    private  TextView textComfirm;         //弹窗 ，确定按钮，点击上传
    private LinearLayout linearLayout;   //发布时，显示的进度


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);
        textTitle.setText("朗读厅");
        //初始化弹窗
        initPopupWindow();
        //初始化控件
        initWidget();
        //请求权限
        requestPermission(1);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_ac_record, null);
        imgBack = popView.findViewById(R.id.imageView_back);
        textComfirm = popView.findViewById(R.id.textView_comfirm);
        editSay = popView.findViewById(R.id.edit_say);
        linearLayout = popView.findViewById(R.id.linearLayout);   //点发布后，显示的进度条

        int pxHeight = Utils.dip2px(this,207);
        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        //设置弹窗适应输入法
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(RecordActivity.this,1f);
            }
        });

        //关闭弹窗
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        //点 确定键， 开始上传
        textComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = MyApplication.pref.getString(MyApplication.prefKey4_audio_path,"");
                if (path == null){
                    Toast.makeText(RecordActivity.this,"录音文件为空",Toast.LENGTH_SHORT).show();
                }else {
                    uploadMicro();
                    linearLayout.setVisibility(View.VISIBLE);
                    textComfirm.setEnabled(false);

                }
            }
        });

    }

    @OnClick(R.id.imageView_upload)
    public void onClickUpload(){
        if (!popupWindow.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            //int dpY = Utils.dip2px(this,120);
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }else {
            popupWindow.dismiss();
        }
    }

    private void initWidget(){
        //进入页面，播放按钮 不可按
        btnPlayPause.setEnabled(false);
        //上传按钮，不可点击
        imgUpload.setEnabled(false);

        //为录音长按 按钮设置 点击监听
        btnLongClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP){
                    stopRecord();
                    if (!btnLongClick.isSelected()){
                        btnLongClick.setSelected(true);
                    }
                    if (!imgUpload.isSelected()){
                        imgUpload.setSelected(true);
                        //录音完成，上传按钮 可点击
                        imgUpload.setEnabled(true);
                    }
                    if (!btnPlayPause.isSelected()){
                        btnPlayPause.setSelected(true);
                    }
                    textLongClick.setText("长按重录");
                    //录音完成后，播放按钮 可按
                    btnPlayPause.setEnabled(true);
                    Log.i(TAG, "onTouch: 长按结束");
                }
                return false;
            }
        });


        //设置计时器的监听事件
        recodTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                //超过5分钟，停止
                Log.i(TAG, "SystemClock.elapsedRealtime() : " + SystemClock.elapsedRealtime());
                if (SystemClock.elapsedRealtime() - chronometer.getBase() > 5*60*1000){
                    stopRecord();
                    if (!btnLongClick.isSelected()){
                        btnLongClick.setSelected(true);
                    }
                    if (!imgUpload.isSelected()){
                        imgUpload.setSelected(true);
                        //录音完成，上传按钮 可点击
                        imgUpload.setEnabled(true);
                    }
                    if (!btnPlayPause.isSelected()){
                        btnPlayPause.setSelected(true);
                    }
                    textLongClick.setText("长按重录");
                    //录音完成后，播放按钮 可按
                    btnPlayPause.setEnabled(true);
                }
                //设置显示样式
               /* miss++;
                Log.i(TAG, "onChronometerTick: miss++ : " + miss);
                int mm = miss/60;
                int ss = miss%60;
                if (mm == 0){
                    chronometer.setText(ss + "″");
                }else if (mm > 0){
                    chronometer.setText(mm + "′" + ss + "″");
                }*/
            }
        });
        //recodTimer.setText("0′0″");

    }

    /**
     * 录音
     * @return
     */

    @OnLongClick(R.id.imageButton_long_click)
    public boolean onLongClick(){
        //播放后，按钮图标会变，需重新设置
        btnPlayPause.setImageDrawable(getDrawable(R.drawable.ac_record_selector1));
        //录音 计时器可见
        //recodTimer.setVisibility(View.VISIBLE);
        //textTimer.setVisibility(View.INVISIBLE);

        if (btnLongClick.isSelected()){
            btnLongClick.setSelected(false);
        }
        if (imgUpload.isSelected()){
            imgUpload.setSelected(false);
        }
        if (btnPlayPause.isSelected()){
            btnPlayPause.setSelected(false);
        }
        textLongClick.setText("长按说话");

        //先判断权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                       == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO )
                       == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                       == PackageManager.PERMISSION_GRANTED) {
            //存储权限和录音权限允许，开始录音
            startRecord();
        }else {
            //申请权限
            Log.i(TAG, "onLongClick: 申请权限");
            requestPermission(2);
        }

        return true;
    }

    private void startRecord(){
        //播放上一次的录音，未播放完时，就重新录音，需要手动 销毁播放器
        if (mMediaPlayer != null) {
            stopPlaying();
        }
        // 创建录音的服务
        intentService = new Intent(this, RecordingService.class);
        //创建存放录音文件的文件夹
        File folder = new File(Environment.getExternalStorageDirectory() + "/typb_sound");
        if (!folder.exists()) {
            folder.mkdir();
        }
        //start Chronometer
        recodTimer.setBase(SystemClock.elapsedRealtime());
        recodTimer.start();
        //start RecordingService
        startService(intentService);

    }

    private void stopRecord(){
        //stop recording
        recodTimer.stop();
        if (intentService != null) {
            stopService(intentService);
        }
    }

    /**
     *   播放
     */
    @OnClick(R.id.imageButton_play_pause)
    public void onClickPlay(){
        if (mMediaPlayer == null){
            startPlaying();
        }else if (mMediaPlayer.isPlaying()){
            pausePlaying();
        }else if (!mMediaPlayer.isPlaying()){
            resumePlaying();
        }

    }


    private void pausePlaying() {
        // 保存这次记录了的时间
        recodTimer.stop();
        recordingTime = SystemClock.elapsedRealtime()- recodTimer.getBase();

        btnPlayPause.setImageResource(play);
        mMediaPlayer.pause();
    }

    private void resumePlaying() {
        // 跳过已经记录了的时间，起到继续计时的作用
        recodTimer.setBase(SystemClock.elapsedRealtime() - recordingTime);
        recodTimer.start();

        btnPlayPause.setImageResource(pause);
        mMediaPlayer.start();

    }


    private void startPlaying() {
        //刷新计时器
        recordingTime = 0;
        recodTimer.setBase(SystemClock.elapsedRealtime());
        recodTimer.start();

        btnPlayPause.setImageResource(pause);
        mMediaPlayer = new MediaPlayer();

        try {
            String path = MyApplication.pref.getString(MyApplication.prefKey4_audio_path,"");

            Log.i(TAG, "startPlaying: path : " + path);
            mMediaPlayer.setDataSource(path);

            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完后，可重新播放，时间清零
                btnPlayPause.setImageResource(play);
                recodTimer.stop();
                stopPlaying();
            }
        });

    }


    private void stopPlaying() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }


    private void requestPermission(int requestCode){
        Log.i(TAG, "requestPermission: ");

        if (mPermissionList.size() >0){
            mPermissionList.clear();
        }

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
                Log.i(TAG, "requestPermission: permissions : " + permissions[i]);
            }
        }

        Log.i(TAG, "requestPermission: mPermissionList.size() : " + mPermissionList.size());
        if (mPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 1:
                Log.i(TAG, "onRequestPermissionsResult:  grantResults[0] " +  grantResults[0]);
                Log.i(TAG, "onRequestPermissionsResult:  grantResults[1]" + grantResults[1]);
                boolean hasPermissionDismiss = false;//有权限没有通过
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == -1) {
                        hasPermissionDismiss = true;
                    }
                }
                if (hasPermissionDismiss){
                    Toast.makeText(this,"未授权无法进行录音",Toast.LENGTH_SHORT).show();
                }

                break;
            case 2:
                boolean hasPermissionDismiss1 = false;//有权限没有通过
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == -1) {
                        hasPermissionDismiss = true;
                    }
                }
                if (hasPermissionDismiss1){
                    Toast.makeText(this,"未授权无法进行录音",Toast.LENGTH_SHORT).show();
                }else {
                    startRecord();
                }
                break;
        }
    }

    //上传音频
    /**
     *  上传图片 需要参数 ： userId，userName，userHeadImg，
     *  visionTitle，visionType（1：图片，2：视频，3：音频），bgmName（视频需传背景音乐名称）
     *  file（文件上传）， file1（视频必须封面），token
     */
    private void uploadMicro(){
        //音频路径
        String path = MyApplication.pref.getString(MyApplication.prefKey4_audio_path,"");
        if (path == null){
            return;
        }

        Log.i(TAG, "uploadMicro: 发布");
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String userName = MyApplication.pref.getString(MyApplication.prefKey13_login_userName,"");
        String userHeadImg = MyApplication.pref.getString(MyApplication.prefKey12_login_headImg,"");
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        String visionTitle = editSay.getText().toString();
        String visionType = "3";

        File file = new File(path);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);    //表单类型

        RequestBody body=RequestBody.create(MediaType.parse("multipart/form-data"),file);//表单类型
        builder.addFormDataPart("file",file.getName(),body);

        builder.addFormDataPart("userId",userId+"");
        builder.addFormDataPart("userName",userName);
        builder.addFormDataPart("userHeadImg",userHeadImg);
        builder.addFormDataPart("visionTitle",visionTitle);
        builder.addFormDataPart("visionType",visionType);
        //builder.addFormDataPart("bgmName",bgmName);
        builder.addFormDataPart("token",token);

        List<MultipartBody.Part> parts=builder.build().parts();

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.uploadMicro(parts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        int code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            Toast.makeText(RecordActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                            linearLayout.setVisibility(View.INVISIBLE);
                            textComfirm.setEnabled(true);
                            if (popupWindow.isShowing()){
                                popupWindow.dismiss();
                            }
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                            linearLayout.setVisibility(View.INVISIBLE);
                            textComfirm.setEnabled(true);
                            if (popupWindow.isShowing()){
                                popupWindow.dismiss();
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(RecordActivity.this,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        e.printStackTrace();
                        RetrofitUtil.requestError();
                        linearLayout.setVisibility(View.INVISIBLE);
                        textComfirm.setEnabled(true);
                        if (popupWindow.isShowing()){
                            popupWindow.dismiss();
                        }

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }





}
