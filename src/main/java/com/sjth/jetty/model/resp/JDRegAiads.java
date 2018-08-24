package com.sjth.jetty.model.resp;

public class JDRegAiads {
    private String appId;
    private String ivString;
    private String businessCode;
    private String token;
    private String rsaPublicKey;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getIvString() {
        return ivString;
    }

    public void setIvString(String ivString) {
        this.ivString = ivString;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRsaPublicKey() {
        return rsaPublicKey;
    }

    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }
}
