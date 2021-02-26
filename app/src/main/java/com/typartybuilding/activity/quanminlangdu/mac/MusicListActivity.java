package com.typartybuilding.activity.quanminlangdu.mac;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.typartybuilding.R;
import com.typartybuilding.activity.quanminlangdu.adapter.MusicItemRecyclerViewAdapter;
import com.typartybuilding.activity.quanminlangdu.entity.MusicEntity;
import com.typartybuilding.activity.quanminlangdu.utils.Config;
import com.typartybuilding.activity.quanminlangdu.utils.SimplePlayerUtils;
import com.typartybuilding.activity.quanminlangdu.utils.Utils;
import com.typartybuilding.utils.UserUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MusicListActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private View mView;
    private int pageNo = 1;
    private List<MusicEntity> musicEntityList = new ArrayList<>();
    private MusicItemRecyclerViewAdapter musicItemRecyclerViewAdapter;
    private MediaPlayer player;
    private MusicEntity playEntity;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.MUSIC_SEARCH_ITEM_SUCCESS:

                    if (refreshLayout.getState().toHeader().isOpening){
                        refreshLayout.finishRefresh(true);
                    }
                    musicEntityList = (List<MusicEntity>) msg.obj;
                    if (musicEntityList == null){
                        return;
                    }
                    Log.i("TAG","search data size is:"+musicEntityList.size());
                    musicItemRecyclerViewAdapter.setData(musicEntityList);
                    musicItemRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                case Config.MUSIC_SEARCH_ITEM_FAIL:
                    break;
                case Config.MUSIC_SEARCH_MORE_ITEM_SUCCESS:
                    if (refreshLayout.getState().toFooter().isOpening){
                        refreshLayout.finishLoadMore(true);
                    }
                    musicEntityList.addAll((List<MusicEntity>) msg.obj);
                    musicItemRecyclerViewAdapter.setData(musicEntityList);
                    musicItemRecyclerViewAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        init();
    }

    private void init(){
        String token = UserUtils.getIns().getToken();
        Config.setToken(token);
        backBtn = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.music_list);
        refreshLayout = findViewById(R.id.smartRefreshLayout);
        mView = findViewById(R.id.mView);
        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        int stateHigh = 0;

        if (Utils.hasNotchAtHuawei(this)){
            stateHigh = Utils.getNotchSizeAtHuawei(MusicListActivity.this)[1];
        }
        if (Utils.hasNotchAtOPPO(MusicListActivity.this)){
            stateHigh = 80;
        }
        if (Utils.hasNotchAtVivo(MusicListActivity.this)){
            stateHigh = Utils.dip2px(MusicListActivity.this,27);
        }
        if (Utils.hasNotchAtMI(MusicListActivity.this)){
            stateHigh = 89;
        }

        if (stateHigh>result){
            result = stateHigh;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,result);
        mView.setBackgroundColor(Color.parseColor("#ffffff"));
        mView.setLayoutParams(params);
        Utils.translucentStatusBar(MusicListActivity.this,true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (player != null){
                        if (player.isPlaying()){
                            player.stop();
                        }
                        player.release();
                        player = null;
                    }
                }catch (Exception e){}

                MusicListActivity.this.finish();
            }
        });
        musicItemRecyclerViewAdapter = new MusicItemRecyclerViewAdapter(musicEntityList, new OnClickMusicItemListener() {
            @Override
            public void OnClickItemListener(MusicEntity entity) {

                if (player != null){
                    if (player.isPlaying()){
                        player.stop();
                    }
                    player.release();
                    player = null;
                }

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("music", entity);
                intent.putExtra("result",bundle);
                setResult(Config.MUSIC_CHOOSE_RESULT_OK,intent);
                finish();
            }

            /**
             * 播放音乐
             * */
            @Override
            public void OnClickMusicPlayOn(MusicEntity entity) {
                if (playEntity == null || !playEntity.getBgmUrl().equals(entity.getBgmUrl())){

                    if (player!=null){
                        player.stop();
                        player.release();
                    }

                    playEntity = entity;
                    player = SimplePlayerUtils.getMediaPlayer(entity.getBgmUrl());
                    player.start();
                }else if (player != null){
                    player.start();
                }
            }
            /**
             * 停止播放
             * */
            @Override
            public void OnClickMusicPlayStop(MusicEntity entity) {
                if (player != null){
                    player.pause();
                }
            }
        });

        searchMusicList(pageNo,Config.getToken(),Config.MUSIC_SEARCH_ITEM_SUCCESS);
        recyclerView.setAdapter(musicItemRecyclerViewAdapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                searchMusicList(pageNo,Config.getToken(),Config.MUSIC_SEARCH_ITEM_SUCCESS);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNo++;
                searchMusicList(pageNo,Config.getToken(),Config.MUSIC_SEARCH_MORE_ITEM_SUCCESS);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (player != null){
                if (player.isPlaying()){
                    player.pause();
                    player.stop();
                }
            }
        }catch (Exception e){}

        musicItemRecyclerViewAdapter.setSelectPostion(-1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //musicItemRecyclerViewAdapter.notifyDataSetChanged();
        searchMusicList(1,Config.getToken(),Config.MUSIC_SEARCH_ITEM_SUCCESS);
    }



    public interface OnClickMusicItemListener{
        void OnClickItemListener(MusicEntity entity);
        void OnClickMusicPlayOn(MusicEntity entity);
        void OnClickMusicPlayStop(MusicEntity entity);
    }

    private void searchMusicList(int pageNo,String token,int MsgWhat){
        String url = Config.getActionUrl(Config.GET_BGM_LIST);
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("pageNo",String.valueOf(pageNo));
        formBody.add("token",token);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message msg = new Message();
                msg.what = Config.MUSIC_SEARCH_ITEM_FAIL;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                Log.i("TAG",data);
                try {
                    JSONObject object = new JSONObject(data);
                    if (object.getInt("code") == 0){
                        List<MusicEntity> musicEntities = paseJsonToList(object);
                        Message msg = new Message();
                        msg.what = MsgWhat;
                        msg.obj = musicEntities;
                        handler.sendMessage(msg);
                    }else {
                        Message msg = new Message();
                        msg.what = Config.MUSIC_SEARCH_ITEM_FAIL;
                        handler.sendMessage(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private List<MusicEntity> paseJsonToList(JSONObject object) throws JSONException {
        if (object.getInt("code") != 0){
            return null;
        }

        JSONObject dataObject = object.getJSONObject("data");
        List<MusicEntity> musicEntities = new ArrayList<>();
        JSONArray array = dataObject.getJSONArray("rows");

        for (int i=0; i < array.length(); i++){
             JSONObject obj = (JSONObject) array.get(i);
             Log.i("TAG",obj.toString());
             MusicEntity entity = new MusicEntity(obj.getString("bgmId"),obj.getInt("bgmDuration"),obj.getString("bgmImg"),obj.getString("bgmName"),
                     obj.getString("bgmUrl"),obj.getInt("createTime"),obj.getString("bgmProfile"));
             musicEntities.add(entity);
        }

        return musicEntities;
    }



}
