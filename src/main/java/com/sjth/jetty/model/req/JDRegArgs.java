package com.sjth.jetty.model.req;

import java.util.Map;

public class JDRegArgs {
    private String wiredMac;
    private Map<String, Object> deviceMap;
    private String hardSearial;
    private String username;
    private Long time;

    public String getWiredMac() {
        return wiredMac;
    }

    public void setWiredMac(String wiredMac) {
        this.wiredMac = wiredMac;
    }

    public Map<String, Object> getDeviceMap() {
        return deviceMap;
    }

    public void setDeviceMap(Map<String, Object> deviceMap) {
        this.deviceMap = deviceMap;
    }

    public String getHardSearial() {
        return hardSearial;
    }

    public void setHardSearial(String hardSearial) {
        this.hardSearial = hardSearial;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
