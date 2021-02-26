package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayAudioActivity;
import com.typartybuilding.activity.PlayAudioActivityNew;
import com.typartybuilding.activity.PlayMicroVideoActivity;
import com.typartybuilding.activity.PlayPictureActivity;
import com.typartybuilding.activity.myRelatedActivity.MyMicVideoActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.douyin.MicroVideoPlayActivity;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 *  用户 个人页面 微视作品， 包含视频，图片，音频
 */
public class PersonalMicVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "PersonalMicVideoAdapter";

    private List<MicroVideo> dataList = new ArrayList<>();

    private Context mContext;
    private MyMicVideoActivity activity;

    private int flag;      //0 表示ta 的微视， 1 表示我的微视


    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindViews({R.id.imageView1, R.id.imageView2, R.id.imageView3})
        ImageView imageView[];
        @BindViews({R.id.textView1, R.id.textView2, R.id.textView3})
        TextView textView[];
        @BindViews({R.id.imageView1_delete, R.id.imageView2_delete, R.id.imageView3_delete})
        ImageView imgDelete[];
        @BindViews({R.id.layout1, R.id.layout2, R.id.layout3})
        ConstraintLayout layout[];

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public PersonalMicVideoAdapter(List<MicroVideo> dataList, Context mContext, int flag) {

        this.dataList = dataList;
        this.mContext = mContext;
        this.flag = flag;
        if (flag == 1){
            activity = (MyMicVideoActivity)mContext;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_personal_mic_video,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        initViewItem(viewHolder, i);
    }


    private void initViewItem(RecyclerView.ViewHolder viewHolder, int i){
        int index = i*3;    //每个item三个数据，index表示每个item的 第一条数据的下标

        ViewHolder holder = (ViewHolder)viewHolder;
        //加载图片
        for (int j = 0; j < 3; j++){
            Log.i(TAG, "initViewItem: index : " + index);

            if (index < dataList.size()){
                MicroVideo microVideo = dataList.get(index);
                //为 1，表示在 我的微视 页面， 删除微视 按钮 可见
                if (flag == 1){
                    holder.imgDelete[j].setVisibility(View.VISIBLE);
                }
                //显示点赞数
                holder.textView[j].setText(Utils.formatPlayTimes(microVideo.visionPraisedNum) + "次赞");
                //显示封面
                String imgUrl = null;   //封面图url
                int visionType = microVideo.visionType; // 1，图片 ； 2 ，视频； 3，音频
                if (visionType == 1){
                    if (microVideo.visionFileUrl.contains(",")){
                        String split[] = microVideo.visionFileUrl.split(",");
                        imgUrl = split[0];
                    }else {
                        imgUrl = microVideo.visionFileUrl;
                    }
                    Glide.with(mContext).load(imgUrl)
                            .apply(MyApplication.requestOptions23)  //url为空或异常显示默认头像
                            .into(holder.imageView[j]);

                }else if (visionType == 2){
                    //加载视频封面图
                    Glide.with(mContext).load(microVideo.videoCover)
                            .apply(MyApplication.requestOptions23)  //url为空或异常显示默认头像
                            .into(holder.imageView[j]);
                }else if (visionType == 3){
                    Glide.with(mContext).load(microVideo.videoCover)
                            .apply(MyApplication.requestOptions23)  //url为空或异常显示默认头像
                            .into(holder.imageView[j]);
                }
                //设置微视 的 点击事件     1，图片 ； 2 ，视频； 3，音频
                holder.imageView[j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (visionType == 1){
                            Intent intent = new Intent(mContext, PlayPictureActivity.class);
                            //intent.putExtra("MicroVideo",microVideo);
                            MyApplication.microVideo = microVideo;
                            mContext.startActivity(intent);
                        }else if (visionType == 2){
                            //Intent intent = new Intent(mContext, PlayMicroVideoActivity.class);
                            Intent intent = new Intent(mContext, MicroVideoPlayActivity.class);
                            intent.putExtra("flag", 1); //1 表示，微视频 来自我的 或 他的页面
                            MyApplication.microVideo = microVideo;
                            mContext.startActivity(intent);
                        }else if (visionType == 3){
                            Intent intent = new Intent(mContext, PlayAudioActivityNew.class);
                            MyApplication.microVideo = microVideo;
                            mContext.startActivity(intent);
                        }
                    }
                });

            }else {  //数据没有 三条时， 剩下的布局不可见
                holder.layout[j].setVisibility(View.INVISIBLE);
            }
            index++;
        }

        //删除个人作品，在个人页面
        holder.imgDelete[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showPopupWindow(i*3,dataList.get(i*3).visionId);
                //deleteMicVideo(i*3);
                /*activity.dataList.remove(i*3);
                activity.adapter.notifyDataSetChanged();*/
            }
        });

        holder.imgDelete[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showPopupWindow(i*3 + 1,dataList.get(i*3+1).visionId);
                //deleteMicVideo(i*3 + 1);
               /* activity.dataList.remove(i*3 + 1);
                activity.adapter.notifyDataSetChanged();*/
            }
        });

        holder.imgDelete[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showPopupWindow(i*3 + 2,dataList.get(i*3+2).visionId);
                //deleteMicVideo(i*3 + 2);
                /*activity.dataList.remove(i*3 + 2);
                activity.adapter.notifyDataSetChanged();*/
            }
        });

    }

    public void deleteMicVideo(int delIndex){
        activity.dataList.remove(delIndex);
        activity.adapter.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        //计算有多少个item
        int total = dataList.size();   //微视频的总数
        int item = 0;
        if (total % 3 == 0) {
            item = total / 3;
        } else {
            item = total / 3 + 1;    //不是3的倍数，要加 1
        }

        Log.i(TAG, "getItemCount: dataList Size : " + total);
        Log.i(TAG, "getItemCount: SIZE : " + item);
        return item ;

    }
}
