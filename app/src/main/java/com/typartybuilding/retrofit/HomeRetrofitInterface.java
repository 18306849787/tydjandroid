package com.typartybuilding.retrofit;

import com.typartybuilding.gsondata.ArticleVideoData;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.GeneralMixtureData;
import com.typartybuilding.gsondata.basicparty.BasicPartyData;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;
import com.typartybuilding.gsondata.choiceness.ChoicenessNewData;
import com.typartybuilding.gsondata.choiceness.CurrentNewsData;
import com.typartybuilding.gsondata.choiceness.MoreHotVideoData;
import com.typartybuilding.gsondata.choiceness.RecommendData;
import com.typartybuilding.gsondata.dreamwish.DreamWishData;
import com.typartybuilding.gsondata.dreamwish.GoodPeopleData;
import com.typartybuilding.gsondata.learntime.EducationFilmData;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.gsondata.pbmap.AgenciesData;
import com.typartybuilding.gsondata.pbmap.RegionNameData;
import com.typartybuilding.gsondata.pbmicrovideo.FindFascinatingData;
import com.typartybuilding.gsondata.pbmicrovideo.HotWordData;
import com.typartybuilding.gsondata.pbmicrovideo.PopularityListData;
import com.typartybuilding.gsondata.tyorganization.TyOrganizationData;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 首页 retrofit网络请求接口
 */
public interface HomeRetrofitInterface {

    //精选页面
    String choiceness = "app/article/selectedArticle";           //精选页面 ,轮播图，热门视频，微视
    String articleNews = "app/article/getArticleNewsWithPage";   //时事新闻 ，更多新闻
    String moreHotVideo = "app/article/getHotVideoListWithPage"; //更多热门视频
    //精选页面 修改后
    String selectedList = "app/article/selectedList";            //首页精选列表（新增）

    //学习时刻
    String articleVideo = "app/article/getArticleListWithPage";   //文章视频分类查询,
    String recommendList = "app/article/recommendList";  //推荐接口
    //新增需求
    String getPartyEduList = "app/article/getPartyEduList";  //党员教育片列表--按子标签分类

    //党建微视
    String hotWord = "app/article/hotWord";                           //党建热搜 热词
    String microRankingList = "app/micro/microRankingList";           //党建微视-人气榜单
    String microMarvellousList = "app/micro/microMarvellousList";     //党建微视-发现精彩
    String search = "app/article/search";                             //党建热搜，资讯文章和微视

    //圆梦心愿
    String wish = "app/consulting/wish";                             //心愿 数据
    String nubHelpProcessor = "app/consulting/nubHelpProcessor";     //助力捐赠
    String wishBrowse = "app/consulting/wishBrowse";                 //心愿浏览量更新
    //新增需求
    String claimAspiration = "app/consulting/claimAspiration";      //心愿认领
    String aspirationTopList = "app/consulting/aspirationTopList";  //好人榜
    String myAspirationList = "app/consulting/myAspirationList";    //我的/他的心愿
    String uploadCredential = "app/consulting/uploadCredential";    //验收申请-上传凭据

    //太原组工
    String getGroupListWithPage = "app/groupWork/getGroupListWithPage";   //太原组工分页查询
    String browseTimes = "app/groupWork/browseTimes";                     //太原组工更新浏览量

    //基层党建 ， 新增
    String getPartyBuildingList = "app/article/getPartyBuildingList";  //党建基层列表--按党组织分类

    //党建地图
    String agencies = "app/regional/agencies";                       //服务机构
    String tyregional = "app/regional/tyregional";                   //太原区域查询, 已弃用
    String getOrganList = "app/regional/getOrganList";       //组织机构选择列表


