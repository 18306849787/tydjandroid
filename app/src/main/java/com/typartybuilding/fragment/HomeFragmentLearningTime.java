package com.typartybuilding.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.fragment.fgLearningTime.FragmentDistanceEducation;
import com.typartybuilding.fragment.fgLearningTime.FragmentEducationFilm;
import com.typartybuilding.fragment.fgLearningTime.FragmentLearnTimeNew;
import com.typartybuilding.fragment.fragmentbottomnavigation.FragmentHome;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 学习时刻 页面
 */
public class HomeFragmentLearningTime extends BaseFragmentHome {

    private String TAG = "HomeFragmentLearningTime";

    @BindView(R.id.horizontal_scrollView)
    HorizontalScrollView horizontalScrollView;

    @BindViews({R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5})
    TextView textView[];             //五个标签，直播，学习新思想，党员教育片，时代先锋，远程教育
    @BindView(R.id.framelayout)
    FrameLayout frameLayout;

    //private FragmentTransaction transaction;
    public List<Fragment> fragmentList = new ArrayList<>();

    public FragmentHome parentFg;
    public HomeActivity homeActivity;

    public int currentFg;      //当前所在的fragment


    @Override
    protected void initViews(Bundle savedInstanceState) {
        parentFg = (FragmentHome) getParentFragment();
        homeActivity = (HomeActivity) getActivity();
        initFragment();
        //导航第一个默认被选中
        textView[0].setSelected(true);
        loadFragment(0);
    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_learning_time;
    }

    @Override
    protected void lazyLoad() {
        /*parentFg = (FragmentHome) getParentFragment();
        homeActivity = (HomeActivity) getActivity();
        initFragment();
        //导航第一个默认被选中
        textView[0].setSelected(true);
        loadFragment(0);*/
    }

    @Override
    protected void stopLoad() {

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    //隐藏当前Fragment和显示将要展示的Fragment时，会分别调用,setUserVisibleHint(false),setUserVisibleHint(true)
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //相当于Fragment的onResume,

        if (isVisibleToUser) {
            if (fragmentList.size() > 0) {
                if (currentFg == 0) {
                    ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onResume();
                } else if (currentFg == 1 || currentFg == 3) {
                    ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onResume();
                }else if (currentFg == 2){
                    ((FragmentEducationFilm) fragmentList.get(currentFg)).onResume();
                }
            }

        } else {
            if (fragmentList.size() > 0) {
                if (currentFg == 0) {
                    ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onPause();
                } else if (currentFg == 1 || currentFg == 3) {
                    ((FragmentLearnTimeNew) fragmentList.get(currentFg)).onPause();
                }else if (currentFg == 2){
                    ((FragmentEducationFilm) fragmentList.get(currentFg)).onPause();
                }
            }
        }

    }

    //横屏播放视频布局
    public void landscapeLayout(){
        horizontalScrollView.setVisibility(View.GONE);    //直播，学习新思想，所在导航栏
        parentFg.homeTitle.setVisibility(View.GONE);
        //parentFg.tabLayout.setVisibility(View.GONE);      //精选，学习时刻，  所在导航栏
        //homeActivity.frameLayoutTitle.setVisibility(View.GONE);  //标题栏
        homeActivity.layoutBottom.setVisibility(View.GONE);      //底部导航栏
    }

    //竖屏播放视频布局
    public void portraitLayout(){
        horizontalScrollView.setVisibility(View.VISIBLE);
        parentFg.homeTitle.setVisibility(View.VISIBLE);
        //parentFg.tabLayout.setVisibility(View.VISIBLE);
        //homeActivity.frameLayoutTitle.setVisibility(View.VISIBLE);  //标题栏
        homeActivity.layoutBottom.setVisibility(View.VISIBLE);      //底部导航栏
    }

    private void initFragment(){
        //articleType   3：党建基层，5：学习领袖，6：党员教育片，7：时代先锋，8：直播

        //直播
        FragmentLearnTimeNew fragment1 = new FragmentLearnTimeNew();
        fragment1.setFlag(8);

        //学习新思想
        //flag  1 ,学习新思想， 2， 党员教育片  3，时代先锋
        FragmentLearnTimeNew fragment2 = new FragmentLearnTimeNew();
        fragment2.setFlag(5);

        //党员教育片
        FragmentEducationFilm fragment3 = new FragmentEducationFilm();

        //时代先锋
        FragmentLearnTimeNew fragment4 = new FragmentLearnTimeNew();
        fragment4.setFlag(7);

        //远程教育
        FragmentDistanceEducation fragment5 = new FragmentDistanceEducation();  //远程教育

        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);
        fragmentList.add(fragment4);
        fragmentList.add(fragment5);
    }

    /**
     * 动态加载FragMentTitle
     */
    private void loadFragment(int i){
        currentFg = i;
        FragmentTransaction transaction;
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout,fragmentList.get(i));
        transaction.commit();
    }

    public void switchFragment(int i) {

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();/*.setCustomAnimations(
                android.R.anim.fade_in, R.anim.slide_in_from_bottom);*/
        if (!fragmentList.get(i).isAdded()) {    // 先判断是否被add过
            transaction.add(R.id.framelayout, fragmentList.get(i)).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.show(fragmentList.get(i)).commit(); // 隐藏当前的fragment，显示下一个
        }
        for (int j = 0; j < 5; j ++){
            if (j != i){
                if (fragmentList.get(j).isVisible()){
                    transaction.hide(fragmentList.get(j));
                }
            }
        }
    }

    @OnClick({R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5})
    public void onClickTextView(View view){
        switch (view.getId()){
            case R.id.textView1 :
                textView[0].setSelected(true);
                changeState(0);
                loadFragment(0);
                //switchFragment(0);
                break;
            case R.id.textView2 :
                textView[1].setSelected(true);
                changeState(1);
                loadFragment(1);
                //switchFragment(1);
                break;
            case R.id.textView3 :
                textView[2].setSelected(true);
                changeState(2);
                loadFragment(2);
                //switchFragment(2);
                break;
            case R.id.textView4 :
                textView[3].setSelected(true);
                changeState(3);
                loadFragment(3);
                //switchFragment(3);
                break;
            case R.id.textView5 :
                textView[4].setSelected(true);
                changeState(4);
                loadFragment(4);
                //switchFragment(4);
                break;
        }

    }

    /**
     * 更改 导航栏的点击状态
     */
    private void changeState(int j){
        for (int i = 0; i < 5; i++){
            if (i != j){
                textView[i].setSelected(false);
            }
        }
    }



}
