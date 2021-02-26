package com.typartybuilding.fragment.camera;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.myRelatedActivity.EditProfileActivity;
import com.typartybuilding.activity.pbmap.ServiceCenterActivity;
import com.typartybuilding.activity.plusRelatedActivity.Camera2Activity;
import com.typartybuilding.activity.plusRelatedActivity.PublishMicVisionActivity;
import com.typartybuilding.activity.plusRelatedActivity.UploadPictureActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.BgMusicAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.HotVideoAcAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.bgmusic.BackgroundMusicData;
import com.typartybuilding.gsondata.personaldata.TaMicro;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.MapUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.AutoFitTextureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 *  拍照
 */
public class TakeVideoFragment extends BaseFragment {

    private static String TAG = "TakeVideoFragment";

    @BindView(R.id.textureView)         //拍照 拍视频 预览
    AutoFitTextureView mPreviewView;

    @BindView(R.id.imageButton_back)
    ImageView btnBack;                //返回按钮
    @BindView(R.id.imageButton_jingyin)
    ImageButton btnMute;               //静音

    @BindView(R.id.textView_center)
    TextView textCenter;               //显示 拍视频 文字
    @BindView(R.id.textView_photo)
    TextView textPhoto;                 //显示 拍照 文字， 点击切换到拍照

    @BindView(R.id.seekBar)
    SeekBar seekBar;                  //拍摄进度条
    @BindView(R.id.textView_time)
    TextView textTime;                //时间提示，  15秒

    @BindView(R.id.textView_music_complete)
    TextView textComplete;           //配乐完成后，显示背景音乐名
    @BindView(R.id.textView_music)
    TextView textMusic;             //点击开始选择配乐
    @BindView(R.id.imageButton_next)
    ImageButton btnNext;            //拍摄完成 和 配乐完成， 点击，跳转到发布页面

    @BindView(R.id.imageButton_del)
    ImageButton btnDel;                //删除
    @BindView(R.id.imageButton_complete)
    ImageButton btnComplete;           //完成

    @BindView(R.id.imageButton_switch)
    ImageView mSwitchBtn;

    @BindView(R.id.imageButton_upload)
    ImageButton btnUpload;             //上传按钮
    @BindView(R.id.textView_upload)
    TextView textUpload;              //上传文字提示

    @BindView(R.id.imageButton_paizhao)
    ImageView btnTake;              //开始拍照和拍视频的 按钮

   /* @BindViews({R.id.textView_video, R.id.textView_center, R.id.textView_photo})
    TextView textView[];             //拍照 拍视频 的切换 按钮
*/

    private CameraManager mCameraManager;
    private Surface surface;

    private String mCameraId;

    private MediaRecorder mMediaRecorder;
    // 预览尺寸
    private Size previewSize;
    //视频尺寸
    private Size mVideoSize;
    private Integer mSensorOrientation;
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mSession;            //预览
    private CameraCaptureSession recorderSession;    //录制视频
    private Handler mainHandler = new Handler();       //关联主线程的 handler， 用户拍照后的回调
    private Handler mHandler;              //子线程的handler, 用于打开相机
    private HandlerThread handlerThread;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private Handler mainhandler = new Handler();   //主线线程hangdler 用于计时 更新进度条
    private int count = 1;                         //计算走了多少秒，用于更新进度条
    private int courrentState = 0;                // 0, 按返回键 返回上一个页面，1，按返回键，进入预览

    private File fileVideo;        //录像后的文件

    public PopupWindow popupWindow;  //底部弹窗 , 显示背景音乐
    public View popView;             //弹窗布局

    public PopupWindow popupWindow1;  //底部弹窗 , 上传时，选择 图片 或 视频
    public View popView1;             //弹窗布局

    private SmartRefreshLayout refreshLayout;
    public RecyclerView recyclerView;      //背景音乐列表
    public BgMusicAdapter adapter;
    public LinearLayout linearLayout;      //播放音乐前 ，显示的进度条
    public LinearLayout linearLayout1;     //下载时，的进度
    public TextView textPercent;           //下载百分百
    public File musicFile;    //存放背景音乐的路径

    private int pageNo = 1;    //分页-页码。不传则默认1
    private int pageSize = 10;  //分页-每页条数。不传则默认10
    private int pageCount ;     //新闻总共多少页
    private int loadingState;
    private List<BackgroundMusicData.MusicData> dataList = new ArrayList<>();   //存放背景音乐

