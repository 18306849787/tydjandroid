package com.typartybuilding.activity.quanminlangdu.mac;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coder.media.callback.AudioCallback;
import com.coder.media.utils.AudioHelper;

import com.typartybuilding.R;
import com.typartybuilding.activity.quanminlangdu.dialog.ShowWaitDialog;
import com.typartybuilding.activity.quanminlangdu.dialog.VerticalSeekBar;
import com.typartybuilding.activity.quanminlangdu.entity.BookEntity;
import com.typartybuilding.activity.quanminlangdu.entity.MusicEntity;
import com.typartybuilding.activity.quanminlangdu.utils.Config;
import com.typartybuilding.activity.quanminlangdu.utils.DownloadUtil;
import com.typartybuilding.activity.quanminlangdu.utils.FileUtils;
import com.typartybuilding.activity.quanminlangdu.utils.MyMediaService;
import com.typartybuilding.activity.quanminlangdu.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class EditRecordActivity extends AppCompatActivity {
    private View mView;
    private ImageButton backBtn;
    private LinearLayout musicPlay;
    private SeekBar musicPlayProcess;
    private TextView musicPlayTime;

    private VerticalSeekBar userSeekBar;
    private VerticalSeekBar bgmSeekBar;

    private ImageButton reRecondBtn;
    private Button nextBtn;
    private BookEntity bookEntity;
    private MusicEntity musicEntity;
    private String audioRecondPath;
    private MediaPlayer bgmPlayer;
    private MediaPlayer audioPlayer;
    private boolean isFirst = true;
    private int audioTime;
    private int seektime;
    private int nowtime = 0;
    private Runnable runnable;
    private String fileName;
    private ShowWaitDialog waitDialog;
    private MusicConnector connector;
    private AudioHelper audioHelper;
    private MyMediaService.MusicBinder musicBinder;
    private TextView textView;

    private boolean isCanChangeFileType = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    int data = (int) msg.obj;
                 //   audioPlayer.setVolume((float)(data/100.0),(float)(data/100.0));
                    musicBinder.setVolume((float)(data/100.0),(float)(data/100.0));
                    break;
                case 2:
                    int data2 = (int) msg.obj;
                    bgmPlayer.setVolume((float)(data2/100.0),(float)(data2/100.0));
                    break;
                case 3:
                    int obj = (int) msg.obj;
                 //   audioPlayer.seekTo(obj);
                    musicBinder.seekto(obj);
                    break;
                case Config.AUDIO_PLAY_TIME:
                    musicPlayTime.setText((String)msg.obj);
                    musicPlayProcess.setProgress(nowtime);
                    break;
                case 5:
                    musicPlay.setSelected(false);
                    if (bgmPlayer != null && bgmPlayer.isPlaying()){
                        bgmPlayer.stop();
                    }
                    bgmPlayer.release();
                    bgmPlayer = null;
                    unbindService(connector);
                    waitDialog.dismiss();
                    String filepath = (String) msg.obj;
                    Intent intent = new Intent(EditRecordActivity.this,ReadInfoActivity.class);
                    intent.putExtra("path",filepath);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("book",bookEntity);
                    intent.putExtra("book",bundle);
                    startActivity(intent);
                    break;
                case 6:
                    String text = (String) msg.obj;
                    waitDialog.setSmallText(text);
                    Log.i("TAG",text);
                    break;
                case 7:
                    waitDialog.dismiss();
                    Toast.makeText(EditRecordActivity.this,"合成取消，请重新合成",Toast.LENGTH_SHORT).show();
                    audioHelper = null;
                    break;
                case 8:
                    waitDialog.dismiss();
                    Toast.makeText(EditRecordActivity.this,"合成错误请检查网络，并重新合成",Toast.LENGTH_SHORT).show();
                    audioHelper = null;
                    Log.i("TAG",(String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        init();
        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
                isCanChangeFileType = true;
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
            }
        });
    }

    private void init(){
        mView = findViewById(R.id.mView);
        backBtn = findViewById(R.id.btnBack);
        musicPlay = findViewById(R.id.end_music_play);
        musicPlayProcess = findViewById(R.id.music_process_seek);
        musicPlayTime = findViewById(R.id.music_process_time);
        userSeekBar = findViewById(R.id.user_seek);
        bgmSeekBar = findViewById(R.id.bgm_seek);
        reRecondBtn = findViewById(R.id.re_recond);
        nextBtn = findViewById(R.id.next_btn);
        mView = findViewById(R.id.mView);
        textView = findViewById(R.id.edit_title);
        Intent intent = new Intent(EditRecordActivity.this,MyMediaService.class);
        connector = new MusicConnector();
        bindService(intent,connector, Context.BIND_AUTO_CREATE);



        //musicBinder = new MyMediaService().onBind(intent);
        audioRecondPath = this.getIntent().getStringExtra("file_path");
        Bundle bundle = this.getIntent().getBundleExtra("data");
        bookEntity = (BookEntity) bundle.get("book");
        musicEntity = (MusicEntity) bundle.get("bgm");

       // startService(intent);
        textView.setText(bookEntity.getReadTitle());
        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        int stateHigh = 0;

        if (Utils.hasNotchAtHuawei(this)){
            stateHigh = Utils.getNotchSizeAtHuawei(EditRecordActivity.this)[1];
        }
        if (Utils.hasNotchAtOPPO(EditRecordActivity.this)){
            stateHigh = 80;
        }
        if (Utils.hasNotchAtVivo(EditRecordActivity.this)){
            stateHigh = Utils.dip2px(EditRecordActivity.this,27);
        }
        if (Utils.hasNotchAtMI(EditRecordActivity.this)){
            stateHigh = 89;
        }

        if (stateHigh>result){
            result = stateHigh;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,result);
        mView.setBackgroundColor(Color.parseColor("#ffffff"));
        mView.setLayoutParams(params);
        Utils.translucentStatusBar(EditRecordActivity.this,true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        musicPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicPlay.isSelected()){
                    musicPlay.setSelected(false);
                    //zhan ting bo fang
                    //audioPlayer.pause();
                    musicBinder.pause();
                    bgmPlayer.pause();
                }else {
                    musicPlay.setSelected(true);
                    seektime = musicPlayProcess.getProgress();
                    nowtime = seektime;
                    Log.i("TAG","data is seek time:"+seektime);
                    musicBinder.start(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (bgmPlayer.isPlaying()){

                                bgmPlayer.pause();
                            }
                            musicPlay.setSelected(false);
                            nowtime = 0;
                            audioTime = musicBinder.getDuration();
                            musicPlayTime.setText(getTimeText(nowtime,audioTime));
                            musicPlayProcess.setProgress(nowtime);
                        }
                    });
                    musicBinder.seekto(seektime);
                    int audio = userSeekBar.getProgress();
                    int bgm = bgmSeekBar.getProgress();
                    musicBinder.setVolume((float)(audio/100.0),(float)(audio/100.0));
                    if (musicEntity!=null){
                        bgmPlayer.start();
                        bgmPlayer.seekTo(seektime);
                        bgmPlayer.setVolume((float)(bgm/100.0), (float)(bgm/100.0));
                    }

                }
            }
        });

        reRecondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicEntity!=null){
                    bgmPlayer.stop();
                    bgmPlayer.release();
                }
                musicPlay.setSelected(false);
                unbindService(connector);
                EditRecordActivity.this.finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bgmPlayer != null){
                    if (bgmPlayer.isPlaying()){
                        bgmPlayer.stop();
                    }
                    bgmPlayer.release();
                }
                musicPlay.setSelected(false);
                EditRecordActivity.this.finish();
            }
        });




        musicPlayProcess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //bo播放
                //audioPlayer.seekTo(progress);
                //musicBinder.seekto(progress);seektime = seekBar.getProgress();
                String text = getTimeText(progress,audioTime);
                musicPlayTime.setText(text);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seektime = seekBar.getProgress();

                if (musicBinder != null){
                    musicBinder.seekto(seekBar.getProgress());
                }
                nowtime = seekBar.getProgress();
                String text = getTimeText(seektime,audioTime);
                musicPlayTime.setText(text);
                if (musicBinder.isPlaying()){
                    musicBinder.seekto(musicPlayProcess.getProgress());
                    musicPlayTime.setText(getTimeText(musicPlayProcess.getProgress(),audioTime));
                }

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitDialog = new ShowWaitDialog(EditRecordActivity.this,null);
                waitDialog.setCanceledOnTouchOutside(false);
                waitDialog.show();
                waitDialog.setSmallText("正在加载背景音乐");

                if (isCanChangeFileType){

                    File flacFile = new File(audioRecondPath);
                    FileUtils.setMp3();
                    AndroidAudioConverter.with(EditRecordActivity.this)
                            .setFile(flacFile)
                            .setFormat(AudioFormat.MP3)
                            .setCallback(new IConvertCallback() {
                                @Override
                                public void onSuccess(File convertedFile) {
                                    Log.i("TAG","转换路径："+convertedFile.toString());
                                    DownloadUtil.get().download(EditRecordActivity.this, musicEntity.getBgmUrl(), FileUtils.getMp3FilePath(), new DownloadUtil.OnDownloadListener() {
                                        @Override
                                        public void onDownloadSuccess(String fileMp3Name) {
                                            Log.i("TAG","mp3 file is:"+FileUtils.getMP3FileAbsolutePath(fileMp3Name));
                                            fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".mp3";
                                            if (audioPlayer == null){
                                                audioHelper = new AudioHelper.Builder(EditRecordActivity.this)
                                                        .setSrc(convertedFile.toString()).setCutStartTime(0)
                                                        .setCutDuration(20).setMix(FileUtils.getMP3FileAbsolutePath(fileMp3Name))
                                                        .setReduceMix((float) (bgmSeekBar.getProgress()/100.0))
                                                        .setTarget(FileUtils.getMP3FileAbsolutePath(fileName))
                                                        .setCallback(new AudioCallback() {
                                                            @Override
                                                            public void onFinish() {
                                                                super.onFinish();
                                                                String path = FileUtils.getMP3FileAbsolutePath(fileName);
                                                                Message msg = new Message();
                                                                msg.what = 5;
                                                                msg.obj = path;
                                                                mHandler.sendMessage(msg);
                                                            }
                                                            @Override
                                                            public void onError(String s) {
                                                                super.onError(s);
                                                                Log.i("TAG","error:"+s);
                                                                Message msg = new Message();
                                                                msg.what = 8;
                                                                mHandler.sendMessage(msg);
                                                            }
                                                            @Override
                                                            public void onProgress(int i) {
                                                                super.onProgress(i);
                                                                Log.i("TAG","this is doing:"+i);
                                                                Message msg = new Message();
                                                                msg.what = 6;
                                                                msg.obj = "正在合成 "+i+"%";
                                                                mHandler.sendMessage(msg);
                                                                //Toast.makeText(EditRecordActivity.this,"正在合成",Toast.LENGTH_SHORT).show();
                                                            }

                                                            @Override
                                                            public void onCancel() {
                                                                super.onCancel();
                                                                Message msg = new Message();
                                                                msg.what = 7;
                                                                mHandler.sendMessage(msg);
                                                            }
                                                        }).build();
                                                audioHelper.startMixAudio();
                                                Message msg = new Message();
                                                msg.what = 6;
                                                msg.obj = "正在合成...";
                                                mHandler.sendMessage(msg);
                                            }

                                        }

                                        @Override
                                        public void onDownloading(int progress) {
                                            Message msg = new Message();
                                            msg.what = 6;
                                            msg.obj = "正在下载 "+progress+"%";
                                            mHandler.sendMessage(msg);
                                        }

                                        @Override
                                        public void onDownloadFailed(String e) {
                                            Message msg = new Message();
                                            msg.what = 8;
                                            msg.obj = "download fail"+e;
                                            mHandler.sendMessage(msg);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Exception error) {
                                    Log.i("TAG",error.toString());
                                }
                            })
                            .convert();

                }else {
                    if (audioPlayer == null){
                        fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".mp3";
                        audioHelper = new AudioHelper.Builder(EditRecordActivity.this)
                                .setSrc(audioRecondPath).setCutStartTime(0)
                                .setCutDuration(20).setMix(musicEntity.getBgmUrl())
                                .setReduceMix((float) (bgmSeekBar.getProgress()/100.0))
                                .setTarget(FileUtils.getAllAudioFilePath(fileName))
                                .setCallback(new AudioCallback() {
                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        String path = FileUtils.getAllAudioFilePath(fileName);
                                        Message msg = new Message();
                                        msg.what = 5;
                                        msg.obj = path;
                                        mHandler.sendMessage(msg);
                                    }
                                    @Override
                                    public void onError(String s) {
                                        super.onError(s);
                                        Log.i("TAG","error:"+s);
                                        Message msg = new Message();
                                        msg.what = 8;
                                        mHandler.sendMessage(msg);
                                    }
                                    @Override
                                    public void onProgress(int i) {
                                        super.onProgress(i);
                                        Log.i("TAG","this is doing:"+i);
                                        Message msg = new Message();
                                        msg.what = 6;
                                        msg.obj = "正在合成 "+i+"%";
                                        mHandler.sendMessage(msg);
                                        //Toast.makeText(EditRecordActivity.this,"正在合成",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancel() {
                                        super.onCancel();
                                        Message msg = new Message();
                                        msg.what = 7;
                                        mHandler.sendMessage(msg);
                                    }
                                }).build();
                        audioHelper.startMixAudio();
                    }
                }
            }
        });

        userSeekBar.setOnProgressChanged(new VerticalSeekBar.ProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = progress;
                mHandler.sendMessage(msg);
            }
        });

        bgmSeekBar.setOnProgressChanged(new VerticalSeekBar.ProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                Message msg = new Message();
                msg.what = 2;
                msg.obj = progress;
                mHandler.sendMessage(msg);
            }
        });

        bgmPlayer = new MediaPlayer();
      //  audioPlayer = new MediaPlayer();
       // audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        bgmPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                bgmPlayer.start();
                bgmPlayer.setLooping(true);
            }
        });
       // audioPlayer.setOnCompletionListener();



        try {
            if (musicEntity != null){
                bgmPlayer.setDataSource(musicEntity.getBgmUrl());
                bgmPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (musicPlay.isSelected()){
                nowtime += 100;
                String time = getTimeText(nowtime,audioTime);
                Message msg = new Message();
                msg.what = Config.AUDIO_PLAY_TIME;
                msg.obj = time;
                mHandler.sendMessage(msg);
            }
        }
    };

    private String getTimeText(int nowtime,int alltime){
        int allmuint = alltime/1000/60;
        int allsec = (alltime/1000)%60;
        int nmint = nowtime/1000/60;
        int nsec = (nowtime/1000)%60;
        StringBuilder builder = new StringBuilder();
        if (nmint<10){
            builder.append(0).append(nmint).append(":");
        }else {
            builder.append(nmint).append(":");
        }
        if (nsec<10){
            builder.append(0).append(nsec).append("/");
        }else {
            builder.append(nsec).append("/");
        }

        if (allmuint<10){
            builder.append(0).append(allmuint).append(":");
        }else {
            builder.append(allmuint).append(":");
        }
        if (allsec<10){
            builder.append(0).append(allsec);
        }else {
            builder.append(allsec);
        }

        return builder.toString();
    }

    private class MusicConnector implements ServiceConnection {
        //成功绑定时调用 即bindService（）执行成功同时返回非空Ibinder对象
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicBinder = (MyMediaService.MusicBinder) iBinder;
            Log.i("TAG","binservice success");
            musicBinder.init(audioRecondPath);
            musicBinder.prepareAsyncData(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    audioTime = musicBinder.getDuration();
                    musicPlayProcess.setMax(audioTime);
                    musicPlayTime.setText(getTimeText(0,audioTime));
                    timer.schedule(timerTask,0,100);
                }
            });
        }

        //不成功绑定时调用
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("binding is fail", "binding is fail");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (musicBinder != null){
            try {
                musicBinder.pause();
            }catch (Exception e){}

        }
        if (bgmPlayer != null){
            try {
                if (bgmPlayer.isPlaying()){
                    bgmPlayer.pause();
                }
            }catch (Exception e){}

        }
        musicPlay.setSelected(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bgmPlayer != null){
            try {
                if (bgmPlayer.isPlaying()){
                    bgmPlayer.pause();
                }
                bgmPlayer.release();
                bgmPlayer = null;
            }catch (Exception e){

            }

        }
        try {
            musicBinder.onDestroy();
        }catch (Exception e){}

    }
}
