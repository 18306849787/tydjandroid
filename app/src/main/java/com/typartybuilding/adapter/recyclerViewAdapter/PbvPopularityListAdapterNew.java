package com.typartybuilding.adapter.recyclerViewAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.UserDetailsActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.pbmicrovideo.PopularityListData;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PbvPopularityListAdapterNew extends RecyclerView.Adapter<PbvPopularityListAdapterNew.ViewHolder>{

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_num)
        TextView textNum;                 //排名
        @BindView(R.id.circle_img_head)
        RoundImageView circleIma;        //头像
        @BindView(R.id.textView_name)
        TextView textName;                //昵称
        @BindView(R.id.imageView_attention)
        ImageView attention;           //关注的状态
        /*@BindView(R.id.imageView_hot)
        ImageView imgHot;               //火焰的图片，前三名，显示该图片*/
        private View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    private List<PopularityListData.PopularityData> dataList = new ArrayList<>();
    private Context mContex;
    private int userType;

    public PbvPopularityListAdapterNew(List<PopularityListData.PopularityData> dataList, Context mContex) {
        this.dataList = dataList;
        this.mContex = mContex;
        userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.item_recyclerview_popularity_list_pbv_new,
                viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        PopularityListData.PopularityData data = dataList.get(i);
        //int num = Integer.parseInt(popularityList.strNum);
        if (i == 0 || i == 1 || i == 2){
            viewHolder.textNum.setTextColor(MyApplication.getContext().getResources().getColor(R.color.hot_bot_num_1));

            TextPaint paint = viewHolder.textNum.getPaint();
            paint.setFakeBoldText(true);
            viewHolder.textNum.setTypeface(null, Typeface.BOLD_ITALIC);
        }else {
            viewHolder.textNum.setTextColor(MyApplication.getContext().getResources().getColor(R.color.hot_bot_num_0));
            viewHolder.textNum.setTypeface(null, Typeface.NORMAL);
        }
        viewHolder.textNum.setText((i + 1) + "");
        viewHolder.textName.setText(data.nickName);
        if (data.isFollowed == 1) {
            viewHolder.attention.setSelected(true);
        }else {
            viewHolder.attention.setSelected(false);
        }
        //加载头像
        Glide.with(MyApplication.getContext()).load(data.headImg)
                .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                .into(viewHolder.circleIma);

        viewHolder.attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userType == 1 || userType == 2) {
                    if (viewHolder.attention.isSelected()) {
                        viewHolder.attention.setSelected(false);
                        //取消关注
                        RetrofitUtil.delFocus(data.userId,viewHolder.attention);

                    } else {
                        viewHolder.attention.setSelected(true);
                        //关注
                        RetrofitUtil.addFocus2(data.userId, data.nickName, data.headImg,viewHolder.attention);
                    }
                }else if (userType == 3){
                    MyApplication.remindVisitor((Activity)mContex);
                }
            }
        });

        viewHolder.circleIma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContex, UserDetailsActivity.class);
                intentAc.putExtra("userId",data.userId);
                intentAc.putExtra("userName",data.nickName);
                mContex.startActivity(intentAc);
            }
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
