package com.typartybuilding.fragment.fragmentbottomnavigation;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.typartybuilding.R;
import com.typartybuilding.base.BaseFragment;

import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 *  组工之家
 */
public class FragmentPartyHome extends BaseFragment {


    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_party_home;
    }

    @OnClick({R.id.imageViewDyzj1,R.id.imageViewDyzj2,R.id.imageViewDyzj3,R.id.imageViewDyzj4,R.id.imageViewDyzj5,
            R.id.imageViewDyzj6,R.id.imageViewDyzj7,R.id.imageViewDyzj8,R.id.imageViewGbzj1,R.id.imageViewGbzj2,
            R.id.imageViewGbzj3,R.id.imageViewGbzj4,R.id.imageViewGbzj5,R.id.imageViewRczj1,R.id.imageViewRczj2,
            R.id.imageViewRczj3,R.id.imageViewRczj4,R.id.imageViewRczj5,R.id.imageViewRczj6,R.id.imageViewRczj7,
            R.id.imageViewRczj8})
    public void onClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("温馨提示");
        builder.setMessage("此功能未开放");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


}
