package com.typartybuilding.fragment.fragmentbottomnavigation;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayVideoDetailActivity;
import com.typartybuilding.activity.second.PlayVideoAct;
import com.typartybuilding.activity.second.TextDetailAct;
import com.typartybuilding.activity.second.VideoMoreAct;
import com.typartybuilding.activity.second.lean.PartyEducationAct;
import com.typartybuilding.activity.second.lean.PartyListenerAct;
import com.typartybuilding.activity.second.lean.PoliciesAct;
import com.typartybuilding.activity.second.lean.XrxyAct;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.ArticleVideoDataNew;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.loader.LearnLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.DisplayUtils;
import com.typartybuilding.utils.GlideImageLoader;
import com.typartybuilding.utils.ImageOpUtils;
import com.typartybuilding.utils.Utils;
import com.typartybuilding.view.HomeViewPager;
import com.typartybuilding.view.WidgetActivityView;
import com.typartybuilding.view.WidgetViewPagerAdapter;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-05
 * @describe
 */
public class LearnFra extends BaseFra {

    @BindView(R.id.home_banner1)
    WidgetActivityView widgetActivityView;

    @BindView(R.id.learn_new_idea_rv)
    RecyclerView mNewIdeaRv;
    @BindView(R.id.learn_good_voice_rv)
    RecyclerView mGoodVoiceRv;
    @BindView(R.id.learn_new_idea_tv)
    TextView mIdeaTv;
    @BindView(R.id.learn_good_voice_tv)
            TextView mVoiceTv;

    List<ArticleBanner> listBanner;
    LearnLoader learnLoader;
    private NewIdeaAdapter newIdeaAdapter;
    private GoodVoiceAdapter goodVoiceAdapter;

