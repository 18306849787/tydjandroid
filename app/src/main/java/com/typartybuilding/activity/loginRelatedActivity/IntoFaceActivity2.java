package com.typartybuilding.activity.loginRelatedActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.ILivenessStrategy;
import com.baidu.idl.face.platform.ILivenessStrategyCallback;
import com.baidu.idl.face.platform.utils.APIUtils;
import com.baidu.idl.face.platform.utils.Base64Utils;
import com.baidu.idl.face.platform.utils.CameraPreviewUtils;
import com.google.gson.JsonSyntaxException;
import com.typartybuilding.R;
import com.typartybuilding.activity.myRelatedActivity.EditProfileActivity;
import com.typartybuilding.baiduface.CameraUtils;
import com.typartybuilding.baiduface.FaceDetectRoundView;
import com.typartybuilding.baiduface.FaceLivenessActivity;
import com.typartybuilding.baiduface.FaceSDKResSettings;
import com.typartybuilding.baiduface.FaceUtils;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.personaldata.TaMicro;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.OkHttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Retrofit;

public class IntoFaceActivity2 extends WhiteTitleBaseActivity implements
        SurfaceHolder.Callback,
        Camera.PreviewCallback,
        Camera.ErrorCallback,
        //VolumeUtils.VolumeCallback,
        ILivenessStrategyCallback {


    public static final String TAG = "IntoFaceActivity2";

    // View
    protected View mRootView;
    protected FrameLayout mFrameLayout;
    protected SurfaceView mSurfaceView;
    protected SurfaceHolder mSurfaceHolder;

    protected ImageView mSuccessView;
    protected TextView mTipsTopView;

    protected FaceDetectRoundView mFaceDetectRoundView;

    // 人脸信息
    protected FaceConfig mFaceConfig;
    protected ILivenessStrategy mILivenessStrategy;
    // 显示Size
    private Rect mPreviewRect = new Rect();
    protected int mDisplayWidth = 0;
    protected int mDisplayHeight = 0;
    protected int mSurfaceWidth = 0;
    protected int mSurfaceHeight = 0;
    protected Drawable mTipsIcon;
    // 状态标识
    protected volatile boolean mIsEnableSound = true;
    protected HashMap<String, String> mBase64ImageMap = new HashMap<String, String>();
    protected boolean mIsCreateSurface = false;
    protected boolean mIsCompletion = false;
    // 相机
    protected Camera mCamera;
    protected Camera.Parameters mCameraParam;
    protected int mCameraId;
    protected int mPreviewWidth;
    protected int mPreviewHight;
    protected int mPreviewDegree;
    // 监听系统音量广播
    protected BroadcastReceiver mVolumeReceiver;

    private int flag;    //flag 为1 ，调修改人脸的接口


    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindView(R.id.framelayout_face_swiping)
    FrameLayout layoutFace;            //刷脸成功失败的布局
    @BindView(R.id.textView_face_swiping)
    TextView textFace;                //刷脸成功失败的提示

    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;        //上传人脸时的 进度条

    //采集的图片
    private String base64img;
    //个推返回的id
    private String phone = MyApplication.pref.getString(MyApplication.prefKey5_phone, "");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_into_face2);
        ButterKnife.bind(this);
        textTitle.setText("4/5");
        //获取传递的flag，用于判断，是否是在 修改人脸
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag",-1);
        //1 重新录入人脸
        if (flag == 1){
            textTitle.setText("录入人脸");
        }

        //初始化 SDK
        FaceUtils.initLib();

        mRootView = this.findViewById(R.id.liveness_root_layout);
        mFrameLayout = (FrameLayout) mRootView.findViewById(R.id.liveness_surface_layout);
        mTipsTopView = (TextView) mRootView.findViewById(R.id.liveness_top_tips);
        mSuccessView = (ImageView) mRootView.findViewById(R.id.liveness_success_image);
        mFaceDetectRoundView = (FaceDetectRoundView) mRootView.findViewById(R.id.liveness_face_round);

        init();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mTipsTopView != null) {
            mTipsTopView.setText(R.string.detect_face_in);
        }
        startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPreview();
    }

    @Override
    public void onStop() {
        if (mILivenessStrategy != null) {
            mILivenessStrategy.reset();
        }

        super.onStop();
        stopPreview();
    }

    private void init(){
        DisplayMetrics dm = new DisplayMetrics();
        Display display = this.getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        mDisplayWidth = dm.widthPixels;
        mDisplayHeight = dm.heightPixels;

        FaceSDKResSettings.initializeResId();
        mFaceConfig = FaceSDKManager.getInstance().getFaceConfig();

        mSurfaceView = new SurfaceView(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setSizeFromLayout();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        int w = mDisplayWidth;
        int h = mDisplayHeight;

        FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(
                (int) (w * FaceDetectRoundView.SURFACE_RATIO), (int) (h * FaceDetectRoundView.SURFACE_RATIO),
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        mSurfaceView.setLayoutParams(cameraFL);
        mFrameLayout.addView(mSurfaceView);

        if (mBase64ImageMap != null) {
            mBase64ImageMap.clear();
        }
    }

    /**
     * 重新采集
     * @return
     */
    private void restartFaceSwiping(){
        Log.i(TAG, "restartFaceSwiping: 重新人脸识别");

        stopPreview();

        if (mTipsTopView != null) {
            mTipsTopView.setText(R.string.detect_face_in);
        }
        if (mBase64ImageMap != null) {
            mBase64ImageMap.clear();
        }
        mSuccessView.setVisibility(View.INVISIBLE);

        mIsCompletion = false;
        //mIDetectStrategy.detectStrategy(new byte[]{});
        mFrameLayout.removeAllViews();
        init();
        startPreview();
    }


    private Camera open() {
        Camera camera;
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            return null;
        }

        int index = 0;
        while (index < numCameras) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                break;
            }
            index++;
        }

        if (index < numCameras) {
            camera = Camera.open(index);
            mCameraId = index;
        } else {
            camera = Camera.open(0);
            mCameraId = 0;
        }
        return camera;
    }

    protected void startPreview() {
        if (mSurfaceView != null && mSurfaceView.getHolder() != null) {
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);
        }

        if (mCamera == null) {
            try {
                mCamera = open();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mCamera == null) {
            return;
        }

        if (mCameraParam == null) {
            mCameraParam = mCamera.getParameters();
        }

        mCameraParam.setPictureFormat(PixelFormat.JPEG);
        int degree = displayOrientation(this);
        mCamera.setDisplayOrientation(degree);
        // 设置后无效，camera.setDisplayOrientation方法有效
        mCameraParam.set("rotation", degree);
        mPreviewDegree = degree;

        Point point = CameraPreviewUtils.getBestPreview(mCameraParam,
                new Point(mDisplayWidth, mDisplayHeight));

        mPreviewWidth = point.x;
        mPreviewHight = point.y;
        // Preview 768,432

        if (mILivenessStrategy != null) {
            mILivenessStrategy.setPreviewDegree(degree);
        }

        mPreviewRect.set(0, 0, mPreviewHight, mPreviewWidth);

        mCameraParam.setPreviewSize(mPreviewWidth, mPreviewHight);
        mCamera.setParameters(mCameraParam);

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.stopPreview();
            mCamera.setErrorCallback(this);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (RuntimeException e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        }
    }

    protected void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.setErrorCallback(null);
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CameraUtils.releaseCamera(mCamera);
                mCamera = null;
            }
        }
        if (mSurfaceHolder != null) {
            mSurfaceHolder.removeCallback(this);
        }
        if (mILivenessStrategy != null) {
            mILivenessStrategy = null;
        }
    }

    private int displayOrientation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
                break;
        }
        int result = (0 - degrees + 360) % 360;
        if (APIUtils.hasGingerbread()) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
        }
        return result;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsCreateSurface = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format,
                               int width,
                               int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        if (holder.getSurface() == null) {
            return;
        }
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsCreateSurface = false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if (mIsCompletion) {
            return;
        }

        if (mILivenessStrategy == null) {
            mILivenessStrategy = FaceSDKManager.getInstance().getLivenessStrategyModule();
            mILivenessStrategy.setPreviewDegree(mPreviewDegree);
            mILivenessStrategy.setLivenessStrategySoundEnable(mIsEnableSound);

            Rect detectRect = FaceDetectRoundView.getPreviewDetectRect(
                    mDisplayWidth, mPreviewHight, mPreviewWidth);
            mILivenessStrategy.setLivenessStrategyConfig(
                    mFaceConfig.getLivenessTypeList(), mPreviewRect, detectRect, this);
        }
        mILivenessStrategy.livenessStrategy(data);
    }

    @Override
    public void onError(int error, Camera camera) {
    }

    @Override
    public void onLivenessCompletion(FaceStatusEnum status, String message,
                                     HashMap<String, String> base64ImageMap) {
        if (mIsCompletion) {
            return;
        }

        if (base64ImageMap != null) {
            Log.i(TAG, "onLivenessCompletion: hashmap size : " + base64ImageMap.size());
        }

        onRefreshView(status, message);

        if (status == FaceStatusEnum.OK) {
            mIsCompletion = true;
            saveImage(base64ImageMap);
        }
        //Ast.getInstance().faceHit("liveness");
    }

    private void onRefreshView(FaceStatusEnum status, String message) {
        Log.i(TAG, "onRefreshView: status : " + status);
        switch (status) {
            case OK:
            case Liveness_OK:
            case Liveness_Completion:
                onRefreshTipsView(false, message);
                //mTipsBottomView.setText("");
                mFaceDetectRoundView.processDrawState(false);
                onRefreshSuccessView(true);
                break;
            case Detect_DataNotReady:
            case Liveness_Eye:
            case Liveness_Mouth:
            case Liveness_HeadUp:
            case Liveness_HeadDown:
            case Liveness_HeadLeft:
            case Liveness_HeadRight:
            case Liveness_HeadLeftRight:
                onRefreshTipsView(false, message);
               // mTipsBottomView.setText("");
                mFaceDetectRoundView.processDrawState(false);
                onRefreshSuccessView(false);
                break;
            case Detect_PitchOutOfUpMaxRange:
            case Detect_PitchOutOfDownMaxRange:
            case Detect_PitchOutOfLeftMaxRange:
            case Detect_PitchOutOfRightMaxRange:
                onRefreshTipsView(true, message);
                //mTipsBottomView.setText(message);
                mFaceDetectRoundView.processDrawState(true);
                onRefreshSuccessView(false);
                break;
            default:
                onRefreshTipsView(false, message);
                //mTipsBottomView.setText("");
                mFaceDetectRoundView.processDrawState(true);
                onRefreshSuccessView(false);
        }
    }

    private void onRefreshTipsView(boolean isAlert, String message) {
        if (isAlert) {
            if (mTipsIcon == null) {
                mTipsIcon = getResources().getDrawable(R.mipmap.ic_warning);
                mTipsIcon.setBounds(0, 0, (int) (mTipsIcon.getMinimumWidth() * 0.7f),
                        (int) (mTipsIcon.getMinimumHeight() * 0.7f));
                mTipsTopView.setCompoundDrawablePadding(15);
            }
            mTipsTopView.setBackgroundResource(R.drawable.bg_tips);
            mTipsTopView.setText(R.string.detect_standard);
            mTipsTopView.setCompoundDrawables(mTipsIcon, null, null, null);
        } else {
            mTipsTopView.setBackgroundResource(R.drawable.bg_tips_no);
            mTipsTopView.setCompoundDrawables(null, null, null, null);
            if (!TextUtils.isEmpty(message)) {
                mTipsTopView.setText(message);
            }
        }
    }

    private void onRefreshSuccessView(boolean isShow) {
        if (mSuccessView.getTag() == null) {
            Rect rect = mFaceDetectRoundView.getFaceRoundRect();
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSuccessView.getLayoutParams();
            rlp.setMargins(
                    rect.centerX() - (mSuccessView.getWidth() / 2),
                    rect.top - (mSuccessView.getHeight() / 2),
                    0,
                    0);
            mSuccessView.setLayoutParams(rlp);
            mSuccessView.setTag("setlayout");
        }
        mSuccessView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    private void saveImage(HashMap<String, String> imageMap) {
        //Set<Map.Entry<String, String>> sets = imageMap.entrySet();

        Set<String> keys = imageMap.keySet();
        for (String key : keys){
            Log.i(TAG, "saveImage: key : " + key);
        }
        //获取key 为 bestImage0的图片
        base64img = imageMap.get("bestImage0");

        //Bitmap bmp = null;
        //mImageLayout.removeAllViews();
        /*int i = 0;
        int size = sets.size();
        Log.i(TAG, "saveImage: size : " + size);
        for (Map.Entry<String, String> entry : sets) {
            //bmp = base64ToBitmap(entry.getValue());
            //保存第一张，传给后台
            if (i == 0){
                base64img = entry.getValue();
                Log.i(TAG, "saveImage: i : " + i);
            }
            i++;
           *//* ImageView iv = new ImageView(this);
            iv.setImageBitmap(bmp);
            mImageLayout.addView(iv, new LinearLayout.LayoutParams(300, 300));*//*
        }
        */

        //Log.i(TAG, "saveImage: base64img size : " + base64img.length());
        //Log.i(TAG, "saveImage: base64img : " + base64img);
        //上传采集的图片给后台
        //Log.i(TAG, "saveImage: flag : " + flag);
        if (flag == 1){
            //修改人脸
            faceUpdateUser(base64img);
            //测试用
            //OkHttpUtil.post(base64img);
        }else {
            //录入人脸
            faceRegister(phone, base64img);
        }

    }

    private static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64Utils.decode(base64Data, Base64Utils.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    private void faceSwipSuccess(){
        layoutFace.setVisibility(View.VISIBLE);
        layoutFace.setSelected(true);
        textFace.setSelected(true);
        textFace.setText("录入成功，即将自动跳转...");
        textFace.setEnabled(false);
    }

    private void faceSwipFail(){
        layoutFace.setVisibility(View.VISIBLE);
        layoutFace.setSelected(false);
        textFace.setSelected(false);
        textFace.setText("录入失败，点击重新录入");
        textFace.setEnabled(true);
    }

    //失败，点击重新刷脸
    @OnClick(R.id.textView_face_swiping)
    public void onClickFaceSwiping(){
        if (!textFace.isSelected()){
            layoutFace.setVisibility(View.INVISIBLE);
            //人脸识别的逻辑
            restartFaceSwiping();
        }
    }

    private void faceRegister(String phone, String base64Img) {

        linearLayout.setVisibility(View.VISIBLE);

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.faceRegister(phone, base64Img)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        try {
                            String responseText = response.string();
                            if (responseText.contains("code")){
                                JSONObject obj = JSON.parseObject(responseText);
                                int code = Integer.valueOf(obj.getString("code"));
                                Log.i(TAG, "onNext: code : " + code);
                                if (code == 0){
                                    faceSwipSuccess();
                                    linearLayout.setVisibility(View.INVISIBLE);
                                    Intent intentAc = new Intent(IntoFaceActivity2.this, SetPasswordActivity.class);
                                    startActivity(intentAc);
                                }else if (code == -1){
                                    faceSwipFail();
                                    RetrofitUtil.errorMsg(obj.getString("message"));
                                    linearLayout.setVisibility(View.INVISIBLE);
                                }

                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                            faceSwipSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        linearLayout.setVisibility(View.INVISIBLE);
                        faceSwipFail();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 修改人脸
     * @param base64Img
     */
    private void faceUpdateUser(String base64Img){

        linearLayout.setVisibility(View.VISIBLE);

        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.faceUpdateUser(userId,base64Img,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        int code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 修改人脸code ： " + code);
                        if (code == 0){
                            faceSwipSuccess();
                            linearLayout.setVisibility(View.INVISIBLE);
                            /*Intent intentAc = new Intent(IntoFaceActivity2.this, EditProfileActivity.class);
                            startActivity(intentAc);*/
                            finish();
                            Toast.makeText(IntoFaceActivity2.this,"录入人脸成功",Toast.LENGTH_SHORT).show();
                        }else if (code == -1){
                            faceSwipFail();
                            linearLayout.setVisibility(View.INVISIBLE);
                            RetrofitUtil.errorMsg(generalData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(IntoFaceActivity2.this,generalData.message);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e : " + e );
                        RetrofitUtil.requestError();
                        faceSwipFail();
                        linearLayout.setVisibility(View.INVISIBLE);
                        if(e instanceof HttpException){
                            ResponseBody body = ((HttpException) e).response().errorBody();
                            try {
                                Log.i(TAG, "onError: errorBody : " + body.string());

                            } catch (IOException IOe) {
                                IOe.printStackTrace();
                            }
                        }else if (e instanceof JsonSyntaxException){
                            //ResponseBody body = ((JsonSyntaxException) e)
                            Log.i(TAG, "onError: message : " + e.getMessage());
                            Log.i(TAG, "onError: ");
                        }

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
