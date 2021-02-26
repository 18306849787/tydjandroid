package com.typartybuilding.network.https;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author chengchunhuiit@163.com
 * @date 2020-08-30
 * @describe
 */
public class BaseLoader {
    /**
     *
     * @param observable
     * @param <T>
     * @return
     */
    protected  <T> Observable<T> observe(Observable<T> observable){


        return             observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .bindToLifecycle(lifecycleOwner)//绑定生命周期
                .debounce(1, TimeUnit.SECONDS);//防止1s内重复请求
    }
}
