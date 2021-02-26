package com.typartybuilding.activity.second.mine;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.PlayAudioMixActivityNew;
import com.typartybuilding.activity.PlayPictureMixActivity;
import com.typartybuilding.activity.myRelatedActivity.MyCollectActivity;
import com.typartybuilding.activity.second.PlayVideoAct;
import com.typartybuilding.activity.second.TextDetailAct;
import com.typartybuilding.activity.second.homepager.DynamicAct;
import com.typartybuilding.adapter.MycollectAdapter;
import com.typartybuilding.base.BaseAct;
import com.typartybuilding.base.BaseListAct;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.douyin.MicroVideoPlayActivity;
import com.typartybuilding.gsondata.GeneralMixtureData;
import com.typartybuilding.gsondata.MixtureData;
import com.typartybuilding.loader.HomePagerLoader;
import com.typartybuilding.retrofit.PersonalRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-12-02
 * @describe
 */
@Route(path = MycollectAct.PATH)
public class MycollectAct extends BaseListAct {
    public static final String PATH = "/act/my_collect";

    private int pageNo = 1;
    private int pageSize = 20;
    private MycollectAdapter mMycollectAdapter;
    private PopupWindow popupWindow;  //弹窗  ，提醒用户 是否要 取消收藏
    private View popView;             //弹窗布局
    private Button btnConfirm;       //弹窗确认按钮
    private Button btnCancel;        //弹窗取消按钮

