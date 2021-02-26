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

public class PbvHotBotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HotWordData.HotWord> dataList = new ArrayList<>();
    private Context mContext;

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

    public PbvHotBotAdapter(List<HotWordData.HotWord> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM_NORMAL) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_hot_bot_pbv,
                    viewGroup, false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_load_more2,
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
        if (type == TYPE_ITEM_NORMAL) {

            ViewHolder holder = (ViewHolder)viewHolder;
            HotWordData.HotWord hotWord = dataList.get(i);

            if (i == 0 || i == 1 || i == 2) {
                holder.textNum.setTextColor(MyApplication.getContext().getResources().getColor(R.color.hot_bot_num_1));
                holder.imageView.setVisibility(View.VISIBLE);
                TextPaint paint = holder.textNum.getPaint();
                paint.setFakeBoldText(true);
                holder.textNum.setTypeface(null,Typeface.BOLD_ITALIC);
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
    }



    public void setTypeItemFooter(){

       /* mHolder.progressBar.setVisibility(View.INVISIBLE);
        mHolder.textHint.setText("没有更多了");*/
        Log.i("FragmentHotBot", "setTypeItemFooter: ");
        if (mHolder != null){
            if (mHolder.progressBar != null){
                mHolder.progressBar.setVisibility(View.INVISIBLE);
            }
            if (mHolder.textHint != null){
                mHolder.textHint.setText("没有更多了");
                Log.i("FragmentHotBot", "setTypeItemFooter: mHolder : " + mHolder);
                Log.i("FragmentHotBot", "setTypeItemFooter: 没有更多了");
            }
        }

    }

    public void setTypeItemFooterStart(){

        if (mHolder != null){
            if (mHolder.progressBar != null){
                mHolder.progressBar.setVisibility(View.VISIBLE);
            }
            if (mHolder.textHint != null){
                mHolder.textHint.setText("正在加载...");
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
        /*if (dataList.size() == 0){
            return 0;
        }else {
            return dataList.size();
        }*/
        return dataList.size()+1;

    }
}
