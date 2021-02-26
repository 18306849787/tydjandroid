package com.typartybuilding.activity.second.interactive;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Bundle;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.dialog.CustomDialog;
import com.typartybuilding.R;
import com.typartybuilding.activity.quanminlangdu.entity.BookEntity;
import com.typartybuilding.activity.quanminlangdu.fragment.RefreshReadBean;
import com.typartybuilding.activity.quanminlangdu.fragment.dummy.DummyContent;
import com.typartybuilding.activity.quanminlangdu.utils.Config;
import com.typartybuilding.base.BaseAct;
import com.typartybuilding.loader.InteractiveLoader;
import com.typartybuilding.network.https.BaseResponse;
import com.typartybuilding.network.https.RequestCallback;
import com.typartybuilding.utils.UserUtils;
import com.typartybuilding.view.WeakHandler;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author chengchunhuiit@163.com
 * @date 1/7/21
 * @describe
 */
@Route(path = MyReadAct.PATH)
public class MyReadAct extends BaseAct implements WeakHandler.IHandler {
    public static final String PATH = "/act/my_read";

    @BindView(R.id.act_layout_my_read_back)
    ImageView mBackImg;
    @BindView(R.id.act_layout_my_read_time)
    TextView mTime;
    @BindView(R.id.act_layout_my_read_play)
    ImageView mPlayview;
    @BindView(R.id.act_layout_my_read_delete)
    ImageView mDeleteView;

    @Autowired
    DummyContent.DummyItem item;

    WeakHandler handler;
    private MediaPlayer mediaPlayer;
    BookEntity entity;
    String total;
    InteractiveLoader interactiveLoader;
    private boolean isonPrepared;
    @Override
    public void initData() {
        interactiveLoader = new InteractiveLoader();
        handler = new WeakHandler(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                isonPrepared = true;
                total = formatSeconds(mediaPlayer.getDuration()/1000);
                mTime.setText("00:00/"+total);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                handler.removeMessages(1);
                mTime.setText("00:00/"+total);
                mPlayview.setImageResource(R.mipmap.ic_my_read_play);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                handler.removeMessages(1);
                mTime.setText("00:00/"+total);
                mediaPlayer.stop();
                mPlayview.setImageResource(R.mipmap.ic_my_read_play);
                Toast.makeText(MyReadAct.this,"播放错误",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        searchBookDetail(item.readId, UserUtils.getIns().getToken());

    }


    private void searchBookDetail(String readId,String token){
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("readId",readId);
        formBody.add("token",token);
        String url = Config.getActionUrl(Config.GET_BOOK_DETAIL);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                try {
                    JSONObject object = new JSONObject(data);
                    entity = pareJsonToBean(object);
                    mediaPlayer.setDataSource(entity.getFileUrl());
                    mediaPlayer.prepareAsync();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private BookEntity pareJsonToBean(JSONObject object){
        try {
            if (object.getInt("code") != 0){
                return null;
            }
            JSONObject obj = object.getJSONObject("data");
            BookEntity entity = new BookEntity(obj.getString("examineStatus"),obj.getString("examineUid"),obj.getString("examineUser"),
                    obj.getInt("publishDate"),obj.getString("readAuthor"),obj.getString("readCover"),obj.getString("readDetail"),
                    obj.getString("readFrequency"),obj.getString("readId"),obj.getString("readNumber"),obj.getString("readProfile"),
                    obj.getString("readTitle"),object.optString("rejectCause"),obj.getInt("updateTime")
                    ,obj.getInt("readType"),obj.optString("fileUrl"));
            return entity;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


    public static String formatSeconds(long seconds){
        String standardTime;
        if (seconds <= 0){
            standardTime = "00:00";
        } else if (seconds < 60) {
            standardTime = String.format(Locale.getDefault(), "00:%02d", seconds % 60);
        } else if (seconds < 3600) {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
        } else {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60);
        }
        return standardTime;
    }


    @Override
    public int getLayoutId() {
        return R.layout.act_layout_my_read;
    }

    @OnClick(R.id.act_layout_my_read_delete)
    void onDelete(){
        interactiveLoader.delAudio(item.readId).subscribe(new RequestCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                EventBus.getDefault().post(new RefreshReadBean());
                finish();
            }

            @Override
            public void onFail(Throwable e) {
                Toast.makeText(MyReadAct.this,"删除失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.act_layout_my_read_back)
    void onBack(){
        finish();
    }

    @OnClick(R.id.act_layout_my_read_play)
    void onPlay(){
        if (!isonPrepared){
            Toast.makeText(MyReadAct.this,"音频加载中,请稍后点击播放",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            if(mediaPlayer!=null){
                mTime.setText(formatSeconds(mediaPlayer.getCurrentPosition()/1000)+"/"+total);
            }
            mPlayview.setImageResource(R.mipmap.ic_my_read_play_zanting);
            handler.sendEmptyMessageDelayed(1,1000);
        }else {
            if(mediaPlayer!=null){
                mTime.setText(formatSeconds(mediaPlayer.getCurrentPosition()/1000)+"/"+total);
            }
            handler.removeMessages(1);
            mPlayview.setImageResource(R.mipmap.ic_my_read_play);
//            mTime.setText("00:00/"+total);
            mediaPlayer.pause();
        }

    }

    @Override
    public void handleMessage(Message msg) {
        handler.removeMessages(1);
        if(mediaPlayer!=null){
            mTime.setText(formatSeconds(mediaPlayer.getCurrentPosition()/1000)+"/"+total);
        }
        handler.sendEmptyMessageDelayed(1,1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        handler.removeMessages(1);
    }
}
