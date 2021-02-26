package com.typartybuilding.fragment.loginregister;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.typartybuilding.R;
import com.typartybuilding.activity.loginRelatedActivity.FaceSwipingActivity;
import com.typartybuilding.activity.loginRelatedActivity.FaceSwipingActivity2;
import com.typartybuilding.activity.loginRelatedActivity.FaceSwipingActivity3;


public class FragmentFaceLogin extends Fragment {

    private static final int REQUEST_PERMISSION_CRAMERA = 101;

    private Button btnFaceSwiping;
    //private Button btnIntoFace;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_face_login_la, null);
        btnFaceSwiping = view.findViewById(R.id.button_face_swiping);
        //btnIntoFace = view.findViewById(R.id.button_into_face);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnFaceSwiping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 最小版本大于5.0，不判断5.0一下的版本
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            REQUEST_PERMISSION_CRAMERA);
                } else {
                    //Intent intent = new Intent(getActivity(), FaceSwipingActivity.class);
                    Intent intentAc = new Intent(getActivity(), FaceSwipingActivity2.class);
                    startActivity(intentAc);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CRAMERA) {
            int result = grantResults[0];
            if (PackageManager.PERMISSION_GRANTED == result) {
                // 权限通过
                Intent intent = new Intent(getActivity(), FaceSwipingActivity2.class);
                startActivity(intent);
            } else {
                // 权限未通过,提示
                Toast.makeText(getActivity(), "刷脸登录需要开启相机权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
