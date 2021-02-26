package com.typartybuilding.activity.second.interactive;

import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.gyf.immersionbar.ImmersionBar;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseAct;
import com.typartybuilding.bean.LiveDetailsBean;
import com.typartybuilding.bean.LiveRoomBean;
import com.typartybuilding.utils.UserUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-29
 * @describe
 */
@Route(path = LiveRoomAct.PATH)
public class LiveRoomAct extends BaseAct {
    public static final String PATH = "/act/live_room";

    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.zhibo_title)
    TextView mTitleView;
    @BindView(R.id.zhibo_num)
    TextView mZhiboNum;
    @Autowired
    LiveDetailsBean liveDetailsBean;
    String url ;
    @Override
    public void initData() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .navigationBarColor(R.color.white)
                .navigationBarDarkIcon(true)
                .init();

        if (liveDetailsBean!=null&&liveDetailsBean.getProList()!=null&&liveDetailsBean.getProList().size()>0){
            mTitleView.setText(liveDetailsBean.getName());
            mZhiboNum.setText(liveDetailsBean.getHeatNum()+"");
            url=liveDetailsBean.getHost()+liveDetailsBean.getProList().get(0).getIndexList().get(0).getHlsIndex()
            +"?sso_token="+liveDetailsBean.getSso_token();

            setupVideoView();
        }
    }



    private void setupVideoView() {
        videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                videoView.start();
            }
        });
        videoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(Exception e) {
                Toast.makeText(LiveRoomAct.this,"播放错误",1).show();
                return false;
            }
        });
        videoView.setVideoURI(Uri.parse(url));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView!=null){
            videoView.stopPlayback();
            videoView.release();
            videoView=null;
        }
    }

    @OnClick(R.id.zhibo_back)
    void onClickBack(){
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_layout_live_room;
    }
}
