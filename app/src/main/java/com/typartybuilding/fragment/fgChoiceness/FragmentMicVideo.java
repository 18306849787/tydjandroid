package com.typartybuilding.fragment.fgChoiceness;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.typartybuilding.R;
import com.typartybuilding.activity.PlayMicroVideoActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.MicVideoAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.fragment.HomeFragmentChoiceness;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingVideo;
import com.typartybuilding.gsondata.choiceness.ChoicenessData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 精选页面的 精彩微视
 */
public class FragmentMicVideo extends BaseFragment {


    @BindView(R.id.recyclerView_mic_video_fg_cho)
    RecyclerView recyclerView;

    private HomeFragmentChoiceness parentFg;   //父fragment
    public List<ChoicenessData.MicroVision> visionList = new ArrayList<>();
    private MicVideoAdapter adapter;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //获取父fragment
        parentFg = (HomeFragmentChoiceness) getParentFragment();

        initRecyclerView();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_choiceness_fragment_mic_video;
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);

        adapter = new MicVideoAdapter(visionList,getActivity());
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.HORIZONTAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line_mic_video_fg_cho));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);
    }


    public void loadData(){
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.textView_more)
    public void onClickMore(){
        parentFg.parentFg.viewPager.setCurrentItem(2);

        HomeFragmentPartyBuildingVideo fragment = null;
        //fragment = (HomeFragmentPartyBuildingVideo)parentFg.parentFg.fragmentList.get(2);

        FragmentStatePagerAdapter pagerAdapter = (FragmentStatePagerAdapter) parentFg.parentFg.viewPager.getAdapter();
        fragment = (HomeFragmentPartyBuildingVideo)pagerAdapter.instantiateItem(parentFg.parentFg.viewPager,2);


        if (fragment != null) {
            fragment.setCurrentFragment(2);
        }

    }


    private void startPlayVideo(){
        Intent intentAc = new Intent(getActivity(), PlayMicroVideoActivity.class);
        startActivity(intentAc);
    }






    /* @OnClick({R.id.imageView1_play, R.id.imageView2_play})
    public void onClickPlayVideo(View view){
        switch (view.getId()){
            case R.id.imageView1_play :
                startPlayVideo();
                break;
            case R.id.imageView2_play :
                startPlayVideo();
                break;
        }
    }*/

    /* @BindViews({R.id.textView1_headline, R.id.textView2_headline})
    TextView headLine [];                       //视频的 标题
    @BindViews({R.id.imageView1, R.id.imageView2})
    ImageView imageView [];                     //视频对应的 图片
    @BindViews({R.id.imageView1_play, R.id.imageView2_play})
    ImageView play [];                          //图片中央的 播放按钮
    @BindViews({R.id.textView1_play_duration, R.id.textView2_play_duration})
    TextView playDuration [];                   //视频 时长
    @BindViews({R.id.circle_img1, R.id.circle_img2})
    CircleImageView headImg [];                  //视频发布者 的头像
    @BindViews({R.id.textView1_name, R.id.textView2_name})
    TextView authorName [];                      //视频发布者 的呢称
    @BindViews({R.id.textView1_like, R.id.textView2_like})
    TextView likeThumb [];                       //点赞
    @BindViews({R.id.textView1_comment, R.id.textView2_comment})
    TextView textComment [];                     //评论*/

   /* private List<String> headLineList = new ArrayList<>();          //标题
    private List<String> imgUrlList = new ArrayList<>();            //视频图片url
    private List<String> playDurationList = new ArrayList<>();      //视频 时长
    private List<String> headImgUrlList = new ArrayList<>();        //发布者头像 url
    private List<String> authorNameList = new ArrayList<>();        //发布者昵称
    private List<String> likeThumbList = new ArrayList<>();         //点赞的 数量*/

}
