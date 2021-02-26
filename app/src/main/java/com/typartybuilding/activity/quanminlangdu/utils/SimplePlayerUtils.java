package com.typartybuilding.activity.quanminlangdu.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

public class SimplePlayerUtils {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static OnPlayOverListener mPlayListener;
    public static void playWithLocal(String filePath,OnPlayOverListener listener) throws IOException {
        mPlayListener = listener;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.prepare();
        mediaPlayer.getDuration();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayListener.playOver();
            }
        });
    }



    public static MediaPlayer getMediaPlayer(String path) {

        try {
            if (mediaPlayer == null){
                mediaPlayer = new MediaPlayer();
            }else {
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
            }

            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void play(){
        if (mediaPlayer!=null){
            mediaPlayer.start();
        }

    }

    public static void puse(){
        if (mediaPlayer!=null){
            mediaPlayer.pause();
        }
    }


    public interface OnPlayOverListener{
        void playOver();
    }
}
