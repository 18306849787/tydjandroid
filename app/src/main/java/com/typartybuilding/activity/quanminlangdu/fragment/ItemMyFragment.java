package com.typartybuilding.activity.quanminlangdu.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.typartybuilding.activity.quanminlangdu.fragment.dummy.DummyContent;
import com.typartybuilding.activity.quanminlangdu.utils.Config;
import com.typartybuilding.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class ItemMyFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private String userID = "";
    private ItemFragment.OnListFragmentInteractionListener mListener;
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

//                    if (DummyContent.ITEMS == null){
//                        return;
//                    }

                    itemRecyclerViewAdapter.setNewData(DummyContent.ITEMS);
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
    public ItemMyFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemMyFragment newInstance(String userID) {
        ItemMyFragment fragment = new ItemMyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COLUMN_COUNT, userID);
        fragment.setArguments(args);
        return fragment;
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
            if (!EventBus.getDefault().isRegistered(this)){
                EventBus.getDefault().register(this);
            }
            searchData(userID,1, UserUtils.getIns().getToken());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_item_list, container, false);

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
        itemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(DummyContent.ITEMS,1,mListener);
        recyclerView.setAdapter(itemRecyclerViewAdapter);
        // Set the adapter

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(RefreshReadBean  refreshReadBean){
        pageNo = 1;
        searchData(userID,1,UserUtils.getIns().getToken());
    }

    public void setOnListFragmentInteractionListener(ItemFragment.OnListFragmentInteractionListener onListFragmentInteractionListener){
        mListener = onListFragmentInteractionListener;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof ItemFragment.OnListFragmentInteractionListener) {
//            mListener = (ItemFragment.OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
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
//    public interface OnListFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onListFragmentInteraction(DummyItem item);
//    }

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
                    List<DummyContent.DummyItem> dummyItemList = pareJsonToData(object);
                    Message msg = new Message();
                    msg.what = Config.SEARCH_BOOK_LIST_SUCCESS;
                    msg.obj = dummyItemList;
                    handler.sendMessage(msg);
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
        if (object.getInt("code") != 0||object.getJSONObject("data").optInt("totalCount")==0){
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
