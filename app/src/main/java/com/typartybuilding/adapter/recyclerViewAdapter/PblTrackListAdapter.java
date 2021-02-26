package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.activity.pblibrary.TrackListActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.pblibrary.TrackData;
import com.typartybuilding.utils.Utils;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PblTrackListAdapter extends RecyclerView.Adapter<PblTrackListAdapter.ViewHolder> {

    private String TAG = "PblTrackListAdapter";

    //private List<TrackData> dataList = new ArrayList<>();

    private List<Track> trackList = new ArrayList<Track>();
    private Context mContext;
    private TrackListActivity trackAc;
    private XmPlayerManager playerManager;

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_num)
        TextView textNum;          //排序编号
        @BindView(R.id.textView_headline)
        TextView headLine;
        @BindView(R.id.textView_play_times)
        TextView playTimes;
        @BindView(R.id.textView_play_duration)
        TextView playDuration;
        @BindView(R.id.textView_date)
        TextView textDate;

        private View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    public PblTrackListAdapter(List<Track> trackList, Context mContext) {
        this.trackList = trackList;
        this.mContext = mContext;
        trackAc = (TrackListActivity)mContext;
        playerManager = XmPlayerManager.getInstance(mContext);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.item_recyclerview_pb_library_audiolist,
                viewGroup,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Track trackData = trackList.get(i);
        //TrackData trackData = dataList.get(i);
        int total = trackList.size() ;
        int num = total - i;
        viewHolder.textNum.setText(num+"");
        viewHolder.headLine.setText(trackData.getTrackTitle());
        int playTimes = trackData.getPlayCount()/10000;
        if (playTimes != 0){
            float playTimes1 = ((float) trackData.getPlayCount())/10000;
            float playTimes2 = ((float) Math.round(playTimes1*10))/10;

            viewHolder.playTimes.setText(playTimes2 + "万");
        }else {
            viewHolder.playTimes.setText(trackData.getPlayCount() + "");
        }
        viewHolder.playDuration.setText(Utils.formatTime(trackData.getDuration()));
        //将返回的unix 毫秒日期，转为指定格式 日期
        String date = new SimpleDateFormat("yyyy-MM").format(new Date(trackData.getUpdatedAt()));
        viewHolder.textDate.setText(date);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!trackAc.popupWindow.isShowing()) {
                    //trackAc.popupWindow.showAtLocation(trackAc.popView, Gravity.BOTTOM, 0, 0);
                    Log.i(TAG, "onClick: ");
                    //弹出，播放的弹窗
                    trackAc.showPopupWindow();

                    initView(trackData);
                    playerManager.playList(trackList,i);

                    List<Track> playList = playerManager.getPlayList();
                    Log.i(TAG, "onClick: playList :" + playList);
                }
            }
        });

    }

    /**
     * 点击某个，音频，弹窗开始播放，初始化 播放页面
     * @param trackData
     */
    private void initView(Track trackData){
        Log.i(TAG, "initView: headline : " + trackData.getTrackTitle());
        Log.i(TAG, "initView: duration : " + trackData.getDuration());
        Log.i(TAG, "initView: url : " + trackData.getCoverUrlLarge());
        //标题
        trackAc.popHeadLine.setText(trackData.getTrackTitle());
        int duration = trackData.getDuration();
        //转换时间格式
        String str = Utils.formatTime(duration);
        trackAc.nowTime.setText("00:00");
        trackAc.duration.setText( str);
        //加载图片

        Glide.with(mContext).load(trackData.getCoverUrlLarge())
                .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                .into(trackAc.popImageView);


    }


    @Override
    public int getItemCount() {
        return trackList.size();
    }
}
