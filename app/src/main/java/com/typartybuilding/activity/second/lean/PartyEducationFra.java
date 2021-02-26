package com.typartybuilding.activity.second.lean;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gyf.immersionbar.ImmersionBar;
import com.typartybuilding.R;
import com.typartybuilding.activity.second.PlayVideoAct;
import com.typartybuilding.activity.second.VideoMoreAct;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.base.BaseListAct;
import com.typartybuilding.base.BaseListFra;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.PartyEducationBean;
import com.typartybuilding.loader.LearnLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.utils.DisplayUtils;
import com.typartybuilding.utils.ImageOpUtils;
import com.typartybuilding.utils.Utils;

import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-15
 * @describe 党员教育
 */
public class PartyEducationFra extends BaseListFra {

    PartyEducationAdapter partyEducationAdapter;
    LearnLoader learnLoader;

    @Override
    public void initData() {
        super.initData();
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(false);
        learnLoader = new LearnLoader();
        mCommonTitleRl.setVisibility(View.GONE);
        partyEducationAdapter = new PartyEducationAdapter();
        recyclerView.setAdapter(partyEducationAdapter);
        learnLoader.getPartyEduList(6).subscribe(new RequestCallback<List<PartyEducationBean>>() {
            @Override
            public void onSuccess(List<PartyEducationBean> partyEducationBean) {
                partyEducationAdapter.setNewData(partyEducationBean);
            }

            @Override
            public void onFail(Throwable e) {

            }
        });
    }


    public class PartyEducationAdapter extends BaseMultiItemQuickAdapter<PartyEducationBean, BaseViewHolder> {

        public PartyEducationAdapter() {
            super(null);
            addItemType(0, R.layout.layout_item_party_education_hot);
            addItemType(1, R.layout.layout_item_party_education_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, PartyEducationBean item) {
            switch (item.getItemType()) {
                case 0:
                    List<PartyEducationBean.RowsBean> list = item.getRows();
                    helper.setText(R.id.education_hot_item_title, item.getArticleLabelName());
                    TextView textView = helper.getView(R.id.education_hot_item_title);
//                    TextPaint paint = textView.getPaint();
//                    paint.setFakeBoldText(true);
//                    textView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/SourceHanSansCN-Heavy.otf"));
                    if (list != null && list.size() > 0) {
                        PartyEducationBean.RowsBean rowsBean = list.get(0);
                        helper.setText(R.id.education_hot_item_f_title, rowsBean.getArticleTitle());
                        Glide.with(mContext).load(rowsBean.getVideoCover())
                                .apply(ImageOpUtils.getYuanRequestOptions(0))
                                .into((ImageView) helper.getView(R.id.education_hot_item_img));
                        SuperTextView superTextView = helper.getView(R.id.education_hot_item_time);
                        superTextView.setCenterString(Utils.formatTime(rowsBean.getVideoDuration()));
                        helper.setOnClickListener(R.id.education_hot_item_img, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ARouter.getInstance().build(PlayVideoAct.PTAH)
                                        .withInt(PlayVideoAct.ARTICLE_ID, rowsBean.getArticleId())
                                        .navigation();

                            }
                        });
                    }

                    break;
                case 1:
                    helper.setText(R.id.party_education_item_title, item.getArticleLabelName());
                    TextView partyTextView = helper.getView(R.id.party_education_item_title);
//                    TextPaint partypaint = partyTextView.getPaint();
//                    partypaint.setFakeBoldText(true);
//                    partyTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/SourceHanSansCN-Heavy.otf"));
                    List<PartyEducationBean.RowsBean> list2 = item.getRows();
                    if (list2 != null && list2.size() > 0) {
                        RecyclerView recyclerView = helper.getView(R.id.party_education_item_recyc);
                        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                            @Override
                            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                                super.getItemOffsets(outRect, view, parent, state);
                                outRect.right = DisplayUtils.dip2px(8);
                                outRect.left = DisplayUtils.dip2px(8);
                                if (parent.getChildLayoutPosition(view) %2==0){
                                    outRect.left = 0;
                                }else if (parent.getChildLayoutPosition(view) %2==1){
                                    outRect.right = 0;
                                }
                            }
                        });
                        helper.setOnClickListener(R.id.learn_new_idea_tv_all, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ARouter.getInstance().build(VideoMoreAct.PATH)
                                        .withInt("typeId",list2.get(0).getArticleType())
                                        .withInt("articleLabel",list2.get(0).getArticleLabel())
                                        .withString("title",item.getArticleLabelName()).navigation(mContext);

                            }
                        });
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                        PartyEudcationAdapter partyEudcationAdapter = new PartyEudcationAdapter(item.getRows());
                        recyclerView.setAdapter(partyEudcationAdapter);
                        partyEudcationAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                PartyEducationBean.RowsBean rowsBean = (PartyEducationBean.RowsBean) adapter.getData().get(position);
                                ARouter.getInstance().build(PlayVideoAct.PTAH)
                                        .withInt(PlayVideoAct.ARTICLE_ID, rowsBean.getArticleId())
                                        .navigation();

                            }
                        });
                    }
                    break;
            }
        }
    }


    public class PartyEudcationAdapter extends BaseQuickAdapter<PartyEducationBean.RowsBean,BaseViewHolder>{

        public PartyEudcationAdapter(@Nullable List<PartyEducationBean.RowsBean> data) {
            super(R.layout.layout_item_party_education_item_fenlei,data);
        }

        @Override
        protected void convert(BaseViewHolder helper, PartyEducationBean.RowsBean item) {
            Glide.with(mContext).load(item.getVideoCover())
                    .apply(ImageOpUtils.getYuanRequestOptions(8))
                    .into((ImageView) helper.getView(R.id.party_fenlei_img));
            helper.setText(R.id.party_fenlei_text,item.getArticleTitle());

            SuperTextView superTextView = helper.getView(R.id.party_fenlei_time);
            superTextView.setCenterString(Utils.formatTime(item.getVideoDuration()));
//            helper.setText(R.id.party_fenlei_time,Utils.formatTime((int) item.getPublishDate()));
        }
    }
}
