package com.typartybuilding.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.typartybuilding.R;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.BaseFragmentHome;

/**
 * A simple {@link Fragment} subclass.
 *   家服务
 */
public class HomeFragmentJiaFuWu extends BaseFragmentHome {


    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_fragment_jia_fu_wu;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void stopLoad() {

    }
}
