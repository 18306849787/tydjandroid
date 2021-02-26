package com.typartybuilding.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.NewsDetailAcAdapter;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.RecommendData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class NewsDetailActivity extends RedTitleBaseActivity {

    private String TAG = "NewsDetailActivity";

    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.textView)
    TextView textView;         //可能想看的文章

    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;    //标题，浏览量，发布日期的布局
    @BindView(R.id.textView_headline)
    TextView textHeadLine;             //标题
    @BindView(R.id.textView_pageviews)
    TextView textPageViews;                //浏览量
    @BindView(R.id.textView_date)
    TextView textDate;                 //发布日期

    //新增，  引标题  和  副标题  ， 标题栏为 新闻标题
    @BindView(R.id.textView_citation)
    TextView textCitation;            //引标题
    @BindView(R.id.textView_subhead)
    TextView textSubhead;             //副标题
    @BindView(R.id.textView_title)
    TextView textTitle;

    @BindView(R.id.webview_news_detail)
    WebView webView;
    @BindView(R.id.recyclerView_news_detail)
    RecyclerView recyclerView;
    @BindView(R.id.textView_collect)
    TextView textCollect;              //收藏
    @BindView(R.id.textView_like)
    TextView textLike;                 //点赞


    private Handler handler = new Handler();

    private List<ArticleBanner> bannerList = new ArrayList<>();
    private NewsDetailAcAdapter adapter;
    private ArticleBanner articleBanner ;     //新闻信息类

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType,-1);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        //获取新闻类
        //Intent intent = getIntent();
        //articleBanner = (ArticleBanner)intent.getSerializableExtra("ArticleBanner");
        articleBanner = MyApplication.articleBanner;
        Log.i(TAG, "onCreate: ArticleBanner : " + articleBanner);

        initView();
        setWebView();

        initRecyclerView();
        //滚动到顶部
        scrollView.scrollTo(0,0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.articleBanner = null;
        if (webView != null){
            webView.destroy();
        }
    }

    //延时获取推荐数据
    private void delayGetRecommendData(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取推荐数据
                getRecommendData();
            }
        },2000);
    }

    private void initView(){
        if (articleBanner != null) {
            //设置标题栏
            //textTitle.setText(articleBanner.articleTitle);

            textLike.setText("(" + articleBanner.praisedNum + ")");
            textCollect.setText("(" + articleBanner.collectedNum + ")");
            Log.i(TAG, "initView: articleBanner.collectedNum ： " + articleBanner.collectedNum);
            Log.i(TAG, "initView: articleBanner.isCollect : " + articleBanner.isCollect);
            Log.i(TAG, "initView: articleBanner.isPraise : " + articleBanner.isPraise);
            if (articleBanner.isCollect == 1) {
                textCollect.setSelected(true);
            }else {
                textCollect.setSelected(false);
            }
            if (articleBanner.isPraise == 1) {
                textLike.setSelected(true);
            }else {
                textLike.setSelected(false);
            }

           /* //包含该内容，表示是后台添加的富文本，需显示标题，浏览量，日期
            if (articleBanner.articleDetailUrl != null && articleBanner.articleDetailUrl.contains("page/html/artdetail")){
                linearLayout.setVisibility(View.VISIBLE);
                textHeadLine.setText(articleBanner.articleTitle);
                textPageViews.setText(articleBanner.browseTimes + "");
                textDate.setText(Utils.formatDate(articleBanner.publishDate));
                //判断是否 有引标题和副标题
                if (articleBanner.quotationTitle != null && articleBanner.quotationTitle != ""){
                    textCitation.setVisibility(View.VISIBLE);
                    textCitation.setText("【" + articleBanner.quotationTitle + "】");
                }
                if (articleBanner.subtitle != null && articleBanner.subtitle != ""){
                    textSubhead.setVisibility(View.VISIBLE);
                    textSubhead.setText("--" + articleBanner.subtitle);
                }
                //浏览量加 1
                articleBanner.browseTimes += 1;
            }else {
                linearLayout.setVisibility(View.GONE);
            }*/

        }
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsDetailAcAdapter(bannerList,this);
        recyclerView.setAdapter(adapter);
    }


    private void setWebView(){
        String url = null;
        if (articleBanner != null) {
            url = articleBanner.articleDetailUrl;
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
                    textView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    webView.scrollTo(0, 0);

                    //延时获取推荐数据
                    delayGetRecommendData();

                    //添加浏览历史记录，更新浏览量
                    RetrofitUtil.addBrowsing(1,articleBanner.articleId);
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


    //收藏 和 点赞
    @OnClick({R.id.textView_collect, R.id.textView_like})
    public void onClickTextView(View view){
        switch (view.getId()){
            case R.id.textView_collect:
                if (userType == 3){
                    MyApplication.remindVisitor(this);
                }else {
                    if (textCollect.isSelected()) {
                        if (articleBanner != null) {
                            textCollect.setSelected(false);
                            String strNum = textCollect.getText().toString();
                            String strNum1 = strNum.replace("(", "");
                            String strNum2 = strNum1.replace(")", "");
                            int count = Integer.parseInt(strNum2) - 1;
                            textCollect.setText("(" + count + ")");
                            //取消收藏
                            RetrofitUtil.delCollect(articleBanner.articleId);
                            articleBanner.isCollect = 0;
                            articleBanner.collectedNum += -1;
                        }

                    } else {
                        if (articleBanner != null) {
                            textCollect.setSelected(true);
                            String strNum = textCollect.getText().toString();
                            String strNum1 = strNum.replace("(", "");
                            String strNum2 = strNum1.replace(")", "");
                            int count = Integer.parseInt(strNum2) + 1;
                            textCollect.setText("(" + count + ")");
                            //收藏
                            RetrofitUtil.addCollect(articleBanner.articleId);
                            articleBanner.isCollect = 1;
                            articleBanner.collectedNum += 1;
                        }
                    }
                }
                break;
            case R.id.textView_like:
                if (userType == 3) {
                    MyApplication.remindVisitor(this);
                }else {
                    if (textLike.isSelected()) {
                        if (articleBanner != null) {
                            textLike.setSelected(false);
                            String strNum = textLike.getText().toString();
                            String strNum1 = strNum.replace("(", "");
                            String strNum2 = strNum1.replace(")", "");
                            int count = Integer.parseInt(strNum2) - 1;
                            textLike.setText("(" + count + ")");
                            //取消点赞
                            RetrofitUtil.delLike(articleBanner.articleId);
                            articleBanner.isPraise = 0;
                            articleBanner.praisedNum += -1;
                        }
                    } else {
                        if (articleBanner != null) {
                            textLike.setSelected(true);
                            String strNum = textLike.getText().toString();
                            String strNum1 = strNum.replace("(", "");
                            String strNum2 = strNum1.replace(")", "");
                            int count = Integer.parseInt(strNum2) + 1;
                            textLike.setText("(" + count + ")");
                            //点赞
                            RetrofitUtil.addLike(articleBanner.articleId);
                            articleBanner.isPraise = 1;
                            articleBanner.praisedNum += 1;
                        }
                    }
                }
                break;
        }
    }

    //分享
    @OnClick(R.id.imagebutton_share)
    public void onClickShare(){
        if (userType == 1 || userType == 2) {
            if (articleBanner != null) {
                JshareUtil.showBroadView(this, articleBanner.articleTitle, articleBanner.articleProfile, articleBanner.articleDetailUrl,
                        1, articleBanner.articleId);
            }

        }else if (userType == 3){
            MyApplication.remindVisitor(this);
        }
    }

    private void getRecommendData(){
        if (articleBanner == null){
            return;
        }
        int articleType = articleBanner.articleType;
        int articleId = articleBanner.articleId;
        int urlType = Integer.valueOf(articleBanner.urlType);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getRecommendData(articleType, articleId,urlType,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RecommendData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RecommendData recommendData) {
                        int code = Integer.valueOf(recommendData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            loadData(recommendData);
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(recommendData.message);
                        }else if (code == 10){
                            RetrofitUtil.tokenLose(NewsDetailActivity.this,recommendData.message);
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

    private void loadData(RecommendData recommendData){
        int size = recommendData.data.length;
        Log.i(TAG, "loadData: size : " + size);
        for (int i = 0; i < size; i++){
            bannerList.add(recommendData.data[i]);
        }
        adapter.notifyDataSetChanged();

    }


}
