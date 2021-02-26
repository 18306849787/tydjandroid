package com.typartybuilding.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.fgPartyBuildingMap.FragmentEducationalBase;
import com.typartybuilding.fragment.fgPartyBuildingMap.FragmentPartyOrganization;
import com.typartybuilding.fragment.fgPartyBuildingMap.FragmentPbMap;
import com.typartybuilding.fragment.fgPartyBuildingMap.FragmentServiceCenter;
import com.typartybuilding.fragment.fgPartyBuildingMap.FragmentVolunteerService;
import com.typartybuilding.gsondata.choiceness.RecommendData;
import com.typartybuilding.gsondata.pbmap.AgenciesData;
import com.typartybuilding.gsondata.pbmap.RegionNameData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.view.WrapContentHeightViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 党建地图 页面
 */
public class HomeFragmentPartyBuildingMap extends BaseFragmentHome {

    private String TAG = "HomeFragmentPartyBuildingMap";

    @BindView(R.id.mapview)
    MapView mapView;
    @BindView(R.id.textView_site)
    TextView textSite;             //切换地址的按钮
    @BindViews({R.id.textView1, R.id.textView2, R.id.textView3})
    TextView textView [] ;          //三个标签，基层党组织， 党群服务中心， 教育基地


    private List<Fragment> fragmentList = new ArrayList<>();
    //切换地址的选择器
    private OptionsPickerView pvOptions;
    //地址数据
    private List<String> siteList = new ArrayList<>();

    private boolean isDestroy;

    private double longitude;     //当前位置经度
    private double latitude;      //当前位置维度
    private String regionName;    //区域名称

    private AMap aMap = null;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    public List<Marker> markerList = new ArrayList<>();

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //设置用户已选择的地址
        String site = MyApplication.pref.getString(MyApplication.prefKey2,"");
        if (site != ""){
            textSite.setText(site);
            regionName = site;
        }else {
            regionName = textSite.getText().toString();
        }
        //获取地位 经纬度
        getLocation();
        //初始化地图
        initMapView(savedInstanceState);

        initFragmentList();
        //导航第一个默认被选中
        textView[0].setSelected(true);

        loadFragment(0);

        isDestroy = false;
        //初始化 地址数据
        getRegionName();
        //判断是否获取了权限
        requestPermission();

    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_party_building_map;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void stopLoad() {

    }

    @Override
    public void onDestroyView() {
        isDestroy = true;

        if (mapView != null){
            Log.i(TAG, "onDestroyView: mapView : " + mapView);
            mapView.onDestroy();
            Log.i(TAG, "onDestroyView: mapView : " + mapView);
        }

        if (aMap != null){
            aMap = null;
        }

        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        super.onDestroyView();

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    private void initFragmentList(){
       /* fragment1 = new FragmentPartyOrganization();
        fragment2 = new FragmentServiceCenter();
        fragment3 = new FragmentEducationalBase();*/
        //基层党组织
        FragmentPbMap fragment1 = new FragmentPbMap();
        fragment1.setFlag(1);
        //党群服务中心
        FragmentPbMap fragment2 = new FragmentPbMap();
        fragment2.setFlag(2);
        //教育基地
        FragmentPbMap fragment3 = new FragmentPbMap();
        fragment3.setFlag(3);
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);

    }

