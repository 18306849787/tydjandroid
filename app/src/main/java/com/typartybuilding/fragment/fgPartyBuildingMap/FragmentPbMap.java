package com.typartybuilding.fragment.fgPartyBuildingMap;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps.model.Marker;
import com.typartybuilding.R;
import com.typartybuilding.activity.pbmap.EducationalBaseActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.PbMapAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingMap;
import com.typartybuilding.gsondata.pbmap.AgenciesData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.UserUtils;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 *   各级党组织，党群服务中心，教育基地 使用，通过flag区分
 */
public class FragmentPbMap extends BaseFragment {

    private String TAG = "FragmentPbMap";

    @BindView(R.id.recyclerView_service_center)
    RecyclerView recyclerView;

    private int flag;         //1：党组织机构，2：党群服务中心，3：教育基地
    private int pageCount;    //总共多少页
    private int pageNo = 1;
    private int pageSize = 200;



    private PopupWindow popupWindow1;  //底部弹窗  ,拨打电话弹窗
    private View popView1;             //弹窗布局

    private Button btnConfirm;        //拨打电话确认按钮
    private TextView textPhoneNum;    //用于显示电话码
    private TextView textReminder;    //拨打电话文字提示

    private PbMapAdapter adapter;
    private List<AgenciesData.AgencyData> dataList = new ArrayList<>();
    HomeFragmentPartyBuildingMap fgParent;

    private boolean isDestroy;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        isDestroy = false;
        fgParent = (HomeFragmentPartyBuildingMap)getParentFragment();
        initRecyclerView();
        initPopupWindow1();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_map_fragment_service_center;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden){

        }
    }

    //设置标记，1：党组织机构，2：党群服务中心，3：教育基地
    public void setFlag(int flag) {
        this.flag = flag;
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PbMapAdapter(dataList,getActivity(),this);
        //添加分割线
//        DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
//        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line_pb_map));
//        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);
        recyclerView.setFocusable(false);

    }

    private void initData(AgenciesData agenciesData){
        if (!isDestroy){
            AgenciesData.AgencyData data[] = agenciesData.data.rows;
            Log.i(TAG, "initData: agenciesData.data.rows : " + agenciesData.data.rows);

            if (dataList.size() > 0) {
                dataList.clear();
            }

            Log.i(TAG, "initData: fgParent.markerList.size : " + fgParent.markerList.size());
            //设置标记前先移除之前的标记
            if (fgParent.markerList.size() > 0){
                for (Marker marker : fgParent.markerList){
                    marker.remove();
                }
                fgParent.markerList.clear();
            }

            if (data != null) {
                Log.i(TAG, "initData: size : " + data.length);

                for (int i = 0; i < data.length; i++){
                    dataList.add(data[i]);
                    Log.i(TAG, "initData: title : " + data[i].orgName);
                }


                //添加标记
                for (int j = 0; j < data.length; j++){
                    fgParent.setMarker(data[j].orgLatitude,data[j].orgLongitude);
                    Log.i(TAG, "initData: data[j].orgLatitude : " + data[j].orgLatitude);
                    Log.i(TAG, "initData: data[j].orgLongitude : " + data[j].orgLongitude);
                }
            }
            adapter.notifyDataSetChanged();

        }
    }

    public void getAgenciesData(double longitude,double latitude,String regionName){
        int orgType = 0;
        if (flag == 1){
            orgType = 1;
        }else if (flag == 2){
            orgType = 2;
        }else if (flag == 3){
            orgType = 3;
        }
        Log.i(TAG, "getAgenciesData: orgType : " + orgType);
        Log.i(TAG, "getAgenciesData: regionName : " + regionName);
        Log.i(TAG, "getAgenciesData: longitude : " + longitude);
        Log.i(TAG, "getAgenciesData: latitude : " + latitude);
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getAgenciesData(longitude,latitude,orgType,regionName,pageNo,pageSize, UserUtils.getIns().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AgenciesData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AgenciesData agenciesData) {
                        int code = Integer.valueOf(agenciesData.code);
                        Log.i(TAG, "onNext: 获取服务机构code ： " + code);
                        if (code == 0){
                            initData(agenciesData);
                            pageCount = agenciesData.data.pageCount;
                            //pageNo++;
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(agenciesData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyApplication.getContext(),agenciesData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e : " + e );
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void showPopupWindow1(String orgPhone, String orgName){
        if (!popupWindow1.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            popupWindow1.showAtLocation(popView1, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(getActivity(),0.7f);
            //初始化弹窗布局
            textPhoneNum.setText(orgPhone);
            textReminder.setText("拨打" + orgName + "的电话");
            //设置点击事件
            //拨打电话
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + orgPhone);
                    intent.setData(data);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 拨打电话弹窗
     */
    private void initPopupWindow1(){
        popView1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.popupwindow_pb_map_call, null);
        popupWindow1 = new PopupWindow(popView1,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnConfirm = popView1.findViewById(R.id.button_comfirm);
        Button btnCancel = popView1.findViewById(R.id.button_cancel);
        //电话好号吗
        textPhoneNum = popView1.findViewById(R.id.textView_phonenum);
        //文字提示 如 ：拨打杏花岭区党群服务中心一的预约电话？
        textReminder = popView1.findViewById(R.id.textView_reminder);


        popupWindow1.setTouchable(true);
        //点击外部消失
        popupWindow1.setOutsideTouchable(true);
        popupWindow1.setFocusable(true);

        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(getActivity(),1f);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow1.isShowing()) {
                    popupWindow1.dismiss();
                }
            }
        });

    }





}
