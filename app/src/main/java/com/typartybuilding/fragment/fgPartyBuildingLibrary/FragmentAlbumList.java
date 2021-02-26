package com.typartybuilding.fragment.fgPartyBuildingLibrary;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.PblAlbumListAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.bean.pblibrary.AlbumData;
import com.typartybuilding.fragment.HomeFragmentPartyBuildingLibraryNew;
import com.typartybuilding.utils.Utils;
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
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 党建图书馆  专辑列表
 */
public class FragmentAlbumList extends BaseFragment {

    private String TAG = "FragmentAlbumList";

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView_new_ideas)
    RecyclerView recyclerView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

   /* @BindView(R.id.textView_no_more)
    TextView textNoMore;             //没有更多的 文字提示*/
    
    private HomeFragmentPartyBuildingLibraryNew parentFg;

    private List<AlbumData> albumDataList = new ArrayList<>();    //每次获取的专辑 存放该集合中
    private List<AlbumData> dataList = new ArrayList<>();

    private PblAlbumListAdapter adapter;

    //private int loadingState;
    private boolean isError;

    //获取音频专辑的 参数
    private String categoryId = "41";     //分类ID，指定分类，为0时表示热门分类
    private String calcDimension = "1";   //计算维度，现支持最火（1），最新（2），经典或播放最多（3）
    private String tagName;            //分类下对应的专辑标签，不填则为热门分类

    private int page = 1;              //返回第几页，必须大于等于1，不填默认为1
    private int count = 30;           //每页多少条，默认20，最多不超过200
    private int totalPage;             //返回的总页数

    private boolean isFirst;          //是否是第一次加载专辑数据


    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //在党建书屋，精选推荐页面 才获取父fragment
        if (tagName == null) {
            parentFg = (HomeFragmentPartyBuildingLibraryNew) getParentFragment();
        }

        isFirst = true;

        initRecyclerView();
        //获取数据
        getAlbumList();
        //设置下拉刷新 或 上拉加载更多
        setRefreshLayout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");

    }


    @Override
    public int getLayoutId() {
        return R.layout.fg_pb_library_fragment_albumlist;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        adapter = new PblAlbumListAdapter(dataList, getActivity());
        recyclerView.setAdapter(adapter);
    }

    private void setRefreshLayout(){
        refreshLayout.setEnableRefresh(false);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (page <= totalPage) {
                    //loadData();
                    getAlbumList();
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                    //textNoMore.setVisibility(View.VISIBLE);
                    //refreshLayout.setEnableLoadMore(false);
                }
            }
        });
    }

    private void initData( List<Album> list){
        if (adapter != null) {
            int startItem = dataList.size();
            for (int i = 0; i < list.size(); i++) {
                Album album1 = list.get(i);
                AlbumData albumData = new AlbumData(album1.getId(), album1.getCoverUrlMiddle(),
                        album1.getPlayCount() + "", album1.getAlbumTitle(), album1.getAlbumIntro());
                dataList.add(albumData);

                adapter.notifyItemInserted(startItem + i);
            }
        }

        //关闭上拉加载更多
        if (refreshLayout != null) {
            refreshLayout.finishLoadMore();
        }

        if (progressBar != null){
            progressBar.setVisibility(View.GONE);
        }

    }

    private void getAlbumList() {
        Log.i(TAG, "getAlbumList: ");
        if (isError) {
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, categoryId);
        if (tagName != null) {
            map.put(DTransferConstants.TAG_NAME, tagName);
        }
        map.put(DTransferConstants.CALC_DIMENSION, calcDimension);
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, count + "");

        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(@Nullable AlbumList albumList) {
                if (albumList != null && albumList.getAlbums() != null && albumList.getAlbums().size() != 0) {
                    Log.i(TAG, "onSuccess: total page : " + albumList.getTotalPage());
                    Log.i(TAG, "onSuccess: currentpage : " + page);
                    totalPage = albumList.getTotalPage();

                    List<Album> list = albumList.getAlbums();
                    Log.i(TAG, "onSuccess: albumlist size :" + list.size());
                    page++;

                    if (list != null){
                        initData(list);
                    }


                  /*  for (int i = 0; i < list.size(); i++) {
                        Album album1 = list.get(i);
                        AlbumData albumData = new AlbumData(album1.getId(), album1.getCoverUrlMiddle(),
                                album1.getPlayCount() + "", album1.getAlbumTitle(), album1.getAlbumIntro());
                        albumDataList.add(albumData);
                    }
                    Log.i(TAG, "onSuccess: albumDataList size :" + albumDataList.size());

                    if (page == 1) {
                        loadData();
                    }
                    if (page < totalPage) {
                        page++;
                        getAlbumList();
                    }*/

                } else {

                }
                isError = false;
            }

            @Override
            public void onError(int i, String s) {
                isError = true;
            }
        });

    }

    private int countNum;
    private void loadData() {
        int total = albumDataList.size();
        int n = 0;
        if (isFirst){
            n = 12;
            isFirst = false;
        }else {
            n = 30;
        }
        Log.i(TAG, "loadData: n : " + n);
        int startItem = dataList.size();

        for (int j = 0; j < n; j++) {
            if (countNum >= total) {
                break;
            }
            dataList.add(albumDataList.get(countNum));
            countNum++;
            if (adapter != null){
                adapter.notifyItemInserted(startItem + j);
            }
        }
        Log.i(TAG, "loadData: dataList : " + dataList.size());

        if (adapter != null) {
            if (progressBar != null){
                progressBar.setVisibility(View.GONE);
            }
            //adapter.notifyDataSetChanged();
        }

        //loadingState = 0;
        //关闭下拉加载更多
        if (refreshLayout != null) {
            refreshLayout.finishLoadMore();
        }

    }







    private void loadDataToThread(){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                loadData();
                String str = "finish";
                emitter.onNext(str);
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String str) {

                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        //关闭下拉加载更多
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e ：" + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


}




