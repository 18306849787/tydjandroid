package com.typartybuilding.douyin;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DoubleClickListener implements View.OnTouchListener {

    private static int timeout=400;//双击间四百毫秒延时
    private int clickCount = 0;//记录连续点击次数
    private long downTime;
    private long upTime;
    private Handler handler;
    private OnClickCallBack onClickCallBack;

    public interface OnClickCallBack{
        void oneClick();//点击一次的回调
        void doubleClick();//连续点击两次的回调
    }

    public DoubleClickListener(OnClickCallBack onClickCallBack) {
        this.onClickCallBack = onClickCallBack;
        this.handler = new Handler();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP){
            upTime = SystemClock.currentThreadTimeMillis();
            //Log.i("MicroVideoPlayAdapter", "onTouch: up");
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            downTime = SystemClock.currentThreadTimeMillis();
            //Log.i("MicroVideoPlayAdapter", "onTouch: down");
        }
        long diffTime = upTime - downTime;
        //Log.i("MicroVideoPlayAdapter", "onTouch: diffTime : " + diffTime);

        if (diffTime < 50 && diffTime > 0) {
            clickCount++;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Log.i("MicroVideoPlayAdapter", "run: clickCount : " + clickCount);
                    if (clickCount == 1) {
                        onClickCallBack.oneClick();
                    }else if(clickCount >= 2){        //在400毫秒内，连续点击数超过2次以上，就调点赞的接口
                        onClickCallBack.doubleClick();
                    }
                    handler.removeCallbacksAndMessages(null);
                    //清空handler延时，并防内存泄漏
                    clickCount = 0;//计数清零
                }
            },timeout);//延时timeout后执行run方法中的代码
        }

        return false;  //让点击事件继续传播，方便再给View添加其他事件监听
    }




}
