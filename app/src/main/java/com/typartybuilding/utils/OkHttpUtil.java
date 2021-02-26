package com.typartybuilding.utils;

import android.util.Log;

import com.typartybuilding.base.MyApplication;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *  测试用，因 retrofit + rxjava 无法获取后台反的json字符串；
 *  所以 用okhttp 来获取
 */
public class OkHttpUtil {

    private static String TAG = "OkHttpUtil";

    public static void post(String base64Img){

        Log.i(TAG, "post: 进入post请求");

        String url = "http://47.97.126.122:8080/app/user/faceUpdateUser";
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Log.i(TAG, "post: userId : " + userId);
        Log.i(TAG, "post: token : " + token);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userId",userId+"")
                .add("base64Img",base64Img)
                .add("token",token).build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody).build();
        client.newCall(request).enqueue(new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.i(TAG, "onResponse: " + responseText);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: e : " + e);
            }
        });
    }


    //基层党建 网络请求测试
    public static void postBpb(){

        Log.i(TAG, "post: 进入post请求");

        String url = "http://47.97.126.122:8080/app/article/getArticleListWithPage";
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Log.i(TAG, "post: userId : " + userId);
        Log.i(TAG, "post: token : " + token);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userId",userId+"")
                .add("pageNo",1 + "")
                .add("pageSize",20+"")
                .add("articleType",3+"")
                .add("urlType",1 + "")
                .add("organName","杏花岭区")
                .add("token",token).build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody).build();
        client.newCall(request).enqueue(new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.i(TAG, "onResponse: " + responseText);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: e : " + e);
            }
        });
    }

}
