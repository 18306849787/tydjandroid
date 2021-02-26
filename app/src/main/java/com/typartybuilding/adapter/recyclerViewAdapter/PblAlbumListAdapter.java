package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.R;
import com.typartybuilding.activity.pblibrary.TrackListActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.pblibrary.AlbumData;
import com.typartybuilding.bean.pblibrary.AlbumListData;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class PblAlbumListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "PblAlbumListAdapter";

    private List<AlbumData> dataList = new ArrayList<>();
    private Context mContext;


    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindViews({R.id.imageView1, R.id.imageView2, R.id.imageView3})
        ImageView imageView [];        //音频图片
        @BindViews({R.id.imageView1_play, R.id.imageView2_play, R.id.imageView3_play})
        ImageView imgPlay [];           //播放次数的图标
        @BindViews({R.id.textView1_play_times, R.id.textView2_play_times, R.id.textView3_play_times})
        TextView textPlayTimes [];     //音频播放次数
        @BindViews({R.id.textView1, R.id.textView2, R.id.textView3})
        TextView textHeadLine [];      //音频标题
        @BindViews({R.id.textView1_subhead, R.id.textView2_subhead, R.id.textView3_subhead})
        TextView textSubHead [];       //子标题

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }

    public PblAlbumListAdapter(List<AlbumData> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_party_building_library,
                viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int index = i * 3;
        Log.i(TAG, "onBindViewHolder: index : " + index);
        ViewHolder holder = (ViewHolder)viewHolder;

        for (int j = 0; j < 3; j++) {
            if (index < dataList.size()) {
                AlbumData albumData = dataList.get(index);
                Log.i(TAG, "onBindViewHolder: imgUrl : " + albumData.imgUrl);
                //加载图片
                Glide.with(mContext).asDrawable().load(albumData.imgUrl)
                        .apply(MyApplication.requestOptions11)  //url为空或异常显示默认图片
                        .into(holder.imageView[j]);
                //大于1万，  去掉4个零
                int count = Integer.parseInt(albumData.playTimes);
                holder.textPlayTimes[j].setText(Utils.formatPlayTimes(count));

                holder.textHeadLine[j].setText(albumData.headLine);
                holder.textSubHead[j].setText(albumData.subHead);

                //每张专辑注册点击事件
                holder.imageView[j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentAc = new Intent(mContext, TrackListActivity.class);
                        intentAc.putExtra("album_data", albumData);
                        mContext.startActivity(intentAc);
                    }
                });
                index++;

            } else {
                holder.imgPlay[j].setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        ViewHolder viewHolder = (ViewHolder)holder;
        ImageView [] imageViews = viewHolder.imageView;
        //Log.i(TAG, "onViewRecycled: imageViews : " + imageViews);
        if (imageViews != null){
            //Log.i(TAG, "onViewRecycled: ");
            Glide.with(mContext).clear(imageViews[0]);
            Glide.with(mContext).clear(imageViews[1]);
            Glide.with(mContext).clear(imageViews[2]);
        }

    }

    @Override
    public int getItemCount() {
        int size = dataList.size();
        if (size == 0){
            return 0;
        }else {
            int n = size % 3;
            //Log.i(TAG, "getItemCount: size : " + size);
            if (n == 0) {
                //Log.i(TAG, "getItemCount: size/3 : " + size / 3);
                return (size / 3) ;
            } else {
                //Log.i(TAG, "getItemCount: size/3 + 1 : " + (size / 3 + 1));
                return (size / 3 + 1) ;
            }
        }

    }
}
