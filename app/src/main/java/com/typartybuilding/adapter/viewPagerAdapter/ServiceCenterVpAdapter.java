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
import com.typartybuilding.activity.pbmap.ServiceCenterActivity;
import com.typartybuilding.base.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 *  党群服务中心 ， 点击图片放大，可翻页
 */
public class ServiceCenterVpAdapter extends PagerAdapter {

    private Context mContext;
    private ServiceCenterActivity activity;
    private List<String> imgUrlList = new ArrayList<>();  //后台返回图片url
    private List<View> viewList = new ArrayList<>(); // 声明一个引导页的视图队列

    public ServiceCenterVpAdapter(Context mContext, List<String> imgUrlList) {
        this.mContext = mContext;
        this.imgUrlList = imgUrlList;
        this.activity = (ServiceCenterActivity)mContext;

        for (int i = 0; i < imgUrlList.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_viewpager_play_picture, null);
            PhotoView photoView = view.findViewById(R.id.photoView);

            Glide.with(mContext).load(imgUrlList.get(i))
                    .apply(MyApplication.requestOptions)
                    .into(photoView);

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity.popupWindow2.isShowing()){
                        activity.popupWindow2.dismiss();
                    }
                }
            });

            viewList.add(view);
        }

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(viewList.get(position));
    }
}
