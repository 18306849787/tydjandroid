package com.typartybuilding.loader;

import com.google.android.exoplayer2.C;
import com.typartybuilding.bean.ArticleVideoDataNew;
import com.typartybuilding.bean.HomeFraBannerBean;
import com.typartybuilding.bean.PartyEducationBean;
import com.typartybuilding.bean.PoliciesBean;
import com.typartybuilding.gsondata.ArticleVideoData;
import com.typartybuilding.gsondata.choiceness.MoreHotVideoData;
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
 * @date 2020-09-08
 * @describe
 */
public class LearnLoader extends BaseLoader {
    LearnService learnService;

    public LearnLoader() {
        learnService = RetrofitManager.getInstance().create(LearnService.class);
    }


    public Observable<ArticleVideoDataNew.Data> getBanner(String articleType,int pageSize){
        return observe(learnService.getBanner(articleType,pageSize)).map(new PreLoad<>());
    }
    public Observable<PoliciesBean> getStatuteList(int pageNo, int pageSize) {
        return observe(learnService.getStatuteList(pageNo, pageSize)).map(new PreLoad<>());
    }

    public Observable<ArticleVideoDataNew.Data> getArticleVideoData(int pageNo, int pageSize, int articleType, int articleLabel) {
        return observe(learnService.getArticleVideo(pageNo,pageSize,articleType,articleLabel)).map(new PreLoad<>());
    }

    public Observable<ArticleVideoDataNew.Data> getArticleVideoData(int pageNo, int pageSize, int articleType) {
        return observe(learnService.getArticleVideo(pageNo,pageSize,articleType)).map(new PreLoad<>());
    }

    public Observable<ArticleVideoDataNew.Data> getArticleVideoData(int articleType) {
        return observe(learnService.getArticleVideo(1,20,articleType)).map(new PreLoad<>());
    }

    public Observable<List<PartyEducationBean>> getPartyEduList(int articleType) {
        return observe(learnService.getPartyEduList(articleType)).map(new PreLoad<>());
    }

    interface LearnService {
        @POST("app/statute/list")
        @FormUrlEncoded
        Observable<BaseResponse<PoliciesBean>> getStatuteList(@Field("pageNo") int pageNo,
                                                              @Field("pageSize") int pageSize);
//        /**
//         *
//         * @param pageNo
//         * @param pageSize
//         * @param
//         * @return
//         */
//        @FormUrlEncoded
//        @POST("app/article/getHotVideoListWithPage")
//        Observable<BaseResponse<MoreHotVideoData>> getMoreHotVideo(@Field("pageNo") int pageNo,
//                                                     @Field("pageSize") int pageSize);


        @FormUrlEncoded
        @POST("app/article/getArticleListWithPage")
        Observable<BaseResponse<ArticleVideoDataNew.Data>> getBanner(@Field("articleType") String articleType,@Field("pageSize") int pageSize);


        @FormUrlEncoded
        @POST("app/article/getArticleListWithPage")
        Observable<BaseResponse<ArticleVideoDataNew.Data>> getArticleVideo(@Field("pageNo") int pageNo,
                                                                   @Field("pageSize") int pageSize, @Field("articleType") int articleType);


        @FormUrlEncoded
        @POST("app/article/getPartyEduList")
        Observable<BaseResponse<List<PartyEducationBean>>> getPartyEduList(@Field("articleType") int articleType);


        @FormUrlEncoded
        @POST("app/article/getArticleListWithPage")
        Observable<BaseResponse<ArticleVideoDataNew.Data>> getArticleVideo(@Field("pageNo") int pageNo,
                                                                   @Field("pageSize") int pageSize, @Field("articleType") int articleType,
                                                                   @Field("articleLabel") int articleLabel
               );

    }
}
