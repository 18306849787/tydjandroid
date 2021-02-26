package com.typartybuilding.activity.second;

import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.immersionbar.ImmersionBar;
import com.typartybuilding.R;
import com.typartybuilding.adapter.RecommentAdapter;
import com.typartybuilding.base.BaseAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.GroupWorkDetailsBean;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.RecommendData;
import com.typartybuilding.loader.MineLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.JshareUtil;
import com.typartybuilding.utils.UserUtils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-14
 * @describe
 */
@Route(path = TextDetailAct.PATH)
public class TextDetailAct extends BaseAct {
    public final static String PATH = "/act/text_detail";

    public final static String URL = "url";
    public final static String ARTICLE_TYPE = "articleType";
    public final static String ARTICLE_ID = "articleId";
    public final static String URL_TYPE = "urlType";
    public final static String BROWSE_TIMES = "browseTimes";
    public final static String PUBLISH_DATE = "publishDate";
    public final static String GW_QUOTATION_TITLE = "gwQuotationTitle";
    public final static String IS_HIDE_COLLECT ="is_hide_collect";
    public final static String IS_HIDE_RECOMENT ="is_hide_recomment";
    public final static String IS_HIDE_SHARE ="is_hide_share";

    @BindView(R.id.text_detail_back)
    ImageView mBackIv;
    @BindView(R.id.views_num)
    TextView mViewsNum;
    @BindView(R.id.text_detail_title)
    TextView mTitleTv;
    @BindView(R.id.textView_date)
    TextView mDate;
    @BindView(R.id.webview_news_detail)
    WebView webView;
    @BindView(R.id.recyclerView_news_detail)
    RecyclerView mRecyclerView;
    @BindView(R.id.webview_news_like)
    LinearLayout mLikeLook;
    @BindView(R.id.text_detail_collection)
    ImageButton mSelectCollection;
    @BindView(R.id.text_detail_share)
    ImageView mShareView;

    @Autowired(name = URL)
    public String url;
    @Autowired(name = ARTICLE_TYPE)
    public int articleType;
    @Autowired(name = ARTICLE_ID)
    public int articleId;
    @Autowired(name = URL_TYPE)
    public int urlType;
    @Autowired(name = BROWSE_TIMES)
    public String browsetimes;
    @Autowired(name = PUBLISH_DATE)
    public long publishdate;
    @Autowired(name = GW_QUOTATION_TITLE)
    public String gwTitle;
    @Autowired(name = IS_HIDE_COLLECT)
    public boolean isHideCollect;
    @Autowired(name = IS_HIDE_SHARE)
    public boolean isHideShare;
    @Autowired
    public String shareType;
    @Autowired
    public int detailType;

    private int userType;
    private ArticleBanner banner;
    private MineLoader mMineLoader;
    public void initData() {
        mMineLoader  = new MineLoader();
        userType =  MyApplication.pref.getInt(MyApplication.prefKey10_login_userType, -1);
        ImmersionBar.with(this)
                .transparentStatusBar()
                .fitsSystemWindows(true)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .navigationBarColor(R.color.white)
                .navigationBarDarkIcon(true)
                .init();

//        mViewsNum.setText("浏览量  "+browsetimes);
//        mDate.setText(DateFormat.format("yyyy.MM.dd",publishdate));
//        mTitleTv.setText(gwTitle);
        initWebView();
        if (isHideCollect){
            mShareView.setVisibility(View.GONE);
        }
        //判断是否收藏
        if (isHideCollect){
            mSelectCollection.setSelected(false);
            mSelectCollection.setVisibility(View.GONE);
        }
        if(detailType==3){
            groupWorkDetails(articleId);
        }else {
            getVideoDetails(articleId);
        }

        if (detailType==3||isHideCollect){
            RetrofitUtil.addBrowseTimes(articleId,UserUtils.getIns().getToken());
        }else {
            RetrofitUtil.addBrowsing(1, articleId);
        }
    }


    private void getVideoDetails(int articleId) {
        mMineLoader.getVideoDetails(articleId).subscribe(new RequestCallback<ArticleBanner>() {
            @Override
            public void onSuccess(ArticleBanner articleBanner) {
                banner = articleBanner;
                mSelectCollection.setSelected(banner.isCollect==1);
            }

            @Override
            public void onFail(Throwable e) {
                Log.e("", "");
            }
        });
    }

