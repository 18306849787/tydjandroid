package com.typartybuilding.activity.quanminlangdu.mac;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeAct;
import com.typartybuilding.activity.quanminlangdu.entity.BookEntity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import retrofit2.Retrofit;

public class ReadInfoActivity extends WhiteTitleBaseActivity {

    private String TAG = "ReadInfoActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.textView_headline)
    TextView textHeadLine;

    @BindView(R.id.imageView_shiting)
    ImageView imgShiTing;         //试听按钮
   /* @BindView(R.id.imageView_fabu)
    ImageView imgFaBu;            //发布按钮*/

    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.textView_upload)
    TextView imgFaBu;

    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;        //上传中的布局


    private PopupWindow popupWindow1;  //底部弹窗  ,上传头像时，显示的弹窗，一个进度条
    private View popView1;             //弹窗布局
    private ImageView imgPlay;        //播放按钮
    private SeekBar seekBar;          //进度条
    private TextView nowTime;         //播放的当前时间
    private TextView playDuration;    //音频时长
    private boolean isFirst = true;   //标记 是否是第一次播放
    private MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();

    private String picPath;           //封面图路径
    private String audioPath;         //音频路径
    private String audioTitle;        //音频标题
    private int readId;               //音频 id

    private BookEntity bookEntity;    //音频数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_info);
        ButterKnife.bind(this);
        //设置标题栏标题
        textTitle.setText("朗读信息");
        //获取上一个antivity 传递的数据
        Intent intent = getIntent();
        audioPath = intent.getStringExtra("path");
        bookEntity = (BookEntity)intent.getBundleExtra("book").get("book");
        Log.i(TAG, "onCreate: audioPath : " + audioPath);
        Log.i(TAG, "onCreate: bookEntity : " + bookEntity);
        //设置音频标题
        if (bookEntity != null) {
            audioTitle = bookEntity.getReadTitle();
            readId = Integer.valueOf(bookEntity.getReadId());
            Log.i(TAG, "onCreate: audioTitle ； " + audioTitle);
        }
        textHeadLine.setText(audioTitle);
        //初始化弹窗
        initPopupWindow1();
        //音频未准备好时，seekbar不可拖动
        seekBar.setEnabled(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mHandler != null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private void showPopupWindow1(){
        if (!popupWindow1.isShowing()) {
            popupWindow1.showAtLocation(popView1, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this, 0.7f);
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
        //seekBar.setPadding(0,0,0,0);
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

    //设置播放点击事件
    private void setOnclickPlay(){
        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i(TAG, "onClick: audioPath : " + audioPath);
                    if (isFirst) {
                        mediaPlayer.setDataSource(audioPath);
                        mediaPlayer.prepare();

                        mediaPlayer.start();
                        //设置进度条
                        setSeekBar();
                        //设置音频时长
                        int duration = mediaPlayer.getDuration();
                        Log.i(TAG, "onPrepared: duration ; " + duration);
                        playDuration.setText(Utils.formatTime(duration/1000));

                        imgPlay.setSelected(true);
                        isFirst = false;
                        updateSeekBar();

                    }else {
                        if (mediaPlayer.isPlaying()){
                            mediaPlayer.pause();
                            imgPlay.setSelected(false);
                            mHandler.removeCallbacks(mRunnable);
                        }else {
                            mediaPlayer.start();
                            imgPlay.setSelected(true);
                            updateSeekBar();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                isFirst = true;
                mediaPlayer.reset();
                imgPlay.setSelected(false);
                mHandler.removeCallbacks(mRunnable);
                seekBar.setProgress(0);
                nowTime.setText("00:00");
                seekBar.setEnabled(false);
            }
        });
    }

    private void initPopupWindow1(){
        popView1 = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_qmld_shiting, null);

        popupWindow1 = new PopupWindow(popView1,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView textClose = popView1.findViewById(R.id.textView_close);
        imgPlay = popView1.findViewById(R.id.imageView_play);
        seekBar = popView1.findViewById(R.id.seekBar);
        nowTime = popView1.findViewById(R.id.textView_now_time);
        playDuration = popView1.findViewById(R.id.textView_play_duration);

        popupWindow1.setTouchable(true);
        //点击外部消失
        popupWindow1.setOutsideTouchable(true);
        popupWindow1.setFocusable(true);
        //创建播放器
        initPlayer();

        setOnclickPlay();

        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow1.isShowing()){
                    popupWindow1.dismiss();
                }
            }
        });

        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(ReadInfoActivity.this,1f);
                if (mediaPlayer != null){
                    mediaPlayer.reset();
                    isFirst = true;
                }
            }
        });
    }

    private void selectPicture(){
        PictureSelector.create(ReadInfoActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.SINGLE)
                .compress(true)
                .previewImage(true)
                .isCamera(false)
                .forResult(1);
    }

    private void handlePicture(Intent data){
        List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
        if (selectList != null) {
            picPath = selectList.get(0).getPath();
            Log.i(TAG, "onActivityResult: pic path : " + picPath);
        }
        //加载图片
        Glide.with(this).load(picPath)
                .apply(MyApplication.requestOptions11)
                .into(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 1 :
                if (resultCode == RESULT_OK){
                    Log.i(TAG, "onActivityResult: ");
                    handlePicture(data);
                }
                break;
        }
    }

    //上传中布局
    private void uploadingLayout(){
        //上传进度条可见
        linearLayout.setVisibility(View.VISIBLE);
        //试听和上传按钮不可点击
        imgShiTing.setEnabled(false);
        imgFaBu.setEnabled(false);
        frameLayout.setEnabled(false);
        //改变屏幕透明度
        Utils.backgroundAlpha(this, 0.7f);

    }

    //上传后
    private void uploadedLayout(){
        //上传进度条不可见
        linearLayout.setVisibility(View.INVISIBLE);
        //试听和上传按钮可点击
        imgShiTing.setEnabled(true);
        imgFaBu.setEnabled(true);
        frameLayout.setEnabled(true);
        //改变屏幕透明度
        Utils.backgroundAlpha(this, 1f);
    }


    @OnClick({R.id.imageView_shiting, R.id.textView_upload, R.id.frameLayout,R.id.imageView})
    public void onClick(View view){
        switch (view.getId()){
            //试听
            case R.id.imageView_shiting:
                showPopupWindow1();
                break;
            //发布
            case R.id.textView_upload:
                //上传中的布局
                uploadingLayout();
                uploadMicro();
                break;
            //发布
            case R.id.frameLayout:
                //上传中的布局
                uploadingLayout();
                uploadMicro();
                break;

            //添加封面图
            case R.id.imageView:
                selectPicture();
                break;

        }
    }

    /**
     *  上传图片 需要参数 ： userId，userName，userHeadImg，
     *  visionTitle，visionType（1：图片，2：视频，3：音频），bgmName（视频需传背景音乐名称）
     *  file（文件上传）， file1（视频必须封面），token
     */
    private void uploadMicro(){

        Log.i(TAG, "uploadMicro: 发布");
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String userName = MyApplication.pref.getString(MyApplication.prefKey13_login_userName,"");
        String userHeadImg = MyApplication.pref.getString(MyApplication.prefKey12_login_headImg,"");
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        String visionTitle = audioTitle;
        String visionType = "3";
        String bgmName = null;

        //音频文件
        File file = new File(audioPath);
        //封面文件
        File file1 = null;
        if (picPath != null){
            file1 = new File(picPath);
        }

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);    //表单类型
        //音频文件
        RequestBody body=RequestBody.create(MediaType.parse("multipart/form-data"),file);//表单类型
        builder.addFormDataPart("file",file.getName(),body);
        //音频封面
        if (file1 != null) {
            RequestBody body1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
            builder.addFormDataPart("file1", file1.getName(), body1);
        }

        builder.addFormDataPart("userId",userId+"");
        builder.addFormDataPart("userName",userName);
        builder.addFormDataPart("userHeadImg",userHeadImg);
        if (visionTitle != null) {
            builder.addFormDataPart("visionTitle",visionTitle);
        }else {
            //visionTitle = ".";
            //builder.addFormDataPart("visionTitle",visionTitle);
        }
        builder.addFormDataPart("visionType",visionType);
        builder.addFormDataPart("token",token);
        builder.addFormDataPart("readId",String.valueOf(readId));
        //builder.addFormDataPart("bgmName", bgmName);

        Log.i(TAG, "uploadMicro: token : " + token);

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
                            //调 增加朗读次数的接口
                            addRead();
                            Toast.makeText(ReadInfoActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                            //上传后的布局
                            uploadedLayout();
                            //跳转到读单页面
                            Intent intentAc = new Intent(ReadInfoActivity.this, HomeAct.class);
                            startActivity(intentAc);
//                            Intent intentAc = new Intent(ReadInfoActivity.this,BookListActivity.class);
//                            startActivity(intentAc);

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                            //上传后的布局
                            uploadedLayout();
                            Toast.makeText(ReadInfoActivity.this,"上传失败",Toast.LENGTH_SHORT).show();


                        }else if (code == 10){
                            RetrofitUtil.tokenLose(ReadInfoActivity.this,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        e.printStackTrace();
                        RetrofitUtil.requestError();
                        //上传后的布局
                        uploadedLayout();

                        Toast.makeText(ReadInfoActivity.this,"上传失败",Toast.LENGTH_SHORT).show();

                        if (e instanceof HttpException){
                            HttpException httpException = (HttpException) e;
                            Log.i(TAG, "onError: da");
                            try{
                                String responseString = httpException.response().errorBody().string();
                                Log.e(TAG, "onError: responseString : " + responseString);
                            }catch(IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void addRead(){
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.addRead(readId,userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        
                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        int code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 增加朗读次数code : " + code);
                        if (code == 0){
                            
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(ReadInfoActivity.this,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "onError: throwable : " + throwable );
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



}
