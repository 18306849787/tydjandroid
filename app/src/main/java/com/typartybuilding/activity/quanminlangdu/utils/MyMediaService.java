package com.typartybuilding.activity.quanminlangdu.utils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

public class MyMediaService extends Service {
    private MediaPlayer mediaPlayer;
    private MusicBinder musicBinder;
    private boolean isSetData;                    //是否设置资源

    //播放模式
    public static final int SINGLE_CYCLE = 1;     //单曲循环
    public static final int ALL_CYCLE = 2;        //全部循环
    public static final int RANDOM_PLAY = 3;      //随机播放

    private int MODE;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化数据
        isSetData = false;
        MODE = ALL_CYCLE;
        mediaPlayer = new MediaPlayer();
        musicBinder = new MusicBinder();
    }

    private  void initMusic(String path){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isSetData = true;
    }
    private void SeekTo(int time){
        mediaPlayer.seekTo(time);
    }

    private void prepareAsync(MediaPlayer.OnPreparedListener onPreparedListener){
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(onPreparedListener);
    }

    private void playMusic(MediaPlayer.OnCompletionListener onCompletionListener) {
        try {
            //播放结束监听
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            isSetData = false;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
           // mediaPlayer.stop();
            mediaPlayer.release();
        }
        isSetData = false;
    }

    public class MusicBinder extends Binder {

        public void init(String path){
            initMusic(path);
        }

        public void prepareAsyncData(MediaPlayer.OnPreparedListener onPreparedListener){
            prepareAsync(onPreparedListener);
        }

        //开始播放
        public void start(MediaPlayer.OnCompletionListener onCompletionListener){
            playMusic(onCompletionListener);
        }
        public void seekto(int time){
            SeekTo(time);
        }

        public void onDestroy(){
            MyMediaService.this.onDestroy();
        }
        //获取资源状态
        public boolean isSetData(){
            return isSetData;
        }

        //获取当前播放状态
        public boolean isPlaying(){
            return mediaPlayer.isPlaying();
        }

        //继续播放
        public boolean play(){
            if (isSetData) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
            return mediaPlayer.isPlaying();
        }
        public void setVolume(float data1,float data2){
            mediaPlayer.setVolume(data1,data2);
        }
        //暂停播放
        public boolean pause(){
            if (isSetData) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
            return mediaPlayer.isPlaying();
        }

        /**
         * 获取歌曲当前时长位置
         * 如果返回-1，则mediaplayer没有缓冲歌曲
         * @return
         */
        public int getCurrent(){
            if (isSetData) {
                return mediaPlayer.getCurrentPosition();
            } else {
                return -1;
            }
        }

        /**
         * 获取歌曲总时长
         * 如果返回-1，则mediaplayer没有缓冲歌曲
         * @return
         */
        public int getDuration(){
            if (isSetData) {
                return mediaPlayer.getDuration();
            } else {
                return -1;
            }
        }

        //获取当前播放模式
        public int getMode(){
            return MODE;
        }

        /**
         * 更换播放模式
         * 单曲循环 → 全部循环 → 随机播放 → 单曲循环
         */
        public int changeMode(){
            switch (MODE) {
                case SINGLE_CYCLE:
                    MODE = ALL_CYCLE;
                    break;

                case ALL_CYCLE:
                    MODE = RANDOM_PLAY;
                    break;

                case RANDOM_PLAY:
                    MODE = SINGLE_CYCLE;
                    break;

                default:
            }
            return MODE;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }


}