    /**
     *  首页 精选页面  轮播图，热门视频，微视
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(choiceness)
    Observable<ChoicenessData> getChoicenessData(@Field("token") String token);

    /**
     *  精选页面  时事新闻和更多新闻
     * @param userId
     * @param pageNo     分页-页码。不传则默认1
     * @param pageSize   分页-每页条数。不传则默认10
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(articleNews)
    Observable<CurrentNewsData> getCurrentNews(@Field("userId") int userId, @Field("pageNo") int pageNo,
                                                 @Field("pageSize") int pageSize, @Field("token") String token);

    /**
     *  更多热门视频
     * @param pageNo
     * @param pageSize
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(moreHotVideo)
    Observable<MoreHotVideoData> getMoreHotVideo(@Field("pageNo") int pageNo,
                                                @Field("pageSize") int pageSize, @Field("token") String token);


    /**
     *  推荐
     * @param articleType    1:时事新闻，3：党建基层，5：学习领袖，6：党员教育片，7：时代先锋，8：直播
     * @param articleId      咨询文章ID
     * @param urlType        1：图片，2：视频
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(recommendList)
    Observable<RecommendData> getRecommendData(@Field("articleType") int articleType, @Field("articleId") int articleId,
                                               @Field("urlType") int urlType, @Field("token") String token);


    /**
     *  文章视频分类查询
     * @param userId
     * @param pageNo
     * @param pageSize
     * @param articleType     3：党建基层，5：学习领袖，6：党员教育片，7：时代先锋，8：直播
     * @param urlType          1：图片，2：视频
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(articleVideo)
    Observable<ArticleVideoData> getArticleVideo(@Field("userId") int userId, @Field("pageNo") int pageNo,
                                                 @Field("pageSize") int pageSize, @Field("articleType") int articleType,
                                                 @Field("urlType") Integer urlType,@Field("token") String token);

    //接口同上，怎么字段 organName， 用于基层党建 的机构选择
    @FormUrlEncoded
    @POST(articleVideo)
    Observable<ArticleVideoData> getArticleVideo2(@Field("userId") int userId, @Field("pageNo") int pageNo,
                                                 @Field("pageSize") int pageSize, @Field("articleType") int articleType,
                                                 @Field("urlType") Integer urlType,@Field("token") String token, @Field("organName") String organName);


    //接口同上， 用于 ， 党员教育片  更多
    @FormUrlEncoded
    @POST(articleVideo)
    Observable<ArticleVideoData> getArticleVideo3(@Field("userId") int userId, @Field("pageNo") int pageNo,
                                                  @Field("pageSize") int pageSize, @Field("articleType") int articleType,
                                                  @Field("urlType") Integer urlType,@Field("token") String token,
                                                  @Field("articleLabel") int articleLabel);


    /**
     *  党建热搜
     * @param pageNo
     * @param pageSize
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(hotWord)
    Observable<HotWordData> getHotWordData(@Field("pageNo") int pageNo, @Field("pageSize") int pageSize, @Field("token") String token);


    /**
     *  党建热搜，资讯文章和微视； 搜索也是这个接口
     * @param keyword   关键词
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(search)
    Observable<GeneralMixtureData> getSearchData(@Field("keyword") String keyword, @Field("token") String token,
                                                 @Field("pageNo") int pageNo, @Field("pageSize") int pageSize);


    /**
     *  党建微视-人气榜单
     * @param userId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(microRankingList)
    Observable<PopularityListData> getPopularityListData( @Field("userId") int userId,@Field("token") String token);



    /**
     *  党建微视-发现精彩
     * @param token
     * @param pageNo
     * @param pageSize
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST(microMarvellousList)
    Observable<FindFascinatingData> getFindFascinatingData( @Field("token") String token, @Field("pageNo") int pageNo,
                                                   @Field("pageSize") int pageSize,@Field("userId") int userId);

    /**
     *  用于，列表播放微视频
     * @param token
     * @param pageNo
     * @param pageSize
     * @param userId
     * @param visionId       查询滑动列表时，从当前id开始查询（不包括当前ID）
     * @param visionType     1：图片，2：视频，3：音频
     * @return
     */
    @FormUrlEncoded
    @POST(microMarvellousList)
    Observable<FindFascinatingData> getFindFascinatingData2( @Field("token") String token, @Field("pageNo") int pageNo,
                                                            @Field("pageSize") int pageSize,@Field("userId") int userId,
                                                             @Field("visionId") int visionId, @Field("visionType") int visionType);



