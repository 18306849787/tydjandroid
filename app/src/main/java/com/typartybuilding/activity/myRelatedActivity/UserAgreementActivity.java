package com.typartybuilding.activity.myRelatedActivity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.LaunchActivity;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.TyUrlData;
import com.typartybuilding.retrofit.GeneralRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.view.WhiteTitleLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class UserAgreementActivity extends WhiteTitleBaseActivity {


    @BindView(R.id.textView_title)
    TextView textTitle;

    @BindView(R.id.webView)
    WebView webView;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        ButterKnife.bind(this);
        textTitle.setText("注册协议");
        getUrl();
        //setWebView();
    }

    private void setWebView(){
        //String url = getResources().getString(R.string.user_agreement_url);
        if (url != null ) {

            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            //settings.setTextZoom(100);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    //webView.scrollTo(0, 0);

                }
            });
            webView.loadUrl(url);

        }else {
            Toast.makeText(this,"链接不可用",Toast.LENGTH_SHORT).show();
        }
    }


    private void getUrl(){
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
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
                            url = tyUrlData.data.gvrp_url;
                            setWebView();

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(tyUrlData.message);

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(UserAgreementActivity.this,tyUrlData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
