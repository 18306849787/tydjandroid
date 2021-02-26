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

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.NewsDetailMixActivity;
import com.typartybuilding.activity.PlayAudioMixActivityNew;
import com.typartybuilding.activity.PlayMicroVideoMixActivity;
import com.typartybuilding.activity.PlayPictureMixActivity;
import com.typartybuilding.activity.PlayVideoDetailMixActivity;
import com.typartybuilding.activity.second.PlayVideoAct;
import com.typartybuilding.activity.second.TextDetailAct;
import com.typartybuilding.activity.second.lean.PoliciesAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.MixtureData;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MixtureDataAdapter extends RecyclerView.Adapter {

    private String TAG = "MixtureDataAdapter";

    private static final int TYPE_ITEM_FOOTER = 0;   //底部最后一个item的布局
    private static final int VIEW_TYPE_ONE = 1;      //视频
    private static final int VIEW_TYPE_TWO = 2;      //三张图片 新闻
    private static final int VIEW_TYPE_THREE = 3;    //一张图片 新闻
    private static final int VIEW_TYPE_FOUR = 4;     //没有图片 新闻
    private static final int VIEW_TYPE_FIVE = 5;    //微视 只有短视频

    private List<MixtureData> dataList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;
    private int flag;              // 1，表示 我的足迹 和 我的收藏； 日期 取 creattime字段

    private ViewHolderFooter mHolder;     //底部最后一个item的布局

    public void setFlag(int flag) {
        this.flag = flag;
    }

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

    //普通视频
    static class ViewHolderOne extends RecyclerView.ViewHolder{

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

        public ViewHolderOne(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    //新闻
    //三张图片 新闻
    static class ViewHolderTwo extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_headline)
        TextView headLine;
        @BindViews({R.id.imageView1, R.id.imageView2, R.id.imageView3})
        ImageView [] imageView;
        @BindView(R.id.textView_site)
        TextView textSite;

        @BindView(R.id.textView_date)
        TextView textDate;

        private View view;

        public ViewHolderTwo(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    //一张图片 新闻
    static class ViewHolderThree extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_headline)
        TextView headLine;
        @BindView(R.id.textView_site)
        TextView textSite;
        @BindView(R.id.textView_date)
        TextView textDate;
        @BindView(R.id.imageView)
        ImageView imageView;
        private View view;

        public ViewHolderThree(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    //没有图片 新闻
    static class ViewHolderFour extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_headline)
        TextView headLine;
        @BindView(R.id.textView_detail)
        TextView textDetail;
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

        @BindView(R.id.imageView_video_pic)
        ImageView imageView;               //视频图片
       /* @BindView(R.id.imageView_audio)
        ImageView imgAudio;                //默认音频图片*/

        @BindView(R.id.textView_headline)
        TextView headLine;                 //标题
        @BindView(R.id.textView_subhead)
        TextView subHead;                  //背景音乐
        @BindView(R.id.imageView_hint1)
        ImageView imgHint;                 //背景音乐 图标
        @BindView(R.id.imageView_play)
        ImageView imgPlay;                 //右下角 播放按钮
        @BindView(R.id.textView_date)
        TextView textDate;                 //发布日期
        @BindView(R.id.textView_like_num)
        TextView likeNum;                  //点赞数量
        @BindView(R.id.textView_name)
        TextView textName;                 //视频发布者的 昵称
        @BindView(R.id.circle_img_head)
        CircleImageView imgHead;           //头像
        private View view;

        public ViewHolderFive(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    public MixtureDataAdapter(List<MixtureData> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemViewType(int position) {

        //if (position + 1 < getItemCount()) {
            MixtureData data = dataList.get(position);
            //targetType：   1：文章(新闻，普通视频)，2：微视
            //urlType：      1：图片，2：视频，3：音频
            //fileUrl:       文件url根据urlType而定，多图以逗号隔开
            if (data.targetType == 1) {
                if (data.urlType == 2) {
                    return VIEW_TYPE_ONE;        //普通视频
                } else {
                    if (data.fileUrl == null) {
                        return VIEW_TYPE_FOUR;   //没有图片 的新闻
                    } else {
                        if (data.fileUrl.contains(",")) {
                            String split[] = data.fileUrl.split(",");
                            if (split.length >= 3) {
                                return VIEW_TYPE_TWO;    //三张图片 新闻
                            } else {
                                return VIEW_TYPE_THREE;  //一张图片 新闻
                            }
                        } else {
                            return VIEW_TYPE_THREE;      //一张图片 新闻
                        }
                    }
                }

            } else {
                return VIEW_TYPE_FIVE;                  //微视
            }
        /*}else {
            return TYPE_ITEM_FOOTER;
        }*/

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_ITEM_FOOTER) {
            mHolder = new ViewHolderFooter(inflater.inflate(R.layout.item_load_more2, viewGroup, false));
            return mHolder;
        }else {
            RecyclerView.ViewHolder viewHolder = null;
            switch (viewType) {
                case VIEW_TYPE_ONE:
                    viewHolder = new ViewHolderOne(inflater.inflate(R.layout.item_recyclerview_hot_video_ac, viewGroup, false));
                    break;
                case VIEW_TYPE_TWO:
                    viewHolder = new ViewHolderTwo(inflater.inflate(R.layout.item_recyclerview_current_new1, viewGroup, false));
                    break;
                case VIEW_TYPE_THREE:
                    viewHolder = new ViewHolderThree(inflater.inflate(R.layout.item_recyclerview_current_new2, viewGroup, false));
                    break;
                case VIEW_TYPE_FOUR:
                    viewHolder = new ViewHolderFour(inflater.inflate(R.layout.item_recyclerview_current_new3, viewGroup, false));
                    break;
                case VIEW_TYPE_FIVE:
                    viewHolder = new ViewHolderFive(inflater.inflate(R.layout.item_recyclerview_pb_hot_bot_ac3, viewGroup, false));
                    break;
            }

            return viewHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.i(TAG, "onBindViewHolder: i : " + i);
        int type = getItemViewType(i);
        if (type == TYPE_ITEM_FOOTER){

        }else {
            MixtureData data = dataList.get(i);
            switch (type) {
                case VIEW_TYPE_ONE:
                    setViewTypeOne(viewHolder, data);
                    break;
                case VIEW_TYPE_TWO:
                    setViewTypeTwo(viewHolder, data);
                    break;
                case VIEW_TYPE_THREE:
                    setViewTypeThree(viewHolder, data);
                    break;
                case VIEW_TYPE_FOUR:
                    setViewTypeFour(viewHolder, data);
                    break;
                case VIEW_TYPE_FIVE:
                    setViewTypeFive(viewHolder, data);
                    break;
            }
        }

    }

    //普通视频
    private void setViewTypeOne(RecyclerView.ViewHolder viewHolder,MixtureData data){
        ViewHolderOne holder = (ViewHolderOne) viewHolder;

        if (data != null) {
            //加载封面图片
            Glide.with(mContext).load(data.videoCover)
                    .apply(MyApplication.requestOptions43)  //url为空或异常显示默认头像
                    .into(holder.roundImg);
            //子线程计算 视频时长
            //Utils.calculateDuration(holder.playDuration,data.fileUrl);
            holder.playDuration.setText(Utils.formatTime(data.videoDuration));
            holder.headLine.setText(data.targetTitle);
            holder.playTimes.setText(Utils.formatPlayTimes(data.browseTimes) + "次播放");

            if (flag == 1){
                holder.textDate.setText(Utils.formatDate(data.createTime));
            }else {
                holder.textDate.setText(Utils.formatDate(data.publishDate));
            }

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build(PlayVideoAct.PTAH)
                            .withString(PlayVideoAct.URL, data.videoCover)
                            .withInt(PlayVideoAct.ARTICLE_TYPE, data.articleType)
                            .withInt(PlayVideoAct.ARTICLE_ID, data.targetId)
                            .withInt(PlayVideoAct.URL_TYPE, 2)
                            .navigation();
//                    Intent intentAc = new Intent(mContext, PlayVideoDetailMixActivity.class);
//                    //intentAc.putExtra("MixtureData",data);
//                    MyApplication.mixtureData = data;
//                    mContext.startActivity(intentAc);
                }
            });
        }
    }

    //三张图片 的新闻
    private void setViewTypeTwo(RecyclerView.ViewHolder viewHolder,MixtureData data){
        ViewHolderTwo holder = (ViewHolderTwo) viewHolder;
        if (data != null){
            holder.headLine.setText(data.targetTitle);
            holder.textSite.setText(data.articleSource);
            if (flag == 1){
                holder.textDate.setText(Utils.formatDate(data.createTime));
            }else {
                holder.textDate.setText(Utils.formatDate(data.publishDate));
            }

            //加载图片
            String imgUrl[] = data.fileUrl.split(",");

            for (int i = 0; i < holder.imageView.length; i++ ) {
                Glide.with(mContext).load(imgUrl[i])
                        .apply(MyApplication.requestOptions43)  //url为空或异常显示默认
                        .into(holder.imageView[i]);
            }

        }
        //设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipNewsDetail(data);
            }
        });
    }

    //一张图片 新闻
    private void setViewTypeThree(RecyclerView.ViewHolder viewHolder, MixtureData data){
        ViewHolderThree holder = (ViewHolderThree)viewHolder;
        if (data != null){
            holder.headLine.setText(data.targetTitle);
            holder.textSite.setText(data.articleSource);

            if (flag == 1){
                holder.textDate.setText(Utils.formatDate(data.createTime));
            }else {
                holder.textDate.setText(Utils.formatDate(data.publishDate));
            }

            //加载图片
            String imgUrl = data.fileUrl;
            if (imgUrl.contains(",")){
                String split [] = imgUrl.split(",");
                imgUrl = split[0];
            }
            Glide.with(mContext).load(imgUrl)
                    .apply(MyApplication.requestOptions43)  //url为空或异常显示默认头像
                    .into(holder.imageView);

        }
        //设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipNewsDetail(data);
            }
        });
    }

    //没有图片的新闻
    private void setViewTypeFour(RecyclerView.ViewHolder viewHolder,MixtureData data){
        ViewHolderFour holder = ( ViewHolderFour)viewHolder;
        if (data != null){
            holder.headLine.setText(data.targetTitle);
            holder.textDetail.setText(data.articleProfile);
            holder.textSite.setText(data.articleSource);

            if (flag == 1){
                holder.textDate.setText(Utils.formatDate(data.createTime));
            }else {
                holder.textDate.setText(Utils.formatDate(data.publishDate));
            }
        }
        //设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipNewsDetail(data);
            }
        });
    }

    //微视, 短视频, 音频，图片
    private void setViewTypeFive(RecyclerView.ViewHolder viewHolder,MixtureData data){
        ViewHolderFive holder = (ViewHolderFive) viewHolder;

        if (data != null){
            //加载封面   "urlType":   1：图片，2：视频，3：音频
            if (data.urlType == 1){
                String imgUrl = data.fileUrl;
                if (imgUrl != null){
                    if (imgUrl.contains(",")){
                        String split [] = imgUrl.split(",");
                        imgUrl = split[0];
                    }
                }
                //加载封面
                Glide.with(mContext).load(imgUrl)
                        .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                        .into(holder.imageView);
                //加载头像
                Glide.with(mContext).load(data.userHeadImg)
                        .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                        .into(holder.imgHead);

                holder.headLine.setText(data.targetTitle);
                //背景音乐名没有
                holder.subHead.setText("");
                holder.imgHint.setVisibility(View.INVISIBLE);   //背景音乐图标不可见
                holder.imgPlay.setVisibility(View.INVISIBLE);   //播放按钮不可见

                if (flag == 1){
                    holder.textDate.setText(Utils.formatDate(data.createTime));
                }else {
                    holder.textDate.setText(Utils.formatDate(data.publishDate));
                }

                holder.likeNum.setText(Utils.formatPlayTimes2(data.praisedNum));
                holder.textName.setText(data.userName);

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skipMicVideo(data);
                    }
                });

            //视频
            }else if (data.urlType == 2) {
                Glide.with(mContext).load(data.videoCover)
                        .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                        .into(holder.imageView);

                //加载头像
                Glide.with(mContext).load(data.userHeadImg)
                        .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                        .into(holder.imgHead);

                holder.headLine.setText(data.targetTitle);
                //背景音乐名
                Log.i(TAG, "setViewTypeFive: 背景音乐名称 ： " + data.bgmName);
                holder.subHead.setText(data.bgmName);
                holder.imgHint.setVisibility(View.VISIBLE);   //背景音乐图标可见
                holder.imgPlay.setVisibility(View.VISIBLE);   //播放按钮可见

                if (flag == 1){
                    holder.textDate.setText(Utils.formatDate(data.createTime));
                }else {
                    holder.textDate.setText(Utils.formatDate(data.publishDate));
                }

                holder.likeNum.setText(Utils.formatPlayTimes2(data.praisedNum));
                holder.textName.setText(data.userName);
                holder.imgPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skipMicVideo(data);
                    }
                });

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skipMicVideo(data);
                    }
                });

            //音频
            }else if (data.urlType == 3){
                //加载封面
                Glide.with(mContext).load(data.videoCover)
                        .apply(MyApplication.requestOptionsAudio)  //url为空或异常显示默认头像
                        .into(holder.imageView);
                //加载头像
                Glide.with(mContext).load(data.userHeadImg)
                        .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                        .into(holder.imgHead);

                holder.headLine.setText(data.targetTitle);
                //背景音乐名没有
                holder.subHead.setText("");
                holder.imgHint.setVisibility(View.INVISIBLE);   //背景音乐图标不可见
                holder.imgPlay.setVisibility(View.INVISIBLE);   //播放按钮不可见

                if (flag == 1){
                    holder.textDate.setText(Utils.formatDate(data.createTime));
                }else {
                    holder.textDate.setText(Utils.formatDate(data.publishDate));
                }

                holder.likeNum.setText(Utils.formatPlayTimes2(data.praisedNum));
                holder.textName.setText(data.userName);

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skipMicVideo(data);
                    }
                });

            }

        }
    }

    /**
     * 跳转到 微视频 播放页面
     * @param
     */
    private void skipMicVideo(MixtureData mixtureData){
        //"urlType": 2,  1：图片，2：视频，3：音频
        if (mixtureData.urlType == 1){
            Intent intentAc = new Intent(mContext, PlayPictureMixActivity.class);
            //intentAc.putExtra("MixtureData", mixtureData);
            MyApplication.mixtureData = mixtureData;
            mContext.startActivity(intentAc);
        }else if (mixtureData.urlType == 2) {
            Intent intentAc = new Intent(mContext, PlayMicroVideoMixActivity.class);
            //intentAc.putExtra("MixtureData", mixtureData);
            MyApplication.mixtureData = mixtureData;
            mContext.startActivity(intentAc);
        }else if (mixtureData.urlType == 3){
            //Intent intentAc = new Intent(mContext, PlayAudioMixActivityNew.class);
            Intent intentAc = new Intent(mContext, PlayAudioMixActivityNew.class);
            MyApplication.mixtureData = mixtureData;
            mContext.startActivity(intentAc);
        }

    }

    /**
     * 跳转到资讯新闻详情页面
     */
    private void skipNewsDetail(MixtureData mixtureData){
        ARouter.getInstance().build(TextDetailAct.PATH)
                .withString(TextDetailAct.URL, mixtureData.articleDetailUrl)
                .withInt(TextDetailAct.ARTICLE_TYPE, 1)
                .withInt(TextDetailAct.ARTICLE_ID, mixtureData.targetId)
                .withInt(TextDetailAct.URL_TYPE, 1)
                .navigation(mContext);
//        Intent intentAc = new Intent(mContext, NewsDetailMixActivity.class);
//        //intentAc.putExtra("MixtureData",mixtureData);
//        MyApplication.mixtureData = mixtureData;
//        mContext.startActivity(intentAc);
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

    //数据加载完后，更改文字提示
    public void setTypeItemFooter(){
       /* mHolder.progressBar.setVisibility(View.INVISIBLE);
        mHolder.textHint.setText("没有更多了");
*/
        if (mHolder != null){
            if (mHolder.progressBar != null){
                mHolder.progressBar.setVisibility(View.INVISIBLE);
            }
            if (mHolder.textHint != null){
                mHolder.textHint.setText("没有更多了");
            }
        }
    }

    @Override
    public int getItemCount() {
        if (dataList.size() == 0){
            return 0;
        }else {
            return dataList.size();
        }
       //return dataList.size() + 1;
    }

}
