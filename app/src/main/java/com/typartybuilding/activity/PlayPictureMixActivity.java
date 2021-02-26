package com.typartybuilding.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.adapter.viewPagerAdapter.PlayPictureAdapter;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.MixtureData;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayPictureMixActivity extends BaseActivity {

    private String TAG = "PlayPictureActivity";

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.textView_title_bar)
    TextView titleBar;              //标题栏
    @BindView(R.id.circle_img_head)
    CircleImageView headImg;       //头像
    @BindView(R.id.imageView_attention)
    ImageView attention;           //关注
    @BindView(R.id.textView_like)
    TextView textLike;             //点赞
    @BindView(R.id.imagebutton_share)
    ImageButton btnShare;          //分享
    @BindView(R.id.textView_name)
    TextView textName;             //姓名
    @BindView(R.id.textView_abstract)
    TextView textAbstrack;         //图片简介

    private List<String> imgUrlList = new ArrayList<>();   //后台返回的图片url
    //private MicroVideo microVideo;
    private MixtureData mixtureData;

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_picture);
        ButterKnife.bind(this);
        //获取上一个activity传递过来的 图片 数据
        Intent intent = getIntent();
        //mixtureData = (MixtureData) intent.getSerializableExtra("MixtureData");
        mixtureData = MyApplication.mixtureData;
        if (mixtureData != null) {
            //取出图片数据放入集合中
            initData();

            initViewPager();
            initView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.mixtureData = null;
    }

    private void initView(){
        if (mixtureData != null) {
            //加载头像
            Glide.with(this).load(mixtureData.userHeadImg)
                    .apply(MyApplication.requestOptions)
                    .into(headImg);
            //是否关注
            if (mixtureData.isFollowed == 1) {
                attention.setVisibility(View.INVISIBLE);
            }else {
                attention.setVisibility(View.VISIBLE);
            }

            //点赞
            if (mixtureData.isPraise == 1){
                textLike.setSelected(true);
            }else {
                textLike.setSelected(false);
            }

            //点赞数
            textLike.setText(Utils.formatPlayTimes2(mixtureData.praisedNum));
            //用户名
            textName.setText(mixtureData.userName);
            textAbstrack.setText(mixtureData.targetTitle);
            //添加浏览历史记录，更新浏览量/播放量 1：咨询文章，2：党建微视
            RetrofitUtil.browseMicro(mixtureData.targetId);
        }

    }

    private void initViewPager(){
        PlayPictureAdapter adapter = new PlayPictureAdapter(this,imgUrlList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        titleBar.setText(1 + "/" + imgUrlList.size());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                int totalPage = imgUrlList.size();
                int currentPage = i + 1;
                titleBar.setText(currentPage + "/" + totalPage);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @OnClick(R.id.imagebutton_back)
    public void onClickBack(){
        finish();
    }

    //点击头像，跳转到详情
    @OnClick(R.id.circle_img_head)
    public void headOnclick(){
        if (mixtureData == null){
            return;
        }
        if (userType == 3){
            MyApplication.remindVisitor(this);
        }else {
            //if (myUserId != mixtureData.userId) {
            Intent intent = new Intent(this, UserDetailsActivity.class);
            intent.putExtra("userId", mixtureData.userId);
            intent.putExtra("userName", mixtureData.userName);
            startActivity(intent);
            // }

        }
    }

    //分享
    @OnClick(R.id.imagebutton_share)
    public void onClickShare(){
        if (mixtureData == null){
            return;
        }
        if (userType == 1 || userType == 2) {
            String targetTitle = mixtureData.targetTitle;
            if (targetTitle == null || targetTitle == ""){
                targetTitle = getResources().getString(R.string.mic_defaul_title);
            }
            //查看的当前图片的url，用于分享
            String currentImgUrl = imgUrlList.get(viewPager.getCurrentItem());
            JshareUtil.showBroadView(this,targetTitle,"",currentImgUrl,
                    2,mixtureData.targetId);

        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    //关注
    @OnClick(R.id.imageView_attention)
    public void onClickAttention(){
        if (mixtureData == null){
            return;
        }
        if (userType == 1 || userType == 2) {
           /* if (attention.isSelected()) {
                attention.setSelected(false);
                RetrofitUtil.delFocus(mixtureData.userId);
            } else {
                attention.setSelected(true);
                RetrofitUtil.addFocus(mixtureData.userId, mixtureData.userName, mixtureData.userHeadImg);
            }*/
            RetrofitUtil.addFocus(mixtureData.userId, mixtureData.userName, mixtureData.userHeadImg,attention);
            mixtureData.isFollowed = 1;

        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    //点赞
    @OnClick(R.id.textView_like)
    public void onClickLike(){
        if (userType == 1 || userType == 2) {
            if (mixtureData != null) {
                //取消点赞
                if (textLike.isSelected()) {
                    textLike.setSelected(false);
                    if (mixtureData.isPraise == 0) {
                        textLike.setText(Utils.formatPlayTimes2(mixtureData.praisedNum));
                        RetrofitUtil.cancelPraise(mixtureData.targetId, mixtureData.userId);
                    } else {
                        //点赞数减1
                        textLike.setText(Utils.formatPlayTimes2(mixtureData.praisedNum - 1));
                        RetrofitUtil.cancelPraise(mixtureData.targetId, mixtureData.userId);
                    }
                    mixtureData.isPraise = 0;
                    mixtureData.praisedNum += -1;
                } else {
                    //点赞
                    textLike.setSelected(true);
                    if (mixtureData.isPraise == 1) {
                        textLike.setText(Utils.formatPlayTimes2(mixtureData.praisedNum));
                        RetrofitUtil.microPraise(mixtureData.targetId, mixtureData.userId);
                    } else {
                        //点赞数加1
                        textLike.setText(Utils.formatPlayTimes2(mixtureData.praisedNum + 1));
                        RetrofitUtil.microPraise(mixtureData.targetId, mixtureData.userId);
                    }
                    mixtureData.isPraise = 1;
                    mixtureData.praisedNum += 1;
                }
            }
        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    /**
     * 取出 图片数据 里的url
     */
    private void initData(){
        String imgUrl = mixtureData.fileUrl;
        Log.i(TAG, "initData: visionFileUrl : " + imgUrl);
        if (imgUrl != null) {
            if (imgUrl.contains(",")) {
                String split[] = imgUrl.split(",");
                Log.i(TAG, "initData: split size : " + split.length);
                for (int i = 0; i < split.length; i++) {
                    imgUrlList.add(split[i]);
                }
            } else {
                imgUrlList.add(imgUrl);
            }
        }

    }
}
