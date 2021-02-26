package com.typartybuilding.loader;


import com.typartybuilding.bean.LiveDetailsBean;
import com.typartybuilding.bean.LiveRoomBean;
import com.typartybuilding.bean.MeShowBean;
import com.typartybuilding.network.https.BaseLoader;
import com.typartybuilding.network.https.BaseResponse;
import com.typartybuilding.network.https.PreLoad;
import com.typartybuilding.network.https.RetrofitManager;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-06
 * @describe
 */
public class InteractiveLoader extends BaseLoader {

    InteractiveService interactiveService;
    public InteractiveLoader(){
        interactiveService = RetrofitManager.getInstance().create(InteractiveService.class);
    }


    public Observable<ResponseBody> getBookList(String pageNo){

        return  observe(interactiveService.getBookList(pageNo));
    }


    public Observable<MeShowBean> getMeShow(int pageNo,int pageSize){
        return  observe(interactiveService.getMeShow(pageNo,pageSize)).map(new PreLoad<>());
    }

    public Observable<LiveRoomBean> getLiveList(int liveType, int pageNo, int pageSize){
        return  observe(interactiveService.getLiveList(liveType,pageNo,pageSize)).map(new PreLoad<>());
    }

    public Observable<LiveDetailsBean> getLiveDetail(int themeId){
        return  observe(interactiveService.getLiveDetail(themeId)).map(new PreLoad<>());
    }

    public Observable<BaseResponse> delAudio(String readId){
        return  observe(interactiveService.delAudio(readId));
    }

    interface InteractiveService{
        @FormUrlEncoded
        @POST("app/read/queryByPage")
        Observable<ResponseBody> getBookList(@Field("pageNo") String pageNo);

        @FormUrlEncoded
        @POST("app/micro/microMarvellousList")
        Observable<BaseResponse<MeShowBean>> getMeShow(@Field("pageNo") int pageNo,
                                                       @Field("pageSize") int pageSize);


        @FormUrlEncoded
        @POST("app/live/list")
        Observable<BaseResponse<LiveRoomBean>> getLiveList(@Field("liveType")int liveType,@Field("pageNo") int pageNo,
                                                       @Field("pageSize") int pageSize);

        @FormUrlEncoded
        @POST("app/live/detail")
        Observable<BaseResponse<LiveDetailsBean>> getLiveDetail(@Field("themeId")int themeId);


        @FormUrlEncoded
        @POST("app/micro/delAudio")
        Observable<BaseResponse> delAudio(@Field("readId")String readId);

    }

}
