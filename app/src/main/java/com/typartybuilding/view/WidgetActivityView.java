package com.typartybuilding.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.typartybuilding.R;
import com.typartybuilding.activity.second.PlayVideoAct;
import com.typartybuilding.bean.HomeFraBannerBean;
import com.typartybuilding.gsondata.ArticleBanner;

import java.util.List;
import java.util.logging.LogManager;


/**
 * @author chengchunhuiit@163.com
 * @date 2019-12-03
 * @describe
 */
public class WidgetActivityView extends FrameLayout {
    private final String WIDGETACTIVITY_TAG = "WidgetActivity_tag";
    public static final String HOME_TYPE = "home";
    public static final String PLAY_TYPE = "play";
    public static final String ROOM_TYPE = "room";
    public static final String GIFT_TYPE = "gift";
    private AutoScrollViewPager viewPager;
    private WidgetViewPagerAdapter adapter;
    private Context mContext;

    public WidgetActivityView(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public WidgetActivityView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public WidgetActivityView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_activity_widget, this);
        viewPager = findViewById(R.id.view_widget_viewpager);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WidgetView);
            viewPager.isShowDot(typedArray.getBoolean(R.styleable.WidgetView_show_dot, true));
            viewPager.setAutoPlay(typedArray.getBoolean(R.styleable.WidgetView_auto_play, true));
            viewPager.setDotSize(typedArray.getDimension(R.styleable.WidgetView_dot_size, 0f));
        }
    }




    private boolean isCircleImage;
    public void setCircleImage(boolean isCircle){
        isCircleImage = isCircle;
    }

    public void initAdapter(List<ArticleBanner> list, boolean isShowTitle,WidgetViewPagerAdapter.OnClickListen onClickListen) {
        if (viewPager != null) {
            adapter = new WidgetViewPagerAdapter(getContext(),list, onClickListen);
            adapter.setCircleImage(isCircleImage);
            viewPager.onNext(5000);
            viewPager.setAutoPlay(true);
            viewPager.setAdapter(adapter,list,isShowTitle);

        }
    }

}
