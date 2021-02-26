package com.typartybuilding.adapter.viewPagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.typartybuilding.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.activity.NewsDetailActivity;
import com.typartybuilding.activity.PlayPictureActivity;
import com.typartybuilding.activity.second.PlayVideoAct;
import com.typartybuilding.activity.second.TextDetailAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.HomeFraBannerBean;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;
import com.typartybuilding.utils.CornerTransform;
import com.typartybuilding.utils.DisplayUtils;
import com.typartybuilding.utils.GlideRoundTransform;

import java.util.ArrayList;
import java.util.List;

public class PictureAdapter extends PagerAdapter {

    private String TAG = "PictureAdapter";

    private Context mContext;
    private int currentPos;
    private List<ArticleBanner> bannerList = new ArrayList<>();  //轮播数据
    private List<View> viewList = new ArrayList<>(); // 声明一个引导页的视图队列
    RequestOptions options ;
    CornerTransform transformation ;
    public PictureAdapter(Context mContext, List<ArticleBanner> bannerList) {
        this.mContext = mContext;
        this.bannerList = bannerList;
//        transformation = new CornerTransform(mContext, DisplayUtils.dip2px(10));
        options  = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.shape_default_pic) //预加载图片
                .error(R.mipmap.default_pic_16_9) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE); //缓存
//                .transform(transformation); //圆角
        for (int i = 0; i < bannerList.size(); i++) {
            //Log.i(TAG, "PictureAdapter: url " + i + ": " + bannerList.get(i).picUrls);
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_viewpager_choiceness_new, null);
            ImageView imageView = view.findViewById(R.id.imageView_cho_vp_new);

            String imgUrl = null;
            if (bannerList.get(i).urlType ==1&&bannerList.get(i).picUrls != null) {
                /*if (bannerList.get(i).picUrls.contains(",")) {
                    String[] splits = bannerList.get(i).picUrls.split(",");
                    imgUrl = splits[0];
                } else {*/
                    imgUrl = bannerList.get(i).picUrls;
                //}
            }else if (bannerList.get(i).urlType ==2){
                imgUrl = bannerList.get(i).videoCover;
            }
            //Log.i(TAG, "PictureAdapter: imgurl : " + imgUrl);
            //Log.i(TAG, "PictureAdapter: 详情url ： " + bannerList.get(i).articleDetailUrl);
//            Glide.with(mContext).load(imgUrl).apply(MyApplication.requestOptions).into(imageView);
            Glide.with(mContext).load(imgUrl).apply(options).into(imageView);
//            Glide.with(mContext).load(imgUrl).apply(options).into(imageView);
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

        if (viewList.size() != 0) {
            //Log.i(TAG, "instantiateItem: 换算前position : " + position);
            //position += 1;
            position %= viewList.size();
            if (position < 0) {
                position = viewList.size() + position;
            }
            //Log.i(TAG, "instantiateItem: 换算后position ： " + position);
            //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            View view = viewList.get(position);
            ViewParent viewParent = view.getParent();
            if (viewParent != null) {
                ViewGroup viewGroup = (ViewGroup) viewParent;
                viewGroup.removeView(view);
            }
            //当前图片
            currentPos = position;
            //设置点击事件
            view.setOnClickListener(new View.OnClickListener() {
                int  currentPos1 = currentPos ;

                @Override
                public void onClick(View v) {
                    ArticleBanner banner =  bannerList.get(currentPos1);
                    if (banner.urlType ==1){
                        ARouter.getInstance().build(TextDetailAct.PATH)
                                .withString(TextDetailAct.URL, banner.articleDetailUrl)
                                .withInt(TextDetailAct.ARTICLE_TYPE, 1)
                                .withInt(TextDetailAct.ARTICLE_ID, banner.articleId)
                                .withInt(TextDetailAct.URL_TYPE, 1)
                                .navigation(mContext);
                    }else {
                        ARouter.getInstance().build(PlayVideoAct.PTAH)
                                .withInt(PlayVideoAct.ARTICLE_ID, banner.articleId)
                                .navigation();
                    }

//                    Intent intent = new Intent(mContext, NewsDetailActivity.class);
//                    //intent.putExtra("ArticleBanner",bannerList.get(currentPos));
//                    MyApplication.articleBanner = bannerList.get(currentPos1);
//                    mContext.startActivity(intent);
//                    Log.i(TAG, "onClick: currentPos : " + currentPos1);
                }
            });

            container.addView(viewList.get(position));
        }
        return viewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //container.removeView(viewList.get(position));
    }
}
