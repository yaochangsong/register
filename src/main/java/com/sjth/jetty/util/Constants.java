/*
 * java
 * Copyright(C) 2013-2016 成都世纪天鸿网络科技有限公司
 * All rights reserved.
 * -----------------------------------------------
 * 2015年5月25日 Created
 */
package com.sjth.jetty.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 错误常量
 *
 * @author Jacky
 * @version v1.0
 * @date 2015年9月11日
 */
public class Constants {

    private Constants() {
    }

    public static final int SUCCESS = 0;
    public static final int FAILED = 1;

    public static final String MSG_SUCCESS = "SUCCESS";
    public static final String MSG_FAILED = "FAILED";

    public enum ErrorMsg {

        SUCCESS_MSG(SUCCESS, MSG_SUCCESS),
        FAILED_MSG(FAILED, MSG_FAILED);

        private final int code;
        private final String message;

        ErrorMsg(int code, String message) {
            this.code = code;
            this.message = message;
        }

        private static final Map<Integer, ErrorMsg> map = new HashMap<Integer, ErrorMsg>();

        static {
            for (ErrorMsg errorMsg : values()) {
                map.put(errorMsg.code, errorMsg);
            }
        }

        public static String getMsg(int code) {
            ErrorMsg errorMsg = map.get(code);
            return errorMsg != null ? errorMsg.message : FAILED_MSG.message;
        }

    }
}
