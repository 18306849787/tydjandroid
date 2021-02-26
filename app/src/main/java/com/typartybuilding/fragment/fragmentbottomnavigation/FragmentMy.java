package com.typartybuilding.fragment.fragmentbottomnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.activity.myRelatedActivity.AnswerBreakthroughActivity;
import com.typartybuilding.activity.myRelatedActivity.EditProfileActivity;
import com.typartybuilding.activity.myRelatedActivity.IntegrationRuleActivity;
import com.typartybuilding.activity.myRelatedActivity.MyAttentionActivity;
import com.typartybuilding.activity.myRelatedActivity.MyCollectActivity;
import com.typartybuilding.activity.myRelatedActivity.MyFootprintActivity;
import com.typartybuilding.activity.myRelatedActivity.MyMicVideoActivity;
import com.typartybuilding.activity.myRelatedActivity.PartyCertificationActivity;
import com.typartybuilding.activity.myRelatedActivity.SmartAnswerActivity;
import com.typartybuilding.activity.myRelatedActivity.SmartAnswerActivityNew;
import com.typartybuilding.activity.myRelatedActivity.SystemSettingsActivity;
import com.typartybuilding.activity.myRelatedActivity.WishClaimActivity;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.personaldata.PersonalInfo;
import com.typartybuilding.gsondata.personaldata.TaMicro;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ActivityCollectorUtil;
import com.typartybuilding.utils.GlideCacheUtil;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class FragmentMy extends BaseFragment {

    private String TAG = " FragmentMy";

    //党员 信息
    @BindView(R.id.textView_name)
    TextView textName;              //姓名
    @BindView(R.id.textView_site)
    TextView textSite;              //所在地区
    @BindView(R.id.imageView_dh)
    ImageView imgDh;                //党徽的图片

    //非党员 信息
    @BindView(R.id.textView_name1)
    TextView textName1;             //非党员，没有姓名，显示电话号码
    @BindView(R.id.textView_authentication)
    TextView authentication;        //非党员，认证按钮

    @BindView(R.id.imageView_headimg)
    CircleImageView headImg;        //头像
    @BindView(R.id.imagebutton_next)
    ImageButton btnNext;            //跳转到个人资料编辑页面

    @BindView(R.id.textView_integral)
    TextView textIntegral;           //积分显示

    private int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

    private PersonalInfo personalInfo;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //loadView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_my_ha;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        getPersonalInfo();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            onResume();
        }
    }

    private void initView(PersonalInfo personalInfo){
        this.personalInfo = personalInfo;
        if (personalInfo != null){
            PersonalInfo.Data data = personalInfo.data;
            Log.i(TAG, "initView:  PersonalInfo.Data : " + data);

            if (data != null) {
                int userType = data.userType;
                //若用户进行了 党员认证，需刷新本地存的用户 类型
                MyApplication.editor.putInt(MyApplication.prefKey10_login_userType,userType);
                MyApplication.editor.apply();
                // "userType":   //1：普通用户，2：认证党员，3：临时账号
                if (userType == 2) {
                    //非党员 布局不可见
                    textName1.setVisibility(View.INVISIBLE);
                    authentication.setVisibility(View.INVISIBLE);
                    //党员布局可见
                    textName.setVisibility(View.VISIBLE);
                    textSite.setVisibility(View.VISIBLE);
                    imgDh.setVisibility(View.VISIBLE);

                    textName.setText(data.username);
                    Log.i(TAG, "initView: data.hmUserParty.partyOrg : " + data.hmUserParty.partyOrg);
                    textSite.setText(data.hmUserParty.partyOrg);

                } else {
                    //非党员布局可见
                    textName1.setVisibility(View.VISIBLE);
                    authentication.setVisibility(View.VISIBLE);
                    //党员布局不可见
                    textName.setVisibility(View.INVISIBLE);
                    textSite.setVisibility(View.INVISIBLE);
                    imgDh.setVisibility(View.INVISIBLE);

                    textName1.setText(data.username);
                }
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
                //加载头像
                Log.i(TAG, "initView: headImg : " + data.headImg);
                String headimg = MyApplication.pref.getString(MyApplication.prefKey12_login_headImg,"");
                Log.i(TAG, "initView: 本地保存的头像 ： " + headimg);
                Glide.with(getActivity()).load(headimg)
                        .apply(MyApplication.requestOptions2)
                        .into(headImg);

                //修改头像后刷新首页的头像
                ((HomeActivity)getActivity()).refreshHeadImg();
            }
        }
    }

    private void getPersonalInfo(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getPersonalInfo(userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PersonalInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PersonalInfo personalInfo) {
                        int code = Integer.valueOf(personalInfo.code);
                        if (code == 0){
                            initView(personalInfo);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(personalInfo.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),personalInfo.message);
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


    //跳转到编辑个人信息
    @OnClick(R.id.imagebutton_next)
    public void onClickNext(){
        Intent intentAc = new Intent(getActivity(), EditProfileActivity.class);
        intentAc.putExtra("PersonalInfo",personalInfo);
        startActivity(intentAc);
    }

    //点击头像，也跳转到编辑个人信息
    @OnClick(R.id.imageView_headimg)
    public void onClickEdtProfile(){
        Intent intentAc = new Intent(getActivity(), EditProfileActivity.class);
        intentAc.putExtra("PersonalInfo",personalInfo);
        startActivity(intentAc);
    }

    //我的收藏，我的关注，我的足迹，我的微视，知识竞答，答题闯关，积分规则，系统设置 , 心愿认领
    @OnClick({R.id.constraintLayout1, R.id.constraintLayout2, R.id.constraintLayout3, R.id.constraintLayout4,
            R.id.constraintLayout5,R.id.constraintLayout5_2, R.id.constraintLayout6, R.id.constraintLayout7, R.id.constraintLayout8})
    public void onClickSkip(View view){
        switch (view.getId()){
            case R.id.constraintLayout1:
                skipToActivity(1);
                break;
            case R.id.constraintLayout2:
                skipToActivity(2);
                break;
            case R.id.constraintLayout3:
                skipToActivity(3);
                break;
            case R.id.constraintLayout4:
                skipToActivity(4);
                break;
            case R.id.constraintLayout5:
                skipToActivity(5);
                break;
            case R.id.constraintLayout6:
                skipToActivity(6);
                break;
            case R.id.constraintLayout7:
                skipToActivity(7);
                break;
            case R.id.constraintLayout8:
                skipToActivity(8);
                break;
            case R.id.constraintLayout5_2:
                skipToActivity(9);
                break;
        }
    }

    private void skipToActivity(int i){
        if (i == 1){       //我的收藏
            Intent intent1 = new Intent(getActivity(), MyCollectActivity.class);
            startActivity(intent1);

        }else if (i == 2){ //我的关注
            Intent intent2 = new Intent(getActivity(), MyAttentionActivity.class);
            startActivity(intent2);

        }else if (i == 3){ //我的足迹
            Intent intent3 = new Intent(getActivity(), MyFootprintActivity.class);
            startActivity(intent3);

        }else if (i == 4){ //我的微视
            Intent intent4 = new Intent(getActivity(), MyMicVideoActivity.class);
            startActivity(intent4);

        }else if (i == 5){ //知识竞答
            Intent intent5 = new Intent(getActivity(), SmartAnswerActivityNew.class);
            startActivity(intent5);

        }else if (i == 6){ //积分规则
            Intent intent6 = new Intent(getActivity(), IntegrationRuleActivity.class);
            startActivity(intent6);

        }else if (i == 7){ //系统设置
            Intent intent7 = new Intent(getActivity(), SystemSettingsActivity.class);
            startActivity(intent7);
            ActivityCollectorUtil.addActivity(getActivity());
        }else if (i == 8){ //心愿认领
            Intent intent8 = new Intent(getActivity(), WishClaimActivity.class);
            startActivity(intent8);

        }else if (i == 9){ //答题闯关
            Intent intent9 = new Intent(getActivity(), AnswerBreakthroughActivity.class);
            startActivity(intent9);
        }

    }

    //点击认证，跳转到党员认证
    @OnClick(R.id.textView_authentication)
    public void onClickAuthentication(){
        Intent intent = new Intent(getActivity(), PartyCertificationActivity.class);
        startActivity(intent);
    }

}



/* private void loadView(){
        //判断是否为党员

        //加载头像
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.mipmap.me_img_touxiang)
                .fallback(R.mipmap.me_img_touxiang)
                .error(R.mipmap.me_img_touxiang);

        Glide.with(getActivity()).load(imgUrl)
                .apply(requestOptions)  //url为空或异常显示默认头像
                .into(headImg);
    }*/

   /* private void modifyImgHead(){
        //从底部显示
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(getActivity(),0.7f);
        }

    }*/


/*  private void initPopupWindow(){
        popView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popupwindow_modify_head_img, null);
        Button btnCamera = popView.findViewById(R.id.button_camera);
        Button btnAbum = popView.findViewById(R.id.button_album);
        Button btnCancel = popView.findViewById(R.id.button_cancel);
        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(getActivity(),1f);
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
        //拍照 设置头像
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //从相册 设置头像
        btnAbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }*/
