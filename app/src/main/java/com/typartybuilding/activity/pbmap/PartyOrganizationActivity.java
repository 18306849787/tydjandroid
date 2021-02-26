package com.typartybuilding.activity.pbmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.gsondata.pbmap.AgenciesData;
import com.typartybuilding.utils.MapUtil;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PartyOrganizationActivity extends RedTitleBaseActivity {

    private String TAG = "PartyOrganizationActivity";

    @BindView(R.id.textView_title)
    TextView textTitle;

    @BindView(R.id.mapview)
    MapView mapView;
    @BindView(R.id.textView_headline)
    TextView headLine;                //标题
    @BindView(R.id.imageButton_navigation)
    ImageView navigation;              //跳转到第三方导航
    @BindView(R.id.textView_site)
    TextView textSite;                //地点
    @BindView(R.id.textView_time)
    TextView textTime;                //工作时间
    @BindView(R.id.textView_linkman)
    TextView linkMan;                 //联系人

    /*@BindView(R.id.textView_phonenum)
    TextView phoneNum;                //联系电话*/

    public PopupWindow popupWindow;  //底部弹窗  ，跳转外部导航弹窗
    public View popView;             //弹窗布局

    public PopupWindow popupWindow1;  //底部弹窗  ,拨打电话弹窗
    public View popView1;             //弹窗布局

    private double longitude;     //当前位置经度
    private double latitude;      //当前位置维度

    private AMap aMap = null;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;

    //这里的经纬度是直接获取的，在实际开发中从应用的地图中获取经纬度;
  /*  private double latx = 39.9037448095;
    private double laty = 116.3980007172;
    private String mAddress = "北京";*/


    private AgenciesData.AgencyData agencyData;
    String phoneStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_organization);
        ButterKnife.bind(this);
        //初始化地图
        initMapView(savedInstanceState);
        //获取定位
        getLocation();
        //获取上一个activity传递的数据
        Intent intent = getIntent();
        agencyData = (AgenciesData.AgencyData)intent.getSerializableExtra("AgencyData");
        if (agencyData != null) {
            textTitle.setText(agencyData.orgName);
            //设置标记
            setMarker(agencyData.orgLatitude,agencyData.orgLongitude);
        }
        //初始化跳转外部导航的弹窗
        initPopupWindow();
        //初始化拨打电话弹窗
        initPopupWindow1();
        //初始化页面
        initView();
        //判断是否获取了权限
        requestPermission();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        if (aMap != null){
            aMap = null;
        }
        if (mLocationClient != null){
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void initView(){
        if (agencyData != null){
            headLine.setText(agencyData.orgName);
            textSite.setText(agencyData.orgAddress);
            textTime.setText(agencyData.businessHours);
            linkMan.setText(agencyData.orgContacts);
            //phoneNum.setText(agencyData.orgPhone);
        }
    }

    /**
     * 拨打电话的按钮，弹出提示框
     */
    @OnClick(R.id.imagebutton_phone)
    public void onClickPhone(){
        if (!popupWindow1.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            popupWindow1.showAtLocation(popView1, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
    }

    /**
     * 跳转到第三方导航,弹出一个窗口提示
     */
    @OnClick(R.id.imageButton_navigation)
    public void onClickNavigation(){
        if (!popupWindow.isShowing()){
            //popupWindow.showAsDropDown(layoutBottom,0,0, Gravity.TOP);
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
    }

    /**
     * 外部导航弹窗
     */
    private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_pb_map_navigation, null);
        Button btnMap = popView.findViewById(R.id.button_map);
        Button btnCancel = popView.findViewById(R.id.button_cancel);

        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(PartyOrganizationActivity.this,1f);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MapUtil.isGdMapInstalled()){
                    MapUtil.openGaoDeNavi(PartyOrganizationActivity.this, 0, 0, null,
                            agencyData.orgLatitude, agencyData.orgLongitude, agencyData.orgAddress);
                }else {
                    Toast.makeText(PartyOrganizationActivity.this, "尚未安装高德地图", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 拨打电话弹窗
     */
    private void initPopupWindow1(){
        popView1 = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_pb_map_call, null);
        popupWindow1 = new PopupWindow(popView1,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Button btnConfirm = popView1.findViewById(R.id.button_comfirm);
        Button btnCancel = popView1.findViewById(R.id.button_cancel);
        //电话好号吗
        TextView textPhoneNum = popView1.findViewById(R.id.textView_phonenum);
        //文字提示 如 ：拨打杏花岭区党群服务中心一的预约电话？
        TextView textReminder = popView1.findViewById(R.id.textView_reminder);

        if (agencyData != null) {
            phoneStr = agencyData.orgPhone;
            textPhoneNum.setText(agencyData.orgPhone);
            textReminder.setText("拨打" + agencyData.orgName + "的电话");
        }

        popupWindow1.setTouchable(true);
        //点击外部消失
        popupWindow1.setOutsideTouchable(true);
        popupWindow1.setFocusable(true);

        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(PartyOrganizationActivity.this,1f);
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

        //拨打电话
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phoneStr);
                intent.setData(data);
                startActivity(intent);
            }
        });
    }

    /**
     *  设置标记
     * @param latitude
     * @param longitude
     */
    public void setMarker(double latitude,double longitude){
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        //markerOption.title(agencyData.orgName).snippet("");

        markerOption.draggable(false);//设置Marker不可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.mipmap.icon_djdt_house)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果

        Marker marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();

    }


    /**
     *  获取定位
     */
    private void getLocation(){
        mLocationClient = new AMapLocationClient(MyApplication.getContext());
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null){
                    if (aMapLocation.getErrorCode() == 0) {
                        //可在其中解析amapLocation获取相应内容。
                    }else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                    longitude = aMapLocation.getLongitude();
                    latitude = aMapLocation.getLatitude();
                    Log.i(TAG, "onLocationChanged: longitude : " + longitude);
                    Log.i(TAG, "onLocationChanged: latitude : " + latitude);

                }
            }
        });
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        mLocationOption.setInterval(10000);
        if(null != mLocationClient){
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    private void initMapView(Bundle savedInstanceState){
        mapView.onCreate(savedInstanceState);
        if (aMap == null){
            aMap = mapView.getMap();
        }

        //显示指定位置 ，太原
        LatLng latLng = new LatLng(MyApplication.latitude,MyApplication.longitude);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));

        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        UiSettings uiSet = aMap.getUiSettings();
        uiSet.setAllGesturesEnabled(false);

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000);
        aMap.setMyLocationStyle(myLocationStyle);
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

    }

    private void requestPermission() {

        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }


}