    /**
     *  心愿 数据
     * @param pageNo
     * @param pageSize
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(wish)
    Observable<DreamWishData> getWishData(@Field("pageNo") int pageNo, @Field("pageSize") int pageSize, @Field("token") String token);


    /**
     *  助力捐赠
     * @param aspirationId    主键ID
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(nubHelpProcessor)
    Observable<GeneralData> helpToDonate(@Field("aspirationId") int aspirationId, @Field("token") String token);

    /**
     *  心愿浏览量更新
     * @param aspirationId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(wishBrowse)
    Observable<GeneralData> wishBrowse(@Field("aspirationId") int aspirationId, @Field("token") String token);

    /**
     *  心愿认领
     * @param aspirationId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(claimAspiration)
    Observable<GeneralData> claimDreamWish(@Field("aspirationId") int aspirationId, @Field("token") String token);

    /**
     *  好人榜
     * @param pageNo
     * @param pageSize
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(aspirationTopList)
    Observable<GoodPeopleData> getGoodPeopleData(@Field("pageNo") int pageNo, @Field("pageSize") int pageSize, @Field("token") String token);

    /**
     * 我的/他的心愿
     * @param pageNo
     * @param pageSize
     * @param userId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(myAspirationList)
    Observable<DreamWishData> getMyOrTaWishData(@Field("pageNo") int pageNo, @Field("pageSize") int pageSize,
                                                @Field("userId") int userId, @Field("token") String token);

    /**
     * 验收申请-上传凭据, 完成心愿后，上传
     * @param partList
     * @return
     */
    @Multipart
    @POST(uploadCredential)
    Observable<GeneralData> uploadPic(@Part List<MultipartBody.Part> partList);



    /**
     *
     * @param longitude      当前位置经度
     * @param latitude       当前位置维度
     * @param orgType        1：党组织机构，2：党群服务中心，3：教育基地
     * @param regionName     区域名称
     * @param pageNo
     * @param pageSize
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(agencies)
    Observable<AgenciesData> getAgenciesData(@Field("longitude") double longitude,@Field("latitude") double latitude,
                                             @Field("orgType") int orgType, @Field("regionName") String regionName,
                                             @Field("pageNo") int pageNo,@Field("pageSize") int pageSize,
                                             @Field("token") String token);

    /**
     * 太原区域查询
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(getOrganList)
    Observable<RegionNameData> getRegionName(@Field("token") String token);


    /**
     *  太原组工分页查询
     * @param pageNo
     * @param pageSize
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(getGroupListWithPage)
    Observable<TyOrganizationData> getTyOrganizationData(@Field("pageNo") int pageNo, @Field("pageSize") int pageSize, @Field("token") String token);


    /**
     * 太原组工更新浏览量
     * @param gwId
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(browseTimes)
    Observable<GeneralData> addBrowseTimes(@Field("gwId") int gwId, @Field("token") String token);


    //新增需求

    /**
     *  党员教育片列表--按子标签分类
     * @param articleType  6
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(getPartyEduList)
    Observable<EducationFilmData> getEducationFilmData(@Field("articleType") int articleType, @Field("token") String token);


    /**
     *  党建基层列表--按党组织分类
     * @param articleType
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(getPartyBuildingList)
    Observable<BasicPartyData> getBasicPartyData(@Field("articleType") int articleType, @Field("token") String token);


    /**
     *  首页精选列表（新增）
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(selectedList)
    Observable<ChoicenessNewData> getChoicenessNewData(@Field("pageNo") int pageNo, @Field("pageSize") int pageSize,@Field("token") String token);



}
