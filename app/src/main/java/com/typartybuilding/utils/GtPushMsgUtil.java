package com.typartybuilding.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.activity.NewsDetailMixActivity;
import com.typartybuilding.activity.PlayLiveVideoDetailActivity;
import com.typartybuilding.activity.PlayMicroVideoMixActivity;
import com.typartybuilding.activity.PlayVideoDetailMixActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.douyin.MicroVideoPlayActivity;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.MixtureData;

import java.util.ArrayList;
import java.util.List;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 *  处理 个推的透传消息
 */

public class GtPushMsgUtil {

    private static String TAG = "GtPushMsgUtil";
    private static int requestCode = 0;
    private static int id = 0;

    public static void handleTransmission(String jsonStr,Context context){

        MixtureData mixtureData = null;
        PendingIntent pi = null;
        if (!TextUtils.isEmpty(jsonStr)){
            Log.i(TAG, "handleTransmission: mixtureData1 : " + mixtureData);
            mixtureData = JSON.parseObject(jsonStr,MixtureData.class);
            Log.i(TAG, "handleTransmission: mixtureData2 : " + mixtureData);
        }
        if (mixtureData != null){
            pi = createPendingIntent(mixtureData,context);
            if (mixtureData.targetType == 0){   //为纯文本通知，文本不省略
                createNotificationText(mixtureData.title,mixtureData.text,pi,context);
            }else {
                createNotification(mixtureData.title, mixtureData.text, pi, context);
            }
        }

    }

    //用于创建 非纯文本的的通知，文本不显示完
    private static void createNotification(String title, String text,PendingIntent pi,Context context){
        NotificationManager manager = (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "channelId";
            CharSequence channelName = "channelName";
            final int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;

            Notification notification = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.push_small_new)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.push))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .build();

            Log.i(TAG, "createNotification: channel");
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId,
                    channelName,channelImportance);
            channel.setShowBadge(true);

            manager.createNotificationChannel(channel);
            //int id = (int)SystemClock.currentThreadTimeMillis();
            manager.notify(id++, notification);

        }else {
            Notification notification = new NotificationCompat.Builder(context, "default")
                    .setSmallIcon(R.drawable.push_small_new)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .build();
            //int id = (int)SystemClock.currentThreadTimeMillis();
            manager.notify(id++, notification);
        }

    }

    //用于创建纯文本的通知，需将文本显示完
    private static void createNotificationText(String title, String text,PendingIntent pi,Context context){

        NotificationManager manager = (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "channelId";
            CharSequence channelName = "channelName";
            final int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;

            Notification notification = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.push_small_new)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.push))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text))  //显示长文本，不省略
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .build();

            Log.i(TAG, "createNotification: channel");
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId,
                    channelName,channelImportance);
            channel.setShowBadge(true);

            manager.createNotificationChannel(channel);
            //int id = (int)SystemClock.currentThreadTimeMillis();
            manager.notify(id++, notification);

        }else {
            Notification notification = new NotificationCompat.Builder(context, "default")
                    .setSmallIcon(R.drawable.push_small_new)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .build();
            //int id = (int)SystemClock.currentThreadTimeMillis();
            manager.notify(id++, notification);
        }

    }


    private static PendingIntent createPendingIntent(MixtureData mixtureData,Context context){
        //"targetType": 1, 1：文章，2：微视
        //articleType : 1:时事新闻，3：党建基层，5：学习领袖，6：党员教育片，7：时代先锋，8：直播
        int targetType = mixtureData.targetType;
        int articleType = mixtureData.articleType;
        Intent intent = null;
        PendingIntent pendingIntent = null;
        //MyApplication.mixtureData = mixtureData;
        if (targetType == 0){          //普通的文本通知推送
            intent = new Intent(context, HomeActivity.class);
            pendingIntent = PendingIntent.getActivity(context,requestCode++,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        }else if (targetType == 2){     //微视
            //intent = new Intent(context, PlayMicroVideoMixActivity.class);
            //intent.putExtra("MixtureData",mixtureData);
            intent = new Intent(context, MicroVideoPlayActivity.class);
            MicroVideo microVideo = mixDataToMicroVideo(mixtureData);
            intent.putExtra("MicroVideo",microVideo);

            pendingIntent = PendingIntent.getActivity(context,requestCode++,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        }else {
            if (articleType == 1){  //时事新闻
                intent = new Intent(context, NewsDetailMixActivity.class);
                intent.putExtra("MixtureData",mixtureData);
                pendingIntent = PendingIntent.getActivity(context,requestCode++,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            }else {  //视频
                intent = new Intent(context, PlayVideoDetailMixActivity.class);
                intent.putExtra("MixtureData",mixtureData);
                pendingIntent = PendingIntent.getActivity(context,requestCode++,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }

        return pendingIntent;
    }


    /**
     * 将混合 数据 转为 微视数据
     * @param mixtureData
     * @return
     */
    private static MicroVideo mixDataToMicroVideo(MixtureData mixtureData){
        MicroVideo microVideo = new MicroVideo(mixtureData.targetId,mixtureData.userId,mixtureData.userName,
                mixtureData.userHeadImg,mixtureData.targetTitle,mixtureData.urlType,mixtureData.fileUrl,
                mixtureData.videoCover);
        return microVideo;
    }



    /**
     * 判断某一个类是否存在任务栈里面
     *
     * @return
     */
    public static boolean isExsitMianActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context
                .getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break; // 跳出循环，优化效率
                }
            }
        }
        return flag;
    }


    /**
     * 返回app运行状态
     *
     * @param context
     *            一个context
     * @param packageName
     *            要判断应用的包名
     * @return int 1:前台 2:后台 0:不存在
     */
    public static int isAppAlive(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> listInfos = activityManager
                .getRunningTasks(20);
        // 判断程序是否在栈顶
        if (listInfos.get(0).topActivity.getPackageName().equals(packageName)) {
            return 1;
        } else {
            // 判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : listInfos) {
                if (info.topActivity.getPackageName().equals(packageName)) {
                    return 2;
                }
            }
            return 0;// 栈里找不到，返回3
        }
    }


    /**
     * 判断进程是否运行
     *
     * @param context
     * @param proessName 应用程序的主进程名一般为包名
     * @return
     */
    public static boolean isProessRunning(Context context, String proessName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 判断某个service是否正在运行
     * @param context
     * @param runService
     *            要验证的service组件的类名
     * @return
     */
    public static boolean isServiceRunning(Context context,
                                           Class<? extends Service> runService) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) am
                .getRunningServices(1024);
        for (int i = 0; i < runningService.size(); ++i) {
            if (runService.getName().equals(
                    runningService.get(i).service.getClassName().toString())) {
                return true;
            }
        }
        return false;
    }

}
