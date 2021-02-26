package com.typartybuilding.fragment.fgPartyBuildingLibrary;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.PblAlbumListAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.bean.pblibrary.AlbumListData;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingLibrary;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 党建书屋  榜样故事
 */
public class FragmentExampleStory extends BaseFragment {

    @BindView(R.id.recyclerView_example_story)
    RecyclerView recyclerView;

    private HomeFragmentPartyBuildingLibrary parentFg;
    //精选推荐 音频数据
    private List<AlbumListData> dataList = new ArrayList<>();
    private PblAlbumListAdapter adapter;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        //获取父fragment
        parentFg = (HomeFragmentPartyBuildingLibrary) getParentFragment();
        initData();
        initRecyclerView();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_library_fragment_example_story;
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

    private void initData(){

    }

}
