package com.typartybuilding.fragment.fgChoiceness;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.choiceness.HotVideoActivity;
import com.typartybuilding.activity.PlayVideoDetailActivity;
import com.typartybuilding.base.BaseFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.fragment.HomeFragmentChoiceness;
import com.typartybuilding.fragment.HomeFragmentChoicenessNew;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;
import com.typartybuilding.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 热门视频
 */
public class FragmentHotVideo extends BaseFragment {

    private String TAG = "FragmentHotVideo";

    @BindView(R.id.textView_more)
    TextView textViewMore;           //更多 按钮
    @BindViews({R.id.imageView1,R.id.imageView2,R.id.imageView3,R.id.imageView4})
    ImageView imageView [];          //  图片 的四个 ImageView
    @BindViews({R.id.textView1_play_times, R.id.textView2_play_times, R.id.textView3_play_times, R.id.textView4_play_times})
    TextView playTimes [];           // 播放次数 的四个 TextView
    @BindViews({R.id.textView1_play_duration, R.id.textView2_play_duration, R.id.textView3_play_duration, R.id.textView4_play_duration})
    TextView playDuration [];        // 播放时长 的四个 TextView
    @BindViews({R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4})
    TextView textHeadline [];         // 视频标题

    private HomeFragmentChoiceness fgParent;

    private boolean isDestroy;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        //获取父fragment
        fgParent = (HomeFragmentChoiceness) getParentFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_choiceness_fragment_hot_video;
    }


    /**
     *  父 fragment 网络加载完成，会调该方法刷新页面
     * @param videoList
     */
    public void loadData( List<ArticleBanner> videoList){
        if (!isDestroy) {
            int size = videoList.size();
            Log.i(TAG, "loadData: video size : " + size);
            if (size > 0) {
                //加载封面图片
                for (int i = 0; i < size; i++) {

                    Log.i(TAG, "loadData: videoList.get(i).picUrls ； " + videoList.get(i).videoCover);

                    Glide.with(getActivity()).load(videoList.get(i).videoCover)
                            .apply(MyApplication.requestOptions)  //url为空或异常显示默认头像
                            .into(imageView[i]);
                    //开启线程计算视频时长
                    //Utils.calculateDuration(playDuration[i], videoList.get(i).videoUrl);

                    playDuration[i].setText(Utils.formatTime(videoList.get(i).videoDuration));
                    playTimes[i].setText(Utils.formatPlayTimes(videoList.get(i).browseTimes) + "次播放");

                    textHeadline[i].setText(videoList.get(i).articleTitle);

                }
            }
        }
    }


    /**
     * “更多” 点击事件
     */
    @OnClick(R.id.textView_more)
    public void onClickMore(){
        Intent intentAc = new Intent(getActivity(), HotVideoActivity.class);
        startActivity(intentAc);
    }

    /**
     * 四张图片 的点击事件
     */
    @OnClick({R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4})
    public void onClickImg(View view){
        switch (view.getId()){
            case R.id.imageView1 :
                startPlayVideo(0);
                break;
            case R.id.imageView2 :
                startPlayVideo(1);
                break;
            case R.id.imageView3 :
                startPlayVideo(2);
                break;
            case R.id.imageView4 :
                startPlayVideo(3);
                break;
            default:
                break;
        }
    }

    /**
     * 点击 图片后，跳转到视频详情的activity
     */
    private void startPlayVideo(int i){
        Intent intentAc = new Intent(getActivity(), PlayVideoDetailActivity.class);
        //intentAc.putExtra("ArticleBanner",fgParent.videoList.get(i));
        MyApplication.articleBanner = fgParent.videoList.get(i);
        startActivity(intentAc);
    }




}
