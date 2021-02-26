package com.typartybuilding.fragment.fgLearningTime;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.HomeFragmentLearningTime;
import com.typartybuilding.gsondata.TyUrlData;
import com.typartybuilding.retrofit.GeneralRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * 学习时刻 远程教育, 嵌套web网页，不做定制开发，对接山西好干部网手机版
 */
public class FragmentDistanceEducation extends BaseFragment {

    private String TAG = "FragmentDistanceEducation";

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.webview_diatance_edu)
    public WebView webView;

    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private String ycjy_url;

    private boolean isDestroy;

    @Override
    protected void initViews(Bundle savedInstanceState) {

        isDestroy = false;
        getUrl();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_learning_time_fragment_distance_education_video;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
    }

    private void setWebView(){
        if (!isDestroy) {
            if (ycjy_url != null ) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        if (progressBar != null) {
                            //progressBar.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (progressBar != null) {
                            //progressBar.setVisibility(View.GONE);
                        }

                    }
                });
                webView.loadUrl(ycjy_url);
            } else {
                Toast.makeText(getActivity(), "链接不可用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getUrl(){

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        GeneralRetrofitInterface generalRetrofitInterface = retrofit.create(GeneralRetrofitInterface.class);
        generalRetrofitInterface.getUrl(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TyUrlData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TyUrlData tyUrlData) {
                        int code = Integer.valueOf(tyUrlData.code);
                        if (code == 0){
                            ycjy_url = tyUrlData.data.ycjy_url;
                            setWebView();
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(tyUrlData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyApplication.getContext(),tyUrlData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
