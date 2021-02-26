package com.typartybuilding.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
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
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayPictureActivity extends BaseActivity {

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
    private MicroVideo microVideo;



    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_picture);
        ButterKnife.bind(this);
        //获取上一个activity传递过来的 图片 数据
        Intent intent = getIntent();
        //microVideo = (MicroVideo)intent.getSerializableExtra("MicroVideo");
        microVideo = MyApplication.microVideo;
        if (microVideo != null) {
            //取出图片数据放入集合中
            initData();

            initViewPager();
            initView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.microVideo = null;
    }

    private void initView(){
        if (microVideo != null) {
            //加载头像
            Glide.with(this).load(microVideo.userHeadImg)
                    .apply(MyApplication.requestOptions)
                    .into(headImg);
            //是否关注
            if (microVideo.isFollowed == 1) {
                attention.setVisibility(View.INVISIBLE);
            }else {
                attention.setVisibility(View.VISIBLE);
            }
            //点赞
            if (microVideo.isPraise == 1){
                textLike.setSelected(true);
            }else {
                textLike.setSelected(false);
            }
            //点赞数
            textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum));
            //用户名
            textName.setText(microVideo.userName);
            textAbstrack.setText(microVideo.visionTitle);
            //添加浏览历史记录，更新浏览量/播放量 1：咨询文章，2：党建微视
            RetrofitUtil.browseMicro(microVideo.visionId);
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
        if (microVideo == null){
            return;
        }
        if (userType == 3){
            MyApplication.remindVisitor(this);
        }else {
            //if (myUserId != mixtureData.userId) {
            Intent intent = new Intent(this, UserDetailsActivity.class);
            intent.putExtra("userId", microVideo.userId);
            intent.putExtra("userName", microVideo.userName);
            startActivity(intent);
            // }

        }
    }

    //分享
    @OnClick(R.id.imagebutton_share)
    public void onClickShare(){
        if (microVideo == null){
            return;
        }
        if (userType == 1 || userType == 2) {
            String visionTitle = microVideo.visionTitle;
            if (visionTitle == null || visionTitle == ""){
                visionTitle = getResources().getString(R.string.mic_defaul_title);
            }
            //查看的当前图片的url，用于分享
            String currentImgUrl = imgUrlList.get(viewPager.getCurrentItem());
            JshareUtil.showBroadView(this,visionTitle,"",currentImgUrl,
                    2,microVideo.visionId);

        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    //关注
    @OnClick(R.id.imageView_attention)
    public void onClickAttention(){
        if (microVideo == null){
            return;
        }
        if (userType == 1 || userType == 2) {
           /* if (attention.isSelected()) {
                attention.setSelected(false);
                RetrofitUtil.delFocus(microVideo.userId);
            } else {
                attention.setSelected(true);
                RetrofitUtil.addFocus(microVideo.userId, microVideo.userName, microVideo.userHeadImg);
            }*/

            RetrofitUtil.addFocus(microVideo.userId, microVideo.userName, microVideo.userHeadImg,attention);
            microVideo.isFollowed = 1;
        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    //点赞
    @OnClick(R.id.textView_like)
    public void onClickLike(){
        if (userType == 1 || userType == 2) {
            if (microVideo != null) {
                //取消点赞
                if (textLike.isSelected()) {
                    textLike.setSelected(false);
                    if (microVideo.isPraise == 0) {
                        textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum));
                        RetrofitUtil.cancelPraise(microVideo.visionId, microVideo.userId);
                    } else {
                        //点赞数减1
                        textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum - 1));
                        RetrofitUtil.cancelPraise(microVideo.visionId, microVideo.userId);
                    }
                    microVideo.isPraise = 0;
                    microVideo.visionPraisedNum += -1;
                } else {
                    //点赞
                    textLike.setSelected(true);
                    if (microVideo.isPraise == 1) {
                        textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum));
                        RetrofitUtil.microPraise(microVideo.visionId, microVideo.userId);
                    } else {
                        //点赞数加1
                        textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum + 1));
                        RetrofitUtil.microPraise(microVideo.visionId, microVideo.userId);
                    }
                    microVideo.isPraise = 1;
                    microVideo.visionPraisedNum += 1;
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
        String imgUrl = microVideo.visionFileUrl;
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
