package com.typartybuilding.fragment.fragmentbottomnavigation;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.tools.ToastManage;
import com.typartybuilding.R;
import com.typartybuilding.activity.myRelatedActivity.AnswerBreakthroughActivity;
import com.typartybuilding.activity.myRelatedActivity.EditProfileActivity;
import com.typartybuilding.activity.myRelatedActivity.IntegrationRuleActivity;
import com.typartybuilding.activity.myRelatedActivity.MyAttentionActivity;
import com.typartybuilding.activity.myRelatedActivity.MyCollectActivity;
import com.typartybuilding.activity.myRelatedActivity.MyFootprintActivity;
import com.typartybuilding.activity.myRelatedActivity.MyMicVideoActivity;
import com.typartybuilding.activity.myRelatedActivity.PartyCertificationActivity;
import com.typartybuilding.activity.myRelatedActivity.SmartAnswerActivityNew;
import com.typartybuilding.activity.myRelatedActivity.SystemSettingsActivity;
import com.typartybuilding.activity.myRelatedActivity.WishClaimActivity;
import com.typartybuilding.activity.second.mine.MyFootprintAct;
import com.typartybuilding.activity.second.mine.MycollectAct;
import com.typartybuilding.base.BaseAct;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.personaldata.PersonalInfo;
import com.typartybuilding.loader.MineLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ActivityCollectorUtil;
import com.typartybuilding.utils.UserUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-02
 * @describe
 */
public class MineFragment extends BaseFra {
    private String TAG = " FragmentMy";
    private PersonalInfo personalInfo;

    @BindView(R.id.me_head_img)
    ImageView headImg;
    @BindView(R.id.mine_fragment_point)
    TextView textIntegral; //积分
    @BindView(R.id.me_name)
    TextView textName;
    @BindView(R.id.me_fragment_auth)
    ImageView authState;
    @BindView(R.id.mine_fragment_point_sign)
    ImageView mSign;
    @BindView(R.id.me_fragment_shuoming)
    TextView mAddress;
    @BindView(R.id.me_level)
    SuperTextView mLevel;
    @BindView(R.id.me_head_img_lv)
    ImageView mLvImg;

    MineLoader mineLoader;

    @Override
    public void initData() {

        mineLoader = new MineLoader();

    }


    @Override
    public void loadingData() {
        super.loadingData();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            getPersonalInfo();

        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_my_ha_new;
    }


