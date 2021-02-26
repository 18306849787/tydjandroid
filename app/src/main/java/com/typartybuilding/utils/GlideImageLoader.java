package com.typartybuilding.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.bean.HomeFraBannerBean;
import com.typartybuilding.gsondata.ArticleBanner;
import com.youth.banner.loader.ImageLoader;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-03
 * @describe
 */
public class GlideImageLoader extends ImageLoader {
    RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.shape_default_pic) //预加载图片
            .error(R.mipmap.default_pic_16_9) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
            .transform(new GlideRoundTransform(6)); //圆角

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if (path instanceof HomeFraBannerBean.ArticleBannerBean){
            Glide.with(context).load(((HomeFraBannerBean.ArticleBannerBean) path).getPicUrls()).apply(options).into(imageView);
//            Glide.with(context).load(((HomeFraBannerBean.ArticleBannerBean) path).getPicUrls()).into(imageView);
        }else if (path  instanceof ArticleBanner){
//            Glide.with(context).load(((ArticleBanner) path).videoCover).into(imageView);
            Glide.with(context).load(((ArticleBanner) path).videoCover).apply(options).into(imageView);

        }




    }

}
