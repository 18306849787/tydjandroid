package com.typartybuilding.activity.myRelatedActivity;

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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.typartybuilding.R;
import com.typartybuilding.activity.loginRelatedActivity.IntoFaceActivity2;
import com.typartybuilding.activity.plusRelatedActivity.Camera2Activity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.personaldata.PersonalInfo;
import com.typartybuilding.gsondata.personaldata.TaMicro;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.GlideCacheUtil;
import com.typartybuilding.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;



public class EditProfileActivity extends WhiteTitleBaseActivity {

    private String TAG = "EditProfileActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;                //标题
    @BindView(R.id.imageView_headimg)
    CircleImageView headImg;           //头像

    //姓名， 身份证号，手机号，党员认证
    @BindViews({R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView5, R.id.textView5_certifi})
    TextView textView[];
    @BindView(R.id.textView4)
    TextView textSite;            //地区

    private PopupWindow popupWindow;  //底部弹窗
    private View popView;             //弹窗布局

    private PopupWindow popupWindow1;  //底部弹窗  ,上传头像时，显示的弹窗，一个进度条
    private View popView1;             //弹窗布局


    public static final int TAKE_PHOTO = 1;      //调相机
    public static final int CHOOSE_PHOTO = 2;    //调相册
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA,};
    private List<String> mPermissionList = new ArrayList<>();

    //切换地址的选择器
    private OptionsPickerView pvOptions;
    //地址数据
    private List<String> siteList = new ArrayList<>();
    String siteList2 = "小店区、迎泽区、杏花岭区、尖草坪区、万柏林区、晋源区、古交市、清徐县、阳曲县、娄烦县、市直工委、市委工信工委、市委住建工委、市委教育工委、市委卫健工委、市公安局党委、市国资委党委、市委城乡管理工委、西山示范区、综改示范区、中北高新技术开发区";

    private PersonalInfo personalInfo;
    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        //设置标题
        textTitle.setText("完善个人信息");
        textTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        //获取上一个activity传递的个人数据
        Intent intent = getIntent();
        personalInfo = (PersonalInfo)intent.getSerializableExtra("PersonalInfo");
        //获取到数据后，初始化页面
        initView();
        //设置用户已选择的地址
        String site = MyApplication.pref.getString(MyApplication.prefKey3,"");
        if (site != ""){
            textSite.setText("山西 太原 " + site);
        }
        //初始化修改头像的弹窗
        initPopupWindow();
        //初始化，上传头像过程的弹窗
        initPopupWindow1();
        //初始化地址信息
        initSiteData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (popupWindow != null) {
            if (popupWindow.isShowing()){
                popupWindow.dismiss();
            }
        }
    }

    private void initView(){
        if (personalInfo != null){
            PersonalInfo.Data data = personalInfo.data;
            Log.i(TAG, "initView: headImg : " + personalInfo.data.headImg);

            String headimg = MyApplication.pref.getString(MyApplication.prefKey12_login_headImg,"");
            //加载头像
            Glide.with(this).load(headimg)
                    .apply(MyApplication.requestOptions2)
                    .into(headImg);
            //姓名
            textView[0].setText(data.username);
            //身份证号
            if (data.idCard != null) {
                if (data.idCard.length() == 18) {
                    String str1 = data.idCard.substring(0, 3);
                    String str2 = data.idCard.substring(14, 17);
                    //身份证号
                    textView[1].setText(str1 + "****" + str2);
                }
            } else {
                textView[1].setText(data.idCard);
            }
            //手机号
            textView[2].setText(data.phone);
            //党员认证
            if (data.userType == 2){        //是党员
                //textView[3].setText("党员");
                textView[3].setVisibility(View.VISIBLE);
                textView[4].setVisibility(View.GONE);
            }else {                         //非党员
                //textView[3].setText("非党员");
                textView[3].setVisibility(View.GONE);
                textView[4].setVisibility(View.VISIBLE);
            }
        }
    }

    private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_modify_head_img, null);
        Button btnCamera = popView.findViewById(R.id.button_camera);
        Button btnAbum = popView.findViewById(R.id.button_album);
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
                Utils.backgroundAlpha(EditProfileActivity.this,1f);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        //拍照 设置头像
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先请求权限
                requestPermission(TAKE_PHOTO);
            }
        });
        //从相册 设置头像
        btnAbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先请求权限
                //requestPermission(CHOOSE_PHOTO);
                openPictureSelector();
            }
        });

    }

    private void showPopupWindow1(){
        if (!popupWindow1.isShowing()) {
            popupWindow1.showAtLocation(popView1, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this, 0.7f);
        }
    }

    private void hidePopupWindow1(){
        if (popupWindow1 != null){
            if (popupWindow1.isShowing()){
                popupWindow1.dismiss();
            }
        }
    }

    private void initPopupWindow1(){
        popView1 = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_progressbar, null);

        popupWindow1 = new PopupWindow(popView1,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        popupWindow1.setTouchable(true);
        //点击外部消失
        popupWindow1.setOutsideTouchable(true);
        popupWindow1.setFocusable(true);
        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(EditProfileActivity.this,1f);
            }
        });
    }

    //点击头像，弹出窗口，从相册或拍摄 修改头像
    @OnClick(R.id.imageView_headimg)
    public void onClickHeadImg(){

       if (personalInfo.data.userType == 1 || personalInfo.data.userType == 2) {
            if (!popupWindow.isShowing()) {
                popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
                //改变屏幕透明度
                Utils.backgroundAlpha(this, 0.7f);
            }
        }else if (personalInfo.data.userType == 3){
            Toast.makeText(this,"请先注册",Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.imageView1)
    public void onClickJT(){

      /*  if (personalInfo.data.userType == 1 || personalInfo.data.userType == 2) {
            if (!popupWindow.isShowing()) {
                popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
                //改变屏幕透明度
                Utils.backgroundAlpha(this, 0.7f);
            }
        }else if (personalInfo.data.userType == 3){
            Toast.makeText(this,"请先注册",Toast.LENGTH_SHORT).show();
        }*/

    }

    //姓名， 身份证号， 手机号码， 地区， 党员认证， 修改密码， 录入人脸
    @OnClick({R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5_certifi, R.id.textView6, R.id.textView7})
    public void onClickTextView(View view){
        switch (view.getId()){
            case R.id.textView1:
                editProfile(1);
                break;
            case R.id.textView2:
                editProfile(2);
                break;
            case R.id.textView3:
                editProfile(3);
                break;
            case R.id.textView4:
                editProfile(4);
                break;
            case R.id.textView5_certifi:
                editProfile(5);
                break;
            case R.id.textView6:
                editProfile(6);
                break;
            case R.id.textView7:
                editProfile(7);
                break;
        }
    }

    private void editProfile(int i){
        if (i == 1){        //修改 姓名

        }else if (i == 2){  //身份证号

        }else if (i == 3){  //手机号

        }else if (i == 4){  //地区
            initOptionsPicker();

        }else if (i == 5){  //党员认证
            if (personalInfo.data.userType == 1) {
                Intent intentAc = new Intent(this, PartyCertificationActivity.class);
                //intentAc.putExtra("phone",personalInfo.data.phone);
                startActivityForResult(intentAc, 3);

            }else if (personalInfo.data.userType == 3){
                Toast.makeText(this,"请先注册",Toast.LENGTH_SHORT).show();
            }

        }else if (i == 6){  //修改密码
            if (personalInfo.data.userType == 1 || personalInfo.data.userType == 2) {
                Intent intentAc = new Intent(this, ResetPasswordActivity.class);
                startActivity(intentAc);

            }else if (personalInfo.data.userType == 3){
                Toast.makeText(this,"请先注册",Toast.LENGTH_SHORT).show();
            }

        }else if (i == 7){  //录入人脸
            if (personalInfo.data.userType == 1 || personalInfo.data.userType == 2) {
//                Intent intentAc = new Intent(this, IntoFaceActivity2.class);
//                intentAc.putExtra("flag",1);
//                startActivity(intentAc);

            }else if (personalInfo.data.userType == 3){
                Toast.makeText(this,"请先注册",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void openPictureSelector(){
        PictureSelector.create(EditProfileActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.SINGLE)
                .compress(true)
                .previewImage(true)
                .isCamera(false)
                .forResult(CHOOSE_PHOTO);
    }

    private void handlePicture(Intent data){
        List<LocalMedia> selectList2 = PictureSelector.obtainMultipleResult(data);
        String picPath = null;
        if (selectList2 != null){
            picPath = selectList2.get(0).getPath();
        }
        //上传头像
        headUpload(picPath,CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){

            case CHOOSE_PHOTO :
                if (resultCode == RESULT_OK){
                    Log.i(TAG, "onActivityResult: ");
                    //handleImage(data);
                    handlePicture(data);
                }
                break;
            case TAKE_PHOTO :
                Log.i(TAG, "onActivityResult: headImage : " + headImage.getAbsolutePath());
                Log.i(TAG, "onActivityResult: headImage size : " + headImage.length());
                if (headImage.length() < 10){
                    break;
                }
                headUpload(headImage.getAbsolutePath(),TAKE_PHOTO);
                //displayCameraImg();
                break;

            case 3:
                if (resultCode == RESULT_OK){
                    int result = data.getIntExtra("result",-1);
                    Log.i(TAG, "onActivityResult: result : " + result);
                    if (result == 1){
                        textView[3].setVisibility(View.VISIBLE);
                        textView[4].setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    private Handler handler = new Handler();
    private void delayShowPopupWindow(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showPopupWindow1();
            }
        },200);
    }

    /**
     * 上传头像
     */
    private void headUpload(String imagePath,int requestCode){
        Log.i(TAG, "headUpload: 开始上传");
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        //弹窗
        delayShowPopupWindow();
        //showPopupWindow1();
        File file = null;
        Log.i(TAG, "headUpload: 压缩前file length : " + new File(imagePath).length());
        try {
            file = new Compressor(this).compressToFile(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "headUpload: 压缩后file length : " + file.length());
        //File file = new File(imagePath);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);    //表单类型
        RequestBody body=RequestBody.create(MediaType.parse("multipart/form-data"),file);//表单类型
        builder.addFormDataPart("file",file.getName(),body);
        builder.addFormDataPart("userId",userId+"");
        builder.addFormDataPart("token",token);
        Log.i(TAG, "headUpload: token : " + token);

        List<MultipartBody.Part> parts=builder.build().parts();

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.headUpload(parts)
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
                            if (requestCode == CHOOSE_PHOTO) {
                                //上传成功，替换头像
                                displayAlbumImg(imagePath);

                            }else if (requestCode == TAKE_PHOTO){
                                displayCameraImg();
                            }
                            //记录是否更改了头像
                            MyApplication.isChageHeadimg = true;
                            //隐藏进度弹窗
                            hidePopupWindow1();

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                            hidePopupWindow1();
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(EditProfileActivity.this,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        hidePopupWindow1();
                        Toast.makeText(EditProfileActivity.this,"上传失败",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    //请求权限， 从相册获取照片
    private void requestPermission(int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //从相册
            if (requestCode == CHOOSE_PHOTO) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    Log.i(TAG, "requestPermission: openAlbum");
                    openAlbum();
                }

            // 从相机
            }else if (requestCode == TAKE_PHOTO){
                if (mPermissionList.size() > 0) {
                    mPermissionList.clear();
                }
                for (int i = 0; i < permissions.length; i++) {
                    if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                        mPermissionList.add(permissions[i]);//添加还未授予的权限
                    }
                }
                if (mPermissionList.size() > 0) {
                    ActivityCompat.requestPermissions(this, permissions, 2);
                }else {
                    //打开相机
                    openCamera();
                }

            }

        }
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
            case 2:
                boolean hasPermissionDismiss = false;//有权限没有通过
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == -1) {
                        hasPermissionDismiss = true;
                    }
                }
                if (!hasPermissionDismiss){
                    openCamera();
                }else {
                    Toast.makeText(this,"未授权相关功能无法使用",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    //打开相机
    private Uri imageUri;
    private File headImage;
    private void openCamera(){
        headImage = new File(getExternalCacheDir(),"headImg.jpg");
        try {
            if (headImage.exists()){
                headImage.delete();
            }
            headImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(EditProfileActivity.this,
                    "com.example.cameraalbum.fileprovider",headImage);
        }else {
            imageUri = Uri.fromFile(headImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,TAKE_PHOTO);
    }

    private void displayCameraImg(){
        if (imageUri != null){
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                //加载图片
                Glide.with(this).load(bitmap)
                        .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                        .into(headImg);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //清除Glide缓存
        //GlideCacheUtil.getInstance().clearImageAllCache(this);
    }


    private void displayAlbumImg(String imagePath){

        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //加载图片
            Glide.with(this).load(imagePath)
                    .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                    .into(headImg);
            //imageView[currentImg].setImageBitmap(bitmap);
        }
        //清除缓存
        //GlideCacheUtil.getInstance().clearImageAllCache(this);
    }


    private void openAlbum(){
        Log.i(TAG, "openAlbum: ");
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void handleImage(Intent data){
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

        //上传头像
        headUpload(imagePath,CHOOSE_PHOTO);
        //displayImage(imagePath);
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





    //初始化地点选择器
    private void initOptionsPicker(){
        //条件选择器
        pvOptions = new OptionsPickerBuilder(EditProfileActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String site = siteList.get(options1);
                //调修改地区的接口
                savePerson(site);
               /* textSite.setText("山西 太原 " + site);
                MyApplication.editor.putString(MyApplication.prefKey3,site);
                MyApplication.editor.apply();*/


            }
        })
                .setSelectOptions(3)
                .setOutSideCancelable(false)
                .build();
        pvOptions.setPicker(siteList);
        pvOptions.show();
    }

    /**
     *  修改地区
     */
    private void savePerson(String address){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.savePerson(userId,address,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        int code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 修改地区code ： " + code);
                        if (code == 0){
                            textSite.setText("山西 太原 " + address);
                            MyApplication.editor.putString(MyApplication.prefKey3,address);
                            MyApplication.editor.apply();
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(EditProfileActivity.this,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError:  e :" + e );
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initSiteData(){
        String[] address = siteList2.split("、");
        if (address!=null&&address.length>0){
            for (int i=0;i<address.length;i++){
                siteList.add(address[i]);
            }
        }

//        siteList.add(getResources().getString(R.string.text1));
//        siteList.add(getResources().getString(R.string.text2));
//        siteList.add(getResources().getString(R.string.text3));
//        siteList.add(getResources().getString(R.string.text4));
//        siteList.add(getResources().getString(R.string.text5));
//        siteList.add(getResources().getString(R.string.text6));
//        siteList.add(getResources().getString(R.string.text7));
//        siteList.add(getResources().getString(R.string.text8));
//        siteList.add(getResources().getString(R.string.text9));
//        siteList.add(getResources().getString(R.string.text10));
//        siteList.add(getResources().getString(R.string.text11));
//        siteList.add(getResources().getString(R.string.text12));

       /* siteList.add(getResources().getString(R.string.text13));
        siteList.add(getResources().getString(R.string.text14));
        siteList.add(getResources().getString(R.string.text15));
        siteList.add(getResources().getString(R.string.text16));
        siteList.add(getResources().getString(R.string.text17));
        siteList.add(getResources().getString(R.string.text18));
        siteList.add(getResources().getString(R.string.text19));
        siteList.add(getResources().getString(R.string.text20));*/

    }




}
