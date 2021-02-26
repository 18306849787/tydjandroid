package com.typartybuilding.utils;


import android.view.View;
import android.view.ViewTreeObserver;

/**
 *  测量控件的宽高
 */
public class ViewUtil {

    public static void getViewWidth(final View view, final OnViewListener onViewListener) {
        ViewTreeObserver vto2 = view.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if(onViewListener!=null){
                    onViewListener.onView(view.getWidth(),view.getHeight());
                }
            }
        });
    }

    public interface OnViewListener {
        void onView(int width,int height);
    }

}
