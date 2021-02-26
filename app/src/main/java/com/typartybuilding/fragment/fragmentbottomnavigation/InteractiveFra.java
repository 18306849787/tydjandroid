package com.typartybuilding.fragment.fragmentbottomnavigation;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.SlidingTabLayout;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeAct;
import com.typartybuilding.activity.second.interactive.BookListFra;
import com.typartybuilding.activity.second.interactive.LiveRoomFra;
import com.typartybuilding.activity.second.interactive.MeShowFra;
import com.typartybuilding.activity.second.interactive.SmartAnswerFra;
import com.typartybuilding.activity.second.interactive.kkk;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.utils.UserUtils;
import com.typartybuilding.view.NoSlideViewPager;
import com.typartybuilding.view.SlidingTabLayoutNew;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-05
 * @describe
 */
public class InteractiveFra extends BaseFra {
    @BindView(R.id.interative_tab)
    SlidingTabLayoutNew mSlidingTabLayout;
    @BindView(R.id.interative_viewpager)
    NoSlideViewPager mViewPager;
    private ArrayList<Fragment> fsRes = new ArrayList<>();
    private String[] titles = {"直播间", "视频会议", "我行我show", "朗读厅", "知识竞答", "知识闯关"};

    @Override
    public void initData() {
        Typeface typeface = ResourcesCompat.getFont(getActivity(),R.font.sourcehansanscn_heavy);
        fsRes.add((Fragment) ARouter.getInstance()
                .build(LiveRoomFra.PATH).withInt("type", 1)
                .withString("title", "直播间").navigation());
        fsRes.add((Fragment) ARouter.getInstance()
                .build(LiveRoomFra.PATH).withInt("type", 2)
                .withString("title", "视频会议").navigation());
        fsRes.add(new MeShowFra());
        fsRes.add(new BookListFra());
        fsRes.add((Fragment) ARouter.getInstance()
                .build(SmartAnswerFra.PATH).withInt("type", 1)
                .navigation());
        fsRes.add((Fragment) ARouter.getInstance()
                .build(SmartAnswerFra.PATH).withInt("type", 2)
                .navigation());
        mSlidingTabLayout.setViewPager(mViewPager, titles, getActivity(), fsRes);
        for (int i = 0; i < titles.length; i++) {
            TextView textView = mSlidingTabLayout.getTitleView(i);
            textView.setIncludeFontPadding(false);
            textView.setTypeface(typeface);
//            onClick(textView,i);
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserUtils.getIns().isTourist()){
            mViewPager.setScanScroll(false);
        }else {
            mViewPager.setScanScroll(true);
        }
    }



    private void onClick(TextView textView, final int position){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem()!= position){
                    if (UserUtils.getIns().isTourist()&&(position==4||position==5||position==6||position==3)){
                        MyApplication.remindVisitor(getActivity());
                    }else {
                        mViewPager.setCurrentItem(position);
                    }
                }

            }
        });
    }
    @Override
    public int getLayoutId() {
        return R.layout.layout_fra_interactive;
    }
}
