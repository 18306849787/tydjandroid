package com.typartybuilding.activity.loginRelatedActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.base.LoginRelatedBaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SetPasswordActivity extends LoginRelatedBaseActivity {

    private String TAG = " SetPasswordActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindView(R.id.edit_password)
    EditText editPassword;        //密码输入框
    @BindView(R.id.button_complete)
    Button btnComplet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        ButterKnife.bind(this);
        textTitle.setText("4/4");

    }

    //设置密码
    @OnClick(R.id.button_complete)
    public void onClickBtn(){
        String phone = MyApplication.pref.getString(MyApplication.prefKey5_phone,"");
        String editText = editPassword.getText().toString();
        String md5Password = Utils.md5Encrypt(editText);
        if (editText.length() >= 6) {
            setPassword(phone, md5Password);
        }else {
            Toast.makeText(this,"密码格式不正确",Toast.LENGTH_SHORT).show();
        }
    }


    private void setPassword(String phone, String password) {
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.setPassword(phone,password)
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
                            Intent intentAc = new Intent(SetPasswordActivity.this,LoginActivity.class);
                            startActivity(intentAc);
                            Toast.makeText(SetPasswordActivity.this,"密码设置成功",Toast.LENGTH_SHORT).show();
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
