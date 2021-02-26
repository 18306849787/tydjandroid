package com.typartybuilding.activity.second.interactive;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.activity.myRelatedActivity.SmartAnswerActivityNew;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.answer.AnswerUrlData;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-06
 * @describe 知识问答
 */
public class KnowledgeAnswerFra extends BaseFra {
    private String TAG = "SmartAnswerActivity";

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.webView)
    WebView webView;

    @Override
    public void initData() {
        getAnswerUrl();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_act_xrxy;
    }


    /**
     *  获取题目列表
     */
    private void getAnswerUrl(){

        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getAnswerUrl(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AnswerUrlData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AnswerUrlData urlData) {
                        int code = Integer.valueOf(urlData.code);
                        Log.i(TAG, "onNext: 题目列表code : " + code);
                        if (code == 0){
                            initView(urlData);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(urlData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(getActivity(),urlData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e : " + e );
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void initView(AnswerUrlData urlData){
        if (urlData != null){
            setWebView(urlData.data.url);
            //setWebView("https://www.baidu.com/");
        }
    }


    private void setWebView(String url){

        Log.i(TAG, "setWebView: url : " + url);
        if (url != null ) {
            WebSettings settings = webView.getSettings();

            settings.setJavaScriptEnabled(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);

            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    //progressBar.setVisibility(View.VISIBLE);

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    //progressBar.setVisibility(View.GONE);
                    webView.scrollTo(0, 0);

                }

               /* @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                    //Android8.0以下的需要返回true 并且需要loadUrl；8.0之后效果相反
                    if(Build.VERSION.SDK_INT<26) {
                        view.loadUrl(url);
                        return true;
                    }
                    return false;
                }*/

            });
            webView.loadUrl(url);
        }else {
            Toast.makeText(getActivity(),"链接不可用",Toast.LENGTH_SHORT).show();
        }

    }


}
