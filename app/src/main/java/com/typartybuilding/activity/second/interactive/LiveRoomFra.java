package com.typartybuilding.activity.second.interactive;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.tools.ToastManage;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseListFra;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.bean.LiveDetailsBean;
import com.typartybuilding.bean.LiveRoomBean;
import com.typartybuilding.loader.InteractiveLoader;
import com.typartybuilding.loader.LearnLoader;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.network.https.StateException;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.DisplayUtils;
import com.typartybuilding.utils.MultiClickUtil;
import com.typartybuilding.utils.UserUtils;

import java.sql.Ref;
import java.util.List;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-19
 * @describe
 */
@Route(path = LiveRoomFra.PATH)
public class LiveRoomFra extends BaseListFra {
    public static final String PATH = "/fra/live_room";
    private InteractiveLoader interactiveLoader;
    private LiveRoomAdapter liveRoomAdapter;

    @Autowired
    int type;
    @Autowired
    String title;

    public int pageNo = 1;
    public int pageCount;

    @Override
    public void initData() {
        super.initData();
        pageNo = 1;
        pageCount= 0;
        mCommonTitleRl.setVisibility(View.GONE);
        interactiveLoader = new InteractiveLoader();
        mTitleTv.setText(title);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                requestLiveRoom(type, pageNo);
            }
        });
        refreshLayout.setBackground(getActivity().getDrawable(R.color.background_color));
        interactiveLoader = new InteractiveLoader();
        recyclerView.setPadding(DisplayUtils.dip2px(16), 0, DisplayUtils.dip2px(16), 0);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
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
        liveRoomAdapter = new LiveRoomAdapter(type);
        recyclerView.setAdapter(liveRoomAdapter);
        liveRoomAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (UserUtils.getIns().isTourist()) {
                    MyApplication.remindVisitor(getActivity());
                    return;
                }
                LiveRoomBean.RowsBean rowsBean = (LiveRoomBean.RowsBean) adapter.getData().get(position);
                if (MultiClickUtil.isNormalClick()) {
                    interactiveLoader.getLiveDetail(rowsBean.getThemeId()).subscribe(new RequestCallback<LiveDetailsBean>() {
                        @Override
                        public void onSuccess(LiveDetailsBean liveDetailsBean) {
                            ARouter.getInstance().build(LiveRoomAct.PATH)
                                    .withSerializable("liveDetailsBean", liveDetailsBean)
                                    .navigation();
                        }

                        @Override
                        public void onFail(Throwable e) {
                            if (e instanceof StateException) {
                                StateException stateException = (StateException) e;
                                Toast.makeText(getContext(), stateException.msg, Toast.LENGTH_SHORT).show();
                            } else {
                                RetrofitUtil.requestError();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void loadingData() {
        super.loadingData();
        requestLiveRoom(type, pageNo);
    }

    public void requestLiveRoom(int type, final int pageNum) {
        interactiveLoader.getLiveList(type, pageNum, 20).subscribe(new RequestCallback<LiveRoomBean>() {
            @Override
            public void onSuccess(LiveRoomBean liveRoomBean) {
                pageCount = liveRoomBean.getPageCount();
                if (pageNum == 1) {
                    liveRoomAdapter.setNewData(liveRoomBean.getRows());
                    refreshLayout.finishRefresh();
                } else {
                    if(liveRoomBean.getRows()!=null){
                        liveRoomAdapter.addData(liveRoomBean.getRows());
                    }
                }
                pageNo++;
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onFail(Throwable e) {
                refreshLayout.finishLoadMore();
            }
        });
    }

    private class LiveRoomAdapter extends BaseQuickAdapter<LiveRoomBean.RowsBean, BaseViewHolder> {

        public LiveRoomAdapter(int type) {
            super(R.layout.layout_item_live_room);
        }

        @Override
        protected void convert(BaseViewHolder helper, LiveRoomBean.RowsBean item) {
            if (type==1){
                helper.setText(R.id.live_room_hot_num, item.getHeatNum() + "");
                helper.setText(R.id.live_room_name, item.getCreaterName());
                helper.setVisible(R.id.live_room_name,true);
                helper.setVisible(R.id.live_room_hot_num,true);
            }else {
                helper.setVisible(R.id.live_room_name,false);
                helper.setVisible(R.id.live_room_hot_num,false);
                helper.setVisible(R.id.live_room_person_num,true);
                helper.setText(R.id.live_room_person_num,item.getHeatNum() + "");
            }
            helper.setText(R.id.live_room_content, item.getTitleName());
            Glide.with(mContext).load(item.getLiveCover())
                    .apply(MyApplication.requestOptions)
                    .into((ImageView) helper.getView(R.id.live_room_img));
        }
    }
}
