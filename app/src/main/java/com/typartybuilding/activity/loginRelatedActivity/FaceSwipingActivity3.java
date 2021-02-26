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

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.ILivenessStrategy;
import com.baidu.idl.face.platform.ILivenessStrategyCallback;
import com.baidu.idl.face.platform.utils.APIUtils;
import com.baidu.idl.face.platform.utils.Base64Utils;
import com.baidu.idl.face.platform.utils.CameraPreviewUtils;
import com.google.gson.Gson;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.baiduface.CameraUtils;
import com.typartybuilding.baiduface.FaceDetectRoundView;
import com.typartybuilding.baiduface.FaceLivenessActivity;
import com.typartybuilding.baiduface.FaceSDKResSettings;
import com.typartybuilding.baiduface.FaceUtils;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.loginregister.UserData;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ActivityCollectorUtil;

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
import retrofit2.Retrofit;

/**
 *  人脸识别 登陆，带活体检测
 */
public class FaceSwipingActivity3 extends RedTitleBaseActivity implements
        SurfaceHolder.Callback,
        Camera.PreviewCallback,
        Camera.ErrorCallback,
        //VolumeUtils.VolumeCallback,
        ILivenessStrategyCallback {


    public static final String TAG = FaceLivenessActivity.class.getSimpleName();

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


    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindView(R.id.framelayout_face_swiping)
    FrameLayout layoutFace;            //刷脸成功失败的布局
    @BindView(R.id.textView_face_swiping)
    TextView textFace;                //刷脸成功失败的提示

    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;        //识别时的进度条

    //采集的图片
    private String base64img;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_face_swiping2);
        ButterKnife.bind(this);
        textTitle.setText("人脸识别");

        //初始化 SDK
        FaceUtils.initLib();

        mRootView = this.findViewById(R.id.detect_root_layout);
        mFrameLayout = (FrameLayout) mRootView.findViewById(R.id.detect_surface_layout);
        mTipsTopView = (TextView) mRootView.findViewById(R.id.detect_top_tips);
        mSuccessView = (ImageView) mRootView.findViewById(R.id.detect_success_image);
        mFaceDetectRoundView = (FaceDetectRoundView) mRootView.findViewById(R.id.detect_face_round);

        init();

    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityCollectorUtil.addActivity(this);
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

        onRefreshView(status, message);

        if (status == FaceStatusEnum.OK) {
            mIsCompletion = true;
            saveImage(base64ImageMap);
        }
        //Ast.getInstance().faceHit("liveness");
    }

    private void onRefreshView(FaceStatusEnum status, String message) {
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
        Set<Map.Entry<String, String>> sets = imageMap.entrySet();
        Log.i(TAG, "saveImage: imageMap size : " + imageMap.size());
        Bitmap bmp = null;
        //mImageLayout.removeAllViews();
        int i = 0;
        for (Map.Entry<String, String> entry : sets) {
            bmp = base64ToBitmap(entry.getValue());
            //保存第一张，传给后台
            if (i == 0){
                base64img = entry.getValue();
            }
            i++;

           /* ImageView iv = new ImageView(this);
            iv.setImageBitmap(bmp);
            mImageLayout.addView(iv, new LinearLayout.LayoutParams(300, 300));*/
        }
        Log.i(TAG, "saveImage: base64img size : " + base64img.length());
        Log.i(TAG, "saveImage: base64img : " + base64img);
        //上传采集的图片给后台
        //上传采集的图片给后台，进行比对
        faceLogin(base64img);

    }

    private static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64Utils.decode(base64Data, Base64Utils.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    private void faceSwipSuccess(){
        layoutFace.setVisibility(View.VISIBLE);
        layoutFace.setSelected(true);
        textFace.setSelected(true);
        textFace.setText("认证成功，即将自动登录...");
        textFace.setEnabled(false);
    }

    private void faceSwipFail(){
        layoutFace.setVisibility(View.VISIBLE);
        layoutFace.setSelected(false);
        textFace.setSelected(false);
        textFace.setText("认证失败，点击重新识别");
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

    /**
     * 人脸识别登陆
     */
    private void faceLogin(String base64Img) {
        //进度条
        linearLayout.setVisibility(View.VISIBLE);

        //个推返回的id
        String clientId = MyApplication.pref.getString(MyApplication.prefKey6_GT_clientId, "");
        if (clientId == null){
            clientId = "0";
        }
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.faceLogin(base64Img,clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserData userData) {
                        int code = Integer.valueOf(userData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            //比对成功
                            faceSwipSuccess();
                            linearLayout.setVisibility(View.INVISIBLE);

                            MyApplication.editor.putInt(MyApplication.pretKey9_login_userId,userData.data.userId);
                            MyApplication.editor.putInt(MyApplication.prefKey10_login_userType,userData.data.userType);
                            MyApplication.editor.putString(MyApplication.prefKey8_login_token,userData.data.token);
                            MyApplication.editor.putString(MyApplication.prefKey13_login_userName,userData.data.nickName);
                            //给头像url 加个时间戳
                            userData.data.headImg = userData.data.headImg + "?" + System.currentTimeMillis();
                            MyApplication.editor.putString(MyApplication.prefKey12_login_headImg,userData.data.headImg);
                            //存放用户数据
                            Gson gson = new Gson();
                            String json = gson.toJson(userData);
                            MyApplication.editor.putString(MyApplication.prefKey11_userData,json);
                            //记录登陆状态
                            MyApplication.editor.putInt(MyApplication.prefKey7_login_state,1);
                            MyApplication.editor.apply();

                            Intent intent_ac = new Intent(MyApplication.getContext(), HomeActivity.class);
                            startActivity(intent_ac);
                        }else if (code == -1){
                            //比对失败
                            faceSwipFail();
                            linearLayout.setVisibility(View.INVISIBLE);
                            RetrofitUtil.errorMsg(userData.message);
                            Log.i(TAG, "onNext: message : " + userData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(FaceSwipingActivity3.this,userData.message);
                            //比对失败
                            faceSwipFail();
                            linearLayout.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        RetrofitUtil.requestError();
                        Log.i(TAG, "onError: e : " + e );
                        //比对失败
                        faceSwipFail();
                        linearLayout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");

                    }
                });

       /* loginRegister.faceLogin(base64Img,clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String responseText = responseBody.string();
                            Log.i(TAG, "onNext: response : " + responseText);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });*/

    }


}
