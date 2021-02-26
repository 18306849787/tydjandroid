package com.typartybuilding.activity.quanminlangdu.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.quanminlangdu.entity.MusicEntity;
import com.typartybuilding.activity.quanminlangdu.mac.MusicListActivity;


import java.util.ArrayList;
import java.util.List;

public class MusicItemRecyclerViewAdapter extends RecyclerView.Adapter<MusicItemRecyclerViewAdapter.ViewHolder> {
    private List<MusicEntity> musicEntityList = new ArrayList<>();
    private MusicListActivity.OnClickMusicItemListener mMusicOnClickListener;
    private Context context;
    private int mPosition = -1;
    private int selectPostion = -1;

    public MusicItemRecyclerViewAdapter(List<MusicEntity> musicEntityList, MusicListActivity.OnClickMusicItemListener mMusicOnClickListener) {
        this.musicEntityList = musicEntityList;
        this.mMusicOnClickListener = mMusicOnClickListener;
    }

    public void setData(List<MusicEntity> musicEntities){
        this.musicEntityList = musicEntities;
        this.mPosition = -1;
    }

    public void setSelectPostion(int i){
        this.selectPostion = i;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_list_item,viewGroup,false);
        context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mItem = musicEntityList.get(i);
        viewHolder.musicName.setText(musicEntityList.get(i).getBgmName());
        viewHolder.musicTime.setText(musicEntityList.get(i).getStrTime());
        Glide.with(context).load(musicEntityList.get(i).getBgmImg()).into(viewHolder.musicImage);
        viewHolder.musicUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicOnClickListener.OnClickItemListener(musicEntityList.get(i));
            }
        });
        viewHolder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPosition == i){
                    if (viewHolder.btnPlay.isSelected()){
                        mPosition = -1;
                        notifyDataSetChanged();
                        viewHolder.btnPlay.setSelected(false);
                        mMusicOnClickListener.OnClickMusicPlayStop(musicEntityList.get(i));
                    }else {
                        mPosition = i;
                        notifyDataSetChanged();
                        viewHolder.btnPlay.setSelected(true);
                        mMusicOnClickListener.OnClickMusicPlayOn(musicEntityList.get(i));
                    }
                }else {
                    mPosition = i;
                    notifyDataSetChanged();
                    viewHolder.btnPlay.setSelected(true);
                    mMusicOnClickListener.OnClickMusicPlayOn(musicEntityList.get(i));
                }
            }
        });
        if (mPosition != i){
            viewHolder.btnPlay.setSelected(false);
        }else if (mPosition == i){
            viewHolder.btnPlay.setSelected(true);
        }

    }

    @Override
    public int getItemCount() {
        return musicEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView musicImage;
        public ImageView btnPlay;
        public TextView musicName;
        public TextView musicTime;
        public ImageButton musicUseBtn;
        public View mView;
        public MusicEntity mItem;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            musicImage = (ImageView) view.findViewById(R.id.music_image);
            btnPlay = (ImageView) view.findViewById(R.id.music_play);
            musicName = (TextView) view.findViewById(R.id.music_title);
            musicTime = (TextView) view.findViewById(R.id.music_time);
            musicUseBtn = (ImageButton) view.findViewById(R.id.music_use);
        }
    }
}
