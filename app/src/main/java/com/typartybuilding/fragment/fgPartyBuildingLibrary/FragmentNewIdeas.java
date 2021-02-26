package com.typartybuilding.fragment.fgPartyBuildingLibrary;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.PblAlbumListAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.bean.pblibrary.AlbumListData;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingLibrary;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 党建图书馆  新思想
 */
public class FragmentNewIdeas extends BaseFragment {

    private String TAG = "FragmentNewIdeas";

    @BindView(R.id.recyclerView_new_ideas)
    RecyclerView recyclerView;

    private HomeFragmentPartyBuildingLibrary parentFg;
    //新思想 音频数据
    private List<AlbumListData> dataList = new ArrayList<>();
    private PblAlbumListAdapter adapter;
    //音频专辑 列表
    private AlbumList mAlbumList = new AlbumList();
    private String categoryId = "41";     //分类ID，指定分类，为0时表示热门分类
    private String calcDimension = "1";   //计算维度，现支持最火（1），最新（2），经典或播放最多（3）
    private String tagName = "新思想";  //分类下对应的专辑标签，不填则为热门分类
    private String page = "1";        //返回第几页，必须大于等于1，不填默认为1
    private String count = "25";     //每页多少条，默认20，最多不超过200


    @Override
    protected void initViews(Bundle savedInstanceState) {
        //获取父fragment
        parentFg = (HomeFragmentPartyBuildingLibrary) getParentFragment();
        //设置上拉刷新
        //setRefresh();
        getAlbumList();

        initRecyclerView();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_library_fragment_albumlist;
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //adapter = new PblAlbumListAdapter(dataList,getActivity());

        recyclerView.setAdapter(adapter);
    }


    private void getAlbumList(){
        Map<String ,String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID ,categoryId);
        map.put(DTransferConstants.TAG_NAME, tagName);
        map.put(DTransferConstants.CALC_DIMENSION ,calcDimension);
        map.put(DTransferConstants.PAGE,page);
        map.put(DTransferConstants.DISPLAY_COUNT,count);
        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(@Nullable AlbumList albumList) {
                if (albumList != null) {
                    mAlbumList = albumList;
                    List<Album> list = albumList.getAlbums();
                    Album album = mAlbumList.getAlbums().get(0);
                    Log.i(TAG, "onSuccess: albumlist size :" + list.size());
                    Log.i(TAG, "onSuccess: album title :" + album.getAlbumTitle());
                    Log.i(TAG, "onSuccess: album_intro :" + album.getAlbumIntro());
                    Log.i(TAG, "onSuccess: modle url :" + album.getCoverUrlMiddle());

                    for (int i = 0; i < list.size(); i++){

                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void initData(){

    }


}
