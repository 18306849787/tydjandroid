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
import com.typartybuilding.activity.dreamwish.GoodPeopleDetailsActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.dreamwish.GoodPeopleData;
import com.typartybuilding.gsondata.pbmicrovideo.PopularityListData;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodPeopleAcAdapter extends RecyclerView.Adapter<GoodPeopleAcAdapter.ViewHolder>{

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_num)
        TextView textNum;                 //排名
        @BindView(R.id.circle_img_head)
        RoundImageView circleIma;        //头像
        @BindView(R.id.textView_name)
        TextView textName;                //昵称
        @BindView(R.id.textView_dream_num)
        TextView textDreamNum;            //心愿完成数

        private View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    private List<GoodPeopleData.GoodPeople> dataList = new ArrayList<>();
    private Context mContex;


    public GoodPeopleAcAdapter(List<GoodPeopleData.GoodPeople> dataList, Context mContex) {
        this.dataList = dataList;
        this.mContex = mContex;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.item_recyclerview_good_people_ac,
                viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        GoodPeopleData.GoodPeople data = dataList.get(i);
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
        viewHolder.textName.setText(data.username);
        //已完成心愿数
        viewHolder.textDreamNum.setText(data.aspirationFinish + "");

        //加载头像
        Glide.with(MyApplication.getContext()).load(data.headImg)
                .apply(MyApplication.requestOptions2)  //url为空或异常显示默认头像
                .into(viewHolder.circleIma);

        viewHolder.circleIma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContex, GoodPeopleDetailsActivity.class);
                intentAc.putExtra("userId",data.userId);
                intentAc.putExtra("userName",data.username);
                mContex.startActivity(intentAc);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
