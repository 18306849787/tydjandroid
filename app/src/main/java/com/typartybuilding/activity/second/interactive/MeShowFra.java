package com.typartybuilding.activity.second.interactive;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeAct;
import com.typartybuilding.activity.PlayAudioActivityNew;
import com.typartybuilding.activity.PlayPictureActivity;
import com.typartybuilding.activity.plusRelatedActivity.Camera2Activity;
import com.typartybuilding.activity.second.PlayVideoAct;
import com.typartybuilding.base.BaseFra;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.douyin.MicroVideoPlayActivity;
import com.typartybuilding.gsondata.MicroVideo;
import com.typartybuilding.bean.MeShowBean;
import com.typartybuilding.loader.InteractiveLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.utils.DisplayUtils;
import com.typartybuilding.utils.UserUtils;
import com.typartybuilding.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-07
 * @describe 我行我show
 */
public class MeShowFra extends BaseFra {
    @BindView(R.id.me_show_rcv)
    RecyclerView recyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;

    InteractiveLoader interactiveLoader;
    MeshowAdapter meshowAdapter;
    int pageNo;
    int pageCount;
    @Override
    public void initData() {
         pageNo = 1;
         pageCount = 0;
        //禁止下拉刷新
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                getArticleVideoData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount) {
                    getArticleVideoData();
                } else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });

        interactiveLoader = new InteractiveLoader();

        meshowAdapter = new MeshowAdapter();
        meshowAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MicroVideo microVideo = (MicroVideo) adapter.getData().get(position);
                int visionType = microVideo.visionType;
                if (visionType == 1) {
                    Intent intent = new Intent(getContext(), PlayPictureActivity.class);
                    //intent.putExtra("MicroVideo",microVideo);
                    MyApplication.microVideo = microVideo;
                    getContext().startActivity(intent);
                } else if (visionType == 2) {
                    //Intent intent = new Intent(mContext, PlayMicroVideoActivity.class);
                    Intent intent = new Intent(getContext(), MicroVideoPlayActivity.class);
                    intent.putExtra("flag", 2); //2 表示，微视频 来自个人页面和发现精彩
                    //intent.putExtra("MicroVideo",microVideo);
                    MyApplication.microVideo = microVideo;
                    getContext().startActivity(intent);
                } else if (visionType == 3) {
                    Intent intent = new Intent(getContext(), PlayAudioActivityNew.class);
                    //intent.putExtra("MicroVideo",microVideo);
                    MyApplication.microVideo = microVideo;
                    getContext().startActivity(intent);
//                    ARouter.getInstance().build(PlayVideoAct.PTAH)
//                            .withInt(PlayVideoAct.ARTICLE_ID, microVideo.visionId)
//                            .navigation();
                }
            }
        });
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(DisplayUtils.dip2px(8), DisplayUtils.dip2px(8),
                        DisplayUtils.dip2px(8), DisplayUtils.dip2px(8));
            }
        });
        recyclerView.setAdapter(meshowAdapter);
        getArticleVideoData();
    }



    //界面可见时再加载数据
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getArticleVideoData();
        }
    }

    private void getArticleVideoData() {
        if (interactiveLoader==null){
            return;
        }
        interactiveLoader.getMeShow(pageNo, 20).subscribe(new RequestCallback<MeShowBean>() {
            @Override
            public void onSuccess(MeShowBean meShowBean) {
                if (pageNo == 1) {
                    refreshLayout.finishRefresh();
                    meshowAdapter.setNewData(meShowBean.getRows());
                } else {
                    meshowAdapter.addData(meShowBean.getRows());
                }

                pageCount = meShowBean.getPageCount();
                if (pageNo < pageCount ) {
                    pageNo++;
                }
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onFail(Throwable e) {
                refreshLayout.finishLoadMore();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_item_me_show;
    }

    public class MeshowAdapter extends BaseQuickAdapter<MicroVideo, BaseViewHolder> {

        public MeshowAdapter() {
            super(R.layout.layout_fra_me_show);
        }

        @Override
        protected void convert(BaseViewHolder helper, MicroVideo item) {

            ImageView imageView = helper.getView(R.id.me_show_item_iv);
//            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
//            if (helper.getAdapterPosition() == 0) {
//                params.height = DisplayUtils.dip2px(199);
//            } else {
//                params.height = DisplayUtils.dip2px(149);
//            }
//            imageView.setLayoutParams(params);
            ImageView headImg = helper.getView(R.id.me_show_item_img);
            if (item.visionType == 1){
                Glide.with(mContext).load(item.visionFileUrl)
                        .apply(MyApplication.requestOptions)
                        .into(imageView);
            }else {
                Glide.with(mContext).load(item.videoCover)
                        .apply(MyApplication.requestOptions)
                        .into(imageView);
            }
            Glide.with(mContext).load(item.userHeadImg)
                    .apply(MyApplication.requestOptions)
                    .into(headImg);

            helper.setText(R.id.me_show_item_zan_num,item.visionPraisedNum<99?item.visionPraisedNum+"":"99+");
            helper.setText(R.id.me_show_item_kan_num,item.visionBrowseTimes<99?item.visionBrowseTimes+"":"99+");
            if (!TextUtils.isEmpty(item.visionTitle)){
                helper.setText(R.id.me_show_item_title, item.visionTitle);
                helper.setVisible(R.id.me_show_item_title,true);
            }else {
                helper.setGone(R.id.me_show_item_title,true);
            }


            helper.setText(R.id.me_show_item_name, item.userName);
        }
    }


    @OnClick(R.id.me_show_upload)
    void onUpload(){
        if (UserUtils.getIns().isTourist()){
            MyApplication.remindVisitor(getActivity());
            return;
        }
        Intent intentAc = new Intent(getActivity(), Camera2Activity.class);
        startActivity(intentAc);
    }
}
