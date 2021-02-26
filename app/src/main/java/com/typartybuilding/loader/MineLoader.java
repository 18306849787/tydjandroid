package com.typartybuilding.loader;

import com.typartybuilding.bean.GroupWorkDetailsBean;
import com.typartybuilding.bean.PartyEducationBean;
import com.typartybuilding.bean.pblibrary.SysMessageBean;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.network.https.BaseLoader;
import com.typartybuilding.network.https.BaseResponse;
import com.typartybuilding.network.https.PreLoad;
import com.typartybuilding.network.https.RetrofitManager;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-19
 * @describe
 */
public class MineLoader extends BaseLoader {
    MineService mineService;

    public MineLoader() {
        mineService = RetrofitManager.getInstance().create(MineService.class);
    }

    public Observable<String> getSign() {
        return observe(mineService.getSign()).map(new PreLoad<>());
    }

    public Observable<ArticleBanner> getVideoDetails(int articleId) {
        return observe(mineService.getVideoDetails(articleId)).map(new PreLoad<>());
    }

    public Observable<GroupWorkDetailsBean> groupWorkDetails(int gwId) {
        return observe(mineService.groupWorkDetails(gwId)).map(new PreLoad<>());
    }

    interface MineService{

        @POST("app/Personal/sign")
        Observable<BaseResponse<String>> getSign();

        @FormUrlEncoded
        @POST("app/article/detail")
        Observable<BaseResponse<ArticleBanner>> getVideoDetails(@Field("articleId")int articleId);

        @FormUrlEncoded
        @POST("app/groupWork/detail")
        Observable<BaseResponse<GroupWorkDetailsBean>> groupWorkDetails(@Field("gwId")int gwId);



    }
}