    /**
     * 动态加载FragMent
     */
    private void loadFragment(int i){
        FragmentTransaction transaction;
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_pb_map,fragmentList.get(i));
        transaction.commit();
    }

    public void switchFragment(int i) {

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();/*.setCustomAnimations(
                android.R.anim.fade_in, R.anim.slide_in_from_bottom);*/
        if (!fragmentList.get(i).isAdded()) {    // 先判断是否被add过
            transaction.add(R.id.framelayout_pb_map, fragmentList.get(i)).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.show(fragmentList.get(i)).commit(); // 隐藏当前的fragment，显示下一个
        }
        for (int j = 0; j < 3; j ++){
            if (j != i){
                if (fragmentList.get(j).isVisible()){
                    transaction.hide(fragmentList.get(j));
                }
            }
        }
        //刷新数据
        FragmentPbMap fragmentPbMap = ( FragmentPbMap)fragmentList.get(i);
        if (longitude == 0 && latitude == 0){
            longitude = MyApplication.longitude;
            latitude = MyApplication.latitude;
        }
        fragmentPbMap.getAgenciesData(longitude,latitude,regionName);

    }

    /**
     * 更改 导航栏的点击状态
     */
    private void changeState(int j){
        for (int i = 0; i < 3; i++){
            if (i != j){
                textView[i].setSelected(false);
            }
        }
    }

    /**
     * 导航栏 的三个标签 基层党组织 党群服务中心 全市党性教育基地
     * @param view
     */
    @OnClick({R.id.textView1, R.id.textView2, R.id.textView3/*, R.id.textView4*/})
    public void onClickTextView(View view){
        switch (view.getId()){
            case R.id.textView1 :
                textView[0].setSelected(true);
                changeState(0);
                //loadFragment(0);
                switchFragment(0);
                break;
            case R.id.textView2 :
                textView[1].setSelected(true);
                changeState(1);
                //loadFragment(1);
                switchFragment(1);
                break;
            case R.id.textView3 :
                textView[2].setSelected(true);
                changeState(2);
                //loadFragment(2);
                switchFragment(2);
                break;

            default:
                break;
        }
    }

    /**
     * 点击，弹出 切换地址的窗口
     */
    @OnClick(R.id.textView_site)
    public void onClickSite(){

       initOptionsPicker();

    }

    private void initOptionsPicker(){
        //条件选择器
        pvOptions = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                if (siteList != null && siteList.size() > 0) {
                    String site = siteList.get(options1);
                    textSite.setText(site);
                    MyApplication.editor.putString(MyApplication.prefKey2, site);
                    MyApplication.editor.apply();
                    //设置区域后，刷新数据
                    regionName = site;
                    for (int i = 0; i < textView.length; i++) {
                        if (textView[i].isSelected()) {
                            if (longitude == 0 && latitude == 0) {
                                longitude = MyApplication.longitude;
                                latitude = MyApplication.latitude;
                            }
                            FragmentPbMap fragmentPbMap = (FragmentPbMap) fragmentList.get(i);
                            fragmentPbMap.getAgenciesData(longitude, latitude, regionName);
                            break;
                        }
                    }
                }else {
                    Toast.makeText(getActivity(),"地区为空",Toast.LENGTH_SHORT).show();
                }


            }
        })
                .setSelectOptions(3)
                .setOutSideCancelable(false)
                .build();
        pvOptions.setPicker(siteList);
        pvOptions.show();
    }

    /**
     *  根据后台获取的 纬度和经度 设置标记
     * @param latitude
     * @param longitude
     */
    public void setMarker(double latitude,double longitude){
        //Log.i(TAG, "setMarker: latitude : " + latitude);
        //Log.i(TAG, "setMarker: longitude : " + longitude);
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        //markerOption.title("").snippet("");

        markerOption.draggable(false);//设置Marker不可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(MyApplication.getContext().getResources(),R.mipmap.icon_djdt_house)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果

        if (aMap != null) {
            Marker marker = aMap.addMarker(markerOption);
            marker.showInfoWindow();
            markerList.add(marker);
        }

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
                    //Log.i(TAG, "onLocationChanged: longitude : " + longitude);
                    //Log.i(TAG, "onLocationChanged: latitude : " + latitude);
                    if (latitude == 0 && latitude == 0){
                        latitude = MyApplication.latitude;
                        longitude = MyApplication.longitude;
                    }
                    //刷新数据
                    for (int i = 0; i < textView.length; i++){
                        if (textView[i].isSelected()){
                            FragmentPbMap fragmentPbMap = (FragmentPbMap)fragmentList.get(i);
                            fragmentPbMap.getAgenciesData(longitude,latitude,regionName);
                            break;
                        }
                    }
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

    /**
     * 初始化地图
     * @param savedInstanceState
     */
    private void initMapView(Bundle savedInstanceState){

        Log.i(TAG, "initMapView: mapView : "  + mapView);
        mapView.onCreate(savedInstanceState);

        if (aMap == null){
            aMap = mapView.getMap();
        }

        Log.i(TAG, "initMapView: aMap : " + aMap);

        if (aMap != null) {
            //显示指定位置 ，太原
            LatLng latLng = new LatLng(MyApplication.latitude, MyApplication.longitude);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
            UiSettings uiSet = aMap.getUiSettings();
            uiSet.setAllGesturesEnabled(false);
            //是否允许显示缩放按钮
            uiSet.setZoomControlsEnabled(false);

            MyLocationStyle myLocationStyle;
            myLocationStyle = new MyLocationStyle();
            myLocationStyle.interval(2000);
            aMap.setMyLocationStyle(myLocationStyle);
            //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        }
    }


    private void initSiteList(RegionNameData regionNameData){
        if (!isDestroy){
            if (regionNameData != null){
                RegionNameData.Data regionNames [] = regionNameData.data;
                if (siteList != null && siteList.size() > 0){
                    siteList.clear();
                }
                if (regionNames != null && regionNames.length > 0){
                    for (int i = 0; i < regionNames.length; i++){
                        siteList.add(regionNames[i].organName);

                    }
                }

            }
        }
    }

    private void getRegionName(){
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getRegionName(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegionNameData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RegionNameData regionNameData) {
                        int code = Integer.valueOf(regionNameData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            initSiteList(regionNameData);

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(regionNameData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyApplication.getContext(),regionNameData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        e.printStackTrace();
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



    private void requestPermission() {

        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getActivity(), permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), permissions, 1);
            }
        }
    }






     /* private void initSiteData(){
        siteList.add(getResources().getString(R.string.text1));
        siteList.add(getResources().getString(R.string.text2));
        siteList.add(getResources().getString(R.string.text3));
        siteList.add(getResources().getString(R.string.text4));
        siteList.add(getResources().getString(R.string.text5));
        siteList.add(getResources().getString(R.string.text6));
        siteList.add(getResources().getString(R.string.text7));
        siteList.add(getResources().getString(R.string.text8));
        siteList.add(getResources().getString(R.string.text9));
        siteList.add(getResources().getString(R.string.text10));
        siteList.add(getResources().getString(R.string.text11));
        siteList.add(getResources().getString(R.string.text12));

     *//*   siteList.add(getResources().getString(R.string.text13));
        siteList.add(getResources().getString(R.string.text14));
        siteList.add(getResources().getString(R.string.text15));
        siteList.add(getResources().getString(R.string.text16));
        siteList.add(getResources().getString(R.string.text17));
        siteList.add(getResources().getString(R.string.text18));
        siteList.add(getResources().getString(R.string.text19));
        siteList.add(getResources().getString(R.string.text20));*//*

    }*/


  /*  private void initData(AgenciesData agenciesData){
        if (!isDestroy){
            AgenciesData.AgencyData data[] = agenciesData.data.rows;
            if (data != null){
                //`orgType`:1,1：党组织机构，2：党群服务中心，3：教育基地',
                if (data[0].orgType == 1){
                    if (dataListPo.size() > 0){
                        dataListPo.clear();
                    }
                    for (int i = 0; i < data.length; i++){
                        dataListPo.add(data[i]);
                    }
                    fragment1.initData(dataListPo);

                }else if (data[0].orgType == 2){
                    if (dataListSc.size() > 0){
                        dataListSc.clear();
                    }
                    for (int i = 0; i < data.length; i++){
                        dataListSc.add(data[i]);
                    }
                    fragment2.initData(dataListSc);

                }else if (data[0].orgType == 3){
                    if (dataListEb.size() > 0){
                        dataListEb.clear();
                    }
                    for (int i = 0; i < data.length; i++){
                        dataListEb.add(data[i]);
                    }
                    fragment3.initData(dataListEb);
                }

            }


        }
    }*/

   /* public void getAgenciesData(int orgType,String address){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getAgenciesData(orgType,token)
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
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(agenciesData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyApplication.getContext());
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
    }*/



}
