package com.typartybuilding.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.BaseFragmentHome;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentFindFascinating;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentHotBot;
import com.typartybuilding.fragment.fgPartyBuildingVideo.FragmentPopularityList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 党建微视 页面
 */
public class HomeFragmentPartyBuildingVideo extends BaseFragmentHome {

    @BindViews({R.id.textView1, R.id.textView2, R.id.textView3})
    TextView textView [] ;
    @BindView(R.id.framelayout_pb_video)
    FrameLayout frameLayout;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initFragmentList();
        //导航第一个默认被选中
        textView[0].setSelected(true);
        loadFragment(0);

    }

    @Override
    public int getLayoutId() {
        return R.layout.home_fragment_party_building_video;
    }

    @Override
    protected void lazyLoad() {
        //loadFragment(0);
    }

    @Override
    protected void stopLoad() {

    }

    private void initFragmentList(){
        FragmentHotBot fragment1 = new FragmentHotBot();
        FragmentPopularityList fragment2 = new FragmentPopularityList();
        FragmentFindFascinating fragment3 = new FragmentFindFascinating();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);
    }



    @OnClick({R.id.textView1, R.id.textView2, R.id.textView3})
    public void onClickTextView(View view){
        switch (view.getId()){
            case R.id.textView1 :
                textView[0].setSelected(true);
                changeState(0);
                //loadFragment(0);
                switchFragment(0);
                break;
            case R.id.textView2 :
                textView[1].setSelected(true);
                changeState(1);
                //loadFragment(1);
                switchFragment(1);
                break;
            case R.id.textView3 :
                textView[2].setSelected(true);
                changeState(2);
                //loadFragment(2);
                switchFragment(2);
                break;
            default:
                break;
        }
    }

    public void setCurrentFragment(int i){
        textView[i].setSelected(true);
        changeState(i);
        //loadFragment(1);
        switchFragment(i);
    }

    /**
     * 动态加载FragMentTitle
     */
    private void loadFragment(int i){
        FragmentTransaction transaction;
        transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_pb_video,fragmentList.get(i));
        transaction.commit();
    }

    public void switchFragment(int i) {

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();/*.setCustomAnimations(
                android.R.anim.fade_in, R.anim.slide_in_from_bottom);*/
        if (!fragmentList.get(i).isAdded()) {    // 先判断是否被add过
            transaction.add(R.id.framelayout_pb_video, fragmentList.get(i)).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.show(fragmentList.get(i)).commit(); // 隐藏当前的fragment，显示下一个
        }
        for (int j = 0; j < 3; j ++){
            if (j != i){
                if (fragmentList.get(j).isVisible()){
                    transaction.hide(fragmentList.get(j));
                }
            }
        }

    }

    /**
     * 更改 导航栏的点击状态
     */
    private void changeState(int j){
        for (int i = 0; i < 3; i++){
            if (i != j){
                textView[i].setSelected(false);
            }
        }
    }



}
