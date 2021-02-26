package com.typartybuilding.adapter.viewPagerAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class PlayPictureAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> imgUrlList = new ArrayList<>();  //后台返回图片url
    private List<View> viewList = new ArrayList<>(); // 声明一个引导页的视图队列

    public PlayPictureAdapter(Context mContext, List<String> imgUrlList) {
        this.mContext = mContext;
        this.imgUrlList = imgUrlList;

        for (int i = 0; i < imgUrlList.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_viewpager_play_picture, null);
            PhotoView photoView = view.findViewById(R.id.photoView);

            Glide.with(mContext).load(imgUrlList.get(i))
                    .apply(MyApplication.requestOptions)
                    .into(photoView);

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
