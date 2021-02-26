package com.typartybuilding.network.https;

import android.content.Intent;
import android.widget.Toast;

import com.typartybuilding.activity.LaunchActivity;
import com.typartybuilding.activity.loginRelatedActivity.LoginActivity;
import com.typartybuilding.base.MyApplication;
import com.typartybuilding.retrofit.RetrofitUtil;
import com.typartybuilding.utils.appmanager.AppManageHelper;

import io.reactivex.functions.Function;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-08-30
 * @describe
 */
public class PreLoad<T>implements Function<BaseResponse<T>,T> {

    @Override
    public T apply(BaseResponse<T> tBaseResponse) throws StateException {
        if(!tBaseResponse.isSuccess()){
            if (tBaseResponse.code == 10){
                RetrofitUtil.tokenLose(AppManageHelper.currentActivity(), "登录失效,请重新登录");
//                Intent intent = new Intent(MyApplication.getContext(), LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//                MyApplication.getContext().startActivity(intent);
            }else {
                throw new StateException(tBaseResponse.code,tBaseResponse.message);
            }
        }
        return tBaseResponse.data;
    }
}
