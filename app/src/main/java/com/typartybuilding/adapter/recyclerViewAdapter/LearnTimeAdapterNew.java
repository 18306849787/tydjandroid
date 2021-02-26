package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayLiveVideoDetailActivity;
import com.typartybuilding.activity.PlayVideoDetailActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fgLearningTime.FragmentLearnTimeNew;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class LearnTimeAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "LearnTimeAdapterNew";

    private List<ArticleBanner> bannerList = new ArrayList<>();
    private Context mContext;
    private FragmentLearnTimeNew fragment;
    private int flag;         // 8直播视频， 5学习新思想和7时代先锋
    private boolean isLoad = false;   //fragment是否已经加载

    public LearnTimeAdapterNew(List<ArticleBanner> bannerList, FragmentLearnTimeNew fragment) {
        this.bannerList = bannerList;
        this.fragment = fragment;
        this.mContext = fragment.getActivity();
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    //学习新思想和时代先锋 置顶视频 布局
    static class ViewHolderTop extends RecyclerView.ViewHolder{
        @BindView(R.id.frameLayout)
        FrameLayout frameLayout;
        @BindView(R.id.constraintLayout)
        ConstraintLayout constraintLayout;

        private View view;

        public ViewHolderTop(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    //直播置顶视频 布局
    static class ViewHolderLiveTop extends RecyclerView.ViewHolder{
        @BindView(R.id.frameLayout8)
        FrameLayout frameLayout;
        @BindView(R.id.constraintLayout8)
        ConstraintLayout constraintLayout8;

        private View view;

        public ViewHolderLiveTop(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

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


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == 0){
            if (flag == 8){
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_learn_time_8,
                        viewGroup, false);
                return new ViewHolderLiveTop(view);
            }else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_learn_time_0,
                        viewGroup, false);
                return new ViewHolderTop(view);
            }
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_learn_time,
                    viewGroup, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.i(TAG, "onBindViewHolder: i : " + i);
        if (i == 0){
            if (!isLoad) {
                setItemViewHolderTop(viewHolder, i);
                isLoad = true;
            }
        }else {
            setItemViewHoldr(viewHolder, i);
        }

    }

    private void setItemViewHolderTop(RecyclerView.ViewHolder viewHolder, int i){
        if (flag == 8){
            ViewHolderLiveTop holder = (ViewHolderLiveTop)viewHolder;
            fragment.loadFragment(holder.constraintLayout8.getId(), bannerList.get(i));
        }else {
            ViewHolderTop holder = (ViewHolderTop) viewHolder;
            fragment.loadFragment(holder.constraintLayout.getId(), bannerList.get(i));
        }
    }

    private void setItemViewHoldr(RecyclerView.ViewHolder viewHolder, int i){
        ViewHolder holder = ( ViewHolder) viewHolder;
        Log.i(TAG, "onBindViewHolder: holder " + i + ": "+ holder);
        //减 1 ，因为0位置是 置顶视频；加 1，因为bannerList的第一条数据给了置顶视频
        int index = (i-1) * 2 + 1;    //每个item2个数据，index表示每个item的 第一条数据的下标
        int pos = index;              //记录item 的第一条数据的 下标
        for (int j = 0; j < 2; j++) {
            if (index < bannerList.size()) {
                ArticleBanner banner = bannerList.get(index);
                String imgUrl = banner.videoCover;
                //避免，item复用导致的不可见
                holder.imageView[j].setVisibility(View.VISIBLE);
                holder.headLine1[j].setVisibility(View.VISIBLE);
                holder.headLine2[j].setVisibility(View.VISIBLE);
                //加载封面图片
                Glide.with(mContext).asDrawable().load(imgUrl)
                        .apply(MyApplication.requestOptionsVideo2)  //url为空或异常显示默认图片
                        .into(holder.imageView[j]);

                holder.headLine1[j].setText(banner.articleTitle);
                holder.headLine2[j].setText(banner.articleProfile);
                holder.playDurations[j].setText(Utils.formatTime(banner.videoDuration));

            } else {
                if (j == 1){
                    //避免，item复用导致的，有封面没数据
                    holder.imageView[1].setVisibility(View.INVISIBLE);
                    holder.headLine1[1].setVisibility(View.INVISIBLE);
                    holder.headLine2[1].setVisibility(View.INVISIBLE);
                }
                break;
            }
            index++;
        }

        holder.imageView[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 8) {
                    skipLiveVideo(bannerList.get(pos));
                } else {
                    skipPlayVideo(bannerList.get(pos));
                }
            }
        });

        holder.imageView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 8) {
                    if ((pos + 1) < bannerList.size()) {
                        skipLiveVideo(bannerList.get(pos + 1));
                    }
                } else {
                    if ((pos + 1) < bannerList.size()) {
                        skipPlayVideo(bannerList.get(pos + 1));
                    }
                }
            }
        });
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
       /* ViewHolder viewHolder = ( ViewHolder) holder;
        ImageView [] imageViews = viewHolder.imageView;
        Log.i(TAG, "onViewRecycled: imageViews : " + imageViews);
        if (imageViews != null){e
            Log.i(TAG, "onViewRecycled: ");
            Glide.with(mContext).clear(imageViews[0]);
            Glide.with(mContext).clear(imageViews[1]);
        }*/
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
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        int size = bannerList.size();
        if (size == 0){
            return 0;
        }else {
            int n = size % 2;
            if (n == 0) {
                return size / 2 + 1 ;    //双数时 + 1，因为有个置顶视频
            } else {
                return (size / 2 + 1) ;  //单数时 + 1，单出的那个 刚好作为置顶视频
            }
        }

    }
}
