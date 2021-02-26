package com.typartybuilding.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Description描述:
 * @Author作者:
 * @Date日期:
 */
public abstract class BaseFragmentHome extends Fragment {

    private String TAG = "BaseFragmentHome";
    Unbinder unbinder;

    private boolean isInit;       //是否已经初始化，处理预加载问题，让fragment懒加载
    private boolean isLoad;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews(savedInstanceState);
        isInit = true;
        isCanLoadData();
        Log.i(TAG, "onActivityCreated: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        isLoad = false;
        isInit = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
        Log.i(TAG, "setUserVisibleHint: isVisibleToUser : " + isVisibleToUser);
    }

    private void isCanLoadData(){
        if (!isInit){
            return;
        }
        //判断视图对用户是否可见
        if (getUserVisibleHint()){
            if (!isLoad) {
                lazyLoad();
                isLoad = true;
            }

        }else {
            if (isLoad){
                stopLoad();
            }
        }
    }

    protected abstract void lazyLoad();

    protected abstract void stopLoad();

    protected abstract void initViews(Bundle savedInstanceState);

    public abstract int getLayoutId();
}
