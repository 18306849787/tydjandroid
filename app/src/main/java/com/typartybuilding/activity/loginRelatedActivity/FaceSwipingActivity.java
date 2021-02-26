package com.typartybuilding.activity.loginRelatedActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.baiduface.FaceUtils;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.utils.ActivityCollectorUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FaceSwipingActivity extends RedTitleBaseActivity {

    private String TAG = "FaceSwipingActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindView(R.id.framelayout_face_swiping)
    FrameLayout layoutFace;            //刷脸成功失败的布局
    @BindView(R.id.textView_face_swiping)
    TextView textFace;                //刷脸成功失败的提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_swiping);
        ButterKnife.bind(this);
        textTitle.setText("人脸识别");
        faceSwipFail();
        //faceSwipSuccess();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCollectorUtil.addActivity(this);
    }

    private void faceSwipSuccess(){
        layoutFace.setVisibility(View.VISIBLE);
        layoutFace.setSelected(true);
        textFace.setSelected(true);
        textFace.setText("认证成功，即将自动登录...");
    }

    private void faceSwipFail(){
        layoutFace.setVisibility(View.VISIBLE);
        layoutFace.setSelected(false);
        textFace.setSelected(false);
        textFace.setText("认证失败，点击重新识别");
    }

    //失败，点击重新刷脸
    @OnClick(R.id.textView_face_swiping)
    public void onClickFaceSwiping(){
        if (!textFace.isSelected()){
            layoutFace.setVisibility(View.INVISIBLE);
            //人脸识别的逻辑
            startFaceSwiping();
        }
    }


    /**
     * 人脸识别登陆
     */
   /* private void faceLogin(String base64Img, String clientId) {
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
                            MyApplication.userType = userData.data.userType;
                            MyApplication.userId = userData.data.userId;
                            MyApplication.userData = userData;
                            //记录登陆状态
                            MyApplication.editor.putInt(MyApplication.prefKey7_login_state,1);
                            MyApplication.editor.apply();

                            Intent intent_ac = new Intent(MyApplication.getContext(), HomeActivity.class);
                            startActivity(intent_ac);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(userData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(FaceSwipingActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        RetrofitUtil.requestError();
                        Log.i(TAG, "onError: e : " + e );
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");

                    }
                });
    }*/

    private void startFaceSwiping(){
        FaceUtils.initLib();
        Intent intent = new Intent(this, FaceSwipingActivity2.class);
        startActivity(intent);
    }



}
