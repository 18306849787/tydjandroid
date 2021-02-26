package com.typartybuilding.network.https;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-08-30
 * @describe
 */
public class DemoLoader extends BaseLoader {
    private DemoService mMovieService;

    public DemoLoader() {
        mMovieService = RetrofitManager.getInstance().create(DemoService.class);
    }

    //有data 数据时候，map 转化成bean 对象
    public Observable<DemoBean> getDemo1(String phone){
       return observe(mMovieService.getDemo1(phone)).map(new PreLoad<DemoBean>());
    }

    //返回原始ResponseBody 对象，ResponseBody.string() 获取原始json
    public Observable<ResponseBody> getDemo(String phone){
        return observe(mMovieService.getDemo(phone));
    }

    //无data 数据时候。不要map转化
    public Observable<BaseResponse> getLoginSMS(SmsPostParam smsPostParam){
        return observe(mMovieService.getLoginSMS(smsPostParam));
    }


    interface DemoService {

        @GET("user/phone")
        Observable<BaseResponse<DemoBean>> getDemo1(@Query("phone") String phone);

        @FormUrlEncoded
        @POST("user/phone")
        Observable<BaseResponse<DemoBean>> getDemo2(@Field("phone") String phone);

        @FormUrlEncoded
        @POST("user/phone")
        Observable<ResponseBody> getDemo(@Field("phone") String phone);

        //返回原始json
        @Headers("Content-Type:application/json")
        @POST("user/phone")
        Observable<ResponseBody> getSMS1(@Body SmsPostParam phone);

        //没data的数据，直接返回BaseResponse
        @Headers("Content-Type:application/json")
        @POST("user/phone")
        Observable<BaseResponse> getLoginSMS(@Body SmsPostParam phone);
    }
}
