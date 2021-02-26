package com.typartybuilding.activity.choiceness;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.typartybuilding.R;
import com.typartybuilding.adapter.recyclerViewAdapter.MixtureDataAdapter;
import com.typartybuilding.adapter.recyclerViewAdapter.SearchHistoryAdapter;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.WhiteTitleBaseActivity;
import com.typartybuilding.gsondata.GeneralMixtureData;
import com.typartybuilding.gsondata.MixtureData;
import com.typartybuilding.gsondata.pbmicrovideo.HotWordData;
import com.typartybuilding.retrofit.HomeRetrofitInterface;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.SharedPreferencesUtil;
import com.typartybuilding.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SearchActivity extends WhiteTitleBaseActivity {

    private String TAG = "SearchActivity";

    @BindView(R.id.imageView_search)
    ImageView imgSearch;              //搜索按钮
    @BindView(R.id.edit_search)
    EditText edtSearch;               //编辑输入 关键词
    /*@BindView(R.id.imageButton_cancel)
    ImageButton edtClear;            //edittext 的一键清除按钮*/

    @BindView(R.id.textView_cancel)
    TextView textCancel;              //返回按钮

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.imageView_no)
    ImageView imageViewNo;       //没搜到内容

    @BindView(R.id.linearLayout_history)
    LinearLayout layoutHis;
    @BindView(R.id.constraintLayout_his)
    ConstraintLayout constraintLayoutHis;   //历史记录布局
    @BindView(R.id.recyclerView_history)
    RecyclerView recyclerViewHis;     //显示历史记录
    @BindView(R.id.imageView_del)
    ImageView imgDel;                 //删除历史记录
    @BindViews({R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4})
    TextView [] textViews;

    private SearchHistoryAdapter adapterHis;
    private List<String> hisList = new ArrayList<>();

    private String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
    private MixtureDataAdapter adapter;
    private List<MixtureData> dataList = new ArrayList<>();
    private int loadingState;

    private int pageNo = 1;
    private int pageSize = 20;
    private int pageCount;

    private String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        //设置edittext  一键清除
        setEdtClear();

        setEdtSearch();
        initRecyclerView();
        initRecyclerViewHis();
        //显示历史记录
        showHistory();
        setRefreshLayout();
        getHotWordData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MixtureDataAdapter(dataList,this);
        //添加分割线
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerLine.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        recyclerView.addItemDecoration(dividerLine);
        recyclerView.setAdapter(adapter);
    }

    private void setRefreshLayout(){
        //禁止下拉刷新
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageNo <= pageCount ){
                    getSearchData();
                }else {
                    Utils.noMore();
                    refreshLayout.finishLoadMore(500);
                }
            }
        });
    }

    /**
     *  点击软件盘 的搜索按钮，可开始搜索
     */
    private void setEdtSearch(){
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    keyWord = edtSearch.getText().toString();
                    //清掉，上次的搜索数据
                    if (dataList.size() != 0){
                        dataList.clear();
                    }
                    pageNo = 1;
                    getSearchData();
                    return true;
                }else {
                    return false;
                }
            }
        });
    }

    private void setEdtClear(){
        /*edtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
                //清除搜索的内容
                dataList.clear();
                if (adapter != null){
                    adapter.notifyDataSetChanged();
                }
                //没有搜索到的图片不可见
                imageViewNo.setVisibility(View.INVISIBLE);
            }
        });*/

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtSearch.getText().toString() != null && !edtSearch.getText().toString().equals("")){
                    //edtClear.setVisibility(View.VISIBLE);

                   /* //输入内容后自动搜索
                    keyWord = edtSearch.getText().toString();
                    //清掉，上次的搜索数据
                    if (dataList.size() != 0){
                        dataList.clear();
                    }
                    pageNo = 1;
                    getSearchData();*/

                }else {
                    //edtSearch.setText("");
                    //清除搜索的内容
                    dataList.clear();
                    if (adapter != null){
                        adapter.notifyDataSetChanged();
                    }
                    //没有搜索到的图片不可见
                    imageViewNo.setVisibility(View.INVISIBLE);

                    //一键清除按钮不可见
                    //edtClear.setVisibility(View.INVISIBLE);
                    //历史记录可见
                    showHistory();
                }
            }
        });
    }


    @OnClick({R.id.imageView_search, R.id.edit_search, R.id.textView_cancel})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.imageView_search:
                keyWord = edtSearch.getText().toString();
                //清掉，上次的搜索数据
                if (dataList.size() != 0){
                    dataList.clear();
                }
                getSearchData();


               break;
            case R.id.edit_search:

                break;

            case R.id.textView_cancel:
                //隐藏软件盘
                Utils.hideSoftInput(edtSearch);
                finish();
                break;
        }
    }

    @OnClick({R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4})
    public void textViewClick(View view){
        switch (view.getId()){
            case R.id.textView1:
                startSearch(textViews[0].getText().toString());
                break;
            case R.id.textView2:
                startSearch(textViews[1].getText().toString());
                break;
            case R.id.textView3:
                startSearch(textViews[2].getText().toString());
                break;
            case R.id.textView4:
                startSearch(textViews[3].getText().toString());
                break;
        }
    }

    private void initData(GeneralMixtureData generalMixtureData){
        int startItem = dataList.size();
        MixtureData data[] = generalMixtureData.data.rows;
        Log.i(TAG, "initData: data : " + data);
        if (data != null){
            //获取数据后，设置上拉加载更多
            refreshLayout.setEnableLoadMore(true);

            for (int i = 0; i < data.length; i++) {
                dataList.add(data[i]);
                //adapter.notifyItemInserted(startItem + i);
            }
            int itemCount = dataList.size();
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            imageViewNo.setVisibility(View.INVISIBLE);
            //历史记录不可见
            layoutHis.setVisibility(View.INVISIBLE);
        }else {
            recyclerView.setVisibility(View.INVISIBLE);
            imageViewNo.setVisibility(View.VISIBLE);
            //历史记录不可见
            layoutHis.setVisibility(View.INVISIBLE);
        }

    }

    private void getSearchData(){
        //隐藏软件盘
        Utils.hideSoftInput(edtSearch);
        //搜索内容为空时，不保存
        if (!keyWord.equals("")) {
            //每次搜索前保存搜索记录
            saveHistory(edtSearch.getText().toString());
        }

        Log.i(TAG, "getSearchData: ");
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getSearchData(keyWord, token,pageNo,pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GeneralMixtureData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GeneralMixtureData generalMixtureData) {
                        int code = Integer.valueOf(generalMixtureData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            pageCount = generalMixtureData.data.pageCount;
                            pageNo++;
                            initData(generalMixtureData);

                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(generalMixtureData.message);
                            //关闭上拉加载更多
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }

                        }else if (code == 10){
                            RetrofitUtil.tokenLose(SearchActivity.this,generalMixtureData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e : " + e);
                        RetrofitUtil.requestError();
                        //关闭上拉加载更多
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadMore();
                        }

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void initRecyclerViewHis(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewHis.setLayoutManager(layoutManager);
        adapterHis = new SearchHistoryAdapter(hisList,this);
        recyclerViewHis.setAdapter(adapterHis);
    }

    //点击历史记录，可进行搜索
    public void startSearch(String word){
        edtSearch.setText(word);
        keyWord = edtSearch.getText().toString();
        if (dataList.size() != 0){
            dataList.clear();
        }
        pageNo = 1;
        getSearchData();
    }

    //清除历史记录
    @OnClick(R.id.imageView_del)
    public void onClickDelHis(){
        MyApplication.editor.putString(MyApplication.prefKey14_search_his,"");
        MyApplication.editor.apply();
        //清除集合
        hisList.clear();
        if (adapterHis != null){
            adapterHis.notifyDataSetChanged();
        }
        hideHistory();
    }

    private void hideHistory(){
        //layoutHis.setVisibility(View.INVISIBLE);
        constraintLayoutHis.setVisibility(View.INVISIBLE);

    }

    private void showHistory(){
        //显示历史记录时，禁止上拉加载更多
        refreshLayout.setEnableLoadMore(false);
        if (hisList.size() > 0){
            hisList.clear();
        }
        //获取历史记录
        SharedPreferencesUtil.getListData(MyApplication.prefKey14_search_his,String.class,hisList);
        Log.i(TAG, "showHistory: hisList size : " + hisList.size());

        if (adapterHis != null){
            adapterHis.notifyDataSetChanged();
        }
        if (hisList.size() > 0){
            layoutHis.setVisibility(View.VISIBLE);
            constraintLayoutHis.setVisibility(View.VISIBLE);
        }else {
            //layoutHis.setVisibility(View.INVISIBLE);
            constraintLayoutHis.setVisibility(View.INVISIBLE);
        }
    }

    private void saveHistory(String hisText){
        //先获取之前的历史记录
        List<String> hisList = SharedPreferencesUtil.getListData(MyApplication.prefKey14_search_his,String.class);
        //移除和最新记录相同的记录
        for (int j = 0; j < hisList.size(); j++){
            if (hisText.equals(hisList.get(j))){
                hisList.remove(j);
                break;
            }
        }
        //将最新记录，放入集合的第一个位置
        hisList.add(0,hisText);
        //将历史记录的集合存入本地
        SharedPreferencesUtil.putListData(MyApplication.prefKey14_search_his,hisList);
    }


    private void getHotWordData(){
        Retrofit retrofit = RetrofitUtil.getInstance().getmRetrofit();
        HomeRetrofitInterface homeRetrofitInterface = retrofit.create(HomeRetrofitInterface.class);
        homeRetrofitInterface.getHotWordData(1,4,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HotWordData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HotWordData hotWordData) {
                        int code = Integer.valueOf(hotWordData.code);
                        Log.i(TAG, "onNext: code : " + code);
                        if (code == 0){
                            if (hotWordData.data!=null&&hotWordData.data.rows!=null){
                                for (int i=0;i<hotWordData.data.rows.length;i++){
                                    textViews[i].setText(hotWordData.data.rows[i].hotWord);
                                }
                            }

                        }else if (code == -1){
                            RetrofitUtil.errorMsg(hotWordData.message);
                        }else if (code == 10){
                            Log.i(TAG, "onNext: message : " + hotWordData.message);
                            RetrofitUtil.tokenLose(SearchActivity.this,hotWordData.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e ; " + e);
                        RetrofitUtil.requestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
