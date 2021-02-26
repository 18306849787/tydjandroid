package com.typartybuilding.adapter;

import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;

import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.utils.DisplayUtils;
import com.typartybuilding.utils.ImageOpUtils;
import com.typartybuilding.utils.Utils;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-13
 * @describe
 */
public class RecommentAdapter extends BaseMultiItemQuickAdapter<ArticleBanner, BaseViewHolder> {

    public RecommentAdapter() {
        super(null);
        addItemType(1000, R.layout.layout_item_recomment_pic_0);
        addItemType(1001, R.layout.layout_item_recomment_pic_1);
        addItemType(1003, R.layout.layout_item_recomment_pic_3);
        addItemType(2, R.layout.layout_item_recomment_video);
    }

    @Override
    protected void convert(BaseViewHolder helper, ArticleBanner item) {
        switch (item.getItemType()) {
            case 1000:
                helper.setText(R.id.recommend_item_title, item.articleTitle);
                if (!TextUtils.isEmpty(item.articleSource)) {
                    helper.setText(R.id.recommend_item_time, item.articleSource + "  " + DateFormat.format("yyyy.MM.dd", item.publishDate));
                } else {
                    helper.setText(R.id.recommend_item_time, DateFormat.format("yyyy.MM.dd", item.publishDate));
                }
                break;
            case 1001:
                helper.setText(R.id.recommend_item_title, item.articleTitle);
                if (!TextUtils.isEmpty(item.articleSource)) {
                    helper.setText(R.id.recommend_item_time, item.articleSource + "  " + DateFormat.format("yyyy.MM.dd", item.publishDate));
                } else {
                    helper.setText(R.id.recommend_item_time, DateFormat.format("yyyy.MM.dd", item.publishDate));
                }
                Glide.with(mContext).load(item.getPicUrls().get(0))
                        .into((ImageView) helper.getView(R.id.recommend_item_img));
                break;
            case 1003:
                helper.setText(R.id.recommend_item_title, item.articleTitle);
                if (!TextUtils.isEmpty(item.articleSource)) {
                    helper.setText(R.id.recommend_item_time, item.articleSource + "  " + DateFormat.format("yyyy.MM.dd", item.publishDate));
                } else {
                    helper.setText(R.id.recommend_item_time, DateFormat.format("yyyy.MM.dd", item.publishDate));
                }
                Glide.with(mContext).load(item.getPicUrls().get(0))
                        .into((ImageView) helper.getView(R.id.recommend_item_imgview0));
                Glide.with(mContext).load(item.getPicUrls().get(1))
                        .into((ImageView) helper.getView(R.id.recommend_item_imgview1));
                Glide.with(mContext).load(item.getPicUrls().get(2))
                        .into((ImageView) helper.getView(R.id.recommend_item_imgview2));
                break;
            case 2:
                helper.setText(R.id.recomment_video_title, item.articleTitle);
                if (!TextUtils.isEmpty(item.articleSource)) {
                    helper.setText(R.id.recomment_video_from, item.articleSource + "  " + DateFormat.format("yyyy.MM.dd", item.publishDate));
                } else {
                    helper.setText(R.id.recomment_video_from, DateFormat.format("yyyy.MM.dd", item.publishDate));
                }
                SuperTextView superTextView = helper.getView(R.id.recomment_video_time_stv);
                superTextView.setCenterString(Utils.formatTime(item.videoDuration));
                Glide.with(mContext).load(item.videoCover)
                        .apply(ImageOpUtils.getYuanRequestOptions(8))
                        .into((ImageView) helper.getView(R.id.recomment_video_img));
                break;
        }
    }
}
