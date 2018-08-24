package com.sjth.jetty.util;

/**
 * 数据响应消息
 * Created by Jacky on 2017/4/19.
 */
public class DataMsg<T> extends Msg {
    public DataMsg() {
    }

    private T data;

    public DataMsg(int code) {
        super(code);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
