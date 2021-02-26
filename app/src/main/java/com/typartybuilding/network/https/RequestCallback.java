package com.typartybuilding.network.https;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-09-05
 * @describe
 */
public abstract class RequestCallback<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onFail(e);
    }

    @Override
    public void onComplete() {

    }


    public abstract void onSuccess(T t);

    public abstract void onFail(Throwable e);
}
