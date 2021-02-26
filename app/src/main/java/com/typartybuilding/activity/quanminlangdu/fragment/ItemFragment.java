package com.typartybuilding.activity.quanminlangdu.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.loginRelatedActivity.LoginActivity;
import com.typartybuilding.activity.quanminlangdu.fragment.dummy.DummyContent;
import com.typartybuilding.activity.quanminlangdu.utils.Config;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.loader.InteractiveLoader;
import com.typartybuilding.utils.UserUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private String userID = "";
    private OnListFragmentInteractionListener mListener;
    private MyItemRecyclerViewAdapter itemRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private int pageNo = 1;

    List<DummyContent.DummyItem> dataList = new ArrayList<>();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.SEARCH_BOOK_LIST_SUCCESS:
                    if (refreshLayout.getState().toHeader().isOpening){
                        refreshLayout.finishRefresh(true);
                    }
                    DummyContent.ITEMS = (List<DummyContent.DummyItem>) msg.obj;

                    if (DummyContent.ITEMS == null){
                        return;
                    }

                    itemRecyclerViewAdapter.setData(DummyContent.ITEMS);
                    itemRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                case Config.SEARCH_BOOK_LIST_MORE_SUCCESS:
                    refreshLayout.finishLoadMore(true);
                    if (DummyContent.ITEMS == null){
                        return;
                    }
                    DummyContent.ITEMS.addAll((List<DummyContent.DummyItem>) msg.obj);
                    itemRecyclerViewAdapter.setData(DummyContent.ITEMS);
                    itemRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                case Config.SEARCH_BOOK_LIST_FAIL:
                    if (refreshLayout.getState().toHeader().isOpening){
                        refreshLayout.finishRefresh(true);
                    }
                    if (refreshLayout.getState().toFooter().isOpening){
                        refreshLayout.finishLoadMore(true);
                    }
                    break;
            }
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(String userID) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COLUMN_COUNT, userID);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnListFragmentInteractionListener(OnListFragmentInteractionListener onListFragmentInteractionListener){
        mListener = onListFragmentInteractionListener;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userID = getArguments().getString(ARG_COLUMN_COUNT);
        }
    }

    //界面可见时再加载数据
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            searchData(userID,1, UserUtils.getIns().getToken());
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        recyclerView = view.findViewById(R.id.list);
        refreshLayout = view.findViewById(R.id.smartRefreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                searchData(userID,1,UserUtils.getIns().getToken());
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNo++;
                searchMoreData(userID,pageNo,UserUtils.getIns().getToken());
            }
        });
        itemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(DummyContent.ITEMS,0,mListener);
        recyclerView.setAdapter(itemRecyclerViewAdapter);
        // Set the adapter

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyContent.DummyItem item,int type);
    }


    private void searchData(String userId,int pageNo,String token){
        Log.i("TAG","search for data");
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        if (!userId.equals("")){
            formBody.add("userId",userId);
        }
        formBody.add("pageNo",String.valueOf(pageNo));
        formBody.add("token",token);

        String url = Config.getActionUrl(Config.GET_BOOK_LIST);
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("TAG",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String data = response.body().string();
                    JSONObject object = new JSONObject(data);
                    Log.i("TAG",object.toString());
                    if (object.getInt("code") == 0){
                        List<DummyContent.DummyItem> dummyItemList = pareJsonToData(object);
                        Message msg = new Message();
                        msg.what = Config.SEARCH_BOOK_LIST_SUCCESS;
                        msg.obj = dummyItemList;
                        handler.sendMessage(msg);
                    }else if (object.getInt("code") == 10){
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(intent);
                    } else {
                        Message msg = new Message();
                        msg.what = Config.SEARCH_BOOK_LIST_FAIL;
                        msg.obj = null;
                        handler.sendMessage(msg);
                    }

                }catch (JSONException e){
                    Message msg = new Message();
                    msg.what = Config.SEARCH_BOOK_LIST_FAIL;
                    msg.obj = null;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void searchMoreData(String userId,int pageNo,String token){
        Log.i("TAG","search for data");
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        if (!userId.equals("")){
            formBody.add("userId",userId);
        }
        formBody.add("pageNo",String.valueOf(pageNo));
        formBody.add("token",token);

        String url = Config.getActionUrl(Config.GET_BOOK_LIST);

        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("TAG",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String data = response.body().string();
                    JSONObject object = new JSONObject(data);
                    Log.i("TAG",object.toString());
                    if (object.getInt("code") == 0){
                        List<DummyContent.DummyItem> dummyItemList = pareJsonToData(object);
                        Message msg = new Message();
                        msg.what = Config.SEARCH_BOOK_LIST_MORE_SUCCESS;
                        msg.obj = dummyItemList;
                        handler.sendMessage(msg);
                    }else {
                        Message msg = new Message();
                        msg.what = Config.SEARCH_BOOK_LIST_FAIL;
                        msg.obj = null;
                        handler.sendMessage(msg);
                    }

                }catch (JSONException e){
                    Message msg = new Message();
                    msg.what = Config.SEARCH_BOOK_LIST_FAIL;
                    msg.obj = null;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private List<DummyContent.DummyItem> pareJsonToData(JSONObject object) throws JSONException {
        if (object.getInt("code") != 0){
            return null;
        }
        List<DummyContent.DummyItem> dummyItems = new ArrayList<>();
        JSONArray jsonArray = object.getJSONObject("data").getJSONArray("rows");
        for (int i = 0;i <jsonArray.length(); i++){
            JSONObject obj = jsonArray.getJSONObject(i);
            DummyContent.DummyItem item = new DummyContent.DummyItem(obj.getString("readId"),obj.getString("readTitle"),obj.getString("readAuthor")
            ,obj.getString("readNumber"),obj.getString("readCover"),obj.getString("readProfile"),obj.getString("readDetail")
            ,obj.getString("readFrequency"),obj.getString("publishDate"),obj.getString("updateTime"),obj.optInt("readType"));
            dummyItems.add(item);
        }
        return dummyItems;
    }

}
