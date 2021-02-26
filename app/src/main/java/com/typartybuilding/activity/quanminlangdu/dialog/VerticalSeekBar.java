package com.typartybuilding.activity.quanminlangdu.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.SeekBar;

@SuppressLint("AppCompatCustomView")
public class VerticalSeekBar extends SeekBar {
    private ProgressChangedListener progressChangedListener;
    public VerticalSeekBar(Context context) {
        super(context);
    }
    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }
    protected void onDraw(Canvas c) {
        //将SeekBar转转90度
        c.rotate(-90);
        //将旋转后的视图移动回来
        c.translate(-getHeight(), 0);
        super.onDraw(c);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int i = 0;
                //获取滑动的距离
                i = getMax() - (int) (getMax() * event.getY() / getHeight());
                //设置进度
                setProgress(i);
                if (progressChangedListener != null) {
                    progressChangedListener.onProgressChanged(getProgress());
                }
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
    public void setOnProgressChanged(ProgressChangedListener progressChangedListener) {
        this.progressChangedListener = progressChangedListener;
    }
    public interface ProgressChangedListener {
        void onProgressChanged(int progress);
    }
}
