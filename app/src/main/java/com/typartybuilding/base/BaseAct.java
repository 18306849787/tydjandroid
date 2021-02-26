package com.typartybuilding.base;

import android.os.Bundle;
import android.support.annotation.Nullable;


import com.alibaba.android.arouter.launcher.ARouter;
import com.typartybuilding.utils.ActivityCollectorUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;


/**
 * @author chengchunhuiit@163.com
 * @date 2020-08-22
 * @describe
 */
public abstract class BaseAct extends BaseActivity {

//    protected PageLayout mStatePageLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollectorUtil.addActivity(this);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        setCommonStatePager();
        initData();
        loadingData();
    }

    public abstract void initData();

    public abstract int getLayoutId();

    public void loadingData() {
    }

    /**
     * 初始化公共加载空布局，错误页，网络错误，加载页
     */
    public void setCommonStatePager() {
//        if (loadingPagerHostView() != null) {
////            final ViewLoading loading = new ViewLoading(getActivity());
////            loading.setBackgroundColor(getResources().getColor(R.color.white));
//            mStatePageLayout = new PageLayout.Builder(this)
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public Object loadingPagerHostView(){
        return null;
    }
}
