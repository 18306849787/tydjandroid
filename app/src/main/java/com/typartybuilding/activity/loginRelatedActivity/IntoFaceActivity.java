package com.typartybuilding.activity.loginRelatedActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.typartybuilding.R;
import com.typartybuilding.base.LoginRelatedBaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.utils.ActivityCollectorUtil;
import com.typartybuilding.retrofit.RetrofitUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class IntoFaceActivity extends LoginRelatedBaseActivity {

    private String TAG = "IntoFaceActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;
    //录入成功
    @BindView(R.id.textView_success1)
    TextView textSuccess1;            //录入人脸
    @BindView(R.id.textView_success2)
    TextView textSuccess2;            //建议略微低头
    @BindView(R.id.framelayout_succes)
    FrameLayout layoutSuccess;        //录入成功，即将自动跳转...
    //录入失败
    @BindView(R.id.textView_fail1)
    TextView textFail1;            //拍摄您本人人脸，请确保正对手机，光线充足
    @BindView(R.id.framelayout_fail)
    FrameLayout layoutFail;        //录入失败，点击重新录入

    private String phone = MyApplication.pref.getString(MyApplication.prefKey5_phone,"");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_into_face);
        ButterKnife.bind(this);
        textTitle.setText("4/5");

        intoFaceFail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCollectorUtil.addActivity(this);
    }

    //录入成功
    private void intoFaceSuccess(){
        textSuccess1.setVisibility(View.VISIBLE);
        textSuccess2.setVisibility(View.VISIBLE);
        layoutSuccess.setVisibility(View.VISIBLE);

        textFail1.setVisibility(View.INVISIBLE);
        layoutFail.setVisibility(View.INVISIBLE);
    }

    //录入失败
    private void intoFaceFail(){
        textSuccess1.setVisibility(View.INVISIBLE);
        textSuccess2.setVisibility(View.INVISIBLE);
        layoutSuccess.setVisibility(View.INVISIBLE);

        textFail1.setVisibility(View.VISIBLE);
        layoutFail.setVisibility(View.VISIBLE);


    }

    //录入失败，点击重新录入
    @OnClick(R.id.textView_fail)
    public void onClickFail(){
        textFail1.setVisibility(View.INVISIBLE);
        layoutFail.setVisibility(View.INVISIBLE);
        layoutSuccess.setVisibility(View.INVISIBLE);

        textSuccess1.setVisibility(View.VISIBLE);
        textSuccess2.setVisibility(View.VISIBLE);

        startIntoFace();
        //faceRegister(phone,"");
    }


    private void faceRegister(String phone, String base64Img) {
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
                                if (code == -1){
                                    Intent intentAc = new Intent(IntoFaceActivity.this,SetPasswordActivity.class);
                                    startActivity(intentAc);
                                }else if (code == 0){
                                    RetrofitUtil.errorMsg(obj.getString("message"));
                                }

                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void startIntoFace(){
        //MyApplication.initLib();
       /* Intent intent = new Intent(this,FaceLivenessActivity.class);
        startActivity(intent);*/
    }



}
