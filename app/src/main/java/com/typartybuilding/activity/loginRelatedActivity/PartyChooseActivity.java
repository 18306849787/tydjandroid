package com.typartybuilding.activity.loginRelatedActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.base.LoginRelatedBaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ActivityCollectorUtil;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 *  选择是否时党员
 */
public class PartyChooseActivity extends LoginRelatedBaseActivity {

    private String TAG = "PartyChooseActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;
    @BindViews({R.id.textView_yes, R.id.textView_no})
    TextView textView[];                 //是否是党员 选择


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_choose);
        ButterKnife.bind(this);
        textTitle.setText("3/4");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCollectorUtil.addActivity(this);
    }

    @OnClick(R.id.button_next)
    public void onClickBtnNext(){
        if (textView[0].isSelected()){
            Intent intentAc = new Intent(this,PartyCertificationActivity.class);
            startActivity(intentAc);
        }else if (textView[1].isSelected()){

            startCertify();


        }


    }

    @OnClick({R.id.textView_yes, R.id.textView_no})
    public void onClickTextView(View view){
        switch (view.getId()) {
            case R.id.textView_yes:
                textView[0].setSelected(true);
                textView[1].setSelected(false);
                break;
            case R.id.textView_no:
                textView[1].setSelected(true);
                textView[0].setSelected(false);
                break;
        }

    }

    private void startCertify( ) {

        String phone = MyApplication.pref.getString(MyApplication.prefKey5_phone,"");
        String idCard = null;

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.partyCertify(phone, 1,idCard)
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
                            Intent intentAc = new Intent(PartyChooseActivity.this, SetPasswordActivity.class);
                            //Intent intentAc = new Intent(this,IntoFaceActivity.class);
                            startActivity(intentAc);
                        }else if (code == -1){
                            Log.i(TAG, "onNext: message : "+ reciMsgData.message);
                            Toast.makeText(PartyChooseActivity.this,"党员选择失败",Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e);
                        RetrofitUtil.requestError();
                        Toast.makeText(MyApplication.getContext(),"党员选择失败",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });

    }


}
