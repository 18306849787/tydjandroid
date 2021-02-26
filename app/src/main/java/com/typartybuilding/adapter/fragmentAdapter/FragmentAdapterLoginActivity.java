package com.typartybuilding.adapter.fragmentAdapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class FragmentAdapterLoginActivity extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> titles;

    public FragmentAdapterLoginActivity(FragmentManager fm, List<Fragment> fragmentList, List<String>titles ) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
