package com.typartybuilding.activity.plusRelatedActivity;



import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.demo.ffmpeg.FFcommandExecuteResponseHandler;
import com.demo.ffmpeg.FFmpeg;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayComplexVideoActivity;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

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
import retrofit2.Retrofit;

public class PublishMicVisionActivity extends BaseActivity {

    private String TAG = "PublishMicVisionActivity";

    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;        //上传中的布局
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;      //合成视频时的进度显示
    @BindView(R.id.imageButton_upload)
    ImageView btnUpload;           //上传按钮

    private ImageView btnPreview;   //视频预览按钮

    /*@BindView(R.id.textView_preview)
    TextView textPreview;           //提示语，点击图片可预览视频*/

    private Handler handler = new Handler();   //上传成功，延时1 秒 跳转

    private String path;           //图片 ， 视频  路径
    private int flag;             // 1, 图片  2， 视频 3，有背景音乐
    private String musicName;    //背景音乐名称
    private Bitmap bitmap;       //存放视频第一帧
    private File videoPic;      //视频第一帧

    private String musicPath;   //背景音乐路径
    private File complexVideo;  //合成的视频


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_publish_picture);
        //ButterKnife.bind(this);
        //获取上一个anctivity传递的数据
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag",-1);
        Log.i(TAG, "onCreate: flag : " + flag);

        if (flag == 1){                      //图片
            setContentView(R.layout.activity_publish_picture);
            ButterKnife.bind(this);

        }else if (flag == 2 || flag == 3){    //视频
            setContentView(R.layout.activity_publish_micvideo);
            ButterKnife.bind(this);
            setPreviewOnclick();
        }

        path = intent.getStringExtra("path");
        if (flag == 3) {
            musicName = intent.getStringExtra("musicName");
            musicPath = intent.getStringExtra("musicPath");
        }
        Log.i(TAG, "onCreate: path : " + path);
        initView();

    }

    /**
     * 合成音视频
     */
    private void complexVideo(){
        //创建存放合成视频文件的文件夹
        File folder = new File(Environment.getExternalStorageDirectory() + "/typb_video");
        if (!folder.exists()) {
            folder.mkdir();
        }
        complexVideo = new File(folder, System.currentTimeMillis() +"complexVideo.mp4");

        if (FFmpeg.getInstance(this).isSupported()){
            FFmpeg.getInstance(this).medaiMux(path, musicPath, complexVideo.getAbsolutePath(), new FFcommandExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.i(TAG, "onSuccess: message : "  + message);
                    //更新到相册
                    Utils.refreshToAlbum(complexVideo);
                }

                @Override
                public void onProgress(String message) {
                    Log.i(TAG, "onProgress: message : " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.i(TAG, "onFailure: message : " + message) ;
                }

                @Override
                public void onStart() {
                    linearLayout1.setVisibility(View.VISIBLE);
                    //相关按钮不可见
                    btnInvisible();
                }

                @Override
                public void onFinish() {
                    linearLayout1.setVisibility(View.INVISIBLE);
                    //相关按钮可见
                    btnVisible();
                    refreshVideo(complexVideo);
                }
            });
        }
    }

    private void btnVisible(){
        btnUpload.setVisibility(View.VISIBLE);
        //预览按钮可见
        if (btnPreview != null){
            btnPreview.setVisibility(View.VISIBLE);
        }
        //图片可点击
        imageView.setEnabled(true);
        //输入框可点击
        editText.setEnabled(true);

    }

    private void btnInvisible(){
        btnUpload.setVisibility(View.INVISIBLE);
        //预览按钮不可见
        if (btnPreview != null){
            btnPreview.setVisibility(View.INVISIBLE);
        }
        //图片不可点击
        imageView.setEnabled(false);
        //输入框不可点击
        editText.setEnabled(false);
    }

    @OnClick(R.id.imageView)
    public void onClick(){
        //onClickPlayMicVideo();
    }

    private void onClickPlayMicVideo(){
        Intent intentAc = new Intent(this, PlayComplexVideoActivity.class);
        if (flag == 3){
            intentAc.putExtra("videoPath",complexVideo.getAbsolutePath());
            startActivity(intentAc);
        }else if (flag == 2){
            intentAc.putExtra("videoPath",path);
            startActivity(intentAc);
        }
    }

    private void setPreviewOnclick(){
        btnPreview = findViewById(R.id.imageButton_preview);
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPlayMicVideo();
            }
        });
    }


    private void initView(){
        if (flag == 1){
            //加载图片
            Glide.with(this).load(path)
                    .apply(MyApplication.requestOptions)
                    .into(imageView);
          /*  //提示语，点击图片可预览视频  不可见
            textPreview.setVisibility(View.INVISIBLE);*/

        }else if (flag == 2){
            //Utils.getVideoPicture(imageView,path);
            //bitmap = Utils.rotateBitmap(Utils.getVideoThumb(path),90);
            bitmap = Utils.getVideoThumb(path);
            Glide.with(this).load(bitmap)
                    .apply(MyApplication.requestOptions)
                    .into(imageView);
            bitmapToJpg();
        }else if (flag == 3){
            //bitmap = Utils.rotateBitmap(Utils.getVideoThumb(path),90);
            bitmap = Utils.getVideoThumb(path);
            Glide.with(this).load(bitmap)
                    .apply(MyApplication.requestOptions)
                    .into(imageView);
            bitmapToJpg();
            //合成
            if (musicPath != null) {
                complexVideo();
            }
        }
    }

    //bitmap转为jpg
    private void bitmapToJpg(){
        //创建存放图片文件的文件夹
        File folder = new File(Environment.getExternalStorageDirectory() + "/typb_pic");
        if (!folder.exists()) {
            folder.mkdir();
        }
        videoPic = new File(folder, System.currentTimeMillis() +"video.jpg");
        try {
            FileOutputStream out = new FileOutputStream(videoPic);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "bitmapToJpg: file1 path : " + videoPic.getAbsolutePath());
        Log.i(TAG, "bitmapToJpg: file1 size : " + videoPic.length());
        //refreshVideo();
    }

    //通知相册更新
    private void refreshVideo(File file){
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            sendBroadcast(intent);
        }
    }

    @OnClick(R.id.imageButton_back)
    public void onClickBack(){
        finish();
    }

    //发布
    @OnClick(R.id.imageButton_upload)
    public void onClickUpload(){
        String visionTitle = editText.getText().toString();
        if (TextUtils.isEmpty(visionTitle)){
            Toast.makeText(this,"发布内容不能为空,请填写",Toast.LENGTH_SHORT).show();
            return;
        }
        uploadMicro();
        linearLayout.setVisibility(View.VISIBLE);
        //相关按钮不可见
        btnInvisible();
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

        String visionTitle = editText.getText().toString();
        String visionType = null;
        String bgmName = null;
        File file = null;
        File file1 = null;

        if (flag == 1){          //图片
            file = new File(path);
            visionType = "1";
            file1 = file;
        }else if (flag == 2 ){   //没有背景音乐
            file = new File(path);
            file1 = videoPic;
            visionType = "2";
            bgmName = musicName;
            if (bgmName == null){
                bgmName = "";
            }
        }else if (flag == 3){  //有背景音乐
            file = complexVideo;
            file1 = videoPic;
            visionType = "2";
            bgmName = musicName;
            if (bgmName == null){
                bgmName = "";
            }
        }

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);    //表单类型

        RequestBody body=RequestBody.create(MediaType.parse("multipart/form-data"),file);//表单类型
        builder.addFormDataPart("file",file.getName(),body);

        builder.addFormDataPart("userId",userId+"");
        builder.addFormDataPart("userName",userName);
        builder.addFormDataPart("userHeadImg",userHeadImg);
        builder.addFormDataPart("visionTitle",visionTitle);
        builder.addFormDataPart("visionType",visionType);
        builder.addFormDataPart("token",token);

        Log.i(TAG, "uploadMicro: token : " + token);

        //视频 才上传封面 和 背景音乐
        if (flag == 3 || flag == 2) {
            builder.addFormDataPart("bgmName", bgmName);

            RequestBody body1 = RequestBody.create(MediaType.parse("multipart/form-data"),file1);
            builder.addFormDataPart("file1",file1.getName(),body1);
        }

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
                            Toast.makeText(PublishMicVisionActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                            linearLayout.setVisibility(View.INVISIBLE);
                            //相关按钮可见
                            btnVisible();
                            //延时跳转到拍摄页面
                            delaySkip();

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                            linearLayout.setVisibility(View.INVISIBLE);
                            //相关按钮可见
                            btnVisible();
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(PublishMicVisionActivity.this,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        e.printStackTrace();
                        RetrofitUtil.requestError();
                        linearLayout.setVisibility(View.INVISIBLE);
                        //相关按钮可见
                        btnVisible();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void delaySkip(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },1000);
    }


}
