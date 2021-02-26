package com.typartybuilding.fragment.fragmentbottomnavigation;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.typartybuilding.R;
import com.typartybuilding.activity.second.interactive.kkk;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.fragment.HomeFragmentDreamWishNew;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingMap;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-06
 * @describe
 */
public class ServiceFra extends BaseFra {
    @BindView(R.id.service_com_tab)
    SlidingTabLayout slidingTabLayout;

    @BindView(R.id.service_viewpager)
    ViewPager viewPager;

    private ArrayList<Fragment> fsRes = new ArrayList<>();
    private String[] titles = {"圆梦心愿","党建地图","便民生活","人才之家"};

    @Override
    public void initData() {
        Typeface typeface = ResourcesCompat.getFont(getActivity(),R.font.sourcehansanscn_heavy);
        fsRes.add(new HomeFragmentDreamWishNew());
        fsRes.add(new HomeFragmentPartyBuildingMap());
        fsRes.add(new kkk());
        fsRes.add(new kkk());
        slidingTabLayout.setViewPager(viewPager,titles,getActivity(),fsRes);
        for (int i=0;i<titles.length;i++){
            TextView textView = slidingTabLayout.getTitleView(i);
            textView.setIncludeFontPadding(false);
            textView.setTypeface(typeface);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_fra_service;
    }
}
