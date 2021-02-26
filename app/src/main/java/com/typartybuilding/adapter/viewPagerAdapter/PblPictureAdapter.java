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
import com.typartybuilding.R;

import java.util.ArrayList;
import java.util.List;

public class PblPictureAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> imgUrlList = new ArrayList<>();  //后台返回图片url
    private List<View> viewList = new ArrayList<>(); // 声明一个引导页的视图队列

    public PblPictureAdapter(Context mContext, List<String> imgUrlList) {
        this.mContext = mContext;
        this.imgUrlList = imgUrlList;

        for (int i = 0; i < imgUrlList.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_viewpager_pb_library, null);
            ImageView imageView = view.findViewById(R.id.imageView_cho_vp);

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.shape_default_pic)
                    .fallback(R.drawable.shape_default_pic)
                    .error(R.drawable.shape_default_pic);
            Glide.with(mContext).load(imgUrlList.get(i)).apply(requestOptions).into(imageView);

            viewList.add(view);
        }

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        position %= viewList.size();
        if (position < 0){
            position = viewList.size() + position;
        }
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        View view = viewList.get(position);
        ViewParent viewParent = view.getParent();
        if (viewParent != null){
            ViewGroup viewGroup = (ViewGroup)viewParent;
            viewGroup.removeView(view);
        }

        //设置点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //container.removeView(viewList.get(position));
    }
}
