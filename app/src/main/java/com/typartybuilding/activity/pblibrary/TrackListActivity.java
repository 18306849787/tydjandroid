package com.typartybuilding.activity.pblibrary;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.PblAlbumListAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.PblTrackListAdapter;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.pblibrary.AlbumData;
import com.typartybuilding.bean.pblibrary.TrackData;
import com.typartybuilding.utils.Utils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.util.NetworkType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrackListActivity extends BaseActivity {

    private String TAG = "TrackListActivity";

    @BindView(R.id.imageView_back)
    ImageView imgBack;
    @BindView(R.id.imageView_headline)
    ImageView imageView;
    @BindView(R.id.textView_play_times)
    TextView playTimes;
    @BindView(R.id.textView_headline)
    TextView headLine;
    @BindView(R.id.textView_subhead)
    TextView subHead;
    @BindView(R.id.recyclerView_audio_playlist_ac)
    RecyclerView recyclerView;


    private AlbumData albumData;
    private PblTrackListAdapter adapter;
    private List<TrackData> dataList = new ArrayList<>();

    private boolean isError;           //标记获取音频是否成功
    private TrackList mTrackList;
    private List<Track> tracks = new ArrayList<>();
    private int page = 1;              //返回第几页，必须大于等于1，不填默认为1
    private int count = 200;           //每页多少条，默认20，最多不超过200
    private String sort = "asc";    //sort	String	否	"asc"表示喜马拉雅正序，"desc"表示喜马拉雅倒序，"time_asc"表示时间升序，"time_desc"表示时间降序，默认为"asc"

    public PopupWindow popupWindow;  //底部弹窗
    public View popView;             //弹窗布局

    public ImageButton imgBtnBack;      //返回按钮
    public TextView popHeadLine;     //标题
    public ImageView popImageView ;  //音频图片
    public TextView nowTime;         //播放到什么时间和
    public TextView duration;        //音频时长
    public SeekBar seekBar ;                  //进度条
    public ImageButton lastBtn ;     //上一曲
    public ImageButton playBtn ;     //播放
    public ImageButton nextBtn ;     //下一曲
    private ProgressBar mProgress;   //缓冲的进度条
    public  XmPlayerManager playerManager;
    private boolean mUpdateProgress = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play_list);
        ButterKnife.bind(this);

        initPlayerManager();
        initPopupWindow();
        //获取上一个activity传递过来的album数据
        Intent intent = getIntent();
        albumData = (AlbumData)intent.getSerializableExtra("album_data");

        initRecyclerView();
        initView();
        getTrackList();
    }

    @Override
    protected void onDestroy() {
        if (playerManager != null) {
            playerManager.removePlayerStatusListener(mPlayerStatusListener);
            playerManager.release();
        }
        //XmPlayerManager.release();

        CommonRequest.release();
        super.onDestroy();
    }


    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PblTrackListAdapter(tracks,this);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.imageView_back)
    public void onClickBack(){
        finish();
    }

    private void initView(){

        //加载图片
        Glide.with(this).load(albumData.imgUrl)
                .apply(MyApplication.requestOptions11)  //url为空或异常显示默认图片
                .into(imageView);
        int count = Integer.parseInt(albumData.playTimes);
        int times = count/10000;
        if (times != 0){
            float playTimes1 = ((float) count)/10000;
            float playTimes2 = ((float) Math.round(playTimes1*10))/10;
            playTimes.setText(playTimes2 + "万");

        }else {
           playTimes.setText(albumData.playTimes);
        }
        //playTimes.setText(albumData.playTimes);
        headLine.setText(albumData.headLine);
        subHead.setText(albumData.subHead);
    }

    public void refresh() {
        Log.e(TAG, "---refresh");
        if (hasMore()) {
            getTrackList();
        }
    }

    private boolean hasMore() {
        if (mTrackList != null && mTrackList.getTotalPage() <= page) {
            return false;
        }
        return true;
    }

    private void getTrackList(){
        if (isError){
            return;
        }
        Log.i(TAG, "getTrackList: ");
        Map<String ,String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID ,albumData.albumId+"");
        map.put(DTransferConstants.PAGE ,page+"");
        map.put(DTransferConstants.PAGE_SIZE,count+"");
        map.put(DTransferConstants.SORT,sort);
        Log.i(TAG, "getTrackList: ");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {

                Log.i(TAG, "onSuccess: ");
                if (trackList != null && trackList.getTracks() != null && trackList.getTracks().size() != 0) {
                    int totalPage = trackList.getTotalPage();
                    Log.i(TAG, "onSuccess: totalPage : " + totalPage);
                    Log.i(TAG, "onSuccess: currentPage : " + page);
                    Log.i(TAG, "onSuccess: size : " + trackList.getTracks().size() );
                    if (mTrackList == null) {
                        mTrackList = trackList;
                    } else {
                        mTrackList.getTracks().addAll(trackList.getTracks());
                    }

                    //更新数据，刷新屏幕
                    if (tracks != null) {
                        tracks.addAll(trackList.getTracks());
                        adapter.notifyDataSetChanged();
                    }

                    if (page < totalPage){
                        page++;
                        getTrackList();
                    }

                    Log.i(TAG, "onSuccess: tracks size : " + tracks.size());
                    //initData(tracks);
                }
                isError = false;
            }

            @Override
            public void onError(int i, String s) {
                isError = true;
            }
        });
    }


    public void showPopupWindow(){
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //initPlayerManager();
        }
    }

    /**
     * 初始化 播放音频的弹窗
     */
    private void initPopupWindow(){

        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_track_play, null);
        imgBtnBack = popView.findViewById(R.id.imageButton_back);      //返回按钮
        popHeadLine = popView.findViewById(R.id.textView_headline);      //标题
        popImageView = popView.findViewById(R.id.imageView_track_pic);  //音频图片
        nowTime = popView.findViewById(R.id.textView_time);         //播放到哪里
        duration = popView.findViewById(R.id.textView_duration);    //时长
        mProgress = popView.findViewById(R.id.progress_bar);
        seekBar = popView.findViewById(R.id.seekBar);                  //进度条
        lastBtn = popView.findViewById(R.id.imageButton_last);     //上一曲
        playBtn = popView.findViewById(R.id.imageButton_play);     //播放
        nextBtn = popView.findViewById(R.id.imageButton_next);     //下一曲

        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //设置弹窗动画效果
        popupWindow.setAnimationStyle(R.style.pbl_popwindow);

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (playerManager.isPlaying()) {
                    playerManager.pause();
                    playBtn.setSelected(false);
                    //playBtn.setImageResource(R.mipmap.djsw_btn_playyp);
                }

                //播放弹窗，关闭，释放播放器
              /*  if (playerManager != null) {
                    playerManager.removePlayerStatusListener(mPlayerStatusListener);
                    playerManager.release();
                }*/
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUpdateProgress = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playerManager.seekToByPercent(seekBar.getProgress() / (float) seekBar.getMax());
                mUpdateProgress = true;
            }
        });
        //上一曲
        lastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerManager.playPre();
            }
        });
        //播放
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerManager.isPlaying()) {
                    playerManager.pause();
                    //playBtn.setImageResource(R.mipmap.djsw_btn_playyp);
                    playBtn.setSelected(false);
                } else {
                    playerManager.play();
                    //playBtn.setImageResource(R.mipmap.djsw_btn_zanting);
                    playBtn.setSelected(true);
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerManager.playNext();
            }
        });

    }

    /**
     * 初始化 音频播放器
     */
    private void initPlayerManager(){

        playerManager = XmPlayerManager.getInstance(this);
        playerManager.init();

       /* Notification mNotification = XmNotificationCreater.getInstanse(this).initNotification(this.getApplicationContext(), TrackListActivity.class);
        playerManager.init((int) System.currentTimeMillis(), mNotification);*/

        playerManager.addPlayerStatusListener(mPlayerStatusListener);
        playerManager.addAdsStatusListener(mAdsListener);
        playerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                playerManager.removeOnConnectedListerner(this);

                playerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
                //Toast.makeText(TrackListActivity.this, "播放器初始化成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
            Log.i(TAG, "onSoundPrepared");
            seekBar.setEnabled(true);
            //playBtn.setImageResource(R.mipmap.djsw_btn_zanting);
            //playBtn.setSelected(true);
            //播放按钮可点击
            playBtn.setEnabled(true);
            mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            Log.i(TAG, "onSoundSwitch index:" + curModel);
            PlayableModel model = playerManager.getCurrSound();
            if (model != null) {
                Track track = (Track) model;
                //设置标题
                popHeadLine.setText(track.getTrackTitle());
                //加载图片
                Glide.with(TrackListActivity.this).load(track.getCoverUrlLarge())
                        .apply(MyApplication.requestOptions11)  //url为空或异常显示默认图片
                        .into(popImageView);
                //设置 播放时间
                String str = Utils.formatTime(track.getDuration());
                duration.setText(str);
            }

        }

       /* private void updateButtonStatus() {
            if (mPlayerManager.hasPreSound()) {
                mBtnPreSound.setEnabled(true);
            } else {
                mBtnPreSound.setEnabled(false);
            }
            if (mPlayerManager.hasNextSound()) {
                mBtnNextSound.setEnabled(true);
            } else {
                mBtnNextSound.setEnabled(false);
            }
        }*/

        @Override
        public void onPlayStop() {
            Log.i(TAG, "onPlayStop");
            //playBtn.setImageResource(R.drawable.widget_play_normal);
            playBtn.setSelected(false);

        }

        @Override
        public void onPlayStart() {
            Log.i(TAG, "onPlayStart");
            //playBtn.setImageResource(R.drawable.widget_pause_normal);
            playBtn.setSelected(true);
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            Log.i(TAG, "onPlayProgress: currPos : " + currPos);
            Log.i(TAG, "onPlayProgress: durtion : " + duration);
            nowTime.setText(Utils.formatTimeMs(currPos));
            if (mUpdateProgress && duration != 0) {
                seekBar.setProgress((int) (100 * currPos / (float) duration));
            }
        }

        @Override
        public void onPlayPause() {
            Log.i(TAG, "onPlayPause");
            playBtn.setSelected(false);

        }

        @Override
        public void onSoundPlayComplete() {
            Log.i(TAG, "onSoundPlayComplete");

        }

        @Override
        public boolean onError(XmPlayerException exception) {
            Log.i(TAG, "XmPlayerException = onError " + exception.getMessage());

            System.out.println("MainFragmentActivity.onError   "+ exception);

            if(!NetworkType.isConnectTONetWork(TrackListActivity.this)) {
                Toast.makeText(TrackListActivity.this, "没有网络导致停止播放", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        @Override
        public void onBufferProgress(int position) {
            seekBar.setSecondaryProgress(position);
            System.out.println("MainFragmentActivity.onBufferProgress   " + position);
        }

        public void onBufferingStart() {
            seekBar.setEnabled(false);
            playBtn.setEnabled(false);
            //mProgress.setVisibility(View.VISIBLE);
        }

        public void onBufferingStop() {
            seekBar.setEnabled(true);
            playBtn.setEnabled(true);
            //mProgress.setVisibility(View.GONE);
        }
    };

    private IXmAdsStatusListener mAdsListener = new IXmAdsStatusListener() {

        @Override
        public void onStartPlayAds(Advertis ad, int position) {
            Log.i(TAG, "onStartPlayAds, Ad:" + ad.getName() + ", pos:" + position);

        }

        @Override
        public void onStartGetAdsInfo() {
            Log.i(TAG, "onStartGetAdsInfo");

        }

        @Override
        public void onGetAdsInfo(final AdvertisList ads) {

        }

        @Override
        public void onError(int what, int extra) {
            Log.i(TAG, "onError what:" + what + ", extra:" + extra);
        }

        @Override
        public void onCompletePlayAds() {
            Log.i(TAG, "onCompletePlayAds");

        }

        @Override
        public void onAdsStopBuffering() {
            Log.i(TAG, "onAdsStopBuffering");
        }

        @Override
        public void onAdsStartBuffering() {
            Log.i(TAG, "onAdsStartBuffering");
        }
    };



    /*  private void initData(List<Track> trackList){
        for (int i = 0; i < trackList.size(); i++){
            Track track = trackList.get(i);
            TrackData trackData = new TrackData(track.getCoverUrlLarge(),track.getDataId(),track.getTrackTitle(),
                    track.getPlayCount(),track.getDuration(),track.getUpdatedAt());
            dataList.add(trackData);
        }
        Log.i(TAG, "initData: dataList size : " + dataList.size());

        adapter.notifyDataSetChanged();

    }
*/



}
