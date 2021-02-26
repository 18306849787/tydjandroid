package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayMicroVideoActivity;
import com.typartybuilding.activity.UserDetailsActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.douyin.MicroVideoPlayActivity;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 *  精选页面，精彩微视
 */

public class ChoicenessMicVideoAdapter extends RecyclerView.Adapter<ChoicenessMicVideoAdapter.ViewHolder> {

    private List<MicroVideo> dataList = new ArrayList<>();
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.imageView_play)
        ImageView imgPlay;          //播放
        @BindView(R.id.circle_img_head)
        CircleImageView imgHead;
        @BindView(R.id.textView_headline)
        TextView headLine;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public ChoicenessMicVideoAdapter(List<MicroVideo> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.item_recyclerview_mic_video_fg_cho,
                viewGroup,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MicroVideo videoData = dataList.get(i);

        //获取视频第一帧，耗时操作，需在子线程中进行,并刷新页面
       // Utils.getVideoPicture(viewHolder.imageView,videoData.visionFileUrl);
        //加载封面图
        Glide.with(mContext).asDrawable().load(videoData.videoCover)
                .apply(MyApplication.requestOptions916)  //url为空或异常显示默认头像
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        if (resource != null){
                            viewHolder.imageView.setImageDrawable(resource);
                        }
                    }
                });
        //加载头像
        Glide.with(mContext).load(videoData.userHeadImg)
                .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                .into(viewHolder.imgHead);
        //加载标题
        viewHolder.headLine.setText(videoData.visionTitle);

        //跳转到用户详情页面
        viewHolder.imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, UserDetailsActivity.class);
                intentAc.putExtra("userId",videoData.userId);
                intentAc.putExtra("userName",videoData.userName);
                mContext.startActivity(intentAc);
            }
        });
        //跳转到微视频播放页面
        viewHolder.imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intentAc = new Intent(mContext, PlayMicroVideoActivity.class);
                Intent intentAc = new Intent(mContext, MicroVideoPlayActivity.class);
                MyApplication.microVideo = videoData;
                mContext.startActivity(intentAc);
            }
        });
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intentAc = new Intent(mContext, PlayMicroVideoActivity.class);
                Intent intentAc = new Intent(mContext, MicroVideoPlayActivity.class);
                MyApplication.microVideo = videoData;
                mContext.startActivity(intentAc);
            }
        });

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