    public boolean hasMusic;           //是否有背景音乐

    private boolean isNext;  //是否，进入下一步；即进入发布页面，需停止相机，onResume后重新打开

    private boolean isDestroy;


    @Override
    protected void initViews(Bundle savedInstanceState) {

        isDestroy = false;
        initPopupWindow();
        initPopupWindow1();
        //创建一个下载背景音乐的文件夹
        File folder = new File(Environment.getExternalStorageDirectory() + "/typb_audio");
        if (!folder.exists()) {
            folder.mkdir();
        }
        musicFile = new File(folder, "bg_music.mp3");

        //使用预览时的布局
        startPreviewLayout();
        //开启线程
        startBackgroundThread();
        initTextureView();
        setSeekBar();
        if (popupWindow1.isShowing()){
            popupWindow1.dismiss();
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_take_video;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (popupWindow1.isShowing()) {
            popupWindow1.dismiss();
        }
        //进入下一步时，执行； 锁屏时，不执行
        if (isNext) {
            //开启线程
           /* startBackgroundThread();
            initTextureView();*/

            mainhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //使用预览时的布局
                    startPreviewLayout();
                    initCamera();
                    setSeekBar();

                    isNext = false;
                }
            },200);
        }
    }

   /* @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
        //进入下一步时，执行； 锁屏时，不执行
        if (isNext) {
            closeCamera();
            stopBackgroundThread();
            mainhandler.removeCallbacks(mRunable);
        }

    }*/


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
        if (adapter != null) {
            if (adapter.mediaPlayer != null) {
                adapter.mediaPlayer.reset();
                adapter.mediaPlayer.release();
                adapter.mediaPlayer = null;
            }
        }
        //销毁时，释放
        closeCamera();
        stopBackgroundThread();
        mainhandler.removeCallbacks(mRunable);
    }

    //返回按钮
    @OnClick(R.id.imageButton_back)
    public void onClickBack() {
        if (courrentState == 1){
            //切换到 预览布局
            startPreviewLayout();
        }else {
            getActivity().finish();
        }
    }

    //是否静音拍摄
    @OnClick(R.id.imageButton_jingyin)
    public void onClickMute(){
        if (btnMute.isSelected()){
            btnMute.setSelected(false);
        }else {
            btnMute.setSelected(true);
        }
    }


    //上传按钮，上传本地图片 和 视频
    @OnClick(R.id.imageButton_upload)
    public void onClickUpload() {
        showPopupWindow1();

    }

    //切换到拍照
    @OnClick({R.id.textView_photo})
    public void onClickSwitch() {
        ((Camera2Activity)getActivity()).loadFragment(0);
    }

    //通知相册更新
    private void refreshVideo(){
        if (fileVideo.exists()) {
            Uri uri = Uri.fromFile(fileVideo);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            getActivity().sendBroadcast(intent);
        }
    }

    //拍视频 按钮
    @OnClick(R.id.imageButton_paizhao)
    public void onClickStartCamera() {
        if (!btnTake.isSelected()) {
            btnTake.setSelected(true);
            count = 1;
            mainhandler.removeCallbacks(mRunable);
            updateSeekBar();
            startRecordingVideo();
            //拍摄中的布局
            takeVideoing();
        }else {
            btnTake.setSelected(false);
            stopRecordingVideo();
            //停止计时
            mainhandler.removeCallbacks(mRunable);
            //显示拍摄完成的布局
            completeLayout1();
            //停止后，通知相册更新
            refreshVideo();
            Log.i(TAG, "onClickStartCamera: fileVideo : " + fileVideo.getAbsolutePath());
            Log.i(TAG, "onClickStartCamera: fileVideo length : " + fileVideo.length());
        }
    }

    //完成拍视频后，选择删除或 完成按钮，点击完成进入发布页面
    @OnClick({R.id.imageButton_del, R.id.imageButton_complete})
    public void onClickComplete(View view) {
        switch (view.getId()) {
            case R.id.imageButton_del:

                delVideo();
                //删除后，显示预览时 布局
                startPreviewLayout();

                break;
            case R.id.imageButton_complete:
                //显示拍摄完成后 ，进入选配乐 的布局
                completeLayout2();
                //publishVideo();

                break;
        }
    }

    //删除视频
    private void delVideo(){
        if (fileVideo != null) {
            if (fileVideo.exists() && fileVideo.isFile()) {
                fileVideo.delete();
                //重新打开相机
                Log.i(TAG, "onClickComplete: 重新打开相机");
                initCamera();
                //更改布局
                startPreviewLayout();
            }
        }
    }

    //发布视频
    private void publishVideo(){
        Intent intentAc = new Intent(getActivity(), PublishMicVisionActivity.class);
        intentAc.putExtra("path",fileVideo.getAbsolutePath());
        if (hasMusic ){
            intentAc.putExtra("flag",3);        //3 表示有背景音乐
            intentAc.putExtra("musicPath",musicFile.getAbsolutePath());
            intentAc.putExtra("musicName",adapter.musicName);
            hasMusic = false;
        }else {
            intentAc.putExtra("flag", 2);       //2 表示是视频,无背景音乐
        }
        startActivity(intentAc);
    }


    //选择配乐 和  下一步
    @OnClick({R.id.textView_music,R.id.imageButton_next})
    public void onClick(View view){
        switch (view.getId()){
            //选择配乐，从后台获取背景音乐列表
            case R.id.textView_music:
                showPopupWindow();
                break;
            case R.id.imageButton_next:
                //添加标记
                isNext = true;
                publishVideo();
                break;
        }
    }

    //重相册 选择视频后，在Comera2Activity 调该方法，给视频 路径 赋值
    public void getVideoPath(String path){
        if (path != null){
            fileVideo = new File(path);
        }
    }

    //选择发布 的 类型， 图片 或 视频
    private void showPopupWindow1(){

        if (!popupWindow1.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            popupWindow1.showAtLocation(popView1, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
        }
    }

    //选择发布 ， 图片 或 视频
    private void initPopupWindow1(){
        popView1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.popupwindow_upload, null);
        Button btnVideo = popView1.findViewById(R.id.button_video);   //视频
        Button btnAbum = popView1.findViewById(R.id.button_album);    //照片
        Button btnCancel = popView1.findViewById(R.id.button_cancel);
        popupWindow1 = new PopupWindow(popView1,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        popupWindow1.setTouchable(true);
        //点击外部消失
        popupWindow1.setOutsideTouchable(true);
        popupWindow1.setFocusable(true);
        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(getActivity(),1f);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow1.isShowing()) {
                    popupWindow1.dismiss();
                }
            }
        });
        //视频
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(getActivity())
                        .openGallery(PictureMimeType.ofVideo())
                        .selectionMode(PictureConfig.SINGLE)
                        .previewVideo(true)
                        .isCamera(false)
                        .videoMaxSecond(30)
                        .forResult(((Camera2Activity)getActivity()).REQUEST_CODE_VIDEO);
            }
        });
        //照片
        btnAbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(getActivity())
                        .openGallery(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.MULTIPLE)
                        .compress(true)
                        .previewImage(true)
                        .isCamera(false)
                        .forResult(((Camera2Activity)getActivity()).REQUEST_CODE_PICTURE);

            }
        });

    }

    //选择配乐
    private boolean isFirst;       //是否是，第一次获取背景音乐
    private void showPopupWindow(){
        initRecyclerView();
        //获取背景音乐
        isFirst = true;
        pageNo = 1;
        getBackgroundMusic();
        if (!popupWindow.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
        }
    }

    //选择配乐
    private void initPopupWindow(){
        popView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popupwindow_bg_music, null);
        ImageButton btnCancel  = popView.findViewById(R.id.imageButton_back);
        recyclerView = popView.findViewById(R.id.recyclerView);
        refreshLayout = popView.findViewById(R.id.smartRefreshLayout);
        //linearLayout = popView.findViewById(R.id.linearLayout);   //播放时的缓冲进度

        linearLayout1 = popView.findViewById(R.id.linearLayout1);  //下载中
        textPercent = popView.findViewById(R.id.textView_percent);  //下载百分比

        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (adapter.mediaPlayer.isPlaying()){
                    adapter.mediaPlayer.reset();
                }
                if (adapter.musicName != null){
                    //完成配乐布局
                    musicComplete();
                    textComplete.setText(adapter.musicName);
                }
            }
        });

    }

    private void initRecyclerView(){
        if (dataList.size() > 0){
            dataList.clear();
            if (adapter != null){
                adapter.notifyDataSetChanged();
                //重新选择配乐，需将之前记录的状态重置
                adapter.currentHolder = null;
                adapter.currentItem = -1;
            }
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if (adapter == null) {
            adapter = new BgMusicAdapter(dataList, this);
        }
        recyclerView.setAdapter(adapter);

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount){
                    getBackgroundMusic();
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
    }

    private void initData(BackgroundMusicData backgroundMusicData){
        if (!isDestroy){
            BackgroundMusicData.MusicData musicData[] = backgroundMusicData.data.rows;
            int startItem = dataList.size();
            if (musicData != null){
                for (int i = 0; i < musicData.length; i++){
                    dataList.add(musicData[i]);
                }
                int itemCount = dataList.size();
                if (isFirst){
                    adapter.notifyDataSetChanged();
                    isFirst = false;
                }else {
                    adapter.notifyItemRangeInserted(startItem, itemCount);
                }
                /*if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }*/
            }
        }
        loadingState = 0;
    }

    //从后台获取音乐数据
    private void getBackgroundMusic(){
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getBackgroundMusic(pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BackgroundMusicData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BackgroundMusicData backgroundMusicData) {
                        int code = Integer.valueOf(backgroundMusicData.code);
                        Log.i(TAG, "onNext: 背景音乐code ： " + code);
                        if (code == 0){
                            initData(backgroundMusicData);
                            pageCount = backgroundMusicData.data.pageCount;
                            pageNo++;
                           /* if (pageNo > pageCount){
                                if (adapter != null) {
                                    adapter.setTypeItemFooter();
                                }
                            }*/
                            if (refreshLayout != null){
                                refreshLayout.finishLoadMore();
                            }
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(backgroundMusicData.message);
                            if (refreshLayout != null){
                                refreshLayout.finishLoadMore();
                            }
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),backgroundMusicData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        RetrofitUtil.requestError();
                        e.printStackTrace();
                        Log.e(TAG, "onError: e : " + e );

                        if (refreshLayout != null){
                            refreshLayout.finishLoadMore();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    //处理选择图片 和 视频 后的调

    //预览时的布局
    private void startPreviewLayout(){
        courrentState = 0;
        btnTake.setSelected(false);
        seekBar.setVisibility(View.INVISIBLE);      //进度条
        textTime.setVisibility(View.INVISIBLE);     //总时间
        textComplete.setVisibility(View.INVISIBLE); //配乐完成，显示背景音乐名
        textMusic.setVisibility(View.INVISIBLE);    //选择配乐的 按钮
        btnNext.setVisibility(View.INVISIBLE);      // 下一步的按钮
        btnDel.setVisibility(View.INVISIBLE);       //拍摄完成 ， 删除按钮
        btnComplete.setVisibility(View.INVISIBLE);  // 拍摄完成，点击 进入选择配乐 布局

        btnBack.setVisibility(View.VISIBLE);
        btnTake.setVisibility(View.VISIBLE);         //拍照按钮
        textCenter.setVisibility(View.VISIBLE);     //显示 拍视频 文字
        textPhoto.setVisibility(View.VISIBLE);      //显示 拍照 文字， 点击切换为拍照
        btnMute.setVisibility(View.VISIBLE);        //静音 按钮
        btnUpload.setVisibility(View.VISIBLE);      //上传 按钮
        textUpload.setVisibility(View.VISIBLE);     //上传 文字提示
        mSwitchBtn.setVisibility(View.VISIBLE);
    }

    //拍摄中 的布局
    private void takeVideoing(){
        seekBar.setVisibility(View.VISIBLE);      //进度条
        textTime.setVisibility(View.VISIBLE);     //总时间
        btnTake.setVisibility(View.VISIBLE);      //拍摄 按钮

        btnBack.setVisibility(View.INVISIBLE);
        textComplete.setVisibility(View.INVISIBLE); //配乐完成，显示背景音乐名
        textMusic.setVisibility(View.INVISIBLE);    //选择配乐的 按钮
        btnNext.setVisibility(View.INVISIBLE);      // 下一步的按钮
        btnDel.setVisibility(View.INVISIBLE);       //拍摄完成 ， 删除按钮
        btnComplete.setVisibility(View.INVISIBLE);  // 拍摄完成，点击 进入选择配乐 布局

        textCenter.setVisibility(View.INVISIBLE);     //显示 拍视频 文字
        textPhoto.setVisibility(View.INVISIBLE);      //显示 拍照 文字， 点击切换为拍照
        btnMute.setVisibility(View.INVISIBLE);        //静音 按钮
        btnUpload.setVisibility(View.INVISIBLE);      //上传 按钮
        textUpload.setVisibility(View.INVISIBLE);     //上传 文字提示
        mSwitchBtn.setVisibility(View.INVISIBLE);
    }

    //拍摄完成1
    private void completeLayout1(){
        courrentState = 1;
        seekBar.setVisibility(View.VISIBLE);      //进度条
        textTime.setVisibility(View.VISIBLE);     //总时间
        btnTake.setVisibility(View.VISIBLE);      //拍摄 按钮
        btnBack.setVisibility(View.VISIBLE);     //返回
        btnDel.setVisibility(View.VISIBLE);       //拍摄完成 ， 删除按钮
        btnComplete.setVisibility(View.VISIBLE);  // 拍摄完成，点击 进入选择配乐 布局

        textComplete.setVisibility(View.INVISIBLE); //配乐完成，显示背景音乐名
        textMusic.setVisibility(View.INVISIBLE);    //选择配乐的 按钮
        btnNext.setVisibility(View.INVISIBLE);      // 下一步的按钮

        textCenter.setVisibility(View.INVISIBLE);     //显示 拍视频 文字
        textPhoto.setVisibility(View.INVISIBLE);      //显示 拍照 文字， 点击切换为拍照
        btnMute.setVisibility(View.INVISIBLE);        //静音 按钮
        btnUpload.setVisibility(View.INVISIBLE);      //上传 按钮
        textUpload.setVisibility(View.INVISIBLE);     //上传 文字提示
    }

    //拍摄完成2,开始配乐
    public void completeLayout2(){
        courrentState = 1;
        btnBack.setVisibility(View.VISIBLE);     //返回
        textMusic.setVisibility(View.VISIBLE);    //选择配乐的 按钮
        btnNext.setVisibility(View.VISIBLE);      // 下一步的按钮

        seekBar.setVisibility(View.INVISIBLE);      //进度条
        textTime.setVisibility(View.INVISIBLE);     //总时间
        btnTake.setVisibility(View.INVISIBLE);      //拍摄 按钮
        btnDel.setVisibility(View.INVISIBLE);       //拍摄完成 ， 删除按钮
        btnComplete.setVisibility(View.INVISIBLE);  // 拍摄完成，点击 进入选择配乐 布局
        textComplete.setVisibility(View.INVISIBLE); //配乐完成，显示背景音乐名

        textCenter.setVisibility(View.INVISIBLE);     //显示 拍视频 文字
        textPhoto.setVisibility(View.INVISIBLE);      //显示 拍照 文字， 点击切换为拍照
        btnMute.setVisibility(View.INVISIBLE);        //静音 按钮
        btnUpload.setVisibility(View.INVISIBLE);      //上传 按钮
        textUpload.setVisibility(View.INVISIBLE);     //上传 文字提示
        mSwitchBtn.setVisibility(
                View.INVISIBLE
        );
    }

    //配乐完成
    private void musicComplete(){
        courrentState = 1;
        btnBack.setVisibility(View.VISIBLE);     //返回
        textMusic.setVisibility(View.VISIBLE);    //选择配乐的 按钮
        btnNext.setVisibility(View.VISIBLE);      // 下一步的按钮
        textComplete.setVisibility(View.VISIBLE); //配乐完成，显示背景音乐名

        seekBar.setVisibility(View.INVISIBLE);      //进度条
        textTime.setVisibility(View.INVISIBLE);     //总时间
        btnTake.setVisibility(View.INVISIBLE);      //拍摄 按钮
        btnDel.setVisibility(View.INVISIBLE);       //拍摄完成 ， 删除按钮
        btnComplete.setVisibility(View.INVISIBLE);  // 拍摄完成，点击 进入选择配乐 布局

        textCenter.setVisibility(View.INVISIBLE);     //显示 拍视频 文字
        textPhoto.setVisibility(View.INVISIBLE);      //显示 拍照 文字， 点击切换为拍照
        btnMute.setVisibility(View.INVISIBLE);        //静音 按钮
        btnUpload.setVisibility(View.INVISIBLE);      //上传 按钮
        textUpload.setVisibility(View.INVISIBLE);     //上传 文字提示
        mSwitchBtn.setVisibility(View.INVISIBLE);
    }

    private void setSeekBar(){
        seekBar.setEnabled(false);   //不可点击
        seekBar.setPadding(0, 0, 0, 0);
        seekBar.setProgress(0);
        seekBar.setMax(30);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: progress : " + progress);
                if (progress == 30){
                    stopRecordingVideo();
                    //停止计时
                    //Log.i(TAG, "onProgressChanged:  mainhandler.removeCallbacks");
                    //mainhandler.removeCallbacks(mRunable);
                    completeLayout1();
                    btnTake.setSelected(false);
                    //通知相册刷新
                    refreshVideo();

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void updateSeekBar(){

        mainhandler.postDelayed(mRunable,1000);
    }

    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(count);
            if (count <= 30) {
                textTime.setText(count + "s");
            }
            Log.i(TAG, "run: count : " + count);
            count++;
            updateSeekBar();
        }
    };



    //初始化TextureView
    private void initTextureView() {
        mPreviewView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

                if (checkCameraHardware(getActivity())) {
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        initCamera();
                    }
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (mCameraDevice != null) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    private void closePreviewSession() {
        if (mSession != null) {
            mSession.close();
            mSession = null;
        }
    }

    private void closeRecorderSession() {
        if (recorderSession != null) {
            try {
                recorderSession.stopRepeating();
                recorderSession.abortCaptures();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            recorderSession.close();
            recorderSession = null;
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            mHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    //初始化 相机
    public void initCamera() {
        initCamera(Camera2Activity.mCameraMode);
    }

    public void initCamera(int forward) {
        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        mCameraId = "" + forward;

        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (map == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));

            previewSize = CameraUtils.getMatchingSize(mCameraManager, mCameraId,
                    mPreviewView.getWidth(), mPreviewView.getHeight());

            Log.i(TAG, "initCamera: mVideoSize width : " + mVideoSize.getWidth());
            Log.i(TAG, "initCamera: mVideoSize heignt : " + mVideoSize.getHeight());
            Log.i(TAG, "initCamera: previewSize width : " + previewSize.getWidth());
            Log.i(TAG, "initCamera: previewSize heignt : " + previewSize.getHeight());

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mCameraManager.openCamera(mCameraId, deviceStateCallback, mHandler);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }

    }

    //相机打开后的回调
    private CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened( CameraDevice camera) {
            mCameraDevice = camera;

            startPreview();
            mCameraOpenCloseLock.release();

        }

        @Override
        public void onDisconnected( CameraDevice camera) {
            mCameraOpenCloseLock.release();
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError( CameraDevice camera, int error) {
            mCameraOpenCloseLock.release();
            //Toast.makeText(getActivity(), "打开摄像头失败", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "onError: error : " + error);
        }
    };

    //开始预览
    public void startPreview()  {

        try {
            closePreviewSession();
            SurfaceTexture texture = mPreviewView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            //texture.setDefaultBufferSize(1280, 720);
            surface = new Surface(texture);
            //mPreviewView.setAspectRatio(previewSize.getHeight(),previewSize.getWidth());

            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            mPreviewBuilder.addTarget(surface);
            mCameraDevice.createCaptureSession(Arrays.asList(surface), mSessionPreviewStateCallback, mHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    //创建会话的 回调， 设置相机
    private CameraCaptureSession.StateCallback mSessionPreviewStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mSession = session;
            //自动对焦
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //打开闪光灯
            mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            //无限次的重复获取图像
            try {
                mSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Toast.makeText(getActivity(), "配置失败", Toast.LENGTH_SHORT).show();
        }
    };

   /* private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            mSession = session;
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            mSession = session;
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    };*/


    /**
     * 设置媒体录制器的配置参数
     * <p>
     * 音频，视频格式，文件路径，频率，编码格式等等
     *
     * @throws IOException
     */

    private void setUpMediaRecorder() throws IOException{
        if (mMediaRecorder != null){
            mMediaRecorder = null;
        }

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        //创建存放视频文件的文件夹
        File folder = new File(Environment.getExternalStorageDirectory() + "/typb_video");
        if (!folder.exists()) {
            folder.mkdir();
        }
        //File file = new File(getExternalFilesDir(null), "pic.jpg");
        fileVideo = new File(folder, System.currentTimeMillis() +"video.mp4");

        //静音，不设置音频源
        if (!btnMute.isSelected()) {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        }
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mMediaRecorder.setOutputFile(fileVideo.getAbsolutePath());
        //mNextVideoAbsolutePath = FileUtils.createVideoDiskFile(appContext, FileUtils.createVideoFileName()).getAbsolutePath();

        mMediaRecorder.setVideoEncodingBitRate((int) ((1.5) * 1024 * 1024));    //900*1024    10000000
        //每秒30帧
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //静音，不设置音频编码
        if (!btnMute.isSelected()) {
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        }

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
            default:
                break;
        }
        mMediaRecorder.prepare();
    }

    private void startRecordingVideo() {

        try {
            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = mPreviewView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //mSession = cameraCaptureSession;
                    recorderSession = cameraCaptureSession;
                    //自动对焦
                    mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    //打开闪光灯
                    mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    //无限次的重复获取图像
                    try {
                        //mSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
                        recorderSession.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);

                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    //updatePreview();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // UI
                            // Start recording
                            mMediaRecorder.start();
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }, mHandler);
        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecordingVideo() {
        Log.i(TAG, "stopRecordingVideo: ");
        // Stop recording
        closePreviewSession();
        closeRecorderSession();

        if (mMediaRecorder != null) {
            try {
                setStopMediaRecorder();
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }catch (IllegalStateException e){
                Log.i(TAG, "stopRecordingVideo: e : " + e);
            }catch (RuntimeException e){
                Log.i(TAG, "stopRecordingVideo: e : " + e);
            }catch (Exception e){
                Log.i(TAG, "stopRecordingVideo: e : " + e);
            }
        }

        //startPreview();
        //测试
       /* closeCamera();
        stopBackgroundThread();
        mainhandler.removeCallbacks(mRunable);
        startBackgroundThread();
        initTextureView();*/
    }

    //用于部分手机，调MediaRecorder.stop() 时报错
    private void setStopMediaRecorder(){
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setOnInfoListener(null);
        mMediaRecorder.setPreviewDisplay(null);
    }


    private  Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {

            Log.i(TAG, "chooseVideoSize: size width : " + size.getWidth());
            Log.i(TAG, "chooseVideoSize: size height : " + size.getHeight());
            if (size.getWidth() == size.getHeight() * mPreviewView.getHeight() / mPreviewView.getWidth()
                    && size.getWidth() <= 1280) {
                return size;
            }else {
                return new Size(1280,720);
            }

        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    public int getOrientation(int rotation) {
        switch (rotation) {
            case Surface.ROTATION_0:
                return 90;
            case Surface.ROTATION_90:
                return 0;
            case Surface.ROTATION_180:
                return 270;
            case Surface.ROTATION_270:
                return 180;
            default:
                return 0;
        }
    }

    //获取图片应该旋转的角度，使图片竖直
    private int getJpegOrientation(CameraCharacteristics c, int deviceOrientation) {
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN)
            return 0;
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);
        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;
        // LENS_FACING相对于设备屏幕的方向,LENS_FACING_FRONT相机设备面向与设备屏幕相同的方向
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) deviceOrientation = -deviceOrientation;
        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;
        return jpegOrientation;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initCamera();
                }else {
                    Toast.makeText(getActivity(),"拍照需要开启相机权限",Toast.LENGTH_SHORT).show();
                }
        }
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    @OnClick(R.id.imageButton_switch)
    void OnclickSwitch(){
        if (Camera2Activity.mCameraMode == CameraCharacteristics.LENS_FACING_FRONT){
            Camera2Activity.mCameraMode = CameraCharacteristics.LENS_FACING_BACK;
        }else {
            Camera2Activity.mCameraMode = CameraCharacteristics.LENS_FACING_FRONT;
        }
        closeCamera();
        initCamera();
    }

}
