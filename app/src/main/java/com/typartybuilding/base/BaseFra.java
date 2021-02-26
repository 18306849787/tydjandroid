package com.typartybuilding.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.alibaba.android.arouter.launcher.ARouter;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-08-09
 * @describe
 */
public abstract class BaseFra extends BaseFragment {

//    protected PageLayout mStatePageLayout;
//    protected LoadingDialog mLoadingDialog;
    protected View mView;



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ARouter.getInstance().inject(this);
        mView = view;
        setCommonStatePager();
        initData();
        loadingData();
    }


    public void loadingData() {
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    public abstract void initData();

    /**
     * 初始化公共加载空布局，错误页，网络错误，加载页
     */
    public void setCommonStatePager() {
//        if (loadingPagerHostView() != null) {
////            final ViewLoading loading = new ViewLoading(getActivity());
////            loading.setBackgroundColor(getResources().getColor(R.color.white));
//            mStatePageLayout = new PageLayout.Builder(getActivity())
//                    .initPage(loadingPagerHostView())
////                    .setLoading(loading)
//                    .setOnRetryListener(new PageLayout.OnRetryClickListener() {
//                        @Override
//                        public void onRetry() {
//                            loadingData();
//                        }
//                    })
//                    .create();
//            mStatePageLayout.setOnStateChangeListener(new PageLayout.OnStateListener() {
//                @Override
//                public void hide() {
//                }
//
//                @Override
//                public void showLoading() {
//                }
//            });
//        }
    }

    /**
     * 设置加载状态页要附着在那个View上
     *
     * @return
     */
    public Object loadingPagerHostView(){
        return null;
    }


    public void dismissLoading(){
//        if (mStatePageLayout!=null){
//            mStatePageLayout.hide();
//        }
//        if (mLoadingDialog!=null&&mLoadingDialog.isShowing()){
//            mLoadingDialog.dismiss();
//        }
    }

    public void showLoading(){
//        if (mLoadingDialog == null){
//            mLoadingDialog = new LoadingDialog(getActivity());
//        }
//        if (mLoadingDialog!=null&&!mLoadingDialog.isShowing()){
//            mLoadingDialog.show();
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

}
