package com.typartybuilding.fragment.fgPartyBuildingMap;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.PbMapAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingMap;
import com.typartybuilding.gsondata.pbmap.AgenciesData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 全市教育基地
 */
public class FragmentEducationalBase extends BaseFragment {

    @BindView(R.id.recyclerView_educational_base)
    RecyclerView recyclerView;

    private PbMapAdapter adapter;
    private List<AgenciesData.AgencyData> dataList = new ArrayList<>();
    HomeFragmentPartyBuildingMap fgParent;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        fgParent = (HomeFragmentPartyBuildingMap)getParentFragment();
        initRecyclerView();
        //fgParent.getAgenciesData(3,"");
    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_map_fragment_educational_base;
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //adapter = new PbMapAdapter(dataList,getContext(),);
        recyclerView.setAdapter(adapter);
    }


    public void initData(List<AgenciesData.AgencyData> agencyDataList){
        if (agencyDataList != null){
            if (dataList.size() > 0){
                dataList.clear();
            }
            for (int i = 0; i < agencyDataList.size(); i++){
                dataList.add(agencyDataList.get(i));
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

    }


}
