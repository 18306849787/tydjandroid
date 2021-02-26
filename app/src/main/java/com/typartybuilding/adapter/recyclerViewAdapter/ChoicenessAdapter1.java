package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.NewsDetailActivity;
import com.typartybuilding.activity.PlayVideoDetailActivity;
import com.typartybuilding.activity.pbvideo.FindFascinatingActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.HomeFragmentChoicenessNew;
import com.typartybuilding.fragment.HomeFragmentChoicenessNew1;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class ChoicenessAdapter1 extends RecyclerView.Adapter {

    private String TAG = "ChoicenessAdapter";

    private static final int VIEW_TYPE_ZERO = 0;   //轮播图
    private static final int VIEW_TYPE_ONE = 1;
    private static final int VIEW_TYPE_TWO = 2;
    private static final int VIEW_TYPE_THREE = 3;
    private static final int VIEW_TYPE_FOUR = 4;
    private static final int VIEW_TYPE_FIVE = 5;

    private List<ArticleBanner> bannerList = new ArrayList<>();
    private Context mContext;
    private HomeFragmentChoicenessNew1 fragmentChoicenessNew1;
    private LayoutInflater inflater;

    public int currentPos;      //记录当前被点击跳转到的详情页面的 数据 的下标
    private int adapterPos;        //该adapter 的下标
    private FgBasicPartyAdapter basicPartyAdapter;  //父adapter


    static class ViewHolderZero extends RecyclerView.ViewHolder{

        @BindView(R.id.frameLayout)
        FrameLayout frameLayout;

        public ViewHolderZero(View view) {
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

    //视频
    static class ViewHolderFour extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_headline)
        TextView headLine;
        @BindView(R.id.imageView)
        ImageView imageView;           //视频封面图
        @BindView(R.id.imageView_play)
        ImageView imgPlay;             //播放按钮

        @BindView(R.id.textView_site)
        TextView textSite;
        @BindView(R.id.textView_date)
        TextView textDate;

        private View view;

        public ViewHolderFour(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    //微视频
    static class ViewHolderFive extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_more)
        TextView textMore;
        @BindView(R.id.recyclerView)
        RecyclerView recyclerView;

        public ViewHolderFive(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }


    public ChoicenessAdapter1(List<ArticleBanner> bannerList, Context mContext,HomeFragmentChoicenessNew1 fragmentChoicenessNew1) {
        this.bannerList = bannerList;
        this.mContext = mContext;
        this.fragmentChoicenessNew1 = fragmentChoicenessNew1;
        this.inflater = LayoutInflater.from(mContext);
    }

    //设置该 adapter  的下标
    public void setAdapterPos(int adapterPos) {
        this.adapterPos = adapterPos;
    }

    //设置父 adaper， FgBasicPartyAdapter
    public void setBasicPartyAdapter(FgBasicPartyAdapter basicPartyAdapter) {
        this.basicPartyAdapter = basicPartyAdapter;
    }


    @Override
    public int getItemViewType(int position) {

        //urlType , // 1：图片，2：视频;   position= 0, 表示轮播图
        if (position == 0){
            return VIEW_TYPE_ZERO;
        }else {
            int urlType = bannerList.get(position).urlType;
            String picUrls = bannerList.get(position).picUrls;
            //Log.i(TAG, "getItemViewType: urlType : " + urlType);
            //判断是否有微视
            if (bannerList.get(position).visionList != null && bannerList.get(position).visionList.length > 0) {
                return VIEW_TYPE_FIVE;
            } else if (urlType == 2) {
                return VIEW_TYPE_FOUR;
            } else {
                if (picUrls != null) {
                    if (!picUrls.contains(",")) {
                        return VIEW_TYPE_TWO;
                    } else {
                        String split[] = picUrls.split(",");
                        if (split.length >= 3) {
                            return VIEW_TYPE_ONE;
                        } else {
                            return VIEW_TYPE_TWO;
                        }
                    }
                } else {
                    return VIEW_TYPE_THREE;
                }
            }
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ZERO:
                viewHolder = new ViewHolderZero(inflater.inflate(R.layout.item_recyclerview_basic_party_0, viewGroup, false));
                break;
            case VIEW_TYPE_ONE:
                viewHolder = new ViewHolderOne(inflater.inflate(R.layout.item_recyclerview_basic_party_1, viewGroup, false));
                break;
            case VIEW_TYPE_TWO:
                viewHolder = new ViewHolderTwo(inflater.inflate(R.layout.item_recyclerview_basic_party_2, viewGroup, false));
                break;
            case VIEW_TYPE_THREE:
                viewHolder = new ViewHolderThree(inflater.inflate(R.layout.item_recyclerview_basic_party_3, viewGroup, false));
                break;
            case VIEW_TYPE_FOUR:
                viewHolder = new ViewHolderFour(inflater.inflate(R.layout.item_recyclerview_basic_party_4, viewGroup, false));
                break;
            case VIEW_TYPE_FIVE:
                viewHolder = new ViewHolderFive(inflater.inflate(R.layout.item_recyclerview_basic_party_5, viewGroup, false));
                break;
        }
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.i(TAG, "onBindViewHolder: i : " + i);
        int type = getItemViewType(i);
        ArticleBanner banner = bannerList.get(i);
        switch (type) {
            case VIEW_TYPE_ZERO:
                setViewTypeZero(viewHolder,banner,i);
                break;
            case VIEW_TYPE_ONE:
                setViewTypeOne(viewHolder, banner,i);
                break;
            case VIEW_TYPE_TWO:
                setViewTypeTwo(viewHolder, banner,i);
                break;
            case VIEW_TYPE_THREE:
                setViewTypeThree(viewHolder, banner,i);
                break;
            case VIEW_TYPE_FOUR:
                setViewTypeFour(viewHolder, banner,i);
                break;
            case VIEW_TYPE_FIVE:
                setViewTypeFive(viewHolder, banner,i);
                break;
        }

    }

    //轮播图
    private void setViewTypeZero(RecyclerView.ViewHolder viewHolder,ArticleBanner banner,int pos){
        ViewHolderZero holder = (ViewHolderZero)viewHolder;
        int id = holder.frameLayout.getId();
        fragmentChoicenessNew1.loadFragment(id);
    }

    //三张图片
    private void setViewTypeOne(RecyclerView.ViewHolder viewHolder,ArticleBanner banner,int pos){
        Log.i(TAG, "setViewTypeOne: ");
        ViewHolderOne holder = (ViewHolderOne)viewHolder;

        if (banner != null){
            holder.headLine.setText(banner.articleTitle);
            //设置新闻出处
            if (banner.articleSource != null && banner.articleSource != "") {
                holder.textSite.setVisibility(View.VISIBLE);
                holder.textSite.setText(banner.articleSource);
            }else {
                holder.textSite.setVisibility(View.GONE);
            }

            holder.textDate.setText(Utils.formatDate(banner.publishDate));
            Log.i(TAG, "setViewTypeOne: banner.publishDate : " + banner.publishDate);
            //加载图片
            String imgUrl[] = banner.picUrls.split(",");

            for (int i = 0; i < holder.imageView.length; i++ ) {
                Glide.with(mContext).asDrawable().load(imgUrl[i])
                        .apply(MyApplication.requestOptions43)  //url为空或异常显示默认头像
                        .into(holder.imageView[i]);
            }

        }
        //设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewsDetail(banner);
                //点击的 新闻 的下标
                currentPos = pos;
                //点击的新闻 所在的 adapter 的 下标
                if (basicPartyAdapter != null) {
                    basicPartyAdapter.adapterPos = adapterPos;
                }
            }
        });
    }

    //一张图片
    private void setViewTypeTwo(RecyclerView.ViewHolder viewHolder,ArticleBanner banner,int pos){
        Log.i(TAG, "setViewTypeTwo: ");
        ViewHolderTwo holder = (ViewHolderTwo)viewHolder;
        Log.i(TAG, "setViewTypeTwo: 一张图片");
        if (banner != null){
            holder.headLine.setText(banner.articleTitle);
            //设置新闻出处
            if (banner.articleSource != null && banner.articleSource != "") {
                holder.textSite.setVisibility(View.VISIBLE);
                holder.textSite.setText(banner.articleSource);
            }else {
                holder.textSite.setVisibility(View.GONE);
            }

            holder.textDate.setText(Utils.formatDate(banner.publishDate));
            Log.i(TAG, "setViewTypeTwo: banner.picUrls : " + banner.picUrls);
            String imgUrl = banner.picUrls;
            if (imgUrl != null && imgUrl.contains(",")){
                String split[] = imgUrl.split(",");
                imgUrl = split[0];
            }
            //加载图片
            Glide.with(mContext).asDrawable().load(imgUrl)
                    .apply(MyApplication.requestOptions43)  //url为空或异常显示默认头像
                    .into(holder.imageView);

                  /*  .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            if (resource != null){
                                holder.imageView.setImageDrawable(resource);
                            }
                        }
                    });
*/
        }
        //设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewsDetail(banner);
                //点击的 新闻 的下标
                currentPos = pos;
                //点击的新闻 所在的 adapter 的 下标
                if (basicPartyAdapter != null) {
                    basicPartyAdapter.adapterPos = adapterPos;
                }
            }
        });
    }

    //没有图片
    private void setViewTypeThree(RecyclerView.ViewHolder viewHolder,ArticleBanner banner,int pos){
        Log.i(TAG, "setViewTypeThree: ");
        ViewHolderThree holder = ( ViewHolderThree)viewHolder;
        if (banner != null){
            holder.headLine.setText(banner.articleTitle);
            //holder.textDetail.setText(banner.articleProfile);
            //设置新闻出处
            if (banner.articleSource != null && banner.articleSource != "") {
                holder.textSite.setVisibility(View.VISIBLE);
                holder.textSite.setText(banner.articleSource);
            }else {
                holder.textSite.setVisibility(View.GONE);
            }

            holder.textDate.setText(Utils.formatDate(banner.publishDate));
        }
        //设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewsDetail(banner);
                //点击的 新闻 的下标
                currentPos = pos;
                //点击的新闻 所在的 adapter 的 下标
                if (basicPartyAdapter != null) {
                    basicPartyAdapter.adapterPos = adapterPos;
                }
            }
        });
    }

    //视频
    private void setViewTypeFour(RecyclerView.ViewHolder viewHolder,ArticleBanner banner,int pos){

        Log.i(TAG, "setViewTypeFour: ");
        ViewHolderFour holder = ( ViewHolderFour) viewHolder;
        if (banner != null){
            holder.headLine.setText(banner.articleTitle);
            //加载封面图
            Glide.with(mContext).asDrawable().load(banner.videoCover)
                    .apply(MyApplication.requestOptionsVideo)
                    .into(holder.imageView);

            //设置新闻出处
            if (banner.articleSource != null && banner.articleSource != "") {
                holder.textSite.setVisibility(View.VISIBLE);
                holder.textSite.setText(banner.articleSource);
            }else {
                holder.textSite.setVisibility(View.GONE);
            }

            holder.textDate.setText(Utils.formatDate(banner.publishDate));
        }
        //设置点击事件,点击播放按钮，跳转到播放页面
        holder.imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.articleBanner = banner;
                Intent intentAc = new Intent(mContext, PlayVideoDetailActivity.class);
                mContext.startActivity(intentAc);
                //点击的 新闻 的下标
                currentPos = pos;
                //点击的新闻 所在的 adapter 的 下标
                if (basicPartyAdapter != null) {
                    basicPartyAdapter.adapterPos = adapterPos;
                }
            }
        });
        //点击封面图，跳转到播放页面
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.articleBanner = banner;
                Intent intentAc = new Intent(mContext, PlayVideoDetailActivity.class);
                mContext.startActivity(intentAc);
                //点击的 新闻 的下标
                currentPos = pos;
                //点击的新闻 所在的 adapter 的 下标
                if (basicPartyAdapter != null) {
                    basicPartyAdapter.adapterPos = adapterPos;
                }
            }
        });
    }

    //微视
    private void setViewTypeFive(RecyclerView.ViewHolder viewHolder,ArticleBanner banner,int pos){
        ViewHolderFive holder = ( ViewHolderFive) viewHolder;
        List<MicroVideo> microVideoList = new ArrayList<>();
        for (int i = 0; i < banner.visionList.length; i++){
            microVideoList.add(banner.visionList[i]);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setFocusable(false);
        ((SimpleItemAnimator)holder.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        ChoicenessMicVideoAdapter adapter = new ChoicenessMicVideoAdapter(microVideoList,mContext);
        //adapter.setHasStableIds(true);
        //添加分割线
       /* DividerItemDecoration dividerLine = new DividerItemDecoration(mContext,DividerItemDecoration.HORIZONTAL);
        dividerLine.setDrawable(mContext.getResources().getDrawable(R.drawable.recyclerview_divider_line_mic_video_fg_cho));
        holder.recyclerView.addItemDecoration(dividerLine);*/

        holder.recyclerView.setAdapter(adapter);
        //设置点击事件
        holder.textMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, FindFascinatingActivity.class);
                mContext.startActivity(intentAc);
            }
        });

    }

    /**
     * 跳转到资讯新闻详情页面
     */
    private void startNewsDetail(ArticleBanner banner){
        Intent intentAc = new Intent(mContext, NewsDetailActivity.class);
        //intentAc.putExtra("ArticleBanner",banner);
        MyApplication.articleBanner = banner;
        mContext.startActivity(intentAc);
    }


    @Override
    public int getItemCount() {
        return bannerList.size();
    }

}
