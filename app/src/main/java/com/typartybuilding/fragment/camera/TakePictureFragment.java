package com.typartybuilding.fragment.camera;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
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
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.typartybuilding.R;
import com.typartybuilding.activity.plusRelatedActivity.Camera2Activity;
import com.typartybuilding.activity.plusRelatedActivity.PublishMicVisionActivity;
import com.typartybuilding.activity.plusRelatedActivity.UploadPictureActivity;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.utils.appmanager.SwitchBackgroundCallbacks;
import com.typartybuilding.view.AutoFitTextureView;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *  拍照
 */
public class TakePictureFragment extends BaseFragment {

    private String TAG = "TakePictureFragment";

    @BindView(R.id.textureView)         //拍照 拍视频 预览
    AutoFitTextureView mPreviewView;

   /* @BindView(R.id.imageButton_jingyin)
    ImageButton btnMute;               //静音*/
    @BindView(R.id.imageButton_del)
    ImageButton btnDel;                //删除
    @BindView(R.id.imageButton_complete)
    ImageButton btnComplete;           //完成
    @BindView(R.id.imageButton_upload)
    ImageButton btnUpload;             //上传按钮
    @BindView(R.id.textView_upload)
    TextView textUpload;              //上传文字提示

    @BindView(R.id.imageButton_paizhao)
    ImageView btnTake;              //开始拍照和拍视频的 按钮
    @BindView(R.id.imageButton_switch)
    ImageView mSwitchIv;
   /* @BindViews({R.id.textView_video, R.id.textView_center, R.id.textView_photo})
    TextView textView[];             //拍照 拍视频 的切换 按钮*/

    //private int flag = 0;             // 1 表示拍照片， 2 表示拍视频
    private CameraManager mCameraManager;
    private Surface surface;


    private String mCameraId;
    private ImageReader mImageReader;
    // 预览尺寸
    private Size previewSize;

    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mSession;
    private Handler mainHandler = new Handler();   //关联主线程的 handler， 用户拍照后的回调
    private Handler mHandler;              //子线程的handler, 用于打开相机
    private HandlerThread handlerThread;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    public PopupWindow popupWindow1;  //底部弹窗 , 上传时，选择 图片 或 视频
    public View popView1;             //弹窗布局

    private File filePic;        //拍照后的文件

    private boolean isNext;  //是否，进入下一步；即进入发布页面，需停止相机，onResume后重新打开


