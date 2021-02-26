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

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.PbHotBotActivity;
import com.typartybuilding.activity.tyorganization.TyOrganizationActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.pbmicrovideo.HotWordData;
import com.typartybuilding.gsondata.tyorganization.TyOrganizationData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TyOrganizationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TyOrganizationData.OrganizationData> dataList = new ArrayList<>();
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_headline)
        TextView headLine;               //标题
        @BindView(R.id.imageView)
        ImageView imageView;
        private View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    public TyOrganizationAdapter(List<TyOrganizationData.OrganizationData> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_ty_org,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ViewHolder holder = (ViewHolder)viewHolder;
        TyOrganizationData.OrganizationData data = dataList.get(i);

        holder.headLine.setText(data.gwTitle);
        //加载图片
        Glide.with(mContext).asDrawable().load(data.picUrls)
                .apply(MyApplication.requestOptions43)
                .into(holder.imageView);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, TyOrganizationActivity.class);
                //intentAc.putExtra("OrganizationData",data);
                MyApplication.organizationData = data;
                mContext.startActivity(intentAc);
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
