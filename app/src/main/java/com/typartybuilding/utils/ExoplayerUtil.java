package com.typartybuilding.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.TextureView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.typartybuilding.R;

import io.reactivex.exceptions.UndeliverableException;

public class ExoplayerUtil {

    private String TAG = "ExoplayerUtil";

    private String videoUrl;      //视频链接
    private SimpleExoPlayerView playerView;
    private TextureView textureView;

    private Context context;

    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private SimpleExoPlayer.VideoListener mVideoListener;
    private Player.EventListener mEventListener;
    private boolean isLoop = false;          //是否循环播放，默认不循环

    private boolean isPlay;   //是否在播放


    public ExoplayerUtil(TextureView textureView, Context context) {
        this.textureView = textureView;
        this.context = context;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }


    //根据raw资源ID获取URL 字符串
    private Uri getUriById() {
        String videoStr = null;
        //videoStr = "asset:///ty_launch_video.mp4";
        return Uri.parse(videoStr);
    }

    /**
     * 播放本地视频
     */
    public void initPlayerForNative(){
        //本地视频
        Uri videoRes = getUriById();

        //playerView.requestFocus();
        if (textureView != null) {
            textureView.requestFocus();
        }

        //创建带宽对象
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        //根据当前宽带来创建选择磁道工厂对象
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        //传入工厂对象，以便创建选择磁道对象
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //根据选择磁道创建播放器对象
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        //将player和view绑定
        //playerView.setPlayer(player);
        player.setVideoTextureView(textureView);

        //定义数据源工厂对象
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "ExoPlayerDemo"));
        //创建Extractor工厂对象，用于提取多媒体文件
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        //创建数据源
        mediaSource = new ExtractorMediaSource(
                videoRes,
                mediaDataSourceFactory, extractorsFactory, null, null);

        //添加数据源到播放器中
        player.prepare(mediaSource);

    }

    /**
     * 播放 网络视频
     */
    public void initPlayer(){
        //playerView.requestFocus();
        Log.i(TAG, "initPlayer: textureView : " + textureView);
        if (textureView != null) {
            textureView.requestFocus();
        }

        //创建带宽对象
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        //根据当前宽带来创建选择磁道工厂对象
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        //传入工厂对象，以便创建选择磁道对象
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //根据选择磁道创建播放器对象
        try {
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        }catch (UndeliverableException e){
            Log.i(TAG, "initPlayer: e : " + e);
            e.printStackTrace();
        }

        //将player和view绑定
        //playerView.setPlayer(player);
        player.setVideoTextureView(textureView);

        //定义数据源工厂对象
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "ExoPlayerDemo"));
        //创建Extractor工厂对象，用于提取多媒体文件
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        //创建数据源
        mediaSource = new ExtractorMediaSource(
                Uri.parse(videoUrl),
                mediaDataSourceFactory, extractorsFactory, null, null);
        //是否循环播放
        if (isLoop){
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
        }
        //添加数据源到播放器中
        player.prepare(mediaSource);
        //player.setPlayWhenReady(shouldAutoPlay);

    }

    //添加 事件监听
    public void addEventListener(Player.EventListener eventListener){
        mEventListener = eventListener;
        if (player != null){
            player.addListener(mEventListener);
        }
    }

    //添加视频监听
    public void addVideoListener(SimpleExoPlayer.VideoListener videoListener){
        mVideoListener = videoListener;
        if (player != null) {
            player.addVideoListener(mVideoListener);
        }
    }


    public void seekTo(long currentPosition){
        if (player != null){
            player.seekTo(currentPosition);
        }
    }

    public void release(){
        if (player != null) {
            if (mVideoListener != null){
                player.removeVideoListener(mVideoListener);
                Log.i(TAG, "release: removeVideoListener()");
            }
            if (mEventListener != null){
                player.removeListener(mEventListener);
                Log.i(TAG, "release: removeListener()");
            }
            player.release();
            player = null;
        }
    }

    //播放完成，
    public void repeatPlay(){
        if (player != null){
            player.setPlayWhenReady(false);
            player.prepare(mediaSource,true,false);
            isPlay = false;
        }
    }

    public long getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public long getDuration(){
        return player.getDuration();
    }

    //设置 播放 和 暂停
    public void setPlayWhenReady(boolean bool){
        if (player != null) {
            player.setPlayWhenReady(bool);
            isPlay = bool;
        }
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public boolean isPlaying(){
        return isPlay;
    }

}
