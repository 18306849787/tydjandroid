package com.typartybuilding.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemProperties;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.MixtureData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 23143 on 2018/9/10.
 */

public class Utils {

    public static String TAG = "Utils";


    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";
    public static final int VIVO_NOTCH = 0x00000020;//是否有刘海
    public static final int VIVO_FILLET = 0x00000008;//是否有圆角


    //资源文件转file
    public static String resToFile(int resId){
        String filePath = MyApplication.getContext().getCacheDir().getAbsolutePath() + "shareimg.jgp";
        File file = new File(filePath);
        if (file.exists()){
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap bitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(),resId);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    //通知相册更新
    public static void refreshToAlbum(File file){
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            MyApplication.getContext().sendBroadcast(intent);
        }
    }


  /*  *//**
     * 设置 上拉刷新
     * @param scrollView
     *//*
    public static void setScrollView(PullToRefreshScrollView scrollView){
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        ILoadingLayout iLoadingLayout = scrollView.getLoadingLayoutProxy(false,true);
        iLoadingLayout.setPullLabel("上拉刷新");

    }*/

    /**
     *  没有更多的提示
     */
    public static void noMore(){
        Toast.makeText(MyApplication.getContext(),"没有更多了",Toast.LENGTH_SHORT).show();
    }

    public static MicroVideo mixtureDataToMicroVideo(MixtureData mixtureData){
        MicroVideo microVideo = new MicroVideo(mixtureData.isPraise,mixtureData.isCollect,mixtureData.praisedNum,
                mixtureData.targetId,mixtureData.userId,mixtureData.userName,
                mixtureData.userHeadImg,mixtureData.targetTitle,
                mixtureData.urlType,mixtureData.fileUrl,mixtureData.videoCover);
        return microVideo;
    }

