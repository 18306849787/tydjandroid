package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.NewsDetailActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsDetailAcAdapter extends RecyclerView.Adapter<NewsDetailAcAdapter.ViewHolder> {

    private String TAG = "NewsDetailAcAdapter";
    private List<ArticleBanner> bannerList =new ArrayList<>();
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imageView)
        RoundImageView imageView;

        @BindView(R.id.textView_headline)
        TextView headLine;

        private View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    public NewsDetailAcAdapter(List<ArticleBanner> bannerList, Context mContext) {
        this.bannerList = bannerList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.item_recyclerview_news_detail_ac,
                viewGroup,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ArticleBanner bannerData = bannerList.get(i);
        Log.i(TAG, "onBindViewHolder: headline: " + bannerData.articleTitle);
        Log.i(TAG, "onBindViewHolder: imgUrl: " + bannerData.picUrls);
        //加载图片

        String imgUrl = null;
        if (bannerData.picUrls != null) {
            if (bannerData.picUrls.contains(",")) {
                String split[] = bannerData.picUrls.split(",");
                imgUrl = split[0];
            } else {
                imgUrl = bannerData.picUrls;
            }
        }
        Glide.with(mContext).load(imgUrl)
                .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                .into(viewHolder.imageView);

        //加载标题
        viewHolder.headLine.setText(bannerData.articleTitle);

        //跳转到详情页面
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, NewsDetailActivity.class);
                //intentAc.putExtra("ArticleBanner",bannerData);
                MyApplication.articleBanner = bannerData;
                mContext.startActivity(intentAc);
                //((NewsDetailActivity)mContext).finish();

            }
        });

    }



    @Override
    public int getItemCount() {
        //Log.i(TAG, "getItemCount: " + dataList.size());
        return bannerList.size();
    }
}
