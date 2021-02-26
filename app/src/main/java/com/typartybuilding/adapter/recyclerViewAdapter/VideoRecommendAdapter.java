package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayLiveVideoDetailActivity;
import com.typartybuilding.activity.PlayVideoDetailActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoRecommendAdapter extends RecyclerView.Adapter<VideoRecommendAdapter.ViewHolder> {

    private List<ArticleBanner> videoDataList = new ArrayList<>();
    private Context mComtext;
    private int flag;    //0 表示直播视频， 1 表示普通视频

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imageView_video_pic)
        ImageView imageView;           //视频图片
        @BindView(R.id.textView_play_duration)
        TextView textDuration;         //视频时长
        @BindView(R.id.textView_headline)
        TextView headLine;                 //标题
        @BindView(R.id.textView_subhead)
        TextView subHead;                 //副标题

        private View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    public VideoRecommendAdapter(List<ArticleBanner> videoDataList, Context mComtext, int flag) {
        this.videoDataList = videoDataList;
        this.mComtext = mComtext;
        this.flag = flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.item_recyclerview_play_video_detail_ac,
                viewGroup,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
       ArticleBanner videoData = videoDataList.get(i);
        //加载图片
        String imgUrl = videoData.videoCover;

        Glide.with(mComtext).asDrawable().load(imgUrl)
                .apply(MyApplication.requestOptionsVideo2)  //url为空或异常显示默认头像
                .into(viewHolder.imageView);

       viewHolder.headLine.setText(videoData.articleTitle);
       viewHolder.subHead.setText(videoData.articleProfile);
       //在子线程加载计算视频时长
        //Utils.calculateDuration(viewHolder.textDuration,videoData.videoUrl);
        viewHolder.textDuration.setText(Utils.formatTime(videoData.videoDuration));

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag ==0){      //直播
                    Intent intentAc = new Intent(mComtext, PlayLiveVideoDetailActivity.class);
                    intentAc.putExtra("ArticleBanner", videoData);
                    mComtext.startActivity(intentAc);

                }else if (flag == 1) {   //普通视频
                    Intent intentAc = new Intent(mComtext, PlayVideoDetailActivity.class);
                    //intentAc.putExtra("ArticleBanner", videoData);
                    MyApplication.articleBanner = videoData;
                    mComtext.startActivity(intentAc);
                }
            }
        });

    }



    @Override
    public int getItemCount() {
        return videoDataList.size();
    }
}