    private void groupWorkDetails(int articleId) {
        mMineLoader.groupWorkDetails(articleId).subscribe(new RequestCallback<GroupWorkDetailsBean>() {
            @Override
            public void onSuccess(GroupWorkDetailsBean articleBanner) {
                mSelectCollection.setSelected(articleBanner.getIsCollect()==1);
            }

            @Override
            public void onFail(Throwable e) {
                Log.e("", "");
            }
        });
    }

    @OnClick({R.id.text_detail_back, R.id.text_detail_collection})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_detail_back:
                finish();
                break;
            case R.id.text_detail_collection:
                if (userType == 1 || userType == 2) {
                    if (mSelectCollection.isSelected()){
                        mSelectCollection.setSelected(false);
                        if (detailType==3){
                            RetrofitUtil.delCollectNew(articleId,detailType);
                        }else {
                            RetrofitUtil.delCollect(articleId);
                        }

                    }else {
                        mSelectCollection.setSelected(true);
                        if (detailType==3){
                            RetrofitUtil.addCollectNew(articleId,detailType);
                        }else {
                            RetrofitUtil.addCollect(articleId);
                        }
                    }
                } else if (userType == 3) {
                    MyApplication.remindVisitor(this);
                }
                break;
        }
    }


    private void initWebView() {
        if (!TextUtils.isEmpty(url)) {
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
//                progressBar.setVisibility(View.VISIBLE);
//                textView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
//                progressBar.setVisibility(View.GONE);
//                textView.setVisibility(View.VISIBLE);
                    webView.scrollTo(0, 0);
                    getRcomment();
                    //延时获取推荐数据
//                    delayGetRecommendData();

                    //添加浏览历史记录，更新浏览量
//                    RetrofitUtil.addBrowsing(1, articleBanner.articleId);
                }
            });
            webView.loadUrl(url);
        } else {
            Toast.makeText(this, "链接不可用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_act_text_detail;
    }

    public void getRcomment() {
        if (articleType == 0  && urlType == 0) {
            mLikeLook.setVisibility(View.GONE);
            return;
        }
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getRecommendData(articleType, articleId, urlType, UserUtils.getIns().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RecommendData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RecommendData recommendData) {
                        int code = Integer.valueOf(recommendData.code);
                        if (code == 0) {
                            loadData(recommendData);
                        } else if (code == -1) {
                            RetrofitUtil.errorMsg(recommendData.message);
                        } else if (code == 10) {
                            RetrofitUtil.tokenLose(TextDetailAct.this, recommendData.message);
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

    private void loadData(RecommendData recommendData) {
        if (recommendData != null && recommendData.data != null && recommendData.data.length > 0) {
            mLikeLook.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecommentAdapter recommentAdapter = new RecommentAdapter();
        recommentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBanner rowsBean = (ArticleBanner) adapter.getData().get(position);
                ARouter.getInstance().build(TextDetailAct.PATH)
                        .withString(TextDetailAct.URL, rowsBean.articleDetailUrl)
                        .withInt(TextDetailAct.ARTICLE_TYPE, 1)
                        .withInt(TextDetailAct.ARTICLE_ID, rowsBean.articleId)
                        .withInt(TextDetailAct.URL_TYPE, urlType)
                        .navigation(TextDetailAct.this);
            }
        });
        recommentAdapter.setNewData(new ArrayList<>(Arrays.asList(recommendData.data)));
        mRecyclerView.setAdapter(recommentAdapter);
    }


//    /**
//     * 收藏按钮
//     */
//    @OnClick(R.id.text_detail_collection)
//    public void onCollect() {
//        if (userType == 1 || userType == 2) {
//            if (mSelectCollection.isSelected()) {
//                mSelectCollection.setSelected(false);
//                RetrofitUtil.delCollect(articleId);
//            } else {
//                mSelectCollection.setSelected(true);
//                RetrofitUtil.addCollect(articleId);
//            }
//        } else if (userType == 3) {
//            MyApplication.remindVisitor(this);
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.articleBanner = null;
        if (webView != null) {
            webView.destroy();
        }
    }

    @OnClick(R.id.text_detail_share)
    public void onClickShare() {
        if (userType == 3) {
            MyApplication.remindVisitor(this);
        } else {
            if (banner!=null){
                JshareUtil.showBroadView(this, banner.articleTitle, banner.articleProfile, banner.articleDetailUrl,
                        1, banner.articleId);
            }else if("dynamic".equals(shareType)){
                JshareUtil.showBroadView(this, gwTitle, "", url,
                        1, articleId);
            }
        }
    }

}
