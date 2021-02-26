package com.typartybuilding.activity.plusRelatedActivity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.ffmpeg.FFmpeg;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.utils.CameraUtils;
import com.typartybuilding.view.MySurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import android.hardware.Camera;

public class CameraActivity extends BaseActivity {

    @BindView(R.id.imageView_back1)
    ImageView imgBack1;               //拍视频 和拍照 时的反回按钮
   /* @BindView(R.id.surfaceView)
    SurfaceView surfaceView;          //显示 视频和照片*/

    @BindView(R.id.surfaceView)
    FrameLayout camera_preview;

    @BindView(R.id.imageView_circle)
    ImageView imgCircle;              //开始拍照和拍视频的 按钮
    @BindView(R.id.imageView_upload)
    ImageView imgUpload;              //上传按钮
    @BindViews({R.id.textView_video, R.id.textView_center, R.id.textView_photo})
    TextView textView[];             //拍照 拍视频 的切换 按钮

    private int flag = 0;             //0 表示拍照片， 1 表示拍视频

    private boolean previewrunning=true;
    private SurfaceHolder surfaceHolder;

    //private FrameLayout camera_preview;
    private MySurfaceView cameraSurfaceView;
    private Camera camera=null;

    private Point mScreenResolution;//屏幕分辨率
    private Point previewSizeOnScreen;//相机预览尺寸
    private Point pictureSizeOnScreen;//图片尺寸
    private Bitmap bitmapCamera = null;
    private ImageView img_camera;


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        hideStatusBar();
        changeNavigationBar();
        initCamera();
        init();

    }

    @OnClick({R.id.textView_video, R.id.textView_photo})
    public void onClickSwitch(View view) {
        switch (view.getId()) {
            case R.id.textView_video:
                flag = 1;
                textView[1].setText("拍视频");
                textView[0].setVisibility(View.INVISIBLE);
                textView[2].setVisibility(View.VISIBLE);
                break;
            case R.id.textView_photo:
                flag = 1;
                textView[1].setText("拍照");
                textView[0].setVisibility(View.VISIBLE);
                textView[2].setVisibility(View.INVISIBLE);
                break;
        }
    }

    @OnClick(R.id.imageView_upload)
    public void onClickUpload(){
        Intent intentAc = new Intent(this,UploadPictureActivity.class);
        startActivity(intentAc);
    }



    private void initCamera() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.CAMERA};
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check == PackageManager.PERMISSION_GRANTED) {
                //调用相机
                camera = CameraUtils.openBackFacingCameraGingerbread();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        } else {
            camera = CameraUtils.openBackFacingCameraGingerbread();
        }
        if (camera == null) {
            //ToastUtil.MyToast(mContext, "摄像头被占用,摄像头权限没打开！");
            return;
        }
        setCameraParameters(camera, camera.getParameters());

      /*  surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFixedSize(480,800);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(Camera.getNumberOfCameras()==2){//获取相机...
                    camera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                }else{
                    camera=Camera.open(0);
                }
                camera.setDisplayOrientation(90);//这个就是设置屏幕需要旋转90度，一般没这句话屏幕内的东西都是纵向的...
                WindowManager manager=(WindowManager) CameraActivity.this.getSystemService(Context.WINDOW_SERVICE);//获取窗口服务..
                Display display=manager.getDefaultDisplay();//获取display对象..
                Camera.Parameters param=CameraActivity.this.camera.getParameters();//获取参数
                param.setPreviewSize(display.getWidth(), display.getHeight());//设置预览时图片的大小..
                param.setPictureSize(display.getWidth(), display.getHeight());//设置拍照后图片的大小..
                param.setPreviewFrameRate(5);//设置预览的时候以每秒五帧进行显示...
                param.setPictureFormat(PixelFormat.JPEG);//设置图片的格式为JPEG...
                param.set("jpeg-quality", 80);//设置图片的质量...

                try {
                    CameraActivity.this.camera.setPreviewDisplay(CameraActivity.this.surfaceHolder);//设置我们预览时的SurfaceHolder...
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                CameraActivity.this.camera.startPreview();//开始预览...
                CameraActivity.this.previewrunning=true;

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                if(CameraActivity.this.camera!=null){
                    if(CameraActivity.this.previewrunning){
                        CameraActivity.this.camera.stopPreview();
                        CameraActivity.this.previewrunning=false;
                    }
                    CameraActivity.this.camera.release();
                }
            }
        });*/

    }

    private void init() {
        cameraSurfaceView = new MySurfaceView(this, camera);
        //设置界面展示大小
        Point point = CameraUtils.calculateViewSize(previewSizeOnScreen, mScreenResolution);
        System.out.println(point.x + "," + point.y);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(point.x, point.y);
        layoutParams.gravity = Gravity.CENTER;
        cameraSurfaceView.setLayoutParams(layoutParams);
        camera_preview.addView(cameraSurfaceView);
    }

    private void setCameraParameters(Camera camera, Camera.Parameters parameters) {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);//得到屏幕的尺寸，单位是像素
        mScreenResolution = theScreenResolution;
        previewSizeOnScreen = CameraUtils.findBestPreviewSizeValue(parameters, theScreenResolution);//通过相机尺寸、屏幕尺寸来得到最好的展示尺寸，此尺寸为相机的
        parameters.setPreviewSize(previewSizeOnScreen.x, previewSizeOnScreen.y);
        pictureSizeOnScreen = CameraUtils.findBestPictureSizeValue(parameters, theScreenResolution);//通过相机尺寸、屏幕尺寸来得到最好的展示尺寸，此尺寸为相机的
        parameters.setPictureSize(pictureSizeOnScreen.x, pictureSizeOnScreen.y);
        boolean isScreenPortrait = mScreenResolution.x < mScreenResolution.y;
        boolean isPreviewSizePortrait = previewSizeOnScreen.x < previewSizeOnScreen.y;
        if (isScreenPortrait != isPreviewSizePortrait) {//相机与屏幕一个方向，则使用相机尺寸
            previewSizeOnScreen = new Point(previewSizeOnScreen.y, previewSizeOnScreen.x);//否则翻个
        }
        // 设置照片的格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        CameraUtils.setFocus(parameters, true, false, true);//设置相机对焦模式
        CameraUtils.setBarcodeSceneMode(parameters, Camera.Parameters.SCENE_MODE_BARCODE);//设置相机场景模式
        CameraUtils.setBestPreviewFPS(parameters);//设置相机帧数
        camera.setParameters(parameters);
        // 系统相机默认是横屏的，我们要旋转90°
        camera.setDisplayOrientation(90);
    }

    //创建jpeg图片回调数据对象
    Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            try {
                bitmapCamera = BitmapFactory.decodeByteArray(data, 0, data.length);
                //bitmapCamera = BitmapUtil.rotateImage(bitmapCamera);
                //zipBitmap();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            camera.stopPreview();//关闭预览 处理数据
            CameraUtils.setCameraSound(false, CameraActivity.this);
        }
    };

    private void zipBitmap() {
        try {
            //bitmapCamera = BitmapUtil.zipBitmap(bitmapCamera, 100);
            //img_camera.setImageBitmap(bitmapCamera);
            //File file = FileUtil.saveImagePath(mContext, "et", "image");
            File file = null;
            if (file != null) {
                FileOutputStream fileOutStream = null;
                fileOutStream = new FileOutputStream(file);
                //把位图输出到指定的文件中
                bitmapCamera.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);
                fileOutStream.close();
                System.out.println("**********图片保存成功***********");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //回收数据
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (bitmapCamera != null) {
            bitmapCamera.recycle();//回收bitmap空间
            bitmapCamera = null;
        }
    }

    private void compoundAudioVideo(){
        if (FFmpeg.getInstance(this).isSupported()){

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else {
                    Toast.makeText(this,"拍照需要开启相机权限",Toast.LENGTH_SHORT).show();
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




}