    public static int getStatusBarHeight() {
        Resources resources = MyApplication.getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 软件盘 操作, 如果输入法在窗口上已经显示，则隐藏，反之则显示
     */
    public static void setSoftInput(){
        Context context = MyApplication.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软件盘
     */
    public static void hideSoftInput(EditText view){
        Context context = MyApplication.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     *
     * 显示软键盘
     */
    public static void showSoftInput(EditText view){
        Context context = MyApplication.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);

    }

    /**
     * md5加密
     * @param passward
     * @return
     */
    public static String md5Encrypt(String passward){
        StringBuffer sb = new StringBuffer();
        //得到一个信息摘要器
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(passward.getBytes());
            //把每一个byte做一个与运算0xff
            for (byte b : result){
                //与运算
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1){
                    sb.append("0");
                }
                sb.append(str);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 开启线程，获取视频第一帧，并显示
     * @param imageView
     * @param videoUrl
     */
    public static void getVideoPicture(ImageView imageView, String videoUrl){
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                Bitmap bitmap = getVideoThumb(videoUrl);
                emitter.onNext(bitmap);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Log.i(TAG, "onNext: duration : " + bitmap);
                        //加载图片
                        Glide.with(MyApplication.getContext()).load(bitmap)
                                .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                                .into(imageView);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e ：" + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


    /**
     * 获取视频的第一帧图片
     */
    public static Bitmap getVideoThumb(String path) {
        Bitmap bitmap = null;
        if (path != null) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                if (path.contains("http")) {
                    //根据url获取缩略图
                    retriever.setDataSource(path, new HashMap());
                    //获得第一帧图片
                    bitmap = retriever.getFrameAtTime();
                }else {
                    retriever.setDataSource(path);
                    bitmap = retriever.getFrameAtTime();
                    //bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                retriever.release();
            }

        }
        return  bitmap;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {

        if (bitmap != null){
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m,

                    true);

            return bitmap;
        }
        return bitmap;
    }


    /**
     * 开启线程，获取视频时长，并用textview 显示
     * @param textView
     * @param videoUrl
     */
    public static void calculateDuration(TextView textView, String videoUrl){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String duration = getDuration(videoUrl);
                emitter.onNext(duration);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "onNext: duration : " + s);
                        textView.setText(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e ：" + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * 获取视频 时长
     * @param
     * @return
     */
    public static String getDuration(String videoUrl){
        MediaPlayer mediaPlayer = new MediaPlayer();
        int duration = 0;
        if (videoUrl != null) {
            if (videoUrl.contains("http")) {
                try {
                    mediaPlayer.setDataSource(videoUrl);
                    mediaPlayer.prepare();
                    duration = mediaPlayer.getDuration();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        }

        Log.i(TAG, "getDuration: duration : " + duration);
        return (formatTime(duration/1000) +"");
    }

    /**
     * 播放次数转换
     */
    public static String formatPlayTimes(int count){
        int playTimes = count/10000;
        if (playTimes != 0){
            float playTimes1 = ((float) count)/10000;
            float playTimes2 = ((float) Math.round(playTimes1*10))/10;

            return  playTimes2 + "万";
        }else {
            return count + "";
        }
    }

    /**
     * 播放次数转换
     */
    public static String formatPlayTimes2(int count){
        int playTimes = count/10000;
        if (playTimes != 0){
            float playTimes1 = ((float) count)/10000;
            float playTimes2 = ((float) Math.round(playTimes1*10))/10;

            return  playTimes2 + "w";
        }else {
            return count + "";
        }
    }

    /**
     * unix时间戳转为 具体格式日期
     */
    public static String formatDate(long msDate){

        String date = null;
        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(msDate));
        return date;
    }

    public static String formatDate(long msDate,String pattern){

        String date = null;
        date = new SimpleDateFormat(pattern).format(new Date(msDate));
        return date;
    }

    public static String formatMonth(long msDate){

        String date = null;
        date = new SimpleDateFormat("MM").format(new Date(msDate));
        return date;
    }

    public static String formatDay(long msDate){

        String date = null;
        date = new SimpleDateFormat("dd").format(new Date(msDate));
        return date;
    }



    /**
     * 时间格式转换
     */
    public static String formatTime(int duration){
        int ss = duration%60;   //秒
        int mm = duration/60;   //分钟
        int hh = duration/3600; //小时
        String str = null;
        if(hh!=0){
            int hmm = mm - hh*60;

            str = String.format("%02d:%02d:%02d",hh,hmm,ss);

        }else {
            str = String.format("%02d:%02d",mm,ss);
        }
        return str;
    }


    private static final int ONE_HOUR = 1 * 60 * 60 * 1000;
    private static final int ONE_MIN = 1 * 60 * 1000;
    private static final int ONE_SECOND = 1 * 1000;
    /**HH:mm:ss*/
    public static String formatTimeMs(long ms) {
        StringBuilder sb = new StringBuilder();
        int hour = (int) (ms / ONE_HOUR);
        int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
        int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
        if (hour == 0) {
//			sb.append("00:");
        } else if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }

    /**
     * 获取屏幕高度
     * @return
     */
    public static int getScreenHeight(){
        DisplayMetrics dm = MyApplication.getContext().getResources().getDisplayMetrics();
        int heigth = dm.heightPixels;

        return heigth;
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    public static int getScreenWidth(){
        DisplayMetrics dm = MyApplication.getContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        return width;
    }

    /**
     * dp转px
     * @param dp
     * @return
     */
    public static int dip2px(Context context, int dp)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp*density+0.5);
    }

    /** px转换dip */
    public static int px2dip(Context context,int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 改变屏幕的透明度
     * @param bgAlpha
     */
    public static void backgroundAlpha(Activity activity,float bgAlpha)
    {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }


    public static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "hasNotchAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "hasNotchAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "hasNotchAtHuawei Exception");
        } finally {
            return ret;
        }
    }
    //int[0]值为刘海宽度 int[v1]值为刘海高度
    public static int[] getNotchSizeAtHuawei(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "getNotchSizeAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "getNotchSizeAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "getNotchSizeAtHuawei Exception");
        } finally {
            return ret;
        }
    }

