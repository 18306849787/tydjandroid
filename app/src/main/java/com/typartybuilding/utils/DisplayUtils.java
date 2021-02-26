package com.typartybuilding.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.luck.picture.lib.tools.ScreenUtils;
import com.typartybuilding.base.MyApplication;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DisplayUtils {
    /**
     * 黄金比例
     */
    public static final float GOLDEN_RATIO = 0.618F;
    /**
     * 屏幕宽度
     */
    public static int screenWidth;
    /**
     * 屏幕高度
     */
    public static int screenHeight;

    /**
     * 屏幕真正高度，去掉底部触摸屏
     */
    public static int realScreenHeight;

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getWidth() {
        if (screenWidth == 0) {
            screenWidth = MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
        }
        return screenWidth;
    }

    /**
     * 获取屏幕高度
     * 屏幕适配的时候使用此值可能会有问题
     * 1、这个高度目前在有些华为等手机上可以随时隐藏底部虚拟按键的设备上会有问题，使用起来有一定风险
     * 2、对于分屏来说，其实activity的高度没有这么高，还使用这个高，可能会有显示一半的问题
     *
     * @return
     */
    @Deprecated
    public static int getHeight() {
        if (screenHeight == 0) {
            screenHeight = MyApplication.getContext()
                    .getResources().getDisplayMetrics().heightPixels;
        }
        return screenHeight;
    }

    /*获取当前屏幕的高度 无论横竖屏*/
    public static int getCurScrHeight() {
        return MyApplication.getContext()
                .getResources().getDisplayMetrics().heightPixels;
    }

    /*获取当前屏幕的宽度 无论横竖屏*/
    public static int getCurScrWidth() {
        return MyApplication.getContext()
                .getResources().getDisplayMetrics().widthPixels;
    }

    /*获取当前屏幕的宽高中较小的那个*/
    public static int getScreenMinLength() {
        int width = MyApplication.getContext()
                .getResources().getDisplayMetrics().widthPixels;
        int height = MyApplication.getContext()
                .getResources().getDisplayMetrics().heightPixels;
        return Math.min(width, height);
    }

    /*获取当前屏幕的宽高中较大的那个*/
    public static int getScreenMaxLength() {
        int width = MyApplication.getContext()
                .getResources().getDisplayMetrics().widthPixels;
        int height = MyApplication.getContext()
                .getResources().getDisplayMetrics().heightPixels;
        return Math.max(width, height);
    }

    /**
     * 获取小的宽度
     *
     * @return
     */
    public static int getMinWidth() {
        int width = getWidth();
        int height = getHeight();
        if (width > height) {
            return height;
        }
        return width;
    }

    public static int getMaxHeight() {
        int width = getWidth();
        int height = getHeight();
        if (width > height) {
            return width;
        }
        return height;
    }

    /**
     * 获得标题栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> mClass = null;
        Object obj = null;
        Field field = null;
        int resID = 0, height = 0;
        try {
            mClass = Class.forName("com.android.internal.R$dimen");
            obj = mClass.newInstance();
            field = mClass.getField("status_bar_height");
            resID = Integer.parseInt(field.get(obj).toString());
            height = context.getResources().getDimensionPixelSize(resID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }

    /**
     * 获取屏幕真正高度，去掉底部触摸屏
     *
     * @return
     */
    public static int getRealHeight() {

        if (realScreenHeight == 0) {

            WindowManager w = (WindowManager) MyApplication.getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            Display d = w.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            d.getMetrics(metrics);
            // since SDK_INT = 1;
            realScreenHeight = metrics.heightPixels;
            // includes window decorations (statusbar bar/navigation bar)
            if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
                try {
                    realScreenHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
                } catch (Exception ignored) {
                }
                // includes window decorations (statusbar bar/navigation bar)
            } else if (Build.VERSION.SDK_INT >= 17) {
                try {
                    android.graphics.Point realSize = new android.graphics.Point();
                    Display.class.getMethod("getRealSize", android.graphics.Point.class).invoke(d, realSize);
                    realScreenHeight = realSize.y;
                } catch (Exception ignored) {
                }
            }
            if (realScreenHeight <= 0) {
                realScreenHeight = DisplayUtils.getHeight();
            }
        }
        return realScreenHeight;
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = MyApplication.getContext()
                .getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        return dip2px(MyApplication.getContext()
                , dpValue);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static float getDensity() {
        return MyApplication.getContext()
                .getResources().getDisplayMetrics().density;
    }

    public static boolean mmerseModel = false;

    /**
     * 设置通知栏沉浸模式
     *
     * @param window
     */
    public static void setStatusBarModel(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int resultMiui = DisplayUtils.setStatusBarTextColor(window, 1);
            int resultFl = DisplayUtils.setStatusBarDarkIcon(window, true);
            //透明状态栏
            if (resultMiui == 1 || resultFl == 1) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                mmerseModel = true;
            }
        }
    }

    /* 只支持MIUI V6
     * @param context
     * @param type 0--只需要状态栏透明 1-状态栏透明且黑色字体 2-清除黑色字体
     */
    public static int setStatusBarTextColor(Window window, int type) {
        if (!isMiUIV6()) {
            return 0;
        }
        Class clazz = window.getClass();
        try {
            int tranceFlag = 0;
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
            tranceFlag = field.getInt(layoutParams);
            field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (type == 0) {
                extraFlagField.invoke(window, tranceFlag, tranceFlag);//只需要状态栏透明
            } else if (type == 1) {
                extraFlagField.invoke(window, tranceFlag | darkModeFlag, tranceFlag | darkModeFlag);//状态栏透明且黑色字体
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
        return 1;
    }

    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";

    private static boolean isMiUIV6() {
//        try {
//            final BuildProperties prop = BuildProperties.newInstance();
//            String name = prop.getProperty(KEY_MIUI_VERSION_NAME, "");
//            return "V6".equals(name);
////	           return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
////	                   || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
////	                   || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
//        } catch (final IOException e) {
//            return false;
//        }
        return false;
    }

    /**
     * 是否是魅族手机
     *
     * @return
     */
    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    //得到魅族smartbar高度
    public static int getSmartBarHeight(Context context) {
        if (!isFlyme()) {
            return 0;
        }
        try {
            Class c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("mz_action_button_min_height");
            int height = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static int setStatusBarDarkIcon(Window window, boolean dark) {
        if (!isFlyme()) {
            return 0;
        }
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
            } catch (Exception e) {
                e.printStackTrace();
                return 2;
            }
        }
        return 1;
    }

    /**
     * 判断横竖屏
     *
     * @param context
     * @return
     */
    public static boolean isLandScreen(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private static float rotation;

    /**
     * 获取床空rotate
     *
     * @param context
     * @return
     */
    public static float getRotation(Context context) {
        if (rotation == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
            rotation = wm.getDefaultDisplay().getRotation();
        }
        return rotation;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将参数转换成黄金比例
     *
     * @param originValue 需要转换的值
     * @return 已经转换成黄金比例的值
     */
    public static int valueByGoldenRatio(float originValue) {
        return (int) (originValue * GOLDEN_RATIO + 0.5F);
    }

    /**
     * 根据横竖屏动态设置窗口的宽和高
     */
    public static void dynamicallySetWindowWidthHeight(final Window targetWindow, final int windowGravity) {
        if (targetWindow == null) {
            return;
        }
        final Context context = MyApplication.getContext()
                ;
        final WindowManager.LayoutParams lp = targetWindow.getAttributes();
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (isLandScreen(context)) {
            final int idealWidth = Math.max(metrics.widthPixels, metrics.heightPixels);
            lp.width = (int) (0.8F * idealWidth);//valueByGoldenRatio(idealWidth); // 黄金比例宽度不满足需求还得再宽点
            lp.gravity = windowGravity;
        } else {
            lp.width = metrics.widthPixels;
        }
        //lp.height = metrics.heightPixels - DisplayUtils.dip2px(30); // 高度
        // lp.height = metrics.heightPixels;
        targetWindow.setAttributes(lp);
    }

    /**
     * 设置Activity方向
     *
     * @param activity
     */
    public static void dynamicallySetOrientation(Activity activity) {
//        if (activity == null) {
//            return;
//        }
//        final Intent intent = activity.getIntent();
//        final int orientation = intent.getIntExtra(ScreenUtils.SCREEN_ORIENTATION_TYPE, ScreenUtils.SCREEN_ORIENTATION_TYPE_PORTRAIT);
//        switch (orientation) {
//            case ScreenUtils.SCREEN_ORIENTATION_TYPE_PORTRAIT:
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                break;
//            case ScreenUtils.SCREEN_ORIENTATION_TYPE_LANDSCAPE:
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                break;
//            case ScreenUtils.SCREEN_ORIENTATION_TYPE_REVERSE_LANDSCAPE:
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
//                break;
//            default:
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                break;
//        }
    }

    /**
     * 判断是否是横屏
     *
     * @return
     */
    public static boolean isLandScreen() {
        Configuration mConfiguration = MyApplication.getContext()
                .getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            return true;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            return false;
        }
        return false;
    }

    /*获取真正的颜色值*/
    public static int getColor(int resId) {
        return MyApplication.getContext()
                .getResources().getColor(resId);
    }

    /**
     * 全屏支持
     *
     * @param activity
     * @param full
     */
    public static void fullScreenChange(Activity activity, boolean full) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        if (full) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 全屏支持
     *
     * @param dialog
     * @param full
     */
    public static void fullScreenChange(Dialog dialog, boolean full) {
        WindowManager.LayoutParams attrs = dialog.getWindow().getAttributes();
        if (full) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            dialog.getWindow().setAttributes(attrs);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setAttributes(attrs);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static boolean isFullScreen(Activity activity) {
        int flag = activity.getWindow().getAttributes().flags;
        return (flag & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public static int getDimenPixelSize(Context context, int resId) {
        return context.getResources().getDimensionPixelSize(resId);
    }

    /**
     * 是否是全面屏手机
     *
     * @return
     */
    public static boolean isFullAspectScreen() {
        final int larger = Math.max(DisplayUtils.getHeight(), DisplayUtils.getWidth());
        final int smaller = Math.min(DisplayUtils.getHeight(), DisplayUtils.getWidth());
        return larger * 1.0F / smaller > 16.0F / 9;
    }

    public static float getDimension(int dimenId) {
        return MyApplication.getContext()
                .getResources().getDimension(dimenId);
    }
}
