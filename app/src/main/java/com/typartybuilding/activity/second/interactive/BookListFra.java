package com.typartybuilding.activity.second.interactive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.RadioGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.quanminlangdu.fragment.ItemFragment;
import com.typartybuilding.activity.quanminlangdu.fragment.ItemMyFragment;
import com.typartybuilding.activity.quanminlangdu.fragment.dummy.DummyContent;
import com.typartybuilding.activity.quanminlangdu.mac.BookListActivity;
import com.typartybuilding.activity.quanminlangdu.mac.ReadActivity;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.loginregister.UserData;
import com.typartybuilding.utils.UserUtils;
import com.typartybuilding.view.NoSlideViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-06
 * @describe 朗读
 */
public class BookListFra extends BaseFra implements ItemFragment.OnListFragmentInteractionListener {
    @BindView(R.id.book_list_fra_viewpager)
    NoSlideViewPager viewPager;
    @BindView(R.id.book_list_radio_group)
    RadioGroup mRadioGroup;
    ItemMyFragment itemMyFragment;
    private String[] mTitles = {"读单", "我的"};


    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    public void initData() {
        ItemFragment itemFragment = ItemFragment.newInstance("");
        itemFragment.setOnListFragmentInteractionListener(this);
        itemMyFragment = ItemMyFragment.newInstance(UserUtils.getIns().getUserId());
        itemMyFragment.setOnListFragmentInteractionListener(this);
        mFragments.add(itemFragment);
        mFragments.add(itemMyFragment);
        viewPager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    mRadioGroup.check(R.id.book_list_radio_group_book);
                }else if (position==1){
                    mRadioGroup.check(R.id.book_list_radio_group_mine);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.book_list_radio_group_book){
                    viewPager.setCurrentItem(0);
                }else if (i== R.id.book_list_radio_group_mine){
                    if (UserUtils.getIns().isTourist()){
                        MyApplication.remindVisitor((Activity) getContext());
                        viewPager.setCurrentItem(0);
                        mRadioGroup.check(R.id.book_list_radio_group_book);
                    }else {
                        viewPager.setCurrentItem(1);
                    }
                }
            }
        });
    }

    //界面可见时再加载数据
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&viewPager!=null) {
            if (UserUtils.getIns().isTourist()){
                viewPager.setScanScroll(false);
            }else {
                viewPager.setScanScroll(true);
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_fra_book_list;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item,int type) {
        if (UserUtils.getIns().isTourist()){
            MyApplication.remindVisitor((Activity) getContext());
        }else if (type==1){
            ARouter.getInstance().build(MyReadAct.PATH).withSerializable("item",item).navigation(getActivity());
        } else {
            Intent intent = new Intent(getActivity(), ReadActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("book", item);
            intent.putExtra("data",bundle);
            startActivity(intent);
        }
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
