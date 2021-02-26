package com.typartybuilding.retrofit;

import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.GeneralMixtureData;
import com.typartybuilding.gsondata.answer.AnswerUrlData;
import com.typartybuilding.gsondata.answer.IntegralData;
import com.typartybuilding.gsondata.answer.QuestionListData;
import com.typartybuilding.gsondata.bgmusic.BackgroundMusicData;
import com.typartybuilding.gsondata.personaldata.MyFocusData;
import com.typartybuilding.gsondata.personaldata.PersonalInfo;
import com.typartybuilding.gsondata.personaldata.TaMicro;
import com.typartybuilding.gsondata.personaldata.TaMicroVideo;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 *  个人相关的 接口， 如 个人信息修改， 个人微视, 微视发布
 */

public interface PersonalRetrofitInterface {

    String myMicroTrack = "app/user/myMicroTrack";   //我/TA的微视 的信息
    String myMicroList = "app/micro/myMicroList";    //我/TA的微视-作品
    String delMicro = "app/micro/delMicro";          //删除作品

    String savePerson = "app/Personal/savePerson";     //.保存用户资料
    String queryPerson = "app/Personal/queryPerson";  //个人基础信息
    String headUpload = "app/Personal/headUpload";    //上传头像
    String queryFocus = "app/Personal/queryFocus";    //我的关注
    String queryCollect = "app/Personal/queryCollect";   //我的收藏
    String getHistoryRecord = "app/user/getHistoryRecord";   //历史记录，我的收藏
    String faceUpdateUser = "app/user/faceUpdateUser";        //修改人脸

    String questionList = "app/answer/questionList";       //.题目列表-随机抽取
    String answerSave = "app/answer/save";                 //答题保存-错题加入错题集

    //新增 ， 答题直接 加载链接内容
    String getUrl = "app/answer/getUrl";              //答题链接，知识竞答
    String getCgUrl = "app/answer/getCgUrl";          //答题闯关

    String addMicro = "app/micro/addMicro";                       //发布党建微视
    String getBgmListWithPage = "app/micro/getBgmListWithPage";  //背景音乐-列表

    //朗读厅
    String addRead = "app/read/addRead";           //朗读厅-发布增加朗读次数


    /**
     *  保存用户资料，完善个人信息页面，修改地区
     * @param userId
     * @param address   地区
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(savePerson)
    Observable<GeneralData> savePerson(@Field("userId") int userId,@Field("address") String address, @Field("token") String token);


    /**
     *  个人基础信息
     * @param userId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(queryPerson)
    Observable<PersonalInfo> getPersonalInfo(@Field("userId") int userId, @Field("token") String token);


    /**
     *    我/TA的微视 的信息
     * @param userId     他的ID
     * @param loginId    登录人ID，查看TA的微视时必传
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(myMicroTrack)
    Observable<TaMicro> getMicroInfo(@Field("userId") int userId,@Field("loginUid") Integer loginId,@Field("token") String token);


    /**
     *     我/TA的 微视作品
     * @param userId       用户ID
     * @param loginId      登录人ID，查看TA的微视时必传
     * @param pageNo
     * @param pageSize
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(myMicroList)
    Observable<TaMicroVideo> getMicroVideo(@Field("userId") int userId, @Field("loginId") Integer loginId, @Field("pageNo") int pageNo,
                                           @Field("pageSize") int pageSize, @Field("token") String token);

    /**
     *  用于，在我的微视 或 他的微视  页面，点击播放后，可上下滑动切换视频
     * @param userId
     * @param loginId
     * @param pageNo
     * @param pageSize
     * @param token
     * @param visionId     查询滑动列表时，从当前id开始查询（不包括当前ID）
     * @param visionType    1：图片，2：视频，3：音频
     * @return
     */
    @FormUrlEncoded
    @POST(myMicroList)
    Observable<TaMicroVideo> getMicroVideo2(@Field("userId") int userId, @Field("loginId") Integer loginId, @Field("pageNo") int pageNo,
                                           @Field("pageSize") int pageSize, @Field("token") String token,
                                            @Field("visionId") int visionId, @Field("visionType") int visionType);



    /**
     *  删除作品
     * @param visionId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(delMicro)
    Observable<GeneralData> delMicro(@Field("visionId") int visionId, @Field("token") String token);


    /**
     *  上传头像
     * @param partList
     * @return
     */
    //@FormUrlEncoded
    @Multipart
    @POST(headUpload)
    Observable<GeneralData> headUpload(/*@Field("userId") int userId,@Field("token") String token,*/
                                       @Part List<MultipartBody.Part> partList);


    /**
     *  关注列表，我的关注
     * @param userId
     * @param pageNo
     * @param pageSize
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(queryFocus)
    Observable<MyFocusData> getFocusData(@Field("userId") int userId, @Field("pageNo") int pageNo,
                                          @Field("pageSize") int pageSize, @Field("token") String token);


    /**
     *  我的收藏
     * @param pageNo
     * @param pageSize
     * @param userId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(queryCollect)
    Observable<GeneralMixtureData> getCollectData(@Field("pageNo") int pageNo, @Field("pageSize") int pageSize,
                                                  @Field("userId") int userId, @Field("token") String token);


    /**
     *  我的足迹
     * @param pageNo
     * @param pageSize
     * @param userId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(getHistoryRecord)
    Observable<GeneralMixtureData> getMyFootprint(@Field("pageNo") int pageNo, @Field("pageSize") int pageSize,
                                                  @Field("userId") int userId, @Field("token") String token);


    /**
     *  修改人脸
     * @param userId
     * @param base64Img
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(faceUpdateUser)
    Observable<GeneralData> faceUpdateUser(@Field("userId") int userId,@Field("base64Img") String base64Img, @Field("token") String token);


    /**
     *  题目列表-随机抽取
     * @param userId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(questionList)
    Observable<QuestionListData> getQuestionList(@Field("userId") int userId,  @Field("token") String token);


    /**
     * 答题保存-错题加入错题集，加入积分
     * @param userId
     * @param itemIds          题目ids,以逗号拼接
     * @param answerOptions    选项 以逗号拼接 如A,B,A,D,C
     * @param answerJudge      结果，以逗号拼接，如 1,0,1,1,0  (0：错误，1：正确。)
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(answerSave)
    Observable<IntegralData> saveAnswer(@Field("userId") int userId, @Field("itemIds") String itemIds ,
                                        @Field("answerOptions")String answerOptions, @Field("answerJudge") String answerJudge,
                                        @Field("token") String token);


    /**
     * 发布党建微视
     * @param partList
     * @return
     */
    @Multipart
    @POST(addMicro)
    Observable<GeneralData> uploadMicro( @Part List<MultipartBody.Part> partList);


    /**
     *  背景音乐
     * @param pageNo
     * @param pageSize
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(getBgmListWithPage)
    Observable<BackgroundMusicData> getBackgroundMusic(@Field("pageNo") int pageNo,@Field("pageSize") int pageSize, @Field("token") String token);


    /**
     *  答题链接
     * @param //type    0:测试环境，1:生产环境
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(getUrl)
    Observable<AnswerUrlData> getAnswerUrl(/*@Field("type") int type,*/ @Field("token") String token);

    /**
     *  答题闯关
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(getCgUrl)
    Observable<AnswerUrlData> getAnswerCgUrl(@Field("token") String token);

    /**
     *  朗读厅-增加朗读次数
     * @param readId
     * @param userId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(addRead)
    Observable<GeneralData> addRead(@Field("readId") int readId,@Field("userId") int userId, @Field("token") String token);



}











