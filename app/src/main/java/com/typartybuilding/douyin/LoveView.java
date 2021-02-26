package com.typartybuilding.douyin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.typartybuilding.R;

import java.util.Random;

public class LoveView extends RelativeLayout {

    private OnCallBack onCallBack;
    private Context context;
    private float[] num = new float[]{-35f, -25f, 0f, 25f, 35f};
    private long[] mHits = new long[2];


    public LoveView(Context context) {
        this(context,null);
    }

    public LoveView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    //监听点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        System.arraycopy(mHits,1, mHits,0,mHits.length - 1);
        mHits[mHits.length-1] = SystemClock.uptimeMillis();

        if (mHits[0] >= (SystemClock.uptimeMillis() - 200)){
            final ImageView imageView = new ImageView(context);

            //在事件产生的坐标处添加心形组件
            LayoutParams layoutParams = new LayoutParams(300,300);
            layoutParams.leftMargin = (int)(event.getX()-150);
            layoutParams.topMargin = (int)(event.getY()-300);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.weishi_btn_dianzan));
            imageView.setLayoutParams(layoutParams);
            imageView.bringToFront();
            addView(imageView);

            //为组件添加动画
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(//缩放动画，X轴2倍缩小至0.9倍
                    scaleAni(imageView, "scaleX", 2f, 0.9f, 100L, 0L))
                    //缩放动画，Y轴2倍缩放至0.9倍
                    .with(scaleAni(imageView, "scaleY", 2f, 0.9f, 100l, 0l))
                    //旋转动画，随机旋转角
                    .with(rotation(imageView, 0l, 0l, num[new Random().nextInt(4)]))
                    //渐变透明动画，透明度从0-1
                    .with(alphaAni(imageView, 0F, 1F, 100l, 0L))
                    //缩放动画，X轴0.9倍缩小至
                    .with(scaleAni(imageView, "scaleX", 0.9f, 1F, 50L, 150L))
                    //缩放动画，Y轴0.9倍缩放至
                    .with(scaleAni(imageView, "scaleY", 0.9f, 1F, 50L, 150L))
                    //位移动画，Y轴从0上移至600
                    .with(translationY(imageView, 0F, -600F, 800L, 400L))
                    //透明动画，从1-0
                    .with(alphaAni(imageView, 1F, 0F, 300L, 400L))
                    //缩放动画，X轴1至3倍
                    .with(scaleAni(imageView, "scaleX", 1F, 3f, 700L, 400L))
                    //缩放动画，Y轴1至3倍
                    .with(scaleAni(imageView, "scaleY", 1F, 3f, 700L, 400L));
            animatorSet.start();
            animatorSet.addListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    removeViewInLayout(imageView);
                }
            });

            if (onCallBack!=null){
                onCallBack.callback();
            }
        }
        return super.onTouchEvent(event);
    }

    private ObjectAnimator scaleAni(View view,String propertyName,Float from,Float to ,Long time,Long delay){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, propertyName, from, to);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setStartDelay(delay);
        objectAnimator.setDuration(time);
        return objectAnimator;
    }

    private ObjectAnimator translationX(View view,Float from,Float to,Long time, Long delayTime){
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "translationX", from, to);
        ani.setInterpolator(new LinearInterpolator());
        ani.setStartDelay(delayTime);
        ani.setDuration(time);
        return ani;
    }

    private ObjectAnimator translationY(View view, Float from, Float to, Long time,Long delayTime){
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "translationY", from, to);
        ani.setInterpolator(new LinearInterpolator());
        ani.setStartDelay(delayTime);
        ani.setDuration(time);
        return ani;
    }

    private ObjectAnimator alphaAni(View view,Float from,Float to,Long time,Long delayTime){
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "alpha", from, to);
        ani.setInterpolator(new LinearInterpolator());
        ani.setStartDelay(delayTime);
        ani.setDuration(time);
        return ani;
    }

    private ObjectAnimator rotation(View view, Long time, Long delayTime, Float values){
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "rotation",values);
        ani.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return 0;
            }
        });
        ani.setStartDelay(delayTime);
        ani.setDuration(time);
        return ani;
    }

    public void setCallBack(OnCallBack onCallBack){
        this.onCallBack = onCallBack;
    }
    public interface OnCallBack{
        void callback();
    }

}
