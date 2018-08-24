package com.sjth.jetty.model.req;

public class JDRegReq {
    private String username;
    private String sign;
    private JDRegArgs args;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public JDRegArgs getArgs() {
        return args;
    }

    public void setArgs(JDRegArgs args) {
        this.args = args;
    }
}