    public static boolean hasNotchAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "hasNotchAtVivo ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "hasNotchAtVivo NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "hasNotchAtVivo Exception");
        } finally {
            return ret;
        }
    }

    /**
     * 将十六进制 颜色代码 转换为 int
     *
     * @return
     */
    public static int HextoColor(String color) {

        // #ff00CCFF
        String reg = "#[a-f0-9A-F]{8}";
        if (!Pattern.matches(reg, color)) {
            color = "#00ffffff";
        }

        return Color.parseColor(color);
    }

    public static boolean hasNotchAtOPPO(Context context) {
        try{
            return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        }catch (Exception e){
            Log.e("Notch", "hasNotchAtOPPO Exception");
        }
        return false;
    }


    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean hasNotchAtMI(Context context){
        try{

            if (SystemProperties.getInt("ro.miui.notch", 0) == 1){
                return true;
            }

        }catch (Exception e){
            Log.e("Notch", "hasNotchAtMI Exception");
        }
        return false;
    }


    public static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }



    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


    public static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            //如果为全透明模式，取消设置Window半透明的Flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置状态栏为透明
            window.setStatusBarColor(Color.TRANSPARENT);
            //设置window的状态栏不可见
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            //如果为半透明模式，添加设置Window半透明的Flag
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        //view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    /**
     * 设置tabLayout的下划线 的宽度，根据内容的宽度来变化
     * @param tabLayout
     */
    public static void setTabLayoutIndicator(final TabLayout tabLayout) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Field field = tabLayout.getClass().getDeclaredField("mTabStrip");
                    field.setAccessible(true);
                    //拿到tabLayout的mTabStrip属性
                    //LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
                    LinearLayout tabStrip = (LinearLayout) field.get(tabLayout);
                    for (int i = 0, count = tabStrip.getChildCount(); i < count; i++) {
                        View tabView = tabStrip.getChildAt(i);
                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        //Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        Field mTextViewField;
                        try {
                            mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        } catch (Exception e) {
                            mTextViewField = tabView.getClass().getDeclaredField("textView"); //安卓28变量名变了
                        }
                        mTextViewField.setAccessible(true);

                        TextView textView = (TextView) mTextViewField.get(tabView);
                        Log.i(TAG, "run: textView: " + textView.toString());

                        tabView.setPadding(0, 0, 0, 0);
                        //字多宽线就多宽，所以测量mTextView的宽度
                        int textWidth = 0;
                        textWidth = textView.getWidth();
                        if (textWidth == 0) {
                            textView.measure(0, 0);
                            textWidth = textView.getMeasuredWidth();
                        }
                        Log.i(TAG, "run: textWidth : " + textWidth);
                        int tabWidth = 0;
                        tabWidth = tabView.getWidth();
                        if (tabWidth == 0) {
                            tabView.measure(0, 0);
                            tabWidth = tabView.getMeasuredWidth();
                        }
                        Log.i(TAG, "run: tabWidth : " + tabWidth);
                        LinearLayout.LayoutParams tabViewParams = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        int margin = (tabWidth - textWidth) / 2;
                        //LogUtils.d("textWidth=" + textWidth + ", tabWidth=" + tabWidth + ", margin=" + margin);
                        tabViewParams.leftMargin = margin;
                        tabViewParams.rightMargin = margin;
                        tabView.setLayoutParams(tabViewParams);
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void reflex(final TabLayout tabLayout){
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    int dp10 = dip2px(tabLayout.getContext(), 10);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        //Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        Field mTextViewField;
                        try {
                            mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        } catch (Exception e) {
                            mTextViewField = tabView.getClass().getDeclaredField("textView"); //安卓28变量名变了
                        }
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width ;
                        params.leftMargin = dp10;
                        params.rightMargin = dp10;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void setTabWidth(final TabLayout tabLayout, final int padding) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距 注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = padding;
                        params.rightMargin = padding;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void setTabWidthSame(final TabLayout tabs) {
        ViewTreeObserver viewTreeObserver = tabs.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                tabs.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //拿到tabLayout的mTabStrip属性
                LinearLayout mTabStrip = (LinearLayout) tabs.getChildAt(0);
                int totalWidth = mTabStrip.getMeasuredWidth();
                int tabWidth = totalWidth/mTabStrip.getChildCount();
                Log.i(TAG, "onGlobalLayout: totalWidth : " + totalWidth);
                Log.i(TAG, "onGlobalLayout: tabWidth : " + tabWidth);
                for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                    View child = mTabStrip.getChildAt(i);
                    child.setPadding(0, 0, 0, 0);

                    Field mTextViewField = null;
                    int width = 0;
                    try {
                        try {
                            mTextViewField = child.getClass().getDeclaredField("mTextView");
                        } catch (Exception e) {
                            mTextViewField = child.getClass().getDeclaredField("textView"); //安卓28变量名变了
                        }
                        mTextViewField.setAccessible(true);
                        TextView mTextView = (TextView) mTextViewField.get(child);
                        //字多宽线就多宽，所以测量mTextView的宽度
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }
                        Log.i(TAG, "onGlobalLayout: textWidth : " + width);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)child.getLayoutParams();
                    params.width = width;
                    params.leftMargin = (tabWidth - width)/2;
                    params.rightMargin = (tabWidth - width)/2;
                    child.setLayoutParams(params);
                    child.invalidate();
                }
            }
        });
    }


}
