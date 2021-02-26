package com.typartybuilding.adapter;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.ImageView;

import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.typartybuilding.R;
import com.typartybuilding.bean.HomeDynamicBean;
import com.typartybuilding.gsondata.MixtureData;
import com.typartybuilding.utils.ImageOpUtils;
import com.typartybuilding.utils.Utils;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-13
 * @describe
 */
public class MycollectAdapter extends BaseMultiItemQuickAdapter<MixtureData, BaseViewHolder> {

    public MycollectAdapter() {
        super(null);
        addItemType(1000, R.layout.layout_item_recomment_pic_0);
        addItemType(1001, R.layout.layout_item_recomment_pic_1);
        addItemType(1003, R.layout.layout_item_recomment_pic_3);//文章
        addItemType(1100, R.layout.layout_item_recomment_video);//视频
        addItemType(1200, R.layout.layout_item_collect_weishi);//微视
    }

    @Override
    protected void convert(BaseViewHolder helper, MixtureData item) {
        switch (item.getItemType()) {
            case 1000:
                helper.setText(R.id.recommend_item_title, item.targetTitle);
                if (!TextUtils.isEmpty(item.articleSource)) {
                    helper.setText(R.id.recommend_item_time, item.articleSource + "  " + DateFormat.format("yyyy.MM.dd", item.publishDate));
                } else {
                    helper.setText(R.id.recommend_item_time, DateFormat.format("yyyy.MM.dd", item.publishDate));
                }
                break;
            case 1001:
                helper.setText(R.id.recommend_item_title, item.targetTitle);
                if (!TextUtils.isEmpty(item.articleSource)) {
                    helper.setText(R.id.recommend_item_time, item.articleSource + "  " + DateFormat.format("yyyy.MM.dd", item.publishDate));
                } else {
                    helper.setText(R.id.recommend_item_time, DateFormat.format("yyyy.MM.dd", item.publishDate));
                }
                Glide.with(mContext).load(item.getPicUrlsList().get(0))
                        .into((ImageView) helper.getView(R.id.recommend_item_img));
                break;
            case 1003:
                helper.setText(R.id.recommend_item_title, item.targetTitle);
                if (!TextUtils.isEmpty(item.articleSource)) {
                    helper.setText(R.id.recommend_item_time, item.articleSource + "  " + DateFormat.format("yyyy.MM.dd", item.publishDate));
                } else {
                    helper.setText(R.id.recommend_item_time, DateFormat.format("yyyy.MM.dd", item.publishDate));
                }
                Glide.with(mContext).load(item.getPicUrlsList().get(0))
                        .into((ImageView) helper.getView(R.id.recommend_item_imgview0));
                Glide.with(mContext).load(item.getPicUrlsList().get(1))
                        .into((ImageView) helper.getView(R.id.recommend_item_imgview1));
                Glide.with(mContext).load(item.getPicUrlsList().get(2))
                        .into((ImageView) helper.getView(R.id.recommend_item_imgview2));
                break;
            case 1100:
                helper.setText(R.id.recomment_video_title, item.targetTitle);
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
            case 1200:
                Glide.with(mContext).load(item.fileUrl)
                        .into((ImageView) helper.getView(R.id.weishi_img));

                Glide.with(mContext).load(item.userHeadImg)
                        .into((ImageView) helper.getView(R.id.head_view));
                helper.setText(R.id.weishi_name, item.userName);
                helper.setText(R.id.weishi_date, Utils.formatDate(item.publishDate));
                if (TextUtils.isEmpty(item.targetTitle)) {
                    helper.setGone(R.id.weishi_title, true);
                } else {
                    helper.setText(R.id.weishi_title, item.targetTitle);
                    helper.setVisible(R.id.weishi_title, true);
                }
                helper.setText(R.id.weishi_zan, "点赞：" + item.praisedNum);
                break;

        }
    }
}