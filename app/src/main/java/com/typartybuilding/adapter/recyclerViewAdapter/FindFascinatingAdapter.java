package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.PlayAudioActivity;
import com.typartybuilding.activity.PlayMicroVideoActivity;
import com.typartybuilding.activity.PlayPictureActivity;
import com.typartybuilding.activity.UserDetailsActivity;
import com.typartybuilding.base.MyApplication;
import com.bumptech.glide.Glide;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FindFascinatingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private String TAG = "FindFascinatingAdapter";

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


    static class ViewHolderNormal extends RecyclerView.ViewHolder{

        @BindView(R.id.imageView_find_fascinating)
        ImageView imageView;                 //图片
        @BindView(R.id.textView_headline)
        TextView headLine;                   //标题
        @BindView(R.id.circle_img_head)
        CircleImageView circleIma;         //头像
        @BindView(R.id.textView_name)
        TextView textName;                //昵称
        @BindView(R.id.textView_play_times)
        TextView textPlayTimes;           //播放次数

        public ViewHolderNormal(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    private List<MicroVideo> dataList = new ArrayList<>();
    private Context mContext;

    public FindFascinatingAdapter(List<MicroVideo> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_ITEM_NORMAL) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_find_fascinating_pbv,
                    viewGroup, false);
            return new ViewHolderNormal(view);
        }else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_load_more2,
                    viewGroup, false);
            //if (mHolder == null) {
                mHolder = new ViewHolderFooter(view);
            //}
            return mHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        if (type == TYPE_ITEM_NORMAL){
            setTypeItemNormal(viewHolder,i);
        }else {

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

    public void setTypeItemFooterStart(){

       /* mHolder.progressBar.setVisibility(View.VISIBLE);
        mHolder.textHint.setText("正在加载...");*/

        if (mHolder != null){
            if (mHolder.progressBar != null){
                mHolder.progressBar.setVisibility(View.VISIBLE);
            }
            if (mHolder.textHint != null){
                mHolder.textHint.setText("正在加载...");
            }
        }

    }

    private void setTypeItemNormal(RecyclerView.ViewHolder viewHolder,int i){
        ViewHolderNormal holder = (ViewHolderNormal)viewHolder;
        MicroVideo microVideo = dataList.get(i);
        if (microVideo.visionTitle == null || microVideo.visionTitle.equals("")){
            holder.headLine.setVisibility(View.GONE);
        }else {
            holder.headLine.setVisibility(View.VISIBLE);
            holder.headLine.setText(microVideo.visionTitle);
        }

        holder.textName.setText(microVideo.userName);
        holder.textPlayTimes.setText(Utils.formatPlayTimes2(microVideo.visionBrowseTimes));
       // Log.i(TAG, "setTypeItemNormal: 浏览次数visionBrowseTimes i : " +i + ": " + microVideo.visionBrowseTimes);

        // 加载封面,   visionType //1：图片，2：视频，3：音频
        String imgUrl = null;   //封面图url
        int visionType = microVideo.visionType;
        //Log.i(TAG, "setTypeItemNormal: visionType ：" + visionType);
        if (visionType == 1){
            if (microVideo.visionFileUrl.contains(",")){
                String split[] = microVideo.visionFileUrl.split(",");
                imgUrl = split[0];
            }else {
                imgUrl = microVideo.visionFileUrl;
            }
            Glide.with(mContext).load(imgUrl)
                    .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                    .into(holder.imageView);

        }else if (visionType == 2){
            //子线程 获取第一帧
            //Utils.getVideoPicture(holder.imageView,microVideo.visionFileUrl);
            //先加载默认图
            Glide.with(mContext).load(microVideo.videoCover)
                    .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                    .into(holder.imageView);

        }else if (visionType == 3){
            Glide.with(mContext).load(R.mipmap.yinpin_imgbg)
                    .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                    .into(holder.imageView);
        }
        //设置微视 的 点击事件     1，图片 ； 2 ，视频； 3，音频
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visionType == 1){
                    Intent intent = new Intent(mContext, PlayPictureActivity.class);
                    //intent.putExtra("MicroVideo",microVideo);
                    MyApplication.microVideo = microVideo;
                    mContext.startActivity(intent);
                }else if (visionType == 2){
                    Intent intent = new Intent(mContext, PlayMicroVideoActivity.class);
                    intent.putExtra("flag", 2); //2 表示，微视频 来自个人页面和发现精彩
                    //intent.putExtra("MicroVideo",microVideo);
                    MyApplication.microVideo = microVideo;
                    mContext.startActivity(intent);
                }else if (visionType == 3){
                    Intent intent = new Intent(mContext, PlayAudioActivity.class);
                    //intent.putExtra("MicroVideo",microVideo);
                    MyApplication.microVideo = microVideo;
                    mContext.startActivity(intent);
                }
            }
        });

        //加载头像
        Glide.with(MyApplication.getContext()).load(microVideo.userHeadImg)
                .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                .into(holder.circleIma);
        //跳转到用户详情页
        holder.circleIma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, UserDetailsActivity.class);
                intentAc.putExtra("userId",microVideo.userId);
                intentAc.putExtra("userName",microVideo.userName);
                mContext.startActivity(intentAc);
            }
        });


    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            int position = holder.getLayoutPosition();
            if (getItemViewType(position) == TYPE_ITEM_FOOTER) {
                params.setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position + 1 < getItemCount()) return TYPE_ITEM_NORMAL;
        else return TYPE_ITEM_FOOTER;
    }

    @Override
    public int getItemCount() {
       /* if (dataList.size() == 0){
            return 0;
        }else {
            return dataList.size() + 1;
        }*/

       return dataList.size() + 1;
    }
}
