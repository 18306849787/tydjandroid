package com.typartybuilding.retrofit;


import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.TyUrlData;
import com.typartybuilding.gsondata.pblibrary.PbLibraryUrlData;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 收藏，关注，点赞等不需要 取后台数据 的接口
 */
public interface GeneralRetrofitInterface {


    String addLike = "app/regional/addLike";           //点赞      用于资讯 文章
    String delLike = "app/regional/delLike";           //取消点赞  用于资讯 文章
    String addFocus = "app/Personal/addFocus";         //关注
    String delFocus = "app/Personal/delFocus";         //取消关注
    String addCollect = "app/Personal/addCollect";    //收藏
    String delCollect = "app/Personal/delCollect";    //取消收藏

    String microPraise = "app/micro/microPraise";     //党建微视点赞
    String cancelPraise = "app/micro/cancelPraise";   //党建微视取消点赞

    String getUrl = "app/article/getUrl";             //太原组工 和 远程教育 url
    String partyLibraryBanner = "app/article/partyLibraryBanner"; //党建书屋 轮播图url

    String browsing = "app/regional/browsing";        //添加浏览历史记录，更新浏览量/播放量
    String shareRecord = "app/user/shareRecord";      //添加分享记录且积分
    String advice = "app/consulting/advice";          //建议

    String browseMicro = "app/micro/browseMicro";     //党建微视-更新浏览次数/入历史记录

    String delHistoricalRecords = "app/Personal/delHistoricalRecords";   //清除所有记录
    String delHistoricalById = "app/Personal/delHistoricalById";         //清除单条记录


    /**
     *  添加浏览历史记录，更新浏览量/播放量
     * @param userId
     * @param hisTargetType    1：咨询文章，2：党建微视
     * @param hisTargetId      对象ID
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(browsing)
    Observable<GeneralData> addBrowsing(@Field("userId") int userId,@Field("hisTargetType") int hisTargetType,@Field("hisTargetId") int hisTargetId ,@Field("token") String token);


    /**
     *  党建微视-更新浏览次数/入历史记录
     * @param userId
     * @param visionId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(browseMicro)
    Observable<GeneralData> browseMicro(@Field("userId") int userId,@Field("visionId") int visionId ,@Field("token") String token);


    /**
     *  添加分享记录且积分
     * @param userId
     * @param shareTargetType     1：文章，2：党建微视，3：心愿
     * @param shareTargetId       分享对象ID
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(shareRecord)
    Observable<GeneralData> shareRecord(@Field("userId") int userId,@Field("shareTargetType") int shareTargetType,@Field("shareTargetId") int shareTargetId ,@Field("token") String token);



    @FormUrlEncoded
    @POST(advice)
    Observable<GeneralData> addAdvice(@Field("userId") int userId,@Field("sugContent") String sugContent ,@Field("token") String token);



    /**
     * 点赞， 资讯文章
     * @param fromUid      用户id
     * @param articleId    咨询文章ID
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(addLike)
    Observable<GeneralData> addLike(@Field("fromUid") int fromUid,@Field("articleId") int articleId ,@Field("token") String token);

    /**
     *  取消点赞 ， 资讯文章
     * @param fromUid      用户id
     * @param articleId    咨询文章ID
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(delLike)
    Observable<GeneralData> delLike(@Field("fromUid") int fromUid,@Field("articleId") int articleId ,@Field("token") String token);


    /**
     *  关注
     * @param userId
     * @param followedUserId       被关注者ID
     * @param followedUserName     被关注者昵称
     * @param followedUserImg      被关注者头像
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(addFocus)
    Observable<GeneralData> addFocus(@Field("userId") int userId, @Field("followedUserId") int followedUserId, @Field("followedUserName") String followedUserName,
                                       @Field("followedUserImg") String followedUserImg,@Field("token") String token);

    /**
     * 取消关注
     * @param userId            用户ID
     * @param followedUserId    被关注者ID
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(delFocus)
    Observable<GeneralData> delFocus(@Field("userId") int userId,@Field("followedUserId") int followedUserId ,@Field("token") String token);



    /**
     *   收藏，collectType 1：咨询/文章/视频，2：党建微视
     *         collectTargetId  收藏目标ID，根据collectType
     */
    @FormUrlEncoded
    @POST(addCollect)
    Observable<GeneralData> addCollect(@Field("userId") int userId, /*@Field("collectType") int collectType,*/
                                       @Field("collectTargetId") int collectTargetId,@Field("token") String token);

    /**
     *   收藏，collectType 1：咨询/文章/视频，2：党建微视
     *         collectTargetId  收藏目标ID，根据collectType
     *
     *         collectType 1,文章，3组工
     */
    @FormUrlEncoded
    @POST(addCollect)
    Observable<GeneralData> addCollect(@Field("userId") int userId, /*@Field("collectType") int collectType,*/
                                       @Field("collectTargetId") int collectTargetId,@Field("collectType") int collectType,@Field("token") String token);

    /**
     *  取消收藏，
     */
    @FormUrlEncoded
    @POST(delCollect)
    Observable<GeneralData> delCollectNew(@Field("userId") int userId,@Field("collectTargetId") int collectTargetId,
                                       @Field("collectType") int collectType,
                                       @Field("token") String token);

    /**
     *  取消收藏，
     */
    @FormUrlEncoded
    @POST(delCollect)
    Observable<GeneralData> delCollect(@Field("userId") int userId,@Field("collectTargetId") int collectTargetId,@Field("token") String token);


    /**
     * 党建微视点赞
     * @param visionId    微视ID
     * @param visionUid   发表人ID
     * @param userId      点赞人ID
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(microPraise)
    Observable<GeneralData> microPraise(@Field("visionId") int visionId,@Field("visionUid") int visionUid,
                                        @Field("userId") int userId ,@Field("token") String token);

    /**
     * 党建微视取消点赞
     * @param visionId
     * @param visionUid
     * @param userId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(cancelPraise)
    Observable<GeneralData> cancelPraise(@Field("visionId") int visionId,@Field("visionUid") int visionUid,
                                        @Field("userId") int userId ,@Field("token") String token);


    /**
     * 获取 太原组工 和 远程教育 url
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(getUrl)
    Observable<TyUrlData> getUrl(@Field("token") String token);


    @FormUrlEncoded
    @POST(partyLibraryBanner)
    Observable<PbLibraryUrlData> getPbLibraryUrl(@Field("token") String token);


    /**
     * 清除所有记录
     * @param userId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(delHistoricalRecords)
    Observable<GeneralData> delAllFootprint(@Field("userId") int userId,@Field("token") String token);

    /**
     * 清除 单条 记录
     * @param hisId    id
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(delHistoricalById )
    Observable<GeneralData> delSingleFootprint(@Field("hisId") int hisId,@Field("token") String token);


}
