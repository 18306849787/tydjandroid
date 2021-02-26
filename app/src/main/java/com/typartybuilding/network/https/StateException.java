package com.typartybuilding.network.https;

/**
 * @author chengchunhuiit@163.com
 * @date 2020-08-30
 * @describe
 */
public class StateException extends Exception {
    public StateException(int status, String msg){
        this.state = status;
        this.msg = msg;
    }
    public int state;
    public String msg;
}
