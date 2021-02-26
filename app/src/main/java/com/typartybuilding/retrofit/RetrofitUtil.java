package com.typartybuilding.retrofit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.activity.loginRelatedActivity.LoginActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.douyin.MicroVideoPlayAdapter;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.gsondata.TyUrlData;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.utils.ActivityCollectorUtil;
import com.typartybuilding.utils.appmanager.AppManageHelper;
import com.typartybuilding.utils.appmanager.SwitchBackgroundCallbacks;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private static String TAG = "RetrofitUtil";

    private volatile static RetrofitUtil sInstance;
    private Retrofit mRetrofit;
    private String baseUrl = MyApplication.getContext().getResources().getString(R.string.base_url);


    private static int code = 11;


    private RetrofitUtil(){
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
    public static RetrofitUtil getInstance(){
        if (sInstance == null){
            synchronized(RetrofitUtil.class){
                if (sInstance == null){
                    sInstance = new RetrofitUtil();
                }
            }
        }
        return sInstance;
    }

    public Retrofit getmRetrofit() {
        return mRetrofit;
    }

    /**
     *  code返回-1 ，提示信息显示 message的内容
     */
    public static void errorMsg(String message){
        Log.i(TAG, "errorMsg: message : " + message);
        if (!SwitchBackgroundCallbacks.isAppGoBackGround()) {
            Toast.makeText(MyApplication.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  code返回10，token失效等token验证问题需重新登录
     */
    public static void tokenLose(Context context,String message){
        //Toast.makeText(MyApplication.getContext(),"登陆超时，请重新登陆",Toast.LENGTH_SHORT).show();
        Toast.makeText(MyApplication.getContext(),message,Toast.LENGTH_SHORT).show();
        Intent intentAc = new Intent(context, LoginActivity.class);
        intentAc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intentAc);
        AppManageHelper.finishOtherActivity(LoginActivity.class);
//        ActivityCollectorUtil.finishAll();
    }

    /**
     * http 请求错误 的提示信息
     */
    public static void requestError(){
        if (!SwitchBackgroundCallbacks.isAppGoBackGround()){
            Toast.makeText(MyApplication.getContext(),"网络错误",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  访客 进行点赞 关注 收藏时，修改个人信息 ，提醒用户 先注册
     */
   /* public static void visitorHint(){
        Toast.makeText(MyApplication.getContext(),"请先注册",Toast.LENGTH_SHORT).show();
    }*/


    /**
     * 点赞  ,资讯文章，
     */
    public static int addLike(int articleId){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.addLike(usetId,articleId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 点赞 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     * 取消点赞，  资讯文章，
     */
    public static int delLike(int articleId){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.delLike(usetId,articleId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 取消点赞 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }


    /**
     * 收藏
     */
    public static int addCollectNew(int collectTargetId,int collectType){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.addCollect(usetId,collectTargetId ,collectType,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 收藏 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            Log.i(TAG, "onNext: message : " + generalData.message);
                            errorMsg(generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.e(TAG, "onError: e : " + e );
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     * 收藏
     */
    public static int addCollect(int collectTargetId){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.addCollect(usetId,collectTargetId ,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 收藏 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            Log.i(TAG, "onNext: message : " + generalData.message);
                            errorMsg(generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.e(TAG, "onError: e : " + e );
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     *   取消收藏
     */
    public static int delCollectNew(int collectTargetId,int collectType){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.delCollectNew(usetId,collectTargetId,collectType,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 取消收藏 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     *   取消收藏
     */
    public static int delCollect(int collectTargetId){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.delCollect(usetId,collectTargetId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 取消收藏 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     * 关注,用于 微视 和 用户详情
     */
    public static int addFocus(int followedUserId, String followedUserName, String followedUserImg, ImageView imageView){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.addFocus(usetId,followedUserId,followedUserName,followedUserImg,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 关注 code : " + code);
                        if (code == 0){
                            imageView.setVisibility(View.INVISIBLE);
                        }else if (code == -1){
                            Log.i(TAG, "onNext: message : " + generalData.message);
                            errorMsg(generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.e(TAG, "onError: e : " + e );
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     *  用于列表播放微视屏，关注后，需更改列表中 同一用户的关注状态
     * @param followedUserId
     * @param followedUserName
     * @param followedUserImg
     * @param imageView
     * @param dataList    微视数据列表
     * @param pos
     * @param adapter
     * @return
     */
    public static int addFocus(int followedUserId, String followedUserName, String followedUserImg,
                               ImageView imageView, List<MicroVideo> dataList, int pos, MicroVideoPlayAdapter adapter){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.addFocus(usetId,followedUserId,followedUserName,followedUserImg,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 关注 code : " + code);
                        if (code == 0){
                            imageView.setVisibility(View.INVISIBLE);
                            for (MicroVideo microVideo : dataList){
                                if (followedUserId == microVideo.userId){
                                    microVideo.isFollowed = 1;
                                }
                            }
                            if (pos > 0 ){
                                //刷新前面的5条数据
                                for (int i = 1; i < 6; i++){
                                    if (pos - i < 0){
                                        break;
                                    }
                                    if (followedUserId == dataList.get(pos-i).userId) {
                                        adapter.notifyItemChanged(pos-i);
                                    }
                                }
                            }
                            if (pos < dataList.size()-1){
                                //刷新后面的5条数据
                                for (int i = 1; i < 6; i++){
                                    if (pos + i > dataList.size()-1){
                                        break;
                                    }
                                    if (followedUserId == dataList.get(pos+i).userId) {
                                        adapter.notifyItemChanged(pos+i);
                                    }
                                }

                            }

                        }else if (code == -1){
                            Log.i(TAG, "onNext: message : " + generalData.message);
                            errorMsg(generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.e(TAG, "onError: e : " + e );
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }


    //用于，我的关注页面 和  人气榜单
    public static int addFocus2(int followedUserId, String followedUserName, String followedUserImg, ImageView imageView){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.addFocus(usetId,followedUserId,followedUserName,followedUserImg,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 关注 code : " + code);
                        if (code == 0){
                            imageView.setSelected(true);
                        }else if (code == -1){
                            Log.i(TAG, "onNext: message : " + generalData.message);
                            errorMsg(generalData.message);
                            imageView.setSelected(false);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.e(TAG, "onError: e : " + e );
                        imageView.setSelected(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     *  取消关注   followedUserId 被关注者ID
     */
    public static int delFocus(int followedUserId,ImageView imageView){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.delFocus(usetId,followedUserId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 取消关注 code : " + code);
                        if (code == 0){
                            imageView.setSelected(false);
                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: message : " + generalData.message);
                            imageView.setSelected(true);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                        imageView.setSelected(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     * 党建微视点赞
     * @param visionId     微视ID
     * @param visionUid    发表人ID
     * @return
     */
    public static int microPraise(int visionId,int visionUid){
        code = 11;
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.microPraise(visionId,visionUid,usetId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 微视点赞 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     *  党建微视取消点赞
     * @param visionId
     * @param visionUid
     * @return
     */
    public static int cancelPraise(int visionId,int visionUid){
        int usetId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        code = 11;
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.cancelPraise(visionId,visionUid,usetId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 微视点赞 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }


    /**
     *  添加浏览历史记录，更新浏览量/播放量
     * @param hisTargetType      1：咨询文章，//2：党建微视 不使用该接口
     * @param hisTargetId        对象ID
     * @return
     */
    public static int addBrowsing(int hisTargetType,int hisTargetId){
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        code = 11;
        Log.i(TAG, "addBrowsing: hisTargetType ； " + hisTargetType);
        Log.i(TAG, "addBrowsing: hisTargetId : " + hisTargetId);
        Log.i(TAG, "addBrowsing: userId : " + userId);
        Log.i(TAG, "addBrowsing: token : " + token);
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.addBrowsing(userId,hisTargetType,hisTargetId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 增加浏览记录 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: 增加浏览记录message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     *组工的浏览数据
     * @param gwId
     * @param token
     */
    public static void addBrowseTimes(int gwId,String  token){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.addBrowseTimes( gwId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {

                    }

                    @Override
                    public void onError(Throwable e) {


                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    /**
     *  添加分享记录且积分
     * @param shareTargetType    1：文章，2：党建微视，3：心愿
     * @param shareTargetId      分享对象ID
     * @return
     */
    public static int shareRecord(int shareTargetType,int shareTargetId){
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        code = 11;
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.shareRecord(userId,shareTargetType,shareTargetId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 添加分享记录且积分 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: 添加分享记录且积分message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }


    public static int  browseMicro(int visionId){
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        code = 11;
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.browseMicro(userId,visionId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 微视浏览记录 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: 微视浏览记录message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }


    /**
     * 清除 所有 足迹
     * @return
     */
    public static int  delAllFootprint(){
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        code = 11;
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.delAllFootprint(userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 清除所有足迹 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: 微视浏览记录message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

    /**
     * 清除 单条 足迹
     * @param hisId
     * @return
     */
    public static int delSingleFootprint(int hisId){
        //int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        code = 11;
        Retrofit retrofit = getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.delSingleFootprint(hisId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 清除单个足迹 code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            errorMsg(generalData.message);
                            Log.i(TAG, "onNext: 微视浏览记录message : " + generalData.message);
                        }else if (code == 10){
                            tokenLose(MyApplication.getContext(),generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestError();
                        Log.i(TAG, "onError: e : " + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return code;
    }

}