/*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisiblePosition = 0;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE){//滑动停止
                    if(lastVisiblePosition + 1 == adapter.getItemCount()) {//滑动到最后一个item
                        if (dataList.size() < albumDataList.size() && loadingState==0){
                            loadingState = 1;
                            loadData();
                        }else if (dataList.size() == albumDataList.size()){
                            adapter.setTypeItemFooter();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

            }
        });*/




/*  private void loadAlbumList(){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                getAlbumList();
                emitter.onNext("nihao");
                emitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "onNext : " + s);
                        initRecyclerView();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e ：" + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }*/

 /* recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向上滑动
            private boolean isSlidingUpward = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i(TAG, "onScrollStateChanged: 1");

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的itemPosition
                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int itemCount = manager.getItemCount();
                    Log.i(TAG, "onScrollStateChanged: lastItemPostion : " + lastItemPosition);
                    Log.i(TAG, "onScrollStateChanged: itemCount : " + itemCount);

                    // 判断是否滑动到了最后一个item，并且是向上滑动
                    if (lastItemPosition == (itemCount - 1)) {
                        parentFg.loadMore.setVisibility(View.VISIBLE);

                        loadData();
                    }
                    if (lastItemPosition == dataList.size() - 1) {
                        parentFg.loadMore.setText("数据已加载完");
                    } else {
                        parentFg.loadMore.setText("上拉加载更多");
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
                Log.i(TAG, "onScrolled: 1");
                if (dy > 0) {
                    isSlidingUpward = true;

                }
            }
        });*/


/* *//**
 * 刷新完毕时关闭
 *//*
    private void refreshComplete() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.onRefreshComplete();
            }
        }, 1000);
    }*/

   /* private void initData(){
        //后台还能取到数据时，只显示3的倍数个 专辑
        //if (isHasData) {
            //取出，上一次刷新后，未显示的album
            if (backUpList.size() != 0) {
                for (int i = 0; i < backUpList.size(); i++) {
                    albumDataList.add(backUpList.get(i));
                }
            }
            int albumCount = albumDataList.size();
            //获取3的余数
            int  remainder = albumCount%3;
            Log.i(TAG, "initData: 3的余数remainder :" + remainder);
            //减去余数后的数,该数为3的整数倍
            int threeNum = albumCount - remainder;
            Log.i(TAG, "initData: 3的整数 ：" + threeNum);
            if (threeNum != 0) { //不等于0，表示album的数量大于3
                int count = 0;
                for (int j = 0; j < threeNum/3; j++) {
                    AlbumListData listData = new AlbumListData();
                    for (int i = 0; i < 3; i++) {
                        listData.albumDataList.add(albumDataList.get(count));
                        count++;
                    }
                    dataList.add(listData);
                }
                Log.i(TAG, "initData: dataList size : " + dataList.size());
                adapter.notifyDataSetChanged();
                //将剩下的album放入 备份集合后，在下次刷新时显示
                if (backUpList.size() != 0){
                    backUpList.clear();
                }
                for (int i = 0; i < remainder; i++){
                    backUpList.add(albumDataList.get(threeNum+i));
                }
                Log.i(TAG, "initData: backUpList size :" + backUpList.size());

            }else {  //表示获取的album数量小于3
                AlbumListData listData = new AlbumListData();
                for (int i = 0; i < albumCount; i++){
                    listData.albumDataList.add(albumDataList.get(i));
                }
                dataList.add(listData);
                adapter.notifyDataSetChanged();
                //已没有数据可显示了，删除备份的数据
                if (backUpList.size() != 0){
                    backUpList.clear();
                }
            }*/



       /* public void refresh() {
        Log.e(TAG, "---refresh");
        if (hasMore()) {
            getAlbumList();
        }
    }*/

   /* private boolean hasMore() {
        if (mAlbumList != null && mAlbumList.getTotalPage() < page) {
            //后台已经没有数据了，取出剩下的未显示的数据，显示在屏幕上
            if (backUpList.size() != 0) {
                AlbumListData listData = new AlbumListData();
                for (int i = 0; i < backUpList.size(); i++) {
                    listData.albumDataList.add(backUpList.get(i));
                }
                dataList.add(listData);
                adapter.notifyDataSetChanged();
                //已没有数据可显示了，删除备份的数据
                if (backUpList.size() != 0){
                    backUpList.clear();
                }
            }
            return false;
        }
        //还有数据
        return true;
    }*/

      /*  }else { //后台已经没有数据了，取出剩下的未显示的数据，显示在屏幕上
            if (backUpList.size() != 0) {
                AlbumListData listData = new AlbumListData();
                for (int i = 0; i < backUpList.size(); i++) {
                    listData.albumDataList.add(backUpList.get(i));
                }
                dataList.add(listData);
                adapter.notifyDataSetChanged();
                //已没有数据可显示了，删除备份的数据
                if (backUpList.size() != 0){
                    backUpList.clear();
                }
            }
        }*/




