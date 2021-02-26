package com.typartybuilding.view;

import android.content.Context;
import android.opengl.Visibility;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.adapter.viewPagerAdapter.PictureAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;

import java.util.ArrayList;
import java.util.List;


/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-24
 * @describe
 */
public class HomeViewPager extends FrameLayout {

    WrapContentHeightViewPager viewPager;
    TextView textView;
    RadioGroup radioGroup;

    private Handler mHandler = new Handler();  //用于图片轮播
    private PictureAdapter adapter;

    private List<ArticleBanner> bannerList = new ArrayList<>();  //轮播数据
    public HomeViewPager(Context context) {
        super(context);
        initView(context);
    }

    public HomeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HomeViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.fg_choiceness_fragment_picture_new,this,true);
        radioGroup = findViewById(R.id.radiogroup_fg_pic);
        textView = findViewById(R.id.textview_fg_pic);
        viewPager = findViewById(R.id.viewpager_fg_pic);

    }

    public void setData(List<ArticleBanner> list,boolean isShowTitle){
        bannerList.clear();
        bannerList.addAll(list);
        textView.setVisibility(isShowTitle?VISIBLE:INVISIBLE);
        initRadioGroup();
        initViewPager();
    }

    private void initRadioGroup(){
        if (radioGroup != null){
            radioGroup.removeAllViews();
        }
        for (int i = 0; i < bannerList.size(); i++){
            RadioButton radioButton = new RadioButton(MyApplication.getContext());
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT
                    ,RadioGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setButtonDrawable(R.drawable.selector_radio_button_cho);
            radioButton.setPadding(10,0,10,0);
            if (radioGroup != null) {
                radioGroup.addView(radioButton);
            }
        }

    }

    private void initViewPager(){
        if (adapter != null) {
            adapter = null;
        }
        adapter = new PictureAdapter(getContext(), bannerList);
        if (viewPager != null) {
            if (bannerList != null && bannerList.size() >= 1&&textView.getVisibility()==VISIBLE){
                textView.setText(bannerList.get(0).articleTitle);
            }
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);
            //图片2张以上，才轮播
            if (bannerList.size() > 1) {
                startSlideShow();
            }
            ((RadioButton) radioGroup.getChildAt(0)).setButtonDrawable(R.drawable.shape_dot_cho_vp_1);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    i %= bannerList.size();
                    //Log.i(TAG, "onPageSelected: bannerList.get(i).articleTitle : " + bannerList.get(i).articleTitle);
                    if (textView.getVisibility() == View.VISIBLE){
                        textView.setText(bannerList.get(i).articleTitle);
                    }
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);

                    radioButton.setButtonDrawable(R.drawable.shape_dot_cho_vp_1);
                    for (int j = 0; j < bannerList.size(); j++) {
                        if (i != j) {
                            RadioButton radioButton1 = (RadioButton) radioGroup.getChildAt(j);
                            radioButton1.setButtonDrawable(R.drawable.shape_dot_cho_vp_0);
                        }
                    }
                    //radioButton.setChecked(true);
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }
    }

    //开始轮播图片
    private void startSlideShow() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 3000);
    }


    /**
     * 开始轮播图
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int item = viewPager.getCurrentItem();
            viewPager.setCurrentItem(item + 1);
            startSlideShow();
        }

    };
}
