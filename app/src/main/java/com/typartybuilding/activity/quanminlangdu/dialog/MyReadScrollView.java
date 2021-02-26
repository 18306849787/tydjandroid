package com.typartybuilding.activity.quanminlangdu.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyReadScrollView extends ScrollView {

    private OnScrollListener listener;

    public MyReadScrollView(Context context) {
        super(context);
    }

    public MyReadScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyReadScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyReadScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public interface OnScrollListener{
        void onScroll(int scrollY);
    }

    public void setOnScrollListener(OnScrollListener listener){
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null){
            listener.onScroll(t);
        }
    }
}
