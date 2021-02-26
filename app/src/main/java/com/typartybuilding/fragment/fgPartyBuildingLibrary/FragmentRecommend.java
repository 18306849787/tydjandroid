package com.typartybuilding.fragment.fgPartyBuildingLibrary;


import android.os.Bundle;
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
public class FragmentRecommend extends BaseFragment {

    private String TAG = "FragmentRecommend";

    @BindView(R.id.recyclerView_recommend)
    RecyclerView recyclerView;

    private HomeFragmentPartyBuildingLibrary parentFg;
    //精选推荐 音频数据
    private List<AlbumListData> dataList = new ArrayList<>();
    private PblAlbumListAdapter adapter;
    //音频专辑 列表
    private AlbumList mAlbumList = new AlbumList();




    @Override
    protected void initViews(Bundle savedInstanceState) {
        //获取父fragment
        parentFg = (HomeFragmentPartyBuildingLibrary) getParentFragment();
        initData();
        initRecyclerView();
        getAlbumList();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_library_fragment_recommend;
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //adapter = new PblAlbumListAdapter(dataList,getActivity());
        //添加分割线
       /* DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);*/
        recyclerView.setAdapter(adapter);
    }

    private void getAlbumList(){
        Log.i(TAG, "getAlbumList: ");
        Map<String ,String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID ,"41");
        map.put(DTransferConstants.CALC_DIMENSION ,"1");
        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(@Nullable AlbumList albumList) {
                if (albumList != null) {
                    mAlbumList = albumList;
                    Album album = mAlbumList.getAlbums().get(0);
                    Log.i(TAG, "onSuccess: album title :" + album.getAlbumTitle());
                    Log.i(TAG, "onSuccess: album_intro :" + album.getAlbumIntro());
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
