package com.typartybuilding.fragment.loginregister;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.activity.loginRelatedActivity.LoginActivity;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 设置密码
 */
public class ForgetPwSetPasswordFragment extends BaseFragment {

    private String TAG = "ForgetPwSetPasswordFragment";

    @BindView(R.id.edit_password)
    EditText editPassword;        //密码输入框
    @BindView(R.id.button_complete)
    Button btnComplet;

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
        //获取焦点
        editPassword.setFocusable(true);
        editPassword.setFocusableInTouchMode(true);
        editPassword.requestFocus();
        //弹出软件盘
       Utils.showSoftInput(editPassword);

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_forget_pw_set_password;
    }

    //重置密码
    @OnClick(R.id.button_complete)
    public void onClickBtn(){
        String phone = MyApplication.pref.getString(MyApplication.prefKey5_phone,"");
        String editText = editPassword.getText().toString();
        String md5Password = Utils.md5Encrypt(editText);
        if (editText.length() >= 6) {
            resetPassword(phone, md5Password);
        }else {
            Toast.makeText(getActivity(),"密码格式不正确",Toast.LENGTH_SHORT).show();
        }
    }


    private void resetPassword(String phone, String password) {
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.resetPassword(phone,password)
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
                            Intent intentAc = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intentAc);
                            Toast.makeText(getContext(),"密码设置成功",Toast.LENGTH_SHORT).show();
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
