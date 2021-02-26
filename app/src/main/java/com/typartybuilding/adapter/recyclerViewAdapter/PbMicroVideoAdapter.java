package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayAudioActivityNew;
import com.typartybuilding.activity.PlayPictureActivity;
import com.typartybuilding.activity.UserDetailsActivity;
import com.typartybuilding.activity.pbvideo.FindFascinatingActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.douyin.MicroVideoPlayActivity;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingVideoNew1;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PbMicroVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "PbMicroVideoAdapter";

    private List<MicroVideo> dataList = new ArrayList<>();
    private HomeFragmentPartyBuildingVideoNew1 fragment;
    private Context mContext;
    private int flag = 2;
    private int clickPos = -1;    //点击的 位置
    private ViewHolderNormal currentHolder;


    public PbMicroVideoAdapter(List<MicroVideo> dataList, HomeFragmentPartyBuildingVideoNew1 fragment) {
        this.dataList = dataList;
        this.fragment = fragment;
        this.mContext = fragment.getActivity();
    }

    //党建热搜
    static class ViewHolderOne extends RecyclerView.ViewHolder{
        @BindView(R.id.frameLayout1)
        FrameLayout frameLayout1;               //排行
        public ViewHolderOne(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    //人气榜单
    static class ViewHolderTwo extends RecyclerView.ViewHolder{
        @BindView(R.id.frameLayout2)
        FrameLayout frameLayout2;               //排行
        public ViewHolderTwo(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    //发现精彩 的文字
    static class ViewHolderThree extends RecyclerView.ViewHolder{
        @BindView(R.id.textView_more)
        TextView textMore;               //排行
        public ViewHolderThree(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    //微视
    static class ViewHolderNormal extends RecyclerView.ViewHolder{

        @BindView(R.id.imageView_find_fascinating)
        ImageView imageView;                 //图片
        @BindView(R.id.textView_headline)
        TextView headLine;                   //标题
        @BindView(R.id.circle_img_head)
        CircleImageView circleIma;         //头像
        @BindView(R.id.textView_name)
        TextView textName;                //昵称
        @BindView(R.id.textView_play_times)
        TextView textPlayTimes;           //播放次数

        private View view;

        public ViewHolderNormal(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_pb_micro_video1,
                    viewGroup, false);
            //view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_pb_micro_video1, null);
            return new ViewHolderOne(view);
        }else if (viewType == 1){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_pb_micro_video2,
                    viewGroup, false);
            return new ViewHolderTwo(view);
        }else if (viewType == 2){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_pb_micro_video3,
                    viewGroup, false);
            return new ViewHolderThree(view);
        }else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_find_fascinating_pbv,
                    viewGroup, false);
            return new ViewHolderNormal(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.i(TAG, "onBindViewHolder: i : " + i);
        if (i == 0) {
            ViewHolderOne holder = (ViewHolderOne) viewHolder;
            int id = holder.frameLayout1.getId();
            fragment.loadFragment(i, id);
        }else if (i == 1){
            ViewHolderTwo holder = (ViewHolderTwo) viewHolder;
            int id = holder.frameLayout2.getId();
            fragment.loadFragment(i, id);
        }else if (i == 2){
            setTypeItemThree(viewHolder,i);

        }else {
            setTypeItemNormal(viewHolder,i);
        }
    }

    private void setTypeItemThree(RecyclerView.ViewHolder viewHolder,int i){
        ViewHolderThree holder = (ViewHolderThree) viewHolder;
        holder.textMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, FindFascinatingActivity.class);
                mContext.startActivity(intentAc);
            }
        });
    }


    private void setTypeItemNormal(RecyclerView.ViewHolder viewHolder,int i){
        ViewHolderNormal holder = (ViewHolderNormal)viewHolder;
        MicroVideo microVideo = dataList.get(i);

        Log.i(TAG, "setTypeItemNormal: videoUrl : " + microVideo.visionFileUrl);
        if (microVideo.visionTitle == null || microVideo.visionTitle.equals("")){
            holder.headLine.setVisibility(View.GONE);
        }else {
            holder.headLine.setVisibility(View.VISIBLE);
            holder.headLine.setText(microVideo.visionTitle);
        }

        holder.textName.setText(microVideo.userName);
        holder.textPlayTimes.setText(Utils.formatPlayTimes2(microVideo.visionBrowseTimes));

        RequestOptions requestOptions = null;
        //1 ,表示在党建微视页面   2 ，表示在发现精彩的 activity 页面
        if (flag == 1){
            requestOptions = MyApplication.requestOptions34;
        }else if (flag == 2){
            int widthPix = (Utils.getScreenWidth() - 3*Utils.dip2px(mContext,10))/2;
            requestOptions = new RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.shape_default_pic)
                    .error(R.mipmap.default_pic_1_1)
                    .override(widthPix,Integer.MAX_VALUE);
        }

        // 加载封面,   visionType //1：图片，2：视频，3：音频
        String imgUrl = null;   //封面图url
        int visionType = microVideo.visionType;

        //图片
        if (visionType == 1){
            if (microVideo.visionFileUrl.contains(",")){
                String split[] = microVideo.visionFileUrl.split(",");
                imgUrl = split[0];
            }else {
                imgUrl = microVideo.visionFileUrl;
            }
            Glide.with(mContext).asDrawable().load(imgUrl)
                    .apply(requestOptions)  //url为空或异常显示默认头像
                    .into(holder.imageView);

            //视频
        }else if (visionType == 2){

            Glide.with(mContext).asDrawable().load(microVideo.videoCover)
                    .apply(requestOptions)  //url为空或异常显示默认头像
                    .into(holder.imageView);

            //音频
        }else if (visionType == 3){

            //1 ,表示在党建微视页面   2 ，表示在发现精彩的 activity 页面
            if (flag == 1){
                requestOptions = MyApplication.requestOptionsAudio;
            }else if (flag == 2){
                int widthPix = (Utils.getScreenWidth() - 3*Utils.dip2px(mContext,10))/2;
                requestOptions = new RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.shape_default_pic)
                        .error(R.mipmap.yinpin_imgbg)
                        .override(widthPix,Integer.MAX_VALUE);
            }

            Glide.with(mContext).asDrawable().load(microVideo.videoCover)
                    .apply(requestOptions)  //url为空或异常显示默认头像
                    .into(holder.imageView);
        }
        //设置微视 的 点击事件     1，图片 ； 2 ，视频； 3，音频
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //记录点击位置
                clickPos = i;
                currentHolder = holder;

                if (visionType == 1){
                    Intent intent = new Intent(mContext, PlayPictureActivity.class);
                    //intent.putExtra("MicroVideo",microVideo);
                    MyApplication.microVideo = microVideo;
                    mContext.startActivity(intent);
                }else if (visionType == 2){
                    //Intent intent = new Intent(mContext, PlayMicroVideoActivity.class);
                    Intent intent = new Intent(mContext, MicroVideoPlayActivity.class);
                    intent.putExtra("flag", 2); //2 表示，微视频 来自个人页面和发现精彩
                    //intent.putExtra("MicroVideo",microVideo);
                    MyApplication.microVideo = microVideo;
                    mContext.startActivity(intent);
                }else if (visionType == 3){
                    Intent intent = new Intent(mContext, PlayAudioActivityNew.class);
                    //intent.putExtra("MicroVideo",microVideo);
                    MyApplication.microVideo = microVideo;
                    mContext.startActivity(intent);
                }
            }
        });

        //加载头像
        Glide.with(MyApplication.getContext()).load(microVideo.userHeadImg)
                .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                .into(holder.circleIma);
        //跳转到用户详情页
        holder.circleIma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, UserDetailsActivity.class);
                intentAc.putExtra("userId",microVideo.userId);
                intentAc.putExtra("userName",microVideo.userName);
                mContext.startActivity(intentAc);
            }
        });

    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            int position = holder.getLayoutPosition();
            int type = getItemViewType(position);
            if (type == 0 || type == 1 || type == 2) {
                params.setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }else if (position == 1){
            return 1;
        }else if (position == 2){
            return 2;
        }else {
            return 3;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //刷新当前点击的微视
    public void refreshData(){
        if (clickPos >= 0) {
            //notifyItemChanged(clickPos);
            if (currentHolder != null){
                if (clickPos < dataList.size()) {
                    currentHolder.textPlayTimes.setText(Utils.formatPlayTimes2(dataList.get(clickPos).visionBrowseTimes));
                }
            }
        }
    }

}
