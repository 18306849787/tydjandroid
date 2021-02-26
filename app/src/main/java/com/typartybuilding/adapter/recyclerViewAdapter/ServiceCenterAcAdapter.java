package com.typartybuilding.adapter.recyclerViewAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.PbHotBotActivity;
import com.typartybuilding.activity.pbmap.ServiceCenterActivity;
import com.typartybuilding.adapter.viewPagerAdapter.ServiceCenterVpAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.pbmicrovideo.HotWordData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class ServiceCenterAcAdapter extends RecyclerView.Adapter<ServiceCenterAcAdapter.ViewHolder> {

    private String TAG = "ServiceCenterAcAdapter";

    private List<String> imgUrlList = new ArrayList<>();
    private Context mContext;
    private ServiceCenterActivity activity;


    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imageView)
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }

    public ServiceCenterAcAdapter(List<String> imgUrlList, Context mContext) {
        this.imgUrlList = imgUrlList;
        this.mContext = mContext;
        this.activity = (ServiceCenterActivity)mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_service_center_ac_new,
                viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Glide.with(mContext).load(imgUrlList.get(i))
                .apply(MyApplication.requestOptions43)
                .into(viewHolder.imageView);


        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.popupWindow2 != null) {
                    if (!activity.popupWindow2.isShowing()){
                        //activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//显示顶部状态栏
                        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        activity.popupWindow2.showAtLocation(activity.popView2, Gravity.CENTER, 0, 0);
                        activity.viewPager.setCurrentItem(i);

                    }
                }
            }
        });


    }


    @Override
    public int getItemCount() {

        return imgUrlList.size() ;

    }
}
