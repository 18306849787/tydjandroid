package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
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
import com.typartybuilding.activity.NewsDetailActivity;
import com.typartybuilding.activity.basicparty.BasicPartyActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.basicparty.BasicPartyData;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class FgBasicPartyAdapter extends RecyclerView.Adapter {

    private String TAG = "FgBasicPartyAdapter";

    private List<BasicPartyData.BasicParty> dataList = new ArrayList<>();
    private Context mContext;
    public List<FgBasicPartyItemAdapter> adapterList = new ArrayList<>();  //用于，查看详情后，刷新浏览量

    public int adapterPos;

    static class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_organization)
        TextView textOrg;
        @BindView(R.id.textView_more)
        TextView textMore;
        @BindView(R.id.recyclerView)
        RecyclerView recyclerView;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public FgBasicPartyAdapter(List<BasicPartyData.BasicParty> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_fg_basic_party, viewGroup, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MyViewHolder holder = (MyViewHolder)viewHolder;

        BasicPartyData.BasicParty basicParty = dataList.get(i);
        //设置 机构名称
        holder.textOrg.setText(basicParty.articleSource);
        //获取新闻数据
        initData(basicParty,holder.recyclerView,i);
        //设置 更多的点击事件
        holder.textMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, BasicPartyActivity.class);
                intentAc.putExtra("organName",basicParty.articleSource);
                mContext.startActivity(intentAc);
            }
        });
    }


    private void initData(BasicPartyData.BasicParty basicParty,RecyclerView recyclerView,int pos){
        List<ArticleBanner> bannerList = new ArrayList<>();
        //取出数据放入集合中
        if (bannerList.size() > 0){
            bannerList.clear();
        }
        ArticleBanner [] articleBanners = basicParty.rows;
        if (articleBanners != null){
            for (int i = 0; i < articleBanners.length; i++){
                bannerList.add(articleBanners[i]);
            }
            initRecyclerView(bannerList,recyclerView,pos);
        }
    }

    private void initRecyclerView(List<ArticleBanner> bannerList,RecyclerView recyclerView,int pos){
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        FgBasicPartyItemAdapter adapter = new FgBasicPartyItemAdapter(bannerList,mContext);
        //设置下标
        adapter.setAdapterPos(pos);
        //设置父 adaper， FgBasicPartyAdapter
        adapter.setBasicPartyAdapter(this);

        recyclerView.setFocusable(false);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(mContext.getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);
        //保存创建的adapter，便于刷新浏览量
        if (adapterList != null ){
            adapterList.add(adapter);
        }


    }

    public void refreshBrowseTimes(){
        if (adapterList != null){
            if (adapterPos < dataList.size()){
                FgBasicPartyItemAdapter adapter = adapterList.get(adapterPos);
                adapter.notifyItemChanged(adapter.currentPos);
            }
        }
    }


    @Override
    public int getItemCount() {
      return dataList.size();
    }

}
