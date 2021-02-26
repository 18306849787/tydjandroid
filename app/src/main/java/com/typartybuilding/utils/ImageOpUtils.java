package com.typartybuilding.utils;

import android.app.Application;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-30
 * @describe
 */
public class ImageOpUtils {

    public static RequestOptions getYuanRequestOptions(int dp){
       return new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.shape_default_pic) //预加载图片
                .error(R.mipmap.default_pic_16_9) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
                .transform(new CornerTransform(MyApplication.getContext(), DisplayUtils.dip2px(dp))); //圆角
    }
}
