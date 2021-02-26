package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayLiveVideoDetailActivity;
import com.typartybuilding.activity.PlayVideoDetailActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.utils.GlideCacheUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class LearnTimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "LearnTimeAdapter";

    private List<ArticleBanner> bannerList = new ArrayList<>();
    private Context mContext;
    private int flag;         // 0, 直播视频， 1 普通视频


    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindViews({R.id.imageView1, R.id.imageView2})
        ImageView[] imageView;
        @BindViews({R.id.textView1_play_duration, R.id.textView2_play_duration})
        TextView[] playDurations;
        @BindViews({R.id.textView1_headline1, R.id.textView2_headline1})
        TextView [] headLine1;
        @BindViews({R.id.textView1_headline2, R.id.textView2_headline2})
        TextView [] headLine2;

        private View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    public LearnTimeAdapter(List<ArticleBanner> bannerList, Context mContext, int flag) {
        this.bannerList = bannerList;
        this.mContext = mContext;
        this.flag = flag;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_learn_time,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ViewHolder holder = ( ViewHolder) viewHolder;
        Log.i(TAG, "onBindViewHolder: holder " + i + ": "+ holder);
        int index = i * 2;    //每个item2个数据，index表示每个item的 第一条数据的下标

        for (int j = 0; j < 2; j++) {
            if (index < bannerList.size()) {
                ArticleBanner banner = bannerList.get(index);
                String imgUrl = banner.videoCover;

                //加载封面图片
                Glide.with(mContext).asDrawable().load(imgUrl)
                        .apply(MyApplication.requestOptionsVideo2)  //url为空或异常显示默认图片
                        .into(holder.imageView[j]);

                holder.headLine1[j].setText(banner.articleTitle);
                holder.headLine2[j].setText(banner.articleProfile);
                holder.playDurations[j].setText(Utils.formatTime(banner.videoDuration));

            } else {

            }
            index++;
        }

        holder.imageView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    skipLiveVideo(bannerList.get(i * 2));
                } else {
                    skipPlayVideo(bannerList.get(i * 2));
                }
            }
        });

        holder.imageView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    if ((i*2 + 1) < bannerList.size()) {
                        skipLiveVideo(bannerList.get(i * 2 + 1));
                    }
                } else {
                    if ((i*2 + 1) < bannerList.size()) {
                        skipPlayVideo(bannerList.get(i * 2 + 1));
                    }
                }
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        ViewHolder viewHolder = ( ViewHolder) holder;
        ImageView [] imageViews = viewHolder.imageView;
        Log.i(TAG, "onViewRecycled: imageViews : " + imageViews);
        if (imageViews != null){
            Log.i(TAG, "onViewRecycled: ");
            Glide.with(mContext).clear(imageViews[0]);
            Glide.with(mContext).clear(imageViews[1]);
        }
    }

    private void skipLiveVideo(ArticleBanner banner){
        Intent intentAc = new Intent(mContext, PlayLiveVideoDetailActivity.class);
        intentAc.putExtra("ArticleBanner",banner);
        mContext.startActivity(intentAc);
    }


    private void skipPlayVideo(ArticleBanner banner){
        Intent intentAc = new Intent(mContext,PlayVideoDetailActivity.class);
        //intentAc.putExtra("ArticleBanner",banner);
        MyApplication.articleBanner = banner;
        mContext.startActivity(intentAc);
    }


    @Override
    public int getItemCount() {
        int size = bannerList.size();
        if (size == 0){
            return 0;
        }else {
            int n = size % 2;
            if (n == 0) {
                return size / 2  ;
            } else {
                return (size / 2 + 1) ;
            }
        }

    }
}
