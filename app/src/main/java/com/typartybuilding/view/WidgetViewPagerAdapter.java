package com.typartybuilding.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.utils.CornerTransform;
import com.typartybuilding.utils.DisplayUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2019-12-03
 * @describe
 */
public class WidgetViewPagerAdapter extends PagerAdapter {
    private List<ArticleBanner> mList;
    private OnClickListen mOnClickListen;
    private LinkedList<ImageView> mListViews = new LinkedList<>();
    private Boolean isCircleImage;
    private RequestOptions options;
    private CornerTransform transformation;
    private Context context;

    public WidgetViewPagerAdapter(Context context, List<ArticleBanner> list, OnClickListen clickListen) {
        mList = list;
        mOnClickListen = clickListen;
        this.context = context;
        transformation = new CornerTransform(context, DisplayUtils.dip2px(0));
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.shape_default_pic) //预加载图片
                .error(R.mipmap.default_pic_16_9) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
                .transform(transformation); //圆角
    }


    public int getRealCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size() * 1000;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView itemView = mListViews.poll();
        if (itemView == null) {
            itemView = new ImageView(context);
            itemView.setImageResource(R.mipmap.ic_load_img);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            itemView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        String imgUrl = null;
        ArticleBanner articleBanner = mList.get(position % mList.size());
        if (articleBanner.urlType == 1 && articleBanner.picUrls != null) {
            imgUrl = articleBanner.picUrls;
            //}
        } else if (articleBanner.urlType == 2) {
            imgUrl = articleBanner.videoCover;
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListen != null) {
                    mOnClickListen.onClick(position % mList.size());
                }
            }
        });
        Glide.with(context).load(imgUrl).apply(options).into(itemView);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mListViews.addFirst((ImageView) object);
    }

    public void setCircleImage(boolean isCircle) {
        isCircleImage = isCircle;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    public interface OnClickListen {
        void onClick(int position);
    }
}