package com.typartybuilding.activity.second.interactive;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.typartybuilding.R;
import com.typartybuilding.activity.myRelatedActivity.AnswerBreakthroughActivity;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.answer.AnswerUrlData;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

@Route(path = SmartAnswerFra.PATH)
public class SmartAnswerFra extends BaseFra {
    public static final String PATH="/act/h5_game";
    private String TAG = "SmartAnswerActivity";


    @BindView(R.id.webView)
    WebView webView;
    @Autowired
    int type ;

    @Override
    public void initData() {


    }



    //界面可见时再加载数据
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //请求网络数据
            if (type==1){
                getAnswerKnowledgeUrl();
            }else if (type==2){
                getAnswerQuestion();
            }
        }else {
            if (webView!=null){
                setWebView("about:blank");
            }

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (webView != null){
            webView.destroy();
        }
    }

    //    private long mExitTime;
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        //判断用户是否点击了“返回键”
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //与上次点击返回键时刻作差
//            if ((System.currentTimeMillis() - mExitTime) > 2000) {
//                //大于2000ms则认为是误操作，使用Toast进行提示
//                Toast.makeText(this, "再按一次退出答题页面", Toast.LENGTH_SHORT).show();
//                //并记录下本次点击“返回键”的时刻，以便下次进行判断
//                mExitTime = System.currentTimeMillis();
//            } else {
//                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
//                //System.exit(0);
//                finish();
//            }
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }


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
                    if (webView!=null){
                        webView.scrollTo(0, 0);
                    }
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

    private void initView(AnswerUrlData urlData){
        if (urlData != null){
            setWebView(urlData.data.url);
            //setWebView("https://www.baidu.com/");
        }
    }

    /**
     *  获取题目列表
     */
    private void getAnswerKnowledgeUrl(){

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


    /**
     *  答题闯关-获取题目列表
     */
    private void getAnswerQuestion(){

        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getAnswerCgUrl(token)
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


//    @OnClick(R.id.textView_back)
//    public void onClickBack(){
//        /*if ((System.currentTimeMillis() - mExitTime) > 2000) {
//            //大于2000ms则认为是误操作，使用Toast进行提示
//            Toast.makeText(this, "再按一次退出答题页面", Toast.LENGTH_SHORT).show();
//            //并记录下本次点击“返回键”的时刻，以便下次进行判断
//            mExitTime = System.currentTimeMillis();
//        } else {
//            //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
//            //System.exit(0);
//            finish();
//        }*/
//        finish();
//    }


    @Override
    public int getLayoutId() {
        return R.layout.layout_fra_smart_answer_new;
    }
}
