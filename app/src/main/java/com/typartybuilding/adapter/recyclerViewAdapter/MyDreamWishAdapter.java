package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.dreamwish.DreamWishDetailActivity;
import com.typartybuilding.activity.dreamwish.DreamWishDetailTaActivity;
import com.typartybuilding.activity.myRelatedActivity.WishClaimActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.dreamwish.DreamWishData;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDreamWishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "DreamWishAdapter";

    private List<DreamWishData.WishData> dataList = new ArrayList<>();
    private Context mContext;
    private WishClaimActivity wishClaimActivity;

    private Drawable draSctp;        //上传图片
    private Drawable draCxsc;      //重新上传
    private Drawable draZzsh;    //正在审核
    private Drawable draYwc;      //已完成

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        ImageView imageView;              //封面图片
        @BindView(R.id.textView_headline)
        TextView headLine;                //标题
        @BindView(R.id.textView_date)
        TextView textDate;                //日期
        @BindView(R.id.imageView_btn)
        ImageView imgBtn;                 //心愿状态
        @BindView(R.id.textView_reason)
        TextView textReason;              //审核不通过的原因

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public MyDreamWishAdapter(List<DreamWishData.WishData> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
        wishClaimActivity = (WishClaimActivity)mContext;
        draSctp = mContext.getResources().getDrawable(R.mipmap.xyrl_btn_upload);
        draCxsc = mContext.getResources().getDrawable(R.mipmap.xyrl_btn_reupload);
        draZzsh = mContext.getResources().getDrawable(R.mipmap.btn_zzsh);
        draYwc = mContext.getResources().getDrawable(R.mipmap.btn_yiwancheng);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_dream_wish_my,
                viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        DreamWishData.WishData wishData = dataList.get(i);
        //设置 标题
        holder.headLine.setText(wishData.aspirationTitle);
        //设置发布日期
        holder.textDate.setText(Utils.formatDate(wishData.aspirationPublishDate));
        //设置心愿状态, 3：心愿认领,4：心愿验收，5：验收驳回，6：心愿完成
        int status = wishData.aspirationStatus;
        if (status == 3){              //上传图片
            holder.imgBtn.setImageDrawable(draSctp);
            holder.textReason.setVisibility(View.GONE);
        }else if (status == 4){        //正在审核
            holder.imgBtn.setImageDrawable(draZzsh);
            holder.textReason.setVisibility(View.GONE);
        }else if (status == 5){        //审核不通过，重新上传
            holder.imgBtn.setImageDrawable(draCxsc);
            //审核不通过原因：
            holder.textReason.setText("审核不通过原因：" + wishData.denyCause);
            holder.textReason.setVisibility(View.VISIBLE);
        }else if (status == 6){
            holder.imgBtn.setImageDrawable(draYwc);
            holder.textReason.setVisibility(View.GONE);
        }

        String imgUrl = wishData.aspirationImg;
        if (imgUrl != null && imgUrl.contains(",")){
            String [] splits = imgUrl.split(",");
            imgUrl = splits[0];
        }

        //加载图片
        Glide.with(mContext).load(imgUrl)
                .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                .into(holder.imageView);

        //设置封面图点击事件，跳转到详情页面
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentAc = new Intent(mContext, DreamWishDetailTaActivity.class);
                //intentAc.putExtra("flag", 1); //标记1， 表示数据来自我的或他的心愿数据
                MyApplication.wishData = wishData;
                mContext.startActivity(intentAc);

            }
        });

        //点击上传图片 或 重新上传图片
        holder.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wishData.aspirationStatus == 3 || wishData.aspirationStatus == 5){
                    wishClaimActivity.openPictureSelector(wishData.aspirationId,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size() ;

    }


}

