package com.typartybuilding.activity.quanminlangdu.mac;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.typartybuilding.R;
import com.typartybuilding.activity.quanminlangdu.fragment.ItemFragment;
import com.typartybuilding.activity.quanminlangdu.fragment.ItemMyFragment;
import com.typartybuilding.activity.quanminlangdu.fragment.adapter.FragmentAdapter;
import com.typartybuilding.activity.quanminlangdu.fragment.dummy.DummyContent;
import com.typartybuilding.activity.quanminlangdu.utils.Config;
import com.typartybuilding.activity.quanminlangdu.utils.Utils;
import com.typartybuilding.base.MyApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    private View mView;
    private ImageButton btnBack;
    private TabLayout tabTitle;
    private ViewPager mViewPager;
    private FragmentAdapter myFragmentAdapter;
    private ItemFragment AllItemFragment;
    private ItemMyFragment MyItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        //Utils.translucentStatusBar(this,true);

        /*
        * 此处
        * 调用 Config.setToken(String token) 设置token
        * 调用 Config.setUserId(Stirng suerId) 设置userId
        * */


        init();
    }

    private void init(){
        String token = MyApplication.pref.getString(MyApplication.prefKey8_login_token,"");
        int userId = MyApplication.pref.getInt(MyApplication.pretKey9_login_userId,-1);
        Config.setToken(token);

        this.mView = findViewById(R.id.mView);
        this.btnBack = findViewById(R.id.btnBack);
        this.tabTitle = findViewById(R.id.tab_title);
        this.mViewPager = findViewById(R.id.viewPager);

        AllItemFragment = ItemFragment.newInstance("");
        MyItemFragment = ItemMyFragment.newInstance(String.valueOf(userId));
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(AllItemFragment);
        fragments.add(MyItemFragment);
        myFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(myFragmentAdapter);
        tabTitle.setupWithViewPager(mViewPager);
        tabTitle.getTabAt(0).setText("  读单  ");
        tabTitle.getTabAt(1).setText("  我的  ");
        mViewPager.setCurrentItem(0);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookListActivity.this.finish();
            }
        });

        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        int stateHigh = 0;

        if (Utils.hasNotchAtHuawei(this)){
            stateHigh = Utils.getNotchSizeAtHuawei(BookListActivity.this)[1];
        }
        if (Utils.hasNotchAtOPPO(BookListActivity.this)){
            stateHigh = 80;
        }
        if (Utils.hasNotchAtVivo(BookListActivity.this)){
            stateHigh = Utils.dip2px(BookListActivity.this,27);
        }
        if (Utils.hasNotchAtMI(BookListActivity.this)){
            stateHigh = 89;
        }

        if (stateHigh>result){
            result = stateHigh;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,result);
        mView.setBackgroundColor(Color.parseColor("#FD3D3E"));
        mView.setLayoutParams(params);

        Utils.translucentStatusBar(BookListActivity.this,true);
    }


    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item,int type) {
        Intent intent = new Intent(BookListActivity.this,ReadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("book", item);
        intent.putExtra("data",bundle);
        startActivity(intent);
    }
}