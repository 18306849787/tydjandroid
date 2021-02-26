package com.typartybuilding.activity.second.lean;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.SlidingTabLayout;
import com.typartybuilding.R;
import com.typartybuilding.activity.second.interactive.kkk;
import com.typartybuilding.base.BaseAct;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-06
 * @describe
 */
@Route(path = PartyEducationAct.PATH)
public class PartyEducationAct extends BaseAct {
    public static final String PATH = "/activity/party_education";
    @BindView(R.id.party_education_tab)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.party_education_viewpager)
    ViewPager viewPager;


    private ArrayList<Fragment> fsRes = new ArrayList<>();
    private String[] titles = {"党员教育片","党课开讲啦","名师库","党性教育基地"};

    @Override
    public void initData() {
        fsRes.add(new PartyEducationFra());
        fsRes.add(new kkk());
        fsRes.add(new kkk());
        fsRes.add(new kkk());
        slidingTabLayout.setViewPager(viewPager,titles,this,fsRes);
    }

    @OnClick(R.id.party_education_back)
    void onClick() {
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_act_party_education;
    }
}