    @Override
    protected void initViews(Bundle savedInstanceState) {
        initPopupWindow1();
        //开启线程
        startBackgroundThread();

        initTextureView();
        //使用预览时的布局
        startPreviewLayout();

        if (popupWindow1.isShowing()){
            popupWindow1.dismiss();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_take_picture;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (popupWindow1.isShowing()) {
            popupWindow1.dismiss();
        }

        Log.i(TAG, "onResume: isNext : " + isNext);
        //进入下一步时，即拍照完成，进入发布页面，执行； 锁屏时，不执行
        if (isNext) {
            //开启线程
            //startBackgroundThread();
            //initTextureView();
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //使用预览时的布局
                    startPreviewLayout();
                    initCamera();
                    isNext = false;
                }
            },200);
        }
    }

  /*  @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: isNext : "  + isNext);
        //进入下一步时，即拍照完成，进入发布页面，执行； 锁屏时，不执行
        if (isNext) {
            //closePreviewSession();
            closeCamera();
            stopBackgroundThread();
        }

    }*/


    @Override
    public void onDestroy() {
        super.onDestroy();
        closeCamera();
        stopBackgroundThread();
    }

    private void closePreviewSession() {
        if (mSession != null) {
            mSession.close();
            mSession = null;
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }

        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        Log.i(TAG, "startBackgroundThread: 线程开启完毕");
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        if (handlerThread != null) {
            handlerThread.quitSafely();
            try {
                handlerThread.join();
                handlerThread = null;
                mHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //返回按钮
    @OnClick(R.id.imageButton_back)
    public void onClickBack() {
        getActivity().finish();
    }

    @OnClick(R.id.imageButton_upload)
    public void onClickUpload() {
        showPopupWindow1();

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

                popupWindow1.dismiss();
                //切换到录制视频
                ((Camera2Activity)getActivity()).loadFragment(1);
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

    //切换到拍视频
    @OnClick({R.id.textView_video})
    public void onClickSwitch(View view) {

        ((Camera2Activity)getActivity()).loadFragment(1);

    }

    //拍照 按钮
    @OnClick(R.id.imageButton_paizhao)
    public void onClickStartCamera() {

        takePicture();

    }

    //完成拍照后，选择删除或 完成按钮，点击完成进入发布页面
    @OnClick({R.id.imageButton_del, R.id.imageButton_complete})
    public void onClickComplete(View view) {
        switch (view.getId()) {
            case R.id.imageButton_del:
                    delPicture();
                break;
            case R.id.imageButton_complete:
                    //完成，进入下一步，发布页面，添加标记
                    isNext = true;
                    publishPicture();
                break;
        }
    }

    //删除照片
    private void delPicture(){
        if (filePic != null) {
            if (filePic.exists() && filePic.isFile()) {
                filePic.delete();
                //重新打开相机
                Log.i(TAG, "onClickComplete: 重新打开相机");
                initCamera();
                //更改布局
                startPreviewLayout();

            }
        }
    }

    //发布照片
    private void publishPicture(){
        Intent intentAc = new Intent(getActivity(), PublishMicVisionActivity.class);
        intentAc.putExtra("path",filePic.getAbsolutePath());
        intentAc.putExtra("flag",1);  //1 表示 照片
        startActivity(intentAc);
    }

    //预览时的布局
    private void startPreviewLayout(){
        btnDel.setVisibility(View.INVISIBLE);
        mSwitchIv.setVisibility(View.VISIBLE);
        btnComplete.setVisibility(View.INVISIBLE);
        btnUpload.setVisibility(View.VISIBLE);
        textUpload.setVisibility(View.VISIBLE);
        //拍照按钮可点击
        btnTake.setEnabled(true);
    }

    //完成拍照的布局
    private void completeLayout(){
        btnDel.setVisibility(View.VISIBLE);
        mSwitchIv.setVisibility(View.INVISIBLE);
        btnComplete.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.INVISIBLE);
        textUpload.setVisibility(View.INVISIBLE);
        //拍照完成
        btnTake.setEnabled(false);
    }


    private void initTextureView() {
        mPreviewView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.i(TAG, "onSurfaceTextureAvailable: ");
                if (checkCameraHardware(getActivity())) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != 
                            PackageManager.PERMISSION_GRANTED || 
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != 
                                    PackageManager.PERMISSION_GRANTED) {
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

    //通知相册更新
    private void refreshPicture(){
        if (filePic.exists()) {
            Uri uri = Uri.fromFile(filePic);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            getActivity().sendBroadcast(intent);
        }
    }

    //用于相片存储
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            //拍照完成，删除按钮和完成按钮可见，上传按钮不可见
            completeLayout();
            //关闭相机
            mCameraDevice.close();
            //退出线程
            stopBackgroundThread();

            Image image = reader.acquireLatestImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);//将image对象转化为byte，再转化为bitmap

            //创建存放图片文件的文件夹
            File folder = new File(Environment.getExternalStorageDirectory() + "/typb_pic");
            if (!folder.exists()) {
                folder.mkdir();
            }

            //File file = new File(getExternalFilesDir(null), "pic.jpg");
            filePic = new File(folder, System.currentTimeMillis() +"pic.jpg");

            try (
                    FileOutputStream output = new FileOutputStream(filePic))
            {
                output.write(bytes);
                Toast.makeText(getActivity(), "保存: "
                        + filePic, Toast.LENGTH_SHORT).show();

                //通知相册更新
                refreshPicture();

                Log.i(TAG, "onImageAvailable: file : " + filePic.getPath());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                image.close();

            }
        }
    };


    public void initCamera() {
        Log.i(TAG, "initCamera: ");
        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        mCameraId = "" + Camera2Activity.mCameraMode;

        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        previewSize = CameraUtils.getMatchingSize(mCameraManager,mCameraId,
                mPreviewView.getWidth(),mPreviewView.getHeight());

        Log.i(TAG, "initCamera: previewSize width : " + previewSize.getWidth());
        Log.i(TAG, "initCamera: previewSize heignt : " + previewSize.getHeight());

        /*SurfaceTexture texture = mPreviewView.getSurfaceTexture();
        texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        //texture.setDefaultBufferSize(1280, 720);
        surface = new Surface(texture);*/
        //mPreviewView.setAspectRatio(previewSize.getHeight(),previewSize.getWidth());
        //初始化用于相片存储的ImageReader
        mImageReader = ImageReader.newInstance(mPreviewView.getHeight(),mPreviewView.getWidth() , ImageFormat.JPEG,2);
        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mainHandler);

        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mCameraManager.openCamera(mCameraId, deviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }


    }

    public void startPreview()  {
        try {
            Log.i(TAG, "startPreview: 开始预览");
            closePreviewSession();
            SurfaceTexture texture = mPreviewView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            //texture.setDefaultBufferSize(1280, 720);
            surface = new Surface(texture);
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(surface);
            mCameraDevice.createCaptureSession(Arrays.asList(surface/*mSurfaceHolder.getSurface()*/, mImageReader.getSurface()), mSessionPreviewStateCallback, mHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //相机打开后的回调
    private CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened( CameraDevice camera) {
            Log.i(TAG, "CameraDevice onOpened: ");
            mCameraDevice = camera;
            //try {
                startPreview();
                mCameraOpenCloseLock.release();
          /*  } catch (CameraAccessException e) {
                e.printStackTrace();
            }*/
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
            Log.i(TAG, "onError: 摄像头打开错误");
            if (!SwitchBackgroundCallbacks.isAppGoBackGround()) {
                Toast.makeText(getActivity(), "打开摄像头失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

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

    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
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
    };



    private void takePicture(){
        //用来设置拍照请求的request
        try {
            CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            //使图片做顺时针旋转
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation(cameraCharacteristics, rotation));
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            mSession.capture(mCaptureRequest, null, mHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
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
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == Camera2Activity.mCameraMode;
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
     void onClickSwict(){
        if (Camera2Activity.mCameraMode == CameraCharacteristics.LENS_FACING_FRONT){
            Camera2Activity.mCameraMode = CameraCharacteristics.LENS_FACING_BACK;
        }else {
            Camera2Activity.mCameraMode = CameraCharacteristics.LENS_FACING_FRONT;
        }
        closeCamera();
        initCamera();
    }












}
