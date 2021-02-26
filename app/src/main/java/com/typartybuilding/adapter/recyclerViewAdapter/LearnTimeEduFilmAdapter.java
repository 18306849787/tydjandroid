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
import com.typartybuilding.activity.learntime.EduFilmActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.learntime.EducationFilmData;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class LearnTimeEduFilmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "LearnTimeEduFilmAdapter";

    private List<EducationFilmData.EducationFilm> dataList = new ArrayList<>();
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

        @BindView(R.id.textView_classify)
        TextView textClassify;          //分类名称
        @BindView(R.id.textView_more)
        TextView textMore;              //更多

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

    public LearnTimeEduFilmAdapter(List<EducationFilmData.EducationFilm> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM_NORMAL) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_learn_time_edu_film,
                    viewGroup, false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_load_more2,
                    viewGroup, false);
            mHolder = new ViewHolderFooter(view);
            return mHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int type = getItemViewType(i);
        if (type == TYPE_ITEM_NORMAL) {
            ViewHolder holder = ( ViewHolder) viewHolder;
            EducationFilmData.EducationFilm educationFilm = dataList.get(i);
            ArticleBanner [] articleBanners = educationFilm.rows;
            int size = articleBanners.length;
            if (size > 2){
                size = 2;
            }
            //分类名称
            holder.textClassify.setText(educationFilm.articleLabelName);

            for (int j = 0; j < size; j++) {

                ArticleBanner banner = articleBanners[j];
                String imgUrl = banner.videoCover;
                Log.i(TAG, "onBindViewHolder: imgUrl : " + imgUrl);
                //加载封面图片
                Glide.with(mContext).load(imgUrl)
                        .apply(MyApplication.requestOptionsVideo2)  //url为空或异常显示默认图片
                        .into(holder.imageView[j]);

                holder.headLine1[j].setText(banner.articleTitle);
                holder.headLine2[j].setText(banner.articleProfile);
                //在子线程中计算视频时长，然后再主线程显示
                //Utils.calculateDuration(holder.playDurations[j], banner.videoUrl);
                holder.playDurations[j].setText(Utils.formatTime(banner.videoDuration));

            }

            //更多按钮
            holder.textMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentAc = new Intent(mContext, EduFilmActivity.class);
                    intentAc.putExtra("articleLabel",educationFilm.articleLabel);
                    intentAc.putExtra("articleLabelName",educationFilm.articleLabelName);
                    mContext.startActivity(intentAc);
                }
            });

            holder.imageView[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    skipPlayVideo(articleBanners[0]);

                }
            });

            holder.imageView[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    skipPlayVideo(articleBanners[1]);
                }

            });
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

    public void setTypeItemFooter(){

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
        return dataList.size();
    }
}
