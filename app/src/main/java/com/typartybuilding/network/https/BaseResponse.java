package com.typartybuilding.network.https;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-08-30
 * @describe
 */
public class BaseResponse<T> {
    public int code;
    public String message;
    public T data;
    public boolean isSuccess(){
        return code == 0;
    }
}
