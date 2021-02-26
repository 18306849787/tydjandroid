package com.typartybuilding.activity.second;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseListAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.ArticleVideoDataNew;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.loader.LearnLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.DisplayUtils;
import com.typartybuilding.utils.ImageOpUtils;
import com.typartybuilding.utils.Utils;

import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-19
 * @describe
 */
@Route(path = VideoMoreAct.PATH)
public class VideoMoreAct extends BaseListAct {
    public static final String PATH = "/act/video_more";


    VideoMoreAdapter videoMoreAdapter;
    LearnLoader learnLoader;

    @Autowired
    String title;
    @Autowired
    int typeId;
    @Autowired
    int articleLabel;

    @Override
    public void initData() {
        super.initData();
        mTitleTv.setText(title);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount) {
                    getArticleVideoData(pageNo, typeId);
                } else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
        refreshLayout.setBackground(getDrawable(R.color.background_color));
        learnLoader = new LearnLoader();
        recyclerView.setPadding(DisplayUtils.dip2px(16), 0, DisplayUtils.dip2px(16), 0);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.right = DisplayUtils.dip2px(8);
                outRect.left = DisplayUtils.dip2px(8);
                if (parent.getChildLayoutPosition(view) % 2 == 0) {
                    outRect.left = 0;
                } else if (parent.getChildLayoutPosition(view) % 2 == 1) {
                    outRect.right = 0;
                }
            }
        });
        videoMoreAdapter = new VideoMoreAdapter();
        recyclerView.setAdapter(videoMoreAdapter);
        videoMoreAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBanner data = (ArticleBanner) adapter.getData().get(position);
                ARouter.getInstance().build(PlayVideoAct.PTAH)
                        .withString(PlayVideoAct.URL, data.videoUrl)
                        .withInt(PlayVideoAct.ARTICLE_TYPE, data.articleType)
                        .withInt(PlayVideoAct.ARTICLE_ID, data.articleId)
                        .withInt(PlayVideoAct.URL_TYPE, 2)
                        .navigation();
            }
        });
    }

    @Override
    public void loadingData() {
        super.loadingData();
        getArticleVideoData(pageNo, typeId);
    }


    public void getArticleVideoData(int page, int type) {
        learnLoader.getArticleVideoData(page, 20, type,articleLabel).subscribe(

                new RequestCallback<ArticleVideoDataNew.Data>() {
                    @Override
                    public void onSuccess(ArticleVideoDataNew.Data articleVideoData) {
                        pageCount = articleVideoData.pageCount;
                        if (page ==1){
                            videoMoreAdapter.setNewData(articleVideoData.rows);
                            refreshLayout.finishRefresh();
                        } else {
                            videoMoreAdapter.addData(articleVideoData.rows);
                            refreshLayout.finishLoadMore();
                        }
                        pageNo++;
                    }

                    @Override
                    public void onFail(Throwable e) {
                        RetrofitUtil.requestError();
                    }
                }
        );
    }

    public class VideoMoreAdapter extends BaseQuickAdapter<ArticleBanner, BaseViewHolder> {

        public VideoMoreAdapter() {
            super(R.layout.layout_adpter_item_new_idea);
        }

        @Override
        protected void convert(BaseViewHolder helper, ArticleBanner item) {
            helper.setText(R.id.item_new_idea_title, item.articleTitle);
            SuperTextView superTextView = helper.getView(R.id.xrxy_fra_times);
            superTextView.setVisibility(View.VISIBLE);
            superTextView.setCenterString(Utils.formatTime(item.videoDuration));
            Glide.with(mContext).load(item.videoCover)
                    .apply(ImageOpUtils.getYuanRequestOptions(2))
                    .into((ImageView) helper.getView(R.id.item_new_idea_img));
        }
    }
}
