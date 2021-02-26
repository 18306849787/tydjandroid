package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.PbHotBotActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.pbmicrovideo.HotWordData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PbvHotBotAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HotWordData.HotWord> dataList = new ArrayList<>();
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_num)
        TextView textNum;               //排行
        @BindView(R.id.textView_news)
        TextView textNews;              //热词
        @BindView(R.id.imageView_hot_bot)
        ImageView imageView;
        private View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    public PbvHotBotAdapterNew(List<HotWordData.HotWord> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_hot_bot_pbv_new,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder)viewHolder;
        HotWordData.HotWord hotWord = dataList.get(i);

        if (i == 0 || i == 1 || i == 2) {
            holder.textNum.setTextColor(MyApplication.getContext().getResources().getColor(R.color.hot_bot_num_1));
            holder.imageView.setVisibility(View.VISIBLE);
            TextPaint paint = holder.textNum.getPaint();
            paint.setFakeBoldText(true);
            holder.textNum.setTypeface(null,Typeface.BOLD_ITALIC);
        }else {
            holder.textNum.setTextColor(MyApplication.getContext().getResources().getColor(R.color.hot_bot_num_0));
            holder.imageView.setVisibility(View.INVISIBLE);
            holder.textNum.setTypeface(null,Typeface.NORMAL);
        }
        holder.textNum.setText((i + 1) + "");
        holder.textNews.setText(hotWord.hotWord);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, PbHotBotActivity.class);
                intentAc.putExtra("hotWord", hotWord.hotWord);
                mContext.startActivity(intentAc);
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
