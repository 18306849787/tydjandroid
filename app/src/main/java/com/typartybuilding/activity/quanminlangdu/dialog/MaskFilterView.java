package com.typartybuilding.activity.quanminlangdu.dialog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class MaskFilterView extends View {
    public MaskFilterView(Context context) {
        super(context);
    }

    public MaskFilterView(Context context,
                          @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaskFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColorFilter(new LightingColorFilter(0xffffff00,0x00000000));
        paint.setAlpha(0x22);
        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
    }
}
