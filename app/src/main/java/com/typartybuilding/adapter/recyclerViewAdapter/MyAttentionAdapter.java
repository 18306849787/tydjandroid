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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.UserDetailsActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.personaldata.MyFocusData;
import com.typartybuilding.retrofit.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyAttentionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "MyAttentionAdapter";

    private List<MyFocusData.FocusPeople> dataList = new ArrayList<>();
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imageView_headimg)
        CircleImageView headImg;
        @BindView(R.id.textView_name)
        TextView textName;
        @BindView(R.id.imageView_attention)
        ImageView imgAttention;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }

    public MyAttentionAdapter(List<MyFocusData.FocusPeople> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int  viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_my_attention,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ViewHolder holder = (ViewHolder)viewHolder;
        MyFocusData.FocusPeople data = dataList.get(i);

        holder.textName.setText(data.followedUserName);
        holder.imgAttention.setSelected(true);
        //加载头像
        Log.i(TAG, "onBindViewHolder: data.followedUserImg : " + data.followedUserImg);
        Glide.with(mContext).load(data.followedUserImg)
                .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                .into(holder.headImg);

        holder.imgAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imgAttention.isSelected()) {
                    holder.imgAttention.setSelected(false);
                    //取消关注
                    RetrofitUtil.delFocus(data.followedUserId,holder.imgAttention);
                } else {
                    holder.imgAttention.setSelected(true);
                    //关注
                    RetrofitUtil.addFocus2(data.followedUserId, data.followedUserName, data.followedUserImg,holder.imgAttention);
                }
            }
        });

        holder.headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, UserDetailsActivity.class);
                intentAc.putExtra("userId",data.followedUserId);
                intentAc.putExtra("userName",data.followedUserName);
                mContext.startActivity(intentAc);
            }
        });
    }

    @Override
    public int getItemCount() {
       return dataList.size();
    }
}
