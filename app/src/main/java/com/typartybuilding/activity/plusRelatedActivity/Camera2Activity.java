package com.typartybuilding.activity.plusRelatedActivity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.typartybuilding.R;
import com.typartybuilding.base.BaseActivity;
import com.typartybuilding.fragment.camera.TakePictureFragment;
import com.typartybuilding.fragment.camera.TakeVideoFragment;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;

public class Camera2Activity extends BaseActivity {

    private static String TAG = "Camera2Activity";


    private String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private List<String> mPermissionList = new ArrayList<>();

    private List<Fragment> fragmentList = new ArrayList<>();

    public static final int REQUEST_CODE_PICTURE = 1;
    public static final int REQUEST_CODE_VIDEO = 2;

    public ArrayList<String> picList = new ArrayList<>();
    public String videoPath;

    private Handler handler = new Handler();

    public static int mCameraMode = CameraCharacteristics.LENS_FACING_FRONT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        ButterKnife.bind(this);

        initFragmentList();
        //第一次，默认加载拍照fragment
        loadFragment(0);
        //请求权限
        requestPermission(1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
        changeNavigationBar();

    }

    private void initFragmentList(){
        TakePictureFragment fragment1 = new TakePictureFragment();
        TakeVideoFragment fragment2 = new TakeVideoFragment();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);

    }

    /**
     * 动态加载FragMent
     */
    public void loadFragment(int i){
        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.frameLayout,fragmentList.get(i));
        transaction.commit();
    }

    //处理选择图片 和 视频 后的调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){

            switch (requestCode){
                case REQUEST_CODE_PICTURE:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList != null) {
                        if (picList.size() > 0) {
                            picList.clear();
                        }
                        for (int i = 0; i < selectList.size(); i++) {
                            picList.add(selectList.get(i).getPath());
                            Log.i(TAG, "onActivityResult: pic path : " + selectList.get(i).getPath());
                        }
                        Intent intent = new Intent(this, UploadPictureActivity.class);
                        intent.putStringArrayListExtra("picList", picList);
                        startActivity(intent);
                    }
                    break;

                case REQUEST_CODE_VIDEO:
                    List<LocalMedia> selectList2 = PictureSelector.obtainMultipleResult(data);
                    if (selectList2 != null){
                        videoPath = selectList2.get(0).getPath();
                    }
                    //切换 到 拍视频fragment
                    //loadFragment(1);
                    //延时 500毫秒
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TakePictureFragment fragment1 = (TakePictureFragment)fragmentList.get(0);
                            TakeVideoFragment fragment2 = (TakeVideoFragment) fragmentList.get(1);

                            Log.i(TAG, "onActivityResult: ");
                            //显示 配乐的布局
                            fragment2.completeLayout2();
                            //传递视频路径
                            fragment2.getVideoPath(videoPath);

                        }
                    },500);

            }
        }
    }

    private void requestPermission(int requestCode){
        Log.i(TAG, "requestPermission: ");

        if (mPermissionList.size() >0){
            mPermissionList.clear();
        }

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
                Log.i(TAG, "requestPermission: permissions : " + permissions[i]);
            }
        }

        Log.i(TAG, "requestPermission: mPermissionList.size() : " + mPermissionList.size());
        if (mPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 1:
                Log.i(TAG, "onRequestPermissionsResult:  grantResults[0] " +  grantResults[0]);
                Log.i(TAG, "onRequestPermissionsResult:  grantResults[1]" + grantResults[1]);
                boolean hasPermissionDismiss = false;//有权限没有通过
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == -1) {
                        hasPermissionDismiss = true;
                    }
                }
                if (hasPermissionDismiss){
                    Toast.makeText(this,"未授权无法进行拍照",Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }


}
