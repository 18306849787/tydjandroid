package com.typartybuilding.fragment.loginregister;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.activity.loginRelatedActivity.ForgetPasswordActivity;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 *   忘记密码时 使用
 *  输入验证码
 */
public class ForgetPwInputCodeFragment extends BaseFragment {

    private String TAG = "InputCodeFragment";

    @BindView(R.id.textView_phone_num)
    TextView textPhoneNum;             //验证码发送到那个手机的号码
    @BindViews({R.id.editText1, R.id.editText2, R.id.editText3, R.id.editText4})
    EditText editText[] ;              //验证码的四个输入框

    private ForgetPasswordActivity activity;
    private String phone ;

    @Override
    protected void initViews(Bundle savedInstanceState) {

        activity = (ForgetPasswordActivity)getActivity();
        //获取电话号码
        phone = MyApplication.pref.getString(MyApplication.prefKey5_phone,"");
        setTextPhoneNum();

        //初始化 验证码输入框 的监听事件
        for (int i = 0; i < 4; i ++){
            setEidtSkip(i);
        }
        //第一个输入框 获得焦点
        editText[0].setFocusable(true);
        editText[0].setFocusableInTouchMode(true);
        editText[0].requestFocus();
        //弹出输入法
        Utils.setSoftInput();
    }

    private void setTextPhoneNum(){
        if (phone != null) {
            String str1 = phone.substring(0, 3);
            String str2 = phone.substring(3, 7);
            String str3 = phone.substring(7, 11);
            textPhoneNum.setText("+86" + " " + str1 + " " + str2 + " " + str3);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_input_code;
    }

    private int count = 0;
    private void setEidtSkip(int i){

        editText[i].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText[i].getText().toString() != null && !editText[i].getText().toString().equals("")){
                    if (i != 3){
                        int j = i + 1;
                        editText[i].setFocusable(false);
                        editText[i].setFocusableInTouchMode(false);
                        editText[j].setFocusable(true);
                        editText[j].setFocusableInTouchMode(true);
                        editText[j].requestFocus();
                    }
                }
            }
        });
        editText[i].setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i(TAG, "onKey: 进入onKey函数");
                if (keyCode == KeyEvent.KEYCODE_DEL){
                    Log.i(TAG, "onKey: 点击删除键");
                    count++;
                    if (count == 2 ){
                        if (i != 0) {
                            int j = i - 1;
                            editText[i].setFocusable(false);
                            editText[i].setFocusableInTouchMode(false);
                            editText[j].setFocusable(true);
                            editText[j].setFocusableInTouchMode(true);
                            editText[j].requestFocus();
                        }
                    }
                }
                return false;
            }
        });
    }

    @OnClick({R.id.editText1, R.id.editText2, R.id.editText3, R.id.editText4})
    public void onClickEdit(View view){
        switch (view.getId()){
            case R.id.editText1 :
                editText[0].setFocusable(true);
                editText[0].setFocusableInTouchMode(true);
                editText[0].requestFocus();
                break;
            case R.id.editText2 :
                editText[1].setFocusable(true);
                editText[1].setFocusableInTouchMode(true);
                editText[1].requestFocus();
                break;
            case R.id.editText3 :
                editText[2].setFocusable(true);
                editText[2].setFocusableInTouchMode(true);
                editText[2].requestFocus();
                break;
            case R.id.editText4 :
                editText[3].setFocusable(true);
                editText[3].setFocusableInTouchMode(true);
                editText[3].requestFocus();
                break;
        }
    }

    @OnClick(R.id.button_next)
    public void onClickBtnNext(){
        String code = "";
        for (int i = 0; i < 4; i++){
            code += editText[i].getText().toString();
        }

        Log.i(TAG, "onClickBtnNext: phone : " + phone);
        Log.i(TAG, "onClickBtnNext: code : " + code);;
        if (code.length() != 4){
            Toast.makeText(getActivity(),"验证码错误",Toast.LENGTH_SHORT).show();
        }else {
            sendMsg(phone,code);
        }
    }


    private void sendMsg(String phone, String code) {
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.inputCode(phone, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReciMsgData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ReciMsgData reciMsgData) {

                        int code = Integer.valueOf(reciMsgData.code);
                        Log.i(TAG, "onNext: code : " + reciMsgData.code);
                        if (code == 0){
                            activity.loadFragment(2);

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(reciMsgData.message);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e);
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });

    }

}
