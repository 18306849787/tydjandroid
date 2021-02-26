package com.typartybuilding.fragment.loginregister;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.activity.loginRelatedActivity.ForgetPasswordActivity;
import com.typartybuilding.activity.myRelatedActivity.ResetPasswordActivity;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.retrofit.RetrofitUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 *  忘记密码时 使用
 * 输入电话号码
 */
public class ResetPwInputPhoneFragment extends BaseFragment {

    private String TAG = "InputPhoneFragment";

    @BindView(R.id.button_next)
    Button btnNext;                     //下一步的按钮，设置密码时，需更改文字提示
    @BindView(R.id.edit_phone_num)
    EditText edtPhoneNum;              //手机号输入框
    @BindView(R.id.imageView_clear)
    ImageView imgClear;                //清空输入框的按钮
    @BindView(R.id.textView_headline)
    TextView textHeadLine;

    public String phone;     //输入的电话号码
    private ResetPasswordActivity activity;    //忘记密码页面



    @Override
    protected void initViews(Bundle savedInstanceState) {
        setEdtClear();
        activity = (ResetPasswordActivity) getActivity();
        textHeadLine.setText("重置密码");
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_input_phone_reset_pw;
    }

    /**
     * 设置editview 一键清除
     */
    private void setEdtClear(){
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPhoneNum.setText("");
            }
        });
        edtPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPhoneNum.getText().toString() != null && !edtPhoneNum.getText().toString().equals("")){
                    imgClear.setVisibility(View.VISIBLE);
                }else {
                    imgClear.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    @OnClick(R.id.button_next)
    public void onClickBtnNext(){
        phone = edtPhoneNum.getText().toString();
        Log.i(TAG, "onClickBtnNext: phone : " + phone);
        Log.i(TAG, "onClickBtnNext: length : " + phone.length());;
        if (phone.length() != 11){
            Toast.makeText(getActivity(),"电话号码格式不正确",Toast.LENGTH_SHORT).show();
        }else {
            //将电话号码，存入本地
            MyApplication.editor.putString(MyApplication.prefKey5_phone,phone);
            MyApplication.editor.apply();
            sendMsg(phone);
        }
    }


    private void sendMsg(String phone) {
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.sendMsg(1, phone)
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
                            activity.loadFragment(1);
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
