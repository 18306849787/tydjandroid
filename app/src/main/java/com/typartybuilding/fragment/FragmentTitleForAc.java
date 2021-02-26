package com.typartybuilding.fragment;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * activity 使用的标题栏
 */
public class FragmentTitleForAc extends BaseFragment {

    @BindView(R.id.constraintLayout_title)
    ConstraintLayout layoutTitle;

    @BindView(R.id.textView_title)
    TextView textViewTitle;

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_title_for_ac;
    }

    @OnClick(R.id.imageView_back)
    public void onClickBack(){
        getActivity().finish();
    }

    public ConstraintLayout getLayoutTitle(){
        return layoutTitle;
    }

    public TextView getTextViewTitle(){
        return textViewTitle;
    }
}
