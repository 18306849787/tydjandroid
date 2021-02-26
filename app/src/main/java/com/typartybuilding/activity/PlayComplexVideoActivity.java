package com.typartybuilding.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.typartybuilding.R;
import com.typartybuilding.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  用于 合成的视频的预览
 */

public class PlayComplexVideoActivity extends BaseActivity {

    @BindView(R.id.videoView)
    VideoView videoView;

    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_complex_video);
        ButterKnife.bind(this);
        hideStatusBar();
        changeNavigationBar();
        Intent intent = getIntent();
        videoPath = intent.getStringExtra("videoPath");
        playVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null){
            videoView.suspend();
        }
    }

    private void playVideo(){
        videoView.setVideoPath(videoPath);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        //videoView.start();
    }

    @OnClick(R.id.imageButton_back)
    public void onClickBack(){
        finish();
    }

}
