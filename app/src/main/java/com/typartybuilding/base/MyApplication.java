package com.typartybuilding.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.igexin.sdk.PushManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.typartybuilding.BuildConfig;
import com.typartybuilding.R;
import com.typartybuilding.activity.loginRelatedActivity.LoginActivity;
import com.typartybuilding.activity.myRelatedActivity.SystemSettingsActivity;
import com.typartybuilding.baiduface.Config;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.MixtureData;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;
import com.typartybuilding.gsondata.dreamwish.DreamWishData;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.gsondata.loginregister.UserData;
import com.typartybuilding.gsondata.loginregister.VisitorData;
import com.typartybuilding.gsondata.tyorganization.TyOrganizationData;
import com.typartybuilding.jshare.ShareBoard;
import com.typartybuilding.jshare.ShareBoardlistener;
import com.typartybuilding.jshare.SnsPlatform;
import com.typartybuilding.reciver.MyPlayerReceiver;
import com.typartybuilding.retrofit.LoginRegister;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.service.GtIntentService;
import com.typartybuilding.service.GtPushService;
import com.typartybuilding.utils.ActivityCollectorUtil;
import com.typartybuilding.utils.CornerTransform;
import com.typartybuilding.utils.DisplayUtils;
import com.typartybuilding.utils.MyCrashHandler;
import com.typartybuilding.utils.appmanager.AppManageHelper;
import com.typartybuilding.utils.appmanager.SwitchBackgroundCallbacks;
import com.ximalaya.ting.android.opensdk.constants.ConstantsOpenSdk;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.ShareParams;
import cn.jiguang.share.android.utils.Logger;
import cn.jiguang.share.facebook.Facebook;
import cn.jiguang.share.facebook.messenger.FbMessenger;
import cn.jiguang.share.jchatpro.JChatPro;
import cn.jiguang.share.qqmodel.QQ;
import cn.jiguang.share.qqmodel.QZone;
import cn.jiguang.share.twitter.Twitter;
import cn.jiguang.share.wechat.Wechat;
import cn.jiguang.share.wechat.WechatFavorite;
import cn.jiguang.share.wechat.WechatMoments;
import cn.jiguang.share.weibo.SinaWeibo;
import cn.jiguang.share.weibo.SinaWeiboMessage;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class MyApplication extends Application {

    private static String TAG = "MyApplication";


    private static Context context;
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    //默认经纬度,太原市
    public static double longitude = 112.55;  //经度
    public static double latitude = 37.87;    //纬度

    public static String prefKey1 = "bpb_site";        //基层党建页面， 存放用户选择的地址下标
    public static String prefKey2 = "pbm_site";        //党建地图页面， 存放用户选择的地址
    public static String prefKey3 = "profile_site";    //个人信息里，所在的地区
    public static String prefKey4_audio_path = "audio_path";          //录音后的音频文件路径
    public static String prefKey4_audio_elpased = "elpased";          //录音后的音频文件 的时长
    public static String prefKey5_phone = "phone";                    //登陆，注册用户的电话号码
    public static String prefKey6_GT_clientId = "gt_client_id";       //个推返回的客户id
    public static String prefKey7_login_state = "login_state";       //记录登陆状态，1 已经登陆，0 没有登陆
    public static String prefKey8_login_token = "login_token";       //存放登陆后的token
    public static String pretKey9_login_userId = "login_userId";     //存放用户id
    public static String prefKey10_login_userType = "login_userType"; //用户类型
    public static String prefKey11_userData = "login_userData";       //存放获取的用户数据
    public static String prefKey12_login_headImg = "head_img";        //用户头像
    public static String prefKey13_login_userName = "login_userName";  //用户名
    public static String prefKey14_search_his = "search_his";         //搜索历史
    public static String prefKey15_launch_gif = "launch_gif";         //启动页gif url
    //public static String prefKey14_change_headimg = "change_headimg";        //记录，是否更改了头像

    public static boolean isChageHeadimg;                              //记录，是否更改了头像

    public static ArticleBanner articleBanner;      //用于资讯文章，activity之间传递引用，便于修改数据
    public static MixtureData mixtureData;          //用于混合数据页面，activity之间传递引用，便于修改数据

    public static ChoicenessData.MicroVision microVision;    //用于首页微视屏，activity之间传递引用，便于修改数据
    public static MicroVideo microVideo;            //个人 微视数据 和  发现精彩 微视数据  ,activity之间传递引用

    public static DreamWishData.WishData wishData;   //向activity传递心愿数据，返回不刷新，修改本地数据

    public static TyOrganizationData.OrganizationData organizationData;  //向activity传递 太原组工 数据，返回不刷新，修改本地数据

    public static boolean isRemind = false;        //在移动数据时，播微视，提醒用户，只提醒一次





    //16：9
    public static RequestOptions requestOptions = new RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.shape_default_pic)
            .error(R.mipmap.default_pic_16_9);

    //用户精选页面，视频封面
    public static RequestOptions requestOptionsVideo = new RequestOptions()
            .skipMemoryCache(true)
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.shape_default_pic)
            .error(R.mipmap.default_pic_16_9)
            //.override(400,226)
            .centerCrop();        //R.mipmap.img_tuliele

    //其他页面视频封面图， 小图， 学习时刻视频封面，播放页面推荐视频封面
    public static RequestOptions requestOptionsVideo2 = new RequestOptions()
            .skipMemoryCache(true)
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.shape_default_pic)
            .error(R.mipmap.default_pic_16_9);
            //.override(200,113);


    //9:16   播放微视 页面
    public static RequestOptions requestOptions916 = new RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.shape_default_pic)
            .error(R.mipmap.default_pic_9_16);        //R.mipmap.img_tuliele

    //4:3 ， 新闻封面图,  我的足迹，我的收藏，搜索，党建热搜的视频封面图
    public static RequestOptions requestOptions43 = new RequestOptions()
            .placeholder(R.drawable.shape_default_pic)
            .error(R.mipmap.default_pic_4_3);
            //.override(200,150);

    //3:4  微视
    public static RequestOptions requestOptions34 = new RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.shape_default_pic)
            .error(R.mipmap.default_pic_1_1);

    //用于音频封面
    public static RequestOptions requestOptionsAudio = new RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.shape_default_pic)
            .error(R.mipmap.yinpin_imgbg);

    //2：3
    public static RequestOptions requestOptions23 = new RequestOptions()
            .placeholder(R.drawable.shape_default_pic)
            .error(R.mipmap.default_pic_2_3);

    //1：1
    public static RequestOptions requestOptions11 = new RequestOptions()
            .placeholder(R.drawable.shape_default_pic)
            .error(R.mipmap.default_pic_1_1);

    //头像
    public static RequestOptions requestOptions2 = new RequestOptions()
            .placeholder(R.mipmap.img_avatar)
            .error(R.mipmap.img_avatar);        //R.mipmap.img_tuliele


    //SmartRrefreshLayout
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorBg, android.R.color.black);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                layout.setPrimaryColorsId(R.color.colorBg,android.R.color.black);
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        context = getApplicationContext();
        //注册Activity生命周期回调的统一管理
        registerActivityLifecycleCallbacks(new SwitchBackgroundCallbacks());
        //LitePalApplication.initialize(context);

        //拿到全局的SharedPreferences
        pref = getSharedPreferences("TY",MODE_PRIVATE);
        editor = pref.edit();

        //初始化 个推服务
        initPushService();

        //初始化 极光分享
        initJShare();

        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this);
        MyCrashHandler.getInstance().init(getApplicationContext());

    }

    public static Context getContext(){
        return context;
    }



    /**
     *  访客， 点击 我的页面， 收藏，点赞，分享，时提醒用户 登陆或注册
     */
    public static void remindVisitor(Activity activity){
        Log.i(TAG, "remindVisitor: ");
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("温馨提示");
        dialog.setMessage("请先登陆");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: 确定");
                exitLogin(activity);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.i(TAG, "onClick: 取消");
            }
        });
        dialog.show();

    }

    /**
     *  退出登陆 接口
     */
    public static void exitLogin(Activity activity){
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        LoginRegister loginRegister = retrofit.create(LoginRegister.class);
        loginRegister.exitLogin(userId, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReciMsgData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ReciMsgData reciMsgData) {
                        int code = Integer.valueOf(reciMsgData.code);
                        Log.i(TAG, "onNext: code : " + reciMsgData.code);
                        if (code == 0){
                            Intent intentAc = new Intent(context, LoginActivity.class);
                            activity.startActivity(intentAc);
                            //登陆状态清0
                            editor.putInt(MyApplication.prefKey7_login_state,0);
                            editor.apply();
                            activity.finish();
//                            ActivityCollectorUtil.finishAll();
                            AppManageHelper.finishOtherActivity(LoginActivity.class);

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(reciMsgData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    /**
     * 初始化 喜马拉雅 sdk
     */
    public static void initXiMaLaYa(){
        ConstantsOpenSdk.isDebug = true;

        CommonRequest mXimalaya = CommonRequest.getInstanse();
        if(DTransferConstants.isRelease) {
            String mAppSecret = "bd8c8eb4f4f352d38d2868367115d945";
            mXimalaya.setAppkey("3c2189a8b83ae602fe4bbc04acf55db3");
            mXimalaya.setPackid("com.typartybuilding");
            mXimalaya.init(context ,mAppSecret);
        } else {
            String mAppSecret = "bd8c8eb4f4f352d38d2868367115d945";
            mXimalaya.setAppkey("3c2189a8b83ae602fe4bbc04acf55db3");
            mXimalaya.setPackid("com.typartybuilding");
            mXimalaya.init(context ,mAppSecret);
        }
    }

    //喜马拉雅播放器通知栏使用
    private void initXiMaLaYaNotification(){
        if(BaseUtil.getCurProcessName(this).contains(":player")) {
            XmNotificationCreater instanse = XmNotificationCreater.getInstanse(this);
            instanse.setNextPendingIntent((PendingIntent)null);
            instanse.setPrePendingIntent((PendingIntent)null);

            String actionName = "com.app.test.android.Action_Close";
            Intent intent = new Intent(actionName);
            intent.setClass(this, MyPlayerReceiver.class);
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent, 0);
            instanse.setClosePendingIntent(broadcast);

            String pauseActionName = "com.app.test.android.Action_PAUSE_START";
            Intent intent1 = new Intent(pauseActionName);
            intent1.setClass(this, MyPlayerReceiver.class);
            PendingIntent broadcast1 = PendingIntent.getBroadcast(this, 0, intent1, 0);
            instanse.setStartOrPausePendingIntent(broadcast1);
        }
    }

    /**
     * 初始化个推
     */
    public static void initPushService(){
        PushManager.getInstance().initialize(context, GtPushService.class);
        PushManager.getInstance().registerPushIntentService(context, GtIntentService.class);
    }

    /**
     * 初始化 极光分享
     */
    private void initJShare(){
        JShareInterface.init(context);
    }





}
