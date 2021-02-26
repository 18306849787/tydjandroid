package com.typartybuilding.activity.myRelatedActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.base.LoginRelatedBaseActivity;
import com.typartybuilding.fragment.loginregister.ForgetPwInputCodeFragment;
import com.typartybuilding.fragment.loginregister.ForgetPwInputPhoneFragment;
import com.typartybuilding.fragment.loginregister.ForgetPwSetPasswordFragment;
import com.typartybuilding.fragment.loginregister.ResetPwInputCodeFragment;
import com.typartybuilding.fragment.loginregister.ResetPwInputPhoneFragment;
import com.typartybuilding.utils.ActivityCollectorUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordActivity extends LoginRelatedBaseActivity {

    private String TAG = "ForgetPasswordActivity";

    @BindView(R.id.textView_title)
    public TextView textTitle;

    public List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        //设置表题
        textTitle.setText("重置密码");
        initFragment();
        //第一次加载 输入手机号的fragment
        loadFragment(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCollectorUtil.addActivity(this);
    }

    private void initFragment(){
        ResetPwInputPhoneFragment fragment1 = new ResetPwInputPhoneFragment();
        ResetPwInputCodeFragment fragment2 = new ResetPwInputCodeFragment();
        ForgetPwSetPasswordFragment fragment3 = new ForgetPwSetPasswordFragment();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);
    }

    /**
     * 动态加载FragMentTitle
     */
    public void loadFragment(int i){
        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_register,fragmentList.get(i));
        //transaction.addToBackStack(null);
        transaction.commit();
    }






}
