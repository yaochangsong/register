package com.sjth.jetty.model.resp;

public class JDRegResp {
    private int code;
    private JDRegData data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JDRegData getData() {
        return data;
    }

    public void setData(JDRegData data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
