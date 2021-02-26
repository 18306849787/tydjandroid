package com.typartybuilding.activity.myRelatedActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.dreamwish.GoodPeopleDetailsActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.DreamWishAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.MyDreamWishAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.TaDreamWishAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.dreamwish.DreamWishData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class WishClaimActivity extends WhiteTitleBaseActivity {

    private String TAG = "WishClaimActivity";

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private PopupWindow popupWindow1;  //底部弹窗  ,上传头像时，显示的弹窗，一个进度条
    private View popView1;             //弹窗布局

    private PopupWindow popupWindow;  //弹窗, 提示用户， 完成心愿后需要上传图片到后台审核
    private View popView;             //弹窗布局

    private int aspirationId;         //上传图片的参数，心愿id
    private int currentPos;         //上传图片的那条心愿的 下标

    private List<String> picList = new ArrayList<>();    //存放照片的路径

    public static final int TAKE_PHOTO = 1;      //调相机
    public static final int CHOOSE_PHOTO = 2;    //调相册
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,};
    private List<String> mPermissionList = new ArrayList<>();

    private List<DreamWishData.WishData> dataList = new ArrayList<>();
    private MyDreamWishAdapter adapter ;

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int pageNo = 1;
    private int pageSize = 20;
    private int pageCount;

    private boolean isRefresh;     //是否 在 下拉刷新
    private boolean isLoadMore;    //是否 在 上拉加载更多

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_claim);
        ButterKnife.bind(this);

        initRecyclerView();
        setRefreshLayout();
        //获取心愿数据
        getMyOrTaWishData();
        //弹窗, 提示用户， 完成心愿后需要上传图片到后台审核
        initPopupWindow();
        //上传中的弹窗
        initPopupWindow1();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //隐藏上传照片的弹窗
        //hidePopupWindow();
    }

    @OnClick(R.id.imageView_back)
    public void onClickBack(){
        finish();
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyDreamWishAdapter(dataList,this);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line_edufilm));
        recyclerView.addItemDecoration(dividerLine);

        recyclerView.setFocusable(false);
        recyclerView.setAdapter(adapter);

    }

    private void setRefreshLayout(){
        //refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh = true;
                pageNo = 1;
                getMyOrTaWishData();

            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount){
                    getMyOrTaWishData();
                    isLoadMore = true;

                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
    }

    private void initData(DreamWishData dreamWishData){

        //下拉刷新，需清空数据
        if (isRefresh){
            if (dataList.size() > 0){
                dataList.clear();
            }
            isRefresh = false;
        }

        DreamWishData.WishData [] wishData = dreamWishData.data.rows;
        if (wishData != null){
            for (int i = 0; i < wishData.length; i++){
                dataList.add(wishData[i]);
            }
            adapter.notifyDataSetChanged();
        }

    }

    private void getMyOrTaWishData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getMyOrTaWishData(pageNo,pageSize,userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DreamWishData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DreamWishData dreamWishData) {
                        int code = Integer.valueOf(dreamWishData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){

                            pageCount = dreamWishData.data.pageCount;
                            pageNo++;
                            //关闭下拉刷新
                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            initData(dreamWishData);

                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(dreamWishData.message);

                            if (isRefresh){
                                refreshLayout.finishRefresh(true);
                            }
                            if (isLoadMore){
                                refreshLayout.finishLoadMore(true);
                                isLoadMore = false;
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(WishClaimActivity.this,dreamWishData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();

                        if (isRefresh){
                            refreshLayout.finishRefresh(true);
                        }
                        if (isLoadMore){
                            refreshLayout.finishLoadMore(true);
                            isLoadMore = false;
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    //图片上传成功后，修改心愿状态 为 正在审核中
    private void changeWishStatus(){
        if (currentPos < dataList.size()) {
            dataList.get(currentPos).aspirationStatus = 4;
        }
        if (adapter != null){
            adapter.notifyItemChanged(currentPos);
        }
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
                Utils.backgroundAlpha(WishClaimActivity.this,1f);
            }
        });
    }


    public void openPictureSelector(int aspirationId, int currentPos){
        //记录心愿数据的id 和 下标
        this.aspirationId = aspirationId;
        this.currentPos = currentPos;

        PictureSelector.create(WishClaimActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.MULTIPLE)
                .compress(true)
                .previewImage(true)
                .isCamera(false)
                .forResult(CHOOSE_PHOTO);
    }

    private void handlePicture(Intent data){
        List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);

        if (selectList != null) {
            if (picList.size() > 0) {
                picList.clear();
            }
            for (int i = 0; i < selectList.size(); i++) {
                picList.add(selectList.get(i).getPath());
                Log.i(TAG, "onActivityResult: pic path : " + selectList.get(i).getPath());
            }
        }
        //上传图片
        uploadPic();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case CHOOSE_PHOTO :
                if (resultCode == RESULT_OK){
                    Log.i(TAG, "onActivityResult: ");
                    handlePicture(data);
                }
                break;
            case TAKE_PHOTO :
                Log.i(TAG, "onActivityResult: headImage : " + wishImage.getAbsolutePath());
                Log.i(TAG, "onActivityResult: headImage size : " + wishImage.length());
                if (wishImage.length() < 10){
                    break;
                }
                //更新到相册
                Utils.refreshToAlbum(wishImage);
                uploadPic();

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

    private void uploadPic(){
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        //弹窗
        //delayShowPopupWindow();
        showPopupWindow1();

       /* File file = null;
        Log.i(TAG, "headUpload: 压缩前file length : " + new File(imagePath).length());
        try {
            file = new Compressor(this).compressToFile(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "headUpload: 压缩后file length : " + file.length());*/

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);    //表单类型
        for (int i = 0; i < picList.size(); i++){
            File file = new File(picList.get(i));
            RequestBody body=RequestBody.create(MediaType.parse("multipart/form-data"),file);//表单类型
            builder.addFormDataPart("files",file.getName(),body);
        }

        builder.addFormDataPart("aspirationId",aspirationId+"");
        builder.addFormDataPart("token",token);
        Log.i(TAG, "headUpload: token : " + token);

        List<MultipartBody.Part> parts=builder.build().parts();

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofit = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofit.uploadPic(parts)
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
                            //上传成功，修改心愿状态，为正在审核
                            changeWishStatus();
                            //隐藏进度弹窗
                            hidePopupWindow1();
                            Toast.makeText(WishClaimActivity.this,"上传成功",Toast.LENGTH_SHORT).show();

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                            hidePopupWindow1();
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(WishClaimActivity.this,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        hidePopupWindow1();
                        Toast.makeText(WishClaimActivity.this,"上传失败",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @OnClick(R.id.textView_instruction)
    public void onClickInstrction(){
        showPopupWindow();
    }

    private void showPopupWindow(){
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this, 0.7f);
        }
    }

   /* public void showPopupWindow(int aspirationId, int currentPos){
        this.aspirationId = aspirationId;
        this.currentPos = currentPos;
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this, 0.7f);
        }
    }*/

    private void hidePopupWindow(){
        if (popupWindow != null){
            if (popupWindow.isShowing()){
                popupWindow.dismiss();
            }
        }
    }

    private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_wish_claim_hint, null);
        //Button btnCamera = popView.findViewById(R.id.button_camera);
        //Button btnAbum = popView.findViewById(R.id.button_album);
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
                Utils.backgroundAlpha(WishClaimActivity.this,1f);
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

      /*  //拍照 设置头像
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
                //openPictureSelector();
            }
        });*/
    }



    //未使用

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

    //打开相机
    private Uri imageUri;
    private File wishImage;
    private void openCamera(){

        //创建存放图片文件的文件夹
        File folder = new File(Environment.getExternalStorageDirectory() + "/typb_pic");
        if (!folder.exists()) {
            folder.mkdir();
        }
        wishImage = new File(folder, System.currentTimeMillis() +"wishpic.jpg");

       /* headImage = new File(getExternalCacheDir(),"wishImg"+ System.currentTimeMillis()+".jpg");
        try {
            if (headImage.exists()){
                headImage.delete();
            }
            headImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        if (Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(WishClaimActivity.this,
                    "com.example.cameraalbum.fileprovider",wishImage);
        }else {
            imageUri = Uri.fromFile(wishImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,TAKE_PHOTO);
    }






}
