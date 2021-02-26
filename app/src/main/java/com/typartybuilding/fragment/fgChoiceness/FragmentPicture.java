package com.typartybuilding.fragment.fgChoiceness;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.adapter.viewPagerAdapter.PictureAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;
import com.typartybuilding.view.WrapContentHeightViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * 精选页面 图片滑动
 */
public class FragmentPicture extends BaseFragment {

    private String TAG = "FragmentPicture";

    @BindView(R.id.viewpager_fg_pic)
    WrapContentHeightViewPager viewPager;
    @BindView(R.id.textview_fg_pic)
    TextView textView;
    @BindView(R.id.radiogroup_fg_pic)
    RadioGroup radioGroup;

    private Handler mHandler = new Handler();  //用于图片轮播
    private PictureAdapter adapter;

    private List<ArticleBanner> bannerList = new ArrayList<>();  //轮播数据
    private boolean isDestroy;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Log.i(TAG, "initViews: ");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
        Log.i(TAG, "onDestroyView: ");
        mHandler.removeCallbacks(mRunnable);
        
    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_choiceness_fragment_picture;
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

    //开始轮播图片
    private void startSlideShow() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 3000);
    }


    private void initViewPager(){
        if (adapter != null) {
            adapter = null;
        }
        adapter = new PictureAdapter(getActivity(), bannerList);
        if (viewPager != null) {
            if (bannerList != null && bannerList.size() >= 1){
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
                    textView.setText(bannerList.get(i).articleTitle);
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

    /**
     * 在精选页面拿到数据后，调该方法，更新页面
     * @param articleBannerList
     */
    public void loadData(List<ArticleBanner> articleBannerList){
        if (!isDestroy) {
            if (articleBannerList.size() > 0){
                if (bannerList.size() > 0) {
                    bannerList.clear();
                }
                for (int i = 0; i < articleBannerList.size(); i++) {
                    bannerList.add(articleBannerList.get(i));

                }

                initRadioGroup();
                initViewPager();
                //adapter.notifyDataSetChanged();
            }
        }
    }


}
