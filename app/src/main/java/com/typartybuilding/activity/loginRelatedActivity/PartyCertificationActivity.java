package com.typartybuilding.activity.loginRelatedActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.base.LoginRelatedBaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.utils.ActivityCollectorUtil;
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

public class PartyCertificationActivity extends LoginRelatedBaseActivity {

    private String TAG = "PartyCertificationActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;           //标题
    @BindView(R.id.edit_id_card)
    EditText editIdCard;          //输入身份证号
    @BindView(R.id.button_certification)
    Button btnCertify;

    private PopupWindow popupWindow;  //底部弹窗  ,认证结果弹窗
    private View popView;             //弹窗布局
    private TextView textResult;      //认证的结果 提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_certification);
        ButterKnife.bind(this);
        textTitle.setText("3/4");
        //初始化弹窗
        initPopupWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCollectorUtil.addActivity(this);
    }

    /**
     * 弹窗
     */
    private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_party_certification, null);
        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Button btnNext = popView.findViewById(R.id.button_next);
        Button btnCancel = popView.findViewById(R.id.button_cancel);
        textResult = popView.findViewById(R.id.textView_result);

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(PartyCertificationActivity.this,1f);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        //下一步
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                Intent intentAc = new Intent(PartyCertificationActivity.this,SetPasswordActivity.class);
                startActivity(intentAc);
            }
        });
    }

    private void showPopupWindow(){
        if (!popupWindow.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
    }

    @OnClick(R.id.button_certification)
    public void onClickCertify(){
        String idCard = editIdCard.getText().toString();
        String phone = MyApplication.pref.getString(MyApplication.prefKey5_phone,"");
        if (idCard.length() != 18){
            Toast.makeText(this,"身份证号格式不正确",Toast.LENGTH_SHORT).show();
        }else {
            Log.i(TAG, "onClickCertify: phone : " + phone);
            Log.i(TAG, "onClickCertify: idCard : " + idCard);
            startCertify(phone,idCard);
        }

    }

    private void startCertify(String phone, String idCard) {
        //身份证号 md5加密
        String md5IdCard = Utils.md5Encrypt(idCard);

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.partyCertify(phone, 2,md5IdCard)
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
                            showPopupWindow();
                            textResult.setText("恭喜成功认证为党员，注册成功后生效！");
                            //隐藏软件盘
                            Utils.hideSoftInput(editIdCard);

                        }else if (code == -1){
                            Log.i(TAG, "onNext: message : "+ reciMsgData.message);
                            showPopupWindow();
                            textResult.setText("认证不通过：" + reciMsgData.message);
                            //隐藏软件盘
                            Utils.hideSoftInput(editIdCard);

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
