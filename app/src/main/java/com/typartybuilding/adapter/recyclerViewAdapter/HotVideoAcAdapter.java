package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayVideoDetailActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HotVideoAcAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "HotVideoAcAdapter";

    private List<ArticleBanner> videoDataList = new ArrayList<>();
    private Context mContext;


    private static final int TYPE_ITEM_NORMAL = 0;
    private static final int TYPE_ITEM_FOOTER = 1;

    private ViewHolderFooter mHolder;

    static class ViewHolderFooter extends RecyclerView.ViewHolder{

        @BindView(R.id.item_load_tv)
        TextView textHint;
        @BindView(R.id.item_load_pb)
        ProgressBar progressBar;

        public ViewHolderFooter(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.round_imageView)
        RoundImageView roundImg;           //带圆角的图片
        @BindView(R.id.textView_play_duration)
        TextView playDuration;             //视频 时长
        @BindView(R.id.textView_headline)
        TextView headLine;                 //标题
        @BindView(R.id.textView_play_times)
        TextView playTimes;                //播放次数
        @BindView(R.id.textView_date)
        TextView textDate;                 //日期

        private View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    public HotVideoAcAdapter(List<ArticleBanner> videoDataList, Context mContext) {
        this.videoDataList = videoDataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM_NORMAL) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_hot_video_ac,
                    viewGroup, false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_load_more2,
                    viewGroup, false);

            if (mHolder == null) {
                mHolder = new ViewHolderFooter(view);
            }
            return mHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        if (type == TYPE_ITEM_NORMAL) {
            ViewHolder holder = (ViewHolder)viewHolder;
            ArticleBanner videoData = videoDataList.get(i);
            //加载封面图片

            String imgUrl = videoData.videoCover;

            Glide.with(mContext).load(imgUrl)
                    .apply(MyApplication.requestOptions43)  //url为空或异常显示默认头像
                    .into(holder.roundImg);
            //开启线程计算视频时长
            //Utils.calculateDuration(holder.playDuration, videoData.videoUrl);

            holder.playDuration.setText(Utils.formatTime(videoData.videoDuration));/*Utils.getDuration(videoData.videoUrl)*/
            holder.headLine.setText(videoData.articleTitle);
            holder.playTimes.setText(Utils.formatPlayTimes(videoData.browseTimes) + "次播放");
            holder.textDate.setText(Utils.formatDate(videoData.publishDate));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentAc = new Intent(mContext, PlayVideoDetailActivity.class);
                    //intentAc.putExtra("ArticleBanner", videoData);
                    MyApplication.articleBanner = videoData;
                    mContext.startActivity(intentAc);
                }
            });
        }

    }

    public void setTypeItemFooter(){

       /* mHolder.progressBar.setVisibility(View.INVISIBLE);
        mHolder.textHint.setText("没有更多了");*/
        if (mHolder != null){
            if (mHolder.progressBar != null){
                mHolder.progressBar.setVisibility(View.INVISIBLE);
            }
            if (mHolder.textHint != null){
                mHolder.textHint.setText("没有更多了");
            }
        }

    }

    public void setTypeItemFooterStart(){

        if (mHolder != null){
            if (mHolder.progressBar != null){
                mHolder.progressBar.setVisibility(View.VISIBLE);
            }
            if (mHolder.textHint != null){
                mHolder.textHint.setText("正在加载...");
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
       /* if(position + 1 < getItemCount()) return TYPE_ITEM_NORMAL;
        else return TYPE_ITEM_FOOTER;*/
       return TYPE_ITEM_NORMAL;

    }

    @Override
    public int getItemCount() {
        if (videoDataList.size() == 0){
            return 0;
        }else {
            return videoDataList.size();
        }
    }
}
