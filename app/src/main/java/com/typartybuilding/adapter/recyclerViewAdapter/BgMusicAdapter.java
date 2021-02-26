package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.camera.TakeVideoFragment;
import com.typartybuilding.gsondata.bgmusic.BackgroundMusicData;
import com.typartybuilding.view.RoundImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BgMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "BgMusicAdapter";

    private List<BackgroundMusicData.MusicData> dataList = new ArrayList<>();

    private TakeVideoFragment fragment;
    public MediaPlayer mediaPlayer;
    public String musicName;
    public ViewHolder currentHolder;      //当前播放的holder
    public int currentItem = -1;               //当前播放的item


    private static final int TYPE_ITEM_NORMAL = 0;
    private static final int TYPE_ITEM_FOOTER = 1;

    private ViewHolderFooter mHolder;

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

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.round_imageView)
        RoundImageView roundImg;           //背景音乐图片
        @BindView(R.id.textView_name)
        TextView bgName;                  //背景音乐名
        @BindView(R.id.imageButton_shiyong)
        ImageButton btnChoose;            //使用背景音乐的按钮
        @BindView(R.id.imageView_play)
        ImageView imgPlay;                //播放暂停按钮
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        private boolean isStart = false;       //是否已经开始播放了
        private View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    public BgMusicAdapter(List<BackgroundMusicData.MusicData> dataList, Fragment fragment) {
        this.dataList = dataList;
        this.fragment = (TakeVideoFragment) fragment;
        mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM_NORMAL) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_bg_music,
                    viewGroup, false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_load_more2,
                    viewGroup, false);
            mHolder = new ViewHolderFooter(view);
            return mHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        if (type == TYPE_ITEM_NORMAL) {
            ViewHolder holder = (ViewHolder)viewHolder;
            BackgroundMusicData.MusicData musicData = dataList.get(i);

            holder.imgPlay.setVisibility(View.VISIBLE);
            holder.imgPlay.setSelected(false);
            holder.isStart = false;
            holder.progressBar.setVisibility(View.GONE);

            //加载图片
            Glide.with(fragment.getActivity()).load(musicData.bgmImg)
                    .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                    .into(holder.roundImg);
            holder.bgName.setText(musicData.bgmName);

            holder.imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i(TAG, "onClick: bgmUrl : " + musicData.bgmUrl);
                    startPlayMusic(musicData.bgmUrl,holder,i);

                }
            });

            holder.btnChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadMusic(musicData.bgmUrl);
                    musicName = musicData.bgmName;
                    //fragment.hasMusic = true;     //有背景音乐
                }
            });
        }

    }

    private void startPlayMusic(String url,ViewHolder holder,int pos){
        try {
            if (mediaPlayer != null) {
                if (holder.isStart) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        holder.imgPlay.setSelected(false);
                    }else {
                        mediaPlayer.start();
                        holder.imgPlay.setSelected(true);
                    }
                } else {
                    //播放另个，上一个播放的状态需重置
                    if (currentHolder != null){
                        currentHolder.imgPlay.setSelected(false);
                        currentHolder.isStart = false;
                    }
                    mediaPlayer.reset();
                    mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            Log.i(TAG, "onInfo: what : " + what);
                            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                                //fragment.linearLayout.setVisibility(View.VISIBLE);

                            } else {
                                //fragment.linearLayout.setVisibility(View.INVISIBLE);
                            }
                            return false;
                        }
                    });

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            //mediaPlayer.reset();
                        }
                    });

                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepareAsync();
                    //进度条可见，播放按钮不可见
                    holder.imgPlay.setVisibility(View.INVISIBLE);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                            mediaPlayer.setLooping(true);
                            currentHolder = holder;
                            currentItem = pos;
                            holder.isStart = true;
                            holder.imgPlay.setVisibility(View.VISIBLE);
                            holder.imgPlay.setSelected(true);
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        ViewHolder holder = (ViewHolder)viewHolder;
        int itemPos = holder.getLayoutPosition();
        if (itemPos == currentItem){
            holder.isStart = true;
            if (mediaPlayer != null){
                if (mediaPlayer.isPlaying()){
                    holder.imgPlay.setSelected(true);
                }
            }
        }
    }

    public void setTypeItemFooter(){

      /*  mHolder.progressBar.setVisibility(View.INVISIBLE);
        mHolder.textHint.setText("没有更多了");*/
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
    public int getItemViewType(int position) {
        /*if(position + 1 < getItemCount()) return TYPE_ITEM_NORMAL;
        else return TYPE_ITEM_FOOTER;*/
        return TYPE_ITEM_NORMAL;
    }

    @Override
    public int getItemCount() {

        return dataList.size();

    }

    //通知相册更新
    private void refreshVideo(File file){
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            fragment.getActivity().sendBroadcast(intent);
        }
    }

    //下载背景音乐
    private void downloadMusic(String musicUrl) {

        Log.i(TAG, "downloadMusic: ");
        String baseUrl = "http://47.97.126.122:8080/";

        File file = fragment.musicFile;
        if (fragment.musicFile.exists()) {
            fragment.musicFile.delete();
        }

        FileDownloader.setup(fragment.getActivity());

        FileDownloader.getImpl().create(musicUrl)
                .setPath(file.getAbsolutePath())
                .setForceReDownload(true)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        fragment.linearLayout1.setVisibility(View.VISIBLE);
                        //页面不可点击
                        fragment.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Log.i(TAG, "pending: ");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.i(TAG, "progress: soFarBytes : " + soFarBytes);
                        Log.i(TAG, "progress: totalBytes : " + totalBytes);

                        int percent = soFarBytes*100/totalBytes;
                        fragment.textPercent.setText(percent + "%");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.i(TAG, "completed: 完成下载");
                        refreshVideo(file);
                        fragment.linearLayout1.setVisibility(View.INVISIBLE);
                        fragment.popupWindow.dismiss();
                        //恢复见面点击
                        fragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(fragment.getActivity(),"背景音乐下载完成",Toast.LENGTH_SHORT).show();
                        fragment.hasMusic = true;     //有背景音乐
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        //恢复见面点击
                        fragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        fragment.linearLayout1.setVisibility(View.INVISIBLE);
                        Log.i(TAG, "error: e : " + e);
                        e.printStackTrace();
                        Toast.makeText(fragment.getActivity(),"背景音乐下载失败",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();






       /* DownloadHelper downloadHelper = new DownloadHelper(baseUrl, new DownloadListener() {
            @Override
            public void onStartDownload() {
                Log.i(TAG, "onStartDownload: 开始下载");
                fragment.linearLayout1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgress(int i) {
                Log.i(TAG, "onProgress: i : " + i);

            }

            @Override
            public void onFinishDownload(File file) {
                Log.i(TAG, "onFinishDownload: file length : " + file.length());
                Log.i(TAG, "onFinishDownload: file : " + file.getAbsolutePath());
                refreshVideo(file);
                fragment.linearLayout1.setVisibility(View.INVISIBLE);
                fragment.popupWindow.dismiss();

            }

            @Override
            public void onFail(Throwable throwable) {
                Log.i(TAG, "onFail: fail : " + throwable);
            }
        });
        downloadHelper.downloadFile(musicUrl,file.getParent(),file.getName());

*/

        /*try {
            URL url = new URL(musicUrl);
            //打开连接
            URLConnection conn = url.openConnection();
            //打开输入流
            InputStream is = conn.getInputStream();
            //获得长度
            int contentLength = conn.getContentLength();
            Log.e(TAG, "contentLength = " + contentLength);

            if (fragment.musicFile.exists()) {
                fragment.musicFile.delete();
            }
            //创建字节流
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(fragment.musicFile);
            //写数据
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            //完成后关闭流
            Log.e(TAG, "download-finish");
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}



/*  String dirName = Environment.getExternalStorageDirectory() + "/MyDownLoad/";
            File file = new File(dirName);
            //不存在创建
            if (!file.exists()) {
                file.mkdir();
            }
            //下载后的文件名
            String fileName = dirName + "xiaomibianqian.apk";
            File file1 = new File(fileName);*/