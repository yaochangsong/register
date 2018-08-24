package com.sjth.jetty.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sjth.jetty.dao.AccessDao;
import com.sjth.jetty.pojo.Access;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.sjth.jetty.model.req.RegisterReq;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class SignUtil {

	private final static String SIGN_TEMPLATE = "%s-%s-%s-%s-%s";
	private static volatile Long time = System.currentTimeMillis() / 1000;

	@Resource
	private AccessDao accessDao;

	private Map<String, String> keyMap;

	@PostConstruct
	public void initAuthMap(){
		if (null == keyMap){
			keyMap = new HashMap<>();
		}
		List<Access> accesses = accessDao.getAccess();
		accesses.forEach(access -> keyMap.put(access.getAccessKey(), access.getAccessSecret()));
	}

	// validate sign
	public boolean validateSign(String clientKey, RegisterReq req) {
		return !StringUtils.isEmpty(clientKey) && clientKey.trim().equals(generateSign(req));
	}

	private String generateSign(RegisterReq req) {
		if (req == null || StringUtils.isEmpty(req.getUrl()) || StringUtils.isEmpty(req.getUid())
				|| StringUtils.isEmpty(req.getId()) || req.getTimestamp() == null
				|| StringUtils.isEmpty(req.getKeyId())) {
			return null;
		} else {
			return generateMd5Code(String.format(SIGN_TEMPLATE, req.getUrl(), req.getId(), req.getTimestamp(),
					req.getUid(), keyMap.get(req.getKeyId())));
		}

	}

	// validate key
	public boolean validateKey(String keyId) {
		boolean ret;
		// 前期发出去的设备没有keyId 也让它验证通过
		if (StringUtils.isEmpty(keyId)){
			ret = true;
		} else {
			Long currentTime = System.currentTimeMillis() / 1000;
			if (keyMap.containsKey(keyId)){
				ret = true;
			}else if(currentTime - time >= 300) {
				initAuthMap();
				time = currentTime;
				ret = validateKey(keyId);
			} else {
				ret = false;
			}
		}
		return ret;
	}

	// generate Md5 code
	private String generateMd5Code(String code) {
		return DigestUtils.md5Hex(code);
	}

}
