package com.typartybuilding.adapter.recyclerViewAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.choiceness.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "SearchHistoryAdapter";

    private List<String> dataList = new ArrayList<>();
    private SearchActivity searchActivity;

    public SearchHistoryAdapter(List<String> dataList, SearchActivity searchActivity) {
        this.dataList = dataList;
        this.searchActivity = searchActivity;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindViews({R.id.textView1, R.id.textView2})
        TextView [] textViews;
        @BindView(R.id.view)
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_ac_search_history,
                viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder)viewHolder;
        int index = 2*i;
        int size = dataList.size();
        holder.view.setVisibility(View.VISIBLE);
        for (int j = 0; j < 2; j++){
            if (index >= size){
                holder.textViews[j].setText("");
                holder.view.setVisibility(View.INVISIBLE);
                break;
            }else {
                holder.textViews[j].setText(dataList.get(index));
            }
            index++;
        }
        holder.textViews[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchActivity.startSearch(holder.textViews[0].getText().toString());
            }
        });
        holder.textViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchActivity.startSearch(holder.textViews[1].getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        int size = dataList.size();
        int n = size%2;
        if (n == 0){
            return size/2;
        }else {
            return size/2 + 1;
        }
    }
}
