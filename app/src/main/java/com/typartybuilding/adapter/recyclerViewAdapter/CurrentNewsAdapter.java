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
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.activity.NewsDetailActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class CurrentNewsAdapter extends RecyclerView.Adapter {

    private String TAG = "CurrentNewsAdapter";

    private static final int VIEW_TYPE_ONE = 1;
    private static final int VIEW_TYPE_TWO = 2;
    private static final int VIEW_TYPE_THREE = 3;
    private static final int TYPE_ITEM_FOOTER = 0;

    private List<ArticleBanner> bannerList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;

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

    //三张图片
    static class ViewHolderOne extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_headline)
        TextView headLine;
        @BindViews({R.id.imageView1, R.id.imageView2, R.id.imageView3})
        ImageView [] imageView;
        @BindView(R.id.textView_site)
        TextView textSite;

        @BindView(R.id.textView_date)
        TextView textDate;

        private View view;

        public ViewHolderOne(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    //一张图片
    static class ViewHolderTwo extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_headline)
        TextView headLine;
        @BindView(R.id.textView_site)
        TextView textSite;
        @BindView(R.id.textView_date)
        TextView textDate;
        @BindView(R.id.imageView)
        ImageView imageView;
        private View view;

        public ViewHolderTwo(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    //没有图片
    static class ViewHolderThree extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_headline)
        TextView headLine;
        @BindView(R.id.textView_detail)
        TextView textDetail;
        @BindView(R.id.textView_site)
        TextView textSite;
        @BindView(R.id.textView_date)
        TextView textDate;

        private View view;

        public ViewHolderThree(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }


    public CurrentNewsAdapter(List<ArticleBanner> bannerList, Context mContext) {
        this.bannerList = bannerList;
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemViewType(int position) {

        if (position + 1 < getItemCount()) {
            String picUrls = bannerList.get(position).picUrls;
            //Log.i(TAG, "getItemViewType: picUrls : " + picUrls);
            if (picUrls != null) {
                if (!picUrls.contains(",")) {
                    return VIEW_TYPE_TWO;
                } else {
                    String split[] = picUrls.split(",");
                    if (split.length >= 3){
                        return VIEW_TYPE_ONE;
                    }else {
                        return VIEW_TYPE_TWO;
                    }
                }
            } else {
                return VIEW_TYPE_THREE;
            }
        }else {
            return TYPE_ITEM_FOOTER;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_ITEM_FOOTER){
            Log.i(TAG, "onCreateViewHolder: ");
            mHolder = new ViewHolderFooter(inflater.inflate(R.layout.item_load_more2, viewGroup, false));
            return mHolder;
        }else {
            RecyclerView.ViewHolder viewHolder = null;
            switch (viewType) {
                case VIEW_TYPE_ONE:
                    viewHolder = new ViewHolderOne(inflater.inflate(R.layout.item_recyclerview_current_new1, viewGroup, false));
                    break;
                case VIEW_TYPE_TWO:
                    viewHolder = new ViewHolderTwo(inflater.inflate(R.layout.item_recyclerview_current_new2, viewGroup, false));
                    break;
                case VIEW_TYPE_THREE:
                    viewHolder = new ViewHolderThree(inflater.inflate(R.layout.item_recyclerview_current_new3, viewGroup, false));
                    break;
            }
            return viewHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.i(TAG, "onBindViewHolder: ");
        int type = getItemViewType(i);
        if (type == TYPE_ITEM_FOOTER){

        }else {
            ArticleBanner banner = bannerList.get(i);
            switch (type) {
                case VIEW_TYPE_ONE:
                    setViewTypeOne(viewHolder, banner);
                    break;
                case VIEW_TYPE_TWO:
                    setViewTypeTwo(viewHolder, banner);
                    break;
                case VIEW_TYPE_THREE:
                    setViewTypeThree(viewHolder, banner);
                    break;
            }
        }

    }



    //三张图片
    private void setViewTypeOne(RecyclerView.ViewHolder viewHolder,ArticleBanner banner){
        ViewHolderOne holder = (ViewHolderOne)viewHolder;

        if (banner != null){
            holder.headLine.setText(banner.articleTitle);
            holder.textSite.setText(banner.articleSource);
            holder.textDate.setText(Utils.formatDate(banner.publishDate));
            //加载图片
            String imgUrl[] = banner.picUrls.split(",");

            for (int i = 0; i < holder.imageView.length; i++ ) {
                Glide.with(mContext).load(imgUrl[i])
                        .apply(MyApplication.requestOptions43)  //url为空或异常显示默认头像
                        .into(holder.imageView[i]);
            }

        }
        //设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewsDetail(banner);
            }
        });
    }

    //一张图片
    private void setViewTypeTwo(RecyclerView.ViewHolder viewHolder,ArticleBanner banner){
        ViewHolderTwo holder = (ViewHolderTwo)viewHolder;
        Log.i(TAG, "setViewTypeTwo: 一张图片");
        if (banner != null){
            holder.headLine.setText(banner.articleTitle);
            holder.textSite.setText(banner.articleSource);
            holder.textDate.setText(Utils.formatDate(banner.publishDate));
            Log.i(TAG, "setViewTypeTwo: banner.picUrls : " + banner.picUrls);
            String imgUrl = banner.picUrls;
            if (imgUrl != null && imgUrl.contains(",")){
                String split[] = imgUrl.split(",");
                imgUrl = split[0];
            }
            //加载图片
            Glide.with(mContext).load(imgUrl)
                    .apply(MyApplication.requestOptions43)  //url为空或异常显示默认头像
                    .into(holder.imageView);

        }
        //设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewsDetail(banner);
            }
        });
    }

    private void setViewTypeThree(RecyclerView.ViewHolder viewHolder,ArticleBanner banner){
        ViewHolderThree holder = ( ViewHolderThree)viewHolder;
        if (banner != null){
            holder.headLine.setText(banner.articleTitle);
            holder.textDetail.setText(banner.articleProfile);
            holder.textSite.setText(banner.articleSource);
            holder.textDate.setText(Utils.formatDate(banner.publishDate));
        }
        //设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewsDetail(banner);
            }
        });
    }

    /**
     * 跳转到资讯新闻详情页面
     */
    private void startNewsDetail(ArticleBanner banner){
        Intent intentAc = new Intent(mContext, NewsDetailActivity.class);
        intentAc.putExtra("ArticleBanner",banner);
        Log.i(TAG, "startNewsDetail: ArticleBanner : " + banner.toString());
        MyApplication.articleBanner = banner;
        Log.i(TAG, "startNewsDetail: MyApplication.articleBanner : " + MyApplication.articleBanner);
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
        Log.i(TAG, "setTypeItemFooterStart: ");
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
    public int getItemCount() {
        //Log.i(TAG, "getItemCount: size : " + bannerList.size());
      /*  if (bannerList.size() == 0){
            return 0;
        }else {
            return bannerList.size() + 1;
        }*/
      return bannerList.size() + 1;
    }

}
