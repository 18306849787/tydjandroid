package com.typartybuilding.fragment.fgChoiceness;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.typartybuilding.R;
import com.typartybuilding.activity.choiceness.CurrentNewsActivity;
import com.typartybuilding.adapter.recyclerViewAdapter.CurrentNewsAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.FgCurrentNewsAdapter;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.ArticleBanner;
import com.typartybuilding.gsondata.choiceness.CurrentNewsData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 *时事新闻
 */
public class FragmentCurrentNews extends BaseFragment {

    private String TAG = "FragmentCurrentNews";
    
    @BindView(R.id.recyclerView_current_news_fg_cho)
    RecyclerView recyclerView;

    private List<ArticleBanner> bannerList = new ArrayList<>();
    private int pageNo = 1;    //分页-页码。不传则默认1
    private int pageSize = 20;  //分页-每页条数。不传则默认10
    private int pageCount ;     //新闻总共多少页
    private int loadingState;

    private int lastVisiblePosition = 0;

    private CurrentNewsAdapter adapter;
    private boolean isDestroy;
    private boolean isRefresh;     //是否 进行 下拉刷新

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initRecyclerView();
        //获取新闻数据
        isDestroy = false;
        isRefresh = false;
        getCurrentNews();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fg_choiceness_fragment_current_news;
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CurrentNewsAdapter(bannerList,getActivity());
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);

        recyclerView.setFocusable(false);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //int lastVisiblePosition = 0;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE){//滑动停止
                    if(lastVisiblePosition + 1 == adapter.getItemCount()) {//滑动到最后一个item
                        if (pageNo <= pageCount && loadingState==0){
                            loadingState = 1;
                            getCurrentNews();
                            adapter.setTypeItemFooterStart();
                        }else if (pageNo > pageCount){
                            adapter.setTypeItemFooter();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                //Log.i(TAG, "onScrolled: lastVisiblePosition : " + lastVisiblePosition);

            }
        });

    }


    @OnClick(R.id.textView_more)
    public void onClickMore(){
        Intent intentAc = new Intent(getActivity(), CurrentNewsActivity.class);
        startActivity(intentAc);
    }

    //用于首页 下拉刷新
    public void refreshCurrentNews(){
        isRefresh = true;
        pageNo = 1;
        //lastVisiblePosition = 0;
        getCurrentNews();
    }

    private void initData(CurrentNewsData currentNewsData) {

        if (!isDestroy) {
            Log.i(TAG, "loadData: pageCount : " + pageCount);

            if (isRefresh){
                if (bannerList.size() > 0){
                    bannerList.clear();
                }
                isRefresh = false;
            }

            if (currentNewsData.data.rows != null) {

                int size = currentNewsData.data.rows.length;
                for (int i = 0; i < size; i++) {
                    bannerList.add(currentNewsData.data.rows[i]);
                }
                adapter.notifyDataSetChanged();
            }
        }
        loadingState = 0;
    }

    private void getCurrentNews(){
        Log.i(TAG, "getCurrentNews: ");
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");

        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getCurrentNews(userId, pageNo,pageSize,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CurrentNewsData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CurrentNewsData currentNewsData) {
                        int code = Integer.valueOf(currentNewsData.code);
                        if (code == 0){
                            initData(currentNewsData);
                            //获取总页数
                            pageCount = currentNewsData.data.pageCount;
                            Log.i(TAG, "onNext: pageCount : " + pageCount);
                            //加载完一次后page 加1 ，下次加载 下一页
                            pageNo++;
                            if (pageNo > pageCount){
                                if (adapter != null) {
                                    adapter.setTypeItemFooter();
                                }
                            }
                        }else if (code == -1){
                            RetrofitUtil.errorMsg(currentNewsData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }






}
