package com.typartybuilding.network.https;

import android.util.Log;


import com.typartybuilding.base.MyApplication;
import com.typartybuilding.constants.SpConstant;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-08-29
 * @describe
 */
public class HttpCommonInterceptor implements Interceptor {
    private String TAG = "OkHttp";
    private Map<String, String> mHeaderParamsMap = new HashMap<>();

    public HttpCommonInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.d("HttpCommonInterceptor", "add common params");
        Request oldRequest = chain.request();
        String method = oldRequest.method();
        Request.Builder newRequestBuild = null;
        String postBodyString = "";
        // 添加新的参数，添加到url 中
     /* HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()                .newBuilder()
         .scheme(oldRequest.url().scheme())
             .host(oldRequest.url().host());*/

        if ("POST".equals(method)) {
            RequestBody oldBody = oldRequest.body();
            if (oldBody instanceof FormBody) {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                formBodyBuilder.add("token", MyApplication.pref.getString(MyApplication.prefKey8_login_token, ""));
                formBodyBuilder.add("userId", String.valueOf(MyApplication.pref.getInt(MyApplication.pretKey9_login_userId, -1)));

                newRequestBuild = oldRequest.newBuilder();

                RequestBody formBody = formBodyBuilder.build();
                postBodyString = bodyToString(oldRequest.body());
                postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
                newRequestBuild.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString));
            } else if (oldBody instanceof MultipartBody) {
                MultipartBody oldBodyMultipart = (MultipartBody) oldBody;
                List<MultipartBody.Part> oldPartList = oldBodyMultipart.parts();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
//                RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), iCommon.DEVICE_OS);
//                RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), Utils.instance().getAppNameNew());
//                RequestBody requestBody3 = RequestBody.create(MediaType.parse("text/plain"), Utils.instance().getAppVersionName());
//                for (MultipartBody.Part part:oldPartList) {
//                    builder.addPart(part);
//                    postBodyString += (bodyToString(part.body()) + "\n");
//                }
//                postBodyString += (bodyToString(requestBody1) + "\n");
//                postBodyString += (bodyToString(requestBody2) + "\n");
//                postBodyString += (bodyToString(requestBody3) + "\n");
//              builder.addPart(oldBody);  //不能用这个方法，因为不知道oldBody的类型，可能是PartMap过来的，也可能是多个Part过来的，所以需要重新逐个加载进去
//                builder.addPart(requestBody1);
//                builder.addPart(requestBody2);
//                builder.addPart(requestBody3);
                newRequestBuild = oldRequest.newBuilder();
                newRequestBuild.post(builder.build());
                Log.e(TAG, "MultipartBody," + oldRequest.url());
            } else {
//                newRequestBuild = oldRequest.newBuilder();
                HttpUrl.Builder authorizedUrlBuilder = oldRequest.url().newBuilder()
                        .addQueryParameter("token",MyApplication.pref.getString(MyApplication.prefKey8_login_token, ""))
                        .addQueryParameter("userId", String.valueOf(MyApplication.pref.getInt(MyApplication.pretKey9_login_userId, -1)));
                newRequestBuild = oldRequest.newBuilder()
                        .method(oldRequest.method(), oldRequest.body())
                        .url(authorizedUrlBuilder.build());

            }
        } else {
            // 添加新的参数
            HttpUrl.Builder commonParamsUrlBuilder = oldRequest.url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host())
                    .addQueryParameter("token", MyApplication.pref.getString(MyApplication.prefKey8_login_token, ""))
                    .addQueryParameter("userId", String.valueOf(MyApplication.pref.getInt(MyApplication.pretKey9_login_userId, -1)));
            newRequestBuild = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(commonParamsUrlBuilder.build());
        }

//        Request newRequest = newRequestBuild
//                .addHeader("Accept", "application/json")
//                .addHeader("Accept-Language", "zh")
//                .build();

        // 新的请求
//        Request.Builder requestBuilder =  oldRequest.newBuilder();
//        requestBuilder.method(oldRequest.method(),
//                oldRequest.body());
//        mHeaderParamsMap.put("Authorization", SPUtils.getInstance().getString(SpConstant.USER_TOKEN));
        //添加公共参数,添加到header中
        if (mHeaderParamsMap.size() > 0) {
            for (Map.Entry<String, String> params : mHeaderParamsMap.entrySet()) {
                newRequestBuild.header(params.getKey(), params.getValue());
            }
        }

        Request newRequest = newRequestBuild.build();

        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(newRequest);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        int httpStatus = response.code();
        StringBuilder logSB = new StringBuilder();
        logSB.append("-------start:"+method+"|");
        logSB.append(newRequest.toString()+"\n|");
        logSB.append(method.equalsIgnoreCase("POST")?"post参数{"+ postBodyString +"}\n|":"");
        logSB.append("httpCode=" + httpStatus + ";Response:" + content+"\n|");
        logSB.append("----------End:" + duration + "毫秒----------");
        Log.e(TAG,logSB.toString());


        return  response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
//        return chain.proceed(newRequest);
    }

    public static class Builder {
        HttpCommonInterceptor mHttpCommonInterceptor;

        public Builder() {
            mHttpCommonInterceptor = new HttpCommonInterceptor();
        }

        public Builder addHeaderParams(String key, String value) {
            mHttpCommonInterceptor.mHeaderParamsMap.put(key, value);
            return this;
        }

        public Builder addHeaderParams(String key, int value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public Builder addHeaderParams(String key, float value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public Builder addHeaderParams(String key, long value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public Builder addHeaderParams(String key, double value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public HttpCommonInterceptor build() {
            return mHttpCommonInterceptor;
        }
    }


    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}