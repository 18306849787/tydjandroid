package com.typartybuilding.loader;

import com.typartybuilding.bean.HomeDynamicBean;
import com.typartybuilding.bean.HomeFraBannerBean;
import com.typartybuilding.bean.HomeRecommentBean;
import com.typartybuilding.bean.pblibrary.SysMessageBean;
import com.typartybuilding.network.https.BaseLoader;
import com.typartybuilding.network.https.BaseResponse;
import com.typartybuilding.network.https.PreLoad;
import com.typartybuilding.network.https.RetrofitManager;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-04
 * @describe
 */
public class HomePagerLoader extends BaseLoader {
    HomepagerService homepagerService;
    public HomePagerLoader(){
        homepagerService = RetrofitManager.getInstance().create(HomepagerService.class);
    }

    public Observable<HomeFraBannerBean> getBanner(String token){
        return observe(homepagerService.getBanner(token)).map(new PreLoad<>());
    }

    public Observable<HomeDynamicBean> getHomeDynamic(int pageSize){
        return observe(homepagerService.getHomeDynamic(pageSize)).map(new PreLoad<>());
    }

    public Observable<HomeDynamicBean> getDynamic(int pageNo,int pageSize){
        return observe(homepagerService.getDynamic(pageNo,pageSize)).map(new PreLoad<>());
    }

    public Observable<HomeRecommentBean> getHomeRecomment(int  pageNo){
        return observe(homepagerService.getHomeRecomment(pageNo)).map(new PreLoad<>());
    }

    public Observable<HomeRecommentBean> getHomeRecomment(int pageNo,int pageSize){
        return observe(homepagerService.getHomeRecomment(pageNo,pageSize)).map(new PreLoad<>());
    }

    public Observable<SysMessageBean> getSysMessage(){
        return observe(homepagerService.getSysMessage().map(new PreLoad<>()));
    }

    interface HomepagerService{
        @FormUrlEncoded
        @POST("app/article/selectedArticle")
        Observable<BaseResponse<HomeFraBannerBean>> getBanner(@Field("token") String token);


        @FormUrlEncoded
        @POST("app/groupWork/getGroupListWithPage")
        Observable<BaseResponse<HomeDynamicBean>> getHomeDynamic(@Field("pageSize") int pageSize);

        @FormUrlEncoded
        @POST("app/groupWork/getGroupListWithPage")
        Observable<BaseResponse<HomeDynamicBean>> getDynamic(@Field("pageNo") int pageNo,
                                                                 @Field("pageSize") int pageSize
                                                             );
        @FormUrlEncoded
        @POST("app/article/getSelectedList")
        Observable<BaseResponse<HomeRecommentBean>> getHomeRecomment(@Field("pageNo") int pageNo,@Field("pageSize") int pageSize);


        @FormUrlEncoded
        @POST("app/article/getSelectedList")
        Observable<BaseResponse<HomeRecommentBean>> getHomeRecomment(@Field("pageNo") int pageNo );


        @POST("app/message/list")
        Observable<BaseResponse<SysMessageBean>> getSysMessage();


    }
}
