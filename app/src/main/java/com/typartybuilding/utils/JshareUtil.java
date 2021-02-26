package com.typartybuilding.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.jshare.ShareBoard;
import com.typartybuilding.jshare.ShareBoardlistener;
import com.typartybuilding.jshare.SnsPlatform;
import com.typartybuilding.retrofit.RetrofitUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.ShareParams;
import cn.jiguang.share.android.utils.Logger;
import cn.jiguang.share.qqmodel.QQ;
import cn.jiguang.share.qqmodel.QZone;
import cn.jiguang.share.wechat.Wechat;
import cn.jiguang.share.wechat.WechatFavorite;
import cn.jiguang.share.wechat.WechatMoments;
import cn.jiguang.share.weibo.SinaWeibo;
import cn.jiguang.share.weibo.SinaWeiboMessage;


/**
 *   极光分享 工具类
 */

public class JshareUtil {

    private static String TAG = "JshareUtil";

    //极光分享
    //private static ShareBoard mShareBoard;
    private static int mAction = Platform.ACTION_SHARE;


    /**
     *  分享时调用
     * @param activity
     * @param title     标题
     * @param text      内容简介
     * @param url       分享url
     * @param shareTargetType    1：文章，2：党建微视，3：心愿
     * @param shareTargetId      分享对象ID
     */
    public static void showBroadView(Activity activity,String title,String text,String url,int shareTargetType,int shareTargetId) {

        ShareBoard mShareBoard = new ShareBoard(activity);
        ShareParams shareParams = new ShareParams();
        //设置分享参数
        //setShareParams(shareParams,title,text,url);

        List<String> platforms = JShareInterface.getPlatformList();
        if (platforms != null) {
            Iterator var2 = platforms.iterator();
            while (var2.hasNext()) {
                String temp = (String) var2.next();
                //取消新浪微博私信
                if (temp.equals(SinaWeiboMessage.Name)){
                    continue;
                }
                SnsPlatform snsPlatform = createSnsPlatform(temp);
                mShareBoard.addPlatform(snsPlatform);
            }
        }
        mShareBoard.setShareboardclickCallback(new ShareBoardlistener() {
            @Override
            public void onclick(SnsPlatform snsPlatform, String platform) {
                Log.i(TAG, "onclick: snsPlatform : " + snsPlatform.toString());
                switch (mAction) {
                    case Platform.ACTION_SHARE:
                        Log.i(TAG, "onclick: platform : " + platform);

                        //设置分享参数
                        setShareParams(platform,shareParams,title,text,url,null);

                        JShareInterface.share(platform, shareParams, new PlatActionListener() {
                            @Override
                            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                                //添加分享记录 加积分
                                RetrofitUtil.shareRecord(shareTargetType,shareTargetId);
                                Log.i(TAG, "onComplete: 分享成功");
                                if (!platform.getName().contains("Wechat")){
                                    Toast.makeText(MyApplication.getContext(),"分享成功",Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Platform platform, int i, int i1, Throwable throwable) {
                                Log.i(TAG, "onError: onError");
                            }

                            @Override
                            public void onCancel(Platform platform, int i) {
                                Log.i(TAG, "onCancel: 取消分享");
                                Toast.makeText(MyApplication.getContext(),"取消分享",Toast.LENGTH_SHORT).show();
                            }
                        });

                        break;
                    default:
                        break;
                }
            }
        });

        mShareBoard.show();
    }


    /**
     *  分享时调用
     * @param activity
     * @param title     标题
     * @param text      内容简介
     * @param url       分享url
     * @param shareTargetType    1：文章，2：党建微视，3：心愿
     * @param shareTargetId      分享对象ID
     */
    public static void showBroadView(Activity activity,String title,String text,String url,int shareTargetType,int shareTargetId,String imageUrl) {

        ShareBoard mShareBoard = new ShareBoard(activity);
        ShareParams shareParams = new ShareParams();
        //设置分享参数
        //setShareParams(shareParams,title,text,url);

        List<String> platforms = JShareInterface.getPlatformList();
        if (platforms != null) {
            Iterator var2 = platforms.iterator();
            while (var2.hasNext()) {
                String temp = (String) var2.next();
                //取消新浪微博私信
                if (temp.equals(SinaWeiboMessage.Name)){
                    continue;
                }
                SnsPlatform snsPlatform = createSnsPlatform(temp);
                mShareBoard.addPlatform(snsPlatform);
            }
        }
        mShareBoard.setShareboardclickCallback(new ShareBoardlistener() {
            @Override
            public void onclick(SnsPlatform snsPlatform, String platform) {
                Log.i(TAG, "onclick: snsPlatform : " + snsPlatform.toString());
                switch (mAction) {
                    case Platform.ACTION_SHARE:
                        Log.i(TAG, "onclick: platform : " + platform);

                        //设置分享参数
                        setShareParams(platform,shareParams,title,text,url,imageUrl);

                        JShareInterface.share(platform, shareParams, new PlatActionListener() {
                            @Override
                            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                                //添加分享记录 加积分
                                RetrofitUtil.shareRecord(shareTargetType,shareTargetId);
                                Log.i(TAG, "onComplete: 分享成功");
                                if (!platform.getName().contains("Wechat")){
                                    Toast.makeText(MyApplication.getContext(),"分享成功",Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Platform platform, int i, int i1, Throwable throwable) {

                            }

                            @Override
                            public void onCancel(Platform platform, int i) {
                                Log.i(TAG, "onCancel: 取消分享");
                                Toast.makeText(MyApplication.getContext(),"取消分享",Toast.LENGTH_SHORT).show();
                            }
                        });

                        break;
                    default:
                        break;
                }
            }
        });

        mShareBoard.show();
    }
    /**
     * 分享连接
     * @param platform  平台名称
     * @param shareParams
     * @param title
     * @param text
     * @param url
     */
    private static void setShareParams( String platform,ShareParams shareParams,String title,String text,String url,String imageUrl){
        Log.i(TAG, "setShareParams: title : " + title);
        Log.i(TAG, "setShareParams: text : " + text);

        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        //shareParams.setImagePath(MyApplication.ImagePath);

        if (title == null || title == ""){
            title = ".";
        }
        if (text == null || text == ""){
            text = title;
        }
        //若分享平台为QQ  text不超过 40 字符，  title不超过 30 字符
        if (platform.contains("QQ")){
            if (title.length() > 30){
                title = title.substring(0,30);
            }
            if (text.length() > 40){
                text = text.substring(0,40);
            }
            shareParams.setTitle(title);
            shareParams.setText(text);
            shareParams.setUrl(url);

            if (TextUtils.isEmpty(imageUrl)){
                shareParams.setImagePath(Utils.resToFile(R.mipmap.share_icon));
            }else {
                shareParams.setImageUrl(imageUrl);
            }


        //若分享平台为QZone，  text最长 600 个字符， title 最长 200 个字符
        }else if (platform.contains("QZone")){
            if (title.length() > 200){
                title = title.substring(0,200);
            }
            if (text.length() > 600){
                text = text.substring(0,600);
            }

            shareParams.setTitle(title);
            shareParams.setText(text);
            shareParams.setUrl(url);
            if (TextUtils.isEmpty(imageUrl)){
                shareParams.setImagePath(Utils.resToFile(R.mipmap.share_icon));
            }else {
                shareParams.setImageUrl(imageUrl);
            }

        //分享平台为新浪微博  text 不超过1999 字符, 无title
        }else if (platform.contains("SinaWeibo")){
            if (text.length() > 1999){
                text = text.substring(0,1999);
            }
            shareParams.setText(text);
            shareParams.setUrl(url);
            if (TextUtils.isEmpty(imageUrl)){
                shareParams.setImagePath(Utils.resToFile(R.mipmap.share_icon));
            }else {
                shareParams.setImageUrl(imageUrl);
            }
            Log.i(TAG, "setShareParams: 新浪微博");

        //分享平台为 微信（包含朋友圈 和 收藏）
        }else if (platform.contains("Wechat")){
            shareParams.setTitle(title);
            shareParams.setText(text);
            shareParams.setUrl(url);
            if (TextUtils.isEmpty(imageUrl)){
                shareParams.setImagePath(Utils.resToFile(R.mipmap.share_icon));
            }else {
                shareParams.setImageUrl(imageUrl);
            }
        }

    }


    public static SnsPlatform createSnsPlatform(String platformName) {
        String mShowWord = platformName;
        String mIcon = "";
        String mGrayIcon = "";
        String mKeyword = platformName;
        if (platformName.equals(Wechat.Name)) {
            mIcon = "jiguang_socialize_wechat";
            mGrayIcon = "jiguang_socialize_wechat";
            mShowWord = "jiguang_socialize_text_weixin_key";
        } else if (platformName.equals(WechatMoments.Name)) {
            mIcon = "jiguang_socialize_wxcircle";
            mGrayIcon = "jiguang_socialize_wxcircle";
            mShowWord = "jiguang_socialize_text_weixin_circle_key";

        } else if (platformName.equals(WechatFavorite.Name)) {
            mIcon = "jiguang_socialize_wxfavorite";
            mGrayIcon = "jiguang_socialize_wxfavorite";
            mShowWord = "jiguang_socialize_text_weixin_favorite_key";

        } else if (platformName.equals(SinaWeibo.Name)) {
            mIcon = "jiguang_socialize_sina";
            mGrayIcon = "jiguang_socialize_sina";
            mShowWord = "jiguang_socialize_text_sina_key";
        } else if (platformName.equals(SinaWeiboMessage.Name)) {
            mIcon = "jiguang_socialize_sina";
            mGrayIcon = "jiguang_socialize_sina";
            mShowWord = "jiguang_socialize_text_sina_msg_key";
        } else if (platformName.equals(QQ.Name)) {
            mIcon = "jiguang_socialize_qq";
            mGrayIcon = "jiguang_socialize_qq";
            mShowWord = "jiguang_socialize_text_qq_key";

        } else if (platformName.equals(QZone.Name)) {
            mIcon = "jiguang_socialize_qzone";
            mGrayIcon = "jiguang_socialize_qzone";
            mShowWord = "jiguang_socialize_text_qq_zone_key";
        } /*else if (platformName.equals(Facebook.Name)) {
            mIcon = "jiguang_socialize_facebook";
            mGrayIcon = "jiguang_socialize_facebook";
            mShowWord = "jiguang_socialize_text_facebook_key";
        } else if (platformName.equals(FbMessenger.Name)) {
            mIcon = "jiguang_socialize_messenger";
            mGrayIcon = "jiguang_socialize_messenger";
            mShowWord = "jiguang_socialize_text_messenger_key";
        }else if (Twitter.Name.equals(platformName)) {
            mIcon = "jiguang_socialize_twitter";
            mGrayIcon = "jiguang_socialize_twitter";
            mShowWord = "jiguang_socialize_text_twitter_key";
        } else if (platformName.equals(JChatPro.Name)) {
            mShowWord = "jiguang_socialize_text_jchatpro_key";
        }*/
        return ShareBoard.createSnsPlatform(mShowWord, mKeyword, mIcon, mGrayIcon, 0);
    }







    private static PlatActionListener mShareListener = new PlatActionListener() {
        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> data) {

            Log.i(TAG, "onComplete: platform : " + platform.getName());
            Log.i(TAG, "onComplete: 分享成功");
            if (!platform.getName().equals("Wechat")){
                Toast.makeText(MyApplication.getContext(),"分享成功",Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void onError(Platform platform, int action, int errorCode, Throwable error) {
            Logger.e(TAG, "error:" + errorCode + ",msg:" + error);

        }

        @Override
        public void onCancel(Platform platform, int action) {
            Log.i(TAG, "onCancel: 取消分享");
            Toast.makeText(MyApplication.getContext(),"取消分享",Toast.LENGTH_SHORT).show();
        }
    };





 /* private static ShareBoardlistener mShareBoardlistener = new ShareBoardlistener() {
        @Override
        public void onclick(SnsPlatform snsPlatform, String platform) {

            Log.i(TAG, "onclick: snsPlatform : " + snsPlatform.toString());
            switch (mAction) {
                case Platform.ACTION_SHARE:
                    //progressDialog.show();
                    //这里以分享链接为例
                    ShareParams shareParams = new ShareParams();
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    shareParams.setTitle("百度");
                    //shareParams.setText(ShareTypeActivity.share_text);
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    shareParams.setUrl("https://www.baidu.com/");
                    //shareParams.setImagePath(MyApplication.ImagePath);
                    JShareInterface.share(platform, shareParams, mShareListener);
                    break;
                default:
                    break;
            }
        }
    };*/

}
