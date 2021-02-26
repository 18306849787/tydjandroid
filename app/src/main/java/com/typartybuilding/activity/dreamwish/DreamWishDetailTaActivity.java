package com.typartybuilding.activity.dreamwish;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.dreamwish.DreamWishData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.JshareUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DreamWishDetailTaActivity extends WhiteTitleBaseActivity {

    private String TAG = "DreamWishDetailActivity";

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.webview_dream_wish)
    WebView webView;

    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    private DreamWishData.WishData wishData ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_wish_detail_ta);
        ButterKnife.bind(this);

        wishData = MyApplication.wishData;
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.wishData = null;
        if (webView != null){
            webView.destroy();
        }
    }

    private void initView(){
        if (wishData != null){
            setWebView();
        }
    }

    private void setWebView(){
        String url = wishData.aspirationDetailUrl;
        Log.i(TAG, "setWebView: url : " + wishData.aspirationDetailUrl);
        if (url != null ) {
            webView.getSettings().setJavaScriptEnabled(true);
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
                    //心愿浏览量更新
                    wishBrowse();
                }
            });
            webView.loadUrl(url);
        }else {
            Toast.makeText(this,"链接不可用",Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.imagebutton_back)
    public void onClickBack(){
        finish();
    }

    //分享
    @OnClick(R.id.imagebutton_share)
    public void onClickShare(){
        if (wishData == null){
            return;
        }
        if (userType == 1 || userType == 2) {
            if (wishData != null) {
                JshareUtil.showBroadView(this, wishData.aspirationTitle, "", wishData.aspirationDetailUrl,
                        3,wishData.aspirationId);
            }
        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    /**
     * 心愿浏览量更新
     */
    private void wishBrowse(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.wishBrowse(wishData.aspirationId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        int code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: 更新浏览量code : " + code);
                        if (code == 0){

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(DreamWishDetailTaActivity.this,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
