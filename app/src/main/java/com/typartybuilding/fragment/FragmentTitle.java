package com.typartybuilding.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.typartybuilding.R;
import com.typartybuilding.activity.HomeActivity;
import com.typartybuilding.activity.choiceness.SearchActivity;
import com.typartybuilding.base.BaseFragment;
import com.typartybuilding.base.MyApplication;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentTitle extends BaseFragment {

    @BindView(R.id.imageView_search)
    ImageView search;                 //搜索按钮
    @BindView(R.id.textView_search)
    TextView textSearch;              //显示搜索的内容
    @BindView(R.id.cir_imageView_head)
    public CircleImageView headImg;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        Log.i("HomeActivity", "onCreateView: ");
        //加载头像
        String imgUrl = MyApplication.pref.getString(MyApplication.prefKey12_login_headImg,"");
        Glide.with(this).load(imgUrl)
                .apply(MyApplication.requestOptions2)
                .into(headImg);

        //设置头像可点击
        ((HomeActivity)getActivity()).setFragmentTitle(headImg);

    }

    //刷新头像
    public void refreshHeadImg(){
        String imgUrl = MyApplication.pref.getString(MyApplication.prefKey12_login_headImg,"");
        Glide.with(this).load(imgUrl)
                .apply(MyApplication.requestOptions2)
                .into(headImg);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_title_ha;
    }

    @OnClick({R.id.imageView_search, R.id.textView_search})
    public void onClickTitle(View view){
        switch (view.getId()){
            case R.id.imageView_search :
                skipSearchActivity();
                break;
            case R.id.textView_search :
                skipSearchActivity();
                break;

        }
    }

    private void skipSearchActivity(){
        Intent intentAc = new Intent(getActivity(), SearchActivity.class);
        startActivity(intentAc);
    }




}
