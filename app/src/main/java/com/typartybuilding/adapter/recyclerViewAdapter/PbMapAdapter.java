package com.typartybuilding.adapter.recyclerViewAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.pbmap.EducationalBaseActivity;
import com.typartybuilding.activity.pbmap.PartyOrganizationActivity;
import com.typartybuilding.activity.pbmap.ServiceCenterActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fgPartyBuildingMap.FragmentPbMap;
import com.typartybuilding.gsondata.pbmap.AgenciesData;
import com.typartybuilding.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 基层党组织
 */

public class PbMapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AgenciesData.AgencyData> dataList = new ArrayList<>();
    private Context mContext;
    private FragmentPbMap fragmentPbMap;

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView_headline)
        TextView headLine;
        @BindView(R.id.imageView_appointment)
        ImageView appointment;            //可预约的图片提示
        @BindView(R.id.textView_site)
        TextView textSite;                //地点
        @BindView(R.id.textView_distance)
        TextView distance;                //距离
        @BindView(R.id.textView_time)
        TextView textTime;                //上班时间

        @BindView(R.id.imageView_phone)
        ImageView imgPhone;               //打电话按钮

        /*@BindView(R.id.constraintLayout_detail)
        ConstraintLayout layoutDetail;    //点击 了解详情的布局*/

        @BindView(R.id.imageView_detail)
        ImageView textDetail;              //了解详情 的文字提示

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }

    public PbMapAdapter(List<AgenciesData.AgencyData> dataList, Context mContext, FragmentPbMap fragmentPbMap) {
        this.dataList = dataList;
        this.mContext = mContext;
        this.fragmentPbMap = fragmentPbMap;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_pb_map,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        ViewHolder viewHolder = (ViewHolder)holder;
        AgenciesData.AgencyData data = dataList.get(i);
        viewHolder.headLine.setText(data.orgName);
        viewHolder.textSite.setText(data.orgAddress);

        //达到 3 位数，改为km
        if (data.distance > 999){
            float n = ((float)data.distance)/1000;
            float distance = (float)(Math.round(n*100))/100;  //保留小数点后两位
            viewHolder.distance.setText("距离目的地有" + distance + "km");
        }else {
            viewHolder.distance.setText("距离目的地有" + data.distance + "m");
        }
        viewHolder.textTime.setText(data.businessHours);
        //获取标记，
        int flag = data.orgType;  //1：党组织机构，2：党群服务中心，3：教育基地',
        if (flag == 2) { //2 是党群服务中心，可以预约
//            viewHolder.appointment.setVisibility(View.VISIBLE);
        }

        viewHolder.textDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtils.getIns().isTourist()) {
                    MyApplication.remindVisitor((Activity) mContext);
                    return;
                }
                if (flag == 1) {
                    skipPartOrganization(data);
                } else if (flag == 2) {
                    skipServiceCenter(data);
                } else if (flag == 3) {
                    skipEducationBase(data);
                }
            }
        });

        //拨打电话
        viewHolder.imgPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtils.getIns().isTourist()) {
                    MyApplication.remindVisitor((Activity) mContext);
                    return;
                }
                fragmentPbMap.showPopupWindow1(data.orgPhone,data.orgName);
            }
        });
    }

    private void skipPartOrganization(AgenciesData.AgencyData data){
        Intent intentAc = new Intent(mContext, PartyOrganizationActivity.class);
        intentAc.putExtra("AgencyData",data);
        mContext.startActivity(intentAc);
    }

    private void skipServiceCenter(AgenciesData.AgencyData data){
        Intent intentAc = new Intent(mContext, ServiceCenterActivity.class);
        intentAc.putExtra("AgencyData", data);
        mContext.startActivity(intentAc);
    }

    private void skipEducationBase(AgenciesData.AgencyData data){
        Intent intentAc = new Intent(mContext, EducationalBaseActivity.class);
        intentAc.putExtra("AgencyData", data);
        mContext.startActivity(intentAc);
    }


   /* public void setTypeItemFooter(){
      *//*  mHolder.progressBar.setVisibility(View.INVISIBLE);
        mHolder.textHint.setText("没有更多了");*//*
        if (mHolder != null){
            if (mHolder.progressBar != null){
                mHolder.progressBar.setVisibility(View.INVISIBLE);
            }
            if (mHolder.textHint != null){
                mHolder.textHint.setText("没有更多了");
            }
        }
    }*/


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
