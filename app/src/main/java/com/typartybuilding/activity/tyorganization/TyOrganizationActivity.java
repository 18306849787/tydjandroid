package com.typartybuilding.activity.tyorganization;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.tyorganization.TyOrganizationData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TyOrganizationActivity extends WhiteTitleBaseActivity {

    private String TAG = "TyOrganizationActivity";

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    /*@BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;    //标题，浏览量，发布日期的布局
    @BindView(R.id.textView_headline)
    TextView textHeadLine;               //标题
    @BindView(R.id.textView_pageviews)
    TextView textPageViews;                //浏览量
    @BindView(R.id.textView_date)
    TextView textDate;                 //发布日期*/

    @BindView(R.id.webView)
    WebView webView;

    private TyOrganizationData.OrganizationData organizationData ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ty_organization);
        ButterKnife.bind(this);
        //获取上一个activity 传递过来的 数据
        organizationData = MyApplication.organizationData;
        //initView();
        setWebView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.organizationData = null;
    }

    @OnClick(R.id.imageView_back)
    public void onClickBack(){
        finish();
    }

    private void initView(){
        if (organizationData != null){
            /*textHeadLine.setText(organizationData.gwTitle);
            textPageViews.setText(organizationData.browseTimes + "");
            textDate.setText(Utils.formatDate(organizationData.publishDate));*/
        }
    }

    private void setWebView(){
        String url = null;
        if (organizationData != null) {
            url = organizationData.gwDetailUrl;
        }
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
                    progressBar.setVisibility(View.VISIBLE);

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);

                    webView.scrollTo(0, 0);

                    //添加浏览历史记录，更新浏览量
                    if (organizationData != null) {
                        addBrowseTimes(organizationData.gwId);
                    }
                }
            });
            webView.loadUrl(url);
        }else {
            Toast.makeText(this,"链接不可用",Toast.LENGTH_SHORT).show();
        }


    }

    private void addBrowseTimes(int gwId){
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.addBrowseTimes( gwId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        int code = Integer.valueOf(generalData.code);
                        if (code == 0){
                            if (organizationData != null){
                                //浏览量加 1
                                organizationData.browseTimes += 1;
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(MyApplication.getContext(),generalData.message);

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
