package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.dreamwish.DreamWishDetailActivity;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.dreamwish.DreamWishData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DreamWishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "DreamWishAdapter";

    private List<DreamWishData.WishData> dataList = new ArrayList<>();
    private Context mContext;

    private static final int TYPE_ITEM_NORMAL = 0;
    private static final int TYPE_ITEM_FOOTER = 1;

    private ViewHolderFooter mHolder;

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType, -1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token, "");

    public int currentPos;    //被点击的当前位置

    private Drawable yjzDrawable;          //已捐赠
    private Drawable wjzDrawable;          //未捐赠
    private Drawable yjqDrawable;          //已集齐
    private Drawable yjqDrawableBg;        //已集齐背景图

    static class ViewHolderFooter extends RecyclerView.ViewHolder {

        @BindView(R.id.item_load_tv)
        TextView textHint;
        @BindView(R.id.item_load_pb)
        ProgressBar progressBar;

        public ViewHolderFooter(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView_headline)
        TextView headLine;                //标题
        @BindView(R.id.textView_subhead)
        TextView subHead;                 //副标题
        @BindView(R.id.imageView)
        ImageView imageView;              //封面图片

        @BindView(R.id.imageView_love_collected)
        ImageView llLoveCollected;    //爱心集齐后的图片

        @BindView(R.id.imageView_jz)
        ImageView imageViewJz;        //捐赠按钮

        @BindView(R.id.seekBar)
        SeekBar seekBar;

        @BindView(R.id.textView_love_collect_num)
        TextView loveNum;                 //爱心的数量

        @BindView(R.id.textView_percent)
        TextView textPercent;             //进度条的百分数


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public DreamWishAdapter(List<DreamWishData.WishData> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
        wjzDrawable = mContext.getResources().getDrawable(R.drawable.ymxy_btn_juanzeng);
        yjzDrawable = mContext.getResources().getDrawable(R.drawable.ymxy_btn_yijuanzeng);
        yjqDrawable = mContext.getResources().getDrawable(R.drawable.ymxy_btn_yijiqi);
        yjqDrawableBg = mContext.getResources().getDrawable(R.mipmap.img_juanzengjieshu);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM_NORMAL) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_dream_wish,
                    viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_load_more_dreamwish,
                    viewGroup, false);

            mHolder = new ViewHolderFooter(view);

            return mHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int type = getItemViewType(i);
        if (type == TYPE_ITEM_NORMAL) {
            ViewHolder holder = (ViewHolder) viewHolder;
            DreamWishData.WishData wishData = dataList.get(i);
            //设置 主副标题
            holder.headLine.setText(wishData.aspirationTitle);
            //截止 5月14日，集齐 1万份 爱心，太原市政府 将奖励 1万元
            String date = Utils.formatMonth(wishData.expirationTime) + "月" +
                    Utils.formatDay(wishData.expirationTime) + "日";
            String str = "截止" + date + "," + "集齐" + wishData.needHeartNum + "份爱心，"
                    + wishData.provideOrgan + "将奖励" + wishData.provideMoney ;
            holder.subHead.setText(str);

            //设置 爱心数量
            holder.loveNum.setText(wishData.heartNum + "");
            //设置进度条
            holder.seekBar.setPadding(0, 0, 0, 0);
            holder.seekBar.setMax(wishData.needHeartNum);
            holder.seekBar.setProgress(wishData.heartNum);

            //Log.i(TAG, "onBindViewHolder: 需要爱心数needHeartNum " + i + ": " + wishData.needHeartNum);
            //Log.i(TAG, "onBindViewHolder: 已集齐爱心数heartNum " + i + ": " + wishData.heartNum);
            //Log.i(TAG, "onBindViewHolder: 是否已捐赠isDonation " + i + ": " + wishData.isDonation);
            //是否已捐赠
            if (wishData.isDonation == 1) {
                holder.imageViewJz.setImageDrawable(yjzDrawable);
            }else {
                holder.imageViewJz.setImageDrawable(wjzDrawable);
            }

            String imgUrl = wishData.aspirationImg;

            //加载图片
            Glide.with(mContext).load(imgUrl)
                    .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                    .into(holder.imageView);

            //已集齐 "aspirationStatus":0 //0：筹集中，1：已集齐，2：过期
            //Log.i(TAG, "onBindViewHolder: 是否已集齐aspirationStatus " + i + ": " + wishData.aspirationStatus);

            if (wishData.aspirationStatus == 1) {

                //设置 “已集齐”的背景图可见
                holder.llLoveCollected.setVisibility(View.VISIBLE);
                //集齐，按钮不可点击
                holder.imageViewJz.setEnabled(false);

                holder.imageViewJz.setImageDrawable(yjqDrawable);

                //设置进度条，全部显示完
                holder.seekBar.setProgress(wishData.needHeartNum);

                //设置显示100%
                holder.textPercent.setText("100%");
            } else {
                //设置 “已集齐”的背景图不可见
                holder.llLoveCollected.setVisibility(View.INVISIBLE);
                //未集齐，按钮可点击
                holder.imageViewJz.setEnabled(true);

                //计算爱心已经 集齐多少的百分比， 10000位集齐总数
                if (wishData.heartNum == 0) {
                    holder.textPercent.setText("0%");
                } else {
                    int percent = (wishData.heartNum * 100) / wishData.needHeartNum;
                    if (percent > 0) {
                        holder.textPercent.setText(percent + "%");
                    } else {
                        percent = 1;
                        holder.textPercent.setText(percent + "%");
                    }
                }


            }
            //设置点击事件
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intentAc = new Intent(mContext, DreamWishDetailActivity.class);
                    //intentAc.putExtra("WishData", wishData);
                    MyApplication.wishData = wishData;
                    mContext.startActivity(intentAc);
                    //记录点击的那条数据
                    currentPos = i;
                }
            });
            holder.imageViewJz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: wishData title : " + wishData.aspirationTitle);
                    if (userType == 3) {
                        MyApplication.remindVisitor((HomeActivity) mContext);
                    } else {
                        //进行捐赠
                        //helpToDonate(wishData.aspirationId,holder.imageViewJz);
                        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
                        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
                        homeRetrofitInterface.helpToDonate(wishData.aspirationId, token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<GeneralData>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(GeneralData generalData) {
                                        int code = Integer.valueOf(generalData.code);
                                        Log.i(TAG, "onNext: code : " + code);
                                        if (code == 0) {
                                            //holder.imageViewJz.setSelected(true);
                                            //holder.imageViewJz.setEnabled(false);
                                            holder.imageViewJz.setImageDrawable(yjzDrawable);
                                            //设置 爱心数量
                                            holder.loveNum.setText(wishData.heartNum + 1 + "");
                                            //设置进度条
                                            holder.seekBar.setProgress(wishData.heartNum + 1);
                                            //设置百分百
                                            int percent = ((wishData.heartNum + 1) * 100) / wishData.needHeartNum;
                                            if (percent > 0) {
                                                holder.textPercent.setText(percent + "%");
                                            } else {
                                                percent = 1;
                                                holder.textPercent.setText(percent + "%");
                                            }
                                            //判断是否已集齐
                                            if (wishData.heartNum + 1 == wishData.needHeartNum) {
                                                //设置 “已集齐”的背景图可见
                                                //holder.llLoveCollected.setVisibility(View.VISIBLE);

                                                holder.imageViewJz.setImageDrawable(yjqDrawable);
                                            }

                                        } else if (code == -1) {
                                            RetrofitUtil.errorMsg(generalData.message);
                                        } else if (code == 10) {
                                            RetrofitUtil.tokenLose(mContext, generalData.message);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "onError: e : " + e);
                                        RetrofitUtil.requestError();
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }

                }
            });
        }
    }


    public void setTypeItemFooter() {

        /*mHolder.progressBar.setVisibility(View.INVISIBLE);
        mHolder.textHint.setText("没有更多了");*/
        if (mHolder != null) {
            if (mHolder.progressBar != null) {
                mHolder.progressBar.setVisibility(View.INVISIBLE);
            }
            if (mHolder.textHint != null) {
                mHolder.textHint.setText("没有更多了");
            }
        }

    }

    public void setTypeItemFooterStart(){

        if (mHolder != null){
            if (mHolder.progressBar != null){
                mHolder.progressBar.setVisibility(View.VISIBLE);
            }
            if (mHolder.textHint != null){
                mHolder.textHint.setText("正在加载...");
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        Log.i("MyLinearLayoutManager", "getItemViewType: ");
        if (position + 1 < getItemCount()) return TYPE_ITEM_NORMAL;
        else return TYPE_ITEM_FOOTER;
    }

    @Override
    public int getItemCount() {
        /*if (dataList.size() == 0){
            return 0;
        }else {
            return dataList.size() + 1;
        }*/
        return dataList.size() + 1;

    }

   /* @Override
    public long getItemId(int position) {
        //Log.i(TAG, "getItemId: position : " + position);
        return position;
    }*/
}

    /**
     * 助力捐赠
     */
  /*  private void helpToDonate(int aspirationId,ImageView imageView) {

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.helpToDonate(aspirationId, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralData generalData) {
                        int code = Integer.valueOf(generalData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0) {
                            imageView.setSelected(true);
                            imageView.setEnabled(false);

                        } else if (code == -1) {
                            RetrofitUtil.errorMsg(generalData.message);
                        } else if (code == 10) {
                            RetrofitUtil.tokenLose(mContext,generalData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
*/





 /* //设置 “已集齐”的文字背景，
                holder.flLoveState.setBackground(mContext.
                        getResources().getDrawable(R.drawable.shape_dream_wish_1));
                //设置 文字显示 “已集齐”
                holder.textViewLoveState.setText(R.string.dream_wish_str4);
                holder.textViewLoveState.setTextColor(mContext.
                        getResources().getColor(R.color.dream_wish_collected));
                //设置TextView 的图片， 一个白色的 心形 图片
                int with = Utils.dip2px(mContext, 18);    //dp 转 px
                int height = Utils.dip2px(mContext, 15);*/
               /* Drawable drawable = MyApplication.getContext().getDrawable();
                drawable.setBounds(0,0,with,height);
                viewHolder.textViewLoveState.setCompoundDrawablesRelative(drawable,null,null,null);
                */

              /*  holder.flProgressbar.setBackground(mContext.
                        getResources().getDrawable(R.drawable.shape_dream_wish_progessbar_red));*/

               /* @BindView(R.id.framelayout_progressbar)
        FrameLayout flProgressbar;        //爱心数量 的进度条的布局，集齐后，背景由gray变red
        @BindView(R.id.imageView_progressbar_state)
        ImageView ivProgressState;        //进度条 显示的长短，根据爱心数量变化而变化*/

//根据爱心数量，计算Imageview的宽度
              /*  GradientDrawable drawable = (GradientDrawable) holder.ivProgressState.getDrawable();
                int pxHeight = Utils.dip2px(mContext, 5);
                int pxWidth = 0;

                int progress = (wishData.heartNum * 180) / 10000;
                if (progress > 0) {
                    pxWidth = Utils.dip2px(MyApplication.getContext(), progress);
                } else {
                    pxWidth = Utils.dip2px(MyApplication.getContext(), 1);
                }
                drawable.setSize(pxWidth, pxHeight);*/