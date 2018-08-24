package com.sjth.jetty.util;

/*
 * JsonMessage.java
 * Copyright(C) 2013-2015 成都世纪天鸿网络科技有限公司
 * All rights reserved.
 * -----------------------------------------------
 * 2015年5月21日 Created
 */
public class Msg {
    public Msg() {
    }

    public Msg(int code) {
        this.code = code;
        this.message = Constants.ErrorMsg.getMsg(code);
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
