package com.typartybuilding.activity.quanminlangdu.mac;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.typartybuilding.R;
import com.typartybuilding.activity.quanminlangdu.dialog.MyReadScrollView;
import com.typartybuilding.activity.quanminlangdu.dialog.ShowTimeDialog;
import com.typartybuilding.activity.quanminlangdu.entity.BookEntity;
import com.typartybuilding.activity.quanminlangdu.entity.MusicEntity;
import com.typartybuilding.activity.quanminlangdu.fragment.dummy.DummyContent;
import com.typartybuilding.activity.quanminlangdu.utils.AudioRecorder;
import com.typartybuilding.activity.quanminlangdu.utils.Config;
import com.typartybuilding.activity.quanminlangdu.utils.FileUtils;
import com.typartybuilding.activity.quanminlangdu.utils.RecordStreamListener;
import com.typartybuilding.activity.quanminlangdu.utils.SimplePlayerUtils;
import com.typartybuilding.activity.quanminlangdu.utils.Utils;
import com.typartybuilding.utils.UserUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

@RuntimePermissions
public class ReadActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private LinearLayout fabuBtn;
    private View mView;
    private LinearLayout bgmImageBtn;
    private RelativeLayout bgmListBtn;
    private LinearLayout bgmPlayBtn;
    private TextView bgmName;

    private LinearLayout shiboBtn;
    private LinearLayout beginBtn;
    private LinearLayout resetBtn;

    private TextView shiboBtnText;
    private TextView beginBtnText;
    private TextView resetBtnText;

    private AudioRecorder audioRecorder = AudioRecorder.getInstance();
    private String fileName;
    private boolean isOne = true;
    private boolean isOneTimeToRecond = true;


    private SoundPool mSoundPool;
    private String readId;
    private MediaPlayer player;
    private MusicEntity musicEntity;
    private BookEntity bookEntity;
    private boolean bgmGo = false;
    private ShowTimeDialog showTimeDialog;
    private TextView bookTitle;
    private TextView bookAuthor;
    private TextView bookDetail;

    private TextView timeTextView;
    private MyReadScrollView scrollView;
    private GifImageView gifImageView;
    private GifDrawable gifDrawable;

    private int audioTime = 0;
    private int playTime = 0;
    private int playTimeAll = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.MUSIC_SEARCH_ITEM_SUCCESS:
                    List<MusicEntity> entities = (List<MusicEntity>) msg.obj;
                    if (entities == null ){
                        return;
                    }
                    if (entities.size()>0){
                        musicEntity = entities.get(0);
                        bgmName.setText(musicEntity.getBgmName());
                        if (bgmGo){
                            if (player != null){
                                if (player.isPlaying()){
                                    player.stop();
                                }
                                player.release();
                                player = null;
                            }
                        }
                        bgmPlayBtn.setSelected(false);
                        bgmImageBtn.setSelected(false);
                        bgmGo = false;
                    }
                    break;
                case Config.SEARCH_BOOK_DETAIL:
                    BookEntity entity = (BookEntity) msg.obj;
                    if (entity == null){
                        return;
                    }else {
                        bookEntity = entity;
                        bookTitle.setText(entity.getReadTitle());
                        bookAuthor.setText("作者:"+entity.getReadAuthor());
                        if (entity.getReadType()==1){
                            bookDetail.setGravity(Gravity.CENTER_HORIZONTAL);
                        }else if (entity.getReadType()==2){
                            bookDetail.setGravity(Gravity.LEFT);
                        }
                        bookDetail.setText(entity.getReadDetail());
                    }
                    break;
                case Config.MUSIC_CHOOSE_RESULT_OK:
                    MusicEntity music = (MusicEntity) msg.obj;
                    musicEntity = music;
                    bgmName.setText(musicEntity.getBgmName());
                    break;
                case Config.AUDIO_PLAY_OVER:
                    shiboBtn.setSelected(false);
                    shiboBtn.setBackgroundResource(R.drawable.ldt_btn_shiboyes);
                    shiboBtnText.setText("试播");
                    gifDrawable.stop();
                    isOne = true;
                    isOneTimeToRecond = true;
                    playTime = 0;
                    break;
                case Config.AUDIO_PLAY_TIME:
                    String time = (String) msg.obj;
                    timeTextView.setText(time);
                    break;
                case 10011:
                    gifDrawable.pause();
                    Toast.makeText(ReadActivity.this,"录音到达最大时间",Toast.LENGTH_SHORT).show();
                    beginBtn.setSelected(false);
                    beginBtn.setEnabled(false);
                    beginBtnText.setText("停止录音");
                    shiboBtn.setEnabled(true);
                    shiboBtnText.setEnabled(true);
                    resetBtn.setEnabled(true);
                    resetBtnText.setEnabled(true);
                    if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START){
                        audioRecorder.pauseRecord();
                        //audioRecorder.stopRecord();
                        //audioRecorder.release();
                    }
                    if (bgmGo){
                        bgmGo = false;
                        bgmPlayBtn.setSelected(false);
                        bgmImageBtn.setBackgroundResource(R.drawable.ldt_icon_play);
                        if (player.isPlaying()){
                            player.pause();
                        }

                    }
                    shiboBtnText.setText("试播");
                    shiboBtn.setBackgroundResource(R.drawable.ldt_btn_shiboyes);
                    Log.i("TAG","录音已到最大时长");
                    break;
                case 10012:
                    int lineNum = (int) msg.obj;
                    if (lineNum < bookDetail.getLineCount()){
                        Layout layout = bookDetail.getLayout();
                        int start = layout.getLineStart(lineNum);
                        int end = layout.getLineEnd(lineNum);
                        String text = layout.getText().toString();
                        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
                        spannable.setSpan(new ForegroundColorSpan(Color.RED),start,end
                                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        bookDetail.setText(spannable);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_read);
       // ButterKnife.bind(this);
        init();
    }

    @SuppressLint("WrongViewCast")
    private void init(){
        mView = findViewById(R.id.mView);
        backBtn = findViewById(R.id.btnBack);
        fabuBtn = findViewById(R.id.fabo_btn);
        fabuBtn.setEnabled(false);
        bgmImageBtn = findViewById(R.id.bgn_icon);
        Bundle bundle = getIntent().getBundleExtra("data");
        readId = ((DummyContent.DummyItem)bundle.get("book")).readId;
        bookEntity = DummyItemToBook((DummyContent.DummyItem)bundle.get("book"));
        bgmListBtn = findViewById(R.id.bgm_list);
        bgmPlayBtn = findViewById(R.id.bgm_play_btn);
        bgmName = findViewById(R.id.music_name);
        shiboBtn = findViewById(R.id.shibo_play);
        shiboBtnText = findViewById(R.id.shibo_play_text);
        beginBtn = findViewById(R.id.begin_recond);
        beginBtnText = findViewById(R.id.begin_recond_text);
        resetBtn = findViewById(R.id.reset_btn);
        resetBtnText = findViewById(R.id.reset_btn_text);
        shiboBtn.setEnabled(false);
        shiboBtnText.setEnabled(false);
        timeTextView = findViewById(R.id.timeGo);
        resetBtn.setEnabled(false);
        resetBtnText.setEnabled(false);
        scrollView = findViewById(R.id.read_scroll_view);
        bookDetail = findViewById(R.id.book_detail);
        bookAuthor = findViewById(R.id.book_author);
        bookTitle = findViewById(R.id.book_tile);
        gifImageView = findViewById(R.id.is_gif);
        if (bookEntity.getReadType()==1){
            bookDetail.setGravity(Gravity.CENTER_HORIZONTAL);
        }else if (bookEntity.getReadType()==2){
            bookDetail.setGravity(Gravity.LEFT);
        }
        bookDetail.setText(bookEntity.getReadDetail());
        bookAuthor.setText("作者:"+bookEntity.getReadAuthor());
        bookTitle.setText(bookEntity.getReadTitle());
        if (mSoundPool == null){
            createSoundPoolIfNeeded();
        }
        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        int stateHigh = 0;
        if (Utils.hasNotchAtHuawei(this)){
            stateHigh = Utils.getNotchSizeAtHuawei(ReadActivity.this)[1];
        }
        if (Utils.hasNotchAtOPPO(ReadActivity.this)){
            stateHigh = 80;
        }
        if (Utils.hasNotchAtVivo(ReadActivity.this)){
            stateHigh = Utils.dip2px(ReadActivity.this,27);
        }
        if (Utils.hasNotchAtMI(ReadActivity.this)){
            stateHigh = 89;
        }
        if (stateHigh>result){
            result = stateHigh;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,result);
        mView.setBackgroundColor(Color.parseColor("#ffffff"));
        mView.setLayoutParams(params);
        Utils.translucentStatusBar(ReadActivity.this,true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        /**
         *重录监听
         * */
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START){
                    audioRecorder.pauseRecord();
                    audioRecorder.stopRecord();
                    audioRecorder.release();
                }
                if (player!=null){
                    player.release();
                    player = null;
                }
                playTime = 0;
                audioTime = 0;
                String timeText = getTimeText(playTime,1000*60*90);
                timeTextView.setText(timeText);
                shiboBtn.setSelected(false);
                shiboBtn.setBackgroundResource(R.drawable.ldt_btn_shibo);
                shiboBtnText.setText("试播");
                fabuBtn.setClickable(false);

                isOneTimeToRecond = true;
                beginBtn.setEnabled(true);
                beginBtn.setSelected(false);

                beginBtnText.setText("准备录音");
//                shiboBtn.setBackgroundResource(R.drawable.ldt_btn_shiboyes);
                audioRecorder.release();
                try {
                    gifDrawable = new GifDrawable(getResources(),R.drawable.yin);
                    gifImageView.setImageDrawable(gifDrawable);
                    gifDrawable.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });



        /**
         *
         * 录音监听
         * */
        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabuBtn.setEnabled(true);
                resetBtn.setEnabled(true);
                gifDrawable.pause();
                if (beginBtn.isSelected()){
                    beginBtn.setSelected(false);
                    beginBtnText.setText("停止录音");
                    shiboBtn.setEnabled(true);
                    shiboBtnText.setEnabled(true);
                    resetBtn.setEnabled(true);
                    resetBtnText.setEnabled(true);
                    audioRecorder.pauseRecord();
                    if (bgmGo){
                        bgmGo = false;
                        bgmPlayBtn.setSelected(false);
                        bgmImageBtn.setBackgroundResource(R.drawable.ldt_icon_play);
                        if (player.isPlaying()){
                            player.pause();
                        }

                    }
                    shiboBtnText.setText("试播");
                    shiboBtn.setBackgroundResource(R.drawable.ldt_btn_shiboyes);
                }else {

                    resetBtn.setEnabled(false);

                    shiboBtn.setEnabled(false);
                    shiboBtn.setSelected(false);
                    shiboBtn.setBackgroundResource(R.drawable.ldt_btn_shibo);
                    fabuBtn.setEnabled(false);

                    if (isOneTimeToRecond){
                        showTimeDialog = new ShowTimeDialog(ReadActivity.this, new ShowTimeDialog.OnTimeOverListener() {
                            @Override
                            public void OnTimeOverToDo() {
                                beginBtn.setSelected(true);
                                if (bgmGo){
                                    if (!player.isPlaying()){
                                        player.start();
                                    }
                                }

                                beginBtnText.setText("正在录音");
                                isOne = true;
                                isOneTimeToRecond = false;
                                initAudio();
                                audioRecorder.startRecord(new RecordStreamListener() {
                                    @Override
                                    public void recordOfByte(byte[] data, int begin, int end) {
                                        Log.i("TAG","lu yin ing ........");
                                    }
                                });
                                audioTime = 0;
                                try {
                                    gifDrawable = new GifDrawable(getResources(),R.drawable.yin);
                                    gifImageView.setImageDrawable(gifDrawable);
                                    gifDrawable.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        showTimeDialog.setCanceledOnTouchOutside(false);
                        showTimeDialog.show();

                    }else {

                        if (player != null){
                            if (!bgmGo){
                                player.release();
                            }
                        }
                        beginBtn.setSelected(true);
                        beginBtnText.setText("正在录音");
                        isOne = true;
//                        initAudio();
//                        audioRecorder.startRecord(new RecordStreamListener() {
//                            @Override
//                            public void recordOfByte(byte[] data, int begin, int end) {
//                                Log.i("TAG","lu yin ing ........");
//                            }
//                        });
                        audioRecorder.startRecord(new RecordStreamListener() {
                            @Override
                            public void recordOfByte(byte[] data, int begin, int end) {
                                Log.i("TAG","lu yin ing ........");
                            }
                        });
                        gifDrawable.start();
                        //shiboBtn.setBackgroundResource(R.drawable.ldt_btn_shiboyes);
                    }
                }
            }
        });
        /***
         * 试播监听
         * **/
        shiboBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shiboBtn.isSelected()) {
                    shiboBtn.setSelected(false);
                    if (player != null){
                        player.pause();
                    }
                    beginBtn.setEnabled(true);
                    shiboBtn.setBackgroundResource(R.drawable.ldt_btn_shiboyes);
                    shiboBtnText.setText("停止播放");
                    gifDrawable.pause();
                }else {
                    if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START || audioRecorder.getStatus() == AudioRecorder.Status.STATUS_PAUSE){
                        audioRecorder.stopRecord();
                        audioRecorder.release();
                    }
                    shiboBtn.setSelected(true);

                    shiboBtn.setBackgroundResource(R.drawable.ldt_btn_shibopause);
                    //onPlay(0);
                    bgmGo = false;
                    isOneTimeToRecond = true;
                    shiboBtnText.setText("正在播放");
                    beginBtn.setEnabled(false);
                    if (!isOne&&player!=null&&gifDrawable!=null){
                        gifDrawable.start();
                        player.start();
                    }else {
                        if (player != null){
                            player.release();
                        }

                        String path = FileUtils.getWavFileAbsolutePath(fileName);
                        Date date = new Date();
                        while (new Date().getTime() - date.getTime() < 700){ }
                        player = SimplePlayerUtils.getMediaPlayer(path);
                        while (new Date().getTime() - date.getTime() < 1000){ }
                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Message msg = new Message();
                                msg.what = Config.AUDIO_PLAY_OVER;
                                mHandler.sendMessage(msg);
                            }
                        });
                        playTimeAll = player.getDuration();
                        playTime = 0;
                        player.start();
                        player.setVolume((float)1,(float)1);

                        gifDrawable.stop();
                        gifDrawable.start();

                        isOne = false;
                    }
                }
            }
        });
        searchBookDetail(readId,UserUtils.getIns().getToken());
        searchMusic(UserUtils.getIns().getToken());
        timer.schedule(timerTask,0,100);
        bgmPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bgmPlayBtn.isSelected()){
                    bgmPlayBtn.setSelected(false);
                    bgmImageBtn.setBackgroundResource(R.drawable.ldt_icon_play);
                    if (player != null ){
                        player.pause();
                    }
                    player.pause();
                }else {
                    if (bgmGo){
                        player.start();
                    }else {
                        if (musicEntity == null){
                            return;
                        }
                        if (player != null){
                            //player.stop();
                            player.release();
                        }

                        player = SimplePlayerUtils.getMediaPlayer(musicEntity.getBgmUrl());
                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                player.start();
                            }
                        });
                        player.start();
                        player.setVolume((float)0.4,(float) 0.4);
                        bgmGo = true;
                    }
                    bgmPlayBtn.setSelected(true);
                    bgmImageBtn.setBackgroundResource(R.drawable.ldt_btn_bgm_pause);
                }

            }
        });

        bgmListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bgmGo){
                    if (player != null){
                        if (player.isPlaying()){
                            player.stop();
                            player.release();
                            player = null;
                        }
                    }
                    bgmPlayBtn.setSelected(false);
                    bgmImageBtn.setBackgroundResource(R.drawable.ldt_icon_play);
                    bgmGo = false;
                }
                Intent intent = new Intent(ReadActivity.this,MusicListActivity.class);
                intent.putExtra("readId",readId);
                startActivityForResult(intent,Config.MUSIC_CHOOSE_REQUEST);
            }
        });

        fabuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabuBtn.setEnabled(false);
                if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START || audioRecorder.getStatus() == AudioRecorder.Status.STATUS_PAUSE){
      //            audioRecorder.pauseRecord();
                    audioRecorder.stopRecord();
                    audioRecorder.release();
                }
                if (player != null){
                    player.release();
                    player = null;
                }
                player = null;
                Intent intent = new Intent(ReadActivity.this,EditRecordActivity.class);
                if (fileName.equals("")){
                    Toast.makeText(ReadActivity.this,"尚未录音",Toast.LENGTH_SHORT).show();
                    return;
                }

                playTime = 0;
                audioTime = 0;
                String timeText = getTimeText(playTime,1000*60*90);
                timeTextView.setText(timeText);
                shiboBtn.setSelected(false);
                shiboBtnText.setText("试播");
                isOneTimeToRecond = true;
                beginBtn.setSelected(false);
                beginBtnText.setText("准备录音");
                shiboBtn.setBackgroundResource(R.drawable.ldt_btn_shiboyes);
                audioRecorder.release();

                String path = FileUtils.getWavFileAbsolutePath(fileName);
                intent.putExtra("file_path",path);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bgm",musicEntity);
                bundle.putSerializable("book",bookEntity);
                intent.putExtra("data",bundle);
                fabuBtn.setEnabled(true);
                startActivity(intent);

            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRecorder.release();
                if (player != null){
                    player.release();
                }
                ReadActivity.this.finish();
            }
        });

        /**
         * gif
         * */
        try {
            gifDrawable = new GifDrawable(getResources(),R.drawable.yin);
            gifImageView.setImageDrawable(gifDrawable);
            gifDrawable.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /***
         *
         * read TextView scrollView
         *
         * **/
        scrollView.setOnScrollListener(new MyReadScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                Log.i("TAG","scroll view y:"+scrollY);
                Log.i("TAG","text view line count is:"+bookDetail.getLineCount());
                Log.i("TAG","text view line height :"+bookDetail.getLineHeight());
                Log.i("TAG","text view height:"+bookDetail.getHeight());
                int lineHeight = bookDetail.getLineHeight();
                int lineCount = bookDetail.getLineCount();
                Message msg = new Message();
                msg.what = 10012;
                int changeLine = scrollY / lineHeight;
                if (changeLine>lineCount){
                    msg.obj = changeLine;
                    mHandler.sendMessage(msg);
                    return;
                }
                if ((scrollY%lineHeight)>lineHeight/2){
                    changeLine++;
                }
                msg.obj = changeLine;
                mHandler.sendMessage(msg);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == Config.MUSIC_CHOOSE_REQUEST){
            if (Config.MUSIC_CHOOSE_RESULT_OK == resultCode){
                Bundle bundle = data.getBundleExtra("result");
                MusicEntity entity = (MusicEntity) bundle.getSerializable("music");
                Message msg = new Message();
                msg.what = Config.MUSIC_CHOOSE_RESULT_OK;
                msg.obj = entity;
                mHandler.sendMessage(msg);
            }
        }
    }

    private void createSoundPoolIfNeeded() {
        if (mSoundPool == null) {
            // 5.0 及 之后
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = null;
                audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

                mSoundPool = new SoundPool.Builder()
                        .setMaxStreams(16)
                        .setAudioAttributes(audioAttributes)
                        .build();
            } else { // 5.0 以前
                mSoundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);  // 创建SoundPool
            }
        }
    }
    /**
     * 初始化录音  申请录音权限
     */
    @NeedsPermission({Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    public void initAudio(){
        this.fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        audioRecorder.createDefaultAudio(this.fileName);
    }

    @OnShowRationale({Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationaleForRecord(final PermissionRequest request){
        new AlertDialog.Builder(this)
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage("是否开启录音权限")
                .show();
    }

    @OnPermissionDenied({Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRecordDenied(){
        Toast.makeText(ReadActivity.this,"拒绝录音权限将无法进行挑战",Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain({Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    void onRecordNeverAskAgain() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 2016/11/10 打开系统设置权限
                        dialog.cancel();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage("您已经禁止了录音权限,是否现在去开启")
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (player != null){
                if (player.isPlaying()){
                    player.pause();
                }
            }
        }catch (Exception e){

        }

        if (bgmGo){
            bgmPlayBtn.setSelected(false);
            bgmImageBtn.setBackgroundResource(R.drawable.ldt_icon_play);
        }
        if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START){
            audioRecorder.pauseRecord();
            isOneTimeToRecond = false;
            beginBtn.setSelected(false);
            gifDrawable.pause();
            beginBtnText.setText("暂停录音");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null){
            player.release();
            player = null;
        }
        if (audioRecorder != null){
            audioRecorder.release();
            audioRecorder = null;
        }

    }

    private void searchMusic(String token){
        String url = Config.getActionUrl(Config.GET_BGM_LIST);
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("token",token);
        formBody.add("pageNo","1");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                try {
                    JSONObject object = new JSONObject(data);
                    List<MusicEntity> entities = paseJsonToList(object);
                    Message msg = new Message();
                    msg.what = Config.MUSIC_SEARCH_ITEM_SUCCESS;
                    msg.obj = entities;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private List<MusicEntity> paseJsonToList(JSONObject object) throws JSONException {
        if (object.getInt("code") != 0){
            return null;
        }

        JSONObject dataObject = object.getJSONObject("data");
        List<MusicEntity> musicEntities = new ArrayList<>();
        JSONArray array = dataObject.getJSONArray("rows");

        for (int i=0; i < array.length(); i++){
            JSONObject obj = (JSONObject) array.get(i);
            MusicEntity entity = new MusicEntity(obj.getString("bgmId"),obj.getInt("bgmDuration"),obj.getString("bgmImg"),obj.getString("bgmName"),
                    obj.getString("bgmUrl"),obj.getInt("createTime"),obj.getString("bgmProfile"));
            musicEntities.add(entity);
        }

        return musicEntities;
    }

    private void searchBookDetail(String readId,String token){
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("readId",readId);
        formBody.add("token",token);
        String url = Config.getActionUrl(Config.GET_BOOK_DETAIL);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                try {
                    JSONObject object = new JSONObject(data);
                    BookEntity entity = pareJsonToBean(object);
                    Message msg = new Message();
                    msg.what = Config.SEARCH_BOOK_DETAIL;
                    msg.obj = entity;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private BookEntity pareJsonToBean(JSONObject object){
        try {
            if (object.getInt("code") != 0){
                return null;
            }
            JSONObject obj = object.getJSONObject("data");
            BookEntity entity = new BookEntity(obj.getString("examineStatus"),obj.getString("examineUid"),obj.getString("examineUser"),
                    obj.getInt("publishDate"),obj.getString("readAuthor"),obj.getString("readCover"),obj.getString("readDetail"),
                    obj.getString("readFrequency"),obj.getString("readId"),obj.getString("readNumber"),obj.getString("readProfile"),
                    obj.getString("readTitle"),object.optString("rejectCause"),obj.getInt("updateTime")
            ,obj.getInt("readType"),obj.optString("fileUrl"));
            return entity;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private BookEntity DummyItemToBook(DummyContent.DummyItem dummyItem){
        BookEntity entity = new BookEntity();
        entity.setPublishDate(Long.valueOf(dummyItem.publishDate));
        entity.setReadAuthor(dummyItem.readAuthor);
        entity.setReadDetail(dummyItem.readDetail);
        entity.setReadFrequency(dummyItem.readFrequency);
        entity.setReadId(dummyItem.readId);
        entity.setReadCover(dummyItem.readCover);
        entity.setReadNumber(dummyItem.readNumber);
        entity.setReadProfile(dummyItem.readProfile);
        entity.setReadTitle(dummyItem.readTitle);
        entity.setUpdateTime(Long.valueOf(dummyItem.updateTime));
        entity.setReadType(dummyItem.readType);
        return entity;
    }

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
//            if (audioTime >= 1000*6){
//                audioTime = 1000*6;
//                Message msg = new Message();
//                msg.what = 10011;
//                mHandler.sendMessage(msg);
//            }
            if (beginBtn.isSelected()){
                audioTime += 100;
                if (audioTime >= 1000*60*90){
                    audioTime = 1000*60*90;
                    Message msg = new Message();
                    msg.what = 10011;
                    mHandler.sendMessage(msg);
                }
                String time = getTimeText(audioTime,1000*60*90);
                Message msg = new Message();
                msg.what = Config.AUDIO_PLAY_TIME;
                msg.obj = time;
                mHandler.sendMessage(msg);
            }

            if (shiboBtn.isSelected()){
                playTime += 100;
                if (playTime>playTimeAll){
                    playTime = playTimeAll;
                }
                String time = getTimeText(playTime,playTimeAll);
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
}
