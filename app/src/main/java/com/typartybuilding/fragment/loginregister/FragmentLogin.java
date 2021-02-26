package com.typartybuilding.fragment.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeAct;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.activity.loginRelatedActivity.ForgetPasswordActivity;
import com.typartybuilding.activity.loginRelatedActivity.RegisterActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.loginregister.UserData;
import com.typartybuilding.gsondata.loginregister.VisitorData;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class FragmentLogin extends Fragment {


    private String TAG = "FragmentLogin";
    private Unbinder unbinder;
    @BindView(R.id.edit_phone_num)
    EditText edtPhoneNum;             //输入手机号码
    @BindView(R.id.edit_password)
    EditText edtPassword;             //输入密码
    @BindView(R.id.text_forget_pw)
    TextView tewForgetPw;             //忘记密码
    @BindView(R.id.button_login)
    Button btnLogin;                  //登录
    @BindView(R.id.button_visitor_longin)
    Button btnVisitorLogin;          //访客登录
    @BindView(R.id.button_register)
    Button btnRegister;               //新用户注册

    private String strPhoneNum;
    private String strPassword;
    private String recordPhone;        //记录上次输入的电话号码
    //private String clientId;         //个推 返回的clientid

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account_login_la,null);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //输入框获取焦点
        edtPhoneNum.setFocusable(true);
        edtPhoneNum.setFocusableInTouchMode(true);
        edtPhoneNum.requestFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        recordPhone = MyApplication.pref.getString(MyApplication.prefKey5_phone,null);
        edtPhoneNum.setText(recordPhone);
        if (recordPhone != null) {
            edtPhoneNum.setSelection(recordPhone.length());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //登陆失败，恢复按钮的可点击状态
    private void canClick(){
        tewForgetPw.setEnabled(true);            //忘记密码
        btnLogin.setEnabled(true);            //登陆
        btnVisitorLogin.setEnabled(true);         //访客登录
        btnRegister.setEnabled(true);               //新用户注册
    }

    //调登陆接口时，按钮不可再点击
    private void notClick(){
        tewForgetPw.setEnabled(false);            //忘记密码
        btnLogin.setEnabled(false);            //登陆
        btnVisitorLogin.setEnabled(false);         //访客登录
        btnRegister.setEnabled(false);               //新用户注册
    }

    //注册点击事件， 忘记密码、登录、访客登录、新用户注册 的点击事件
    @OnClick({R.id.text_forget_pw,R.id.button_login,
            R.id.button_visitor_longin,R.id.button_register})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.text_forget_pw :
                //Log.i(TAG, "onViewClicked: 点击忘记密码");
                forgetPassward();
                break;
            case R.id.button_login :
                //Log.i(TAG, "onViewClicked: 点击登录");
                //获取输入的手机号和密码
                strPhoneNum = edtPhoneNum.getText().toString();
                strPassword = edtPassword.getText().toString();
                //Log.i(TAG, "onViewClicked: 手机号：" + strPhoneNum);
                //Log.i(TAG, "onViewClicked: 密码 ：" + strPassword);
                buttonLongin();

                break;
            case R.id.button_visitor_longin :
                //Log.i(TAG, "onViewClicked: 点击访客登录");
                buttonVisitorLongin();

                break;
            case R.id.button_register :
                //Log.i(TAG, "onViewClicked: 点击新用户注册");
                buttonRegister();
                break;
            default:
                break;
        }
    }

    //忘记密码
    private void forgetPassward(){
        Intent intentAc = new Intent(getActivity(), ForgetPasswordActivity.class);
        startActivity(intentAc);
    }

    //登陆
    private void buttonLongin(){
        //对密码 进行md5加密
        String md5PassWard = Utils.md5Encrypt(strPassword);
        if (strPhoneNum.length() == 11 && strPassword != ""){
            passwardLogin(strPhoneNum,md5PassWard);
            //保存电话号码
            MyApplication.editor.putString(MyApplication.prefKey5_phone,strPhoneNum);
            MyApplication.editor.apply();
        }else {
            Toast.makeText(getActivity(),"电话号码或密码格式不正确",Toast.LENGTH_SHORT).show();
        }

    }

    //访客登陆
    private void buttonVisitorLongin(){
        visitorLogin();

    }

    //注册
    private void buttonRegister(){
        Intent intentAc = new Intent(getActivity(), RegisterActivity.class);
        startActivity(intentAc);
    }

    /**
     * 密码登陆
     */
    private void passwardLogin(String phone, String password) {
        //登陆时，其他按钮不可点击
        notClick();
        //获取个推 返回的clientid
        String clientId = MyApplication.pref.getString(MyApplication.prefKey6_GT_clientId,"");
        if (clientId == ""){
            MyApplication.initPushService();
        }
        //Log.i(TAG, "passwardLogin: clientId : " + clientId);
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.passwardLongin(phone, password,clientId)
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

                            MyApplication.editor.putInt(MyApplication.pretKey9_login_userId,userData.data.userId);
                            MyApplication.editor.putInt(MyApplication.prefKey10_login_userType,userData.data.userType);
                            MyApplication.editor.putString(MyApplication.prefKey8_login_token,userData.data.token);

                            //Log.i(TAG, "onNext: token : " + userData.data.token);
                            //Log.i(TAG, "onNext: userid : " + userData.data.userId);
                            MyApplication.editor.putString(MyApplication.prefKey13_login_userName,userData.data.nickName);
                            //给头像url 加个时间戳
                            userData.data.headImg = userData.data.headImg + "?" + System.currentTimeMillis();
                            MyApplication.editor.putString(MyApplication.prefKey12_login_headImg,userData.data.headImg);
                            //Log.i(TAG, "onNext: userData.data.headImg : " + userData.data.headImg);
                            //记录登陆状态
                            MyApplication.editor.putInt(MyApplication.prefKey7_login_state,1);
                            //存放用户数据
                            Gson gson = new Gson();
                            String json = gson.toJson(userData);
                            MyApplication.editor.putString(MyApplication.prefKey11_userData,json);
                            MyApplication.editor.apply();
                            String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token, "");
                            Intent intent_ac = new Intent(MyApplication.getContext(), HomeAct.class);
                            startActivity(intent_ac);
                            getActivity().finish();
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(userData.message);
                            //登陆失败，恢复点击状态
                            canClick();
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),userData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        RetrofitUtil.requestError();
                        Log.i(TAG, "onError: e : " + e );
                        //登陆失败，恢复点击状态
                        canClick();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");

                    }
                });

    }

    /**
     * 访客登陆
     */
    private void visitorLogin(){
        //登陆时，其他按钮不可点击
        notClick();
        //获取个推 返回的clientid
        String clientId = MyApplication.pref.getString(MyApplication.prefKey6_GT_clientId,"");
        Log.i(TAG, "visitorLogin: clientId : " + clientId);
        if (clientId == ""){
            MyApplication.initPushService();
        }
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.visitorLogin(clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VisitorData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(VisitorData visitorData) {
                        int code = Integer.valueOf(visitorData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                           //Log.i(TAG, "onNext: token : " + visitorData.data.token);
                            //Log.i(TAG, "onNext: usetId : " + visitorData.data.userId);
                            //Log.i(TAG, "onNext: usetType: " + visitorData.data.userType);
                            String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

                            MyApplication.editor.putInt(MyApplication.pretKey9_login_userId,visitorData.data.userId);
                            MyApplication.editor.putInt(MyApplication.prefKey10_login_userType,visitorData.data.userType);
                            MyApplication.editor.putString(MyApplication.prefKey8_login_token,visitorData.data.token);
                            MyApplication.editor.putString(MyApplication.prefKey13_login_userName,visitorData.data.nickName);
                            //访客登陆，清空之前用户头像
                            MyApplication.editor.putString(MyApplication.prefKey12_login_headImg,"");
                            //记录登陆状态
                            MyApplication.editor.putInt(MyApplication.prefKey7_login_state,1);
                            MyApplication.editor.apply();

                            String token1 = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
//                            Intent intent_ac = new Intent(MyApplication.getContext(), HomeActivity.class);

                            Intent intent_ac = new Intent(MyApplication.getContext(), HomeAct.class);
                            startActivity(intent_ac);
                            getActivity().finish();
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(visitorData.message);
                            //登陆失败，恢复点击状态
                            canClick();
                        }else if (code == 10){
                            canClick();
                            RetrofitUtil.tokenLose(getActivity(),visitorData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        RetrofitUtil.requestError();
                        Log.i(TAG, "onError: e : " + e );
                        //登陆失败，恢复点击状态
                        canClick();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

}
