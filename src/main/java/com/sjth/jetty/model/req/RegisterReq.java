package com.sjth.jetty.model.req;

import java.io.Serializable;

/**
 * 注册请求对象 Created by Bone on 2017/10/11.
 */
public class RegisterReq implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;// 设备ID IPCID/MAC NVRID/[XXYYMAC]
	private int type;// 设备类型 1-摄像头 2-网关设备 3-NVR
	private String sign;// 签名
	private Long timestamp;
	private Integer uid;// 随机数
	private String url;// request url
	private String keyId;// keyId

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

}
