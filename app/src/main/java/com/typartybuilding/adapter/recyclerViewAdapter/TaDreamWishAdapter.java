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

import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.activity.dreamwish.DreamWishDetailActivity;
import com.typartybuilding.activity.dreamwish.DreamWishDetailTaActivity;
import com.typartybuilding.base.MyApplication;
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

public class TaDreamWishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "DreamWishAdapter";

    private List<DreamWishData.WishData> dataList = new ArrayList<>();
    private Context mContext;


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        ImageView imageView;              //封面图片
        @BindView(R.id.textView_headline)
        TextView headLine;                //标题
        @BindView(R.id.textView_date)
        TextView textDate;                //日期


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public TaDreamWishAdapter(List<DreamWishData.WishData> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_dream_wish_ta,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        DreamWishData.WishData wishData = dataList.get(i);
        //设置 标题
        holder.headLine.setText(wishData.aspirationTitle);
        //设置发布日期
        holder.textDate.setText(Utils.formatDate(wishData.aspirationPublishDate));

        String imgUrl = wishData.aspirationImg;
        if (imgUrl != null && imgUrl.contains(",")){
            String [] splits = imgUrl.split(",");
            imgUrl = splits[0];
        }

        //加载图片
        Glide.with(mContext).load(imgUrl)
                .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                .into(holder.imageView);

        //设置点击事件
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentAc = new Intent(mContext, DreamWishDetailTaActivity.class);
                //intentAc.putExtra("flag", 1); //标记1， 表示数据来自我的或他的心愿数据
                MyApplication.wishData = wishData;
                mContext.startActivity(intentAc);

            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size() ;

    }


}