    @Override
    public void initData() {
        super.initData();
        mTitleTv.setText("我的收藏");
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo=1;
                getCollectData();
            }
        });
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount) {
                    getCollectData();
                } else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
        mMycollectAdapter = new MycollectAdapter();
        recyclerView.setAdapter(mMycollectAdapter);
        mMycollectAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MixtureData mixtureData = (MixtureData) adapter.getData().get(position);
                if (mixtureData.targetType==2){
                    skipMicVideo(mixtureData);
                }else if (mixtureData.targetType==3){
                    skipTextDetail(mixtureData);
                }else if(mixtureData.targetType==1){
                    skipTextDetail(mixtureData);
                }
            }
        });

        mMycollectAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                MixtureData mixtureData = (MixtureData) adapter.getData().get(position);
                delArticle(position,mixtureData);
                return false;
            }
        });

        getCollectData();
        initPopupWindow();
    }


    public void skipTextDetail(MixtureData articleBanner){
        if (articleBanner.urlType ==2){
            if (articleBanner.targetType==3){
                ARouter.getInstance().build(TextDetailAct.PATH)
                        .withString(TextDetailAct.URL, articleBanner.articleDetailUrl)
                        .withInt(TextDetailAct.ARTICLE_TYPE, 1)
                        .withInt(TextDetailAct.ARTICLE_ID, articleBanner.targetId)
                        .withString(TextDetailAct.GW_QUOTATION_TITLE, articleBanner.title)
                        .withString("shareType", "dynamic")
                        .withInt("detailType",3)
                        .navigation();
            }else {
                ARouter.getInstance().build(PlayVideoAct.PTAH)
                        .withString(PlayVideoAct.URL, articleBanner.fileUrl)
                        .withInt(PlayVideoAct.ARTICLE_TYPE, articleBanner.articleType)
                        .withInt(PlayVideoAct.ARTICLE_ID, articleBanner.targetId)
                        .withInt(PlayVideoAct.URL_TYPE, 2)
                        .navigation();
            }

        }else {
            if (articleBanner.targetType==3){
                ARouter.getInstance().build(TextDetailAct.PATH)
                        .withString(TextDetailAct.URL, articleBanner.articleDetailUrl)
                        .withInt(TextDetailAct.ARTICLE_TYPE, 1)
                        .withInt(TextDetailAct.ARTICLE_ID, articleBanner.targetId)
                        .withString(TextDetailAct.GW_QUOTATION_TITLE, articleBanner.title)
                        .withString("shareType", "dynamic")
                        .withInt("detailType",3)
                        .navigation();
            }else {
                ARouter.getInstance().build(TextDetailAct.PATH)
                        .withString(TextDetailAct.URL, articleBanner.articleDetailUrl)
                        .withInt(TextDetailAct.ARTICLE_TYPE, 1)
                        .withInt(TextDetailAct.ARTICLE_ID, articleBanner.targetId)
                        .withInt(TextDetailAct.URL_TYPE, 1)
                        .navigation();
            }
        }
    }
    /**
     * 跳转到 微视频 播放页面
     * @param
     */
    private void skipMicVideo(MixtureData mixtureData){
        //"urlType": 2,  1：图片，2：视频，3：音频
        if (mixtureData.urlType == 1){
            Intent intentAc = new Intent(this, PlayPictureMixActivity.class);
            //intentAc.putExtra("MixtureData", mixtureData);
            MyApplication.mixtureData = mixtureData;
            this.startActivity(intentAc);
        }else if (mixtureData.urlType == 2) {
           /* Intent intentAc = new Intent(mContext, PlayMicroVideoMixActivity.class);
            MyApplication.mixtureData = mixtureData;*/
            Intent intentAc = new Intent(this, MicroVideoPlayActivity.class);
            MyApplication.microVideo = Utils.mixtureDataToMicroVideo(mixtureData);
            this.startActivity(intentAc);
        }else if (mixtureData.urlType == 3){
            Intent intentAc = new Intent(this, PlayAudioMixActivityNew.class);
            //intentAc.putExtra("MixtureData", mixtureData);
            MyApplication.mixtureData = mixtureData;
            this.startActivity(intentAc);
        }

    }

    private void getCollectData() {
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId, -1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token, "");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        PersonalRetrofitInterface personalRetrofit = retrofit.create(PersonalRetrofitInterface.class);
        personalRetrofit.getCollectData(pageNo, pageSize, userId, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralMixtureData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralMixtureData generalMixtureData) {
                        int code = Integer.valueOf(generalMixtureData.code);
                        if (code == 0) {
                            pageCount = generalMixtureData.data.pageCount;
                            addData(generalMixtureData);
                            pageNo++;
//                            initData(generalMixtureData);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                                refreshLayout.finishRefresh();
                            }

                        } else if (code == -1) {
                            RetrofitUtil.errorMsg(generalMixtureData.message);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }
                        } else if (code == 10) {
                            RetrofitUtil.tokenLose(MycollectAct.this, generalMixtureData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        RetrofitUtil.requestError();
                        //关闭上拉加载更多
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    public void addData(GeneralMixtureData generalMixtureData) {
        MixtureData data[] = generalMixtureData.data.rows;
        if (data != null) {
            ArrayList<MixtureData> list = new ArrayList<>(Arrays.asList(data)) ;
            if (pageNo==1){
                mMycollectAdapter.setNewData(list);
            }else {
                mMycollectAdapter.addData(list);
            }

        }
    }


    /**
     *  显示弹窗，删除资讯类 收藏
     * @param position
     * @param
     */
    public void delArticle(int position,MixtureData mixtureData){
        if (!popupWindow.isShowing()){
            popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
            //改变屏幕透明度
            Utils.backgroundAlpha(this,0.7f);
        }
        //确认删除 资讯类 收藏
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mixtureData.targetType==2){
                    RetrofitUtil.cancelPraise(mixtureData.targetId, mixtureData.userId);
                }else if(mixtureData.targetType==3){
                    RetrofitUtil.delCollectNew(mixtureData.targetId,mixtureData.targetType);
                }else {
                    //请求后台删除收藏
                    RetrofitUtil.delCollect(mixtureData.targetId);
                }
                //刷新页面
                mMycollectAdapter.remove(position);
                popupWindow.dismiss();
            }
        });
    }


    private void initPopupWindow(){
        popView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_delete_mic_video, null);
        popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnConfirm = popView.findViewById(R.id.button_comfirm);
        btnCancel = popView.findViewById(R.id.button_cancel);

        TextView textTitle = popView.findViewById(R.id.textView_phonenum);
        TextView text = popView.findViewById(R.id.textView_reminder);
        textTitle.setText("取消收藏");
        text.setText("确认取消收藏吗？");

        popupWindow.setTouchable(true);
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.backgroundAlpha(MycollectAct.this,1f);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

    }
}
