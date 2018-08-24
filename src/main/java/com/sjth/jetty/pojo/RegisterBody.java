package com.sjth.jetty.pojo;

/**
 * Created by Bone on 2017/10/11.
 */
public class RegisterBody {
	private String deviceName;// IPC/IPCID GATEWAY/MAC NVR/NVRID
	private int type;// ipc:3 nvr:2 gateway:1

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
