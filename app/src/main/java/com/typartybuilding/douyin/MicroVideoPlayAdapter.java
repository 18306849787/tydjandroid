package com.typartybuilding.douyin;

import android.content.Intent;
import android.media.session.PlaybackState;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.typartybuilding.R;
import com.typartybuilding.activity.UserDetailsActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.ExoplayerUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.NetworkStateUtil;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MicroVideoPlayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "MicroVideoPlayAdapter";

    private int myUserId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    //private List<String> urlList = new ArrayList<>();
    private List<MicroVideo> dataList = new ArrayList<>();
    private MicroVideoPlayActivity activity;

    private boolean isFirst = true;          //是否是第一次进入，微视播放
    private ExoplayerUtil currentPlayer;     //当前播放的播放器
    private ViewHolder currentHolder;        //当前播放的ViewHolder

    private List<ExoplayerUtil> exoList = new ArrayList<>(); //存放ExoplayerUtil，页面销毁时，全部销毁


    public MicroVideoPlayAdapter(List<MicroVideo> dataList, MicroVideoPlayActivity activity) {
        this.dataList = dataList;
        this.activity = activity;
    }

    public ExoplayerUtil getCurrentPlayer() {
        return currentPlayer;
    }

    public ViewHolder getCurrentHolder() {
        return currentHolder;
    }

    public List<ExoplayerUtil> getExoList() {
        return exoList;
    }

    public void removeAllExo(){
        exoList.removeAll(exoList);
    }

    public void setExoListNull(){
        exoList = null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        //视频播放
        @BindView(R.id.imageView_back)
        ImageView imgBack;             //返回按钮
        @BindView(R.id.textureView)
        TextureView textureView;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;
        @BindView(R.id.imageView)
        ImageView imageView;          //封面图
        @BindView(R.id.imagebutton_play)
        ImageButton btnPlay;          //播放按钮

        //用户信息
        @BindView(R.id.circle_img_head)
        CircleImageView headImg;       //头像
        @BindView(R.id.imageView_attention)
        ImageView attention;           //关注
        @BindView(R.id.textView_like)
        TextView textLike;             //点赞数量，或播放量
        @BindView(R.id.imagebutton_share)
        ImageButton btnShare;          //分享
        @BindView(R.id.textView_name)
        TextView textName;             //姓名
        @BindView(R.id.textView_abstract)
        TextView textAbstrack;         //视频简介

        private View view;

        private ExoplayerUtil exoplayerUtil;
        private float ratio;               //视频宽高比

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_micro_video,
                viewGroup, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        ViewHolder viewHolder = (ViewHolder)holder;
        //设置微视 的播放
        setMicVideoPlay(viewHolder,i);
        //初始化页面
        initView(viewHolder,i);
        //设置点击事件
        setOnClick(viewHolder,i);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //初始化页面布局
    private void initView(ViewHolder holder, int pos){
        MicroVideo microVideo = dataList.get(pos);
        if (microVideo != null) {
            holder.textName.setText(microVideo.userName);
            holder.textAbstrack.setText(microVideo.visionTitle);
            holder.textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum));
            //Log.i(TAG, "initView: microVideo.visionPraisedNum : " + microVideo.visionPraisedNum);
            //是否已关注
            if (microVideo.isFollowed == 1){
                holder.attention.setVisibility(View.INVISIBLE);
            }else {
                holder.attention.setVisibility(View.VISIBLE);
            }
            //点赞
            //Log.i(TAG, "initView: microVideo.isPraise : " + microVideo.isPraise);
            if (microVideo.isPraise == 1){
                holder.textLike.setSelected(true);
            }else {
                holder.textLike.setSelected(false);
            }

            //加载头像
            Glide.with(activity).load(microVideo.userHeadImg)
                    .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                    .into(holder.headImg);
            //加载视频封面
            Glide.with(activity).load(microVideo.videoCover)
                    .apply(MyApplication.requestOptions916)  //url为空或异常显示默认头像
                    .into(holder.imageView);
            //添加浏览历史记录，更新浏览量/播放量 1：咨询文章，2：党建微视
            //Log.i(TAG, "initView: microVideo.visionId : " + microVideo.visionId);
            RetrofitUtil.browseMicro(microVideo.visionId);

        }
    }

    //设置点击事件
    private void setOnClick(ViewHolder holder, int pos){
        MicroVideo microVideo = dataList.get(pos);
        //点击头像，跳转到详情
        holder.headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType == 3){
                    MyApplication.remindVisitor(activity);
                }else {
                    Intent intent = new Intent(activity, UserDetailsActivity.class);
                    intent.putExtra("userId", microVideo.userId);
                    intent.putExtra("userName", microVideo.userName);
                    activity.startActivity(intent);
                }
            }
        });

        // 分享
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType == 1 || userType == 2) {
                    String visionTitle = microVideo.visionTitle;
                    if (visionTitle == null || visionTitle == ""){
                        visionTitle = activity.getResources().getString(R.string.mic_defaul_title);
                    }
                    JshareUtil.showBroadView(activity,visionTitle,"",microVideo.visionFileUrl,
                            2,microVideo.visionId);

                }else if (userType == 3){
                    MyApplication.remindVisitor(activity);
                }
            }
        });

        //关注
        holder.attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType == 1 || userType == 2) {
                    RetrofitUtil.addFocus(microVideo.userId, microVideo.userName, microVideo.userHeadImg,
                            holder.attention,dataList,pos,MicroVideoPlayAdapter.this);
                    microVideo.isFollowed = 1;
                }else if (userType == 3){
                    MyApplication.remindVisitor(activity);
                }
            }
        });

        //点赞
        holder.textLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisionPraise(microVideo,holder);
            }
        });
    }

    //设置点赞
    private void setVisionPraise(MicroVideo microVideo,ViewHolder holder){
        if (userType == 1 || userType == 2) {
            if (NetworkStateUtil.isNetWorkConnected(activity)) {
                if (microVideo != null) {
                    //取消点赞
                    if (holder.textLike.isSelected()) {
                        holder.textLike.setSelected(false);
                        if (microVideo.isPraise == 0) {
                            holder.textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum));
                            RetrofitUtil.cancelPraise(microVideo.visionId, microVideo.userId);
                        } else {
                            //点赞数减1, 以前点过赞
                            holder.textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum - 1));
                            RetrofitUtil.cancelPraise(microVideo.visionId, microVideo.userId);
                        }
                        microVideo.isPraise = 0;
                        microVideo.visionPraisedNum += -1;

                    } else {
                        //点赞
                        holder.textLike.setSelected(true);
                        if (microVideo.isPraise == 1) {
                            holder.textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum));
                            RetrofitUtil.microPraise(microVideo.visionId, microVideo.userId);
                        } else {
                            //点赞数加1
                            holder.textLike.setText(Utils.formatPlayTimes2(microVideo.visionPraisedNum + 1));
                            RetrofitUtil.microPraise(microVideo.visionId, microVideo.userId);
                        }
                        microVideo.isPraise = 1;
                        microVideo.visionPraisedNum += 1;
                    }
                }
            }else {
                RetrofitUtil.requestError();
            }

        }else if (userType == 3){
            MyApplication.remindVisitor(activity);
        }
    }

    private void setMicVideoPlay(ViewHolder holder, int pos){
        //Log.i(TAG, "setMicVideoPlay: pos : " + pos);
        holder.exoplayerUtil = new ExoplayerUtil(holder.textureView,activity);

        //设置播放器
        setExoplayer(holder,dataList.get(pos).visionFileUrl);
        //设置屏幕点击事件
        doubleOnClick(holder,pos);
       /* holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.exoplayerUtil.isPlaying()){
                    holder.exoplayerUtil.setPlayWhenReady(false);
                    holder.btnPlay.setVisibility(View.VISIBLE);
                }else {
                    holder.exoplayerUtil.setPlayWhenReady(true);
                    holder.btnPlay.setVisibility(View.INVISIBLE);
                }
            }
        });*/

        //设置播放按钮的点击事件
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.exoplayerUtil.setPlayWhenReady(true);
                holder.btnPlay.setVisibility(View.INVISIBLE);
            }
        });
        //设置返回按钮的点击事件
        holder.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        if (pos == dataList.size()-2){
            activity.getMoreMicroVideo();
            //Log.i(TAG, "setMicVideoPlay: 获取数据");
        }

    }

    //双击点赞, 单击 暂停或播放
    private void doubleOnClick(ViewHolder holder,int pos){
        MicroVideo microVideo = dataList.get(pos);

        holder.view.setOnTouchListener(new DoubleClickListener(new DoubleClickListener.OnClickCallBack() {
            @Override
            public void oneClick() {
                if (holder.exoplayerUtil.isPlaying()){
                    holder.exoplayerUtil.setPlayWhenReady(false);
                    holder.btnPlay.setVisibility(View.VISIBLE);
                }else {
                    holder.exoplayerUtil.setPlayWhenReady(true);
                    holder.btnPlay.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void doubleClick() {
                if (microVideo.isPraise != 1) {
                    setVisionPraise(microVideo, holder);
                }
            }
        }));
    }

    //设置exoplayer
    private void setExoplayer(ViewHolder holder,String videoUrl){
        //Log.i(TAG, "setExoplayer: videoUrl : " + videoUrl);
        //传入视频url
        holder.exoplayerUtil.setVideoUrl(videoUrl);
        //设置循环播放
        holder.exoplayerUtil.setLoop(true);
        //初始化播放器
        holder.exoplayerUtil.initPlayer();

        //播放按钮不可见
        holder.btnPlay.setVisibility(View.INVISIBLE);

        holder.exoplayerUtil.addEventListener(new Player.EventListener() {
//            @Override
//            public void onTimelineChanged(Timeline timeline, Object manifest) {
//
//            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState){
                    case PlaybackState.STATE_PLAYING :
                        //Log.i(TAG, "onPlayerStateChanged: start play");
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        //封面图不可见
                        holder.imageView.setVisibility(View.INVISIBLE);

                        break;
                    case PlaybackState.STATE_PAUSED :
                        holder.progressBar.setVisibility(View.VISIBLE);
                        break;
                    //播放完成
                    case PlaybackState.STATE_FAST_FORWARDING :
                        Log.i(TAG, "onPlayerStateChanged: STATE_FAST_FORWARDING  ");
                        break;

                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

//            @Override
//            public void onPositionDiscontinuity() {
//
//            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
        
        holder.exoplayerUtil.addVideoListener(new SimpleExoPlayer.VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                //Log.i(TAG, "onVideoSizeChanged: width : " + width);
                //Log.i(TAG, "onVideoSizeChanged: height : " + height);
                holder.ratio = (float)width/(float)height;
                //Log.i(TAG, "onVideoSizeChanged: ratio : " + holder.ratio);
                refreshVideoViewSize(holder,width,height);
            }

            @Override
            public void onRenderedFirstFrame() {
                Log.i(TAG, "onRenderedFirstFrame: 1");
                //遍历集合，暂停其他还在播放的视频
                //Log.i(TAG, "onRenderedFirstFrame: exoList size : " + exoList.size());
               /* for (ExoplayerUtil exoplayerUtil : exoList){
                    if (exoplayerUtil != currentHolder.exoplayerUtil) {
                        if (exoplayerUtil.isPlaying()) {
                            //exoplayerUtil.setPlayWhenReady(false);
                            exoplayerUtil.repeatPlay();
                        }
                        //exoplayerUtil.repeatPlay();
                        exoplayerUtil.setPlayWhenReady(false);

                    }
                }*/
            }
            
        });

    }

    //根据视频尺寸，重新设置TextureView的宽高；
    private void refreshVideoViewSize(ViewHolder holder,int w,int h ){
        //Log.i(TAG, "refreshVideoViewSize: ratio : " + holder.ratio);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.textureView.getLayoutParams();
        int width = Utils.getScreenWidth();   //屏幕宽度
        if (holder.ratio >= 1){
            params.height = (int) (width/holder.ratio);
            params.width = width;
            holder.textureView.setLayoutParams(params);
        }else {
           /* params.width = activity.getScreenWidth();
            params.height = activity.getScreenHeight();
            holder.textureView.setLayoutParams(params);*/
            //视频是竖屏时，也按视频宽高比播放，不全屏播放
            params.height = (int) (width/holder.ratio);
            params.width = width;
            holder.textureView.setLayoutParams(params);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.i(TAG, "onViewDetachedFromWindow: ");
        ViewHolder viewHolder = (ViewHolder)holder;
        /*if (viewHolder.exoplayerUtil.isPlaying()) {
            viewHolder.exoplayerUtil.setPlayWhenReady(false);
        }*/
        //viewHolder.exoplayerUtil.repeatPlay();
    }


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Log.i(TAG, "onViewAttachedToWindow: ");

        ViewHolder viewHolder = (ViewHolder)holder;
        //第一次进入，需设置自动播放
        if (isFirst) {
            viewHolder.exoplayerUtil.setPlayWhenReady(true);
            isFirst = false;
        }

        if (viewHolder.btnPlay.getVisibility() == View.VISIBLE){
            viewHolder.btnPlay.setVisibility(View.INVISIBLE);
        }
        //记录当前播放的播放器
        currentPlayer = viewHolder.exoplayerUtil;
        //记录当前播放的ViewHolder
        currentHolder = viewHolder;
        //将exoplayerUtil 放入集合
        exoList.add(viewHolder.exoplayerUtil);
        //遍历集合，暂停其他还在播放的视频
        /*for (ExoplayerUtil exoplayerUtil : exoList){
            if (exoplayerUtil != viewHolder.exoplayerUtil) {
                exoplayerUtil.setPlayWhenReady(false);
            }
        }*/
    }

    public void onPageSelected(){
        for (ExoplayerUtil exoplayerUtil : exoList){
            if (exoplayerUtil != currentHolder.exoplayerUtil) {
                if (exoplayerUtil.isPlaying()) {
                    //exoplayerUtil.setPlayWhenReady(false);
                    exoplayerUtil.repeatPlay();
                    Log.i(TAG, "onPageSelected: repeat");
                }
                //exoplayerUtil.repeatPlay();
            }
            exoplayerUtil.setPlayWhenReady(false);
        }
        currentHolder.exoplayerUtil.setPlayWhenReady(true);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        ViewHolder viewHolder = (ViewHolder)holder;
        //将exoplayerUtil 移出集合
        exoList.remove(viewHolder.exoplayerUtil);
        viewHolder.exoplayerUtil.release();
        viewHolder.exoplayerUtil = null;
    }



}
