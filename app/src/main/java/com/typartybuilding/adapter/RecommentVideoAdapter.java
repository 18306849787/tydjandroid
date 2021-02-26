package com.typartybuilding.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.ImageView;

import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.utils.ImageOpUtils;
import com.typartybuilding.utils.Utils;

import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-20
 * @describe
 */
public class RecommentVideoAdapter extends BaseQuickAdapter<ArticleBanner, BaseViewHolder> {
    public RecommentVideoAdapter() {
        super(R.layout.layout_item_recomment_video_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ArticleBanner item) {
        helper.setText(R.id.recomment_video_title, item.articleTitle);
        SuperTextView superTextView = helper.getView(R.id.recomment_video_time_stv);
        superTextView.setCenterString(Utils.formatTime(item.videoDuration));
        Glide.with(mContext).load(item.videoCover)
                .apply(ImageOpUtils.getYuanRequestOptions(2))
                .into((ImageView) helper.getView(R.id.recomment_video_img));
    }
}
