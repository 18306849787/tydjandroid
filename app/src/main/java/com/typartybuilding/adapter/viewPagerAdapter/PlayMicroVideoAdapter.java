package com.typartybuilding.adapter.viewPagerAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;

import java.util.ArrayList;
import java.util.List;

public class PlayMicroVideoAdapter extends PagerAdapter {

    private Context mContext;
    private List<ChoicenessData.MicroVision> dataList = new ArrayList<>();  //后台返回图片url
    //private List<View> viewList = new ArrayList<>(); // 声明一个引导页的视图队列


    public PlayMicroVideoAdapter(Context mContext, List<ChoicenessData.MicroVision> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;


    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_play_micro_video, null);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