    @Override
    public void initData() {
//        mIdeaTv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/SourceHanSansCN-Heavy.otf"));
//        mVoiceTv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/SourceHanSansCN-Heavy.otf"));
        learnLoader = new LearnLoader();
        mNewIdeaRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        newIdeaAdapter = new NewIdeaAdapter();
        newIdeaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBanner data  = (ArticleBanner) adapter.getData().get(position);
                ARouter.getInstance().build(PlayVideoAct.PTAH)
                        .withString(PlayVideoAct.URL, data.videoUrl)
                        .withInt(PlayVideoAct.ARTICLE_TYPE, data.articleType)
                        .withInt(PlayVideoAct.ARTICLE_ID, data.articleId)
                        .withInt(PlayVideoAct.URL_TYPE, 2)
                        .navigation();
            }
        });

        mNewIdeaRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.right = DisplayUtils.dip2px(8);
                outRect.left = DisplayUtils.dip2px(8);
                if (parent.getChildLayoutPosition(view) %2==0){
                    outRect.left = 0;
                }else if (parent.getChildLayoutPosition(view) %2==1){
                    outRect.right = 0;
                }
            }
        });
        mNewIdeaRv.setAdapter(newIdeaAdapter);

        mGoodVoiceRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        goodVoiceAdapter = new GoodVoiceAdapter();
        goodVoiceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBanner data  = (ArticleBanner) adapter.getData().get(position);
                ARouter.getInstance().build(PlayVideoAct.PTAH)
                        .withString(PlayVideoAct.URL, data.videoUrl)
                        .withInt(PlayVideoAct.ARTICLE_TYPE, data.articleType)
                        .withInt(PlayVideoAct.ARTICLE_ID, data.articleId)
                        .withInt(PlayVideoAct.URL_TYPE, 2)
                        .navigation();
            }
        });

        mGoodVoiceRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.right = DisplayUtils.dip2px(8);
                outRect.left = DisplayUtils.dip2px(8);
                if (parent.getChildLayoutPosition(view) %2==0){
                    outRect.left = 0;
                }else if (parent.getChildLayoutPosition(view) %2==1){
                    outRect.right = 0;
                }
            }
        });
        mGoodVoiceRv.setAdapter(goodVoiceAdapter);

        mGoodVoiceRv.setNestedScrollingEnabled(false);
        mNewIdeaRv.setNestedScrollingEnabled(false);
    }

    @Override
    public void loadingData() {
        super.loadingData();
        requestBanner();
        getArticleVideoData(5);
//        getArticleVideoData(9);

    }

    @OnClick({R.id.learn_fra_xxy, R.id.learn_fra_zzfg, R.id.learn_fra_dyjy, R.id.learn_fra_tdy, R.id.learn_new_idea_tv_all, R.id.learn_good_voice_rv_all})
    void Onclick(View view) {
        switch (view.getId()) {
            case R.id.learn_fra_xxy:
                ARouter.getInstance().build(XrxyAct.PATH).navigation(getActivity());
                break;
            case R.id.learn_fra_zzfg:
                ARouter.getInstance().build(PoliciesAct.PATH).navigation(getActivity());
                break;
            case R.id.learn_fra_dyjy:
                ARouter.getInstance().build(PartyEducationAct.PATH).navigation(getActivity());
                break;
            case R.id.learn_fra_tdy:
                ARouter.getInstance().build(PartyListenerAct.PATH).navigation(getActivity());
                break;
            case R.id.learn_new_idea_tv_all:
                ARouter.getInstance().build(VideoMoreAct.PATH)
                        .withString("title","新思想 新征程")
                        .withInt("typeId",5).navigation(getActivity());
                break;
            case R.id.learn_good_voice_rv_all:
                ARouter.getInstance().build(VideoMoreAct.PATH)
                        .withInt("typeId",9)
                        .withString("title","党政好声音").navigation(getActivity());
                break;
        }
    }


    private void requestBanner() {
        learnLoader.getBanner("13",5).subscribe(
                new RequestCallback<ArticleVideoDataNew.Data>() {
                    @Override
                    public void onSuccess(ArticleVideoDataNew.Data data) {
                        if (data != null && data.rows != null) {
                            listBanner = data.rows;
                            if (data.rows!= null
                                    && data.rows.size() > 0) {
                                widgetActivityView.initAdapter(listBanner, false, new WidgetViewPagerAdapter.OnClickListen() {
                                    @Override
                                    public void onClick(int position) {
                                        ArticleBanner data = listBanner.get(position);
                                        if (data.urlType == 2){
                                            ARouter.getInstance().build(PlayVideoAct.PTAH)
                                                    .withInt(PlayVideoAct.ARTICLE_ID, data.articleId)
                                                    .navigation();
                                        }

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFail(Throwable e) {
                        Log.e("", "");
                    }

                }
        );
    }


    public void getArticleVideoData(final int type) {
        learnLoader.getArticleVideoData(1,6,type).subscribe(

                new RequestCallback<ArticleVideoDataNew.Data>() {
                    @Override
                    public void onSuccess(ArticleVideoDataNew.Data articleVideoData) {
                        if (type == 5) {
                            newIdeaAdapter.setNewData(articleVideoData.rows);
                        }else if (type ==9){
                            goodVoiceAdapter.setNewData(articleVideoData.rows);
                        }


                    }

                    @Override
                    public void onFail(Throwable e) {
                        RetrofitUtil.requestError();

                    }
                }
        );
    }


    public class NewIdeaAdapter extends BaseQuickAdapter<ArticleBanner, BaseViewHolder> {

        public NewIdeaAdapter() {
            super(R.layout.layout_adpter_item_new_idea);
        }

        @Override
        protected void convert(BaseViewHolder helper, ArticleBanner item) {
            SuperTextView superTextView = helper.getView(R.id.xrxy_fra_times);
            superTextView.setVisibility(View.VISIBLE);
            superTextView.setCenterString(Utils.formatTime(item.videoDuration));
            helper.setText(R.id.item_new_idea_title, item.articleTitle);
            Glide.with(mContext).load(item.videoCover)
                    .apply(ImageOpUtils.getYuanRequestOptions(8))
                    .into((ImageView) helper.getView(R.id.item_new_idea_img));
        }
    }


    public class GoodVoiceAdapter extends BaseQuickAdapter<ArticleBanner, BaseViewHolder> {

        public GoodVoiceAdapter() {
            super(R.layout.layout_adpter_item_good_voice);
        }

        @Override
        protected void convert(BaseViewHolder helper, ArticleBanner item) {
            SuperTextView superTextView = helper.getView(R.id.item_good_voice_time);
            superTextView.setVisibility(View.VISIBLE);
            superTextView.setCenterString(Utils.formatTime(item.videoDuration));
            helper.setText(R.id.item_good_voice_title, item.articleTitle);
            Glide.with(mContext).load(item.videoCover)
                    .apply(ImageOpUtils.getYuanRequestOptions(8))
                    .into((ImageView) helper.getView(R.id.item_good_voice_img));
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.layout_fra_learn;
    }
}
