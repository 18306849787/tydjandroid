package com.typartybuilding.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager {

    private boolean isCanScroll = false;

    public ChildViewPager(@NonNull Context context) {
        super(context);
    }

    public ChildViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

   /* public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }



    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        //第二个参数false 表示去掉页面切换的滑动效果
        super.setCurrentItem(item,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //返回false 禁止滑动切换页面
        if (isCanScroll) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //返回false 禁止滑动切换页面
        if (isCanScroll) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }*/


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int curPosition;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(false);

               /* curPosition = this.getCurrentItem();
                int count = this.getAdapter().getCount();
                // 当当前页面在最后一页和第0页的时候，由父亲拦截触摸事件
                if (curPosition == count - 1|| curPosition==0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {//其他情况，由孩子拦截触摸事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                }*/

        }
        return super.dispatchTouchEvent(ev);
    }

}
