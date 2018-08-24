package com.sjth.jetty.model.resp;

import com.sjth.jetty.pojo.Topic;

import java.util.List;

/**
 * Created by Bone on 2017/10/11.
 */
public class RegisterResp {
	private String deviceId;
	private String deviceName;
	private String deviceSecret;
	private String productKey;
	private String mqttDomain;

	private List<Topic> topic;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceSecret() {
		return deviceSecret;
	}

	public void setDeviceSecret(String deviceSecret) {
		this.deviceSecret = deviceSecret;
	}

	public String getProductKey() {
		return productKey;
	}

	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

	public List<Topic> getTopic() {
		return topic;
	}

	public void setTopic(List<Topic> topic) {
		this.topic = topic;
	}

	public String getMqttDomain() {
		return mqttDomain;
	}

	public void setMqttDomain(String mqttDomain) {
		this.mqttDomain = mqttDomain;
	}

}
