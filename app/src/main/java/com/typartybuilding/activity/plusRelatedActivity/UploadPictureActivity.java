package com.typartybuilding.activity.plusRelatedActivity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.FragmentTitleForAc;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.CameraUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
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

public class UploadPictureActivity extends BaseActivity {

    private String TAG = "UploadPictureActivity";

    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.imageButton_upload)
    ImageButton btnUpload;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;        //上传中的布局

    @BindViews({R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5, R.id.imageView6, R.id.imageView7, R.id.imageView8, R.id.imageView9})
    ImageView imageView[] ;


    private int currentImg = 0;     //当前点击的是哪个ImageView
    private ArrayList<String> imgPathList = new ArrayList<>();      //存放图片路径

    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        imgPathList = intent.getStringArrayListExtra("picList");
        setTitle();
        initView();
    }

    private void setTitle(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTitleForAc fragmentTitle = (FragmentTitleForAc)manager.findFragmentById(R.id.fragment_title_ac);
        fragmentTitle.getTextViewTitle().setText(R.string.upload_pic_ac_str1);
        fragmentTitle.getLayoutTitle().setBackgroundColor(getResources().getColor(R.color.upload_pic_ac_bg));
    }

    private void initView(){
        if (imgPathList.size() > 0){
            for (int i = 0; i < imgPathList.size(); i++){
                //加载图片
                Glide.with(this).load(imgPathList.get(i))
                        .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                        .into(imageView[i]);
            }
        }
    }


    @OnClick(R.id.imageButton_upload)
    public void onClickUpload(){
        uploadMicro();
        linearLayout.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.INVISIBLE);
        editText.setEnabled(false);
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
        String visionType = "1";
        //File file = null;

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);    //表单类型

        for (int i = 0; i < imgPathList.size(); i++){
            File file = new File(imgPathList.get(i));
            RequestBody body=RequestBody.create(MediaType.parse("multipart/form-data"),file);//表单类型
            builder.addFormDataPart("file",file.getName(),body);
        }

        //RequestBody body1 = RequestBody.create(MediaType.parse("multipart/form-data"),file1);
        //builder.addFormDataPart("file1",file1.getName(),body1);

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
                            Toast.makeText(UploadPictureActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                            linearLayout.setVisibility(View.INVISIBLE);
                            btnUpload.setVisibility(View.VISIBLE);
                            editText.setEnabled(true);
                            delaySkip();
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                            linearLayout.setVisibility(View.INVISIBLE);
                            btnUpload.setVisibility(View.VISIBLE);
                            editText.setEnabled(true);

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(UploadPictureActivity.this,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        e.printStackTrace();
                        RetrofitUtil.requestError();
                        linearLayout.setVisibility(View.INVISIBLE);
                        btnUpload.setVisibility(View.VISIBLE);
                        editText.setEnabled(true);

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




    private void displayImage(String imagePath){

        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //加载图片
            Glide.with(this).load(imagePath)
                    .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                    .into(imageView[currentImg]);
            //imageView[currentImg].setImageBitmap(bitmap);
            if (currentImg != 8) {
                imageView[currentImg + 1].setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){

        }
    }






/* @OnClick({R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5, R.id.imageView6, R.id.imageView7, R.id.imageView8, R.id.imageView9})
    public void onClickImg(View view){
        Log.i(TAG, "onClickImg: 点击image");
        switch (view.getId()){
            case R.id.imageView1 :
                Log.i(TAG, "onClickImg: 点击image1");
                currentImg = 0;

                break;
            case R.id.imageView2 :
                currentImg = 1;

                break;
            case R.id.imageView3 :
                currentImg = 2;

                break;
            case R.id.imageView4 :
                currentImg = 3;

                break;
            case R.id.imageView5 :
                currentImg = 4;

                break;
            case R.id.imageView6 :
                currentImg = 5;

                break;
            case R.id.imageView7 :
                currentImg = 6;

                break;
            case R.id.imageView8 :
                currentImg = 7;

                break;
            case R.id.imageView9 :
                currentImg = 8;

                break;
        }
    }*/







   /* private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            } else {
                Log.i(TAG, "requestPermission: openAlbum");

            }
        }
    }*/

   /* private void openAlbum(){
        Log.i(TAG, "openAlbum: ");
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"权限不允许",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
*/


  /*  private void handleImage(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)) {
            //如果是doucument 类型的uri，通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content 类型uri，则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的uri，直接获取图片路径
            imagePath = uri.getPath();
        }
        Log.i(TAG, "handleImage: imagPath: " + imagePath);
        if (imgPathList.size() != 0) {

            if (imgPathList.size() > currentImg) {
                if (imgPathList.get(currentImg) != null) {
                    imgPathList.remove(currentImg);
                }
                Log.i(TAG, "handleImage: imgPathList size ：" + imgPathList.size());
                imgPathList.add(currentImg, imagePath);
            } else {
                imgPathList.add(currentImg, imagePath);
            }

        }else {
            imgPathList.add(currentImg,imagePath);
        }

        displayImage(imagePath);
        for (int i = 0; i < imgPathList.size(); i++){
            Log.i(TAG, "handleImage: imagePath" + i + " " + imgPathList.get(i));
        }
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
*/

}
