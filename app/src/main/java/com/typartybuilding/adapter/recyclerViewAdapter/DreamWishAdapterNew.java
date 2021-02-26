package com.typartybuilding.adapter.recyclerViewAdapter;

import android.app.Activity;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.dreamwish.DreamWishDetailActivity;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.activity.dreamwish.GoodPeopleActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.HomeFragmentDreamWishNew;
import com.typartybuilding.gsondata.GeneralData;
import com.typartybuilding.gsondata.dreamwish.DreamWishData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DreamWishAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "DreamWishAdapterNew";

    private List<DreamWishData.WishData> dataList = new ArrayList<>();
    private Context mContext;
    private HomeFragmentDreamWishNew fragmentDreamWishNew;

    private int userType = MyApplication.pref.getInt(MyApplication.prefKey10_login_userType, -1);
    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token, "");

    public int currentPos;    //被点击的当前位置
    public boolean isNextAc = false;  //是否进入详情页面

    private Drawable yjzDrawable;          //已捐赠
    private Drawable wjzDrawable;          //未捐赠
    private Drawable rlDrawable;          //已集齐,认领心愿


    static class ViewHolderZero extends RecyclerView.ViewHolder {
        @BindView(R.id.textView_more)
        TextView textMore;

        public ViewHolderZero(View view) {
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

    public DreamWishAdapterNew(List<DreamWishData.WishData> dataList, Context mContext,HomeFragmentDreamWishNew fragmentDreamWishNew) {
        this.dataList = dataList;
        this.mContext = mContext;
        this.fragmentDreamWishNew = fragmentDreamWishNew;
        wjzDrawable = mContext.getResources().getDrawable(R.drawable.ymxy_btn_juanzeng);
        yjzDrawable = mContext.getResources().getDrawable(R.drawable.ymxy_btn_yijuanzeng);
        rlDrawable = mContext.getResources().getDrawable(R.mipmap.ymxy_btn_rlxy);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == 0){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_dream_wish_0,
                    viewGroup, false);
            return new ViewHolderZero(view);
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_dream_wish,
                    viewGroup, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
       if (i == 0){
           setItemViewHolerZero(viewHolder,i);
       }else {
           setItemViewHolder(viewHolder,i);
       }

    }

    private void setItemViewHolerZero(RecyclerView.ViewHolder viewHolder, int i){
        ViewHolderZero holder = (ViewHolderZero)viewHolder;
        holder.textMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAc = new Intent(mContext, GoodPeopleActivity.class);
                mContext.startActivity(intentAc);
            }
        });

    }

    private void setItemViewHolder(RecyclerView.ViewHolder viewHolder, int i){
        ViewHolder holder = (ViewHolder) viewHolder;
        DreamWishData.WishData wishData = dataList.get(i-1);
        //设置 主副标题
        holder.headLine.setText(wishData.aspirationTitle);
        //截止 5月14日，集齐 1万份 爱心，太原市政府 将奖励 1万元
        String date = Utils.formatMonth(wishData.expirationTime) + "月" +
                Utils.formatDay(wishData.expirationTime) + "日";
        String str = "集齐" + wishData.needHeartNum + "份爱心，"
                + wishData.provideOrgan + "将奖励" + wishData.provideMoney ;
        holder.subHead.setText(str);

        //设置 爱心数量
        holder.loveNum.setText(wishData.heartNum + "");
        //设置进度条
        holder.seekBar.setPadding(0, 0, 0, 0);
        holder.seekBar.setMax(wishData.needHeartNum);
        holder.seekBar.setProgress(wishData.heartNum);

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
        Log.i(TAG, "onBindViewHolder: wishData.aspirationStatus : " + wishData.aspirationStatus);
        if (wishData.aspirationStatus == 1) {

            //设置 “已集齐”的背景图可见
            holder.llLoveCollected.setVisibility(View.VISIBLE);
            //集齐，可认领
            holder.imageViewJz.setImageDrawable(rlDrawable);
            //设置进度条，全部显示完
            holder.seekBar.setProgress(wishData.needHeartNum);
            //设置显示100%
            holder.textPercent.setText("100%");
        } else {
            //设置 “已集齐”的背景图不可见
            holder.llLoveCollected.setVisibility(View.INVISIBLE);

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
                //标记，进入了详情页面
                isNextAc = true;
            }
        });
        holder.imageViewJz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1 表示已集齐，点击认领； 非 1 ，点击捐赠
                if (wishData.aspirationStatus == 1){
                    //认领心愿,只有党员可以认领
                    if (userType == 2) {
                        claimDreamWish(i, wishData.aspirationId);
                    }else {
                        Toast.makeText(mContext,"非认证党员不可认领",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if (userType == 3) {
                        MyApplication.remindVisitor((Activity) mContext);
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
                                            //捐赠成功，修改状态,已捐赠爱心数据量加 1
                                            wishData.isDonation = 1;
                                            wishData.heartNum += 1;
                                            holder.imageViewJz.setImageDrawable(yjzDrawable);
                                            //设置 爱心数量
                                            holder.loveNum.setText(wishData.heartNum + "");
                                            //设置进度条
                                            holder.seekBar.setProgress(wishData.heartNum );
                                            //设置百分百
                                            int percent = ((wishData.heartNum ) * 100) / wishData.needHeartNum;
                                            if (percent > 0) {
                                                holder.textPercent.setText(percent + "%");
                                            } else {
                                                percent = 1;
                                                holder.textPercent.setText(percent + "%");
                                            }
                                            //判断是否已集齐
                                            if (wishData.heartNum == wishData.needHeartNum) {
                                                //设置 “已集齐”的背景图可见
                                                holder.llLoveCollected.setVisibility(View.VISIBLE);
                                                holder.imageViewJz.setImageDrawable(rlDrawable);
                                                //修改状态，表示已集齐
                                                wishData.aspirationStatus = 1;
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

            }
        });
    }

    private void claimDreamWish(int position,int aspirationId){

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.claimDreamWish(aspirationId, token)
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
                            //刷新数据
                            fragmentDreamWishNew.refreshWishData(position);

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


    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }


}