    private void getPersonalInfo() {
        if (MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,3)==3){
            return;
        }
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getPersonalInfo(Integer.valueOf(UserUtils.getIns().getUserId()), UserUtils.getIns().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PersonalInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PersonalInfo personalInfo) {
                        int code = Integer.valueOf(personalInfo.code);
                        if (code == 0) {
                            initView(personalInfo);
                        } else if (code == -1) {
                            RetrofitUtil.errorMsg(personalInfo.message);
                        } else if (code == 10) {
                            RetrofitUtil.tokenLose(getActivity(), personalInfo.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void initView(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
        if (personalInfo != null) {
            PersonalInfo.Data data = personalInfo.data;
            Log.i(TAG, "initView:  PersonalInfo.Data : " + data);

            if (data != null) {
                mAddress.setText(data.hmUserParty.partyOrg);
                mLevel.setCenterString("LV." + data.userLevel);
                mLevel.setCenterTextIsBold(true);
                if (Integer.valueOf(data.userLevel) < 4) {
                    mLvImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lv1_3));
                } else if (Integer.valueOf(data.userLevel) < 7) {
                    mLvImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lv4_6));
                } else if (Integer.valueOf(data.userLevel) < 10) {
                    mLvImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lv7_9));
                } else if (Integer.valueOf(data.userLevel) < 17) {
                    mLvImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lv10_16));
                } else {
                    mLvImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_lv17_all));
                }

                int userType = data.userType;
                //若用户进行了 党员认证，需刷新本地存的用户 类型
                MyApplication.editor.putInt(MyApplication.prefKey10_login_userType, userType);
                MyApplication.editor.apply();
                // "userType":   //1：普通用户，2：认证党员，3：临时账号
                textName.setText(data.username);
                if (userType == 2) {
                    authState.setImageResource(R.mipmap.me_yrz);
                    authState.setEnabled(false);
                } else {
                    authState.setImageResource(R.mipmap.ic_go_auth);

                }

                //me_yqd.png 签到 和未签到

                //加载积分
                textIntegral.setText(data.userIntegral + "");
                if (MyApplication.isChageHeadimg) {
                    //清除缓存
                    //GlideCacheUtil.getInstance().clearImageAllCache(getActivity());
                    //给图片url加个时间戳
                    data.headImg = data.headImg + "?" + System.currentTimeMillis();
                    //保存头像到本地
                    MyApplication.editor.putString(MyApplication.prefKey12_login_headImg, data.headImg);
                    MyApplication.editor.apply();
                    MyApplication.isChageHeadimg = false;
                }
                mSign.setImageResource(data.signed ? R.mipmap.me_yqd : R.mipmap.me_jfqd);
                mSign.setEnabled(!data.signed);
                //加载头像
                String headimg = MyApplication.pref.getString(MyApplication.prefKey12_login_headImg, "");
                Glide.with(getActivity()).load(headimg)
                        .apply(MyApplication.requestOptions2)
                        .into(headImg);
            }
        }
    }

    // R.id.constraintLayout5, R.id.constraintLayout5_2,
    //我的收藏，我的关注，我的足迹，我的微视，知识竞答，答题闯关，积分规则，系统设置 , 心愿认领
    @OnClick({R.id.mine_fragment_point_sign, R.id.mine_fragment_collection,
            R.id.mine_fragment_follow, R.id.mine_fragment_footprint, R.id.mine_fragment_weishi,
            R.id.mine_fragment_Integral_rule, R.id.mine_fragment_set, R.id.mine_fragment_wish})
    public void onClickSkip(View view) {
        switch (view.getId()) {
            case R.id.mine_fragment_point_sign:
                requestSign();
                break;
            case R.id.mine_fragment_collection:
                skipToActivity(1);
                break;
            case R.id.mine_fragment_follow:
                skipToActivity(2);
                break;
            case R.id.mine_fragment_footprint:
                skipToActivity(3);
                break;
            case R.id.mine_fragment_weishi:
                skipToActivity(4);
                break;
//            case R.id.constraintLayout5://知识竞答
//                skipToActivity(5);
//            break;
            case R.id.mine_fragment_Integral_rule://积分规则
                skipToActivity(6);
                break;
            case R.id.mine_fragment_set:
                skipToActivity(7);
                break;
            case R.id.mine_fragment_wish://心愿认领
                skipToActivity(8);
                break;
//            case R.id.mine_fragment_wish:
//                skipToActivity(9);
//                break;
        }
    }

    private void requestSign() {
        mineLoader.getSign().subscribe(new RequestCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(getActivity(), "签到成功", Toast.LENGTH_SHORT).show();
                mSign.setImageResource(R.mipmap.me_yqd);
                mSign.setEnabled(false);
                getPersonalInfo();
            }

            @Override
            public void onFail(Throwable e) {

            }
        });
    }

    private void skipToActivity(int i) {
        if (i == 1) {       //我的收藏
//            Intent intent1 = new Intent(getActivity(), MyCollectActivity.class);
//            startActivity(intent1);
            ARouter.getInstance().build(MycollectAct.PATH).navigation();

        } else if (i == 2) { //我的关注
            Intent intent2 = new Intent(getActivity(), MyAttentionActivity.class);
            startActivity(intent2);

        } else if (i == 3) { //我的足迹
//            Intent intent3 = new Intent(getActivity(), MyFootprintActivity.class);
//            startActivity(intent3);

            ARouter.getInstance().build(MyFootprintAct.PATH).navigation();
        } else if (i == 4) { //我的微视
            Intent intent4 = new Intent(getActivity(), MyMicVideoActivity.class);
            startActivity(intent4);

        } else if (i == 5) { //知识竞答
            Intent intent5 = new Intent(getActivity(), SmartAnswerActivityNew.class);
            startActivity(intent5);

        } else if (i == 6) { //积分规则
            Intent intent6 = new Intent(getActivity(), IntegrationRuleActivity.class);
            startActivity(intent6);

        } else if (i == 7) { //系统设置
            Intent intent7 = new Intent(getActivity(), SystemSettingsActivity.class);
            startActivity(intent7);
            ActivityCollectorUtil.addActivity(getActivity());
        } else if (i == 8) { //心愿认领
            Intent intent8 = new Intent(getActivity(), WishClaimActivity.class);
            startActivity(intent8);

        } else if (i == 9) { //答题闯关
            Intent intent9 = new Intent(getActivity(), AnswerBreakthroughActivity.class);
            startActivity(intent9);
        }

    }

    //跳转到编辑个人信息
    @OnClick(R.id.me_head_img)
    public void onClickNext() {
        Intent intentAc = new Intent(getActivity(), EditProfileActivity.class);
        intentAc.putExtra("PersonalInfo", personalInfo);
        startActivity(intentAc);
    }

    //点击认证，跳转到党员认证
    @OnClick(R.id.me_fragment_auth)
    public void onClickAuthentication() {
        if (personalInfo.data.userType != 2) {
            Intent intent = new Intent(getActivity(), PartyCertificationActivity.class);
            startActivity(intent);
        }
    }
}
