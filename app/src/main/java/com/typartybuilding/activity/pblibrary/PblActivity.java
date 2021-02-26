package com.typartybuilding.activity.pblibrary;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.base.RedTitleBaseActivity;
import com.typartybuilding.fragment.fgPartyBuildingLibrary.FragmentAlbumList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PblActivity extends RedTitleBaseActivity {

    private String TAG = "PblActivity";

    @BindView(R.id.textView_title)
    TextView title;

    private String tagName;            //分类下对应的专辑标签

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pbl);
        ButterKnife.bind(this);
        //初始化 喜马拉雅sdk
        MyApplication.initXiMaLaYa();
        //获取上一个 页面传递的数据
        Intent intent = getIntent();
        tagName = intent.getStringExtra("tagName");
        title.setText(tagName);
        //加载页面
        loadFragment();

    }

    /**
     * 动态加载FragMent
     */
    private void loadFragment(){
        Log.i(TAG, "loadFragment: tagName : " + tagName);
        FragmentAlbumList fragment = new FragmentAlbumList();
        fragment.setTagName(tagName);

        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.frameLayout,fragment);
        transaction.commit();
    }
}
