package com.sjth.jetty.model.resp;

public class JDRegData {
    private String deviceUuid;
    private String imUsername;
    private String imPassword;
    private String rsaPub;
    private String token;
    private JDRegAiads aiads;

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public String getImUsername() {
        return imUsername;
    }

    public void setImUsername(String imUsername) {
        this.imUsername = imUsername;
    }

    public String getImPassword() {
        return imPassword;
    }

    public void setImPassword(String imPassword) {
        this.imPassword = imPassword;
    }

    public String getRsaPub() {
        return rsaPub;
    }

    public void setRsaPub(String rsaPub) {
        this.rsaPub = rsaPub;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public JDRegAiads getAiads() {
        return aiads;
    }

    public void setAiads(JDRegAiads aiads) {
        this.aiads = aiads;
    }
}
