package com.typartybuilding.activity.dreamwish;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
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
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DreamWishDetailActivity extends WhiteTitleBaseActivity {

    private String TAG = "DreamWishDetailActivity";

    @BindView(R.id.imagebutton_share)
    ImageButton btnShare;              //分享按钮

    @BindView(R.id.textView_headline)
    TextView textHeadLine;             //标题
    @BindView(R.id.textView_pageviews)
    TextView textPageViews;                //浏览量
    @BindView(R.id.textView_date)
    TextView textDate;                 //发布日期

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.webview_dream_wish)
    WebView webView;
    @BindView(R.id.imagebutton_love)
    ImageView btnLove;       //捐赠按钮

    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);

    private DreamWishData.WishData wishData ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_wish_detail);
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
            //"aspirationStatus":0 //0：筹集中，1：已集齐，2：过期
            //“isDonation”:1 //0：未捐赠，1：已捐赠
            Log.i(TAG, "initView: wishData.aspirationStatus : " + wishData.aspirationStatus);
            if (wishData.isDonation == 0){
                btnLove.setImageDrawable(getResources().getDrawable(R.mipmap.ymxy_btn_juanzengaixin));
            } else if (wishData.isDonation == 1){
                btnLove.setImageDrawable(getResources().getDrawable(R.mipmap.ymxy_btn_yijuanzeng));
                //按钮，不可点击
                //btnLove.setEnabled(false);
            }
            //是否已经集齐,集齐，可认领
            if (wishData.aspirationStatus == 1){
                btnLove.setImageDrawable(getResources().getDrawable(R.mipmap.ymxy_btn_rl));
                //btnLove.setEnabled(false);
            }

           /* textHeadLine.setText(wishData.aspirationTitle);
            textPageViews.setText(wishData.aspirationBrowseTimes + "");
            textDate.setText(Utils.formatDate(wishData.aspirationPublishDate));*/
            setWebView();
        }else {
            Log.i(TAG, "initView: wishData : " + wishData);
        }
    }

    private void setWebView(){
        String url = wishData.aspirationDetailUrl;
        Log.i(TAG, "setWebView: url : " + wishData.aspirationDetailUrl);
        if (url != null ) {
            //webView.getSettings().setJavaScriptEnabled(true);
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
                    //心愿浏览量更新
                    wishBrowse();
                    //浏览量加 1
                    wishData.aspirationBrowseTimes += 1;

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

    //捐赠按钮
    @OnClick(R.id.imagebutton_love)
    public void onClickLove(){
        if (wishData == null){
            return;
        }
        //已集齐，点击认领
        if (wishData.aspirationStatus == 1){
            if (userType == 2) {
                claimDreamWish(wishData.aspirationId);
            }else {
                Toast.makeText(this,"非认证党员不可认领",Toast.LENGTH_SHORT).show();
            }
        }else {
            if (userType == 1 || userType == 2) {
                helpToDonate();
            } else if (userType == 3) {
                MyApplication.remindVisitor(this);
            }
        }

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
     * 助力捐赠
     */
    private void helpToDonate(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.helpToDonate(wishData.aspirationId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        int code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            //btnLove.setSelected(true);
                            btnLove.setImageDrawable(getResources().getDrawable(R.mipmap.ymxy_btn_yijuanzeng));
                            //wishData.aspirationBrowseTimes += 1;
                            wishData.isDonation = 1;
                            wishData.heartNum = wishData.heartNum + 1;
                            if (wishData.heartNum == wishData.needHeartNum){
                                wishData.aspirationStatus = 1;
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(DreamWishDetailActivity.this,generalData.message);
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
                            RetrofitUtil.tokenLose(DreamWishDetailActivity.this,generalData.message);
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

    //点击 认领
    private void claimDreamWish(int aspirationId){

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.claimDreamWish(aspirationId, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        int code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0) {
                            //刷新数据,更改状态，认领后的心愿，不在显示
                            wishData.aspirationStatus = 3;
                            Toast.makeText(MyApplication.getContext(),"心愿认领成功",Toast.LENGTH_SHORT).show();

                        } else if (code == -1) {
                            RetrofitUtil.errorMsg(generalData.message);
                        } else if (code == 10) {
                            RetrofitUtil.tokenLose(DreamWishDetailActivity.this, generalData.message);
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
