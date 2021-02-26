package com.typartybuilding.view;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.utils.DisplayUtils;

import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2019-12-03
 * @describe
 */
public class AutoScrollViewPager extends FrameLayout implements WeakHandler.IHandler {
    private final int SWITCH_VIEW_PAGER = 1;
    private ViewPager viewPager;
    private WidgetViewPagerAdapter mAdapter;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private boolean isShowDot = false;
    private int pageMargin = 0;
    private int currentIndex = 0; //当前位置
    private static long time = 5000; //自动播放时间
    private boolean autoPlay = false; //是否自动播放
    private TextView mTitleView;
    private boolean mIsShowTitle;
    private WeakHandler handler = new WeakHandler(this, Looper.getMainLooper());


    public AutoScrollViewPager(@NonNull Context context) {
        this(context, null);
    }

    public AutoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 动态添加viewpager和小圆点
     */
    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.layout_scroll_view_pager, this);
        viewPager = findViewById(R.id.widget_viewpager);
        mLinearLayout = findViewById(R.id.widget_indicator_dot);
        mTitleView = findViewById(R.id.textview_fg_pic);
    }

    private float x;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            x = ev.getX();
        }
        if (!autoPlay&&ev.getAction() == MotionEvent.ACTION_MOVE){
            if (ev.getX() - x>10||x-ev.getX()>10){
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
    }

    /**
     * 初始化viewpager
     */
    private void initViewPager() {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }
        if (mIsShowTitle){
            mTitleView.setVisibility(VISIBLE);
        }else {
            mTitleView.setVisibility(INVISIBLE);
        }
        if (isShowDot) {
            mLinearLayout.setVisibility(VISIBLE);
            setIndicatorDot();
        } else {
            mLinearLayout.setVisibility(GONE);
        }
        //设置红缓存的页面数
        viewPager.setOffscreenPageLimit(1);
        // 设置2张图之前的间距。
        viewPager.setPageMargin(pageMargin);
//        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (isShowDot && mLinearLayout.getChildCount() > 0) {
                    for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
                        ImageView imageViewUnSelect = (ImageView) mLinearLayout.getChildAt(i);
//                        imageViewUnSelect.getLayoutParams().width = DisplayUtils.dip2px(4);
                        imageViewUnSelect.setImageResource(R.drawable.shape_dot_cho_vp_0);
                    }
                    ArticleBanner articleBanner = mList.get(position % mAdapter.getRealCount());
                    if (mTitleView.getVisibility() == View.VISIBLE){
                        mTitleView.setText(articleBanner.articleTitle);
                    }
                    ImageView imageViewSelect = (ImageView) mLinearLayout.getChildAt(position % mAdapter.getRealCount());
//                    imageViewSelect.getLayoutParams().width = DisplayUtils.dip2px(11);
//                    imageViewSelect.setEnabled(true);
                    imageViewSelect.setImageResource(R.drawable.shape_dot_cho_vp_1);
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        currentIndex = mAdapter.getCount() / 2;
        viewPager.setCurrentItem(currentIndex);

    }



    public void onNext(long time) {
        handler.sendEmptyMessageDelayed(SWITCH_VIEW_PAGER,time);
    }

    public void isShowDot(boolean isShow) {
        isShowDot = isShow;
    }

    /**
     * 取消播放
     */
    public void cancel() {
        handler.removeMessages(SWITCH_VIEW_PAGER);
    }

    /**
     * 设置小圆点
     */
    private void setIndicatorDot() {
        mLinearLayout.removeAllViews();
        for (int i = 0; i < mAdapter.getRealCount(); i++) {
            //添加底部灰点
            ImageView v = new ImageView(mContext);
            v.setImageResource(R.drawable.selector_radio_button_cho2);
            v.setEnabled(false);
            LinearLayout.LayoutParams params;
            if (mDotSize > 0){
                params = new LinearLayout.LayoutParams(mDotSize, mDotSize);
            }else {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DisplayUtils.dip2px(4));
//                params = new LinearLayout.LayoutParams(DisplayUtils.dip2px(10), DisplayUtils.dip2px(10));
            }
//            if (i != 0){
//                params.leftMargin = DisplayUtils.dip2px(0);
//            }
            v.setPadding(10,0,10,0);
            v.setLayoutParams(params);
            mLinearLayout.addView(v);
        }
        if (mAdapter.getRealCount() ==1){
            mLinearLayout.setVisibility(INVISIBLE);
        }
    }

    private List<ArticleBanner> mList;
    /**
     * 设置适配器
     *
     * @param adpter
     */
    public void setAdapter(WidgetViewPagerAdapter adpter, List<ArticleBanner> list,boolean isShowTitle) {
        mAdapter = adpter;
        mList = list;
        mIsShowTitle = isShowTitle;
        viewPager.setAdapter(mAdapter);
        initViewPager();
    }

    /**
     * 设置是否自动播放
     *
     * @param autoPlay
     */
    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
        if (!autoPlay) {
            handler.removeMessages(SWITCH_VIEW_PAGER);
        }
    }

    private int mDotSize ;
    public void setDotSize(float dotSize){
        mDotSize = (int) dotSize;
    }

    @Override
    public void handleMessage(Message msg) {
        handler.removeMessages(SWITCH_VIEW_PAGER);
//        if (isOnscreen()) {
            if (currentIndex + 1 < mAdapter.getCount()) {
                viewPager.setCurrentItem(++currentIndex);
            } else {
                currentIndex = 0;
                viewPager.setCurrentItem(currentIndex);
            }
//        }
        if (autoPlay) {
            handler.sendEmptyMessageDelayed(SWITCH_VIEW_PAGER,time);
        }
    }

//    private boolean isOnscreen() {
//        if (getContext() instanceof BaseFragmentActivity) {
//            return ((BaseFragmentActivity) getContext()).isActivityVisible();
//        }
//        return true;
//    }

}