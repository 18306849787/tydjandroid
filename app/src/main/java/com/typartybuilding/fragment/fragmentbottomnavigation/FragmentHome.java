package com.typartybuilding.fragment.fragmentbottomnavigation;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.PopupWindowCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.choiceness.SearchActivity;
import com.typartybuilding.activity.myRelatedActivity.AnswerBreakthroughActivity;
import com.typartybuilding.activity.myRelatedActivity.SmartAnswerActivityNew;
import com.typartybuilding.activity.plusRelatedActivity.Camera2Activity;
import com.typartybuilding.activity.quanminlangdu.mac.BookListActivity;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.fragment.HomeFragmentBasicPartyBuildingNew;
import com.typartybuilding.fragment.HomeFragmentChoicenessNew1;
import com.typartybuilding.fragment.HomeFragmentDreamWishNew;
import com.typartybuilding.fragment.HomeFragmentJiaFuWu;
import com.typartybuilding.fragment.HomeFragmentLearningTime;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingLibraryNew;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingMap;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingVideoNew1;
import com.typartybuilding.fragment.HomeFragmentTyOrganization;
import com.typartybuilding.fragment.HomeFragmentWenZhengWu;
import com.typartybuilding.fragment.fgLearningTime.FragmentEducationFilm;
import com.typartybuilding.fragment.fgLearningTime.FragmentLearnTimeNew;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.NoSlideViewPager;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FragmentHome extends BaseFragment {

    private String TAG = "FragmentHome";

    @BindView(R.id.tablayout_fragment_home)
    public TabLayout tabLayout;
    @BindView(R.id.constraintLayout_home)
    public ConstraintLayout homeTitle;
    @BindView(R.id.viewpager_fragment_home)
    public NoSlideViewPager viewPager;
    @BindView(R.id.imageView_search)
    ImageView imageViewSearch;    //搜索
    @BindView(R.id.imageView_option)
    ImageView imageViewOption;    //选项
    @BindView(R.id.viewStatusBar)
    View viewStatusBar;           //状态栏的高度

    private PopupWindow popupWindow;  //右上角弹窗
    private View popView;             //弹窗布局

    private List<String> fragmentNames = new ArrayList<>();
    private List<Class> fragmentList = new ArrayList<>();

    private List<String> tabStrs = new ArrayList<>();
    private List<Drawable> drawableList0 = new ArrayList<>();    //未选中
    private List<Drawable> drawableList1 = new ArrayList<>();    //选中

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //动态设置占位布局的高度，该布局覆盖状态栏的高度，用于适配
        setViewStatusBarHeight();
        //设置viewpager可以左右滑动
        viewPager.setScanScroll(true);
        initTabs();
        initFragment();
        setViewPagerAdapter();
        tabLayout.getTabAt(0).select();
        setPagerChangeListener();
        setTabSelectedListener();
        //初始化弹窗
        initPopupWindow();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_ha;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    //设置状态栏的高度，动态设置，适配各种屏幕
    private void setViewStatusBarHeight(){
        int statusBarHeight = Utils.getStatusBarHeight();
        int barDp = Utils.px2dip(getActivity(),statusBarHeight);
        Log.i(TAG, "setViewStatusBarHeight: barDp : " + barDp);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) viewStatusBar.getLayoutParams();
        layoutParams.height = statusBarHeight;
        viewStatusBar.setLayoutParams(layoutParams);
    }

    //点击底部首页按钮，若页面未在精选页面，自动回到精选页面
    private void setViewPagerItem(){
        int itemCurrent = viewPager.getCurrentItem();
        Log.i(TAG, "setViewPagerItem: currentItem : " + itemCurrent);
        if (itemCurrent != 0){
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            //自动回到精选页面
            //setViewPagerItem();
        }
        if (viewPager != null && viewPager.getCurrentItem() == 1){
            HomeFragmentLearningTime learningTime = null;
            FragmentStatePagerAdapter pagerAdapter = (FragmentStatePagerAdapter)viewPager.getAdapter();
            learningTime = (HomeFragmentLearningTime)pagerAdapter.instantiateItem(viewPager,1);
            if (learningTime != null){
                List<Fragment> fragmentList = learningTime.fragmentList;
                int currentFg = learningTime.currentFg;
                if (!hidden) {
                    if (fragmentList.size() > 0) {
                        if (currentFg == 0) {
                            ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onResume();
                        } else if (currentFg == 1 || currentFg == 3) {
                            ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onResume();
                        }else if (currentFg == 2){
                            ((FragmentEducationFilm) fragmentList.get(currentFg)).onResume();
                        }
                    }

                } else {
                    if (fragmentList.size() > 0) {
                        if (currentFg == 0) {
                            ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onPause();
                        } else if (currentFg == 1 || currentFg == 3) {
                            ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onPause();
                        }else if (currentFg == 2){
                            ((FragmentEducationFilm) fragmentList.get(currentFg)).onPause();
                        }
                    }
                }
            }
        }
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (viewPager != null && viewPager.getCurrentItem() == 1){
            HomeFragmentLearningTime learningTime = null;
            FragmentStatePagerAdapter pagerAdapter = (FragmentStatePagerAdapter)viewPager.getAdapter();
            learningTime = (HomeFragmentLearningTime)pagerAdapter.instantiateItem(viewPager,1);
            if (learningTime != null){
                List<Fragment> fragmentList = learningTime.fragmentList;
                int currentFg = learningTime.currentFg;

                if (isVisibleToUser) {
                    if (fragmentList.size() > 0) {
                        if (currentFg == 0) {

                            ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onResume();
                        } else if (currentFg == 1 || currentFg == 3) {
                            ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onResume();
                        }else if (currentFg == 2){
                            ((FragmentEducationFilm) fragmentList.get(currentFg)).onResume();
                        }
                    }

                } else {
                    if (fragmentList.size() > 0) {
                        if (currentFg == 0) {

                            ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onPause();
                        } else if (currentFg == 1 || currentFg == 3) {
                            ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onPause();
                        }else if (currentFg == 2){
                            ((FragmentEducationFilm) fragmentList.get(currentFg)).onPause();
                        }
                    }
                }
            }
        }
    }*/

    private void initPopupWindow(){
        popView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popupwindow_option, null);
        TextView textPws = popView.findViewById(R.id.textView_pws);  //拍微视
        TextView textLdt = popView.findViewById(R.id.textView_ldt);  //朗读厅
        TextView textZsjd = popView.findViewById(R.id.textView_zsjd);     //知识竞答
        TextView textZscg = popView.findViewById(R.id.textView_zscg);     //知识闯关

        popupWindow = new PopupWindow(popView,
                Utils.dip2px(getActivity(),116), Utils.dip2px(getActivity(),166));

        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        //拍微视
        textPws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(getActivity(), Camera2Activity.class);
                startActivity(intentAc);
            }
        });
        //朗读厅
        textLdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(getActivity(), BookListActivity.class);
                startActivity(intentAc);
            }
        });
        //知识竞答
        textZsjd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SmartAnswerActivityNew.class);
                startActivity(intent);
            }
        });
        //知识闯关
        textZscg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AnswerBreakthroughActivity.class);
                startActivity(intent);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    @OnClick(R.id.imageView_option)
    public void optionClick(){
        if (popupWindow != null && !popupWindow.isShowing()){
            int x = Utils.dip2px(getActivity(),92);
            //popupWindow.showAsDropDown(imageViewOption,-100,0);
            //popupWindow.update();
            PopupWindowCompat.showAsDropDown(popupWindow,imageViewOption,-x,0,Gravity.CENTER);
        }
    }

    @OnClick(R.id.imageView_search)
    public void searchClick(){
        Intent intentAc = new Intent(getActivity(), SearchActivity.class);
        startActivity(intentAc);
    }

    private void initDrawable(){
        //为选中
        Drawable drawable10 = getResources().getDrawable(R.mipmap.new_two_d1_0,null);  //荐/精选
        Drawable drawable20 = getResources().getDrawable(R.mipmap.new_two_d2_0,null);  //学/习语
        Drawable drawable30 = getResources().getDrawable(R.mipmap.new_two_d3_0,null);  //看/微视
        Drawable drawable40 = getResources().getDrawable(R.mipmap.new_two_d4_0,null);  //听/党音
        Drawable drawable50 = getResources().getDrawable(R.mipmap.new_two_d9_0,null); //家服务
        Drawable drawable60 = getResources().getDrawable(R.mipmap.new_two_d5_0,null); //帮/心愿
        Drawable drawable70 = getResources().getDrawable(R.mipmap.new_two_d6_0,null); //推/组工
        Drawable drawable80 = getResources().getDrawable(R.mipmap.new_two_d10_0,null); // 问/政务
        Drawable drawable90 = getResources().getDrawable(R.mipmap.new_two_d7_0,null);  //夯/基层
        Drawable drawable100 = getResources().getDrawable(R.mipmap.new_two_d8_0,null);  //查/地图
        drawableList0.add(drawable10);
        drawableList0.add(drawable20);
        drawableList0.add(drawable30);
        drawableList0.add(drawable40);
        drawableList0.add(drawable50);
        drawableList0.add(drawable60);
        drawableList0.add(drawable70);
        drawableList0.add(drawable80);
        drawableList0.add(drawable90);
        drawableList0.add(drawable100);
        //选中
        Drawable drawable11 = getResources().getDrawable(R.mipmap.new_two_d1_1,null);
        Drawable drawable21 = getResources().getDrawable(R.mipmap.new_two_d2_1,null);
        Drawable drawable31 = getResources().getDrawable(R.mipmap.new_two_d3_1,null);
        Drawable drawable41 = getResources().getDrawable(R.mipmap.new_two_d4_1,null);
        Drawable drawable51 = getResources().getDrawable(R.mipmap.new_two_d9_1,null);
        Drawable drawable61 = getResources().getDrawable(R.mipmap.new_two_d5_1,null);
        Drawable drawable71 = getResources().getDrawable(R.mipmap.new_two_d6_1,null);
        Drawable drawable81 = getResources().getDrawable(R.mipmap.new_two_d10_1,null);
        Drawable drawable91 = getResources().getDrawable(R.mipmap.new_two_d7_1,null);
        Drawable drawable101 = getResources().getDrawable(R.mipmap.new_two_d8_1,null);
        drawableList1.add(drawable11);
        drawableList1.add(drawable21);
        drawableList1.add(drawable31);
        drawableList1.add(drawable41);
        drawableList1.add(drawable51);
        drawableList1.add(drawable61);
        drawableList1.add(drawable71);
        drawableList1.add(drawable81);
        drawableList1.add(drawable91);
        drawableList1.add(drawable101);
    }

    private void initTabs(){
        initDrawable();
        //fragmentNames.add(HomeFragmentChoiceness.class.getCanonicalName());
        /*fragmentNames.add(HomeFragmentChoicenessNew.class.getCanonicalName());
        fragmentNames.add(HomeFragmentLearningTime.class.getCanonicalName());
        fragmentNames.add(HomeFragmentPartyBuildingVideo.class.getCanonicalName());
        fragmentNames.add(HomeFragmentPartyBuildingLibrary.class.getCanonicalName());
        fragmentNames.add(HomeFragmentDreamWish.class.getCanonicalName());
        fragmentNames.add(HomeFragmentTyOrganization.class.getCanonicalName());
        fragmentNames.add(HomeFragmentBasicPartyBuilding.class.getCanonicalName());
        fragmentNames.add(HomeFragmentPartyBuildingMap.class.getCanonicalName());*/

        for (int j = 0; j < drawableList0.size(); j++){
            TabLayout.Tab tab = tabLayout.newTab();
            ImageView imageView = new ImageView(getActivity());
            if (j == 0){
                imageView.setImageDrawable(drawableList1.get(0));
            }else {
                imageView.setImageDrawable(drawableList0.get(j));
            }

            tabLayout.addTab(tab.setCustomView(imageView));
            //去掉tab 点击的 灰色阴影
            //tabLayout.setTabRippleColor(ColorStateList.valueOf(getContext().getResources().getColor(R.color.white)));
        }
    }

    private void initFragment(){

        //精选
        //fragmentList.add(HomeFragmentChoiceness.class);
        //fragmentList.add(HomeFragmentChoicenessNew.class);
        fragmentList.add(HomeFragmentChoicenessNew1.class);
        //学习时刻
        fragmentList.add(HomeFragmentLearningTime.class);
        //党建微视
        //fragmentList.add(HomeFragmentPartyBuildingVideo.class);
        //fragmentList.add(HomeFragmentPartyBuildingVideoNew.class);
        fragmentList.add(HomeFragmentPartyBuildingVideoNew1.class);
        //党建书屋
        //fragmentList.add(HomeFragmentPartyBuildingLibrary.class);
        fragmentList.add(HomeFragmentPartyBuildingLibraryNew.class);
        //(二期)家门口（家服务）
        fragmentList.add(HomeFragmentJiaFuWu.class);
        //圆梦心愿
        //fragmentList.add(HomeFragmentDreamWish.class);
        fragmentList.add(HomeFragmentDreamWishNew.class);
        //太原组工
        fragmentList.add(HomeFragmentTyOrganization.class);
        //(二期)太原政务(问/政务)
        fragmentList.add(HomeFragmentWenZhengWu.class);
        //fragmentList.add(HomeFragmentBasicPartyBuilding.class);
        fragmentList.add(HomeFragmentBasicPartyBuildingNew.class);
        fragmentList.add(HomeFragmentPartyBuildingMap.class);
    }

    private void setViewPagerAdapter(){

        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                //String fragmentName = tabInfos.get(i).fragmentName;
                //return Fragment.instantiate(getActivity(),fragmentNames.get(i));
                //return fragmentList.get(i);
                try {
                    return (Fragment) fragmentList.get(i).newInstance();
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                }catch (java.lang.InstantiationException e){
                    e.printStackTrace();
                }
                return null;

            }

            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }

        });
    }

    private void setPagerChangeListener(){
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void setTabSelectedListener(){
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                View imageView = tab.getCustomView();
                if (null != imageView && imageView instanceof ImageView){
                    ((ImageView)imageView).setImageDrawable(drawableList1.get(pos));
                }
                viewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                View imageView = tab.getCustomView();
                if (null != imageView && imageView instanceof ImageView){
                    ((ImageView)imageView).setImageDrawable(drawableList0.get(pos));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
