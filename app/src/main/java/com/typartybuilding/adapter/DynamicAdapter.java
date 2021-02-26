package com.typartybuilding.adapter;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.ImageView;

import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.typartybuilding.R;
import com.typartybuilding.bean.HomeDynamicBean;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.utils.ImageOpUtils;
import com.typartybuilding.utils.Utils;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-13
 * @describe
 */
public class DynamicAdapter extends BaseMultiItemQuickAdapter<HomeDynamicBean.RowsBean, BaseViewHolder>
        {

    public DynamicAdapter() {
        super(null);
        addItemType(1000, R.layout.layout_item_recomment_pic_0);
        addItemType(1001, R.layout.layout_item_recomment_pic_1);
        addItemType(1003, R.layout.layout_item_recomment_pic_3);
//        addItemType(2, R.layout.layout_item_recomment_video);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeDynamicBean.RowsBean item) {
        switch (item.getItemType()) {
            case 1000:
                helper.setText(R.id.recommend_item_title, item.getGwTitle());
                if (!TextUtils.isEmpty(item.getGwSource())) {
                    helper.setText(R.id.recommend_item_time, item.getGwSource() + "  " + DateFormat.format("yyyy.MM.dd", item.getPublishDate()));
                } else {
                    helper.setText(R.id.recommend_item_time, DateFormat.format("yyyy.MM.dd", item.getPublishDate()));
                }
                break;
            case 1001:
                helper.setText(R.id.recommend_item_title, item.getGwTitle());
                if (!TextUtils.isEmpty(item.getGwSource())) {
                    helper.setText(R.id.recommend_item_time, item.getGwSource() + "  " + DateFormat.format("yyyy.MM.dd", item.getPublishDate()));
                } else {
                    helper.setText(R.id.recommend_item_time, DateFormat.format("yyyy.MM.dd", item.getPublishDate()));
                }
                Glide.with(mContext).load(item.getPicUrlsList().get(0))
                        .into((ImageView) helper.getView(R.id.recommend_item_img));
                break;
            case 1003:
                helper.setText(R.id.recommend_item_title, item.getGwTitle());
                if (!TextUtils.isEmpty(item.getGwSource())) {
                    helper.setText(R.id.recommend_item_time, item.getGwSource() + "  " + DateFormat.format("yyyy.MM.dd", item.getPublishDate()));
                } else {
                    helper.setText(R.id.recommend_item_time, DateFormat.format("yyyy.MM.dd", item.getPublishDate()));
                }
                Glide.with(mContext).load(item.getPicUrlsList().get(0))
                        .into((ImageView) helper.getView(R.id.recommend_item_imgview0));
                Glide.with(mContext).load(item.getPicUrlsList().get(1))
                        .into((ImageView) helper.getView(R.id.recommend_item_imgview1));
                Glide.with(mContext).load(item.getPicUrlsList().get(2))
                        .into((ImageView) helper.getView(R.id.recommend_item_imgview2));
                break;
        }
//        helper.setText(R.id.dyanmic_item_title, item.getGwTitle());
//        if (!TextUtils.isEmpty(item.getGwSource())) {
//            helper.setText(R.id.dyanmic_item_time, item.getGwSource() + "  " + DateFormat.format("yyyy.MM.dd", item.getPublishDate()));
//        } else {
//            helper.setText(R.id.dyanmic_item_time, DateFormat.format("yyyy.MM.dd", item.getPublishDate()));
//        }
//        Glide.with(mContext).load(item.getPicUrls())
//                .apply(ImageOpUtils.getYuanRequestOptions(8))
//                .into((ImageView) helper.getView(R.id.dyanmic_item_img));
    }
}